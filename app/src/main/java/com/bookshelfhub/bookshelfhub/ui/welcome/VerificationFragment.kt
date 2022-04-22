package com.bookshelfhub.bookshelfhub.ui.welcome

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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.domain.viewmodels.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.domain.viewmodels.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.helpers.textlinkbuilder.TextLinkBuilder
import com.klinker.android.link_builder.applyLinks
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class VerificationFragment:Fragment(){

    private var binding: FragmentVerificationBinding?=null
    private val args:VerificationFragmentArgs by navArgs()
    private var otpCode:String? = null
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()
    private val phoneAuthViewModel: PhoneAuthViewModel by activityViewModels()
    private val verificationViewModel:VerificationViewModel by viewModels()
    @Inject
    lateinit var userAuth: IUserAuth
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
                       layout.otpErrorTxtView.text = getString(R.string.otp_error_msg)
                       layout.otpErrorTxtView.visibility= View.VISIBLE
                   }
                }

                val links = listOf(textLinkBuilder.getTextLink(getString(R.string.resend_code_link)){
                    (requireActivity() as WelcomeActivity).resendVerificationCode(args.phoneNumber, R.raw.loading)
                })

                layout.resendCodeTxtView.applyLinks(links)


                viewLifecycleOwner.lifecycleScope.launch {
                    phoneAuthViewModel.getIsCodeSent().collect{
                        layout.timerTxtView.visibility= VISIBLE
                        layout.resendCodeTxtView.visibility= GONE
                        verificationViewModel.countDownTime().collect{timeRemainingInSec->
                            countDownTime(timeRemainingInSec, layout)
                        }
                    }
                }

                if(userAuthViewModel.isNavigatedFromLogin()){
                    viewLifecycleOwner.lifecycleScope.launch {
                        verificationViewModel.countDownTime().collect{timeRemainingInSec->
                           countDownTime(timeRemainingInSec, layout)
                        }
                    }
                }

                phoneAuthViewModel.getOTPCode().observe(viewLifecycleOwner, Observer { otpCode ->
                    layout.otpView.setOTP(otpCode)
                })

                phoneAuthViewModel.getIsSignedInFailedError().observe(viewLifecycleOwner, Observer { signInErrorMsg ->
                    layout.otpErrorTxtView.text = signInErrorMsg
                    layout.otpErrorTxtView.visibility = View.VISIBLE
                    if (signInErrorMsg==getString(R.string.otp_error_msg)){
                        layout.otpView.setOTP("")
                        layout.otpView.showError()
                    }
                })

                phoneAuthViewModel.getIsSignedInSuccessfully().observe(viewLifecycleOwner, Observer { isSignedInSuccessfully ->
                    if (isSignedInSuccessfully){
                        val isNewUser = phoneAuthViewModel.getIsNewUser()!!
                        if (isNewUser){
                            val actionUserInfo = VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(true)
                            findNavController().navigate(actionUserInfo)
                        }
                    }
                })




                userAuthViewModel.getIsExistingUser().observe(viewLifecycleOwner, Observer { isExistingUser ->
                    if (!isExistingUser){
                        val actionUserInfo = VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(false)
                        findNavController().navigate(actionUserInfo)
                    }
                })

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