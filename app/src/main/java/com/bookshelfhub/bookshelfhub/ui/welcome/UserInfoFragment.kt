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
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.databinding.FragmentUserInfoBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.bookshelfhub.bookshelfhub.wrapper.Json
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
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

        layout= FragmentUserInfoBinding.inflate(inflater, container, false);

        val isNewUser = args.isNewUser
        val userId = userAuth.getUserId()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        }

        if (!isNewUser) {
            cloudDb.getDataAsync(DbFields.USERS_COLL.KEY, userId) {

                it?.let {
                    try {
                        val jsonObj = it.get(DbFields.BOOK_INTEREST.KEY).toString()
                        val bookInterest = json.fromJson(jsonObj, BookInterestRecord::class.java)
                        bookInterest.uploaded = true
                        lifecycleScope.launch(IO) {
                            database.addBookInterest(bookInterest)
                        }
                    } catch (e: Exception) {
                    }

                    try {
                        val userJsonString = it.get(DbFields.USER.KEY).toString()
                        val userData = json.fromJson(userJsonString, UserRecord::class.java)
                        layout.nameEditTxt.setText(userData.name)
                        layout.phoneEditTxt.setText(userData.phone)
                        layout.emailEditTxt.setText(userData.email)
                        userData.uploaded = true
                        lifecycleScope.launch(IO) {
                            withContext(Main) {
                                userAuthViewModel.setIsAddingUser(false, userData)
                            }
                        }
                    } catch (e: Exception) {
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
            layout.phoneEditTxtLayout.error=null
            val email = layout.emailEditTxt.text.toString()
            val phone = layout.phoneEditTxt.text.toString()
            val name = layout.nameEditTxt.text.toString()

            if (!stringUtil.isValidEmailAddress(email)){
                layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
            }else if(TextUtils.isEmpty(name)){
                layout.emailEditTxtLayout.error=getString(R.string.empty_name_error)
            }else if(!stringUtil.isValidPhoneNumber(phone)){
                layout.phoneEditTxtLayout.error=getString(R.string.valid_phone_error)
            }else{
                keyboardUtil.hideKeyboard(layout.emailEditTxt)
                keyboardUtil.hideKeyboard(layout.phoneEditTxt)

                val referrer = userAuthViewModel.getReferrer()
                var referrerId:String?=null
                if (isNewUser){
                    referrer?.let {
                        if (it.length==userId.length){
                            referrerId = it
                        }
                    }
                }

                lifecycleScope.launch(IO) {
                    val localDateTime= LocalDateTimeUtil.getDateTimeAsString()
                    val user = UserRecord(userId, userAuth.getAuthType())
                    user.appVersion=appUtil.getAppVersionName()
                    user.name = name
                    user.device = deviceUtil.getDeviceBrandAndModel()
                    user.email = email
                    user.phone= phone
                    user.referrerId = referrerId
                    user.deviceOs = deviceUtil.getDeviceOSVersionInfo(
                    Build.VERSION.SDK_INT)
                    user.lastUpdated = localDateTime
                    withContext(Main){
                      userAuthViewModel.setIsAddingUser(false, user)
                    }
                }
            }
        }

        return layout.root
    }

}

