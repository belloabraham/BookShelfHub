package com.bookshelfhub.bookshelfhub.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bookshelfhub.bookshelfhub.*
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.IconUtil
import com.bookshelfhub.bookshelfhub.adapters.recycler.StoreSearchResultAdapter
import com.bookshelfhub.bookshelfhub.adapters.paging.*
import com.bookshelfhub.bookshelfhub.databinding.FragmentStoreBinding
import com.bookshelfhub.bookshelfhub.book.Category
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.views.materialsearch.internal.SearchLayout
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class StoreFragment : Fragment() {

    private lateinit var layout: FragmentStoreBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by viewModels()
    private var allBooksLive = emptyList<PublishedBook>()
    private var storeSearchHistory = emptyList<StoreSearchHistory>()
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var connectionUtil: ConnectionUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentStoreBinding.inflate(inflater, container, false)

        storeViewModel.getLiveTotalCartItemsNo().observe(viewLifecycleOwner, Observer { cartItemsCount ->
            layout.materialSearchView.setMenuNotifCount(cartItemsCount)
        })

        storeViewModel.getIsNoConnection().observe(viewLifecycleOwner, Observer { isNoConnection ->
           if (isNoConnection){
               showErrorMsg(R.string.no_connection_err_msg)
           }
        })

        storeViewModel.getIsNetworkError().observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError){
                showErrorMsg(R.string.bad_connection_err_msg)
            }
        })

        val searchListAdapter = StoreSearchResultAdapter(requireContext()).getSearchResultAdapter()

        val recommendBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val trendingBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val scienceAndTechBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val comicBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val religionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val artAndCraftBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val lawBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val historyBooksAdapter =StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val howToBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val languageAndRefBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val newsBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val loveAndPoetryBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val politicsBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val sportBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val businessBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val cooksBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val educationBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val travelBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val fictionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val entertainmentBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())

        setRecyclerViewLayoutManager()

        layout.recommendedRecView.adapter = recommendBooksAdapter
        layout.trendingRecView.adapter=trendingBooksAdapter
        layout.sciAndTechRecView.adapter=scienceAndTechBooksAdapter
        layout.comicRecView.adapter=comicBooksAdapter
        layout.religionRecView.adapter=religionBooksAdapter
        layout.artAndCraftRecView.adapter=artAndCraftBooksAdapter
        layout.lawRecView.adapter=lawBooksAdapter
        layout.historyRecView.adapter=historyBooksAdapter
        layout.howToRecView.adapter=howToBooksAdapter
        layout.cookBooksRecView.adapter=cooksBooksAdapter
        layout.businessRecView.adapter=businessBooksAdapter
        layout.sportRecView.adapter=sportBooksAdapter
        layout.politicsRecView.adapter=politicsBooksAdapter
        layout.loveAndPoetryRecView.adapter=loveAndPoetryBooksAdapter
        layout.newsRecView.adapter = newsBooksAdapter
        layout.langAndRefRecView.adapter=languageAndRefBooksAdapter


        val bookReqMsg = getString(R.string.cant_find_book)

        mainActivityViewModel.getStoreSearchHistory().observe(viewLifecycleOwner, Observer { searchHistory ->
            searchListAdapter.submitList(searchHistory)
            storeSearchHistory = searchHistory
        })


        layout.materialSearchView.apply {
            val params =  layout.materialSearchView.layoutParams as AppBarLayout.LayoutParams
            setItemAnimator(null)
            setAdapter(searchListAdapter)
            setOnNavigationClickListener(object : SearchLayout.OnNavigationClickListener {
                override fun onNavigationClick(hasFocus: Boolean) {
                    if (hasFocus()) {
                        layout.materialSearchView.clearFocus()
                    } else {
                        layout.materialSearchView.requestFocus()
                    }
                }
            })

            setMenuIconImageResource(R.drawable.ic_cart)
            setMenuIconVisibility(View.VISIBLE)
            setOnMenuClickListener(object:SearchLayout.OnMenuClickListener{
                override fun onMenuClick() {
                    val intent = Intent(activity, CartActivity::class.java)
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
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
                        searchListAdapter.submitList(storeSearchHistory)
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                    layout.materialSearchView.layoutParams = params
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                    val result = allBooksLive.filter {
                        it.name.contains(newText, true)||it.author.contains(newText, true)
                    }
                   searchListAdapter.submitList(result.plus(BookRequest(bookReqMsg)))
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
               mainActivityViewModel.setOnBackPressed(true)
            }
        }

        layout.retryBtn.setOnClickListener {
            layout.errorLayout.visibility = GONE
            layout.loadingAnimView.visibility = VISIBLE
           //TODO  storeViewModel.loadPublishedBooks()
        }

        storeViewModel.getAllPublishedBooks().observe(viewLifecycleOwner, Observer { _ ->


            val allBooks = listOf(
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("2", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("3", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("4", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("5", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("6", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("7", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("8", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("9", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = "", totalDownloads=10),
                PublishedBook("11", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("12",  name="A Quite place", totalDownloads=20, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("13", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("14", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("15", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("16", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("17", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("18", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("19", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("20", name="A Quite place",totalDownloads=30, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("21", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("22", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("23", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("100", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("101", name="A Quite place", totalDownloads=35, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("102", name="A Quite place", totalDownloads=135, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("103", name="A Quite place",totalDownloads=40, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("104", name="A Quite place",totalDownloads=45, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("105", name="A Quite place", totalDownloads=100, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("106", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("107", name="A Quite place",totalDownloads=25, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("108", name="A Quite place", totalDownloads=1, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("109", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("110", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("1111", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("112", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("113", name="A Quite place",totalDownloads=102, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("114", name="A Quite place",totalDownloads=200, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("115", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("116", name="A Quite place",totalDownloads=1000, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("117", name="A Quite place", totalDownloads=200, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("118", name="A Quite place",totalDownloads=105, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("119", name="A Quite place",totalDownloads=300, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("120", name="A Quite place",totalDownloads=405, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("121", name="A Quite place", totalDownloads=3000, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("122", name="A Quite place",totalDownloads=276, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("123", name="A Quite place", totalDownloads=50, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("124", name="A Quite place", totalDownloads=555, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("125", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("126", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("131", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("132", name="A Quite place",totalDownloads=110, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("133", name="A Quite place", totalDownloads=600, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("134", name="A Quite place",totalDownloads=107, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("135", name="A Quite place", totalDownloads=378, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("136", name="A Quite place", totalDownloads=673, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("127", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("128", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("128", name="A Quite place",totalDownloads=984, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("130", name="A Quite place", totalDownloads=352, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("131", name="A Quite place", totalDownloads=123, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("132", name="A Quite place", totalDownloads=134, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("133", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("134", name="A Quite place",totalDownloads=783, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("135", name="A Quite place",totalDownloads =268, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("136", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("137", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("138", name="A Quite place",totalDownloads=764, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("139", name="A Quite place",totalDownloads=267, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("140", name="A Quite place",totalDownloads=243, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=674, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", totalDownloads=676, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1kh", name="A Quite place", totalDownloads=2324, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1klj", name="A Quite place",totalDownloads=456, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1.jm.", name="A Quite place",totalDownloads=567, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", totalDownloads=678, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("100i", name="A Quite place",totalDownloads=725, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("hj", name="A Quite place", totalDownloads=4353, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1jkh", name="A Quite place",totalDownloads=3443, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1jh", name="A Quite place",totalDownloads=678, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1khkjl", name="A Quite place",totalDownloads=767, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=2232, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=2234, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=3424, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=3243, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=234, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=43454, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=3243, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=3243, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=434, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=6768, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=65465, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=34234, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=333, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=2123, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=123, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=3345, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=788, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=678, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=987, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=104567, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=12324, coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place",totalDownloads=2343, coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Bqv7XLw/bookfair1.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/k0J2mt0/lazy-sunday.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Dfn7jGQ/my-cosy-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Px19qdd/bookfair3.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/ZWbKSZR/bookfair4.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/Rb40dwX/home-sweet-home.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/D1mYc3s/greate-weekend.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Love and Poetry", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Religion", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Science and Technology", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Art and Craft", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Politics", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Politics", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Politics", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Politics", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Art and Craft", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Art and Craft", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Art and Craft", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Art and Craft", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "How-to and Manuals", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "How-to and Manuals", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "How-to and Manuals", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "News", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "How-to and Manuals", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Language and Reference", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Language and Reference", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Sport", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Sport", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Sport", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Sport", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Language and Reference", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Language and Reference", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Language and Reference", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Language and Reference", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Comic", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Comic", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Comic", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Comic", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Comic", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Business and Finance", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "History", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "History", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "History", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "History", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Law", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
                PublishedBook("1", name="A Quite place", coverUrl =  "https://i.ibb.co/gMpTyLY/bookfair2.png",
                    category = "Cook Books", tag = ""),
            )

            allBooksLive = allBooks

            if (allBooks.isNotEmpty()){
                layout.loadingAnimView.visibility = GONE
                layout.appbarLayout.visibility = VISIBLE
                layout.errorLayout.visibility = GONE
                layout.booksNestedScroll.visibility = VISIBLE

                 /*  cloudDb.getLiveListOfDataAsyncFrom(
                       DbFields.PUBLISHED_BOOKS.KEY,
                       PublishedBook::class.java,
                       allBooks[0].dateTimePublished!!
                   ){
                       lifecycleScope.launch(IO){
                           localDb.addAllPubBooks(it)
                       }
                   }*/

            }else{
                layout.booksNestedScroll.visibility = GONE
                layout.appbarLayout.visibility = INVISIBLE

                layout.loadingAnimView.visibility = VISIBLE

                    cloudDb.getLiveListOfDataAsync(
                        DbFields.PUBLISHED_BOOKS.KEY,
                        PublishedBook::class.java,
                        DbFields.DATE_TIME_PUBLISHED.KEY
                    ) {
                        layout.loadingAnimView.visibility = GONE
                        storeViewModel.addAllBooks(it)
                    }
            }
        })


        lifecycleScope.launch {
            storeViewModel.getTrendingBooksPageSource().collectLatest { books ->
                loadBooks(books, trendingBooksAdapter, layout.trendingLayout)
            }
        }

        lifecycleScope.launch {
            storeViewModel.getRecommendedBooksPageSource().collectLatest { books ->
                loadBooks(books, recommendBooksAdapter, layout.recommendedLayout)
            }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.religion)).collectLatest { books ->
                loadBooks(books, religionBooksAdapter, layout.religionLayout)
            }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.cook_books))
                .collectLatest { books ->
                    loadBooks(books, cooksBooksAdapter, layout.cookBookLayout)
                }
        }
        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.science_technology))
                .collectLatest { books ->
                    loadBooks(books, scienceAndTechBooksAdapter, layout.scienceTecLayout)
                }
        }
        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.art_craft))
                .collectLatest { books ->
                    loadBooks(books, artAndCraftBooksAdapter, layout.artCraftLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.news))
                .collectLatest { books ->
                    loadBooks(books, newsBooksAdapter, layout.newsLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.history))
                .collectLatest { books ->
                    loadBooks(books, historyBooksAdapter, layout.historyLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.business_finance))
                .collectLatest { books ->
                    loadBooks(books, businessBooksAdapter, layout.businessFinanceLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.poetry))
                .collectLatest { books ->
                    loadBooks(books, loveAndPoetryBooksAdapter, layout.loveLayout)
                }
        }
        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.manuals))
                .collectLatest { books ->
                    loadBooks(books, howToBooksAdapter, layout.howToLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.politics))
                .collectLatest { books ->
                    loadBooks(books, politicsBooksAdapter, layout.politicsLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.comic))
                .collectLatest { books ->
                    loadBooks(books, comicBooksAdapter, layout.comicLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.sport))
                .collectLatest { books ->
                    loadBooks(books, sportBooksAdapter, layout.sportLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.law))
                .collectLatest { books ->
                    loadBooks(books, lawBooksAdapter, layout.lawLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.languages_reference))
                .collectLatest { books ->
                    loadBooks(books, languageAndRefBooksAdapter, layout.langRefLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.education))
                .collectLatest { books ->
                    loadBooks(books, educationBooksAdapter, layout.educationLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.entertainment))
                .collectLatest { books ->
                    loadBooks(books, entertainmentBooksAdapter, layout.entertainmentLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.fiction))
                .collectLatest { books ->
                    loadBooks(books, fictionBooksAdapter, layout.fictionLayout)
                }
        }

        lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.travel))
                .collectLatest { books ->
                    loadBooks(books, travelBooksAdapter, layout.travelLayout)
                }
        }


        layout.recommendedCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.recommended_for))
        }
        layout.trendingCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.trending))
        }
        layout.artAndCraftCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.art_craft))
        }
        layout.religionCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.religion))
        }
        layout.businessAndFinanceCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.business_finance))
        }
        layout.loveAndPoetryCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.poetry))
        }
        layout.comicCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.comic))
        }
        layout.cookBooksCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.cook_books))
        }
        layout.historyCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.history))
        }
        layout.lanAndRefCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.languages_reference))
        }
        layout.newsCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.news))
        }
        layout.politicsCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.politics))
        }

        layout.sciAndTechCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.science_technology))
        }
        layout.sportCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.sport))
        }

        layout.lawCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.law))
        }
        layout.howToCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.manuals))
        }

        layout.travelCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.travel))
        }
        layout.educationCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.education))
        }

        layout.entertainmentCard.setOnClickListener {
            startBookCategoryActivity(getString(R.string.entertainment))
        }
        layout.fictionCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.fiction))
        }


        return layout.root
    }

    private fun setRecyclerViewLayoutManager(){
        layout.recommendedRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.trendingRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.artAndCraftRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.businessRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.comicRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.cookBooksRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.historyRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.howToRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.langAndRefRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.lawRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.loveAndPoetryRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.newsRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.politicsRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.religionRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.sciAndTechRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.sportRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.educationRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.fictionRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.entertainmentRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.travelRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private  fun loadBooks(list:PagingData<PublishedBook>, adapter: StoreListAdapter, layout:LinearLayoutCompat){

        adapter.addLoadStateListener { loadState ->
            layout.isVisible =
                !(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1)
        }

        lifecycleScope.launch {
            adapter.submitData(list)
        }
    }

    private fun startBookCategoryActivity(category: String){
        val intent = Intent(requireActivity(), BookCategoryActivity::class.java)
        intent.putExtra(Category.TITLE.KEY,category)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): StoreFragment {
            return StoreFragment()
        }
    }

    private fun showErrorMsg(errorMsg:Int){
        layout.loadingAnimView.visibility=View.GONE
        layout.errorImg.setImageDrawable(IconUtil.getDrawable(requireContext(), R.drawable.ic_network_alert))
        layout.errorMsgText.text = getString(errorMsg)
        layout.errorLayout.visibility=View.VISIBLE
    }


}