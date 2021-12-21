package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Build
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
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.bookshelfhub.bookshelfhub.extensions.showErrorToolTip
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.*
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.User
import com.bookshelfhub.bookshelfhub.helpers.Json
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
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var json: Json


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentLoginBinding.inflate(inflater, container, false)

        //Populate UI controls with data based on User login or Sign up
        layout.title.text = args.loginSignup
        val description= String.format(getString(R.string.login_signup_desicription), args.loginSignup)
        val loginSignupText= String.format(getString(R.string.with_phone), args.loginSignup)
        val googleBtnText= String.format(getString(R.string.google_btn_text), args.loginSignup)
        layout.description.text = description
        layout.btnPhoneLogin.text = loginSignupText
        layout.btnGoogleLogin.text = googleBtnText
        layout.ccp.registerCarrierNumberEditText(layout.phoneNumEditText)


        viewLifecycleOwner.lifecycleScope.launch(IO) {
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
            viewLifecycleOwner.lifecycleScope.launch(Main) {
                delay(100)
                withContext(Main){
                    showErrorToolTip(layout.errorAlertBtn, getString(R.string.phone_error_msg)) {
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
                    val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment(true)
                    findNavController().navigate(actionUserInfo)
                }else{

                    cloudDb.getLiveDataAsync(requireActivity(), DbFields.USERS.KEY, userAuth.getUserId(), retry = true){ docSnapShot, _ ->

                        if(docSnapShot!=null && docSnapShot.exists()){
                            try {
                                val jsonObj = docSnapShot.get(DbFields.BOOK_INTEREST.KEY)
                                val bookInterest = json.fromAny(jsonObj!!, BookInterest::class.java)
                                bookInterest.uploaded=true
                                viewLifecycleOwner.lifecycleScope.launch(IO){
                                    database.addBookInterest(bookInterest)
                                }
                            }catch (e:Exception){

                            }

                            try {
                                val userJsonString = docSnapShot.get(DbFields.USER.KEY)
                                val user = json.fromAny(userJsonString!!, User::class.java)
                                if (user.device != DeviceUtil.getDeviceBrandAndModel() || user.deviceOs!=DeviceUtil.getDeviceOSVersionInfo(
                                        Build.VERSION.SDK_INT)){
                                    user.device = DeviceUtil.getDeviceBrandAndModel()
                                    user.deviceOs=DeviceUtil.getDeviceOSVersionInfo(Build.VERSION.SDK_INT)
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

        userAuthViewModel.getIsExistingUser().observe(viewLifecycleOwner, Observer { isExistingUser ->
            if (!isExistingUser){
                val actionUserInfo = LoginFragmentDirections.actionLoginFragmentToUserInfoFragment(false)
                findNavController().navigate(actionUserInfo)
            }
        })

        return layout.root
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
        lifecycleScope.launch(IO) {
            settingsUtil.setString(PHONE, number)
            settingsUtil.setString(DIALING_CODE, layout.ccp.selectedCountryCodeWithPlus)
        }
    }

}