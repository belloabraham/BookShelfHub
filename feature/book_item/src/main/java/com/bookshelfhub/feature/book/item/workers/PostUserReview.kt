package com.bookshelfhub.feature.book.item.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.data.repos.user_review.IUserReviewRepo
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.datastore.settings.SettingsUtil
import com.bookshelfhub.core.model.entities.UserReview
import com.bookshelfhub.core.remote.database.RemoteDataFields
import com.bookshelfhub.core.remote.webapi.PerspectiveAPI.getPostBody
import com.bookshelfhub.core.remote.webapi.wordtoxicityanalyzer.IWordAnalyzerAPI
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val wordAnalyzerAPI: IWordAnalyzerAPI,
    private val userReviewRepo: IUserReviewRepo,
    private val settingsUtil: SettingsUtil,
    private val userAuth: IUserAuth
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {

        val bookId = inputData.getString(Book.ID)!!
        val apiKey:String = settingsUtil.getString(Settings.PERSPECTIVE_API)!!
        val userReview  = userReviewRepo.getUserReview(bookId).get()

        val userRatingDiff = inputData.getDouble(Book.RATING_DIFF, 0.0)

        val userId = userAuth.getUserId()
        val updatedBookValues: HashMap<String, FieldValue>? = getUpdatedBookValues(userReview, userRatingDiff)

        try {
             val postBody = getPostBody(userReview.review)
             val response = wordAnalyzerAPI.analyze(postBody, apiKey)

               val requestNotSuccessful = !response.isSuccessful || response.body() == null
               if (requestNotSuccessful){
                  return Result.retry()
               }

                val responseBody = response.body()!!
                val toxicityScore = responseBody.attributeScores.TOXICITY.summaryScore.value
                val isNonToxicUserReview = toxicityScore <= 0.5

                if (isNonToxicUserReview){
                    userReviewRepo.updateRemoteUserReview(userReview, updatedBookValues, bookId, userId)

                    if (userReview.isVerified){
                        userReview.postedBefore = true
                        userReviewRepo.addUserReview(userReview)
                    }
                }

               return Result.success()

        }catch (e:Exception){
           ErrorUtil.e(e)
            return Result.retry()
        }

    }

    private fun getUpdatedBookValues(userReview: UserReview, userRatingDiff:Double): HashMap<String, FieldValue>? {
        val userReviewNoForBook: Long = if (userReview.postedBefore) 0  else  1
        if (userReview.isVerified) {
            return if (userReviewNoForBook > 0) {
                hashMapOf(
                    RemoteDataFields.TOTAL_REVIEWS to FieldValue.increment(userReviewNoForBook),
                    RemoteDataFields.TOTAL_RATINGS to FieldValue.increment(userRatingDiff)
                )
            } else {
                hashMapOf(
                    RemoteDataFields.TOTAL_RATINGS to FieldValue.increment(userRatingDiff)
                )
            }
        }
        return null
    }
}
