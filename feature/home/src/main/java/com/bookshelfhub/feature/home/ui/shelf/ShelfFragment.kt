package com.bookshelfhub.feature.home.ui.shelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.model.ISearchResult
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.bookshelfhub.feature.home.MainActivityViewModel
import com.bookshelfhub.feature.home.adapters.recycler.OrderedBooksAdapter
import com.bookshelfhub.feature.home.adapters.recycler.ShelfSearchResultAdapter
import com.bookshelfhub.feature.home.databinding.FragmentShelfBinding
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.core.ui.views.materialsearch.internal.SearchLayout


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    private var orderedBookList:List<OrderedBookUiState> = emptyList()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val shelfViewModel: ShelfViewModel by viewModels()
    private var binding: FragmentShelfBinding?=null
    private var mOrderedBooksAdapter: ListAdapter<OrderedBookUiState, RecyclerViewHolder<OrderedBookUiState>>?=null
    private var mSearchListAdapter: ListAdapter<ISearchResult, RecyclerViewHolder<ISearchResult>>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShelfBinding.inflate(inflater, container, false)
        val layout = binding!!

        mSearchListAdapter = ShelfSearchResultAdapter(requireActivity()).getSearchResultAdapter()
        mOrderedBooksAdapter = OrderedBooksAdapter(
            requireActivity(),
            shelfViewModel,
            viewLifecycleOwner)
            .getOrderedBooksAdapter()

        val searchListAdapter = mSearchListAdapter!!
        val orderedBooksAdapter = mOrderedBooksAdapter!!

        layout.orderedBooksRecView.layoutManager = GridLayoutManager(requireContext(), 3)
        layout.orderedBooksRecView.adapter = orderedBooksAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            shelfViewModel.getLiveListOfOrderedBooksUiState().asFlow()
                .collectLatest { orderedBooksUiStates ->
                    //Hide just in case data get load by user swipe to refresh
                    layout.swipeRefreshLayout.isRefreshing = false
                    //Hide as this is visible by default
                    layout.progressBar.visibility = GONE
                    if (orderedBooksUiStates.isNotEmpty()) {
                        //Notify the UI that there is no book on the cloud that need to be sync down to the device
                        shelfViewModel.updateBookPurchaseState(isNewlyPurchased = false)
                        layout.orderedBooksRecView.visibility = VISIBLE
                        layout.emptyShelf.visibility = GONE
                        layout.appbarLayout.visibility = VISIBLE
                        orderedBooksAdapter.submitList(orderedBooksUiStates)
                        orderedBookList = orderedBooksUiStates
                    } else {
                        layout.emptyShelf.visibility = VISIBLE
                        layout.appbarLayout.visibility = INVISIBLE
                        layout.orderedBooksRecView.visibility = GONE
                    }
            }
        }

        layout.swipeRefreshLayout.setColorSchemeResources(
            R.color.light_blue,
            R.color.orange,
            R.color.purple_700)

        layout.swipeRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    shelfViewModel.loadRemoteOrderedBooks()
                }catch (e:Exception){
                    ErrorUtil.e(e)
                }finally {
                    layout.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            shelfViewModel.doesUserHaveUnDownloadedPurchasedBooks().collect{ ifUserHaveUnDownloadedPurchasedBooks->
                layout.newlyPurchasedBooksMsgTxt.isVisible = ifUserHaveUnDownloadedPurchasedBooks
            }
        }


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

                        // Fill the searchList Adapter with the default value after being changed
                        // by @setOnQueryTextListener when user exits search view

                        viewLifecycleOwner.lifecycleScope.launch {
                            val searchHistory = shelfViewModel.getTop4ShelfSearchHistory()
                            searchListAdapter.submitList(searchHistory)
                        }

                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                    layout.materialSearchView.layoutParams = params
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                    val result = orderedBookList.filter {
                        it.name.contains(newText, true)
                    }
                    searchListAdapter.submitList(result)
                    return true
                }

                override fun onQueryTextSubmit(query: CharSequence): Boolean {
                    return true
                }
            })
        }

        //Only shelf fragment can listen to onBackPressed
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

    override fun onResume() {
        //Check if more remote ordered books in the case of user just purchasing a book or books
        shelfViewModel.getRemoteOrderedBooksRepeatedly()
        shelfViewModel.checkIfUserHaveUnDownloadedPurchasedBook()
        super.onResume()
    }

    override fun onDestroyView() {
        binding=null
        mSearchListAdapter = null
        mOrderedBooksAdapter=null
        super.onDestroyView()
    }

}