package com.bookshelfhub.bookshelfhub.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
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
import com.bookshelfhub.bookshelfhub.adapters.search.StoreSearchResultAdapter
import com.bookshelfhub.bookshelfhub.adapters.store.*
import com.bookshelfhub.bookshelfhub.databinding.FragmentStoreBinding
import com.bookshelfhub.bookshelfhub.enums.Category
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.view.materialsearch.internal.SearchLayout
import com.bookshelfhub.bookshelfhub.view.toast.Toast
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
    private val storeFragmentViewModel:StoreFragmentViewModel by viewModels()
    private var allBooksLive = emptyList<PublishedBooks>()
    private var storeSearchHistory = emptyList<StoreSearchHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentStoreBinding.inflate(inflater, container, false)


        storeFragmentViewModel.getIsNoConnection().observe(viewLifecycleOwner, Observer { isNoConnection ->
           if (isNoConnection){
               showErrorMsg(R.string.no_connection_err_msg)
           }
        })

        storeFragmentViewModel.getIsNetworkError().observe(viewLifecycleOwner, Observer { isNetworkError ->
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
            layout.errorLayout.visibility = View.GONE
            layout.loadingAnimView.visibility = View.VISIBLE
            storeFragmentViewModel.loadBooksFromCloud(emptyList())
        }

        storeFragmentViewModel.getAllPublishedBooks().observe(viewLifecycleOwner, Observer { allBooks ->
            allBooksLive = allBooks

            if (allBooks.isNotEmpty()){
                layout.loadingContainer.visibility = View.GONE
                layout.booksNestedScroll.visibility = View.VISIBLE
            }
        })


        lifecycleScope.launch {
            storeFragmentViewModel.getTrendingBooksPageSource().collectLatest { books ->
                loadBooks(books, trendingBooksAdapter, layout.trendingLayout)
            }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getRecommendedBooksPageSource().collectLatest { books ->
                loadBooks(books, recommendBooksAdapter, layout.recommendedLayout)
            }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.religion)).collectLatest { books ->
                loadBooks(books, religionBooksAdapter, layout.religionLayout)
            }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.cook_books))
                .collectLatest { books ->
                    loadBooks(books, cooksBooksAdapter, layout.cookBookLayout)
                }
        }
        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.science_technology))
                .collectLatest { books ->
                    loadBooks(books, scienceAndTechBooksAdapter, layout.scienceTecLayout)
                }
        }
        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.art_craft))
                .collectLatest { books ->
                    loadBooks(books, artAndCraftBooksAdapter, layout.artCraftLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.news))
                .collectLatest { books ->
                    loadBooks(books, newsBooksAdapter, layout.newsLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.history))
                .collectLatest { books ->
                    loadBooks(books, historyBooksAdapter, layout.historyLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.business_finance))
                .collectLatest { books ->
                    loadBooks(books, businessBooksAdapter, layout.businessFinanceLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.poetry))
                .collectLatest { books ->
                    loadBooks(books, loveAndPoetryBooksAdapter, layout.loveLayout)
                }
        }
        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.manuals))
                .collectLatest { books ->
                    loadBooks(books, howToBooksAdapter, layout.howToLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.politics))
                .collectLatest { books ->
                    loadBooks(books, politicsBooksAdapter, layout.politicsLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.comic))
                .collectLatest { books ->
                    loadBooks(books, comicBooksAdapter, layout.comicLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.sport))
                .collectLatest { books ->
                    loadBooks(books, sportBooksAdapter, layout.sportLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.law))
                .collectLatest { books ->
                    loadBooks(books, lawBooksAdapter, layout.lawLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.languages_reference))
                .collectLatest { books ->
                    loadBooks(books, languageAndRefBooksAdapter, layout.langRefLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.education))
                .collectLatest { books ->
                    loadBooks(books, educationBooksAdapter, layout.educationLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.entertainment))
                .collectLatest { books ->
                    loadBooks(books, entertainmentBooksAdapter, layout.entertainmentLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.fiction))
                .collectLatest { books ->
                    loadBooks(books, fictionBooksAdapter, layout.fictionLayout)
                }
        }

        lifecycleScope.launch {
            storeFragmentViewModel.getBooksByCategoryPageSource(getString(R.string.travel))
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

    private  fun loadBooks(list:PagingData<PublishedBooks>, adapter: StoreListAdapter, layout:LinearLayoutCompat){

        adapter.addLoadStateListener { loadState ->
            layout.isVisible =
                !(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1)
        }

        lifecycleScope.launch {
            adapter.submitData(list)
        }
    }

    private fun startBookCategoryActivity(category: String,){
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