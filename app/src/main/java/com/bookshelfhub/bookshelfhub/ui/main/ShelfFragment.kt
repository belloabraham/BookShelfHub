package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.OrderedBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.search.ShelfSearchResultAdapter
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.services.database.cloud.CloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.view.search.internal.SearchLayout
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.search_view.view.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    @Inject
    lateinit var localDb: LocalDb
    private var shelfSearchHistoryList:List<ShelfSearchHistory> = emptyList()
    private var orderedBookList:List<OrderedBooks> = emptyList()
    @Inject
    lateinit var cloudDb: CloudDb

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val recGridColumn = 3
    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)

         val searchListAdapter = ShelfSearchResultAdapter(requireContext()).getSearchResultAdapter()
         val orderedBooksAdapter = OrderedBooksAdapter(requireActivity()).getOrderedBooksAdapter()

        mainActivityViewModel.getShelfSearchHistory().observe(viewLifecycleOwner, Observer { shelfSearchHistory ->
            searchListAdapter.submitList(shelfSearchHistory)
            shelfSearchHistoryList=shelfSearchHistory
        })

        mainActivityViewModel.getOrderedBooks().observe(viewLifecycleOwner, Observer { orderedBooks ->

            val books = listOf(
                OrderedBooks("1","1","1","1","50 Shades of grey",
                    "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    "", "", null),
                OrderedBooks("2","2","2","2","Gifted Hands",
                    "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/QDHKYV8/peace-where-love.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Hello its me",
                    "https://i.ibb.co/YdZMYzW/best-place.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","The Carebean",
                    "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Pirates of the seven seas",
                    "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Shreck",
                    "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/Px19qdd/bookfair3.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    "", "", null),
                OrderedBooks("1","1","1","1","50 Shades of grey",
                    "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    "", "", null),
                OrderedBooks("2","2","2","2","Gifted Hands",
                    "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/QDHKYV8/peace-where-love.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Hello its me",
                    "https://i.ibb.co/YdZMYzW/best-place.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","The Carebean",
                    "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Pirates of the seven seas",
                    "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Shreck",
                    "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/Px19qdd/bookfair3.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    "", "", null),
                OrderedBooks("1","1","1","1","50 Shades of grey",
                    "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    "", "", null),
                OrderedBooks("2","2","2","2","Gifted Hands",
                    "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/QDHKYV8/peace-where-love.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Hello its me",
                    "https://i.ibb.co/YdZMYzW/best-place.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","The Carebean",
                    "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Pirates of the seven seas",
                    "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Shreck",
                    "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/Px19qdd/bookfair3.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    "", "", null),
                OrderedBooks("1","1","1","1","50 Shades of grey",
                    "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    "", "", null),
                OrderedBooks("2","2","2","2","Gifted Hands",
                    "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/QDHKYV8/peace-where-love.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Hello its me",
                    "https://i.ibb.co/YdZMYzW/best-place.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","The Carebean",
                    "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Pirates of the seven seas",
                    "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Shreck",
                    "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    "", "", null),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/Px19qdd/bookfair3.png",
                    "", "", null),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    "", "", null)
            )

            if (books.isNotEmpty()){
                layout.booksSwipeToRefLayout.visibility = View.VISIBLE
                layout.emptyShelf.visibility = View.GONE
                layout.materialSearchView.search_search_edit_text.isEnabled = true
                layout.materialSearchView.search_image_view_navigation.isEnabled = true
                orderedBooksAdapter.submitList(books)
                orderedBookList = books
            }else{
                layout.emptyShelf.visibility = View.VISIBLE
                layout.booksSwipeToRefLayout.visibility = View.INVISIBLE
                layout.materialSearchView.search_search_edit_text.isEnabled = false
                layout.materialSearchView.search_image_view_navigation.isEnabled = false
            }
        })


        layout.orderedBooksRecView.layoutManager = GridLayoutManager(requireContext(), recGridColumn)
        layout.orderedBooksRecView.adapter = orderedBooksAdapter


        layout.materialSearchView.apply {
            val params =  layout.materialSearchView.layoutParams as AppBarLayout.LayoutParams
            setAdapter(searchListAdapter)
            setItemAnimator(null)
            setOnNavigationClickListener(object : SearchLayout.OnNavigationClickListener {
                override fun onNavigationClick(hasFocus: Boolean) {
                    if (hasFocus()) {
                        layout.materialSearchView.clearFocus()
                    } else {
                        layout.materialSearchView.requestFocus()
                    }
                }
            })

            setOnFocusChangeListener(object : SearchLayout.OnFocusChangeListener {
                override fun onFocusChange(hasFocus: Boolean) {
                    layout.materialSearchView.navigationIconSupport = if (hasFocus) {
                        params.scrollFlags=0
                        SearchLayout.NavigationIconSupport.ARROW
                    } else {
                        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        searchListAdapter.submitList(shelfSearchHistoryList)
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                    layout.materialSearchView.layoutParams = params
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                    val result = orderedBookList.filter {
                        it.title.contains(newText, true)
                    }
                    searchListAdapter.submitList(result)
                    return true
                }

                override fun onQueryTextSubmit(query: CharSequence): Boolean {
                    //TODO Come back to implement this
                    return true
                }
            })
        }

        mainActivityViewModel.getOnBackPressed().observe(viewLifecycleOwner, Observer { isBackPressed ->
            if (layout.materialSearchView.hasFocus()){
                layout.materialSearchView.clearFocus()
            }else if (isBackPressed){
                activity?.finish()
            }
        })

        layout.gotoStoreBtn.setOnClickListener {
            mainActivityViewModel.setSelectedIndex(1)
        }


        layout.booksSwipeToRefLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(),R.color.purple_700),
            ContextCompat.getColor(requireContext(),R.color.orange),
            ContextCompat.getColor(requireContext(),R.color.light_blue_A400)
        )

        layout.booksSwipeToRefLayout.setOnRefreshListener {
            orderedBooksAdapter.submitList(emptyList())
            orderedBooksAdapter.submitList(mainActivityViewModel.getOrderedBooks().value)
            layout.booksSwipeToRefLayout.isRefreshing = false
        }

        return layout.root
    }



    companion object {
        @JvmStatic
        fun newInstance(): ShelfFragment {
            return ShelfFragment()
        }
    }


}