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
import com.bookshelfhub.bookshelfhub.helpers.webapi.currencyconverter.Currency
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.helpers.utils.*
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.google.common.base.Optional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private var canUserPostReview:Boolean = false
    private var priceInUSD:Double = 0.0
    private var visibilityAnimDuration:Long=0
    private lateinit var userId: String
    private lateinit var bookId:String
    private lateinit var localCurrencyOrUSD:String
    private lateinit var localPublishedBook: PublishedBook

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

        bookItemActivityViewModel.getALiveOrderedBook().observe(this, Observer { orderedBook ->
           checkIfBookAlreadyPurchasedByUser(orderedBook)
        })

        bookItemActivityViewModel.getLiveListOfCartItems().observe(this, Observer { cartItems ->
            checkIfBookIsAlreadyInCart(cartItems)
        })

        lifecycleScope.launch {
            localPublishedBook = bookItemActivityViewModel.getLocalPublishedBook().get()
            showBookDetails(localPublishedBook)
        }

        bookItemActivityViewModel.getBookRemotelyIfNotPurchased().observe(this, Observer { book ->

            val countryCode = Location.getCountryCode(this)

            if(countryCode != null){

                val bookIsFree = book.price <= 0.0
                val bookIsPaid  = !bookIsFree

                localCurrencyOrUSD = Currency.getLocalCurrencyOrUSD(countryCode)

                if (bookIsPaid) {
                    showAddToCartButtonAndHideOthers()

                    val localCurrencyIsNotUSD = localCurrencyOrUSD != Currency.USD

                    if (localCurrencyIsNotUSD){
                        lifecycleScope.launch {
                            try {
                                val response =  bookItemActivityViewModel.convertCurrency(
                                    fromCurrency =  localCurrencyOrUSD,
                                    toCurrency = Currency.USD, amount = book.price)

                                if(response.isSuccessful && response.body()!=null){
                                    priceInUSD = response.body()!!.info.rate*book.price
                                    showBookPrice(book, localCurrencyOrUSD)
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

                    val localCurrencyIsUSD = !localCurrencyIsNotUSD
                    if(localCurrencyIsUSD){
                        priceInUSD = book.price
                        showBookPrice(book, localCurrencyOrUSD)
                    }
                }

                if(bookIsFree){
                    priceInUSD = book.price
                    layout.price.text = getString(R.string.price_free)
                    addAFreeBook(book, countryCode)
                    showBooksItemLayout()
                }

            }

        })

        layout.addToCartBtn.setOnClickListener {

            lifecycleScope.launch {
                val userHaveNotPurchasedBookBefore = !bookItemActivityViewModel.getBookAlreadyPurchasedByUser()
                if (userHaveNotPurchasedBookBefore) {
                    val collaboratorId: String? =
                        bookItemActivityViewModel.getCollaboratorIdForThisBook()
                    val book = localPublishedBook
                    val cart = CartItem(
                        userId, book.bookId,
                        book.name,
                        book.author,
                        book.pubId,
                        book.coverUrl,
                        collaboratorId,
                        book.price,
                        localCurrencyOrUSD, priceInUSD
                    )
                    bookItemActivityViewModel.addToCart(cart)
                }
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
            lifecycleScope.launch {
                startBookInfoActivity(bookId,  title = localPublishedBook.name, R.id.bookInfoFragment)
            }
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, BookCategoryActivity::class.java)
            intent.putExtra(Category.TITLE, localPublishedBook.category)
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

            val newReview = UserReview(bookId, review, newRating, userName, canUserPostReview, userPhotoUri, postedBefore)

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

    private fun addAFreeBook(book:PublishedBook, countryCode:String){
        lifecycleScope.launch {
            val serialNo = bookItemActivityViewModel.getAllOrderedBooks().size
            val additionalInfo = bookItemActivityViewModel.getUser().additionInfo
            val orderedBook = OrderedBook(book.bookId,
                0.0, userId,
                book.name, book.coverUrl,
                book.pubId, null,
                countryCode,null,
                getString(R.string.app_name), null,
                DateTimeUtil.getMonth(), DateTimeUtil.getYear(), serialNo.toLong(),
                additionalInfo
            )
            bookItemActivityViewModel.addAnOrderedBook(orderedBook)
        }
    }


    private fun checkIfBookAlreadyPurchasedByUser(orderedBook: Optional<OrderedBook>){

        val bookId = bookItemActivityViewModel.getBookIdFromPossiblyMergedIds(this.bookId)
        val bookAlreadyPurchasedByUser = bookItemActivityViewModel.getBookAlreadyPurchasedByUser()

        canUserPostReview = bookAlreadyPurchasedByUser

        if (bookAlreadyPurchasedByUser){
            val book = orderedBook.get()
            val fileNameWithExt = "$bookId${FileExtension.DOT_PDF}"

           val bookAlreadyDownloadedByUser =  AppExternalStorage.getDocumentFilePath(
                book.pubId,
                bookId,
                fileNameWithExt, applicationContext).exists()

            if(bookAlreadyDownloadedByUser){
                showOpenBookButtonAndHideOthers()
            }else{
                showDownloadBookButtonAndHideOthers()
            }

            showBooksItemLayout()
        }
    }

    private fun checkIfBookIsAlreadyInCart(cartItems:List<CartItem>){
        if(cartItems.isNotEmpty()){
            layout.checkoutNotifText.text = "${cartItems.size}"

           val bookInCart = cartItems.filter {
                it.bookId == bookId
            }

            if (bookInCart.isNotEmpty()){
                showViewCartButtonAndHideOthers()
            }
        }
    }

    private fun showBookPrice(book: PublishedBook, buyerVisibleCurrency:String){
        layout.price.text = if(book.price == priceInUSD) String.format(getString(R.string.usd_price), book.price)
        else String.format(getString(R.string.local_price_and_usd), buyerVisibleCurrency,book.price, priceInUSD)

        showBooksItemLayout()
    }

    private fun showBookDetails(book: PublishedBook){

        layout.title.text = book.name
        layout.author.text = String.format(getString(R.string.by), book.author)

        layout.cover.setImageBitmap(IconUtil.getBitmap(book.coverUrl))

        val similarBooksAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())

        layout.similarBooksRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        layout.similarBooksRecView.adapter = similarBooksAdapter

        layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), book.totalReviews)

        val rating = book.totalRatings/book.totalReviews
        layout.noRatingTxt.text = "$rating"
        layout.noOfDownloadsText.text = Downloads.getHumanReadable(book.totalDownloads)

        loadSimilarBooks(book.category, similarBooksAdapter)
    }

    private fun showBooksItemLayout(){
        layout.progressBar.visibility = GONE
        layout.bookItemLayout.visibility = VISIBLE
    }

    private fun showAddToCartButtonAndHideOthers(){
        layout.openBookBtn.visibility = GONE
        layout.downloadBtn.visibility = GONE
        layout.viewCartFrame.visibility = GONE
        layout.addToCartBtn.visibility= VISIBLE
    }

    private fun showDownloadBookButtonAndHideOthers(){
        layout.openBookBtn.visibility = GONE
        layout.downloadBtn.visibility = VISIBLE
        layout.viewCartFrame.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }

    private fun showOpenBookButtonAndHideOthers(){
        layout.openBookBtn.visibility = VISIBLE
        layout.downloadBtn.visibility = GONE
        layout.viewCartFrame.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }

   private  fun showViewCartButtonAndHideOthers(){
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
            startActivity(ShareUtil.getShareIntent(it, localPublishedBook.name))
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
        lifecycleScope.launch {
            val book = bookItemActivityViewModel.getOptionalOrderedBook(bookId).get()
            val bookId = bookItemActivityViewModel.getBookIdFromPossiblyMergedIds(book.bookId)
            val intent = Intent(this@BookItemActivity, BookActivity::class.java)
            with(intent){
                putExtra(Book.NAME, book.name)
                putExtra(Book.ID, bookId)
            }
            startActivity(intent)
        }

    }


}