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
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.workDataOf
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateFormat
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateUtil
import com.bookshelfhub.bookshelfhub.adapters.paging.DiffUtilItemCallback
import com.bookshelfhub.bookshelfhub.adapters.paging.SimilarBooksAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.UserReviewListAdapter
import com.bookshelfhub.bookshelfhub.data.*
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.helpers.AppExternalStorage
import com.bookshelfhub.bookshelfhub.helpers.payment.Currency
import com.bookshelfhub.bookshelfhub.data.models.entities.OrderedBook
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.utils.*
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.google.common.base.Optional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private var priceInUSD:Double = 0.0 // I left this variable just in case there will be a need for it in the future for global transaction in USD
    private lateinit var userId: String
    private lateinit var bookId:String
    private lateinit var localCurrencyOrUSD:String
    private lateinit var onlinePublishedBook: PublishedBook
    private val countryCode = Location.getCountryCode(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout =  ActivityBookItemBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = null

        bookId = bookItemActivityViewModel.getBookId()

        userId = userAuth.getUserId()
        val userPhotoUri = userAuth.getPhotoUrl()

        bookItemActivityViewModel.getALiveOrderedBook().observe(this, Observer { orderedBook ->
                checkIfBookAlreadyAddedByUser(orderedBook)
        })

        bookItemActivityViewModel.getOnlinePublishedBook().observe(this, Observer { onlinePublishedBook ->
            this.onlinePublishedBook = onlinePublishedBook
            lifecycleScope.launch {
                bookItemActivityViewModel.updatePublishedBook(onlinePublishedBook)
            }

            showBookDetails(onlinePublishedBook)

            if(countryCode != null){

                val bookIsFree = onlinePublishedBook.price <= 0.0
                val bookIsPaid  = !bookIsFree

                localCurrencyOrUSD = Currency.getLocalCurrencyOrUSD(countryCode)

                if (bookIsPaid) {
                    showBookPrice(onlinePublishedBook, localCurrencyOrUSD)
                    bookItemActivityViewModel.getBookFromCart().observe(this@BookItemActivity, Observer { bookInCart ->
                        lifecycleScope.launch {
                            val bookHaveNotBeenOrdered = !bookItemActivityViewModel.getAnOrderedBook().isPresent
                            if(bookHaveNotBeenOrdered){
                                if(bookInCart.isPresent){
                                    showViewCartButtonAndHideOthers()
                                }else{
                                    showAddToCartButtonAndHideOthers()
                                }
                            }
                        }
                    })
                }

               if(bookIsFree){
                   layout.price.text = getString(R.string.price_free)
                   showAddToShelfButtonAndHideOthers()
               }

               showBooksItemLayout()

            }
        })

        layout.addToShelf.setOnClickListener {
            lifecycleScope.launch {
                val bookIsFree = onlinePublishedBook.price <= 0.0
                if(bookIsFree){
                    addFreeBook(onlinePublishedBook, countryCode!!)
                }
            }
        }

        layout.addToCartBtn.setOnClickListener {
            lifecycleScope.launch {
                    val collaboratorId: String? =
                        bookItemActivityViewModel.getCollaboratorIdForThisBook()
                    val book = onlinePublishedBook
                    val cart = CartItem(
                        userId, book.bookId,
                        book.name,
                        book.author,
                        book.pubId,
                        book.coverUrl,
                        collaboratorId,
                        book.price,
                        localCurrencyOrUSD,
                        priceInUSD
                    )
                    bookItemActivityViewModel.addToCart(cart)
                    showViewCartButtonAndHideOthers()
            }
        }

        layout.shareBookBtn.setOnClickListener {
            shareBook()
        }

        layout.downloadBtn.setOnClickListener {

                val workData = workDataOf(
                    Book.ID to bookItemActivityViewModel.getBookIdFromPossiblyMergedIds(this.bookId),
                    Book.SERIAL_NO to onlinePublishedBook.serialNo.toInt(),
                    Book.PUB_ID to onlinePublishedBook.pubId,
                    Book.NAME to onlinePublishedBook.name
                )

                bookItemActivityViewModel.startBookDownload(workData)
                layout.downloadProgressBar.visibility = VISIBLE
                layout.downloadBtn.visibility = GONE
        }


        lifecycleScope.launch {
            bookItemActivityViewModel.getLiveBookDownloadState(
                bookItemActivityViewModel.getBookIdFromPossiblyMergedIds(bookId)
            ).asFlow().collect{
                if(it.isPresent){

                    layout.downloadProgressLayout.visibility = VISIBLE
                    val downloadBookState = it.get()
                    val progress = downloadBookState.progress

                    layout.downloadProgressBar.progress = progress
                    layout.downloadProgressTxt.text = getString(R.string.downloading)

                    if(downloadBookState.hasError){
                        layout.downloadBtn.visibility = VISIBLE
                        layout.downloadProgressTxt.text = String.format(getString(R.string.unable_to_download_book))
                    }

                    if(progress>=100){
                        layout.downloadProgressTxt.text = getString(R.string.download_complete)
                        layout.downloadProgressLayout.visibility = GONE

                        val orderedBook = bookItemActivityViewModel.getAnOrderedBook()
                        checkIfBookAlreadyAddedByUser(orderedBook)
                        layout.downloadBtn.visibility = VISIBLE
                        bookItemActivityViewModel.deleteDownloadState(downloadBookState)
                    }
                }
            }
        }

        layout.openBookBtn.setOnClickListener {
            startBookActivity()
        }

        layout.aboutBookCard.setOnClickListener {
            startBookInfoActivity(bookId,  title = onlinePublishedBook.name, R.id.bookInfoFragment)
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, BookCategoryActivity::class.java)
            intent.putExtra(Category.TITLE, onlinePublishedBook.category)
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
            layout.ratingInfoLayout.visibility = VISIBLE
           layout.rateBookLayout.visibility = VISIBLE

        }

        bookItemActivityViewModel.getTwoUserReviewsForBook().observe(this, Observer { reviews ->

            if (reviews.isNotEmpty()){
                layout.ratingsAndReviewLayout.visibility = VISIBLE
                val reviewsAdapter = UserReviewListAdapter().getAdapter()
                layout.reviewRecView.adapter = reviewsAdapter
                reviewsAdapter.submitList(reviews)
            }

        })

        bookItemActivityViewModel.getLiveUserReview().observe(this, Observer { review ->

            if (review.isPresent){
                layout.ratingInfoLayout.visibility = VISIBLE
                layout.yourReviewLayout.visibility = VISIBLE
                layout.rateBookLayout.visibility = GONE

                review.get().let { userReview ->
                    layout.ratingBar.rating = userReview.userRating.toFloat()

                    userReview.dateTime?.let {
                        val  localDate = DateUtil.getHumanReadable(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                        layout.date.text = localDate
                    }

                    layout.userNameText.text = userReview.userName
                    layout.userReviewTxt.text = userReview.review
                    layout.userRatingBar.rating = userReview.userRating.toFloat()
                    layout.userReviewEditText.setText(userReview.review)

                    layout.userReviewTxt.isVisible = userReview.review.isNotBlank()
                    setUserReviewProfilePhoto(userPhotoUri, userReview.userName)
                }
            }else{
                layout.rateBookLayout.visibility = VISIBLE
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

    private suspend fun addFreeBook(book:PublishedBook, countryCode:String){
            val serialNo = bookItemActivityViewModel.getTotalNoOfOrderedBooks()
            val additionalInfo = bookItemActivityViewModel.getUser().additionInfo
            val orderedBook = OrderedBook(book.bookId,
                0.0, userId,
                book.name, book.coverUrl,
                book.pubId, countryCode,
                getString(R.string.app_name),
                null,
                DateTimeUtil.getMonth(),
                DateTimeUtil.getYear(),
                serialNo.toLong(),
                additionalInfo,
                null, null, 0.0
            )
            bookItemActivityViewModel.addAnOrderedBook(orderedBook)
    }

    private fun checkIfBookAlreadyAddedByUser(orderedBook: Optional<OrderedBook>){

            val bookAlreadyPurchasedByUser = orderedBook.isPresent

            if (bookAlreadyPurchasedByUser){
                val bookId = bookItemActivityViewModel.getBookIdFromPossiblyMergedIds(this.bookId)
                canUserPostReview = true

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

    private fun showBookPrice(book: PublishedBook, buyerVisibleCurrency:String){
        layout.price.text = String.format(getString(R.string.local_price), buyerVisibleCurrency, book.price)
    }

    private fun showBookDetails(book: PublishedBook){

        layout.title.text = book.name
        layout.author.text = String.format(getString(R.string.by), book.author)

        layout.cover.setImageBitmap(IconUtil.getBitmap(book.coverUrl))

        val similarBooksAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())

        layout.similarBooksRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        layout.similarBooksRecView.adapter = similarBooksAdapter

        layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), book.totalReviews)

       val rating =  if(book.totalRatings == 0f) 0 else book.totalRatings/book.totalReviews

        layout.noRatingTxt.text = "$rating"

        layout.descText.text = book.description

        layout.noOfDownloadsText.text = Downloads.getHumanReadable(book.totalDownloads)

        loadSimilarBooks(book.category, excludedBookId = book.bookId, similarBooksAdapter)
    }

    private fun showBooksItemLayout(){
            layout.progressBar.visibility = GONE
            layout.bookItemLayout.visibility = VISIBLE
    }


    private fun showAddToCartButtonAndHideOthers(){
        layout.openBookBtn.visibility = GONE
        layout.downloadBtn.visibility = GONE
        layout.viewCartButton.visibility = GONE
        layout.addToCartBtn.visibility= VISIBLE
    }

    private fun showAddToShelfButtonAndHideOthers(){
        layout.addToShelf.visibility = VISIBLE
        layout.openBookBtn.visibility = GONE
        layout.downloadBtn.visibility = GONE
        layout.viewCartButton.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }


    private fun showDownloadBookButtonAndHideOthers(){
        layout.openBookBtn.visibility = GONE
        layout.addToShelf.visibility = GONE
        layout.downloadBtn.visibility = VISIBLE
        layout.viewCartButton.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }

    private fun showOpenBookButtonAndHideOthers(){
        layout.openBookBtn.visibility = VISIBLE
        layout.downloadBtn.visibility = GONE
        layout.addToShelf.visibility = GONE
        layout.viewCartButton.visibility = GONE
        layout.addToCartBtn.visibility= GONE
    }

   private  fun showViewCartButtonAndHideOthers(){
       layout.viewCartButton.visibility = VISIBLE
       layout.addToShelf.visibility = GONE
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
            startActivity(ShareUtil.getShareIntent(it, onlinePublishedBook.name))
        }
    }


    private fun startBookInfoActivity(bookId: String, title:String, fragmentID:Int){
        val intent = Intent(this, BookInfoActivity::class.java)
        with(intent){
            putExtra(Book.NAME, title)
            putExtra(Fragment.ID, fragmentID)
            putExtra(Book.ID, bookId)
        }
        startActivity(intent)
    }

    private fun loadSimilarBooks(category: String, excludedBookId:String, similarBooksAdapter:SimilarBooksAdapter){
        lifecycleScope.launch {
            bookItemActivityViewModel.
            getSimilarBooksByCategoryPageSource(category, excludedBookId).collectLatest { similarBooks ->

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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