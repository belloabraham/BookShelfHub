package com.bookshelfhub.bookshelfhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bookshelfhub.bookshelfhub.Utils.*
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateFormat
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateUtil
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.adapters.paging.DiffUtilItemCallback
import com.bookshelfhub.bookshelfhub.adapters.paging.SimilarBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.ReviewListAdapter
import com.bookshelfhub.bookshelfhub.config.IRemoteConfig
import com.bookshelfhub.bookshelfhub.extensions.string.Regex
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.book.*
import com.bookshelfhub.bookshelfhub.extensions.containsUrl
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import com.bookshelfhub.bookshelfhub.ui.Fragment
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.PostUserReview
import com.bookshelfhub.bookshelfhub.wrappers.Json
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.PubReferrer
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.ReferrerLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.continue_reading.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class BookItemActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookItemBinding
    private val BOOK_REPORT_URL = "book_report_url"
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var dynamicLink: IDynamicLink
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var json: Json
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth
    private val bookItemViewModel:BookItemViewModel by viewModels()
    private var userReview:UserReview?=null
    private var isVerifiedReview:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout =  ActivityBookItemBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = null


        var bookPrice:Float? =null
        val userId = userAuth.getUserId()
        val userPhotoUri = userAuth.getUserPhotoUrl()

        val title = intent.getStringExtra(Book.TITLE.KEY)!!
        val isbn = intent.getStringExtra(Book.ISBN.KEY)!!
        val author = intent.getStringExtra(Book.AUTHOR.KEY)!!
        val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)

        if (isSearchItem){
            bookItemViewModel.addStoreSearchHistory(StoreSearchHistory(isbn, title, userAuth.getUserId(), author, DateTimeUtil.getDateTimeAsString()))
        }

        val similarBooksAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())
        layout.similarBooksRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layout.similarBooksRecView.adapter = similarBooksAdapter

        bookItemViewModel.getPublishedBookOnline().observe(this, Observer { book ->
            bookPrice = book.price
            layout.progressBar.visibility = GONE
            layout.bookItemLayout.visibility = VISIBLE

            layout.price.text = String.format(getString(R.string.price), book.price)
            layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), book.totalReviews)

            val rating = book.totalRatings/book.totalReviews
            layout.noRatingTxt.text = "$rating"
            layout.noOfDownloadsText.text = getNoOfDownloads(book.totalDownloads)

            lifecycleScope.launch(IO){
                val orderedBook = localDb.getAnOrderedBook(isbn, userId)
                withContext(Main){
                    if (orderedBook.isPresent){
                        layout.openBookBtn.visibility = VISIBLE
                        isVerifiedReview = true
                    }else if(book.price > 0.0){
                        layout.addToCartBtn.visibility = VISIBLE
                    }else{
                        layout.downloadBtn.visibility = VISIBLE
                    }
                }
            }
        })


        var bookItem:PublishedBook? = null

        bookItemViewModel.getLivePublishedBook().observe(this, Observer { book->

                bookItem =  book
                layout.title.text = book.name
                layout.author.text = String.format(getString(R.string.by), book.author)

                layout.cover.load(book.coverUrl, R.drawable.ic_store_item_place_holder)

                bookItemViewModel.getLiveListOfCartItems().observe(this@BookItemActivity, Observer { cartItems ->

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
                loadSimilarBooks(book.category, similarBooksAdapter)

        })

        layout.addToCartBtn.setOnClickListener {

            bookPrice?.let {

                val book = bookItem!!
                val cart = Cart(
                    userId, book.isbn,
                    book.name, book.author,
                    book.coverUrl, it
                )

                bookItemViewModel.addToCart(cart)
            }


        }

        layout.shareBookBtn.setOnClickListener {

            var  link:String?
            lifecycleScope.launch {
                  link =  settingsUtil.getString(PubReferrer.USER_REF_LINK.KEY)

                    if (link==null){
                        dynamicLink.getLinkAsync(
                            remoteConfig.getString(ReferrerLink.TITLE.KEY),
                            remoteConfig.getString(ReferrerLink.DESC.KEY),
                            remoteConfig.getString(ReferrerLink.IMAGE_URL.KEY),
                            userId
                        ){
                           it?.let {
                              shareBook(it.toString())
                           }
                        }
                    }else{
                        shareBook(link!!)
                    }
            }
        }

        layout.downloadBtn.setOnClickListener {

        }

        layout.aboutBookCard.setOnClickListener {

            startBookInfoActivity(isbn,  title = bookItem!!.name, R.id.bookInfoFragment)
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, BookCategoryActivity::class.java)
            intent.putExtra(Category.TITLE.KEY, bookItem!!.category)
            startActivity(intent)
        }

        layout.allReviewsBtn.setOnClickListener {
            startBookInfoActivity(isbn)
        }
        
        layout.checkoutOutBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        layout.ratingBar.setOnRatingChangeListener { ratingBar, rating ->
            layout.ratingInfoLayout.isVisible = rating>0
        }

        layout.openBookBtn.setOnClickListener {

        }

        layout.postBtn.setOnClickListener {

                val review = layout.userReviewEditText.text.toString()
                val newRating = layout.ratingBar.rating.toDouble()

                val userName = userAuth.getName()!!

            var ratingDiff = 0.0
            var postedBefore = false
            userReview?.let {
                ratingDiff = newRating - it.userRating
                postedBefore = it.postedBefore
            }

            val newReview = UserReview(isbn, review, newRating, userName,isVerifiedReview, userPhotoUri, postedBefore)

               bookItemViewModel.addUserReview(newReview)
                if (isVerifiedReview && !review.containsUrl(Regex.URL_IN_TEXT)){
                    val data = Data.Builder()
                    data.putString(Book.ISBN.KEY, isbn)
                    data.putDouble(Book.RATING_DIFF.KEY, ratingDiff)
                    val userReviewPostWorker =
                        OneTimeWorkRequestBuilder<PostUserReview>()
                            .setConstraints(Constraint.getConnected())
                            .setInputData(data.build())
                            .build()
                    WorkManager.getInstance(applicationContext).enqueue(userReviewPostWorker)
                }

        }

        layout.userReviewEditText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    layout.reviewLengthTxt.text  = String.format(getString(R.string.reviewtextLength), it.length)
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val animDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        layout.editYourReviewBtn.setOnClickListener {
            layout.yourReviewLayout.visibility = GONE
            layout.rateBookLayout.visibility = VISIBLE
            AnimUtil(this).crossFade(layout.rateBookLayout, layout.yourReviewLayout, animDuration)
        }

        bookItemViewModel.getUserReviews().observe(this, Observer { reviews ->

            if (reviews.isNotEmpty()){
                layout.ratingsAndReviewLayout.visibility = VISIBLE
                val reviewsAdapter = ReviewListAdapter().getAdapter()
                layout.reviewRecView.adapter = reviewsAdapter
                reviewsAdapter.submitList(reviews)
            }

        })


        bookItemViewModel.getLiveUserReview().observe(this, Observer { review ->

            layout.ratingInfoLayout.visibility = GONE

            if (review.isPresent){

                AnimUtil(this).crossFade(layout.yourReviewLayout, layout.rateBookLayout, animDuration)
                userReview = review.get()
                review.get().let { userReview ->
                    layout.ratingBar.rating = userReview.userRating.toFloat()

                    userReview.reviewDate?.let {
                        val  date = DateUtil.dateToString(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                        layout.date.text = date
                    }
                    layout.userNameText.text = userReview.userName
                    layout.userReviewTxt.text = userReview.review
                    layout.userRatingBar.rating = userReview.userRating.toFloat()
                    layout.userReviewEditText.setText(userReview.review)

                    layout.userReviewTxt.isVisible = userReview.review.isNotBlank()

                    if (userPhotoUri!=null){
                        layout.letterIcon.visibility = GONE
                        layout.userImage.visibility = VISIBLE
                        layout.userImage.load(userPhotoUri){
                            showLetterIcon(userReview.userName)
                        }
                    }else{
                        showLetterIcon(userReview.userName)
                    }
                }
            }else{
                getUserReviewAsync(isbn, userId, animDuration)
            }

            val reviewLength = layout.userReviewEditText.text.toString().length

            layout.reviewLengthTxt.text  = String.format(getString(R.string.reviewtextLength), reviewLength)
        })
    }

    private fun showLetterIcon(value:String){
        layout.letterIcon.visibility = VISIBLE
        layout.letterIcon.letter = value
        layout.userImage.visibility = GONE
    }
    private fun shareBook(text:String){
        ShareUtil(this).shareText(text)
    }

    private fun getUserReviewAsync(isbn:String, userId:String, animDuration:Long){
        cloudDb.getLiveDataAsync(DbFields.PUBLISHED_BOOKS.KEY,isbn, DbFields.REVIEWS.KEY, userId){ documentSnapshot, _ ->
            documentSnapshot?.let {
                if (it.exists()){
                    val doc = it.data
                    val userReview = json.fromAny(doc!!, UserReview::class.java)

                    bookItemViewModel.addUserReview(userReview)

                }else{
                    AnimUtil(this).crossFade(layout.rateBookLayout, layout.yourReviewLayout, animDuration)
                }
            }
            if (documentSnapshot==null){
                AnimUtil(this).crossFade(layout.rateBookLayout, layout.yourReviewLayout, animDuration)
            }
        }
    }

    private fun getNoOfDownloads(value:Long): String {
        val thousand = 1000
        val billion = 1000000000
        val million = 1000000
        val remainder:Int
        val main:Long

      val unit:String = when {
          value>=billion -> {
              remainder = value.mod(billion)
              main = (value-remainder)/billion
              "B"
          }
          value >= million -> {
              remainder = value.mod(million)
              main = (value-remainder)/million
              "M"
          }
          value >= thousand -> {
              remainder = value.mod(thousand)
              main = (value-remainder)/thousand
              "K"
          }
          else -> {
              main = value
              ""
          }
      }
        val unitPlus = if (unit.isNotBlank()){
            "$unit+"
        }else{
            unit
        }
         return "$main"+unitPlus
    }



    private fun startBookInfoActivity(isbn: String, title:String, fragmentID:Int){
        val intent = Intent(this, BookInfoActivity::class.java)
        with(intent){
            putExtra(Fragment.TITLE.KEY,title)
            putExtra(Fragment.ID.KEY, fragmentID)
            putExtra(Book.ISBN.KEY, isbn)
        }
        startActivity(intent)
    }

    private fun startBookInfoActivity(isbn:String, title:Int=R.string.ratings_reviews, fragmentID:Int = R.id.reviewsFragment){
        startBookInfoActivity(isbn, getString(title), fragmentID)
    }

    private fun loadSimilarBooks(category: String, similarBooksAdapter:SimilarBooksAdapter){
        lifecycleScope.launch {

            bookItemViewModel.
            getBooksByCategoryPageSource(category).collectLatest { similarBooks ->

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_item_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.reportBook){
            val url = remoteConfig.getString(BOOK_REPORT_URL)
            val intent = Intent(this, WebViewActivity::class.java)
            with(intent){
                putExtra(WebView.TITLE.KEY, getString(R.string.report_book))
                putExtra(WebView.URL.KEY, url)
            }
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}