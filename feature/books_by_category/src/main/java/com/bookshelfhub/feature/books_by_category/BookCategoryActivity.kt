package com.bookshelfhub.feature.books_by_category

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.book.purchase.CartActivity
import com.bookshelfhub.core.model.BookRequest
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.ui.views.adapters.DiffUtilItemCallback
import com.bookshelfhub.core.ui.views.materialsearch.internal.SearchLayout
import com.bookshelfhub.feature.books_by_category.adapters.CategoryListAdapter
import com.bookshelfhub.feature.books_by_category.adapters.CategorySearchResultAdapter
import com.bookshelfhub.feature.books_by_category.databinding.ActivityBookCategoryBinding
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookCategoryActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookCategoryBinding
    private var listOfBooks = mutableListOf<PublishedBookUiState>()
    private val bookCategoryActivityViewModel by viewModels<BookCategoryActivityViewModel>()

    private lateinit var bookRequestMsg:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookCategoryBinding.inflate(layoutInflater)
        setContentView(layout.root)

        bookRequestMsg = getString(R.string.cant_find_book)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = bookCategoryActivityViewModel.getCategory()

        val categorySearchListAdapter = CategorySearchResultAdapter(this).getSearchResultAdapter()
        val bookListAdapter = CategoryListAdapter(this, DiffUtilItemCallback())

        val noOfRows = resources.getInteger(R.integer.no_of_category_books_in_a_row)
        layout.categoryBookRecView.layoutManager = GridLayoutManager(this, noOfRows)
        layout.categoryBookRecView.adapter = bookListAdapter


        lifecycleScope.launch {
            bookCategoryActivityViewModel.getFlowOfBookCategory().collectLatest { books->
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

        bookCategoryActivityViewModel.getLiveTotalCartItemsNo().observe(this) { cartItemsCount ->
            val cartIsNotEmpty = cartItemsCount > 0
            if(cartIsNotEmpty){
                layout.cartNotifText.text = "$cartItemsCount"
                layout.cartBtnContainer.visibility = VISIBLE
            }else{
                layout.cartBtnContainer.visibility = GONE
            }

        }

        lifecycleScope.launch {
            listOfBooks.addAll(bookCategoryActivityViewModel.getBooksByCategory())
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
            layout.materialSearchView.visibility = GONE
            layout.toolbar.visibility = VISIBLE
        }else{
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.search){
            layout.toolbar.visibility = GONE
            layout.materialSearchView.visibility = VISIBLE
            layout.materialSearchView.requestFocus()
        }else{
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}