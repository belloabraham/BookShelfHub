package com.bookshelfhub.bookshelfhub

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWelcomeBinding
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuth
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuthViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuth
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var layout: ActivityWelcomeBinding
    private lateinit var phoneAuth:PhoneAuth
    private lateinit var googleAuth:GoogleAuth
    private val phoneAuthViewModel: PhoneAuthViewModel by viewModels()
    private val googleAuthViewModel: GoogleAuthViewModel by viewModels()
    private lateinit var resultLauncher:ActivityResultLauncher<Intent>

    @Inject
     lateinit var connectionUtil: ConnectionUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(layout.root)

        phoneAuth= PhoneAuth(this, phoneAuthViewModel)
        googleAuth = GoogleAuth(this, googleAuthViewModel)

        phoneAuthViewModel.getIsCodeSent().observe(this, Observer { isCodeSent ->
                hideAnimation()
        })

        phoneAuthViewModel.getSignInStarted().observe(this, Observer { signInStarted ->
            if (signInStarted){
                showAnimation(R.raw.loading)
            }
        })

        phoneAuthViewModel.getSignInCompleted().observe(this, Observer { signInCompleted ->
            if (signInCompleted){
                hideAnimation()
            }
        })

        googleAuthViewModel.getIsAuthenticationdComplete().observe(this, Observer { isAuthComplete ->
            if (isAuthComplete){
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


    private fun showAnimation(animation: Int){
        layout.lottieAnimView.setAnimation(animation)
        layout.lottieContainerView.visibility = View.VISIBLE
        layout.lottieAnimView.playAnimation()
    }

    private fun hideAnimation(){
        layout.lottieContainerView.visibility = View.GONE
        layout.lottieAnimView.cancelAnimation()
    }


    fun startPhoneNumberVerification(phoneNumber: String, animation: Int){
        if (!layout.lottieAnimView.isAnimating){
            if (connectionUtil.isConnected()){
                showAnimation(animation)
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
        phoneAuth.verifyPhoneNumberWithCode(code)
    }

    override fun onBackPressed() {
        if (!layout.lottieAnimView.isAnimating){
            super.onBackPressed()
        }
    }


    fun signInOrSignUpWithGoogle(signInErrorMsg:String){
        if (!layout.lottieAnimView.isAnimating) {
            if (connectionUtil.isConnected()){
                showAnimation(R.raw.google_sign_in)
                resultLauncher =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val data: Intent? = result.data
                            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                            try {
                                // Google Sign In was successful, authenticate with Firebase
                                val account = task.getResult(ApiException::class.java)!!
                                googleAuth.authWithGoogle(account.idToken!!, getString(R.string.authentication)+": "+signInErrorMsg)
                            } catch (e: ApiException) {
                                // Google Sign In failed, update UI appropriately
                                    hideAnimation()
                                googleAuthViewModel.setSignInError(signInErrorMsg)
                            }
                        }else{
                            hideAnimation()
                        }
                    }
                googleAuth.signInOrSignUpWithGoogle(resultLauncher)
            }else{
                Snackbar.make(layout.rootView, R.string.connection_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

}