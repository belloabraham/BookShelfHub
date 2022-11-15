package com.bookshelfhub.feature.home.ui.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.core.model.entities.BookInterest
import com.bookshelfhub.feature.home.workers.RecommendedBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookInterestViewModel @Inject constructor(
    private val bookInterestRepo: IBookInterestRepo,
    val userAuth: IUserAuth,
    private val worker: Worker
    ): ViewModel(){

    private var bookInterest: LiveData<Optional<BookInterest>> = MutableLiveData()
    private val userId:String = userAuth.getUserId()

    init {
        bookInterest = bookInterestRepo.getLiveBookInterest(userId)
    }

    fun updatedRecommendedBooks(){
        val recommendedBooksWorker =
            OneTimeWorkRequestBuilder<RecommendedBooks>()
                .build()
        worker.enqueueUniqueWork(
            Tag.recommendedBooksWorker,
            ExistingWorkPolicy.REPLACE,
            recommendedBooksWorker
        )
    }


    suspend fun addBookInterest(bookInterest: BookInterest){
            bookInterestRepo.addBookInterest(bookInterest)
    }

    fun getBookInterest(): LiveData<Optional<BookInterest>> {
        return bookInterest
    }
}