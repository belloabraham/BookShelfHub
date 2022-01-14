package com.bookshelfhub.bookshelfhub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWelcomeBinding
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.MaterialAlertDialogBuilder
import com.bookshelfhub.bookshelfhub.services.authentication.*
import com.bookshelfhub.bookshelfhub.services.authentication.IGoogleAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.GoogleAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.PhoneAuth
import com.bookshelfhub.bookshelfhub.workers.DownloadBookmarks
import com.bookshelfhub.bookshelfhub.helpers.google.GooglePlayServices
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var signInErrorMsg: String
    private lateinit var layout: ActivityWelcomeBinding
    private lateinit var phoneAuth:IPhoneAuth
    private lateinit var googleAuth: IGoogleAuth
    private val phoneAuthViewModel: PhoneAuthViewModel by viewModels()
    private val googleAuthViewModel: GoogleAuthViewModel by viewModels()
    private val userAuthViewModel: UserAuthViewModel by viewModels()
    private val welcomeActivityViewModel: WelcomeActivityViewModel by viewModels()
    private lateinit var resultLauncher:ActivityResultLauncher<Intent>

    //***Get Nullable referral userID or PubIdAndISBN
    private var referrer:String?=null

    @Inject
    lateinit var connectionUtil: ConnectionUtil
    @Inject
    lateinit var worker: Worker
    @Inject
    lateinit var userAuth: IUserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(layout.root)

        referrer=welcomeActivityViewModel.getReferrer()

        //TODO Set to userAuthViewModel if referral Id is not for a publisherReferrer but for an individual user
        referrer?.let { referrerId->
            if (!referrerId.contains(Referrer.SEPARATOR.KEY)){
                //Todo An individual refer this user
                userAuthViewModel.setUserReferrerId(referrerId)
            }
        }

        //TODO Pass Nullable referral userID or PubIdAndISBN and set to Main Activity to be opened in store immediately if PubISBN
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Referrer.ID.KEY, referrer)

        //TODO Check if user Device have Google Play services installed as it is required for proper functioning of application
        GooglePlayServices(this).checkForGooglePlayServices()

        //TODO Initialize Firebase Phone Verification and Auth
        phoneAuth= PhoneAuth(this, phoneAuthViewModel, R.string.otp_error_msg, R.string.too_many_request_error, R.string.phone_sign_in_error)

        //TODO Initialize Firebase Google Auth
        googleAuth = GoogleAuth(this, googleAuthViewModel, R.string.gcp_web_client)

        //TODO Start an activity for result callback
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    //TODO Sign in with Google
                    GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(this){
                        //TODO Google Sign In was successful, authenticate with Firebase
                        googleAuth.authWithGoogle(it.idToken!!, getString(R.string.authentication)+": "+signInErrorMsg)
                    }
                    .addOnFailureListener {
                        //TODO Google Sign In failed, update UI appropriately
                        hideAnimation()
                        googleAuthViewModel.setSignInError(signInErrorMsg)
                    }
                }else{
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
            if (signInStarted){
                showAnimation(R.raw.loading)
            }
        })

        phoneAuthViewModel.getSignInCompleted().observe(this, Observer {
            //TODO If Phone sign in attempt failed hide animation
            if (!userAuth.getIsUserAuthenticated()){
                hideAnimation()
            }
        })

        googleAuthViewModel.getIsNewUser().observe(this, Observer { isNewUser ->
            //TODO once sign in operation successful
            if(isNewUser){
                hideAnimation()
            }
            restoreBookmarks(isNewUser)
        })


        googleAuthViewModel.getIsAuthenticationComplete().observe(this, Observer {
            //TODO if Google authentication failed hide animation
            if (!userAuth.getIsUserAuthenticated()){
                hideAnimation()
            }
        })

        userAuthViewModel.getIsAddingUser().observe(this, Observer { isAddingUser ->
            //TODO user data is being added to the cloud
            if (isAddingUser){
                showAnimation()
            }else{
                //TODO User data is added completely or is not beign added bcos user data alredy exist
                hideAnimation()
                if (googleAuthViewModel.getIsNewUser().value==true||phoneAuthViewModel.getIsNewUser().value == true){
                   showConfettiAnim()

                    //TODO ***Delay for 1.7sec for confetti animation to complete showing ***
                    lifecycleScope.launch(IO) {
                        delay(1700)
                        withContext(Main) {
                            showSignUpCompletedDialog(intent)
                        }
                    }
                }else{
                    finish()
                    startMainActivity(intent)
                }
            }
        })

        userAuthViewModel.getIsExistingUser().observe(this, Observer { isExistingUser ->
            //TODO Confirmed that user data exist on the cloud
            if (!isExistingUser){
                hideAnimation()
            }
        })

        googleAuthViewModel.getAuthenticationError().observe(this, Observer { authErrorMsg ->
            Snackbar.make(layout.rootView, authErrorMsg, Snackbar.LENGTH_LONG)
                .setAction(R.string.try_again) {
                    val errorMsg = authErrorMsg.replace(getString(R.string.authentication)+": ", "")
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
    }


     private fun showAnimation(animation: Int = R.raw.loading){
        layout.lottieAnimView.setAnimation(animation)
        layout.lottieContainerView.visibility = View.VISIBLE
        layout.lottieAnimView.playAnimation()
    }

     private fun showConfettiAnim(){
        layout.confettiAnimView.setAnimation(R.raw.confetti)
        layout.confettiContainer.visibility = View.VISIBLE
        layout.confettiAnimView.playAnimation()
    }

    private fun hideAnimation(){
        layout.lottieContainerView.visibility = View.GONE
        layout.lottieAnimView.cancelAnimation()
    }


    fun startPhoneNumberVerification(phoneNumber: String){
        if (!layout.lottieAnimView.isAnimating){
            if (connectionUtil.isConnected()){
                showAnimation()
                phoneAuth.startPhoneNumberVerification(phoneNumber)
            }else{
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun resendVerificationCode(number:String, animation: Int){
        if (!layout.lottieAnimView.isAnimating ){
            if (connectionUtil.isConnected()){
                showAnimation(animation)
                phoneAuth.resendVerificationCode(number)
            }else{
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun verifyPhoneNumberWithCode(code:String){
        phoneAuth.verifyPhoneNumberWithCode(code, R.string.otp_error_msg)
    }

    override fun onBackPressed() {
        if (!layout.lottieAnimView.isAnimating){
            super.onBackPressed()
        }
    }

    fun signInOrSignUpWithGoogle(signInErrorMsg:String){
        this.signInErrorMsg= signInErrorMsg
        if (!layout.lottieAnimView.isAnimating) {
            if (connectionUtil.isConnected()){
                showAnimation(R.raw.google_sign_in)
                googleAuth.signInOrSignUpWithGoogle(resultLauncher)
            }else{
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showSignUpCompletedDialog(intent:Intent){
        MaterialAlertDialogBuilder(this)
            .setPositiveBtnId(R.id.getStartedBtn){
                finish()
                startMainActivity(intent)
            }
            .build(this)
            .showDialog(R.layout.sign_up_completed, R.dimen.signup_completed_dialog_max_width )

    }

    private fun startMainActivity(intent: Intent){
        hideAnimation()
        startActivity(intent)
    }

    private fun restoreBookmarks(isNewUser:Boolean){
        //TODO restore bookmark if user is existing user
        if(!isNewUser){
            val downLoadBookmarksWorker =
                OneTimeWorkRequestBuilder<DownloadBookmarks>()
                    .setConstraints(Constraint.getConnected())
                    .build()
            worker.enqueue(downLoadBookmarksWorker)
        }
    }


}