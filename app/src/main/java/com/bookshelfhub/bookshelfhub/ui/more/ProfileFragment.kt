package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.KeyboardUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentProfileBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.enums.Gender
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserRecord
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ProfileFragment : Fragment() {

    @Inject
    lateinit var userAuth: UserAuth
    @Inject
    lateinit var localDb: LocalDb
    @Inject
    lateinit var keyboardUtil: KeyboardUtil
    var gender:String?=null
    private lateinit var layout: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentProfileBinding.inflate(inflater, container, false)

        var userRecord:UserRecord

        lifecycleScope.launch(IO){
            userRecord= localDb.getUser(userAuth.getUserId()).get()
            withContext(Main){
                if (userAuth.getAuthType()==AuthType.GOOGLE.ID){
                    layout.phoneEditTxtLayout.visibility=View.VISIBLE
                    layout.phoneEditTxt.setText(userRecord.email)
                }else{
                    layout.emailEditTxtLayout.visibility=View.VISIBLE
                    layout.emailEditTxt.setText(userRecord.email)
                }
                userRecord.dateOfBirth?.let {
                   // layout.dobDatePicker.date = it
                }
                gender = userRecord.gender
            }
        }

        layout.dobDatePicker.setOnDatePickListener {
            Toast(requireActivity()).showToast(it.toString())
        }

        layout.genderDropDown.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                keyboardUtil.hideKeyboard(layout.phoneEditTxt)
            }
        }

        layout.genderDropDown.setOnItemClickListener{ parent, view, position, id ->
           val value = parent.getItemAtPosition(position)
            Toast(requireActivity()).showToast(value.toString())
        }


        return layout.root
    }

    override fun onResume() {
        super.onResume()
        val genderArr = resources.getStringArray(R.array.genders)
        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(requireContext(), R.layout.spinner_item, genderArr)
        layout.genderDropDown.setAdapter(spinnerAdapter)
        val selection:Int? = when(gender){
            Gender.MALE.Value->0
            Gender.FEMALE.Value->1
          else -> null
      }
        layout.genderDropDown.setSelection(0)
        selection?.let {
            layout.genderDropDown.setSelection(it)
        }
    }

}