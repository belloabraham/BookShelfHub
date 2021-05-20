package com.bookshelfhub.bookshelfhub.ui.login

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bookshelfhub.bookshelfhub.OnBoardingViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.wrapper.tooltip.ToolTip
import com.hbb20.CountryCodePicker
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
    private val onboardingViewModel: OnBoardingViewModel by activityViewModels()

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
        onboardingViewModel.setIsNewUser(args.isNewUser)


        lifecycleScope.launch(IO) {
           val number = settingsUtil.getString(Settings.PHONE.key)
            val dialingCode = settingsUtil.getString(Settings.DIALING_CODE.key)
            if (number!=null && dialingCode==layout.ccp.selectedCountryCodeWithPlus){
                onboardingViewModel.setPhoneNumber(number)
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
                    tooltip.showPhoneNumErrorBottom(layout.errorAlertBtn, getString(R.string.phone_error_msg), layout.phoneNumEditText) {
                        keyboardUtil.showKeyboard(it)
                    }
                }
            }
        }


        //Try to login or signup user when the done key gets press on keyboard if phone number is valid
        layout.phoneNumEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->

                keyboardUtil.hideKeyboard(layout.phoneNumEditText)
                if (layout.ccp.isValidFullNumber){
                    layout.errorAlertBtn.visibility = View.GONE

                    savePhoneNumber()

                }else{
                    layout.errorAlertBtn.visibility = View.VISIBLE

                }
            actionId == EditorInfo.IME_ACTION_DONE
        })

        //Try to login or signup user when the login or signup button gets press if phone number is valid
        layout.btnPhoneLogin.setOnClickListener {
            if (layout.ccp.isValidFullNumber){
                layout.errorAlertBtn.visibility = View.GONE
                savePhoneNumber()
            }else{
                layout.errorAlertBtn.visibility = View.VISIBLE

            }
        }

        return layout.root
    }

    private fun savePhoneNumber(){
        val number = layout.ccp.fullNumberWithPlus
        lifecycleScope.launch(IO) {
            settingsUtil.setString(Settings.PHONE.key, number)
            settingsUtil.setString(Settings.DIALING_CODE.key, layout.ccp.selectedCountryCodeWithPlus)
        }
        onboardingViewModel.setPhoneNumber(number)
    }

}