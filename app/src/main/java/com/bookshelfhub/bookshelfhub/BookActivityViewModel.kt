package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.bookshelfhub.data.repos.bookvideos.IBookVideosRepo
import com.bookshelfhub.bookshelfhub.data.repos.orderedbooks.IOrderedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.publishedbooks.IPublishedBooksRepo
import com.bookshelfhub.bookshelfhub.data.repos.readhistory.IReadHistoryRepo
import com.bookshelfhub.bookshelfhub.data.repos.searchhistory.ISearchHistoryRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.utils.ConnectionUtil
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
    val userAuth: IUserAuth,
    private val settingsUtil: SettingsUtil,
    val savedState: SavedStateHandle,
    private val connectionUtil: ConnectionUtil,
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val dynamicLink:IDynamicLink,
    private val publishedBooksRepo: IPublishedBooksRepo,
    private val readHistoryRepo: IReadHistoryRepo,
    private val searchHistoryRepo: ISearchHistoryRepo,
    private val bookmarksRepo: IBookmarksRepo,
    private val bookVideosRepo: IBookVideosRepo,
) : ViewModel() {

    val userId = userAuth.getUserId()
    private var bookId = savedState.get<String>(Book.ID)!!
    private var bookName = savedState.get<String>(Book.NAME)!!
    private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM) ?: false
    private var liveOrderedBook: LiveData<OrderedBook> = MutableLiveData()
    private var readHistory: LiveData<Optional<ReadHistory>> = MutableLiveData()
    private var bookShareLink:String?=null
    private lateinit var book:PublishedBook


    private var livePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()
    private lateinit var orderedBook: OrderedBook

    init {


        viewModelScope.launch {
            bookShareLink = settingsUtil.getString(bookId)

            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP, true)
            if (showPopup) {
                val firstRecordInTheDatabase = 0
                readHistory = readHistoryRepo.getLiveReadHistory(firstRecordInTheDatabase)
            }
            book = publishedBooksRepo.getPublishedBook(bookId).get()
        }


        livePublishedBook = publishedBooksRepo.getALiveOptionalPublishedBook(bookId)
        loadLiveOrderedBook(bookId, bookName)
        viewModelScope.launch {
            orderedBook = orderedBooksRepo.getAnOrderedBook(bookId)
        }

        if (isSearchItem) {
            addShelfSearchHistory(
                ShelfSearchHistory(
                    bookId,
                    bookName,
                    userId,
                    DateTimeUtil.getDateTimeAsString()
                )
            )
        }
    }

     fun getBookVideos(){
        viewModelScope.launch {
            if(connectionUtil.isConnected()){
                try {
                    val bookVideos =  bookVideosRepo.getRemoteBookVideos(bookId)
                    if(bookVideos.isNotEmpty()){
                        bookVideosRepo.addBookVideos(bookVideos)
                    }
                }catch (e:Exception){
                    Timber.e(e)
                    return@launch
                }
            }
        }
    }

    fun loadLiveOrderedBook(isbn: String, bookName: String) {
        this.bookId = isbn
        this.bookName = bookName
        liveOrderedBook = orderedBooksRepo.getLiveOrderedBook(isbn)
    }

    fun generateBookShareLink(){
        val shouldGenerateBookShareUrl = connectionUtil.isConnected() && bookShareLink == null
        viewModelScope.launch {
            if(shouldGenerateBookShareUrl){
                try {
                    bookShareLink = dynamicLink.generateShortDynamicLinkAsync(book.name , book.description, book.coverUrl, userId).toString()
                    settingsUtil.setString(bookId, bookShareLink!!.toString())
                }catch (e:Exception){
                    Timber.e(e)
                    return@launch
                }
            }
        }
    }

    fun getBookShareLink(): String? {
        return bookShareLink
    }

    fun getLiveOrderedBook(): LiveData<OrderedBook> {
        return liveOrderedBook
    }

    fun getIsbnNo(): String {
        return bookId
    }

    fun getBookName(): String {
        return bookName
    }

    fun deleteFromBookmark(pageNumb: Int) {
        viewModelScope.launch {
            bookmarksRepo.deleteFromBookmark(pageNumb, bookId)
        }
    }

    suspend fun getBookmark(currentPage: Int): Optional<Bookmark> {
        return bookmarksRepo.getBookmark(currentPage, bookId)
    }

    fun addBookmark(pageNumber: Int) {
        val bookmark = Bookmark(userId, bookId, pageNumber, bookName)
        viewModelScope.launch {
            bookmarksRepo.addBookmark(bookmark)
        }
    }

    fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
        return livePublishedBook
    }

    fun getAnOrderedBook(): OrderedBook {
        return orderedBook
    }


    fun getLiveListOfBookVideos(): LiveData<List<BookVideo>> {
        return bookVideosRepo.getLiveListOfBookVideos(bookId)
    }

    private fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory) {
        viewModelScope.launch {
            searchHistoryRepo.addShelfSearchHistory(shelfSearchHistory)
        }
    }

    fun getLiveReadHistory(): LiveData<Optional<ReadHistory>> {
        return readHistory
    }

    fun addReadHistory(currentPage: Int, totalPages: Int) {
        viewModelScope.launch {
            readHistoryRepo.deleteAllHistory()
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP, true)
            if (showPopup) {
                val percentage = (currentPage / totalPages) * 100
                val readHistory = ReadHistory(bookId, currentPage, percentage, bookName)
                readHistoryRepo.addReadHistory(readHistory)
            }
        }
    }

}