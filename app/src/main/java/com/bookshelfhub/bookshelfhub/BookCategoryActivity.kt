package com.bookshelfhub.bookshelfhub

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bookshelfhub.bookshelfhub.adapters.recycler.StoreSearchResultAdapter
import com.bookshelfhub.bookshelfhub.adapters.paging.CategoryListAdapter
import com.bookshelfhub.bookshelfhub.adapters.paging.DiffUtilItemCallback
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookCategoryBinding
import com.bookshelfhub.bookshelfhub.enums.Category
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.views.materialsearch.internal.SearchLayout
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookCategoryActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookCategoryBinding
    private var listOfBooks = emptyList<PublishedBook>()
    private val bookCategoryViewModel:BookCategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookCategoryBinding.inflate(layoutInflater)
        setContentView(layout.root)

        val category = intent.getStringExtra(Category.TITLE.KEY)!!

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = category

        val searchListAdapter = StoreSearchResultAdapter(this).getSearchResultAdapter()
        val bookListAdapter = CategoryListAdapter(this, DiffUtilItemCallback())


        lifecycleScope.launch {
            if (category==getString(R.string.recommended_for)){
                bookCategoryViewModel.getRecommendedBooks().collectLatest { books->
                    bookListAdapter.submitData(books)
                }
            }else if(category==getString(R.string.trending)){
                bookCategoryViewModel.getTrendingBooks().collectLatest { books->
                    bookListAdapter.submitData(books)
                }
            }else{
                bookCategoryViewModel.getBooksByCategory().collectLatest { books->
                    bookListAdapter.submitData(books)
                }
            }
        }


        layout.categoryBookRecView.layoutManager = GridLayoutManager(this, 3)
        layout.categoryBookRecView.adapter = bookListAdapter

        val bookReqMsg = getString(R.string.cant_find_book)

        layout.materialSearchView.apply {
            val params =  layout.toolbarLayout.layoutParams as AppBarLayout.LayoutParams
            setAdapter(searchListAdapter)
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
                    searchListAdapter.submitList(result.plus(BookRequest(bookReqMsg)))
                    return true
                }

                override fun onQueryTextSubmit(query: CharSequence): Boolean {
                    //TODO Come back to implement this
                    return true
                }
            })
        }

        layout.checkOutBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        bookCategoryViewModel.getLiveTotalCartItemsNo().observe(this, Observer { cartItemsCount ->
            if(cartItemsCount>0){
                layout.cartNotifText.text = "$cartItemsCount"
                layout.cartBtnContainer.visibility = View.VISIBLE
            }else{
                layout.cartBtnContainer.visibility = View.GONE
            }

        })

        bookCategoryViewModel.getLiveBooksByCategory().observe(this, Observer { books ->
            listOfBooks = books
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_category_activity_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (layout.materialSearchView.hasFocus()) {
            layout.materialSearchView.clearFocus()
            layout.materialSearchView.visibility = View.GONE
            layout.toolbar.visibility = View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        layout.toolbar.visibility = View.GONE
        layout.materialSearchView.visibility = View.VISIBLE
        layout.materialSearchView.requestFocus()
        return super.onOptionsItemSelected(item)
    }
}