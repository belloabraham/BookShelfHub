package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentUserInfoBinding
import com.bookshelfhub.bookshelfhub.extensions.isFullName
import com.bookshelfhub.bookshelfhub.extensions.isValidEmailAddress
import com.bookshelfhub.bookshelfhub.extensions.isPhoneNumber
import com.bookshelfhub.bookshelfhub.helpers.authentication.AuthType
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.domain.viewmodels.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.models.entities.remote.RemoteUser
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.slider_item.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class UserInfoFragment : Fragment() {
    private var binding:FragmentUserInfoBinding?=null
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var appUtil: AppUtil
    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    @Inject
    lateinit var json: Json
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }


        if (userAuthType== AuthType.GOOGLE.ID){
            layout.phoneEditTxtLayout.visibility=View.VISIBLE
            layout.emailEditTxt.setText(userAuth.getEmail())
        }else{
            layout.emailEditTxtLayout.visibility=View.VISIBLE
            layout.phoneEditTxt.setText(userAuth.getPhone())
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

            if (!email.isValidEmailAddress()){
                layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
            }else if(TextUtils.isEmpty(firstName)){
                layout.firstNameEditTxtLayout.error=getString(R.string.first_name_req_error)
            }else if(TextUtils.isEmpty(lastName)){
                layout.lastNameEditTxtLayout.error=getString(R.string.last_name_req_error)
            }else if(!phone.isPhoneNumber()){
                layout.phoneEditTxtLayout.error=getString(R.string.valid_phone_error)
            }else{
                keyboardUtil.hideKeyboard(layout.emailEditTxt)
                keyboardUtil.hideKeyboard(layout.phoneEditTxt)

                val referrer = userAuthViewModel.getUserReferrerId()
                var referrerId:String?=null
                if (isNewUser){
                    //If an individual user refer this user get that user id
                    referrer?.let {
                        if (it.length==userId.length){
                            referrerId = it
                        }
                    }
                }


                viewLifecycleOwner.lifecycleScope.launch {

                    userAuth.updateDisplayName(firstName)

                    val user = User(userId, userAuthType)
                    user.appVersion=appUtil.getAppVersionName()
                    user.firstName = firstName
                    user.lastName = lastName
                    user.device = DeviceUtil.getDeviceBrandAndModel()
                    user.email = email
                    user.phone = phone
                    //Save the user ID to database
                    user.referrerId = referrerId
                    user.deviceOs = DeviceUtil.getDeviceOSVersionInfo(
                    Build.VERSION.SDK_INT)

                    withContext(Main){
                      userAuthViewModel.addRemoteUserData(RemoteUser(user, null, null))
                      userAuthViewModel.setIsAddingUser(false)
                    }
                }
            }
        }

        return layout.root
    }


    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }


}

