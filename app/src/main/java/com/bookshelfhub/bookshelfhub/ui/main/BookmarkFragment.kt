package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bookshelfhub.bookshelfhub.adapters.BookmarkListAdapter
import com.bookshelfhub.bookshelfhub.databinding.FragmentBookmarkBinding

class BookmarkFragment : Fragment() {

    private lateinit var layout: FragmentBookmarkBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentBookmarkBinding.inflate(inflater, container, false)


        val adapter = BookmarkListAdapter()
        layout.bookmarkListRecView.layoutManager = LinearLayoutManager(requireContext())
        layout.bookmarkListRecView.adapter = adapter

        return layout.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): BookmarkFragment {
            return BookmarkFragment()
        }
    }

}