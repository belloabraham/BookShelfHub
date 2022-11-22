package com.bookshelfhub.feature.book.item.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.ordered_books.OrderedBooksRepo
import com.bookshelfhub.core.remote.cloud_functions.CloudFunctions
import com.bookshelfhub.core.remote.cloud_functions.ICloudFunctions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class AddAFreeBook  @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudFunction: ICloudFunctions,
    private val orderedBooksRepo: OrderedBooksRepo,
    private val json: Json,
    ): CoroutineWorker(context,
    workerParams
) {

    companion object{
       private const val A_FREE_BOOK = "aFreeBook"
    }

    override suspend fun doWork(): Result {

        val bookId = inputData.getString(Book.ID)!!

        return try {

           val aFreeBook = orderedBooksRepo.getAnOrderedBook(bookId)
            val data = hashMapOf<String, Any?>(
                A_FREE_BOOK to json.getJsonString(aFreeBook)
            )

            cloudFunction.call(CloudFunctions.ADD_A_FREE_BOOK, data)

             Result.success()
        }catch (e:Exception){
            ErrorUtil.e(e)
            Result.retry()
        }
    }
}