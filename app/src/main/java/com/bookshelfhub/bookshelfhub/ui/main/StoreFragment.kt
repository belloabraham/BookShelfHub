package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bookshelfhub.bookshelfhub.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {

    private lateinit var layout: FragmentStoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentStoreBinding.inflate(inflater, container, false)


        return layout.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StoreFragment()
    }
}