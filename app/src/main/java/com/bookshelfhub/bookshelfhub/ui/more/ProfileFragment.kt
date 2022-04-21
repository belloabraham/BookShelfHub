package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentProfileBinding
import com.bookshelfhub.bookshelfhub.helpers.authentication.AuthType
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateFormat
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.User
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
    private val profileViewModel:ProfileViewModel by viewModels()
    private var binding: FragmentProfileBinding?=null
    private lateinit var layout: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfileBinding.inflate(inflater, container, false)
        layout = binding!!

        var user: User? = null
        val userAuthType = userAuth.getAuthType()

        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { liveUser ->
            user = liveUser
            layout.firstNameEditTxt.setText(liveUser!!.firstName)
            layout.lastNameEditTxt.setText(liveUser.lastName)
            if (userAuthType == AuthType.GOOGLE.ID){
                layout.phoneEditTxtLayout.visibility=View.VISIBLE
            }else{
                layout.emailEditTxtLayout.visibility=VISIBLE
            }
            layout.phoneEditTxt.setText(liveUser.phone)
            layout.emailEditTxt.setText(liveUser.email)

            liveUser.dateOfBirth?.let {
                dateOfBirth=it
                layout.dobDatePicker.date = DateUtil.stringToDate(it, DateFormat.MM_DD_YYYY.completeFormatValue )
            }
            liveUser.gender?.let {
                gender =it
                layout.genderLayout.hint = it
            }

        })

        layout.dobDatePicker.setOnDatePickListener {
            dateOfBirth = DateUtil.getHumanReaddable(it, DateFormat.MM_DD_YYYY.completeFormatValue)
        }

        layout.genderDropDown.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                keyboardUtil.hideKeyboard(layout.phoneEditTxt)
            }
        }

        layout.genderDropDown.setOnItemClickListener{ parent, _, position, _ ->
           gender = parent.getItemAtPosition(position).toString().trim()
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
                user?.let { updatedUserRecord ->
                    updatedUserRecord.dateOfBirth = dateOfBirth
                    updatedUserRecord.gender = gender
                    updatedUserRecord.firstName = firstName
                    updatedUserRecord.lastName = lastName
                    updatedUserRecord.additionInfo = additionalInfo
                    if (userAuthType == AuthType.GOOGLE.ID) {
                        if (phone != updatedUserRecord.phone) {
                            updatedUserRecord.mailOrPhoneVerified = false
                            updatedUserRecord.phone = phone
                        }
                    } else if (email != updatedUserRecord.email) {
                        updatedUserRecord.mailOrPhoneVerified = false
                        updatedUserRecord.email = email
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

        return layout.root
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        val genderArr = resources.getStringArray(R.array.genders)
        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_item, genderArr)
        layout.genderDropDown.setAdapter(spinnerAdapter)
        gender?.let {
            layout.genderLayout.hint = it
        }
    }

}