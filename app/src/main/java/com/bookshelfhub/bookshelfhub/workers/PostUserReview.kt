package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.Settings
import com.bookshelfhub.bookshelfhub.helpers.utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.data.Book
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.RemoteDataFields
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.repos.sources.remote.IRemoteDataSource
import com.bookshelfhub.bookshelfhub.data.repos.UserReviewRepo
import com.bookshelfhub.bookshelfhub.helpers.webapi.WordAnalyzerAPI
import com.bookshelfhub.bookshelfhub.helpers.wordtoxicity.Perspective
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await


@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val remoteDataSource: IRemoteDataSource,
    private val wordAnalyzerAPI: WordAnalyzerAPI,
    private val userReviewRepo: UserReviewRepo,
    private val settingsUtil: SettingsUtil,
    private val userAuth: IUserAuth
): CoroutineWorker(context,
    workerParams
){

    override suspend fun doWork(): Result {

        val bookId = inputData.getString(Book.ISBN)!!
        val apiKey:String = settingsUtil.getString(Settings.PERSPECTIVE_API)!!
        val userReview  = userReviewRepo.getUserReview(bookId).get()

        // Check if the review have been posted before
        val userReviewForBook: Long = if (userReview.postedBefore) 0  else  1

        val userRatingDiff = inputData.getDouble(Book.RATING_DIFF, 0.0)
        val userId = userAuth.getUserId()
        var dynamicBookAttr: HashMap<String, FieldValue>? = null

        if (userReview.verified) {
            dynamicBookAttr = if (userReviewForBook > 0) { //If user is posting for the first time
                hashMapOf(
                    // Add to book total review
                    RemoteDataFields.TOTAL_REVIEWS to FieldValue.increment(userReviewForBook),
                    // Add userRatingDiff to total ratings that can be + or -
                    RemoteDataFields.TOTAL_RATINGS to FieldValue.increment(userRatingDiff)
                )
            } else {
                hashMapOf(
                    // Has user has posted before only upload userRatingDiff
                    RemoteDataFields.TOTAL_RATINGS to FieldValue.increment(userRatingDiff)
                )
            }
        }

        val postBody = Perspective().getPostBody(userReview.review)

       return try {
            val response = wordAnalyzerAPI.analyze(postBody, apiKey)
            val responseBody = response.body()!!
            if(response.isSuccessful && response.body() != null){
                if (responseBody.attributeScores.TOXICITY.summaryScore.value<=0.5){
                    remoteDataSource.updateUserReview(
                        dynamicBookAttr, userReview,
                        RemoteDataFields.PUBLISHED_BOOKS_COLL, bookId,
                        RemoteDataFields.REVIEWS_COLL, userId).await()

                    if (userReview.verified){
                        // Update user review locally
                       userReview.postedBefore = true
                        userReviewRepo.addUserReview(userReview)
                    }
                }
                Result.success()
            }else{
                Result.retry()
            }
        }catch (e:Exception){
            Result.retry()
        }

    }
}
