package com.bookshelfhub.bookshelfhub.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding
import com.bookshelfhub.bookshelfhub.databinding.FragmentSignupBinding

class LoginFragment:Fragment() {

    private lateinit var layout: FragmentLoginBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentLoginBinding.inflate(inflater, container, false);


        return layout.root
    }

}