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
import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWelcomeBinding
import com.bookshelfhub.bookshelfhub.helpers.MaterialAlertDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.authentication.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IGoogleAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.GoogleAuth
import com.bookshelfhub.bookshelfhub.helpers.authentication.firebase.PhoneAuth
import com.bookshelfhub.bookshelfhub.helpers.google.GooglePlayServices
import com.bookshelfhub.bookshelfhub.domain.viewmodels.GoogleAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    lateinit var connectionUtil: ConnectionUtil

    @Inject
    lateinit var userAuth: IUserAuth

    private var resendToken: PhoneAuthProvider.ForceResendingToken?=null
    private var storedVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneAuthCallbacks = getPhoneAuthCallBack(  R.string.otp_error_msg,
            R.string.too_many_request_error,
            R.string.phone_sign_in_error)

        layout = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(layout.root)

        userAuthViewModel.setUserReferrerId(welcomeActivityViewModel.getUserReferralId())

        //Check if user Device have Google Play services installed as it is required for proper functioning of application
        GooglePlayServices(this).checkForGooglePlayServices()

        phoneAuth = PhoneAuth(
            this,
        )

        googleAuth = GoogleAuth(this, R.string.gcp_web_client)

        resultLauncher = getGoogleSignInActivityResult()


        phoneAuthViewModel.getIsCodeSent().observe(this, Observer { _ ->
            hideAnimation()
        })

        phoneAuthViewModel.getSignInStarted().observe(this, Observer { signInStarted ->
            if (signInStarted) {
                showAnimation()
            }
        })

        phoneAuthViewModel.getSignInCompleted().observe(this, Observer {
            val signInAttemptFailed = !userAuth.getIsUserAuthenticated()
            if (signInAttemptFailed) {
                hideAnimation()
            }
        })

        googleAuthViewModel.getIsAuthenticationComplete().observe(this, Observer {
            val authAttemptFailed = !userAuth.getIsUserAuthenticated()
            if (authAttemptFailed) {
                hideAnimation()
            }
        })

        userAuthViewModel.getIsAddingUser().observe(this, Observer { userDataUploading ->
            if (userDataUploading) {
                showAnimation()
            } else {
                hideAnimation()
                val intent = Intent(this, MainActivity::class.java)

                val isNewUser = googleAuthViewModel.getIsNewUser() == true || phoneAuthViewModel.getIsNewUser() == true
                if (isNewUser) {
                    showConfettiAnim()
                    //***Delay for 1.7sec for confetti animation to complete showing ***
                    lifecycleScope.launch {
                        delay(1700)
                        showSignUpCompletedDialog(intent)
                    }
                } else {
                    finish()
                    startMainActivity(intent)
                }
            }
        })

        userAuthViewModel.getIsExistingUser().observe(this, Observer { isExistingUser ->
           val isNotAnExistingUser = !isExistingUser
            if (isNotAnExistingUser) {
                hideAnimation()
            }
        })


        lifecycleScope.launch {
            googleAuthViewModel.getAuthenticationError().collect{authErrorMsg ->
                Snackbar.make(layout.rootView, authErrorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.try_again) {
                        val errorMsg =
                            authErrorMsg.replace(getString(R.string.authentication) + ": ", "")
                        signInOrSignUpWithGoogle(errorMsg)
                    }
                    .show()
            }
        }

        lifecycleScope.launch {
            googleAuthViewModel.getSignInError().collect{ signInErrorMsg ->
                Snackbar.make(layout.rootView, signInErrorMsg, Snackbar.LENGTH_LONG)
                    .setAction(R.string.try_again) {
                        signInOrSignUpWithGoogle(signInErrorMsg)
                    }
                    .show()
            }
        }


        phoneAuthViewModel.getIsSignedInSuccessfully().observe(this, Observer { isSignedInSuccessfully ->
            if (isSignedInSuccessfully){
                val isNewUser = phoneAuthViewModel.getIsNewUser()!!
                afterAuthCompletes(!isNewUser)
                if(isNewUser){
                    hideAnimation()
                }
            }
        })

        googleAuthViewModel.getIsAuthenticatedSuccessful().observe(this, Observer { isAuthSuccessful ->
            if (isAuthSuccessful){
                val isNewUser = googleAuthViewModel.getIsNewUser()!!
                val isExistingUser = !isNewUser
                if(isNewUser){
                    hideAnimation()
                }
                afterAuthCompletes(isExistingUser)
            }
        })

    }

    private fun getGoogleSignInActivityResult(): ActivityResultLauncher<Intent> {
       return   registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                lifecycleScope.launch{
                    try {
                        val googleSignIn = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                        try {
                            val authResult =
                                googleAuth.authWithGoogle(googleSignIn.idToken!!).await()
                            googleAuthViewModel.setIsNewUser(authResult.additionalUserInfo!!.isNewUser)
                            googleAuthViewModel.setIsAuthenticatedSuccessful(true)
                        } catch (e: Exception) {
                            val authErrorMsg =
                                getString(R.string.authentication) + ": " + signInErrorMsg
                            googleAuthViewModel.setAuthenticationError(authErrorMsg)
                        } finally {
                            googleAuthViewModel.setIsAuthenticationComplete(true)
                        }

                    } catch (e: Exception) {
                        hideAnimation()
                        googleAuthViewModel.setSignInError(signInErrorMsg)
                    }
                }

            } else {
                hideAnimation()
            }
        }
    }

    private fun afterAuthCompletes(isExistingUser:Boolean){
        if (isExistingUser){
            lifecycleScope.launch {
                    welcomeActivityViewModel.getRemoteUser()
                        .asFlow()
                        .collect{ remoteUser->
                            val userDataExist = remoteUser !=null
                            if(userDataExist){
                                welcomeActivityViewModel.addRemoteBookInterest(remoteUser!!.bookInterest)
                                welcomeActivityViewModel.updateUserDeviceType(remoteUser.user)
                                //Notify UI that user data uploading to local Db is complete
                                userAuthViewModel.setIsAddingUser(false)
                            }else{
                                userAuthViewModel.setIsExistingUser(false)
                            }
                        }
            }
        }
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
        lifecycleScope.launch{
            try {
                val authResult = phoneAuth.verifyPhoneNumberWithCode(code, storedVerificationId!!).await()
                    phoneAuthViewModel.setIsNewUser(authResult.additionalUserInfo!!.isNewUser)
                    phoneAuthViewModel.setIsSignedInSuccessfully(true)
            }catch (e:Exception){
                if (e is FirebaseAuthInvalidCredentialsException) {
                        phoneAuthViewModel.setSignedInFailedError(getString(R.string.otp_error_msg))
                }
            }finally {
                    phoneAuthViewModel.setSignInCompleted(true)
            }
        }
    }

    override fun onBackPressed() {
        if (!layout.lottieAnimView.isAnimating) {
            val deviceOSIsAndroid10 = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
            if (deviceOSIsAndroid10) {
                exitAppWithoutMemoryLeakOnAndroid10()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun exitAppWithoutMemoryLeakOnAndroid10(){
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            super.onBackPressed()
        } else {
            finishAfterTransition()
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
        intent.putExtra(Referrer.ID, welcomeActivityViewModel.getACollaboratorOrUserReferralId())
        startActivity(intent)
    }


    private fun getPhoneAuthCallBack(wrongOTPErrorMsg:Int, tooManyReqErrorMsg:Int, otherAuthErrorMsg:Int): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
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