package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.bookshelfhub.DummyData
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.adapters.recycler.OrderedBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.ShelfSearchResultAdapter
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.views.materialsearch.internal.SearchLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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

        viewLifecycleOwner.lifecycleScope.launch(IO){
            val orderedBooks = shelfViewModel.getOrderedBooks()
            if (orderedBooks.isEmpty()){
                //Get all available ordered books the user have
                cloudDb.getLiveOrderedBooks(
                    requireActivity(),
                    DbFields.ORDERED_BOOKS.KEY,
                    userId,
                    OrderedBooks::class.java
                ) {
                    if (it.isEmpty()){
                        layout.progressBar.visibility = GONE
                        //Show empty shelf that proves that the have not bought any books
                        layout.emptyShelf.visibility = VISIBLE
                    }else{
                        shelfViewModel.addOrderedBooks(it)
                    }
                }
            }else{
                orderedBooks[0].dateTime?.let { timestamp->
                    cloudDb.getLiveOrderedBooks(
                        requireActivity(),
                        DbFields.ORDERED_BOOKS.KEY,
                        userId,
                        OrderedBooks::class.java,
                        orderBy = DbFields.ORDER_DATE_TIME.KEY,
                        Query.Direction.DESCENDING,
                        startAfter = timestamp
                    ) {
                        shelfViewModel.addOrderedBooks(it)
                    }
                }
            }
        }



        shelfViewModel.getLiveOrderedBooks().observe(viewLifecycleOwner, Observer { orderedBooks ->

            val books = DummyData.getOrderedBooks(userId)

            if (books.isNotEmpty()){
                layout.orderedBooksRecView.visibility = VISIBLE
                layout.emptyShelf.visibility = GONE
                layout.appbarLayout.visibility = VISIBLE
                orderedBooksAdapter.submitList(books)
                orderedBookList = books
            }else{
                layout.progressBar.visibility = VISIBLE
                layout.appbarLayout.visibility = INVISIBLE
                layout.orderedBooksRecView.visibility = GONE
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

                        //remove scroll flags from Appbar layout so that user doesn't scroll search view result on scroll
                        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL

                        /**
                         * Fill the searchList Adapter with the default value after being changed
                         * by @setOnQueryTextListener when user exits search view
                         */
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

    companion object {
        @JvmStatic
        fun newInstance(): ShelfFragment {
            return ShelfFragment()
        }
    }

}