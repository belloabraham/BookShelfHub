package com.bookshelfhub.book.page

import androidx.lifecycle.*
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.bookmarks.IBookmarksRepo
import com.bookshelfhub.core.data.repos.ordered_books.IOrderedBooksRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.core.data.repos.read_history.IReadHistoryRepo
import com.bookshelfhub.core.data.repos.user.IUserRepo
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.dynamic_link.IDynamicLink
import com.bookshelfhub.core.model.entities.*
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
    val userAuth: IUserAuth,
    private val settingsUtil: SettingsUtil,
    savedState: SavedStateHandle,
    private val connectionUtil: ConnectionUtil,
    private val orderedBooksRepo: IOrderedBooksRepo,
    private val dynamicLink: IDynamicLink,
    private val publishedBooksRepo: IPublishedBooksRepo,
    private val readHistoryRepo: IReadHistoryRepo,
    private val userRepo: IUserRepo,
    private val bookmarksRepo: IBookmarksRepo,
    private val remoteConfig:IRemoteConfig
) : ViewModel() {

    val userId = userAuth.getUserId()
    private var bookId = savedState.get<String>(Book.ID)!!
    private var bookName = savedState.get<String>(Book.NAME)!!
    private var bookShareLink:String?=null

    init {

        viewModelScope.launch {
            bookShareLink = settingsUtil.getString(bookId)
        }

    }

    fun getRemoteString(key:String): String {
       return remoteConfig.getString(key)
    }

    suspend fun getInt(key:String, defaultVal:Int): Int {
       return settingsUtil.getInt(key, defaultVal)
    }

    suspend fun getPublishedBook(): Optional<PublishedBook> {
        return publishedBooksRepo.getPublishedBook(bookId)
    }

    fun addIntToSettings(key:String, value:Int){
        viewModelScope.launch {
            settingsUtil.setInt(key, value)
        }
    }

    fun generateBookShareLink(){
        val shouldGenerateBookShareUrl = connectionUtil.isConnected() && bookShareLink == null
        viewModelScope.launch {
            val book = publishedBooksRepo.getPublishedBook(bookId).get()
            if(shouldGenerateBookShareUrl){
                try {
                    val userEarningsCurrency = userRepo.getUser(userId).get().earningsCurrency
                    val userIdAndEarningsCurrency = "$userId@$userEarningsCurrency"
                    bookShareLink = dynamicLink.generateShortDynamicLinkAsync(
                        book.name,
                        book.description,
                        book.coverDataUrl,
                        userIdAndEarningsCurrency
                    ).toString()
                    settingsUtil.setString(bookId, bookShareLink!!.toString())
                }catch (e:Exception){
                    ErrorUtil.e(e)
                    return@launch
                }
            }
        }
    }

    fun getBookShareLink(): String? {
        return bookShareLink
    }

    fun getBookId(): String {
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

    suspend fun getAnOrderedBook(): OrderedBook {
        return orderedBooksRepo.getAnOrderedBook(bookId).get()
    }

    suspend fun getBoolean(key: String, defaultVal: Boolean): Boolean {
        return settingsUtil.getBoolean(key, defaultVal)
    }

    suspend fun getReadHistory(): Optional<ReadHistory> {
        return readHistoryRepo.getReadHistory(bookId)
    }

    fun addReadHistory(currentPage: Int, totalPages: Int) {
        viewModelScope.launch {
            val percentage = (currentPage / totalPages) * 100
            val readHistory = ReadHistory( currentPage, percentage, bookName, bookId)
            readHistoryRepo.addReadHistory(readHistory)
        }
    }

}