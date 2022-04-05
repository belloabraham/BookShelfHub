package com.bookshelfhub.bookshelfhub

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWelcomeBinding
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.MaterialAlertDialogBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.helpers.authentication.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IGoogleAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.GoogleAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.PhoneAuth
import com.bookshelfhub.bookshelfhub.workers.DownloadBookmarks
import com.bookshelfhub.bookshelfhub.helpers.google.GooglePlayServices
import com.bookshelfhub.bookshelfhub.domain.usecases.Database
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.DbFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.ICloudDb
import com.bookshelfhub.bookshelfhub.domain.viewmodels.GoogleAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var signInErrorMsg: String
    private lateinit var layout: ActivityWelcomeBinding
    private lateinit var phoneAuth: IPhoneAuth
    private lateinit var googleAuth: IGoogleAuth
    private val phoneAuthViewModel: PhoneAuthViewModel by viewModels()
    private val googleAuthViewModel: GoogleAuthViewModel by viewModels()
    private val userAuthViewModel: UserAuthViewModel by viewModels()
    private val welcomeActivityViewModel: WelcomeActivityViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var phoneAuthCallbacks:  PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null

    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var json: Json

    @Inject
    lateinit var database: Database

    //***Get Nullable referral userID or PubIdAndISBN
    private var referrer: String? = null

    @Inject
    lateinit var connectionUtil: ConnectionUtil

    @Inject
    lateinit var worker: Worker

    @Inject
    lateinit var userAuth: IUserAuth

    private var resendToken: PhoneAuthProvider.ForceResendingToken?=null
    private var storedVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneAuthCallbacks = getAuthCallBack(  R.string.otp_error_msg,
            R.string.too_many_request_error,
            R.string.phone_sign_in_error)

        layout = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(layout.root)

        referrer = welcomeActivityViewModel.getReferrer()

        //Set to userAuthViewModel if referral Id is not for a publisherReferrer but for an individual user
        referrer?.let { referrerId ->
            if (!referrerId.contains(Referrer.SEPARATOR.KEY)) {
                //An individual refer this user
                userAuthViewModel.setUserReferrerId(referrerId)
            }
        }

        //Pass Nullable referral userID or PubIdAndISBN and set to Main Activity to be opened in store immediately if PubISBN
        val intent = Intent(this, MainActivity::class.java)
        intent.flags =  Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(Referrer.ID.KEY, referrer)

        //Check if user Device have Google Play services installed as it is required for proper functioning of application
        GooglePlayServices(this).checkForGooglePlayServices()

        //Initialize Firebase Phone Verification and Auth
        phoneAuth = PhoneAuth(
            this,
        )

        //Initialize Firebase Google Auth
        googleAuth = GoogleAuth(this, R.string.gcp_web_client)

        //Start an activity for result callback
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    //Sign in with Google
                    lifecycleScope.launch(IO) {
                        try {
                            val googleSignIn =
                                GoogleSignIn.getSignedInAccountFromIntent(data).await()
                            try {
                                //Google Sign In was successful, authenticate with Firebase
                                val authResult =
                                    googleAuth.authWithGoogle(googleSignIn.idToken!!).await()
                                withContext(Main) {
                                    googleAuthViewModel.setIsNewUser(authResult.additionalUserInfo!!.isNewUser)
                                    googleAuthViewModel.setIsAuthenticatedSuccessful(true)
                                }
                            } catch (e: Exception) {
                                val authErrorMsg =
                                    getString(R.string.authentication) + ": " + signInErrorMsg
                                withContext(Main) {
                                    googleAuthViewModel.setAuthenticationError(authErrorMsg)
                                }
                            } finally {
                                withContext(Main) {
                                    googleAuthViewModel.setIsAuthenticationComplete(true)
                                }
                            }

                        } catch (e: Exception) {
                            withContext(Main) {
                                //Google Sign In failed, update UI appropriately
                                hideAnimation()
                                googleAuthViewModel.setSignInError(signInErrorMsg)
                            }
                        }

                    }

                } else {
                    hideAnimation()
                }
            }

        phoneAuthViewModel.getIsNewUser().observe(this, Observer { isNewUser ->
            hideAnimation()
            restoreBookmarks(isNewUser)
        })

        phoneAuthViewModel.getIsCodeSent().observe(this, Observer { isCodeSent ->
            hideAnimation()
        })

        phoneAuthViewModel.getSignInStarted().observe(this, Observer { signInStarted ->
            if (signInStarted) {
                showAnimation(R.raw.loading)
            }
        })

        phoneAuthViewModel.getSignInCompleted().observe(this, Observer {
            //If Phone sign in attempt failed hide animation
            if (!userAuth.getIsUserAuthenticated()) {
                hideAnimation()
            }
        })

        googleAuthViewModel.getIsNewUser().observe(this, Observer { isNewUser ->
            //once sign in operation successful
            if (isNewUser) {
                hideAnimation()
            }
            restoreBookmarks(isNewUser)
        })


        googleAuthViewModel.getIsAuthenticationComplete().observe(this, Observer {
            //if Google authentication failed hide animation
            if (!userAuth.getIsUserAuthenticated()) {
                hideAnimation()
            }
        })

        userAuthViewModel.getIsAddingUser().observe(this, Observer { isAddingUser ->
            //user data is being added to the cloud
            if (isAddingUser) {
                showAnimation()
            } else {
                //User data is added completely or is not beign added bcos user data alredy exist
                hideAnimation()
                if (googleAuthViewModel.getIsNewUser().value == true || phoneAuthViewModel.getIsNewUser().value == true) {
                    showConfettiAnim()

                    //***Delay for 1.7sec for confetti animation to complete showing ***
                    lifecycleScope.launch(IO) {
                        delay(1700)
                        withContext(Main) {
                            showSignUpCompletedDialog(intent)
                        }
                    }
                } else {
                    finish()
                    startMainActivity(intent)
                }
            }
        })

        userAuthViewModel.getIsExistingUser().observe(this, Observer { isExistingUser ->
            //Confirmed that user data exist on the cloud
            if (!isExistingUser) {
                hideAnimation()
            }
        })

        googleAuthViewModel.getAuthenticationError().observe(this, Observer { authErrorMsg ->
            Snackbar.make(layout.rootView, authErrorMsg, Snackbar.LENGTH_LONG)
                .setAction(R.string.try_again) {
                    val errorMsg =
                        authErrorMsg.replace(getString(R.string.authentication) + ": ", "")
                    signInOrSignUpWithGoogle(errorMsg)
                }
                .show()
        })

        googleAuthViewModel.getSignInError().observe(this, Observer { signInErrorMsg ->
            Snackbar.make(layout.rootView, signInErrorMsg, Snackbar.LENGTH_LONG)
                .setAction(R.string.try_again) {
                    signInOrSignUpWithGoogle(signInErrorMsg)
                }
                .show()
        })

        phoneAuthViewModel.getIsSignedInSuccessfully().observe(this, Observer { isSignedInSuccessfully ->
            if (isSignedInSuccessfully){
                val isNewUser = phoneAuthViewModel.getIsNewUser().value!!
                if (!isNewUser){
                    cloudDb.getLiveDataAsync(this, DbFields.USERS.KEY, userAuth.getUserId(), retry = true){ docSnapShot, _ ->

                        if(docSnapShot!=null && docSnapShot.exists()){
                            try {
                                val jsonString = docSnapShot.get(DbFields.BOOK_INTEREST.KEY)
                                val bookInterest = json.fromAny(jsonString!!, BookInterest::class.java)

                                bookInterest.uploaded=true
                                lifecycleScope.launch(IO){
                                    database.addBookInterest(bookInterest)
                                }
                            }catch (e:Exception){
                            }

                            try {
                                val userJsonString = docSnapShot.get(DbFields.USER.KEY)
                                val user = json.fromAny(userJsonString!!, User::class.java)
                                if (user.device != DeviceUtil.getDeviceBrandAndModel() || user.deviceOs!= DeviceUtil.getDeviceOSVersionInfo(
                                        Build.VERSION.SDK_INT)){
                                    user.device =   DeviceUtil.getDeviceBrandAndModel()
                                    user.deviceOs= DeviceUtil.getDeviceOSVersionInfo(Build.VERSION.SDK_INT)
                                }else {
                                    user.uploaded = true
                                }
                                userAuthViewModel.setIsAddingUser(false, user)
                            }catch (ex:Exception){
                                userAuthViewModel.setIsExistingUser(false)
                            }

                        }else{
                            userAuthViewModel.setIsExistingUser(false)
                        }
                    }
                }
            }
        })

        googleAuthViewModel.getIsAuthenticatedSuccessful().observe(this, Observer { isAuthSuccessful ->

            if (isAuthSuccessful){
                val isNewUser = googleAuthViewModel.getIsNewUser().value!!
                if (!isNewUser){
                    cloudDb.getLiveDataAsync(this, DbFields.USERS.KEY, userAuth.getUserId(), retry = true){ docSnapShot, _ ->

                        if(docSnapShot!=null && docSnapShot.exists()){
                            try {
                                val jsonObj = docSnapShot.get(DbFields.BOOK_INTEREST.KEY)
                                val bookInterest = json.fromAny(jsonObj!!, BookInterest::class.java)
                                bookInterest.uploaded=true
                                lifecycleScope.launch(IO){
                                    database.addBookInterest(bookInterest)
                                }
                            }catch (e:Exception){

                            }

                            try {
                                val userJsonString = docSnapShot.get(DbFields.USER.KEY)
                                val user = json.fromAny(userJsonString!!, User::class.java)
                                if (user.device != DeviceUtil.getDeviceBrandAndModel() || user.deviceOs!= DeviceUtil.getDeviceOSVersionInfo(
                                        Build.VERSION.SDK_INT)){
                                    user.device = DeviceUtil.getDeviceBrandAndModel()
                                    user.deviceOs= DeviceUtil.getDeviceOSVersionInfo(Build.VERSION.SDK_INT)
                                }else {
                                    user.uploaded = true
                                }
                                userAuthViewModel.setIsAddingUser(false, user)
                            }catch (ex:Exception){
                                userAuthViewModel.setIsExistingUser(false)
                            }
                        }else{
                            userAuthViewModel.setIsExistingUser(false)
                        }
                    }
                }
            }
        })

    }


    private fun showAnimation(animation: Int = R.raw.loading) {
        layout.lottieAnimView.setAnimation(animation)
        layout.lottieContainerView.visibility = View.VISIBLE
        layout.lottieAnimView.playAnimation()
    }

    private fun showConfettiAnim() {
        layout.confettiAnimView.setAnimation(R.raw.confetti)
        layout.confettiContainer.visibility = View.VISIBLE
        layout.confettiAnimView.playAnimation()
    }

    private fun hideAnimation() {
        layout.lottieContainerView.visibility = View.GONE
        layout.lottieAnimView.cancelAnimation()
    }


    fun startPhoneNumberVerification(phoneNumber: String) {
        if (!layout.lottieAnimView.isAnimating) {
            if (connectionUtil.isConnected()) {
                showAnimation()
                phoneAuth.startPhoneNumberVerification(phoneNumber, phoneAuthCallbacks!!)
            } else {
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun resendVerificationCode(number: String, animation: Int) {
        if (!layout.lottieAnimView.isAnimating) {
            if (connectionUtil.isConnected()) {
                showAnimation(animation)
                phoneAuth.resendVerificationCode(number, resendToken!!, phoneAuthCallbacks!!)
            } else {
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun verifyPhoneNumberWithCode(code: String) {
        phoneAuthViewModel.setSignInStarted(true)
        lifecycleScope.launch(IO){
            try {
                val authResult = phoneAuth.verifyPhoneNumberWithCode(code, storedVerificationId!!).await()
                withContext(Main){
                    phoneAuthViewModel.setIsNewUser(authResult.additionalUserInfo!!.isNewUser)
                    phoneAuthViewModel.setIsSignedInSuccessfully(true)
                }

            }catch (e:Exception){
                if (e is FirebaseAuthInvalidCredentialsException) {
                    withContext(Main){
                        phoneAuthViewModel.setSignedInFailedError(getString(R.string.otp_error_msg))
                    }
                }
            }finally {
                withContext(Main) {
                    phoneAuthViewModel.setSignInCompleted(true)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!layout.lottieAnimView.isAnimating) {
            //Workaround to android 10 leak when user press back button on main activity
            //This code prevents the leak
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                if (onBackPressedDispatcher.hasEnabledCallbacks()) {
                    super.onBackPressed()
                } else {
                    finishAfterTransition()
                }
            } else {
                super.onBackPressed()
            }
        }
    }

    fun signInOrSignUpWithGoogle(signInErrorMsg: String) {
        this.signInErrorMsg = signInErrorMsg
        if (!layout.lottieAnimView.isAnimating) {
            if (connectionUtil.isConnected()) {
                showAnimation(R.raw.google_sign_in)
                googleAuth.signInOrSignUpWithGoogle(resultLauncher)
            } else {
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun showSignUpCompletedDialog(intent: Intent) {
        MaterialAlertDialogBuilder(this)
            .setPositiveBtnId(R.id.getStartedBtn) {
                finish()
                startMainActivity(intent)
            }
            .build(this)
            .showDialog(R.layout.sign_up_completed, R.dimen.signup_completed_dialog_max_width)

    }

    private fun startMainActivity(intent: Intent) {
        hideAnimation()
        startActivity(intent)
    }

    private fun restoreBookmarks(isNewUser: Boolean) {
        //restore bookmark if user is existing user
        if (!isNewUser) {
            val downLoadBookmarksWorker =
                OneTimeWorkRequestBuilder<DownloadBookmarks>()
                    .setConstraints(Constraint.getConnected())
                    .build()
            worker.enqueue(downLoadBookmarksWorker)
        }
    }

    private fun getAuthCallBack(wrongOTPErrorMsg:Int, tooManyReqErrorMsg:Int, otherAuthErrorMsg:Int): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                phoneAuthViewModel.setOTPCode("000000")
                phoneAuth.signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        phoneAuthViewModel.setSignedInFailedError(getString(wrongOTPErrorMsg))
                    }
                    is FirebaseTooManyRequestsException -> {
                        phoneAuthViewModel.setSignedInFailedError(getString(tooManyReqErrorMsg))
                    }
                    else -> {
                        phoneAuthViewModel.setSignedInFailedError(getString(otherAuthErrorMsg))
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                phoneAuthViewModel.setIsCodeSent(true)
            }
        }
    }



}