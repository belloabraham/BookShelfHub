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
import com.bookshelfhub.bookshelfhub.models.BookRequest
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.views.materialsearch.internal.SearchLayout
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookCategoryActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookCategoryBinding
    private var listOfBooks = emptyList<PublishedBook>()
    private val bookCategoryActivityViewModel:BookCategoryActivityViewModel by viewModels()
    //Message shown when user cant find a book they are searching for
    private lateinit var bookRequestMsg:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookCategoryBinding.inflate(layoutInflater)
        setContentView(layout.root)

        bookRequestMsg = getString(R.string.cant_find_book)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = bookCategoryActivityViewModel.getCategory()

        val searchListAdapter = StoreSearchResultAdapter(this).getSearchResultAdapter()
        val bookListAdapter = CategoryListAdapter(this, DiffUtilItemCallback())

        layout.categoryBookRecView.layoutManager = GridLayoutManager(this, 3)
        layout.categoryBookRecView.adapter = bookListAdapter


        lifecycleScope.launch {
            //If the category that trigger this book is recommended books, load recommended books as there is no book
            //with a category of "Recommended for you"
            bookCategoryActivityViewModel.getFlowOfBookCategory().collectLatest { books->
                bookListAdapter.submitData(books)
            }
        }


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
                    searchListAdapter.submitList(result.plus(BookRequest(bookRequestMsg)))
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

        bookCategoryActivityViewModel.getLiveTotalCartItemsNo().observe(this, Observer { cartItemsCount ->
            if(cartItemsCount>0){
                layout.cartNotifText.text = "$cartItemsCount"
                layout.cartBtnContainer.visibility = View.VISIBLE
            }else{
                layout.cartBtnContainer.visibility = View.GONE
            }

        })

        bookCategoryActivityViewModel.getLiveBooksByCategory().observe(this, Observer { books ->
            listOfBooks = books
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_category_activity_menu, menu)
        return true
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