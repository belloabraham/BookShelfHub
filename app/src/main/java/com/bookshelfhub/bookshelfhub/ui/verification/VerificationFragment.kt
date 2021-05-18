package com.bookshelfhub.bookshelfhub.ui.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.databinding.FragmentVerificationBinding

class VerificationFragment:Fragment(){

    private lateinit var layout: FragmentVerificationBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentVerificationBinding.inflate(inflater, container, false);


        return layout.root
    }

}