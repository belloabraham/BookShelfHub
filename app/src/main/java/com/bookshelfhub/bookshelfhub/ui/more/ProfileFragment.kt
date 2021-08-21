package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.DateUtil
import com.bookshelfhub.bookshelfhub.Utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentProfileBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.enums.DateFormat
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.User
import com.bookshelfhub.bookshelfhub.views.Toast
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class ProfileFragment : Fragment() {

    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    private var gender:String?=null
    private var dateOfBirth:String?=null
    private val profileViewModel:ProfileViewModel by viewModels()
    private lateinit var layout: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentProfileBinding.inflate(inflater, container, false)

        var user:User? = null

        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { liveUser ->
            user = liveUser
            layout.nameEditTxt.setText(liveUser!!.name)
            if (userAuth.getAuthType()==AuthType.GOOGLE.ID){
                layout.phoneEditTxtLayout.visibility=View.VISIBLE
            }else{
                layout.emailEditTxtLayout.visibility=View.VISIBLE
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
            dateOfBirth = DateUtil.dateToString(it, DateFormat.MM_DD_YYYY.completeFormatValue)
        }

        layout.genderDropDown.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                keyboardUtil.hideKeyboard(layout.phoneEditTxt)
            }
        }

        layout.genderDropDown.setOnItemClickListener{ parent, view, position, id ->
           gender = parent.getItemAtPosition(position).toString().trim()
        }

        layout.btnSave.setOnClickListener {
            layout.phoneEditTxtLayout.error = null
            layout.nameEditTxtLayout.error = null
            layout.emailEditTxtLayout.error = null
            val email = layout.emailEditTxt.text.toString().trim()
            val phone = layout.phoneEditTxt.text.toString().trim()
            val name = layout.nameEditTxt.text.toString().trim()
            if(TextUtils.isEmpty(name)){
                layout.nameEditTxtLayout.error = getString(R.string.name_req_error)
            }else if (TextUtils.isEmpty(phone)){
                layout.phoneEditTxtLayout.error = getString(R.string.phone_req_error)
            }else if (TextUtils.isEmpty(email)){
                layout.emailEditTxtLayout.error = getString(R.string.mail_req_error)
            }else {
                user?.let { updatedUserRecord ->
                    updatedUserRecord.dateOfBirth = dateOfBirth
                    updatedUserRecord.gender = gender
                    updatedUserRecord.name = name
                    if (userAuth.getAuthType() == AuthType.GOOGLE.ID) {
                        if (phone != updatedUserRecord.phone) {
                            updatedUserRecord.mailOrPhoneVerified = false
                            updatedUserRecord.phone = phone
                        }
                    } else if (email != updatedUserRecord.email) {
                        updatedUserRecord.mailOrPhoneVerified = false
                        updatedUserRecord.email = email
                    }
                    lifecycleScope.launch(IO) {
                        updatedUserRecord.uploaded=false
                        database.addUser(updatedUserRecord)
                        withContext(Main){
                            showToast(getString(R.string.updated))
                            requireActivity().finish()
                        }
                    }
                }
            }
        }

        return layout.root
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