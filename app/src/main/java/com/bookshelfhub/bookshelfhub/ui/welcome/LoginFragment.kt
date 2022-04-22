package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.bookshelfhub.bookshelfhub.domain.viewmodels.GoogleAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.extensions.showErrorToolTip
import com.bookshelfhub.bookshelfhub.helpers.utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.helpers.authentication.*
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
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

    private var binding: FragmentLoginBinding?=null
    private lateinit var layout: FragmentLoginBinding

    private val args:LoginFragmentArgs by navArgs()
    private lateinit var phoneNumber:String
    private val phoneAuthViewModel: PhoneAuthViewModel by activityViewModels()
    private val googleAuthViewModel: GoogleAuthViewModel by activityViewModels()
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()


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
           val number = settingsUtil.getString(Settings.PHONE)
            val dialingCode = settingsUtil.getString(Settings.DIALING_CODE)
            if (number!=null && dialingCode==layout.ccp.selectedCountryCodeWithPlus){
                val phone  = number.replace(layout.ccp.selectedCountryCodeWithPlus,"")
                layout.phoneNumEditText.setText(phone)
            }
        }

       layout.phoneNumEditText.doOnTextChanged { text, start, before, count ->
           layout.errorAlertBtn.visibility = View.GONE
       }

        layout.errorAlertBtn.setOnClickListener {
            keyboardUtil.hideKeyboard(layout.phoneNumEditText)
            viewLifecycleOwner.lifecycleScope.launch(Main) {
                delay(100)
                withContext(Main){
                    showErrorToolTip(layout.errorAlertBtn, getString(R.string.phone_error_msg)) {
                        keyboardUtil.showKeyboard(layout.phoneNumEditText)
                    }
                }
            }
        }


        layout.phoneNumEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
               keyboardUtil.hideKeyboard(layout.phoneNumEditText)
                startPhoneNumberVerification()
               actionId == EditorInfo.IME_ACTION_DONE
        })

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
                    userAuthViewModel.setIsNavigatedFromLogin(true)
                    findNavController().navigate(actionVerifyPhone)
                }
            }
        }


        googleAuthViewModel.getIsAuthenticatedSuccessful().observe(viewLifecycleOwner, Observer { isAuthSuccessful ->
            if (isAuthSuccessful){
                val isNewUser = googleAuthViewModel.getIsNewUser()!!
                if (isNewUser){
                    val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment(true)
                    findNavController().navigate(actionUserInfo)
                }
            }
        })

        userAuthViewModel.getIsExistingUser().observe(viewLifecycleOwner, Observer { isExistingUser ->
            if (!isExistingUser){
                val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment(false)
                findNavController().navigate(actionUserInfo)
            }
        })

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
            savePhoneNumber(phoneNumber)
            (requireActivity() as WelcomeActivity).startPhoneNumberVerification(phoneNumber)
            layout.ccp.isEnabled = false
            layout.phoneNumEditText.isEnabled=false
        }
    }

    private fun savePhoneNumber(number: String){
        lifecycleScope.launch {
            settingsUtil.setString(Settings.PHONE, number)
            settingsUtil.setString(Settings.DIALING_CODE, layout.ccp.selectedCountryCodeWithPlus)
        }
    }

}