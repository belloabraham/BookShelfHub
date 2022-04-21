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
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateFormat
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateUtil
import com.bookshelfhub.bookshelfhub.adapters.paging.DiffUtilItemCallback
import com.bookshelfhub.bookshelfhub.adapters.paging.SimilarBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.ReviewListAdapter
import com.bookshelfhub.bookshelfhub.data.*
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.helpers.currencyconverter.Currency
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.helpers.utils.*
import com.bookshelfhub.bookshelfhub.workers.*
import com.google.common.base.Optional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class BookItemActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookItemBinding
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var userAuth: IUserAuth

    private val bookItemActivityViewModel:BookItemActivityViewModel by viewModels()
    private var userReview: UserReview?=null
    private var isVerifiedReview:Boolean = false
    private var referrer:String?=null
    private var onlineBookPriceInUSD:Double? =null
    private var visibilityAnimDuration:Long=0
    private var bookOnlineVersion: PublishedBook? = null
    private var localBook: PublishedBook?=null
    private lateinit var userVisibleCurrency:String
    private lateinit var userId: String
    private lateinit var bookId:String
    private var orderedBook: Optional<OrderedBook> = Optional.absent()
    private var cartItems = listOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout =  ActivityBookItemBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = null

        bookId = bookItemActivityViewModel.getBookId()
        visibilityAnimDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        userId = userAuth.getUserId()
        val userPhotoUri = userAuth.getPhotoUrl()

        bookItemActivityViewModel.getOptionalCollaboratorForThisBook().observe(this, Observer { collaborator ->
            if (collaborator.isPresent){
                this.referrer = collaborator.get().collabId
            }
        })

        bookItemActivityViewModel.getALiveOrderedBook().observe(this, Observer { orderedBook ->
            this.orderedBook = orderedBook
           showOrderedBook(orderedBook)
        })

        // Get books in cart to know if books are in cart
        bookItemActivityViewModel.getLiveListOfCartItems().observe(this, Observer { cartItems ->
            this.cartItems = cartItems
            showCartItems(cartItems)
        })

        bookItemActivityViewModel.getLiveLocalBook().observe(this, Observer { pubBook->
            val book = pubBook.get()
                localBook = book
                layout.title.text = book.name
                layout.author.text = String.format(getString(R.string.by), book.author)
                layout.cover.load(book.coverUrl, R.drawable.ic_store_item_place_holder)
        })


        bookItemActivityViewModel.getPublishedBookOnline().observe(this, Observer { book ->

            bookOnlineVersion =  book

            val countryCode = Location.getCountryCode(this)

            countryCode?.let {

                val localCurrency = Currency.getLocalCurrency(countryCode = it)

                userVisibleCurrency = if(book.sellerCurrency == localCurrency){
                    book.sellerCurrency
                }else{
                    Currency.USD
                }

                val bookIsFree = book.price > 0

                if (bookIsFree) {
                    showAddToCartButton()
                    val bookCurrencySameAsBuyerLocalCurrency = userVisibleCurrency != Currency.USD
                    if (bookCurrencySameAsBuyerLocalCurrency){
                        lifecycleScope.launch {
                            try {
                              val response =  bookItemActivityViewModel.convertCurrency(
                                    fromCurrency =  userVisibleCurrency,
                                    toCurrency = Currency.USD, amount = book.price)

                                if(response.isSuccessful && response.body()!=null){
                                    val priceInUSD = response.body()!!.info.rate*book.price
                                    showBookDetails(book, userVisibleCurrency, priceInUSD)
                                }else{
                                    Timber.e(response.message())
                                    return@launch
                                }
                            }catch (e:Exception){
                                Timber.e(e)
                                return@launch
                            }
                        }
                    }

                    if(!bookCurrencySameAsBuyerLocalCurrency){
                        showBookDetails(book, userVisibleCurrency)
                    }
                }

                if(!bookIsFree){
                    showBookDetails(book, userVisibleCurrency)
                }
            }
        })


        layout.addToCartBtn.setOnClickListener {
            onlineBookPriceInUSD?.let {
                val book = bookOnlineVersion!!
                val priceInUSD:Double? = if(it == book.price) null else it

                val cart = CartItem(
                    userId, book.bookId,
                    book.name,
                    book.author,
                    book.pubId,
                    book.coverUrl,
                    referrer,
                    book.price,
                    userVisibleCurrency, priceInUSD
                )

                bookItemActivityViewModel.addToCart(cart)
            }
        }

        layout.shareBookBtn.setOnClickListener {
            shareBook()
        }

        layout.downloadBtn.setOnClickListener {

        }

        layout.openBookBtn.setOnClickListener {
            startBookActivity()
        }


        layout.aboutBookCard.setOnClickListener {
            startBookInfoActivity(bookId,  title = localBook!!.name, R.id.bookInfoFragment)
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, BookCategoryActivity::class.java)
            intent.putExtra(Category.TITLE, bookOnlineVersion!!.category)
            startActivity(intent)
        }

        layout.allReviewsBtn.setOnClickListener {
            startBookInfoActivity(bookId, getString(R.string.ratings_reviews), R.id.reviewsFragment)
        }
        
        layout.viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        layout.ratingBar.setOnRatingChangeListener { ratingBar, rating ->
            val userRated = rating > 0
            layout.ratingInfoLayout.isVisible = userRated
        }

        layout.postBtn.setOnClickListener {

            val review = layout.userReviewEditText.text.toString()
            val newRating = layout.ratingBar.rating.toDouble()
            val userName = bookItemActivityViewModel.getUser().firstName

            var ratingDiff = 0.0
            var postedBefore = false
            userReview?.let {
                ratingDiff = newRating - it.userRating
                postedBefore = it.postedBefore
            }

            val newReview = UserReview(bookId, review, newRating, userName, isVerifiedReview, userPhotoUri, postedBefore)

            bookItemActivityViewModel.addUserReview(newReview, ratingDiff)

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
        

        layout.editYourReviewBtn.setOnClickListener {
            layout.yourReviewLayout.visibility = GONE
            layout.rateBookLayout.visibility = VISIBLE
            AnimUtil(this).crossFade(layout.rateBookLayout, layout.yourReviewLayout, visibilityAnimDuration)
        }

        bookItemActivityViewModel.getTwoUserReviewsForBook().observe(this, Observer { reviews ->

            if (reviews.isNotEmpty()){
                layout.ratingsAndReviewLayout.visibility = VISIBLE
                val reviewsAdapter = ReviewListAdapter().getAdapter()
                layout.reviewRecView.adapter = reviewsAdapter
                reviewsAdapter.submitList(reviews)
            }

        })

        bookItemActivityViewModel.getLiveUserReview().observe(this, Observer { review ->

            layout.ratingInfoLayout.visibility = GONE

            if (review.isPresent){

                AnimUtil(this).crossFade(layout.yourReviewLayout, layout.rateBookLayout, visibilityAnimDuration)

                review.get().let { userReview ->
                    layout.ratingBar.rating = userReview.userRating.toFloat()

                    userReview.dateTime?.let {
                        val  localDate = DateUtil.getHumanReaddable(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                        layout.date.text = localDate
                    }

                    layout.userNameText.text = userReview.userName
                    layout.userReviewTxt.text = userReview.review
                    layout.userRatingBar.rating = userReview.userRating.toFloat()
                    layout.userReviewEditText.setText(userReview.review)

                    layout.userReviewTxt.isVisible = userReview.review.isNotBlank()

                    setUserReviewProfilePhoto(userPhotoUri, userReview.userName)
                }
            }

            val reviewLength = layout.userReviewEditText.text.toString().length

            layout.reviewLengthTxt.text  = String.format(getString(R.string.reviewtextLength), reviewLength)

            showOrderedBook(orderedBook)
            showCartItems(cartItems)
        })
    }

    private fun setUserReviewProfilePhoto(userPhotoUri:String?, userName:String){
        if (userPhotoUri!=null){
            layout.letterIcon.visibility = GONE
            layout.userImage.visibility = VISIBLE
            layout.userImage.load(userPhotoUri){
                showLetterIcon(userName)
            }
        }else{
            showLetterIcon(userName)
        }
    }

    private fun showOrderedBook(orderedBook: Optional<OrderedBook>){
        if (orderedBook.isPresent){

            val book = orderedBook.get()
            val filName = "$bookId.pdf"
            val dirPath = "${book.pubId}${File.separator}$bookId"
            if(AppExternalStorage.isDocumentFileExist(applicationContext, dirPath, filName)){
                showOpenBookButton()
            }else{
                showDownloadBookButton()
            }

            isVerifiedReview = true
        }
    }

    private fun showCartItems(cartItems:List<CartItem>){
        if(cartItems.isNotEmpty()){
            layout.checkoutNotifText.text = "${cartItems.size}"

           val bookInCart = cartItems.filter {
                it.bookId == bookId
            }

            if (bookInCart.isNotEmpty()){
                showViewCartButton()
            }
        }
    }

    private fun showBookDetails(book: PublishedBook, buyerVisibleCurrency:String, priceInUSD:Double?=null){

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

        layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), book.totalReviews)

        val rating = book.totalRatings/book.totalReviews
        layout.noRatingTxt.text = "$rating"
        layout.noOfDownloadsText.text = Downloads.getHumanReadable(book.totalDownloads)

        if(book.price == 0.0){
            val fileName = "$bookId.pdf"
            val dirPath = "${book.pubId}${File.separator}$bookId"
            if(AppExternalStorage.isDocumentFileExist(applicationContext, dirPath, fileName)){
                showOpenBookButton()
            }else{
                showDownloadBookButton()

            }
        }

        loadSimilarBooks(book.category, similarBooksAdapter)

        layout.bookItemLayout.visibility = VISIBLE
    }

    private fun showAddToCartButton(){
        layout.openBookBtn.visibility = GONE
        layout.downloadBtn.visibility = GONE
        layout.viewCartFrame.visibility = GONE
        layout.addToCartBtn.visibility= VISIBLE
    }

    private fun showDownloadBookButton(){
        layout.openBookBtn.visibility = GONE
        layout.downloadBtn.visibility = VISIBLE
        layout.viewCartFrame.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }

    private fun showOpenBookButton(){
        layout.openBookBtn.visibility = VISIBLE
        layout.downloadBtn.visibility = GONE
        layout.viewCartFrame.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }

   private  fun showViewCartButton(){
       layout.viewCartButton.visibility = VISIBLE
       layout.viewCartFrame.visibility = VISIBLE
       layout.openBookBtn.visibility = GONE
       layout.downloadBtn.visibility = GONE
       layout.addToCartBtn.visibility= GONE
   }

    private fun showLetterIcon(value:String){
        layout.letterIcon.visibility = VISIBLE
        layout.letterIcon.letter = value
        layout.userImage.visibility = GONE
    }

    private fun shareBook(){
        bookItemActivityViewModel.getBookShareLink()?.let {
            startActivity(ShareUtil.getShareIntent(it.toString(), bookOnlineVersion!!.name))
        }
    }


    private fun startBookInfoActivity(isbn: String, title:String, fragmentID:Int){
        val intent = Intent(this, BookInfoActivity::class.java)
        with(intent){
            putExtra(Book.NAME,title)
            putExtra(Fragment.ID, fragmentID)
            putExtra(Book.ID, isbn)
        }
        startActivity(intent)
    }

    private fun loadSimilarBooks(category: String, similarBooksAdapter:SimilarBooksAdapter){
        lifecycleScope.launch {
            bookItemActivityViewModel.
            getBooksByCategoryPageSource(category).collectLatest { similarBooks ->

                similarBooksAdapter.addLoadStateListener { loadState ->
                    val isVisible =
                        !(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && similarBooksAdapter.itemCount < 1)
                    layout.similarBooksCard.isVisible = isVisible
                    layout.similarBooksRecView.isVisible = isVisible
                }

                similarBooksAdapter.submitData(similarBooks)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_item_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.reportBookSubMenu){

            val url = remoteConfig.getString(Config.BOOK_REPORT_URL)
            val intent = Intent(this, WebViewActivity::class.java)
            with(intent){
                putExtra(WebView.TITLE, getString(R.string.report_book))
                putExtra(WebView.URL, url)
            }
            startActivity(intent)
        }else if (item.itemId != R.id.reportBook){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun startBookActivity(){
        val book = orderedBook.get()
        val intent = Intent(this, BookActivity::class.java)
        with(intent){
            putExtra(Book.NAME, book.name)
            putExtra(Book.ID, book.bookId)
        }
        startActivity(intent)
    }


}