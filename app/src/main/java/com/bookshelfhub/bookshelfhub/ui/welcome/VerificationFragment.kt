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
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding
import com.bookshelfhub.bookshelfhub.wrapper.textlinkbuilder.TextLinkBuilder
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class VerificationFragment:Fragment(){

    private lateinit var layout: FragmentVerificationBinding;
    private val args:VerificationFragmentArgs by navArgs()
    private var otpCode:String? = null

    private val welcomeActivityViewModel:WelcomeActivityViewModel by activityViewModels()

    @Inject
    lateinit var textLinkBuilder:TextLinkBuilder

            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                        //TODO Start phone number verification
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
                    (requireActivity() as WelcomeActivity).resendVerificationCode(args.phoneNumber)
                }

                welcomeActivityViewModel.startTimer(20000L)


                welcomeActivityViewModel.getTimerTimeRemaining().observe(viewLifecycleOwner, Observer { timeRemainingInSec ->

                    if (timeRemainingInSec!=0L){
                        layout.timerTxtView.setText(String.format(getString(R.string.seconds_remaining), timeRemainingInSec))
                    }else{
                        layout.timerTxtView.visibility=View.GONE
                        layout.resendCodeTxtView.visibility=View.VISIBLE
                    }

                })

                welcomeActivityViewModel.getOTPCode().observe(viewLifecycleOwner, Observer { otpCode ->
                    layout.otpView.setOTP(otpCode)

                })

                welcomeActivityViewModel.getIsSignedInFailedError().observe(viewLifecycleOwner, Observer { signInErrorMsg ->
                    layout.otpErrorTxtView.setText(signInErrorMsg)
                    layout.otpErrorTxtView.visibility = View.VISIBLE
                    if (signInErrorMsg==getString(R.string.otp_error_msg)){
                        layout.otpView.setOTP("")
                        layout.otpView.showError()
                    }
                })


                return layout.root
    }

}