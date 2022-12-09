package com.bookshelfhub.feature.home.ui.more

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.feature.home.R
import com.bookshelfhub.core.authentication.AuthType
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.extensions.escapeJSONSpecialChars
import com.bookshelfhub.core.common.extensions.purifyJSONString
import com.bookshelfhub.core.common.extensions.showToast
import com.bookshelfhub.core.common.helpers.KeyboardUtil
import com.bookshelfhub.core.common.helpers.utils.datetime.DateFormat
import com.bookshelfhub.core.common.helpers.utils.datetime.DateUtil
import com.bookshelfhub.feature.home.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class ProfileFragment : Fragment() {

    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var keyboardUtil: KeyboardUtil

    private var gender:String?=null
    private var dateOfBirth:String?=null
    private val profileViewModel: ProfileViewModel by viewModels()
    private var binding: FragmentProfileBinding?=null
    private lateinit var layout: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfileBinding.inflate(inflater, container, false)
        layout = binding!!


        val userAuthType = userAuth.getAuthType()

        /**
         * Load user data of user in the case of device configuration changed or previously saved user data
         * In the database
        */
        profileViewModel.loadUserData()

        profileViewModel.getLiveUser().observe(viewLifecycleOwner) { liveUser ->
            layout.firstNameEditTxt.setText(liveUser!!.firstName)
            layout.lastNameEditTxt.setText(liveUser.lastName)
            if (userAuthType == AuthType.PHONE){
                layout.emailEditTxtLayout.visibility = VISIBLE
            }else{
                layout.phoneEditTxtLayout.visibility = VISIBLE
            }
            layout.phoneEditTxt.setText(liveUser.phone)
            layout.emailEditTxt.setText(liveUser.email)

            liveUser.dateOfBirth?.let {
                dateOfBirth = it
                layout.dobDatePicker.date =
                    DateUtil.stringToDate(it, DateFormat.MM_DD_YYYY.completeFormatValue)
            }
            liveUser.gender?.let {
                gender = it
                layout.genderLayout.hint = it
            }
        }

        layout.dobDatePicker.setOnDatePickListener {
            dateOfBirth = DateUtil.getHumanReadable(it, DateFormat.MM_DD_YYYY.completeFormatValue)
            profileViewModel.getUser()?.dateOfBirth = dateOfBirth
        }

        layout.genderDropDown.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                keyboardUtil.hideKeyboard(layout.phoneEditTxt)
            }
        }

        layout.genderDropDown.setOnItemClickListener{ parent, _, position, _ ->
           gender = parent.getItemAtPosition(position).toString().trim()
           profileViewModel.getUser()?.gender = gender
        }

        layout.btnSave.setOnClickListener {
            layout.phoneEditTxtLayout.error = null
            layout.lastNameEditTxtLayout.error=null
            layout.emailEditTxtLayout.error=null
            layout.emailEditTxtLayout.error = null
            val email = layout.emailEditTxt.text.toString().trim()
            val phone = layout.phoneEditTxt.text.toString().trim()
            val firstName = layout.firstNameEditTxt.text.toString().trim()
            val lastName = layout.firstNameEditTxt.text.toString().trim()
            val additionalInfo = layout.additionalInfoText.text.toString().trim()

            if(TextUtils.isEmpty(firstName)){
                layout.firstNameEditTxtLayout.error = getString(R.string.first_name_req_error)
            }else if(TextUtils.isEmpty(lastName)){
              layout.lastNameEditTxtLayout.error=getString(R.string.last_name_req_error)
            }else if (TextUtils.isEmpty(phone)){
                layout.phoneEditTxtLayout.error = getString(R.string.phone_req_error)
            }else if (TextUtils.isEmpty(email)){
                layout.emailEditTxtLayout.error = getString(R.string.mail_req_error)
            }else {
                profileViewModel.getUser()?.let { updatedUserRecord ->
                    updatedUserRecord.dateOfBirth = dateOfBirth
                    updatedUserRecord.gender = gender
                    updatedUserRecord.firstName = firstName.purifyJSONString()
                    updatedUserRecord.lastName = lastName.purifyJSONString()
                    updatedUserRecord.additionInfo = additionalInfo.escapeJSONSpecialChars()
                    if (userAuthType != AuthType.PHONE) {
                        if (phone != updatedUserRecord.phone) {
                            updatedUserRecord.mailOrPhoneVerified = false
                            updatedUserRecord.phone = phone.purifyJSONString()
                        }
                    } else if (email != updatedUserRecord.email) {
                        updatedUserRecord.mailOrPhoneVerified = false
                        updatedUserRecord.email = email.purifyJSONString()
                    }

                    viewLifecycleOwner.lifecycleScope.launch{
                        updatedUserRecord.uploaded=false
                        profileViewModel.addUser(updatedUserRecord)
                            showToast(getString(R.string.updated))
                            requireActivity().finish()
                    }
                }
            }
        }

        val genderArr = resources.getStringArray(R.array.genders)
        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(requireContext(), com.bookshelfhub.core.resources.R.layout.spinner_item, genderArr)
        layout.genderDropDown.setAdapter(spinnerAdapter)


        layout.firstNameEditTxt.addTextChangedListener {
            it?.let {
                profileViewModel.getUser()?.firstName = it.toString()
            }
        }

        layout.lastNameEditTxt.addTextChangedListener{
            it?.let {
                profileViewModel.getUser()?.lastName = it.toString()
            }
        }

        layout.emailEditTxt.addTextChangedListener{
            it?.let {
                profileViewModel.getUser()?.email = it.toString()
            }
        }

        layout.phoneEditTxt.addTextChangedListener{
            it?.let {
                profileViewModel.getUser()?.phone = it.toString()
            }
        }

        layout.additionalInfoText.addTextChangedListener{
            it?.let {
                profileViewModel.getUser()?.additionInfo = it.toString()
            }
        }

        return layout.root
    }


    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        gender?.let {
            layout.genderLayout.hint = it
        }
    }

}