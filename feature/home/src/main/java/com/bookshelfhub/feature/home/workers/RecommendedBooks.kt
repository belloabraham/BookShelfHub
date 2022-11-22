package com.bookshelfhub.feature.home.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.data.repos.bookinterest.IBookInterestRepo
import com.bookshelfhub.core.data.repos.published_books.IPublishedBooksRepo
import com.bookshelfhub.feature.home.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecommendedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth: IUserAuth,
    private val publishedBooksRepo: IPublishedBooksRepo,
    private val bookInterestRepo: IBookInterestRepo,
) : CoroutineWorker(context,
    workerParams
    ) {


    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val optionalBookInterest = bookInterestRepo.getBookInterest(userAuth.getUserId())
        val userAddedBookInterest = optionalBookInterest.isPresent && optionalBookInterest.get().added

       if (userAddedBookInterest){
           val bookInterest = optionalBookInterest.get()

           val business = context.getString(R.string.business_finance)
           if (bookInterest.businessAndFinanceChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(business)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(business, false)
           }

           val artAndCraft = context.getString(R.string.art_craft)
           if (bookInterest.artAndCraftChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(artAndCraft)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(artAndCraft, false)
           }

           val biography = context.getString(R.string.biography)
           if (bookInterest.biographyChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(biography)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(biography, false)
           }

           val fashion = context.getString(R.string.fashion)
           if (bookInterest.fashionChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(fashion)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(fashion, false)
           }

           val history = context.getString(R.string.history)
           if (bookInterest.historyChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(history)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(history, false)
           }

           val comic = context.getString(R.string.comic)
           if (bookInterest.comicChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(comic)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(comic, false)
           }

           val travel = context.getString(R.string.travel)
           if (bookInterest.travelChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(travel)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(travel, false)
           }

           val scienceTech = context.getString(R.string.science_technology)
           if (bookInterest.scienceAndTechnologyChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(scienceTech)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(scienceTech, false)
           }

           val howToManuals = context.getString(R.string.manuals)
           if (bookInterest.howToAndManualsChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(howToManuals)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(howToManuals, false)
           }

           val healthFitness = context.getString(R.string.health_fitness)
           if (bookInterest.healthAndFitnessChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(healthFitness)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(healthFitness, false)
           }

           val nutrition = context.getString(R.string.nutrition)
           if (bookInterest.nutritionChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(nutrition)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(nutrition, false)
           }

           val law = context.getString(R.string.law)
           if (bookInterest.lawChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(law)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(law, false)
           }

           val fiction = context.getString(R.string.fiction)
           if (bookInterest.fictionChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(fiction)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(fiction, false)
           }

           val langAndRef = context.getString(R.string.languages_reference)
           if (bookInterest.languagesAndReferenceChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(langAndRef)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(langAndRef, false)
            }

           val news = context.getString(R.string.news)
           if (bookInterest.newsChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(news)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(news, false)
           }

           val mathsAndEng = context.getString(R.string.engineering_mathematics)
           if (bookInterest.mathsAndEngineeringChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(mathsAndEng)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(mathsAndEng, false)
           }

           val literature = context.getString(R.string.literature)
           if (bookInterest.literatureChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(literature)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(literature, false)
           }

           val cookBooks = context.getString(R.string.cook_books)
           if (bookInterest.cookBooksChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(cookBooks)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(cookBooks, false)
            }

           val politics = context.getString(R.string.politics)
           if (bookInterest.politicsChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(politics)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(politics, false)
           }

           val islam = context.getString(R.string.islam)
           if (bookInterest.islamChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(islam)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(islam, false)
           }

           val christianity = context.getString(R.string.christianity)
            if(bookInterest.christianityChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(christianity)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(christianity, false)
           }
           
           val entertainment = context.getString(R.string.entertainment)
           if(bookInterest.entertainmentChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(entertainment)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(entertainment, false)
           }

           val religion = context.getString(R.string.religion)
           if (bookInterest.religionChecked && !bookInterest.islamChecked && !bookInterest.christianityChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(religion)
           }else if (!bookInterest.religionChecked){
            publishedBooksRepo.updateRecommendedBooksByCategory(religion, false)
           }

        }

        return Result.success()

    }
}