package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.PerspectiveAPI
import com.bookshelfhub.bookshelfhub.data.models.entities.UserReview
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.userreview.IUserReviewRepo
import com.bookshelfhub.bookshelfhub.helpers.webapi.wordtoxicityanalyzer.IWordAnalyzerAPI
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber


@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val wordAnalyzerAPI: IWordAnalyzerAPI,
    private val userReviewRepo: IUserReviewRepo,
    private val settingsUtil: SettingsUtil,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
    workerParams
){

    override suspend fun doWork(): Result {

        val bookId = inputData.getString(Book.ID)!!
        val apiKey:String = settingsUtil.getString(Settings.PERSPECTIVE_API)!!
        val userReview  = userReviewRepo.getUserReview(bookId).get()

        val userRatingDiff = inputData.getDouble(Book.RATING_DIFF, 0.0)
        val userId = userAuth.getUserId()
        val updatedBookValues: HashMap<String, FieldValue>? = getUpdatedBookValues(userReview, userRatingDiff)


       return try {
             val postBody = PerspectiveAPI.getPostBody(userReview.review)
             val response = wordAnalyzerAPI.analyze(postBody, apiKey)

               val requestNotSuccessful = !response.isSuccessful || response.body() == null
               if (requestNotSuccessful){
                   Result.retry()
               }

                val responseBody = response.body()!!
                val isNonToxicUserReview = responseBody.attributeScores.TOXICITY.summaryScore.value<=0.5
                if (isNonToxicUserReview){
                    userReviewRepo.updateRemoteUserReview(
                        userReview, updatedBookValues, bookId, userId)

                    if (userReview.verified){
                       userReview.postedBefore = true
                        userReviewRepo.addUserReview(userReview)
                    }
                }

                Result.success()

        }catch (e:Exception){
           Timber.e(e)
            Result.retry()
        }

    }

    private fun getUpdatedBookValues(userReview: UserReview, userRatingDiff:Double): HashMap<String, FieldValue>? {
        val userReviewNoForBook: Long = if (userReview.postedBefore) 0  else  1
        if (userReview.verified) {
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
