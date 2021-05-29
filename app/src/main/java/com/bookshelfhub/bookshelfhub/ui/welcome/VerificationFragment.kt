package com.bookshelfhub.bookshelfhub.ui.welcome

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding
import com.bookshelfhub.bookshelfhub.enums.FragSavedState
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.wrapper.textlinkbuilder.TextLinkBuilder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class VerificationFragment:Fragment(){

    private lateinit var layout: FragmentVerificationBinding;
    private val args:VerificationFragmentArgs by navArgs()
    private var otpCode:String? = null
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()
    private val phoneAuthViewModel: PhoneAuthViewModel by activityViewModels()
    private var inProgress:Boolean=true

    @Inject
    lateinit var textLinkBuilder:TextLinkBuilder

            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

                if (savedInstanceState!=null){
                    inProgress =  savedInstanceState[FragSavedState.IN_PROGRESS.KEY] as Boolean
                }

                phoneAuthViewModel.setIsCodeSent(false)
        layout= FragmentVerificationBinding.inflate(inflater, container, false);
        layout.phoneNumberTxt.setText(args.phoneNumber)

               layout.otpView.otpListener = object : OTPListener {
                    override fun onInteractionListener() {
                        otpCode=layout.otpView.otp
                        layout.otpErrorTxtView.visibility= View.GONE
                    }
                    override fun onOTPComplete(otp: String) {
                       otpCode=otp
                    }
                }

                layout.verifyBtn.setOnClickListener {
                   if (otpCode?.length == resources.getInteger(R.integer.otp_code_length)  ){
                     (requireActivity() as WelcomeActivity).verifyPhoneNumberWithCode(otpCode!!)
                   }else{
                       layout.otpView.showError()
                       layout.otpErrorTxtView.setText(getString(R.string.otp_error_msg))
                       layout.otpErrorTxtView.visibility= View.VISIBLE
                   }
                }

                textLinkBuilder.createTextLink(layout.resendCodeTxtView, getString(R.string.resend_code_link),
                    ContextCompat.getColor(this@VerificationFragment.requireContext(), R.color.purple_700)
                ){
                        (requireActivity() as WelcomeActivity).resendVerificationCode(args.phoneNumber, R.raw.mail_send)
                }

                if (inProgress){
                    phoneAuthViewModel.startTimer(60000L)
                    inProgress=false
                }

                phoneAuthViewModel.getTimerTimeRemaining().observe(viewLifecycleOwner, Observer { timeRemainingInSec ->

                    if (timeRemainingInSec!=0L){
                        layout.timerTxtView.setText(String.format(getString(R.string.seconds_remaining), timeRemainingInSec))
                    }else{
                        layout.timerTxtView.visibility=View.GONE
                        layout.resendCodeTxtView.visibility=View.VISIBLE
                    }

                })

                phoneAuthViewModel.getOTPCode().observe(viewLifecycleOwner, Observer { otpCode ->
                    layout.otpView.setOTP(otpCode)

                })

                phoneAuthViewModel.getIsSignedInFailedError().observe(viewLifecycleOwner, Observer { signInErrorMsg ->
                    layout.otpErrorTxtView.setText(signInErrorMsg)
                    layout.otpErrorTxtView.visibility = View.VISIBLE
                    if (signInErrorMsg==getString(R.string.otp_error_msg)){
                        layout.otpView.setOTP("")
                        layout.otpView.showError()
                    }
                })

                phoneAuthViewModel.getIsSignedInSuccessfully().observe(viewLifecycleOwner, Observer { isSignedInSuccessfully ->
                    if (isSignedInSuccessfully){
                        val isNewUser = phoneAuthViewModel.getIsNewUser().value!!
                        if (isNewUser){
                            val actionUserInfo = VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment()
                            findNavController().navigate(actionUserInfo)
                        }else{
                            //Todo try to get user data from the cloud first, if fail, hide animation then navigate to UserInfo fragment where I will try again, if not fail set hide animation and navigate to main activity

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FragSavedState.IN_PROGRESS.KEY, inProgress)
        super.onSaveInstanceState(outState)
    }

}