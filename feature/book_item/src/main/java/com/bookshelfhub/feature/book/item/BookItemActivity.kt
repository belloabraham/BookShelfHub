package com.bookshelfhub.feature.book.item

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.workDataOf
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.extensions.load
import com.bookshelfhub.feature.webview.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asFlow
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.book.purchase.CartActivity
import com.bookshelfhub.core.common.extensions.escapeJSONSpecialChars
import com.bookshelfhub.core.common.helpers.utils.IconUtil
import com.bookshelfhub.core.common.helpers.utils.Location
import com.bookshelfhub.core.common.helpers.utils.ShareUtil
import com.bookshelfhub.core.common.helpers.utils.datetime.DateFormat
import com.bookshelfhub.core.common.helpers.utils.datetime.DateUtil
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.model.entities.*
import com.bookshelfhub.core.remote.remote_config.RemoteConfig
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.core.ui.views.adapters.DiffUtilItemCallback
import com.bookshelfhub.feature.about.book.BookInfoActivity
import com.bookshelfhub.feature.book.item.adapters.SimilarBooksAdapter
import com.bookshelfhub.feature.book.item.databinding.ActivityBookItemBinding
import com.bookshelfhub.feature.book_reviews.BookReviewsActivity
import com.bookshelfhub.feature.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import androidx.activity.viewModels
import com.bookshelfhub.core.common.helpers.dialog.AlertDialogBuilder
import com.bookshelfhub.core.domain.usecases.LocalFile

@AndroidEntryPoint
class BookItemActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookItemBinding
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var userAuth: IUserAuth

    private lateinit var user:User
    private val bookItemActivityViewModel by viewModels<BookItemActivityViewModel>()

    private var userReview: UserReview?=null
    private var canUserPostReview:Boolean = false
    private lateinit var userId: String
    private lateinit var bookId:String
    private lateinit var onlinePublishedBook: PublishedBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout =  ActivityBookItemBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = null

        bookId = bookItemActivityViewModel.getBookId()

        userId = userAuth.getUserId()
        val userPhotoUri = userAuth.getPhotoUrl()

        lifecycleScope.launch {
            user = bookItemActivityViewModel.getUser()
        }

        bookItemActivityViewModel.getALiveOrderedBook().observe(this) { orderedBook ->
            checkIfBookAlreadyAddedByUser(orderedBook)
        }

        bookItemActivityViewModel.getRemotePublishedBook().observe(this) { onlinePublishedBook ->
            this.onlinePublishedBook = onlinePublishedBook

            lifecycleScope.launch {
                bookItemActivityViewModel.updatePublishedBook(onlinePublishedBook)
            }

            showBookDetails(onlinePublishedBook)

            val bookIsFree = onlinePublishedBook.price <= 0.0
            val bookIsPaid = !bookIsFree

            if (bookIsPaid) {
                showBookPrice(onlinePublishedBook)
                bookItemActivityViewModel.getBookFromCart()
                    .observe(this@BookItemActivity) { bookInCart ->
                        lifecycleScope.launch {
                            val bookHaveNotBeenOrdered =
                                !bookItemActivityViewModel.getAnOrderedBook().isPresent
                            if (bookHaveNotBeenOrdered) {
                                if (bookInCart.isPresent) {
                                    showViewCartButtonAndHideOthers()
                                } else {
                                    showAddToCartButtonAndHideOthers()
                                }
                            }
                        }
                    }
            }

            if (bookIsFree) {
                layout.price.text = getString(R.string.price_free)
                lifecycleScope.launch {
                    val bookIsNotInShelf = !bookItemActivityViewModel.getAnOrderedBook().isPresent
                    if (bookIsNotInShelf) {
                        showAddToShelfButtonAndHideOthers()
                    }
                }
            }

            showBookItemLayout()
        }

        layout.addToShelf.setOnClickListener {
            val countryCode = Location.getCountryCode(applicationContext)
            lifecycleScope.launch {
                val bookIsFree = onlinePublishedBook.price <= 0.0
                if(bookIsFree){
                    addFreeBook(onlinePublishedBook, countryCode!!)
                }
            }
        }

        layout.addToCartBtn.setOnClickListener {
            lifecycleScope.launch {
                val collaboratorId = bookItemActivityViewModel.getCollaboratorIdForThisBook()
                val book = onlinePublishedBook
                val cart = CartItem(
                    userId,
                    book.bookId,
                    book.name,
                    book.author,
                    book.pubId,
                    book.coverDataUrl,
                    collaboratorId,
                    book.price,
                    book.sellerCurrency,
                )

                if(user.additionInfo.isNullOrBlank()){
                    AlertDialogBuilder.with(R.string.include_addition_info_msg, this@BookItemActivity)
                        .setNegativeAction(R.string.no){
                            bookItemActivityViewModel.addToCart(cart)
                            showViewCartButtonAndHideOthers()
                        }
                        .setPositiveAction(R.string.yes){
                            cart.userAdditionalInfo = user.additionInfo
                            bookItemActivityViewModel.addToCart(cart)
                            showViewCartButtonAndHideOthers()
                        }.build().showDialog(R.string.include_addition_info_title)
                }else{
                    bookItemActivityViewModel.addToCart(cart)
                    showViewCartButtonAndHideOthers()
                }
            }
        }

        layout.shareBookBtn.setOnClickListener {
            shareBook()
        }

        layout.downloadBtn.setOnClickListener {

            val workData = workDataOf(
                Book.ID to this.bookId,
                Book.SERIAL_NO to onlinePublishedBook.serialNo.toInt(),
                Book.PUB_ID to onlinePublishedBook.pubId,
                Book.NAME to onlinePublishedBook.name
            )

            bookItemActivityViewModel.startBookDownload(workData)
            layout.downloadProgressBar.visibility = View.VISIBLE
            layout.downloadBtn.visibility = View.GONE
        }


        lifecycleScope.launch {
            bookItemActivityViewModel.getLiveBookDownloadState(bookId).asFlow().collect{

                if(it.isPresent){

                    layout.downloadProgressLayout.visibility = View.VISIBLE
                    val downloadBookState = it.get()
                    val progress = downloadBookState.progress

                    layout.downloadProgressBar.progress = progress
                    layout.downloadProgressTxt.text = getString(R.string.downloading)

                    if(downloadBookState.hasError){
                        layout.downloadBtn.visibility = View.VISIBLE
                        layout.downloadProgressTxt.text = String.format(getString(R.string.unable_to_download_book))
                    }

                    if(progress >= 100){
                        layout.downloadProgressTxt.text = getString(R.string.download_complete)
                        val totalDownloads = onlinePublishedBook.totalDownloads + 1
                        layout.noOfDownloadsText.text = "$totalDownloads"
                        //val orderedBook = bookItemActivityViewModel.getAnOrderedBook()
                        //checkIfBookAlreadyAddedByUser(orderedBook)
                        showOpenBookButtonAndHideOthers()
                        layout.downloadProgressLayout.visibility = View.GONE
                        bookItemActivityViewModel.deleteDownloadState(downloadBookState)
                    }
                }
            }
        }

        layout.openBookBtn.setOnClickListener {
            startBookActivity()
        }

        layout.aboutBookCard.setOnClickListener {
            startBookInfoActivity(bookId,  title = onlinePublishedBook.name)
        }

        layout.similarBooksCard.setOnClickListener {
            val intent = Intent(this, SimilarBooksActivity::class.java)
            intent.putExtra(SimilarBooks.CATEGORY, onlinePublishedBook.category)
            startActivity(intent)
        }

        layout.allReviewsBtn.setOnClickListener {
            val intent = Intent(this, BookReviewsActivity::class.java)
            intent.putExtra(Book.ID, bookId)
            startActivity(intent)
        }

        layout.viewCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }


        layout.ratingBar.setOnRatingChangeListener { _, rating ->
            bookItemActivityViewModel.rating = rating
            val userRated = rating > 0
            layout.ratingInfoLayout.isVisible = userRated
        }

        layout.postBtn.setOnClickListener {

            lifecycleScope.launch {

                val newRating = layout.ratingBar.rating.toDouble()

                if(userReview == null){
                    val totalRatings = onlinePublishedBook.totalRatings + newRating
                    val totalReviews = onlinePublishedBook.totalReviews + 1
                    val rating =  totalRatings/totalReviews
                    layout.noRatingTxt.text = "$rating"
                    layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), totalReviews)
                }

                bookItemActivityViewModel.review = layout.userReviewEditText.text.toString().escapeJSONSpecialChars()
                val userName = user.firstName

                var ratingDiff = newRating
                var postedBefore = false
                userReview?.let {
                    ratingDiff = newRating - it.userRating
                    postedBefore = it.postedBefore
                }

                val newReview = UserReview(
                    bookId,
                    bookItemActivityViewModel.review,
                    newRating, userName,
                    canUserPostReview,
                    userPhotoUri,
                    postedBefore
                )
                bookItemActivityViewModel.addUserReview(newReview, ratingDiff)
            }

        }

        layout.userReviewEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    layout.reviewLengthTxt.text  = String.format(getString(R.string.reviewtextLength), it.length)
                    bookItemActivityViewModel.review = it.toString()
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })


        layout.editYourReviewBtn.setOnClickListener {
            layout.yourReviewLayout.visibility = View.GONE
            layout.ratingInfoLayout.visibility = View.VISIBLE
            layout.rateBookLayout.visibility = View.VISIBLE

        }

        bookItemActivityViewModel.getTwoUserReviewsForBook().observe(this) { reviews ->

            if (reviews.isNotEmpty()) {
                layout.ratingsAndReviewLayout.visibility = View.VISIBLE
                val reviewsAdapter = com.bookshelfhub.feature.book_reviews.adapters.UserReviewListAdapter().getAdapter()
                layout.reviewRecView.adapter = reviewsAdapter
                reviewsAdapter.submitList(reviews)
            }

        }

        bookItemActivityViewModel.getLiveUserReview().observe(this) { review ->

            if (review.isPresent) {
                layout.ratingInfoLayout.visibility = View.VISIBLE
                layout.yourReviewLayout.visibility = View.VISIBLE
                layout.rateBookLayout.visibility = View.GONE

                review.get().let { userReview ->
                    this.userReview = userReview
                    layout.ratingBar.rating = userReview.userRating.toFloat()

                    userReview.dateTime?.let {
                        val localDate = DateUtil.getHumanReadable(
                            it.toDate(),
                            DateFormat.DD_MM_YYYY.completeFormatValue
                        )
                        layout.date.text = localDate
                    }

                    layout.userNameText.text = userReview.userName
                    layout.userReviewTxt.text = userReview.review
                    layout.userRatingBar.rating = userReview.userRating.toFloat()
                    layout.userReviewEditText.setText(userReview.review)

                    layout.userReviewTxt.isVisible = userReview.review.isNotBlank()
                    setUserReviewProfilePhoto(userPhotoUri, userReview.userName)
                }

            } else {
                layout.rateBookLayout.visibility = View.VISIBLE
            }

            showUnsavedUserReviewBeforeActivityRestart()

            val reviewLength = layout.userReviewEditText.text.toString().length

            layout.reviewLengthTxt.text = String.format(getString(R.string.reviewtextLength), reviewLength)

        }
    }

    private fun showUnsavedUserReviewBeforeActivityRestart(){
        val userWroteAnUnsavedReviewBeforeActivityRestarted = bookItemActivityViewModel.rating > 0
        if(userWroteAnUnsavedReviewBeforeActivityRestarted){
            layout.ratingBar.rating = bookItemActivityViewModel.rating
            layout.userReviewEditText.setText(bookItemActivityViewModel.review)
        }
    }


    private fun setUserReviewProfilePhoto(userPhotoUri:String?, userName:String){
        if (userPhotoUri!=null){
            layout.letterIcon.visibility = View.GONE
            layout.userImage.visibility = View.VISIBLE
            layout.userImage.load(userPhotoUri){
                showLetterIcon(userName)
            }
        }else{
            showLetterIcon(userName)
        }
    }

    private suspend fun addFreeBook(book:PublishedBook, countryCode:String){
        var serialNo =  bookItemActivityViewModel.getTotalNoOfOrderedBooks().toLong()
        val additionalInfo = user.additionInfo
        val orderedBook = OrderedBook(
            book.bookId,
            userId,
            book.name,
            book.coverDataUrl,
            book.pubId,
            book.sellerCurrency,
            countryCode,
            null,
            null,
            ++serialNo,
            additionalInfo,
            null,
            null,
            0.0
        )
        bookItemActivityViewModel.addAFreeOrderedBook(orderedBook)
    }

    private fun checkIfBookAlreadyAddedByUser(orderedBook: Optional<OrderedBook>){

        val bookAlreadyPurchasedByUser = orderedBook.isPresent

        if (bookAlreadyPurchasedByUser){
            canUserPostReview = true
            val book = orderedBook.get()

            val bookFile = LocalFile.getBookFile(book.bookId, book.pubId, this)

            val bookAlreadyDownloadedByUser = bookFile.exists()

            if(bookAlreadyDownloadedByUser){
                showOpenBookButtonAndHideOthers()
            }else{
                showDownloadBookButtonAndHideOthers()
            }
        }
    }

    private fun showBookPrice(book: PublishedBook){
        layout.price.text = String.format(getString(R.string.local_price), book.sellerCurrency, book.price)
    }

    private fun showBookDetails(book: PublishedBook){

        layout.title.text = book.name
        layout.author.text = String.format(getString(R.string.by), book.author)

        layout.cover.setImageBitmap(IconUtil.getBitmap(book.coverDataUrl))

        val similarBooksAdapter = SimilarBooksAdapter(this, DiffUtilItemCallback())

        layout.similarBooksRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        layout.similarBooksRecView.adapter = similarBooksAdapter

        layout.noOfReviewTxt.text = String.format(getString(R.string.review_no), book.totalReviews)

        val rating =  if(book.totalRatings == 0f){
            layout.rateLabel.text = getString(R.string.first_to_rate_book)
            0
        } else{
            book.totalRatings/book.totalReviews
        }

        layout.noRatingTxt.text = "$rating"

        layout.descText.text = book.description

        layout.noOfDownloadsText.text = Downloads.getHumanReadable(book.totalDownloads)

        loadSimilarBooks(book.category, excludedBookId = book.bookId, similarBooksAdapter)
    }

    private fun showBookItemLayout(){
        layout.progressBar.visibility = View.GONE
        layout.bookItemLayout.visibility = View.VISIBLE
        layout.bottomLayout.visibility = View.VISIBLE
    }

    private fun showAddToCartButtonAndHideOthers(){
        layout.openBookBtn.visibility = View.GONE
        layout.downloadBtn.visibility = View.GONE
        layout.viewCartButton.visibility = View.GONE
        layout.addToCartBtn.visibility= View.VISIBLE
    }

    private fun showAddToShelfButtonAndHideOthers(){
        layout.addToShelf.visibility = View.VISIBLE
        layout.openBookBtn.visibility = View.GONE
        layout.downloadBtn.visibility = View.GONE
        layout.viewCartButton.visibility = View.GONE
        layout.addToCartBtn.visibility= View.GONE
    }

    private fun showDownloadBookButtonAndHideOthers(){
        layout.openBookBtn.visibility = View.GONE
        layout.addToShelf.visibility = View.GONE
        layout.downloadBtn.visibility = View.VISIBLE
        layout.viewCartButton.visibility = View.GONE
        layout.addToCartBtn.visibility= View.GONE
    }

    private fun showOpenBookButtonAndHideOthers(){
        layout.openBookBtn.visibility = View.VISIBLE
        layout.downloadBtn.visibility = View.GONE
        layout.addToShelf.visibility = View.GONE
        layout.viewCartButton.visibility = View.GONE
        layout.addToCartBtn.visibility= View.GONE
    }

    private  fun showViewCartButtonAndHideOthers(){
        layout.viewCartButton.visibility = View.VISIBLE
        layout.addToShelf.visibility = View.GONE
        layout.openBookBtn.visibility = View.GONE
        layout.downloadBtn.visibility = View.GONE
        layout.addToCartBtn.visibility= View.GONE
    }

    private fun showLetterIcon(value:String){
        layout.letterIcon.visibility = View.VISIBLE
        layout.letterIcon.letter = value
        layout.userImage.visibility = View.GONE
    }

    private fun shareBook(){
        bookItemActivityViewModel.getBookShareLink()?.let {
            startActivity(ShareUtil.getShareIntent(it, onlinePublishedBook.name))
        }
    }

    private fun startBookInfoActivity(bookId: String, title:String){
        val intent = Intent(this, BookInfoActivity::class.java)
        with(intent){
            putExtra(Book.NAME, title)
            putExtra(Book.ID, bookId)
        }
        startActivity(intent)
    }

    private fun loadSimilarBooks(category: String, excludedBookId:String, similarBooksAdapter: SimilarBooksAdapter){
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

            val url = remoteConfig.getString(RemoteConfig.BOOK_REPORT_URL)
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
            val intent = Intent(this@BookItemActivity, BookActivity::class.java)
            with(intent){
                putExtra(Book.NAME, book.name)
                putExtra(Book.ID, book.bookId)
            }
            startActivity(intent)
        }

    }
}