package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.adapters.search.ShelfSearchResultAdapter
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.view.search.internal.SearchLayout
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.search_view.view.*
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    @Inject
    lateinit var userAuth: UserAuth
    @Inject
    lateinit var localDb: LocalDb
    private lateinit var searchHistoryList:List<ShelfSearchHistory>
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)


        val searchListAdapter = ShelfSearchResultAdapter(requireContext()).getSearchResultAdapter()

        val userId = userAuth.getUserId()

        mainActivityViewModel.getShelfSearchHistory().observe(viewLifecycleOwner, Observer { shelfSearchHistory ->
            searchHistoryList=shelfSearchHistory
            val list = listOf(
                ShelfSearchHistory("1","Hello",userId),
                ShelfSearchHistory("1","Marhaba",userId),
                ShelfSearchHistory("1","Howdy",userId),
                ShelfSearchHistory("1","Hi",userId),
            )
            searchHistoryList=list
        })


        layout.materialSearchView.apply {
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
                        searchListAdapter.submitList(searchHistoryList)
                        SearchLayout.NavigationIconSupport.ARROW
                    } else {
                        searchListAdapter.submitList(null)
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(query: CharSequence): Boolean {
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