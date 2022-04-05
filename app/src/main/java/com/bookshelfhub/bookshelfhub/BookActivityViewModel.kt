package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.models.entities.*
import com.bookshelfhub.bookshelfhub.data.enums.Book
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.DbFields
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.google.common.base.Optional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
    val cloudDb: ICloudDb,
    val userAuth: IUserAuth,
    private val settingsUtil: SettingsUtil,
    private val localDb: ILocalDb,
    val savedState: SavedStateHandle
) : ViewModel() {

    val userId = userAuth.getUserId()
    private var isbn = savedState.get<String>(Book.ISBN.KEY)!!
    private var bookName = savedState.get<String>(Book.NAME.KEY)!!
    private val isSearchItem = savedState.get<Boolean>(Book.IS_SEARCH_ITEM.KEY) ?: false
    private var liveOrderedBook: LiveData<OrderedBooks> = MutableLiveData()
    private var readHistory: LiveData<Optional<History>> = MutableLiveData()


    private var livePublishedBook: LiveData<Optional<PublishedBook>> = MutableLiveData()
    private lateinit var orderedBook: OrderedBooks

    init {

        viewModelScope.launch(IO) {
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            if (showPopup) {
                readHistory = localDb.getLiveReadHistory()
            }
        }

        livePublishedBook = localDb.getLivePublishedBook(isbn)
        loadLiveOrderedBook(isbn, bookName)
        viewModelScope.launch(IO) {
            orderedBook = localDb.getAnOrderedBook(isbn)
        }

        cloudDb.getLiveListOfDataWhereAsync(
            DbFields.PUBLISHED_BOOKS.KEY,
            isbn,
            DbFields.VIDEO_LIST.KEY,
            BookVideos::class.java,
            true
        ) {
            if (it.isNotEmpty()) {
                viewModelScope.launch(IO) {
                    localDb.addBookVideos(it)
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
        liveOrderedBook = localDb.getLiveOrderedBook(isbn)
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
        viewModelScope.launch(IO) {
            localDb.deleteFromBookmark(pageNumb, isbn)
        }
    }

    suspend fun getBookmark(currentPage: Int): Optional<Bookmark> {
        return localDb.getBookmark(currentPage, isbn)
    }

    fun addBookmark(pageNumber: Int) {
        val bookmark = Bookmark(userId, isbn, pageNumber, bookName)
        viewModelScope.launch(IO) {
            localDb.addBookmark(bookmark)
        }
    }

    fun getLivePublishedBook(): LiveData<Optional<PublishedBook>> {
        return livePublishedBook
    }

    fun getAnOrderedBook(): OrderedBooks {
        return orderedBook
    }


    fun getLiveListOfBookVideos(): LiveData<List<BookVideos>> {
        return localDb.getLiveListOfBookVideos(isbn)
    }

    private fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory) {
        viewModelScope.launch(IO) {
            localDb.addShelfSearchHistory(shelfSearchHistory)
        }
    }

    fun getLiveReadHistory(): LiveData<Optional<History>> {
        return readHistory
    }

    fun addReadHistory(currentPage: Int, totalPages: Int) {
        viewModelScope.launch(IO) {
            //Delete all previous history when a book gets opened as only one is needed to show the user resume dialog
            localDb.deleteAllHistory()
            val showPopup = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            if (showPopup) {
                val percentage = (currentPage / totalPages) * 100
                val readHistory = History(isbn, currentPage, percentage, bookName)
                localDb.addReadHistory(readHistory)
            }
        }
    }

}