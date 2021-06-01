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
import com.bookshelfhub.bookshelfhub.Utils.StringUtils
import com.bookshelfhub.bookshelfhub.databinding.FragmentUserInfoBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
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

    private val userAuthViewModel: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        layout= FragmentUserInfoBinding.inflate(inflater, container, false);

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        //TODO check if user data exist on the cloud, if so get user data, auto populate UI with it, save data to local db and and set addingUser to false
        //
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

            if (!StringUtils.isValidEmailAddress(email)||email.endsWith(PROTONMAIL)||email.endsWith(TUTANOTAMAIL)){
                layout.emailEditTxtLayout.error=getString(R.string.valid_email_error)
            }else if(TextUtils.isEmpty(name)){
                layout.emailEditTxtLayout.error=getString(R.string.empty_name_error)
            }else if(!StringUtils.isValidPhoneNumber(phone)){
                layout.emailEditTxtLayout.error=getString(R.string.valid_phone_error)
            }else{

                userAuthViewModel.setIsAddingUser(true)
                lifecycleScope.launch(IO) {
                    //TODO Save data to the local db,
                    //Todo start a worker one time request that requires internet connection and that saves data from local db to the cloud margin the data with the existing one
                    withContext(Main){
                      userAuthViewModel.setIsAddingUser(false)
                    }
                }
            }

        }

        return layout.root
    }

}

