package com.bookshelfhub.bookshelfhub.ui.main.store

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
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.*
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.IconUtil
import com.bookshelfhub.bookshelfhub.adapters.recycler.StoreSearchResultAdapter
import com.bookshelfhub.bookshelfhub.adapters.paging.*
import com.bookshelfhub.bookshelfhub.databinding.FragmentStoreBinding
import com.bookshelfhub.bookshelfhub.data.Category
import com.bookshelfhub.bookshelfhub.data.models.BookRequest
import com.bookshelfhub.bookshelfhub.data.models.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.data.models.uistate.PublishedBookUiState
import com.bookshelfhub.bookshelfhub.views.materialsearch.internal.SearchLayout
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class StoreFragment : Fragment() {

    private var binding: FragmentStoreBinding?=null
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by viewModels()
    private var storeSearchHistory = emptyList<StoreSearchHistory>()
    private var mSearchListAdapter: ListAdapter<Any, RecyclerViewHolder<Any>>?=null


    private var mRecommendBooksAdapter:StoreListAdapter?=null
    private var mTrendingBooksAdapter:StoreListAdapter?=null
    private var mScienceAndTechBooksAdapter : StoreListAdapter?=null
    private var mComicBooksAdapter : StoreListAdapter?=null
    private var mReligionBooksAdapter : StoreListAdapter?=null
    private var mArtAndCraftBooksAdapter : StoreListAdapter?=null
    private var mLawBooksAdapter : StoreListAdapter?=null
    private var mHistoryBooksAdapter : StoreListAdapter?=null
    private var mHowToBooksAdapter : StoreListAdapter?=null
    private var mLanguageAndRefBooksAdapter : StoreListAdapter?=null
    private var mNewsBooksAdapter : StoreListAdapter?=null
    private var mLoveAndPoetryBooksAdapter : StoreListAdapter?=null
    private var mPoliticsBooksAdapter : StoreListAdapter?=null
    private var mSportBooksAdapter : StoreListAdapter?=null
    private var mBusinessBooksAdapter : StoreListAdapter?=null
    private var mCooksBooksAdapter : StoreListAdapter?=null
    private var mEducationBooksAdapter : StoreListAdapter?=null
    private var mTravelBooksAdapter : StoreListAdapter?=null
    private var mFictionBooksAdapter : StoreListAdapter?=null
    private var mEntertainmentBooksAdapter : StoreListAdapter?=null
    

    @Inject
    lateinit var connectionUtil: ConnectionUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        binding= FragmentStoreBinding.inflate(inflater, container, false)
        val layout = binding!!
        
        storeViewModel.getLiveTotalCartItemsNo().observe(viewLifecycleOwner, Observer { cartItemsCount ->
            layout.materialSearchView.setMenuNotifCount(cartItemsCount)
        })


        storeViewModel.getDoesBookLoadSuccessfully().observe(viewLifecycleOwner, Observer { isSuccess ->
            showRemoteBooksLoadStatus(isSuccess, layout)
        })

        mSearchListAdapter = StoreSearchResultAdapter(requireContext()).getSearchResultAdapter()
        val searchListAdapter = mSearchListAdapter!!

        mRecommendBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val recommendBooksAdapter = mRecommendBooksAdapter!!

        mTrendingBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val trendingBooksAdapter = mTrendingBooksAdapter!!

        mScienceAndTechBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val scienceAndTechBooksAdapter = mScienceAndTechBooksAdapter!!

        mComicBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val comicBooksAdapter = mComicBooksAdapter!!

        mReligionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val religionBooksAdapter = mReligionBooksAdapter!!
        
        mArtAndCraftBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val artAndCraftBooksAdapter = mArtAndCraftBooksAdapter!!
        
        mLawBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val lawBooksAdapter = mLawBooksAdapter!!
        
        mHistoryBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val historyBooksAdapter = mHistoryBooksAdapter!!
        
        mHowToBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val howToBooksAdapter = mHowToBooksAdapter!!

        mLanguageAndRefBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val languageAndRefBooksAdapter = mLanguageAndRefBooksAdapter!!

        mNewsBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val newsBooksAdapter = mNewsBooksAdapter!!

        mLoveAndPoetryBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val loveAndPoetryBooksAdapter = mLoveAndPoetryBooksAdapter!!

        mPoliticsBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val politicsBooksAdapter = mPoliticsBooksAdapter!!

        mSportBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val sportBooksAdapter = mSportBooksAdapter!!

        mBusinessBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val businessBooksAdapter = mBusinessBooksAdapter!!

        mCooksBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val cooksBooksAdapter = mCooksBooksAdapter!!

        mEducationBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val educationBooksAdapter = mEducationBooksAdapter!!

        mTravelBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val travelBooksAdapter = mTravelBooksAdapter!!

        mFictionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val fictionBooksAdapter = mFictionBooksAdapter!!

        mEntertainmentBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val entertainmentBooksAdapter = mEntertainmentBooksAdapter!!

        setRecyclerViewLayoutManager(layout)

        layout.entertainmentRecView.adapter = entertainmentBooksAdapter
        layout.fictionRecView.adapter= fictionBooksAdapter
        layout.travelRecView.adapter= travelBooksAdapter
        layout.educationRecView.adapter= educationBooksAdapter
        
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

        storeViewModel.getStoreSearchHistory().observe(viewLifecycleOwner, Observer { searchHistory ->
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
                    val result = storeViewModel.getBooksForSearchFiler().filter {
                        it.name.contains(newText, true)||it.author.contains(newText, true)
                    }
                   searchListAdapter.submitList(result.plus(BookRequest(bookReqMsg)))
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
               mainActivityViewModel.setOnBackPressed(true)
            }
        }

        layout.retryBtn.setOnClickListener {
            layout.errorLayout.visibility = GONE
            layout.progressBar.visibility = VISIBLE
            storeViewModel.loadRemotePublishedBooks()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getTrendingBooksPageSource().collectLatest { books ->
                loadBooks(books, trendingBooksAdapter, layout.trendingLayout)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getRecommendedBooksPageSource().collectLatest { books ->
                loadBooks(books, recommendBooksAdapter, layout.recommendedLayout)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.religion)).collectLatest { books ->
                loadBooks(books, religionBooksAdapter, layout.religionLayout)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.cook_books))
                .collectLatest { books ->
                    loadBooks(books, cooksBooksAdapter, layout.cookBookLayout)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.science_technology))
                .collectLatest { books ->
                    loadBooks(books, scienceAndTechBooksAdapter, layout.scienceTecLayout)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.art_craft))
                .collectLatest { books ->
                    loadBooks(books, artAndCraftBooksAdapter, layout.artCraftLayout)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.news))
                .collectLatest { books ->
                    loadBooks(books, newsBooksAdapter, layout.newsLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.history))
                .collectLatest { books ->
                    loadBooks(books, historyBooksAdapter, layout.historyLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.business_finance))
                .collectLatest { books ->
                    loadBooks(books, businessBooksAdapter, layout.businessFinanceLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.poetry))
                .collectLatest { books ->
                    loadBooks(books, loveAndPoetryBooksAdapter, layout.loveLayout)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.manuals))
                .collectLatest { books ->
                    loadBooks(books, howToBooksAdapter, layout.howToLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.politics))
                .collectLatest { books ->
                    loadBooks(books, politicsBooksAdapter, layout.politicsLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.comic))
                .collectLatest { books ->
                    loadBooks(books, comicBooksAdapter, layout.comicLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.sport))
                .collectLatest { books ->
                    loadBooks(books, sportBooksAdapter, layout.sportLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.law))
                .collectLatest { books ->
                    loadBooks(books, lawBooksAdapter, layout.lawLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.languages_reference))
                .collectLatest { books ->
                    loadBooks(books, languageAndRefBooksAdapter, layout.langRefLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.education))
                .collectLatest { books ->
                    loadBooks(books, educationBooksAdapter, layout.educationLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.entertainment))
                .collectLatest { books ->
                    loadBooks(books, entertainmentBooksAdapter, layout.entertainmentLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.fiction))
                .collectLatest { books ->
                    loadBooks(books, fictionBooksAdapter, layout.fictionLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
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

    private fun setRecyclerViewLayoutManager(layout:FragmentStoreBinding){
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

    private  fun loadBooks(list:PagingData<PublishedBookUiState>, adapter: StoreListAdapter, layout:LinearLayoutCompat){

        adapter.addLoadStateListener { loadState ->
            layout.isVisible =
                !(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.submitData(list)
        }
    }

    private fun startBookCategoryActivity(category: String){
        val intent = Intent(requireActivity(), BookCategoryActivity::class.java)
        intent.putExtra(Category.TITLE,category)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): StoreFragment {
            return StoreFragment()
        }
    }

    override fun onDestroyView() {
        binding=null
        nullifyAllAdapters()
        super.onDestroyView()
    }

    private fun showRemoteBooksLoadStatus(bookLoadSuccessfully:Boolean, layout: FragmentStoreBinding){
        layout.progressBar.visibility=GONE
        if(bookLoadSuccessfully){
            layout.errorLayout.visibility=GONE
            layout.booksNestedScroll.visibility = VISIBLE
            layout.appbarLayout.visibility = VISIBLE
        }else{
            layout.errorImg.setImageDrawable(IconUtil.getDrawable(requireActivity().applicationContext, R.drawable.ic_network_alert))
            layout.errorMsgText.text = getString(R.string.bad_connection_err_msg)
            layout.errorLayout.visibility=VISIBLE
        }
    }

    private fun nullifyAllAdapters(){
        mSearchListAdapter = null
        mRecommendBooksAdapter = null
        mTrendingBooksAdapter = null
        mScienceAndTechBooksAdapter = null
        mComicBooksAdapter = null
        mReligionBooksAdapter = null
        mArtAndCraftBooksAdapter = null
        mLawBooksAdapter = null
        mHistoryBooksAdapter = null
        mHowToBooksAdapter =null
        mLanguageAndRefBooksAdapter = null
        mNewsBooksAdapter = null
        mLoveAndPoetryBooksAdapter = null
        mPoliticsBooksAdapter = null
        mSportBooksAdapter = null
        mBusinessBooksAdapter = null
        mCooksBooksAdapter = null
        mEducationBooksAdapter = null
        mTravelBooksAdapter = null
        mFictionBooksAdapter = null
        mEntertainmentBooksAdapter = null
    }
    
}