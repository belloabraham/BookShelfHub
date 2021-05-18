package com.bookshelfhub.bookshelfhub.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bookshelfhub.bookshelfhub.databinding.FragmentLoginBinding

class LoginFragment:Fragment() {

    private lateinit var layout: FragmentLoginBinding;
    private val args:LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentLoginBinding.inflate(inflater, container, false);
        layout.description.setText(args.description)
        return layout.root
    }

}