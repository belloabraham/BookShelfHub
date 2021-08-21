package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.recycler.BookmarkListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.FragmentBookmarkBinding
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmark
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class BookmarkFragment : Fragment() {

    private lateinit var layout: FragmentBookmarkBinding
    private val bookmarkViewModel:BookmarkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentBookmarkBinding.inflate(inflater, container, false)


        val adapter = BookmarkListAdapter(requireContext()).getBookmarkListAdapter(){
            removeBookmarkHint()
        }

        layout.bookmarkListRecView.adapter = adapter

        var bookmarkList: ArrayList<Bookmark> =  ArrayList()

        bookmarkViewModel.getLiveBookmarks().observe(viewLifecycleOwner, Observer { bookmarks ->

            if (bookmarkList.isEmpty()){
                bookmarkList = bookmarks as ArrayList<Bookmark>
                adapter.submitList(bookmarkList)
            }

            if (bookmarks.isEmpty()){
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
                bookmarkList.removeAt(position)
                adapter.notifyItemRemoved(position)
                    bookmarkViewModel.addBookmark(bookmark)
                        bookmark.deleted = false
                        val snackBar = Snackbar.make(layout.rootCoordinateLayout, R.string.bookmark_removed_msg, Snackbar.LENGTH_LONG)
                        snackBar.setAction(R.string.undo) {
                            bookmarkList.add(position, bookmark)
                            adapter.notifyItemInserted(position)

                                bookmarkViewModel.addBookmark(bookmark)

                        }.show()

            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(layout.bookmarkListRecView)

        return layout.root
    }


    private fun removeBookmarkHint():Boolean{
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