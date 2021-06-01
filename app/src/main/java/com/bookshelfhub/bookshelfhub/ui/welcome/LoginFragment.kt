package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuthViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.view.toast.Toasty
import com.bookshelfhub.bookshelfhub.wrapper.tooltip.ToolTip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class LoginFragment:Fragment() {

    private lateinit var layout: FragmentLoginBinding;
    private val args:LoginFragmentArgs by navArgs()
    private lateinit var phoneNumber:String
    private val phoneAuthViewModel: PhoneAuthViewModel by activityViewModels()
    private val googleAuthViewModel: GoogleAuthViewModel by activityViewModels()
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()
    private val PHONE = "phone"
    private val DIALING_CODE="dialingCode"

    //Injecting class instance with Dagger Hilt
    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    @Inject
    lateinit var tooltip: ToolTip
    @Inject
    lateinit var settingsUtil: SettingsUtil


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentLoginBinding.inflate(inflater, container, false);

        //Populate UI controls with data based on User login or Sign up
        layout.title.setText(args.loginSignup)
        val description= String.format(getString(R.string.login_signup_desicription), args.loginSignup)
        val loginSignupText= String.format(getString(R.string.with_phone), args.loginSignup)
        val googleBtnText= String.format(getString(R.string.google_btn_text), args.loginSignup)
        layout.description.setText(description)
        layout.btnPhoneLogin.setText(loginSignupText)
        layout.btnGoogleLogin.setText(googleBtnText)
        layout.ccp.registerCarrierNumberEditText(layout.phoneNumEditText)


        lifecycleScope.launch(IO) {
           val number = settingsUtil.getString(PHONE)
            val dialingCode = settingsUtil.getString(DIALING_CODE)
            if (number!=null && dialingCode==layout.ccp.selectedCountryCodeWithPlus){
                withContext(Main) {
                    val phone  = number.replace(layout.ccp.selectedCountryCodeWithPlus,"")
                    layout.phoneNumEditText.setText(phone)
                }
            }
        }

        //Hide error button as user edit or re-type phone number
       layout.phoneNumEditText.doOnTextChanged { text, start, before, count ->
           layout.errorAlertBtn.visibility = View.GONE
       }

        //Hide keyboard and show error message when error button is clicked
        layout.errorAlertBtn.setOnClickListener {

            keyboardUtil.hideKeyboard(layout.phoneNumEditText)

            lifecycleScope.launch(Main) {
                delay(100)
                withContext(Main){
                    tooltip.showPhoneNumErrorBottom(layout.errorAlertBtn, getString(R.string.phone_error_msg)) {
                        keyboardUtil.showKeyboard(layout.phoneNumEditText)
                    }
                }
            }
        }


        //Try to login or signup user when the done key gets press on keyboard if phone number is valid
        layout.phoneNumEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
               keyboardUtil.hideKeyboard(layout.phoneNumEditText)
                startPhoneNumberVerification()
               actionId == EditorInfo.IME_ACTION_DONE
        })

        //Try to login or signup user when the login or signup button gets press if phone number is valid

        layout.btnPhoneLogin.setOnClickListener {
                startPhoneNumberVerification()
        }

        layout.btnGoogleLogin.setOnClickListener {
            val errorMsg = String.format(getString(R.string.google_signin_error), args.loginSignup)
            (requireActivity() as WelcomeActivity).signInOrSignUpWithGoogle(errorMsg)
        }

        phoneAuthViewModel.getIsCodeSent().observe(viewLifecycleOwner, Observer { isCodeSent ->
            if (isCodeSent){
                val actionVerifyPhone = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(phoneNumber)
                findNavController().navigate(actionVerifyPhone)
            }
        })

        googleAuthViewModel.getIsAuthenticatedSuccessful().observe(viewLifecycleOwner, Observer { isAuthSuccessful ->
            if (isAuthSuccessful){
                val isNewUser = googleAuthViewModel.getIsNewUser().value!!
                if (isNewUser){
                    val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment()
                    findNavController().navigate(actionUserInfo)
                }else{
                    //Todo try to get user data from the cloud first, if fail stop animation and navigate to UserInfo fragment where I will try again, if pass stop animation and navigate to main activity using setIsAddingUser to false
                }
            }
        })

        userAuthViewModel.getIsExistingUser().observe(viewLifecycleOwner, Observer { isExistingUser ->
            if (!isExistingUser){
                val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment()
                findNavController().navigate(actionUserInfo)
            }
        })

        return layout.root
    }


    private fun startPhoneNumberVerification(){
        if (layout.ccp.isValidFullNumber){
            layout.errorAlertBtn.visibility = View.GONE
            phoneNumber=layout.ccp.fullNumberWithPlus
            savePhoneNumber(phoneNumber)
            (requireActivity() as WelcomeActivity).startPhoneNumberVerification(phoneNumber)
            layout.ccp.isEnabled = false
            layout.phoneNumEditText.isEnabled=false
        }else{
            layout.errorAlertBtn.visibility = View.VISIBLE
        }
    }

    private fun savePhoneNumber(number: String){
        lifecycleScope.launch(IO) {
            settingsUtil.setString(PHONE, number)
            settingsUtil.setString(DIALING_CODE, layout.ccp.selectedCountryCodeWithPlus)
        }
    }

}