package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding

class ShelfFragment : Fragment() {

    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)


        return layout.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ShelfFragment()
    }
}