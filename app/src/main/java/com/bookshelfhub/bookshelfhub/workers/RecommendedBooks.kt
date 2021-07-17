package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb

class RecommendedBooks (val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context,
    workerParams
    ) {
    override suspend fun doWork(): Result {

        val userAuth = UserAuth()

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val localDb = LocalDb(context)
        val optionalBookInterest = localDb.getBookInterest(userAuth.getUserId())

       if (optionalBookInterest.isPresent){
           val bookInterest = optionalBookInterest.get()

           if (bookInterest.archeologyChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.archeology))
           }else if (bookInterest.businessAndFinanceChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.business_finance))
           }else if (bookInterest.artAndCraftChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.art_craft))
           }else if (bookInterest.biographyChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.biography))
           }else if (bookInterest.civilEngChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.civil_eng))
           }else if (bookInterest.examAndTestPrepChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.preparation))
           }else if (bookInterest.fashionChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.fashion))
           }else if (bookInterest.historyChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.history))
           }else if (bookInterest.comicChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.comic))
           }else if (bookInterest.travelChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.travel))
           }else if (bookInterest.scienceAndTechnologyChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.science_technology))
           }else if (bookInterest.howToAndManualsChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.manuals))
           }else if (bookInterest.healthAndFitnessChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.health_fitness))
           }else if (bookInterest.poetryChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.poetry))
           }else if (bookInterest.lawChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.law))
           }else if (bookInterest.musicChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.music))
           }else if (bookInterest.fictionChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.fiction))
           }else if (bookInterest.languagesAndReferenceChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.languages_reference))
           }else if (bookInterest.newsChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.news))
           }else if (bookInterest.mathsAndEngineeringChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.engineering_mathematics))
           }else if (bookInterest.dataAnalysisChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.data_analysis))
           }else if (bookInterest.educationChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.education))
           }else if (bookInterest.computerProgrammingChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.computer_programming))
           }else if (bookInterest.transportationChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.transportation))
           }else if (bookInterest.literatureChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.literature))
           }else if (bookInterest.novelChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.novel))
           }else if (bookInterest.cookBooksChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.cook_books))
           }else if (bookInterest.politicsChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.politics))
           }else if (bookInterest.psychologyChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.psychology))
           }else if (bookInterest.humourChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.humour))
           }else if (bookInterest.islamChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.islam))
           }else if(bookInterest.christianityChecked){
               localDb.updateRecommendedBooksByTag(context.getString(R.string.christianity))
           }else if(bookInterest.sportChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.sport))
           }else if(bookInterest.entertainmentChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.entertainment))
           }


           if (bookInterest.religionChecked && !bookInterest.islamChecked && !bookInterest.christianityChecked){
               localDb.updateRecommendedBooksByCategory(context.getString(R.string.religion))
           }

        }

        return Result.success()

    }
}