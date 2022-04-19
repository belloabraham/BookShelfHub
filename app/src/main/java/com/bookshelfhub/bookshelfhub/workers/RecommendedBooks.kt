package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.data.repos.BookInterestRepo
import com.bookshelfhub.bookshelfhub.data.repos.PublishedBooksRepo
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecommendedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userAuth:IUserAuth,
    private val publishedBooksRepo: PublishedBooksRepo,
    private val bookInterestRepo: BookInterestRepo,
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

           val archeology = context.getString(R.string.archeology)
           if (bookInterest.archeologyChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(archeology)
           }else{
               publishedBooksRepo.updateRecommendedBooksByTag(archeology, false)
           }

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
               publishedBooksRepo.updateRecommendedBooksByTag(biography)
           }else{
               publishedBooksRepo.updateRecommendedBooksByTag(biography, false)
           }
           val civilEng = context.getString(R.string.civil_eng)
           if (bookInterest.civilEngChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(civilEng)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(civilEng, false)
           }

           val examPrep = context.getString(R.string.preparation)
           if (bookInterest.examAndTestPrepChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(examPrep)
           }else{
               publishedBooksRepo.updateRecommendedBooksByTag(examPrep, false)
           }

           val fashion = context.getString(R.string.fashion)
           if (bookInterest.fashionChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(fashion)
           }else{
               publishedBooksRepo.updateRecommendedBooksByTag(fashion, false)
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
               publishedBooksRepo.updateRecommendedBooksByTag(healthFitness)
           }else{
               publishedBooksRepo.updateRecommendedBooksByTag(healthFitness, false)
           }

           val poetry = context.getString(R.string.poetry)
           if (bookInterest.poetryChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(poetry)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(poetry, false)
           }

           val law = context.getString(R.string.law)
           if (bookInterest.lawChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(law)
           }else{
               publishedBooksRepo.updateRecommendedBooksByCategory(law, false)
           }

           val music = context.getString(R.string.music)
           if (bookInterest.musicChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(music)
           }else{
               publishedBooksRepo.updateRecommendedBooksByTag(music, false)
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
               publishedBooksRepo.updateRecommendedBooksByTag(mathsAndEng)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(mathsAndEng, false)
           }

           val dataAnalysis = context.getString(R.string.data_analysis)
            if (bookInterest.dataAnalysisChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(dataAnalysis)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(dataAnalysis, false)
            }

           val education = context.getString(R.string.education)
            if (bookInterest.educationChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(education)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(education, false)
            }

           val compProg = context.getString(R.string.computer_programming)
           if (bookInterest.computerProgrammingChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(compProg)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(compProg, false)
           }

           val transportation = context.getString(R.string.transportation)
           if (bookInterest.transportationChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(transportation)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(transportation, false)
           }

           val literature = context.getString(R.string.literature)
           if (bookInterest.literatureChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(literature)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(literature, false)
           }

           val novel = context.getString(R.string.novel)
            if (bookInterest.novelChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(novel)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(novel, false)
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

           val psychology = context.getString(R.string.psychology)
           if (bookInterest.psychologyChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(psychology)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(psychology, false)
           }

           val humour = context.getString(R.string.humour)
           if (bookInterest.humourChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(humour)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(humour, false)
           }

           val islam = context.getString(R.string.islam)
           if (bookInterest.islamChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(islam)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(islam, false)
           }

           val christianity = context.getString(R.string.christianity)
            if(bookInterest.christianityChecked){
               publishedBooksRepo.updateRecommendedBooksByTag(christianity)
           }else {
               publishedBooksRepo.updateRecommendedBooksByTag(christianity, false)
           }

           val sport = context.getString(R.string.sport)
           if(bookInterest.sportChecked){
               publishedBooksRepo.updateRecommendedBooksByCategory(sport)
           }else {
               publishedBooksRepo.updateRecommendedBooksByCategory(sport, false)
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