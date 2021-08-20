package com.bookshelfhub.bookshelfhub.ui.welcome

import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.WelcomeActivity
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.enums.VeriFragSavedState
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.PhoneAuthViewModel
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.bookshelfhub.bookshelfhub.wrappers.Json
import com.bookshelfhub.bookshelfhub.wrappers.textlinkbuilder.TextLinkBuilder
import com.klinker.android.link_builder.applyLinks
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
    private val timerDurationinMilliSec = 180000L
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var json: Json
    @Inject
    lateinit var textLinkBuilder:TextLinkBuilder
    @Inject
    lateinit var deviceUtil: DeviceUtil
    @Inject
    lateinit var appUtil: AppUtil

            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

                if (savedInstanceState!=null){
                    inProgress =  savedInstanceState[VeriFragSavedState.IN_PROGRESS.KEY] as Boolean
                }

        layout= FragmentVerificationBinding.inflate(inflater, container, false);

                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

                }

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

                val links = listOf(textLinkBuilder.getTextLink(getString(R.string.resend_code_link)){
                    (requireActivity() as WelcomeActivity).resendVerificationCode(args.phoneNumber, R.raw.loading)
                })

                layout.resendCodeTxtView.applyLinks(links)


                if (inProgress){
                     phoneAuthViewModel.startTimer(timerDurationinMilliSec)
                     inProgress=false
                 }

                phoneAuthViewModel.getTimerTimeRemaining().observe(viewLifecycleOwner, Observer { timeRemainingInSec ->
                    if (timeRemainingInSec!=0L){
                        val min = timeRemainingInSec/60L
                        val sec = timeRemainingInSec%60L
                        layout.timerTxtView.setText(String.format(getString(R.string.time_remaining), min, sec))
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
                            val actionUserInfo = VerificationFragmentDirections.actionVerificationFragmentToUserInfoFragment(true)
                            findNavController().navigate(actionUserInfo)
                        }else{

                            cloudDb.getLiveDataAsync(DbFields.USERS.KEY, userAuth.getUserId(), retry = true){ docSnapShot, _ ->

                                if(docSnapShot!=null && docSnapShot.exists()){
                                    try {
                                        val jsonString = docSnapShot.get(DbFields.BOOK_INTEREST.KEY)
                                        val bookInterest = json.fromAny(jsonString!!, BookInterest::class.java)

                                        bookInterest.uploaded=true
                                        lifecycleScope.launch(IO){
                                            database.addBookInterest(bookInterest)
                                        }
                                    }catch (e:Exception){
                                    }

                                    try {
                                        val userJsonString = docSnapShot.get(DbFields.USER.KEY)
                                        val user = json.fromAny(userJsonString!!, User::class.java)
                                        if (user.device != deviceUtil.getDeviceBrandAndModel() || user.deviceOs!=deviceUtil.getDeviceOSVersionInfo(
                                                Build.VERSION.SDK_INT)){
                                            user.device =   deviceUtil.getDeviceBrandAndModel()
                                            user.deviceOs=deviceUtil.getDeviceOSVersionInfo(Build.VERSION.SDK_INT)
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(VeriFragSavedState.IN_PROGRESS.KEY, inProgress)
        super.onSaveInstanceState(outState)
    }


}