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
import com.bookshelfhub.bookshelfhub.extensions.isFullName
import com.bookshelfhub.bookshelfhub.extensions.isValidEmailAddress
import com.bookshelfhub.bookshelfhub.extensions.isPhoneNumber
import com.bookshelfhub.bookshelfhub.services.authentication.AuthType
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.User
import com.bookshelfhub.bookshelfhub.helpers.Json
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
    private var binding:FragmentUserInfoBinding?=null
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var appUtil:AppUtil
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        }

        if (!isNewUser) {
            cloudDb.getLiveDataAsync(requireActivity(), DbFields.USERS.KEY, userAuth.getUserId(), retry = true) { docSnapShot,_ ->
                docSnapShot?.let {
                    try {
                        val jsonObj = docSnapShot.get(DbFields.BOOK_INTEREST.KEY)
                        val bookInterest = json.fromAny(jsonObj!!, BookInterest::class.java)
                        bookInterest.uploaded = true
                        viewLifecycleOwner.lifecycleScope.launch(IO) {
                            database.addBookInterest(bookInterest)
                        }
                    } catch (e: Exception) {
                    }

                    try {
                        val userJsonString = docSnapShot.get(DbFields.USER.KEY)
                        val userData = json.fromAny(userJsonString!!, User::class.java)
                        layout.nameEditTxt.setText(userData.name)
                        layout.phoneEditTxt.setText(userData.phone)
                        layout.emailEditTxt.setText(userData.email)
                        userData.uploaded = true
                        viewLifecycleOwner.lifecycleScope.launch(IO) {
                            withContext(Main) {
                                userAuthViewModel.setIsAddingUser(false, userData)
                            }
                        }
                    } catch (e: Exception) {
                    }

                }

            }
        }

        if (userAuth.getAuthType()== AuthType.GOOGLE.ID){
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
            val email = layout.emailEditTxt.text.toString().trim()
            val phone = layout.phoneEditTxt.text.toString().trim()
            val name = layout.nameEditTxt.text.toString().trim()

            if (!email.isValidEmailAddress()){
                layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
            }else if(TextUtils.isEmpty(name)){
                layout.nameEditTxtLayout.error=getString(R.string.empty_name_error)
            }else if(!phone.isPhoneNumber()){
                layout.phoneEditTxtLayout.error=getString(R.string.valid_phone_error)
            }else if (!name.isFullName(Regex.FIRST_NAME_LAST_NAME)){
                layout.nameEditTxtLayout.error=getString(R.string.valid_full_name)
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

                viewLifecycleOwner.lifecycleScope.launch(IO) {
                    val user = User(userId, userAuth.getAuthType())
                    user.appVersion=appUtil.getAppVersionName()
                    user.name = name
                    user.device = DeviceUtil.getDeviceBrandAndModel()
                    user.email = email
                    user.phone = phone
                    //Save the user ID to database
                    user.referrerId = referrerId
                    user.deviceOs = DeviceUtil.getDeviceOSVersionInfo(
                    Build.VERSION.SDK_INT)
                    withContext(Main){
                      userAuthViewModel.setIsAddingUser(false, user)
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

