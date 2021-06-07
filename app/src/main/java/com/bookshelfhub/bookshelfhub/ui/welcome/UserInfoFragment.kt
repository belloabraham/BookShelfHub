package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.AppUtil
import com.bookshelfhub.bookshelfhub.Utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentUserInfoBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.enums.DbCollections
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.google.firebase.auth.ktx.userProfileChangeRequest
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
    private val PROTONMAIL = "@protonmail.com"
    private val TUTANOTAMAIL = "@tutanota.com"
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
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout= FragmentUserInfoBinding.inflate(inflater, container, false);

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        cloudDb.getDataAsync(DbCollections.USERS.KEY, userAuth.getUserId(), User::class.java){
            it?.let {
                val userData = it as User
                layout.nameEditTxt.setText(userData.name)
                layout.phoneEditTxt.setText(userData.phone)
                layout.emailEditTxt.setText(userData.email)
                lifecycleScope.launch(IO) {
                    val user = User(userAuth.getUserId(),userData.name, userData.email,userData.phone, userData.photoUri, userAuth.getAuthType(),appUtil.getAppVersionName(), deviceUtil.getDeviceBrandAndModel())
                    database.addUser(user)
                    withContext(Main){
                        userAuthViewModel.setIsAddingUser(false)
                    }
                }
            }
        }

        if (userAuth.getAuthType()==AuthType.GOOGLE.ID){
            layout.phoneEditTxtLayout.visibility=View.VISIBLE
            layout.nameEditTxt.setText(userAuth.getName())
        }else{
            layout.emailEditTxtLayout.visibility=View.VISIBLE
        }

        layout.btnContinue.setOnClickListener {
            layout.nameEditTxtLayout.error=null
            layout.emailEditTxtLayout.error=null
            layout.phoneEditTxtLayout.error=null
            val email = layout.emailEditTxt.text.toString()
            val phone = layout.phoneEditTxt.text.toString()
            val name = layout.nameEditTxt.text.toString()

            if (!stringUtil.isValidEmailAddress(email)||email.endsWith(PROTONMAIL)||email.endsWith(TUTANOTAMAIL)){
                layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
            }else if(TextUtils.isEmpty(name)){
                layout.emailEditTxtLayout.error=getString(R.string.empty_name_error)
            }else if(!stringUtil.isValidPhoneNumber(phone)){
                layout.emailEditTxtLayout.error=getString(R.string.valid_phone_error)
            }else{

                userAuthViewModel.setIsAddingUser(true)
                lifecycleScope.launch(IO) {
                    val user = User(userAuth.getUserId(),name, email,phone, null, userAuth.getAuthType(),appUtil.getAppVersionName(), deviceUtil.getDeviceBrandAndModel())
                    database.addUser(user)
                    withContext(Main){
                      userAuthViewModel.setIsAddingUser(false)
                    }
                }
            }

        }

        return layout.root
    }

}
