package com.bookshelfhub.feature.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.feature.onboarding.R
import com.bookshelfhub.core.common.extensions.showErrorToolTip
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.authentication.view_models.GoogleAuthViewModel
import com.bookshelfhub.core.authentication.view_models.PhoneAuthViewModel
import com.bookshelfhub.core.common.helpers.KeyboardUtil
import com.bookshelfhub.feature.onboarding.WelcomeActivity
import com.bookshelfhub.feature.onboarding.WelcomeActivityViewModel
import com.bookshelfhub.feature.onboarding.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class LoginFragment:Fragment() {

    private var binding: FragmentLoginBinding?=null
    private lateinit var layout: FragmentLoginBinding

    private val args:LoginFragmentArgs by navArgs()
    private lateinit var phoneNumber:String
    private val phoneAuthViewModel: PhoneAuthViewModel by activityViewModels()
    private val googleAuthViewModel: GoogleAuthViewModel by activityViewModels()
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    private val welcomeActivityViewModel: WelcomeActivityViewModel by activityViewModels()


    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    @Inject
    lateinit var settingsUtil: SettingsUtil


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentLoginBinding.inflate(inflater, container, false)
        layout = binding!!


        layout.title.text = args.loginSignup
        val description= String.format(getString(R.string.login_signup_desicription), args.loginSignup)
        val loginSignupText= String.format(getString(R.string.with_phone), args.loginSignup)
        val googleBtnText= String.format(getString(R.string.google_btn_text), args.loginSignup)
        layout.description.text = description
        layout.btnPhoneLogin.text = loginSignupText
        layout.btnGoogleLogin.text = googleBtnText
        layout.ccp.registerCarrierNumberEditText(layout.phoneNumEditText)


        viewLifecycleOwner.lifecycleScope.launch {
           val previousPhoneNumber = settingsUtil.getString(Settings.PHONE)
            val previousDialingCode = settingsUtil.getString(Settings.DIALING_CODE)
            if (previousPhoneNumber!=null && previousDialingCode==layout.ccp.selectedCountryCodeWithPlus){
                val phoneNumberToBeInEditText  = previousPhoneNumber.replace(layout.ccp.selectedCountryCodeWithPlus,"")
                layout.phoneNumEditText.setText(phoneNumberToBeInEditText)
            }
        }

       layout.phoneNumEditText.doOnTextChanged { _, _, _, _ ->
           layout.errorAlertBtn.visibility = View.GONE
       }

        layout.errorAlertBtn.setOnClickListener {
            keyboardUtil.hideKeyboard(layout.phoneNumEditText)
            viewLifecycleOwner.lifecycleScope.launch(Main) {
                delay(100) //Delay before showing tool tip to avoid animation flicker
                withContext(Main){
                    showErrorToolTip(layout.errorAlertBtn, getString(R.string.phone_error_msg)) {
                        keyboardUtil.showKeyboard(layout.phoneNumEditText)
                    }
                }
            }
        }


        layout.phoneNumEditText.setOnEditorActionListener { _, actionId, _ ->
            keyboardUtil.hideKeyboard(layout.phoneNumEditText)
            startPhoneNumberVerification()
            actionId == EditorInfo.IME_ACTION_DONE
        }

        layout.btnPhoneLogin.setOnClickListener {
                startPhoneNumberVerification()
        }

        layout.btnGoogleLogin.setOnClickListener {
            val errorMsg = String.format(getString(R.string.google_signin_error), args.loginSignup)
            (requireActivity() as WelcomeActivity).signInOrSignUpWithGoogle(errorMsg)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            phoneAuthViewModel.getIsCodeSent().collect{ codeIsSent ->
                if (codeIsSent){
                    val actionVerifyPhone = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(phoneNumber)
                    welcomeActivityViewModel.setIsNavigatedFromLogin(true)
                    findNavController().navigate(actionVerifyPhone)
                }
            }
        }


        googleAuthViewModel.getIsAuthenticatedSuccessful().observe(viewLifecycleOwner) { isAuthSuccessful ->
            if (isAuthSuccessful){
                val isNewUser = googleAuthViewModel.getIsNewUser()!!
                if (isNewUser){
                    val actionUserInfo =  LoginFragmentDirections.actionLoginFragmentToUserInfoFragment(true)
                    findNavController().navigate(actionUserInfo)
                }
            }
        }

        userInfoViewModel.getIsUserDataAlreadyInRemoteDatabase().observe(viewLifecycleOwner) { isExistingUser ->
            if (!isExistingUser){
                val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment(false)
                findNavController().navigate(actionUserInfo)
            }
        }

        return layout.root
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

    private fun startPhoneNumberVerification(){
        val phone = layout.phoneNumEditText.text.toString().replace(" ","").trim()
        if ((layout.ccp.selectedCountryCodeWithPlus=="+234" && phone.length<9) || !layout.ccp.isValidFullNumber){
            layout.errorAlertBtn.visibility = View.VISIBLE
        }else{
            layout.errorAlertBtn.visibility = View.GONE
            phoneNumber=layout.ccp.fullNumberWithPlus
            savePhoneNumberToSettings(phoneNumber)
            (requireActivity() as WelcomeActivity).startPhoneNumberVerification(phoneNumber)
            layout.ccp.isEnabled = false
            layout.phoneNumEditText.isEnabled=false
        }
    }

    private fun savePhoneNumberToSettings(number: String){
        lifecycleScope.launch {
            settingsUtil.setString(Settings.PHONE, number)
            settingsUtil.setString(Settings.DIALING_CODE, layout.ccp.selectedCountryCodeWithPlus)
        }
    }

}