package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private lateinit var layout: FragmentCartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentCartBinding.inflate(inflater, container, false)



        return layout.root
    }


}