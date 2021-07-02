package com.bookshelfhub.bookshelfhub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.search.SearchViewHolder
import com.bookshelfhub.bookshelfhub.adapters.search.local.SearchResultViewHolder
import com.bookshelfhub.bookshelfhub.databinding.FragmentShelfBinding
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.CloudSearchHistory
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.LocalSearchHistory
import com.bookshelfhub.bookshelfhub.view.search.internal.SearchLayout
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.search_view.view.*
import me.ibrahimyilmaz.kiel.adapterOf
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ShelfFragment : Fragment() {
    @Inject
    lateinit var userAuth: UserAuth
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentShelfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentShelfBinding.inflate(inflater, container, false)


        val userId = userAuth.getUserId()


        val recyclerViewAdapter = adapterOf<Any> {

            register(
                layoutResource = R.layout.book_history_search_item,
                viewHolder = ::SearchViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                }
            )

            register(
                layoutResource = R.layout.book_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.title
                }
            )

        }


       val hist = listOf(
            LocalSearchHistory("A","Hello of persia",  userId),
            LocalSearchHistory("BCS","Ji of persia", userId),
            LocalSearchHistory("DEDF","HLL of persia 1", userId),
            LocalSearchHistory("CDFFF","jj of persia", userId),
            LocalSearchHistory("FGFFFF","LL of persia", userId),
            LocalSearchHistory("HGFFFFF","llll of persia 1", userId),
            LocalSearchHistory("UJFFFFFF","Lord of persia", userId),
            LocalSearchHistory("FASFFFFFF","FH of persia", userId),
            LocalSearchHistory("ZZFFFFFFFFFF","iui of persia", userId)
        )

        val combineList =  hist.plus(listOf(
            CloudSearchHistory("33444333FFF","Prince of persia", userId ),
            CloudSearchHistory("3344F433FFFFF","Prince of persia", userId ),
            CloudSearchHistory("3344338881","Prince of persia 1", userId ),
            CloudSearchHistory("0GGGGGSSDERRTT","Prince of persia", userId ),
            CloudSearchHistory("444ASDRTHUIO333","Prince of persia 1", userId ),
            CloudSearchHistory("AHKLLPOIUSGFWERT","Prince of persia", userId ),
            CloudSearchHistory("HUJNMKOPLGTHSDER","Prince of persia", userId ),
            CloudSearchHistory("YUHNBGFTYUISPUTGF","Prince of persia", userId),
            CloudSearchHistory("UHNGBFTSGOIPLOKHNGBF","Prince of persia", userId)
        ))


        /*layout.recView.apply {
            layoutManager= LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }*/


       recyclerViewAdapter.submitList(hist)
       // recyclerViewAdapter.notifyDataSetChanged()


        Toast(requireActivity()).showToast("${combineList.size}")

        layout.materialSearchView.apply {

            setAdapter(recyclerViewAdapter)
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
                        SearchLayout.NavigationIconSupport.ARROW
                    } else {
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                   // adapter.filter(newText)
                    //Toast(requireActivity()).showToast(newText.toString())
                    val filter = combineList.filter {
                        it.title.contains(newText.toString(), true)
                    }
                    recyclerViewAdapter.submitList(filter)
                   // recyclerViewAdapter.notifyDataSetChanged()
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