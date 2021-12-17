package com.bookshelfhub.bookshelfhub

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import androidx.work.ExistingWorkPolicy
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
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.extensions.string.Regex
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.Category
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
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.models.conversion.ConversionResponse
import com.bookshelfhub.bookshelfhub.services.payment.Conversion
import com.bookshelfhub.bookshelfhub.services.payment.Currency
import com.bookshelfhub.bookshelfhub.services.payment.LocalCurrency
import com.bookshelfhub.bookshelfhub.workers.ClearCart
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Response
import java.util.concurrent.TimeUnit
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
    private var pubReferrerId:String?=null
    private var onlineBookPriceInUSD:Double? =null
    private var visibilityAnimDuration:Long=0
    private var bookOnlineVersion:PublishedBook? = null
    private var localBook:PublishedBook?=null
    private lateinit var buyerVisibleCurrency:String
    private lateinit var userId: String
    private var bookShareUrl: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout =  ActivityBookItemBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = null

        visibilityAnimDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        val title = intent.getStringExtra(Book.TITLE.KEY)!!
        val isbn = intent.getStringExtra(Book.ISBN.KEY)!!
        val author = intent.getStringExtra(Book.AUTHOR.KEY)!!
        val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)

        //Check if this activity was started by a search result adapter item in StoreFragment, if so record a search history
        //for store fragment search result
        if (isSearchItem){
            bookItemViewModel.addStoreSearchHistory(StoreSearchHistory(isbn, title, userAuth.getUserId(), author, DateTimeUtil.getDateTimeAsString()))
        }

        userId = userAuth.getUserId()
        //UserPhoto Uri if user signed in with Gmail, to be display bu user review
        val userPhotoUri = userAuth.getUserPhotoUrl()

        //Check referrer database to see if referrer refer this user to the current book and get their ID
        bookItemViewModel.getLivePubReferrer().observe(this, Observer { pubReferrer ->
            if (pubReferrer.isPresent){
                pubReferrerId = pubReferrer.get().pubId
            }
        })


        //While Progress bar is loading
        //Get this particular book from the cloud as things like price, no of downloads and ratings may have changed
        //which have been queried by the by bookItemViewModel in init as snapshot listener
        bookItemViewModel.getPublishedBookOnline().observe(this, Observer { book ->

            bookOnlineVersion =  book

            val countryCode = Location.getCountryCode(this)

                countryCode?.let {

                    val localCurrency = LocalCurrency.get(it)

                     buyerVisibleCurrency = if(book.sellerCurrency == localCurrency){ //If seller currency is same as buyer's
                        //Return seller currency
                        book.sellerCurrency
                    }else{
                        //The buyer must be buying a book not sold in its country (it's fair to show book price in dollars)
                        Currency.USD.Value
                    }

                    if (book.price >0.0) {

                        val conversionEndpoint = remoteConfig.getString(Currency.CONVERSION_ENDPOINT.Value)
                        val conversionSuccessfulCode = 200

                        //If buyerVisibleCurrency is not in USD meaning the buyer is buying book sold in his/her country
                        //Then convert the buyerVisibleCurrency to USD (to show side by side with the local currency)
                        if (buyerVisibleCurrency != Currency.USD.Value){

                            val toCurrency = Currency.USD.Value
                            val queryParameters = Conversion.getQueryParam(buyerVisibleCurrency, toCurrency, book.price)
                            convertCurrency(conversionEndpoint, queryParameters){ response ->
                                if (response.code==conversionSuccessfulCode){
                                    try {
                                        val result  =  json.fromJson(response.body.toString(), ConversionResponse::class.java)
                                        val priceInUSD = result.info.rate*book.price
                                        showBookDetails(book,buyerVisibleCurrency, priceInUSD)
                                    }catch (e:Exception){

                                    }
                                }
                            }
                        }else{
                           showBookDetails(book, buyerVisibleCurrency)
                        }

                    }else{
                        showBookDetails(book, buyerVisibleCurrency)
                    }
                }



        })


        bookItemViewModel.getALiveOrderedBook().observe(this, Observer { orderedBook ->

            if (orderedBook.isPresent){
                //If the user have bought this book then hide downloadBtn and addToCartBtn
                //And make op visible so user can just open the book
                layout.openBookBtn.visibility = VISIBLE
                layout.downloadBtn.visibility = GONE
                layout.addToCartBtn.visibility = GONE

                //If the user have bought this book then the user review is verified
                isVerifiedReview = true

            }
        })

        //Get books in cart to know if books are in cart and if to show Checkout Button or not
        bookItemViewModel.getLiveListOfCartItems().observe(this, Observer { cartItems ->

            if(cartItems.isNotEmpty()){
                //Show checkout button no of items in cart and checkout button
                layout.checkoutNotifText.text = "${cartItems.size}"
                layout.checkoutBtnContainer.visibility = VISIBLE

                //Check if this book is in cart
                val bookInCart = cartItems.filter {
                    it.isbn == isbn
                }

                //if Book is in cart hide addToCartBtn
                if (bookInCart.isNotEmpty()){
                    layout.addToCartBtn.visibility = GONE
                }

            }else{
                layout.checkoutBtnContainer.visibility = GONE
            }
        })

        bookItemViewModel.getLiveLocalBook().observe(this, Observer { pubBook->
            val book = pubBook.get()
            dynamicLink.generateShortLinkAsync(
                book.name,
                book.description,
                book.coverUrl,
                userId
            ){
                bookShareUrl = it
            }

                localBook = book

                //This value should remain the same and should not be changed with online book value to avoid surprises for the
                // user as sudden change in the book cover, title or name is bad
                layout.title.text = book.name
                layout.author.text = String.format(getString(R.string.by), book.author)
                layout.cover.load(book.coverUrl, R.drawable.ic_store_item_place_holder)


        })

        layout.addToCartBtn.setOnClickListener {
            onlineBookPriceInUSD?.let {
                val book = bookOnlineVersion!!
                val priceInUSD:Double? = if(it == book.price){
                    null
                }else{
                    it
                }
                val cart = Cart(
                    userId, book.isbn,
                    book.name, book.author,
                    book.coverUrl, pubReferrerId, book.price,
                    buyerVisibleCurrency, priceInUSD
                )
                bookItemViewModel.addToCart(cart)

                //Clear every Items in this cart in the next 15 hours
                val clearCart =
                    OneTimeWorkRequestBuilder<ClearCart>()
                        .setInitialDelay(15, TimeUnit.HOURS)
                        .build()
                WorkManager.getInstance(this).enqueueUniqueWork("clearCart", ExistingWorkPolicy.REPLACE ,clearCart)
            }
        }

        layout.shareBookBtn.setOnClickListener {
            shareBook(this)
        }

        layout.downloadBtn.setOnClickListener {

        }

        layout.aboutBookCard.setOnClickListener {
            startBookInfoActivity(isbn,  title = localBook!!.name, R.id.bookInfoFragment)
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, BookCategoryActivity::class.java)
            intent.putExtra(Category.TITLE.KEY, bookOnlineVersion!!.category)
            startActivity(intent)
        }

        layout.allReviewsBtn.setOnClickListener {
            startBookInfoActivity(isbn, getString(R.string.ratings_reviews), R.id.reviewsFragment)
        }
        
        layout.checkoutOutBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        layout.ratingBar.setOnRatingChangeListener { ratingBar, rating ->
            //Hide rating layout of edit text if user rating is <=0
            layout.ratingInfoLayout.isVisible = rating>0
        }

        layout.openBookBtn.setOnClickListener {

        }

        //Post user review
        layout.postBtn.setOnClickListener {

            //Get all userReview Data
            val review = layout.userReviewEditText.text.toString()
            val newRating = layout.ratingBar.rating.toDouble()
            val userName = userAuth.getName()!!

            //difference in user rating from before compared to now
            var ratingDiff = 0.0
            //Have the user rating been uploaded to the cloud before?
            var postedBefore = false
            userReview?.let {
                ratingDiff = newRating - it.userRating
                postedBefore = it.postedBefore
            }

            val newReview = UserReview(isbn, review, newRating, userName, isVerifiedReview, userPhotoUri, postedBefore)

            // Save user review to local database
               bookItemViewModel.addUserReview(newReview)

            //Post user review to the cloud if user review does not contain any form of url
                if (!review.containsUrl(Regex.URL_IN_TEXT)){
                    //Put data to be passed to the review worker, data of the ISBN(Book that was reviewed) and Rating diff
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

            //Show the user the number of text he/she have left to type out of 500 which is review edit
            // text length as the user type
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    layout.reviewLengthTxt.text  = String.format(getString(R.string.reviewtextLength), it.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        

        //Show review layout
        layout.editYourReviewBtn.setOnClickListener {
            layout.yourReviewLayout.visibility = GONE
            layout.rateBookLayout.visibility = VISIBLE
            //Review layout to existing review layout(if visible) visibility animation
            AnimUtil(this).crossFade(layout.rateBookLayout, layout.yourReviewLayout, visibilityAnimDuration)
        }

        bookItemViewModel.getTopThreeOnlineUserReviews().observe(this, Observer { reviews ->

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
                //Visibility animation between existing review and new review layout
                AnimUtil(this).crossFade(layout.yourReviewLayout, layout.rateBookLayout, visibilityAnimDuration)
                userReview = review.get()

                review.get().let { userReview ->
                    layout.ratingBar.rating = userReview.userRating.toFloat()

                    val date = userReview.dateTime as Timestamp
                    date.let {
                        //Convert server dateTime to local date in string
                        val  localDate = DateUtil.dateToString(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                        layout.date.text = localDate
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
                //If user review is not present bcos user have not yet reviewed this app or user is opening this book on a
                    // new device get user review from online database
                getUserReviewAsync(isbn, userId, visibilityAnimDuration)
            }

            //Get how long the user review text lenght is
            val reviewLength = layout.userReviewEditText.text.toString().length

            layout.reviewLengthTxt.text  = String.format(getString(R.string.reviewtextLength), reviewLength)
        })
    }

    private fun showBookDetails(book:PublishedBook, buyerVisibleCurrency:String, priceInUSD:Double?=null){

        layout.progressBar.visibility = GONE


        if(priceInUSD!=null){
            onlineBookPriceInUSD = priceInUSD
            layout.price.text = String.format(getString(R.string.local_price_and_usd), buyerVisibleCurrency,book.price,priceInUSD)
        }else{
            onlineBookPriceInUSD = book.price
            layout.price.text = String.format(getString(R.string.usd_price), book.price)

        }

        val similarBooksAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())

        layout.similarBooksRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        layout.similarBooksRecView.adapter = similarBooksAdapter

        layout.bookItemLayout.visibility = VISIBLE


        //  layout.price.text = String.format(getString(R.string.price), book.price)
        layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), book.totalReviews)

        val rating = book.totalRatings/book.totalReviews
        layout.noRatingTxt.text = "$rating"
        layout.noOfDownloadsText.text = getNoOfDownloads(book.totalDownloads)

        if(book.price == 0.0){
            layout.downloadBtn.visibility = VISIBLE
            layout.addToCartBtn.visibility = GONE
        }

        //Load similar books for this book by category
        loadSimilarBooks(book.category, similarBooksAdapter)
    }

    private fun showLetterIcon(value:String){
        layout.letterIcon.visibility = VISIBLE
        layout.letterIcon.letter = value
        layout.userImage.visibility = GONE
    }

    private fun shareBook(activity: Activity){
        bookShareUrl?.let {
            ShareUtil(activity).shareText(it.toString())
        }

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

    private fun convertCurrency(conversionEndpoint:String, queryParameters:String, onComplete:(Response)->Unit){
        WebApi().get(conversionEndpoint, queryParameters) { response ->
            onComplete(response)
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
            putExtra(Book.TITLE.KEY,title)
            putExtra(Fragment.ID.KEY, fragmentID)
            putExtra(Book.ISBN.KEY, isbn)
        }
        startActivity(intent)
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

        //User can report this book for anything, like a stolen copyrighted content or so
        if(item.itemId == R.id.reportBookSubMenu){

            val url = remoteConfig.getString(BOOK_REPORT_URL)
            val intent = Intent(this, WebViewActivity::class.java)
            with(intent){
                putExtra(WebView.TITLE.KEY, getString(R.string.report_book))
                putExtra(WebView.URL.KEY, url)
            }
            startActivity(intent)
        }else if (item.itemId != R.id.reportBook){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }



}