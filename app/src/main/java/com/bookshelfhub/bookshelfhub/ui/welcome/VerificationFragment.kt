package com.bookshelfhub.bookshelfhub.ui.welcome

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.domain.data.repos.sources.remote.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.textlinkbuilder.TextLinkBuilder
import com.klinker.android.link_builder.applyLinks
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
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
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var json: Json
    @Inject
    lateinit var textLinkBuilder: TextLinkBuilder
    @Inject
    lateinit var appUtil: AppUtil

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


                verificationViewModel.getTimerTimeRemaining().observe(viewLifecycleOwner, Observer { timeRemainingInSec ->
                    if (timeRemainingInSec!=0L){
                        val min = timeRemainingInSec/60L
                        val sec = timeRemainingInSec%60L
                        layout.timerTxtView.text =
                            String.format(getString(R.string.time_remaining), min, sec)
                    }else{
                        layout.timerTxtView.visibility=View.GONE
                        layout.resendCodeTxtView.visibility=View.VISIBLE
                    }

                })

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

                //This gets triggered if user is new user from firebase authentication
                phoneAuthViewModel.getIsSignedInSuccessfully().observe(viewLifecycleOwner, Observer { isSignedInSuccessfully ->
                    if (isSignedInSuccessfully){
                        val isNewUser = phoneAuthViewModel.getIsNewUser().value!!
                        if (isNewUser){
                            val actionUserInfo = VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(true)
                            findNavController().navigate(actionUserInfo)
                        }
                    }
                })

                //This gets triggered if user is not new user from firebase authenetication but user data does not exist
                //On firestire
                userAuthViewModel.getIsExistingUser().observe(viewLifecycleOwner, Observer { isExistingUser ->
                    if (!isExistingUser){
                        val actionUserInfo = VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(false)
                        findNavController().navigate(actionUserInfo)
                    }
                })

                return layout.root
    }


    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

    override fun onDestroy() {
        verificationViewModel.setInProgress(false)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        verificationViewModel.setInProgress(true)
        super.onSaveInstanceState(outState)
    }


}