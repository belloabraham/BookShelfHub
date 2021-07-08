package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.Utils.LocalDateTimeUtil
import com.bookshelfhub.bookshelfhub.adapters.search.OrderedBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.search.ShelfSearchResultAdapter
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.models.ISearchResult
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchResult
import com.bookshelfhub.bookshelfhub.view.search.internal.SearchLayout
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.search_view.view.*
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    //@Inject
   // lateinit var userAuth: UserAuth
    @Inject
    lateinit var localDb: LocalDb
    private var shelfSearchHistoryList:List<ShelfSearchResult> = emptyList()
    private var orderedBookList:List<OrderedBooks> = emptyList()
    private var searchList:List<ISearchResult> = emptyList()

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val recGridColumn = 3

    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)

        val searchListAdapter = ShelfSearchResultAdapter(requireContext()).getSearchResultAdapter()
        val orderedBooksAdapter = OrderedBooksAdapter(requireContext()).getOrderedBooksAdapter()

        mainActivityViewModel.getShelfSearchHistory().observe(viewLifecycleOwner, Observer { shelfSearchHistory ->
            searchListAdapter.submitList(shelfSearchHistory)
            shelfSearchHistoryList=shelfSearchHistory
        })

        mainActivityViewModel.getOrderedBooks().observe(viewLifecycleOwner, Observer { orderedBooks ->

            val books = listOf(
                OrderedBooks("1","1","1","1","50 Shades of grey",
                "https://i.ibb.co/yyXyxWk/keep-your-focus.jpg",
                ""),
                OrderedBooks("2","2","2","2","Gifted Hands",
                    "https://i.ibb.co/s1JbH68/nutricious-food.jpg",
                    ""),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/5kGGcbq/ic-place-holder.png",
                    ""),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/ZTvCTYD/true-wealth.jpg",
                    ""),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/QvHtgtS/dress-in-holiness.jpg",
                    ""),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/vYdcwLt/prioratise-the-word.jpg",
                    ""),
                OrderedBooks("3","3","3","3","Tunde on the run",
                    "https://i.ibb.co/5kGGcbq/ic-place-holder.png",
                    ""),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/ZTvCTYD/true-wealth.jpg",
                    ""),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/QvHtgtS/dress-in-holiness.jpg",
                    ""),
                OrderedBooks("4","4","4","4","Prince of Persia",
                    "https://i.ibb.co/vYdcwLt/prioratise-the-word.jpg",
                    "")
            )

            if (books.isNotEmpty()){
                layout.orderedBooksRecView.visibility = View.VISIBLE
                layout.emptyShelf.visibility = View.GONE
                layout.materialSearchView.search_search_edit_text.isEnabled = true
                layout.materialSearchView.search_image_view_navigation.isEnabled = true
                orderedBooksAdapter.submitList(books)
                orderedBookList = books
            }else{
                layout.emptyShelf.visibility = View.VISIBLE
                layout.orderedBooksRecView.visibility = View.GONE
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
                        searchList = shelfSearchHistoryList.plus(orderedBookList)
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
                    val result = searchList.filter {
                        it.title.contains(newText,true)
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


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            if (layout.materialSearchView.hasFocus()){
                layout.materialSearchView.clearFocus()
            }else{
               activity?.finish()
            }
        }

        layout.gotoStoreBtn.setOnClickListener {
            mainActivityViewModel.setSelectedIndex(1)
        }

        return layout.root
    }

}