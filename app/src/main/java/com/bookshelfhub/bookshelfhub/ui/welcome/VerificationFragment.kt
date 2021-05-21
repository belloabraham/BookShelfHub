package com.bookshelfhub.bookshelfhub.ui.welcome

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding
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

    private val welcomeViewModel:WelcomeViewModel by activityViewModels()

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
                    }
                    override fun onOTPComplete(otp: String) {
                       otpCode=otp
                    }
                }

                layout.verifyBtn.setOnClickListener {
                   if (otpCode?.length == resources.getInteger(R.integer.otp_code_length)  ){
                        //TODO Start phone number verification
                   }else{
                       layout.otpView.showError()
                   }
                }

                textLinkBuilder.createTextLink(layout.resendCodeTxtView, getString(R.string.resend_code_link),
                    ContextCompat.getColor(this@VerificationFragment.requireContext(), R.color.purple_700)
                ){

                    //TODO re-send authentication code to firebase again
                }

                welcomeViewModel.startTimer(20000L)


                welcomeViewModel.getIsTimerCompleted().observe(viewLifecycleOwner, Observer { isTimerCompleted ->

                    if (isTimerCompleted!=null && isTimerCompleted==true){
                        layout.timerTxtView.visibility=View.GONE
                        layout.resendCodeTxtView.visibility=View.VISIBLE
                    }

                })

                welcomeViewModel.getTimerTimeRemaining().observe(viewLifecycleOwner, Observer { timeRemainingInSec ->

                    if (timeRemainingInSec!=0L){
                        layout.timerTxtView.setText(String.format(getString(R.string.seconds_remaining), timeRemainingInSec))
                    }

                })


                

        return layout.root
    }

}