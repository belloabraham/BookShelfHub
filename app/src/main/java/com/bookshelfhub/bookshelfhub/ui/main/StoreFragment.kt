package com.bookshelfhub.bookshelfhub.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import androidx.paging.filter
import androidx.recyclerview.widget.LinearLayoutManager
import com.bookshelfhub.bookshelfhub.*
import com.bookshelfhub.bookshelfhub.adapters.search.ShelfSearchResultAdapter
import com.bookshelfhub.bookshelfhub.adapters.search.StoreSearchResultAdapter
import com.bookshelfhub.bookshelfhub.adapters.store.*
import com.bookshelfhub.bookshelfhub.databinding.FragmentStoreBinding
import com.bookshelfhub.bookshelfhub.enums.Category
import com.bookshelfhub.bookshelfhub.enums.Profile
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.view.search.internal.SearchLayout
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.android.synthetic.main.fragment_store.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        storeFragmentViewModel.getAlPublishedBooks().observe(viewLifecycleOwner, Observer { allBooks ->
            allBooksLive = allBooks
        })


        storeFragmentViewModel.getReligionBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, religionBooksAdapter, layout.religionLayout)
        })

        storeFragmentViewModel.getCooksBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, cooksBooksAdapter, layout.cookBookLayout)
        })

         storeFragmentViewModel.getTrendingBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, trendingBooksAdapter, layout.trendingLayout)
        })

        storeFragmentViewModel.getPoliticBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, politicsBooksAdapter, layout.politicsLayout)
        })

         storeFragmentViewModel.getHowToBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, howToBooksAdapter, layout.howToLayout)
        })

        storeFragmentViewModel.getLoveAndPoetryBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, loveAndPoetryBooksAdapter, layout.loveLayout)
        })

         storeFragmentViewModel.getBusinessBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, businessBooksAdapter, layout.businessFinanceLayout)
        })

        storeFragmentViewModel.getHistoryBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, historyBooksAdapter, layout.historyLayout)
        })

         storeFragmentViewModel.getRecommendedBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, recommendBooksAdapter, layout.recommendedLayout)
        })

        storeFragmentViewModel.getArtAndCraftBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, artAndCraftBooksAdapter, layout.artCraftLayout)
        })

         storeFragmentViewModel.getScienceAndTechBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, scienceAndTechBooksAdapter, layout.scienceTecLayout)
        })

        storeFragmentViewModel.getNewsBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, newsBooksAdapter, layout.newsLayout)
        })

         storeFragmentViewModel.getLawBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, lawBooksAdapter, layout.lawLayout)
        })

        storeFragmentViewModel.getComicBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, comicBooksAdapter, layout.comicLayout)
        })

         storeFragmentViewModel.getSportBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, sportBooksAdapter, layout.sportLayout)
        })

        storeFragmentViewModel.getLangAndRefBooks().observe(viewLifecycleOwner, Observer { books ->
            loadData(books, languageAndRefBooksAdapter, layout.langRefLayout)
        })

        layout.recommendedCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.recommended_for))
        }
        layout.trendingCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.trending))
        }
        layout.artAndCraftCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.art_craft))
        }
        layout.religionCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.religion))
        }
        layout.businessAndFinanceCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.business_finance))
        }
        layout.loveAndPoetryCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.poetry))
        }
        layout.comicCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.comic))
        }
        layout.cookBooksCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.cook_books))
        }
        layout.historyCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.history))
        }
        layout.lanAndRefCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.languages_reference))
        }
        layout.newsCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.news))
        }
        layout.politicsCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.politics))
        }

        layout.sciAndTechCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.science_technology))
        }
        layout.sportCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.sport))
        }

        layout.lawCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.law))
        }
        layout.howToCard.setOnClickListener {
            startBookCategoryActivity(it, getString(R.string.manuals))
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
    }

    private  fun loadData(list:PagingData<PublishedBooks>, adapter: StoreListAdapter, layout:LinearLayoutCompat){
        layout.isVisible =  true

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.NotLoading){
                    layout.isVisible =  adapter.itemCount>0
                }
            }
        }

        adapter.submitData(lifecycle, list)

    }

    private fun startBookCategoryActivity( view:View, category: String,){
        val intent = Intent(requireActivity(), BookCategoryActivity::class.java)
        intent.putExtra(Category.TITLE.KEY,category)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
    }

    companion object {
        @JvmStatic
        fun newInstance(): StoreFragment {
            return StoreFragment()
        }
    }


}