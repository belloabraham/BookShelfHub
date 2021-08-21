package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.adapters.recycler.OrderedBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.ShelfSearchResultAdapter
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.views.materialsearch.internal.SearchLayout
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.search_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    private var shelfSearchHistoryList:List<ShelfSearchHistory> = emptyList()
    private var orderedBookList:List<OrderedBooks> = emptyList()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val shelfViewModel: ShelfViewModel by viewModels()

    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var cloudDb: ICloudDb

    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)
         val userId = userAuth.getUserId()
         val searchListAdapter = ShelfSearchResultAdapter(requireContext()).getSearchResultAdapter()
         val orderedBooksAdapter = OrderedBooksAdapter(requireActivity()).getOrderedBooksAdapter()

        shelfViewModel.getShelfSearchHistory().observe(viewLifecycleOwner, Observer { shelfSearchHistory ->
            searchListAdapter.submitList(shelfSearchHistory)
            shelfSearchHistoryList=shelfSearchHistory
        })

        shelfViewModel.getOrderedBooks().observe(viewLifecycleOwner, Observer { orderedBooks ->

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
                layout.orderedBooksRecView.visibility = VISIBLE
                layout.progressBar.visibility = GONE
                layout.emptyShelf.visibility = GONE
                layout.appbarLayout.visibility = VISIBLE
                orderedBooksAdapter.submitList(books)
                orderedBookList = books

            }else{
                layout.appbarLayout.visibility = INVISIBLE
                layout.orderedBooksRecView.visibility = GONE
                checkOrderedBooks(userId)
            }
        })


        layout.orderedBooksRecView.layoutManager = GridLayoutManager(requireContext(), 3)
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


        return layout.root
    }

    var orderedBooksChecked = false
    private fun checkOrderedBooks(userId:String){
        if (!orderedBooksChecked){
            layout.progressBar.visibility = VISIBLE
            cloudDb.getLiveListOfDataAsync(
                DbFields.USERS.KEY,
                userId,
                DbFields.SHELF.KEY,
                OrderedBooks::class.java
            ) {
                layout.progressBar.visibility = GONE
                if (it.isEmpty()){
                    layout.emptyShelf.visibility = VISIBLE
                }
                orderedBooksChecked = true
                shelfViewModel.addOrderedBooks(it)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(): ShelfFragment {
            return ShelfFragment()
        }
    }


}