package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.repos.*
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
    val remoteDataSource: IRemoteDataSource,
    val userAuth: IUserAuth,
    private val settingsUtil: SettingsUtil,
    val savedState: SavedStateHandle,
    private val orderedBooksRepo: OrderedBooksRepo,
    publishedBooksRepo: PublishedBooksRepo,
    private val readHistoryRepo: ReadHistoryRepo,
    private val searchHistoryRepo: SearchHistoryRepo,
    private val bookmarksRepo: BookmarksRepo,
    private val bookVideosRepo: BookVideosRepo,
) : ViewModel() {

    val userId = userAuth.getUserId()
    private var isbn = savedState.get<String>(Book.ID)!!
    private var bookName = savedState.get<String>(Book.NAME)!!
    private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM) ?: false
    private var liveOrderedBook: LiveData<OrderedBooks> = MutableLiveData()
    private var readHistory: LiveData<Optional<History>> = MutableLiveData()


    private var livePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()
    private lateinit var orderedBook: OrderedBooks

    init {

        viewModelScope.launch {
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP, true)
            if (showPopup) {
                readHistory = readHistoryRepo.getLiveReadHistory()
            }
        }

        livePublishedBook = publishedBooksRepo.getALiveOptionalPublishedBook(isbn)
        loadLiveOrderedBook(isbn, bookName)
        viewModelScope.launch {
            orderedBook = orderedBooksRepo.getAnOrderedBook(isbn)
        }

        remoteDataSource.getLiveListOfDataWhereAsync(
            RemoteDataFields.PUBLISHED_BOOKS_COLL,
            isbn,
            RemoteDataFields.VIDEO_LIST,
            BookVideos::class.java,
            true
        ) {
            if (it.isNotEmpty()) {
                viewModelScope.launch {
                    bookVideosRepo.addBookVideos(it)
                }
            }
        }
        if (isSearchItem) {
            addShelfSearchHistory(
                ShelfSearchHistory(
                    isbn,
                    bookName,
                    userId,
                    DateTimeUtil.getDateTimeAsString()
                )
            )
        }
    }


    fun loadLiveOrderedBook(isbn: String, bookName: String) {
        //Re set the value of ISBN as this activity can be opened with FLagUpdateCurrent
        this.isbn = isbn
        this.bookName = bookName
        liveOrderedBook = orderedBooksRepo.getLiveOrderedBook(isbn)
    }

    fun getLiveOrderedBook(): LiveData<OrderedBooks> {
        return liveOrderedBook
    }

    fun getIsbnNo(): String {
        return isbn
    }

    fun getBookName(): String {
        return bookName
    }

    fun deleteFromBookmark(pageNumb: Int) {
        viewModelScope.launch {
            bookmarksRepo.deleteFromBookmark(pageNumb, isbn)
        }
    }

    suspend fun getBookmark(currentPage: Int): Optional<Bookmark> {
        return bookmarksRepo.getBookmark(currentPage, isbn)
    }

    fun addBookmark(pageNumber: Int) {
        val bookmark = Bookmark(userId, isbn, pageNumber, bookName)
        viewModelScope.launch {
            bookmarksRepo.addBookmark(bookmark)
        }
    }

    fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
        return livePublishedBook
    }

    fun getAnOrderedBook(): OrderedBooks {
        return orderedBook
    }


    fun getLiveListOfBookVideos(): LiveData<List<BookVideos>> {
        return bookVideosRepo.getLiveListOfBookVideos(isbn)
    }

    private fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory) {
        viewModelScope.launch {
            searchHistoryRepo.addShelfSearchHistory(shelfSearchHistory)
        }
    }

    fun getLiveReadHistory(): LiveData<Optional<History>> {
        return readHistory
    }

    fun addReadHistory(currentPage: Int, totalPages: Int) {
        viewModelScope.launch {
            //Delete all previous history when a book gets opened as only one is needed to show the user resume dialog
            readHistoryRepo.deleteAllHistory()
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP, true)
            if (showPopup) {
                val percentage = (currentPage / totalPages) * 100
                val readHistory = History(isbn, currentPage, percentage, bookName)
                readHistoryRepo.addReadHistory(readHistory)
            }
        }
    }

}