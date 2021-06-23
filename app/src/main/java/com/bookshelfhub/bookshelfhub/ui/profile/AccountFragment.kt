package com.bookshelfhub.bookshelfhub.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bookshelfhub.bookshelfhub.databinding.FragmentAboutBinding
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding

class AccountFragment : Fragment() {

    private lateinit var layout: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentAboutBinding.inflate(inflater, container, false)


        return layout.root
    }


}