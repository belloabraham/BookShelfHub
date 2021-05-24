package com.bookshelfhub.bookshelfhub.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import javax.inject.Inject

class UserInfoFragment : Fragment() {

    @Inject
    lateinit var userAuth: UserAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_home, container, false)

        /*  //TODO Check for user data on the cloud (firestore) using welcomeviewmodel and userID, listen for user value changed
                         //Todo if there is user data, populate the UI with data and navigate to main activity straight
                      val intent = Intent(this, MainActivity::class.java)
                         finish()
                         startActivity(intent)*/

        //TODO if there is no user data, collect the data and submit to the cloud, on submit successfully show sign up successfully popup

/*        if (userAuth.getEmail()!=null){
            //TODO Gmail auth, set phone edit text value and disable the edit text
        }else{
            //TODO Phone auth, set email edit text value and disable the edit text
        }*/

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }



        return layout
    }

}

