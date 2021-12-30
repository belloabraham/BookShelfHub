package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.Logger
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.rest.MediaType
import com.bookshelfhub.bookshelfhub.helpers.rest.WebApi
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.UserReview
import com.bookshelfhub.bookshelfhub.models.perspective.response.ResponseBody
import com.bookshelfhub.bookshelfhub.services.PrivateKeys
import com.bookshelfhub.bookshelfhub.services.wordtoxicity.Perspective
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import okhttp3.RequestBody.Companion.toRequestBody


@HiltWorker
class PostUserReview @AssistedInject constructor(
    @Assisted val context: Context, @Assisted workerParams: WorkerParameters,
    private val localDb: ILocalDb,
    private val cloudDb: ICloudDb,
    private val webApi:WebApi,
    private val settingsUtil: SettingsUtil,
    private val json: Json,
    private val userAuth: IUserAuth
): Worker(context,
    workerParams
){

    override fun doWork(): Result {

        val isbn = inputData.getString(Book.ISBN.KEY)!!

        val apiKey:String

        //TODO no worries about null exception as this worker is one time and will be triggered on book item activity, so api is certain to be available
            runBlocking {
                apiKey = settingsUtil.getString(PrivateKeys.PERSPECTIVE_API_KEY)!!
            }


        val userReview:UserReview
        //TODO Get the user review
        runBlocking {
            userReview = localDb.getUserReview(isbn).get()
        }


        //TODO Check if the review have been posted before
        val bookTotalReview: Long = if (userReview.postedBefore) {
            //TODO If user is updating a previously posted review
            0
        } else {
            //TODO If user is posting a review for the first time
            1
        }
        val userRatingDiff = inputData.getDouble(Book.RATING_DIFF.KEY, 0.0)
        val userId = userAuth.getUserId()
        var dynamicBookAttr: HashMap<String, FieldValue>? = null

        if (userReview.verified) {
            dynamicBookAttr = if (bookTotalReview > 0) { //If user is posting for the first time
                hashMapOf(
                    //TODO Add to book total review
                    DbFields.TOTAL_REVIEWS.KEY to FieldValue.increment(bookTotalReview),
                    //TODO Add userRatingDiff to total ratings that can be + or -
                    DbFields.TOTAL_RATINGS.KEY to FieldValue.increment(userRatingDiff)
                )
            } else {
                hashMapOf(
                    //TODO Has user has posted before only upload userRatingDiff
                    DbFields.TOTAL_RATINGS.KEY to FieldValue.increment(userRatingDiff)
                )
            }
        }


        val endPointUrl = context.getString(R.string.perspective_api_endpoint)
        val postBody = Perspective().getPostBody(userReview.review)
        val reqBody = json.getJsonString(postBody).toRequestBody(MediaType.APPLICATION_JSON)

        val response = webApi.post(endPointUrl, apiKey, reqBody)

      return   if (response.body!=null){

            try {
                val body = response.body!!.string()
                val responseBody = json.fromJson(body, ResponseBody::class.java)
                if (responseBody.attributeScores.TOXICITY.summaryScore.value<=0.5){
                    val task =  cloudDb.updateUserReview(
                        dynamicBookAttr, userReview,
                        DbFields.PUBLISHED_BOOKS.KEY, isbn,
                        DbFields.REVIEWS.KEY, userId)

                    Tasks.await(task)

                        if(task.isSuccessful){
                            if (userReview.verified){
                                //TODO Update user review locally
                                userReview.postedBefore = true
                                runBlocking {
                                    localDb.addUserReview(userReview)
                                }
                            }
                            Result.success()
                        }else{
                            Result.retry()
                        }
                }else{
                    Result.success()
                }
            }catch (e:Exception){
                Logger.log("Worker:PostUserReview", e)
                Result.success()
            }

        }else{
            Result.retry()
        }

    }
}
