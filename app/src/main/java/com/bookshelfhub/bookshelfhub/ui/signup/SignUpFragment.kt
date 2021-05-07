package com.bookshelfhub.bookshelfhub.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.databinding.FragmentSignupBinding

class SignUpFragment:Fragment(){

    private lateinit var layout: FragmentSignupBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentSignupBinding.inflate(inflater, container, false);


        return layout.root
    }

}