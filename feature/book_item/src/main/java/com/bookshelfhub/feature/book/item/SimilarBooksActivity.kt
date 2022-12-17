package com.bookshelfhub.feature.book.item

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.book.purchase.CartActivity
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.core.model.BookRequest
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.ui.views.adapters.DiffUtilItemCallback
import com.bookshelfhub.core.ui.views.materialsearch.internal.SearchLayout
import com.bookshelfhub.feature.book.item.adapters.SimilarBooksAdapter
import com.bookshelfhub.feature.book.item.adapters.SimilarBooksSearchResultAdapter
import com.bookshelfhub.feature.book.item.databinding.ActivitySimilarBooksBinding
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SimilarBooksActivity : AppCompatActivity() {
    private lateinit var layout: ActivitySimilarBooksBinding
    private var listOfBooks = mutableListOf<PublishedBookUiState>()
    private val similarBooksActivityViewModel by viewModels<SimilarBooksActivityViewModel>()
    private lateinit var bookRequestMsg:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivitySimilarBooksBinding.inflate(layoutInflater)
        setContentView(layout.root)

        bookRequestMsg = getString(R.string.cant_find_book)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = getString(R.string.similar_books)

        val categorySearchListAdapter = SimilarBooksSearchResultAdapter(this).getSearchResultAdapter()
        val bookListAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())

        val noOfRows = resources.getInteger(R.integer.no_of_category_books_in_a_row)
        layout.categoryBookRecView.layoutManager = GridLayoutManager(this, noOfRows)
        layout.categoryBookRecView.adapter = bookListAdapter


        lifecycleScope.launch {
            similarBooksActivityViewModel.getFlowOfBookBy().collectLatest { books->
                bookListAdapter.submitData(books)
            }
        }


        layout.materialSearchView.apply {
            val params =  layout.toolbarLayout.layoutParams as AppBarLayout.LayoutParams
            setAdapter(categorySearchListAdapter)
            setItemAnimator(null)
            setOnNavigationClickListener(object : SearchLayout.OnNavigationClickListener {
                override fun onNavigationClick(hasFocus: Boolean) {
                    onBackPressed()
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
                        SearchLayout.NavigationIconSupport.SEARCH
                    }
                    layout.toolbarLayout.layoutParams = params
                }
            })

            setOnQueryTextListener(object : SearchLayout.OnQueryTextListener {
                override fun onQueryTextChange(newText: CharSequence): Boolean {
                    val result = listOfBooks.filter {
                        it.name.contains(newText, true) || it.author.contains(newText, true)
                    }
                    categorySearchListAdapter.submitList(result.plus(BookRequest(bookRequestMsg)))
                    return true
                }

                override fun onQueryTextSubmit(query: CharSequence): Boolean {
                    return true
                }
            })
        }

        layout.checkOutBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        similarBooksActivityViewModel.getLiveTotalCartItemsNo().observe(this) { cartItemsCount ->
            val cartIsNotEmpty = cartItemsCount > 0
            if(cartIsNotEmpty){
                layout.cartNotifText.text = "$cartItemsCount"
                layout.cartBtnContainer.visibility = View.VISIBLE
            }else{
                layout.cartBtnContainer.visibility = View.GONE
            }

        }

        lifecycleScope.launch {
            val booksByCategory = similarBooksActivityViewModel.getBooksByCategory()
             listOfBooks.addAll(booksByCategory)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.book_category_activity_menu, menu)
        return true
    }

    override fun onDestroy() {
        layout.categoryBookRecView.adapter=null
        layout.materialSearchView.setAdapter(null)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (layout.materialSearchView.hasFocus()) {
            layout.materialSearchView.clearFocus()
            layout.materialSearchView.visibility = View.GONE
            layout.toolbar.visibility = View.VISIBLE
        }else{
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.search){
            layout.toolbar.visibility = View.GONE
            layout.materialSearchView.visibility = View.VISIBLE
            layout.materialSearchView.requestFocus()
        }else{
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}