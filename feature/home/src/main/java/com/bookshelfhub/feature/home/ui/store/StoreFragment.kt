package com.bookshelfhub.feature.home.ui.store

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
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.book.purchase.CartActivity
import com.bookshelfhub.bookshelfhub.adapters.paging.StoreListAdapter
import com.bookshelfhub.core.common.helpers.utils.IconUtil
import com.bookshelfhub.feature.books_by_category.Category
import com.bookshelfhub.core.model.BookRequest
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.ui.views.adapters.DiffUtilItemCallback
import com.bookshelfhub.feature.home.databinding.FragmentStoreBinding
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.core.ui.views.materialsearch.internal.SearchLayout
import com.bookshelfhub.feature.books_by_category.BookCategoryActivity
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import com.bookshelfhub.feature.home.MainActivityViewModel
import com.bookshelfhub.feature.home.adapters.recycler.StoreSearchResultAdapter

@AndroidEntryPoint
@WithFragmentBindings
class StoreFragment : Fragment() {

    private var binding: FragmentStoreBinding?=null
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by viewModels()
    private var mSearchListAdapter: ListAdapter<Any, RecyclerViewHolder<Any>>?=null

    private var mIslamBooksAdapter: StoreListAdapter?=null
    private var mChristianityBooksAdapter: StoreListAdapter?=null

    private var mRecommendBooksAdapter: StoreListAdapter?=null
    private var mTrendingBooksAdapter:StoreListAdapter?=null
    private var mFashionBooksAdapter:StoreListAdapter?=null
    private var mScienceAndTechBooksAdapter : StoreListAdapter?=null
    private var mComicBooksAdapter : StoreListAdapter?=null
    private var mReligionBooksAdapter : StoreListAdapter?=null
    private var mEngAndMathBooksAdapter : StoreListAdapter?=null
    private var mArtAndCraftBooksAdapter : StoreListAdapter?=null
    private var mLawBooksAdapter : StoreListAdapter?=null
    private var mHistoryBooksAdapter : StoreListAdapter?=null
    private var mHowToBooksAdapter : StoreListAdapter?=null
    private var mLanguageAndRefBooksAdapter : StoreListAdapter?=null
    private var mNewsBooksAdapter : StoreListAdapter?=null
    private var mNutritionBooksAdapter : StoreListAdapter?=null
    private var mPoliticsBooksAdapter : StoreListAdapter?=null
    private var mHealthBooksAdapter : StoreListAdapter?=null
    private var mBusinessBooksAdapter : StoreListAdapter?=null
    private var mBiographyBooksAdapter : StoreListAdapter?=null
    private var mCooksBooksAdapter : StoreListAdapter?=null
    private var mTravelBooksAdapter : StoreListAdapter?=null
    private var mFictionBooksAdapter : StoreListAdapter?=null
    private var mEntertainmentBooksAdapter : StoreListAdapter?=null
    private var mLiteratureBooksAdapter : StoreListAdapter?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        binding= FragmentStoreBinding.inflate(inflater, container, false)
        val layout = binding!!
        
        storeViewModel.getLiveTotalCartItemsNo().observe(viewLifecycleOwner) { cartItemsCount ->
            layout.materialSearchView.setMenuNotifCount(cartItemsCount)
        }

        storeViewModel.getDoesBookLoadSuccessfully().observe(viewLifecycleOwner) { isSuccess ->
            showRemoteBooksLoadStatus(isSuccess, layout)
        }

        mSearchListAdapter = StoreSearchResultAdapter(requireContext()).getSearchResultAdapter()
        val searchListAdapter = mSearchListAdapter!!

        mRecommendBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val recommendBooksAdapter = mRecommendBooksAdapter!!

        mFashionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val fashionBooksAdapter = mFashionBooksAdapter!!

        mTrendingBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val trendingBooksAdapter = mTrendingBooksAdapter!!

        mScienceAndTechBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val scienceAndTechBooksAdapter = mScienceAndTechBooksAdapter!!

        mComicBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val comicBooksAdapter = mComicBooksAdapter!!

        mIslamBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val islamBooksAdapter = mIslamBooksAdapter!!

        mChristianityBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val christianityBooksAdapter = mChristianityBooksAdapter!!

        mReligionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val religionBooksAdapter = mReligionBooksAdapter!!

        mEngAndMathBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val engAndMathBooksAdapter = mEngAndMathBooksAdapter!!

        mArtAndCraftBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val artAndCraftBooksAdapter = mArtAndCraftBooksAdapter!!

        mLiteratureBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val literatureBooksAdapter = mLiteratureBooksAdapter!!
        
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

        mNutritionBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val nutritionBooksAdapter = mNutritionBooksAdapter!!

        mPoliticsBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val politicsBooksAdapter = mPoliticsBooksAdapter!!

        mHealthBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val sportBooksAdapter = mHealthBooksAdapter!!

        mBusinessBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val businessBooksAdapter = mBusinessBooksAdapter!!

        mBiographyBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val biographyBooksAdapter = mBiographyBooksAdapter!!

        mCooksBooksAdapter = StoreListAdapter(requireActivity(), DiffUtilItemCallback())
        val cooksBooksAdapter = mCooksBooksAdapter!!

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
        layout.recommendedRecView.adapter = recommendBooksAdapter
        layout.fashionRecView.adapter = recommendBooksAdapter
        layout.sciAndTechRecView.adapter=scienceAndTechBooksAdapter
        layout.comicRecView.adapter=comicBooksAdapter
        layout.islamRecView.adapter=islamBooksAdapter
        layout.christianityRecView.adapter=christianityBooksAdapter
        layout.religionRecView.adapter=religionBooksAdapter
        layout.engAndMathRecView.adapter=religionBooksAdapter
        layout.artAndCraftRecView.adapter=artAndCraftBooksAdapter
        layout.literatureRecView.adapter=literatureBooksAdapter

        layout.lawRecView.adapter=lawBooksAdapter
        layout.historyRecView.adapter=historyBooksAdapter
        layout.howToRecView.adapter=howToBooksAdapter
        layout.cookBooksRecView.adapter=cooksBooksAdapter
        layout.businessRecView.adapter=businessBooksAdapter
        layout.biographyRecView.adapter=businessBooksAdapter
        layout.healthRecView.adapter=sportBooksAdapter
        layout.politicsRecView.adapter=politicsBooksAdapter
        layout.loveAndPoetryRecView.adapter=nutritionBooksAdapter
        layout.newsRecView.adapter = newsBooksAdapter
        layout.langAndRefRecView.adapter=languageAndRefBooksAdapter

        if(storeViewModel.shouldEnableTrending()){
            layout.trendingRecView.adapter=trendingBooksAdapter
        }

        val bookReqMsg = getString(R.string.cant_find_book)

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
            setMenuIconVisibility(VISIBLE)
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
                        viewLifecycleOwner.lifecycleScope.launch {
                            val storeSearchHistory = storeViewModel.getTop4StoreSearchHistory()
                            searchListAdapter.submitList(storeSearchHistory)
                        }
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                    layout.materialSearchView.layoutParams = params
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val sqlLIKEQueryValue = "%$newText%"
                        val result =  storeViewModel.getPublishedBooksByNameOrAuthor(sqlLIKEQueryValue)
                        searchListAdapter.submitList(result.plus(BookRequest(bookReqMsg)))
                    }

                    return true
                }

                override fun onQueryTextSubmit(query: CharSequence): Boolean {
                    return true
                }
            })

        }

        //Store fragment can listen to onBackPressed for shelf fragment
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

        if(storeViewModel.shouldEnableTrending()){
            viewLifecycleOwner.lifecycleScope.launch {
                storeViewModel.getTrendingBooksPageSource().collectLatest { books ->
                    loadBooks(books, trendingBooksAdapter, layout.trendingLayout)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getRecommendedBooksPageSource().collectLatest { books ->
                loadBooks(books, recommendBooksAdapter, layout.recommendedLayout)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.fashion)).collectLatest { books ->
                loadBooks(books, fashionBooksAdapter, layout.fashionLayout)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.fashion)).collectLatest { books ->
                loadBooks(books, engAndMathBooksAdapter, layout.engAndMathLayout)
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
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.literature))
                .collectLatest { books ->
                    loadBooks(books, literatureBooksAdapter, layout.literatureLayout)
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
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.biography))
                .collectLatest { books ->
                    loadBooks(books, biographyBooksAdapter, layout.biographyLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.nutrition))
                .collectLatest { books ->
                    loadBooks(books, nutritionBooksAdapter, layout.nutritionLayout)
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
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.islam))
                .collectLatest { books ->
                    loadBooks(books, islamBooksAdapter, layout.islamLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.christianity))
                .collectLatest { books ->
                    loadBooks(books, christianityBooksAdapter, layout.christianityLayout)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storeViewModel.getBooksByCategoryPageSource(getString(R.string.health_fitness))
                .collectLatest { books ->
                    loadBooks(books, sportBooksAdapter, layout.healthLayout)
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
            startBookCategoryActivity( getString(com.bookshelfhub.feature.home.R.string.recommended_for))
        }

        layout.fashionCard.setOnClickListener {
            startBookCategoryActivity( getString(com.bookshelfhub.feature.home.R.string.fashion))
        }

        layout.trendingCard.setOnClickListener {
            startBookCategoryActivity( getString(com.bookshelfhub.feature.home.R.string.trending))
        }
        layout.artAndCraftCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.art_craft))
        }
        layout.literatureCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.literature))
        }
        layout.religionCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.religion))
        }

        layout.engAndMathCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.engineering_mathematics))
        }

        layout.businessAndFinanceCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.business_finance))
        }
        layout.biographyCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.biography))
        }
        layout.nutritionCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.nutrition))
        }
        layout.comicCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.comic))
        }

        layout.islamCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.islam))
        }

        layout.christianityCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.christianity))
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
        layout.healthCard.setOnClickListener {
            startBookCategoryActivity( getString(R.string.health_fitness))
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
        layout.fashionRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.trendingRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.artAndCraftRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.literatureRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.biographyRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.businessRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.comicRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.islamRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.christianityRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.cookBooksRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.historyRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.howToRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.langAndRefRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.lawRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.loveAndPoetryRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.newsRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.politicsRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.religionRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.engAndMathRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.sciAndTechRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layout.healthRecView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
        mFashionBooksAdapter = null
        mTrendingBooksAdapter = null
        mScienceAndTechBooksAdapter = null
        mComicBooksAdapter = null
        mReligionBooksAdapter = null
        mEngAndMathBooksAdapter = null
        mIslamBooksAdapter = null
        mChristianityBooksAdapter = null
        mArtAndCraftBooksAdapter = null
        mLiteratureBooksAdapter = null
        mLawBooksAdapter = null
        mHistoryBooksAdapter = null
        mHowToBooksAdapter =null
        mLanguageAndRefBooksAdapter = null
        mNewsBooksAdapter = null
        mNutritionBooksAdapter = null
        mPoliticsBooksAdapter = null
        mHealthBooksAdapter = null
        mBusinessBooksAdapter = null
        mBiographyBooksAdapter = null
        mCooksBooksAdapter = null
        mTravelBooksAdapter = null
        mFictionBooksAdapter = null
        mEntertainmentBooksAdapter = null
    }
    
}