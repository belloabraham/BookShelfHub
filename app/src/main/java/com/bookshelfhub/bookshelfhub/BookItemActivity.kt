package com.bookshelfhub.bookshelfhub

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bookshelfhub.bookshelfhub.Utils.LocalDateTimeUtil
import com.bookshelfhub.bookshelfhub.adapters.paging.DiffUtilItemCallback
import com.bookshelfhub.bookshelfhub.adapters.paging.SimilarBooksAdapter
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.Category
import com.bookshelfhub.bookshelfhub.enums.Fragment
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class BookItemActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookItemBinding
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var userAuth: IUserAuth
    private val bookItemViewModel:BookItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

          layout =  ActivityBookItemBinding.inflate(layoutInflater)
          setContentView(layout.root)
          setSupportActionBar(layout.toolbar)
          supportActionBar?.title = null

        val userId = userAuth.getUserId()

        val title = intent.getStringExtra(Book.TITLE.KEY)!!
        val isbn = intent.getStringExtra(Book.ISBN.KEY)!!
        val author = intent.getStringExtra(Book.AUTHOR.KEY)!!
        val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)

        if (isSearchItem){
            lifecycleScope.launch(IO){
                localDb.addStoreSearchHistory(StoreSearchHistory(isbn, title, userAuth.getUserId(), author, LocalDateTimeUtil.getDateTimeAsString()))
            }
        }

        val similarBooksAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())
        layout.similarBooksRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layout.similarBooksRecView.adapter= similarBooksAdapter

        var bookItem:PublishedBooks? = null
       lifecycleScope.launch(IO){
          bookItem =  localDb.getPublishedBook(isbn)
           withContext(Main){
              val book = bookItem!!
               book.price = 10.0
               if (book.price==0.0){
                   layout.downloadBtn.visibility = VISIBLE
               }

               layout.title.text = book.name
               layout.author.text = String.format(getString(R.string.by), book.author)
               layout.price.text = String.format(getString(R.string.price), book.price)
               layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), 10)
               layout.noRatingTxt.text = "4.5"
               layout.noOfDownloadsText.text = getNoOfDownloads(book.noOfDownloads)


               layout.cover.load(book.coverUrl, R.drawable.ic_store_item_place_holder)

               bookItemViewModel.getLiveListOfCartItems(userId).observe(this@BookItemActivity, Observer { cartItems ->
                   if(book.price > 0.0){
                       layout.addToCartBtn.visibility = VISIBLE
                   }

                   if(cartItems.isNotEmpty()){
                       layout.checkoutNotifText.text = "${cartItems.size}"
                       layout.checkoutBtnContainer.visibility = VISIBLE

                       val bookInCart = cartItems.filter {
                           it.isbn == book.isbn
                       }

                       if (bookInCart.isNotEmpty()){
                           layout.addToCartBtn.visibility = GONE
                       }


                   }else{
                       layout.checkoutBtnContainer.visibility = GONE
                   }
               })

               lifecycleScope.launch {
                   bookItemViewModel.getBooksByCategoryPageSource(book.category).collectLatest { similarBooks ->

                       similarBooksAdapter.addLoadStateListener { loadState ->
                           val isVisible =
                               !(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && similarBooksAdapter.itemCount < 1)
                           layout.similarBooksCard.isVisible = isVisible
                           layout.similarBooksRecView.isVisible = isVisible
                       }

                       lifecycleScope.launch {
                           similarBooksAdapter.submitData(similarBooks)
                       }

                   }
               }

           }
       }

        layout.addToCartBtn.setOnClickListener {

            val book = bookItem!!
            val cart = Cart(
                userId, book.isbn, book.pubId,
                book.name, book.author, book.coverUrl,
                book.description, book.dateTimePublished,
                book.noOfDownloads, book.price,
                book.category, book.language, book.tag
            )

            lifecycleScope.launch(IO) {
                localDb.addToCart(cart)
            }

        }

        layout.shareBookBtn.setOnClickListener {

        }

        layout.downloadBtn.setOnClickListener {

        }

        layout.aboutBookCard.setOnClickListener {
            startBookInfoActivity(bookItem!!.name, R.id.bookInfoFragment)
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, BookCategoryActivity::class.java)
            intent.putExtra(Category.TITLE.KEY, bookItem!!.category)
            startActivity(intent)
        }

        layout.reviewCard.setOnClickListener {
            startBookInfoActivity(R.string.ratings_reviews, R.id.reviewsFragment)
        }

        layout.allReviewsBtn.setOnClickListener {
            startBookInfoActivity(R.string.ratings_reviews, R.id.reviewsFragment)
        }
        
        layout.checkoutOutBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }


    private fun getNoOfDownloads(value:Long): String {
        val thousand = 1000
        val billion = 1000000000
        val million = 1000000
        var remainder:Int=0
        var main:Long = 0

      val unit:String =  if(value>=billion){
            remainder = value.mod(billion)
            main = (value-remainder)/billion
          "B"
        }else if (value >= million){
            remainder = value.mod(million)
            main = (value-remainder)/million
          "M"
        }else if (value >= thousand){
            remainder = value.mod(thousand)
            main = (value-remainder)/thousand
           "K"
        }else{
            main = value
            ""
       }
        val unitPlus = if (unit.isNotBlank()){
            "$unit+"
        }else{
            unit
        }
         return "$main"+unitPlus
    }

    private fun startBookInfoActivity(title:String, fragmentID:Int){
        val intent = Intent(this, BookInfoActivity::class.java)
        with(intent){
            putExtra(Fragment.TITLE.KEY,title)
            putExtra(Fragment.ID.KEY, fragmentID)
        }
        startActivity(intent)
    }

    private fun startBookInfoActivity(title:Int, fragmentID:Int){
        startBookInfoActivity(getString(title), fragmentID)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_item_activity_menu, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}