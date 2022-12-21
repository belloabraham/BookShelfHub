package com.bookshelfhub.feature.onboarding.ui

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.core.authentication.AuthType
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.extensions.*
import com.bookshelfhub.core.common.helpers.KeyboardUtil
import com.bookshelfhub.core.common.helpers.utils.AppUtil
import com.bookshelfhub.core.common.helpers.utils.DeviceUtil
import com.bookshelfhub.core.common.helpers.utils.Location
import com.bookshelfhub.core.common.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.core.dynamic_link.Referrer
import com.bookshelfhub.core.model.entities.User
import com.bookshelfhub.core.model.entities.remote.RemoteUser
import com.bookshelfhub.feature.onboarding.WelcomeActivityViewModel
import com.bookshelfhub.feature.onboarding.databinding.FragmentUserInfoBinding
import com.bookshelfhub.payment.EarningsCurrency
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class UserInfoFragment : Fragment() {
    private var binding: FragmentUserInfoBinding?=null
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var appUtil: AppUtil
    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    private val welcomeActivityViewModel: WelcomeActivityViewModel by activityViewModels()
    private val args:UserInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        val layout = binding!!

        val isNewUser = args.isNewUser
        val userId = userAuth.getUserId()
        val userAuthType = userAuth.getAuthType()

        layout.phoneEditTxt.setText(userInfoViewModel.phoneNumber)
        layout.firstNameEditTxt.setText(userInfoViewModel.phoneNumber)
        layout.lastNameEditTxt.setText(userInfoViewModel.lastName)
        layout.emailEditTxt.setText(userInfoViewModel.email)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        if (userAuthType == AuthType.PHONE){
            layout.emailEditTxtLayout.visibility=View.VISIBLE
            layout.phoneEditTxt.setText(userAuth.getPhone())
        }else{
            layout.phoneEditTxtLayout.visibility=View.VISIBLE
            layout.emailEditTxt.setText(userAuth.getEmail())
        }

        layout.firstNameEditTxt.addTextChangedListener {
            it?.let {
                userInfoViewModel.firstName = it.toString()
            }
        }

        layout.lastNameEditTxt.addTextChangedListener{
            it?.let {
                userInfoViewModel.lastName = it.toString()
            }
        }

        layout.emailEditTxt.addTextChangedListener{
            it?.let {
                userInfoViewModel.email = it.toString()
            }
        }

        layout.phoneEditTxt.addTextChangedListener{
            it?.let {
                userInfoViewModel.phoneNumber = it.toString()
            }
        }

        layout.btnContinue.setOnClickListener {

                layout.firstNameEditTxtLayout.error=null
                layout.lastNameEditTxtLayout.error=null
                layout.emailEditTxtLayout.error=null
                layout.phoneEditTxtLayout.error=null

                val email = layout.emailEditTxt.text.toString().trim()
                val phone = layout.phoneEditTxt.text.toString().trim()
                val firstName = layout.firstNameEditTxt.text.toString().trim()
                val lastName = layout.lastNameEditTxt.text.toString().trim()

                val shouldCheckForEmailValidation = userAuthType == AuthType.PHONE && !email.isValidEmailAddress()
                val shouldCheckForPhoneValidation = userAuthType == AuthType.GOOGLE && !phone.isPhoneNumber()
                if (shouldCheckForEmailValidation){
                    layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
                }else if(TextUtils.isEmpty(firstName)){
                    layout.firstNameEditTxtLayout.error=getString(R.string.first_name_req_error)
                }else if(TextUtils.isEmpty(lastName)){
                    layout.lastNameEditTxtLayout.error=getString(R.string.last_name_req_error)
                }else if(shouldCheckForPhoneValidation){
                    layout.phoneEditTxtLayout.error=getString(R.string.valid_phone_error)
                } else{

                    keyboardUtil.hideKeyboard(layout.emailEditTxt)
                    keyboardUtil.hideKeyboard(layout.phoneEditTxt)

                    val optionalUserReferralId = if(isNewUser) welcomeActivityViewModel.getUserReferralId() else null

                    viewLifecycleOwner.lifecycleScope.launch {
                        userAuth.updateDisplayName(firstName)
                    }

                    val timeZone = DateTimeUtil.getTimeZone()
                    val earningsCurrency = EarningsCurrency.getByTimeZone(timeZone)
                    val countryCode = Location.getCountryCode(requireContext().applicationContext)
                    val user = User(userId, userAuthType, countryCode!!, earningsCurrency)

                    var userReferralId:String? = null

                    if(optionalUserReferralId != null){
                        val userReferralIdAndCurrency = optionalUserReferralId.splitBy(Referrer.SEPARATOR[0])
                        val referralCurrency = userReferralIdAndCurrency[1]
                        if(referralCurrency == user.earningsCurrency){
                            userReferralId = userReferralIdAndCurrency[0]
                        }
                    }

                    user.appVersion=appUtil.getAppVersionName()
                    user.firstName = firstName.purifyJSONString()
                    user.lastName = lastName.purifyJSONString()
                    user.device = DeviceUtil.getDeviceBrandAndModel()

                    if (userAuthType == AuthType.PHONE){
                        user.phone = userAuth.getPhone()!!
                        user.email = email.purifyJSONString()
                    }else{
                        user.email =  userAuth.getEmail()!!
                        user.phone = phone.purifyJSONString()
                    }

                    user.referrerId = userReferralId
                    user.deviceOs = DeviceUtil.getDeviceOSVersionInfo(
                        Build.VERSION.SDK_INT)
                    userInfoViewModel.setIsAddingUser(true)
                    userInfoViewModel.addRemoteAndLocalUser(RemoteUser(user, null, null))
                }

        }

        return layout.root
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }


}

