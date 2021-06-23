package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.databinding.FragmentUserInfoBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.models.User
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.bookshelfhub.bookshelfhub.wrapper.tooltip.ToolTip
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
class UserInfoFragment : Fragment() {
    private lateinit var layout:FragmentUserInfoBinding
    @Inject
    lateinit var userAuth: UserAuth
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var cloudDb: CloudDb
    @Inject
    lateinit var deviceUtil: DeviceUtil
    @Inject
    lateinit var appUtil:AppUtil
    @Inject
    lateinit var stringUtil:StringUtil
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    @Inject
    lateinit var tooltip: ToolTip
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout= FragmentUserInfoBinding.inflate(inflater, container, false);
        layout.ccp.registerCarrierNumberEditText(layout.phoneEditTxt)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        cloudDb.getDataAsync(DbFields.USERS_COLL.KEY, userAuth.getUserId(), DbFields.USER.KEY, User::class.java){
            it?.let {
               val userData = it as User
                layout.nameEditTxt.setText(userData.name)
                layout.phoneEditTxt.setText(userData.phone)
                layout.emailEditTxt.setText(userData.email)
                lifecycleScope.launch(IO) {
                    withContext(Main){
                        userAuthViewModel.setIsAddingUser(false, userData)
                    }
                }
            }
        }

        layout.phoneEditTxt.doOnTextChanged { text, start, before, count ->
            layout.errorAlertBtn.visibility = View.GONE
        }

        layout.errorAlertBtn.setOnClickListener {
            keyboardUtil.hideKeyboard(layout.phoneEditTxt)
            lifecycleScope.launch(Main) {
                delay(100)
                withContext(Main){
                    tooltip.showPhoneNumErrorBottom(layout.errorAlertBtn, getString(R.string.phone_error_msg)) {
                        keyboardUtil.showKeyboard(layout.phoneEditTxt)
                    }
                }
            }
        }


        if (userAuth.getAuthType()==AuthType.GOOGLE.ID){
            layout.phoneEditTxtLayout.visibility=View.VISIBLE
            layout.nameEditTxt.setText(userAuth.getName())
            layout.emailEditTxt.setText(userAuth.getEmail())
        }else{
            layout.emailEditTxtLayout.visibility=View.VISIBLE
            layout.phoneEditTxt.setText(userAuth.getPhone())
        }

        layout.btnContinue.setOnClickListener {

            layout.nameEditTxtLayout.error=null
            layout.emailEditTxtLayout.error=null
            layout.errorAlertBtn.visibility = View.GONE
            val email = layout.emailEditTxt.text.toString()
            val phone = layout.phoneEditTxt.text.toString().replace(" ","").trim()
            val name = layout.nameEditTxt.text.toString()

            if (!stringUtil.isValidEmailAddress(email)){
                layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
            }else if(TextUtils.isEmpty(name)){
                layout.emailEditTxtLayout.error=getString(R.string.empty_name_error)
            }else if((layout.ccp.selectedCountryCodeWithPlus=="+234" && phone.length<9) || !layout.ccp.isValidFullNumber){
                layout.errorAlertBtn.visibility = View.VISIBLE
            }else{
                lifecycleScope.launch(IO) {
                    val localDateTime= DateTimeUtil.getDateTimeAsString()
                    val user = UserRecord(userAuth.getUserId(),name, email,phone, null, userAuth.getAuthType(),appUtil.getAppVersionName(), deviceUtil.getDeviceBrandAndModel(), deviceUtil.getDeviceOSVersionInfo(
                        Build.VERSION.SDK_INT), localDateTime)
                    withContext(Main){
                      userAuthViewModel.setIsAddingUser(false, user)
                    }
                }
            }
        }

        return layout.root
    }

}

