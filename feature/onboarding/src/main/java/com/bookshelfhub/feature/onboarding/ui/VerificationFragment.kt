package com.bookshelfhub.feature.onboarding.ui

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.feature.onboarding.WelcomeActivity
import com.bookshelfhub.core.authentication.view_models.PhoneAuthViewModel
import com.bookshelfhub.core.common.extensions.applyLinks
import com.bookshelfhub.core.common.helpers.textlinkbuilder.TextLinkBuilder
import com.bookshelfhub.feature.onboarding.WelcomeActivityViewModel
import com.bookshelfhub.feature.onboarding.databinding.FragmentVerificationBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class VerificationFragment:Fragment(){

    private var binding: FragmentVerificationBinding?=null
    private val args:VerificationFragmentArgs by navArgs()
    private var otpCode:String? = null
    private val phoneAuthViewModel: PhoneAuthViewModel by activityViewModels()
    private val verificationViewModel: VerificationViewModel by viewModels()
    private val welcomeActivityViewModel: WelcomeActivityViewModel by activityViewModels()
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()

    @Inject
    lateinit var textLinkBuilder: TextLinkBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        val layout = binding!!

                layout.phoneNumberTxt.text = args.phoneNumber

       layout.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                otpCode=layout.otpView.otp
                layout.otpErrorTxtView.visibility= GONE
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
               layout.otpErrorTxtView.text = getString(R.string.otp_error_msg)
               layout.otpErrorTxtView.visibility=  VISIBLE
           }
        }

        val links = listOf(textLinkBuilder.getTextLink(getString(R.string.resend_code_link)){
            (requireActivity() as WelcomeActivity).resendVerificationCode(args.phoneNumber, com.bookshelfhub.feature.onboarding.R.raw.loading)
        })

        layout.resendCodeTxtView.applyLinks(links)

        viewLifecycleOwner.lifecycleScope.launch {
            /*By using sharedFlow with collect{} there will be no cached value for this to get triggered by default
            But only when a value get again sent by clicking the retry link*/
            phoneAuthViewModel.getIsCodeSent().collect{
                verificationViewModel.startCountDownTimer()
                layout.timerTxtView.visibility = VISIBLE
                layout.resendCodeTxtView.visibility = GONE
                verificationViewModel.timerRemainingFromCountDown.collect{ timeRemainingInSec->
                    countDownTime(timeRemainingInSec, layout)
                }
            }
        }

        /*
        Start reading the default timer time remaining value that get started by the verification fragment
        This way if the activity resumes from total memory loss isNavigatedFromLogin will be false thereby showing only the retry link and not the count down progress
        */
        if(welcomeActivityViewModel.isNavigatedFromLogin()){
            layout.timerTxtView.visibility = VISIBLE
            layout.resendCodeTxtView.visibility = GONE
            viewLifecycleOwner.lifecycleScope.launch {
                verificationViewModel.timerRemainingFromCountDown.collect{ timeRemainingInSec->
                   countDownTime(timeRemainingInSec, layout)
                }
            }
        }

        phoneAuthViewModel.getOTPCode().observe(viewLifecycleOwner) { otpCode ->
            layout.otpView.setOTP(otpCode)
        }

        phoneAuthViewModel.getIsSignedInFailedError().observe(viewLifecycleOwner) { signInErrorMsg ->
            layout.otpErrorTxtView.text = signInErrorMsg
            layout.otpErrorTxtView.visibility = VISIBLE
            if (signInErrorMsg == getString(R.string.otp_error_msg)) {
                layout.otpView.setOTP("")
                layout.otpView.showError()
            }
        }

        phoneAuthViewModel.getIsSignedInSuccessfully().observe(viewLifecycleOwner) { isSignedInSuccessfully ->
            if (isSignedInSuccessfully) {
                val isNewUser = phoneAuthViewModel.getIsNewUser()!!
                if (isNewUser) {
                    val actionUserInfo =
                        VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(
                            true
                        )
                    findNavController().navigate(actionUserInfo)
                }
            }
        }

        userInfoViewModel.getIsUserDataAlreadyInRemoteDatabase().observe(viewLifecycleOwner) { isExistingUser ->
            if (!isExistingUser) {
                val actionUserInfo =
                    VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(
                        false
                    )
                findNavController().navigate(actionUserInfo)
            }
        }

        return layout.root
    }

    private fun countDownTime(timeRemainingInSec:Long, layout:FragmentVerificationBinding){
        if (timeRemainingInSec!=0L){
            val min = timeRemainingInSec/60L
            val sec = timeRemainingInSec%60L
            layout.timerTxtView.text =
                String.format(getString(R.string.time_remaining), min, sec)
        }else{
            layout.timerTxtView.visibility=GONE
            layout.resendCodeTxtView.visibility=VISIBLE
        }
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }
}