package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.recycler.BookmarkListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.FragmentBookmarkBinding
import com.bookshelfhub.bookshelfhub.domain.models.entities.Bookmark
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


@AndroidEntryPoint
@WithFragmentBindings
class BookmarkFragment : Fragment() {

    private var binding: FragmentBookmarkBinding?=null
    private val bookmarkViewModel:BookmarkViewModel by viewModels()
    private var mAdapter:ListAdapter<Bookmark, RecyclerViewHolder<Bookmark>>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentBookmarkBinding.inflate(inflater, container, false)
        val layout = binding!!

         mAdapter = BookmarkListAdapter(requireContext()).getBookmarkListAdapter{
            //Show users a hint on how to remove an added bookmark
            showRemoveBookmarkHint(layout)
        }

        val adapter = mAdapter!!


        layout.bookmarkListRecView.adapter = adapter

        var bookmarkArrayList: ArrayList<Bookmark> =  ArrayList()

        bookmarkViewModel.getLiveBookmarks().observe(viewLifecycleOwner, Observer { bookmarkList ->

            if (bookmarkArrayList.isEmpty()){
                bookmarkArrayList = bookmarkList as ArrayList<Bookmark>
                adapter.submitList(bookmarkArrayList)
            }

            if (bookmarkList.isEmpty()){
                layout.emptyBookmarksLayout.visibility = View.VISIBLE
                layout.bookmarkListRecView.visibility = View.GONE
            }else{
                layout.emptyBookmarksLayout.visibility = View.GONE
                layout.bookmarkListRecView.visibility = View.VISIBLE
            }
        })

        layout.bookmarkListRecView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )


        val swipeToDeleteCallback  = object : SwipeToDeleteCallBack(requireContext(), R.color.errorColor, R.drawable.ic_bookmark_minus_white) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position: Int = viewHolder.bindingAdapterPosition
                val bookmark: Bookmark = adapter.currentList[position]
                bookmark.deleted=true
                bookmarkArrayList.removeAt(position)
                adapter.notifyItemRemoved(position)
                    bookmarkViewModel.addBookmark(bookmark)
                        bookmark.deleted = false
                        val snackBar = Snackbar.make(layout.rootCoordinateLayout, R.string.bookmark_removed_msg, Snackbar.LENGTH_LONG)
                        snackBar.setAction(R.string.undo) {
                            bookmarkArrayList.add(position, bookmark)
                            adapter.notifyItemInserted(position)

                                bookmarkViewModel.addBookmark(bookmark)

                        }.show()

            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(layout.bookmarkListRecView)

        return layout.root
    }

    override fun onDestroyView() {
        binding=null
        mAdapter=null
        super.onDestroyView()
    }

    private fun showRemoveBookmarkHint(layout:FragmentBookmarkBinding):Boolean{
        Snackbar.make(layout.rootCoordinateLayout, R.string.remove_bookmark_msg, Snackbar.LENGTH_LONG)
            .show()
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance(): BookmarkFragment {
            return BookmarkFragment()
        }
    }

}