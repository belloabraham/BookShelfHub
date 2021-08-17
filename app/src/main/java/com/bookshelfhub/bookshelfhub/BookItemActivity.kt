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
import com.bookshelfhub.bookshelfhub.adapters.paging.DiffUtilItemCallback
import com.bookshelfhub.bookshelfhub.adapters.paging.SimilarBooksAdapter
import com.bookshelfhub.bookshelfhub.config.IRemoteConfig
import com.bookshelfhub.bookshelfhub.const.Regex
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.enums.*
import com.bookshelfhub.bookshelfhub.extensions.containsUrl
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PublishedBooks
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.PostUserReview
import com.bookshelfhub.bookshelfhub.wrappers.Json
import com.bookshelfhub.bookshelfhub.wrappers.dynamiclink.IDynamicLink
import com.google.firebase.firestore.ktx.toObject
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

          layout =  ActivityBookItemBinding.inflate(layoutInflater)
          setContentView(layout.root)
          setSupportActionBar(layout.toolbar)
          supportActionBar?.title = null

        val userId = userAuth.getUserId()
        val userPhotoUri = userAuth.getUserPhotoUrl()

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

               bookItemViewModel.getLiveListOfCartItems().observe(this@BookItemActivity, Observer { cartItems ->
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

                   bookItemViewModel.
                   getBooksByCategoryPageSource(book.category).collectLatest { similarBooks ->

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

            var  link:String?
            lifecycleScope.launch {
                  link =  settingsUtil.getString(PubReferrer.USER_REF_LINK.KEY)

                    if (link==null){
                        dynamicLink.getLinkAsync(
                            remoteConfig.getString(UserReferrer.USER_REF_TITLE.KEY),
                            remoteConfig.getString(UserReferrer.USER_REF_DESC.KEY),
                            remoteConfig.getString(UserReferrer.USER_REF_IMAGE_URI.KEY),
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

        layout.ratingsAndReviewCard.setOnClickListener {
            startBookInfoActivity(isbn)
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

        layout.postBtn.setOnClickListener {

            val review = layout.userReviewEditText.text.toString()
            val rating = layout.ratingBar.rating

            val userName = userAuth.getName()!!

            val userReview = UserReview(isbn, review, rating, userName)


             lifecycleScope.launch {
                    localDb.addUserReview(userReview)
                    if (!review.containsUrl(Regex.URL_IN_TEXT)){
                        val data = Data.Builder()
                        data.putString(Book.ISBN.KEY, isbn)
                        val userReviewPostWorker =
                            OneTimeWorkRequestBuilder<PostUserReview>()
                                .setConstraints(Constraint.getConnected())
                                .setInputData(data.build())
                                .build()
                        WorkManager.getInstance(applicationContext).enqueue(userReviewPostWorker)
                    }
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

        cloudDb.getListOfDataAsync(DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.REVIEWS.KEY, UserReview::class.java, 3){


        }


        bookItemViewModel.getLiveUserReview().observe(this, Observer { review ->

            layout.ratingInfoLayout.visibility = GONE

            if (review.isPresent){
                AnimUtil(this).crossFade(layout.yourReviewLayout, layout.rateBookLayout, animDuration)
                val userReview = review.get()
                layout.ratingBar.rating = userReview.userRating

                layout.userNameText.text = userReview.userName
                layout.userReviewTxt.text = userReview.review
                layout.userRatingBar.rating = userReview.userRating
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
        Share(this).shareText(text)
    }

  private fun getUserReviewAsync(isbn:String, userId:String, animDuration:Long){
        cloudDb.getLiveDataAsync(DbFields.PUBLISHED_BOOKS.KEY,isbn, DbFields.REVIEWS.KEY, userId){ documentSnapshot, _ ->
            documentSnapshot?.let {
                if (it.exists()){
                    val doc = it.toObject<Any>()
                    val userReview = json.fromAny(doc!!, UserReview::class.java)
                    lifecycleScope.launch {
                        localDb.addUserReview(userReview)
                    }
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_item_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val url = remoteConfig.getString(BOOK_REPORT_URL)
        val intent = Intent(this, WebViewActivity::class.java)
        with(intent){
            putExtra(WebView.TITLE.KEY, getString(R.string.report_book))
            putExtra(WebView.URL.KEY, url)
        }
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}