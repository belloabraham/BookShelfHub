package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.FBUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.RoomDb
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecommendedBooks @AssistedInject constructor (
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val userAuth:IUserAuth,
    val localDb:ILocalDb) : CoroutineWorker(context,
    workerParams
    ) {
    override suspend fun doWork(): Result {

        if (!userAuth.getIsUserAuthenticated()){
            return Result.retry()
        }

        val optionalBookInterest = localDb.getBookInterest(userAuth.getUserId())

       if (optionalBookInterest.isPresent && optionalBookInterest.get().added){
           val bookInterest = optionalBookInterest.get()

           val archeology = context.getString(R.string.archeology)
           if (bookInterest.archeologyChecked){
               localDb.updateRecommendedBooksByTag(archeology)
           }else{
               localDb.updateRecommendedBooksByTag(archeology, false)
           }

           val business = context.getString(R.string.business_finance)
           if (bookInterest.businessAndFinanceChecked){
               localDb.updateRecommendedBooksByCategory(business)
           }else{
               localDb.updateRecommendedBooksByCategory(business, false)
           }

           val artAndCraft = context.getString(R.string.art_craft)
           if (bookInterest.artAndCraftChecked){
               localDb.updateRecommendedBooksByCategory(artAndCraft)
           }else{
               localDb.updateRecommendedBooksByCategory(artAndCraft, false)
           }

           val biography = context.getString(R.string.biography)
           if (bookInterest.biographyChecked){
               localDb.updateRecommendedBooksByTag(biography)
           }else{
               localDb.updateRecommendedBooksByTag(biography, false)
           }
           val civilEng = context.getString(R.string.civil_eng)
           if (bookInterest.civilEngChecked){
               localDb.updateRecommendedBooksByTag(civilEng)
           }else {
               localDb.updateRecommendedBooksByTag(civilEng, false)
           }

           val examPrep = context.getString(R.string.preparation)
           if (bookInterest.examAndTestPrepChecked){
               localDb.updateRecommendedBooksByTag(examPrep)
           }else{
               localDb.updateRecommendedBooksByTag(examPrep, false)
           }

           val fashion = context.getString(R.string.fashion)
           if (bookInterest.fashionChecked){
               localDb.updateRecommendedBooksByTag(fashion)
           }else{
               localDb.updateRecommendedBooksByTag(fashion, false)
           }

           val history = context.getString(R.string.history)
           if (bookInterest.historyChecked){
               localDb.updateRecommendedBooksByCategory(history)
           }else{
               localDb.updateRecommendedBooksByCategory(history, false)
           }

           val comic = context.getString(R.string.comic)
           if (bookInterest.comicChecked){
               localDb.updateRecommendedBooksByCategory(comic)
           }else{
               localDb.updateRecommendedBooksByCategory(comic, false)
           }

           val travel = context.getString(R.string.travel)
           if (bookInterest.travelChecked){
               localDb.updateRecommendedBooksByCategory(travel)
           }else{
               localDb.updateRecommendedBooksByCategory(travel, false)
           }

           val scienceTech = context.getString(R.string.science_technology)
           if (bookInterest.scienceAndTechnologyChecked){
               localDb.updateRecommendedBooksByCategory(scienceTech)
           }else{
               localDb.updateRecommendedBooksByCategory(scienceTech, false)
           }

           val howToManuals = context.getString(R.string.manuals)
           if (bookInterest.howToAndManualsChecked){
               localDb.updateRecommendedBooksByCategory(howToManuals)
           }else{
               localDb.updateRecommendedBooksByCategory(howToManuals, false)
           }

           val healthFitness = context.getString(R.string.health_fitness)
           if (bookInterest.healthAndFitnessChecked){
               localDb.updateRecommendedBooksByTag(healthFitness)
           }else{
               localDb.updateRecommendedBooksByTag(healthFitness, false)
           }

           val poetry = context.getString(R.string.poetry)
           if (bookInterest.poetryChecked){
               localDb.updateRecommendedBooksByCategory(poetry)
           }else{
               localDb.updateRecommendedBooksByCategory(poetry, false)
           }

           val law = context.getString(R.string.law)
           if (bookInterest.lawChecked){
               localDb.updateRecommendedBooksByCategory(law)
           }else{
               localDb.updateRecommendedBooksByCategory(law, false)
           }

           val music = context.getString(R.string.music)
           if (bookInterest.musicChecked){
               localDb.updateRecommendedBooksByTag(music)
           }else{
               localDb.updateRecommendedBooksByTag(music, false)
           }

           val fiction = context.getString(R.string.fiction)
           if (bookInterest.fictionChecked){
               localDb.updateRecommendedBooksByCategory(fiction)
           }else {
               localDb.updateRecommendedBooksByCategory(fiction, false)
           }

           val langAndRef = context.getString(R.string.languages_reference)
           if (bookInterest.languagesAndReferenceChecked){
               localDb.updateRecommendedBooksByCategory(langAndRef)
           }else {
               localDb.updateRecommendedBooksByCategory(langAndRef, false)
            }

           val news = context.getString(R.string.news)
           if (bookInterest.newsChecked){
               localDb.updateRecommendedBooksByCategory(news)
           }else {
               localDb.updateRecommendedBooksByCategory(news, false)
           }

           val mathsAndEng = context.getString(R.string.engineering_mathematics)
           if (bookInterest.mathsAndEngineeringChecked){
               localDb.updateRecommendedBooksByTag(mathsAndEng)
           }else {
               localDb.updateRecommendedBooksByTag(mathsAndEng, false)
           }

           val dataAnalysis = context.getString(R.string.data_analysis)
            if (bookInterest.dataAnalysisChecked){
               localDb.updateRecommendedBooksByTag(dataAnalysis)
           }else {
               localDb.updateRecommendedBooksByTag(dataAnalysis, false)
            }

           val education = context.getString(R.string.education)
            if (bookInterest.educationChecked){
               localDb.updateRecommendedBooksByCategory(education)
           }else {
               localDb.updateRecommendedBooksByCategory(education, false)
            }

           val compProg = context.getString(R.string.computer_programming)
           if (bookInterest.computerProgrammingChecked){
               localDb.updateRecommendedBooksByTag(compProg)
           }else {
               localDb.updateRecommendedBooksByTag(compProg, false)
           }

           val transportation = context.getString(R.string.transportation)
           if (bookInterest.transportationChecked){
               localDb.updateRecommendedBooksByTag(transportation)
           }else {
               localDb.updateRecommendedBooksByTag(transportation, false)
           }

           val literature = context.getString(R.string.literature)
           if (bookInterest.literatureChecked){
               localDb.updateRecommendedBooksByTag(literature)
           }else {
               localDb.updateRecommendedBooksByTag(literature, false)
           }

           val novel = context.getString(R.string.novel)
            if (bookInterest.novelChecked){
               localDb.updateRecommendedBooksByTag(novel)
           }else {
               localDb.updateRecommendedBooksByTag(novel, false)
           }

           val cookBooks = context.getString(R.string.cook_books)
           if (bookInterest.cookBooksChecked){
               localDb.updateRecommendedBooksByCategory(cookBooks)
           }else {
               localDb.updateRecommendedBooksByCategory(cookBooks, false)
            }

           val politics = context.getString(R.string.politics)
           if (bookInterest.politicsChecked){
               localDb.updateRecommendedBooksByCategory(politics)
           }else {
               localDb.updateRecommendedBooksByCategory(politics, false)
           }

           val psychology = context.getString(R.string.psychology)
           if (bookInterest.psychologyChecked){
               localDb.updateRecommendedBooksByTag(psychology)
           }else {
               localDb.updateRecommendedBooksByTag(psychology, false)
           }

           val humour = context.getString(R.string.humour)
           if (bookInterest.humourChecked){
               localDb.updateRecommendedBooksByTag(humour)
           }else {
               localDb.updateRecommendedBooksByTag(humour, false)
           }

           val islam = context.getString(R.string.islam)
           if (bookInterest.islamChecked){
               localDb.updateRecommendedBooksByTag(islam)
           }else {
               localDb.updateRecommendedBooksByTag(islam, false)
           }

           val christianity = context.getString(R.string.christianity)
            if(bookInterest.christianityChecked){
               localDb.updateRecommendedBooksByTag(christianity)
           }else {
               localDb.updateRecommendedBooksByTag(christianity, false)
           }

           val sport = context.getString(R.string.sport)
           if(bookInterest.sportChecked){
               localDb.updateRecommendedBooksByCategory(sport)
           }else {
               localDb.updateRecommendedBooksByCategory(sport, false)
           }

           val entertainment = context.getString(R.string.entertainment)
           if(bookInterest.entertainmentChecked){
               localDb.updateRecommendedBooksByCategory(entertainment)
           }else{
               localDb.updateRecommendedBooksByCategory(entertainment, false)
           }

           val religion = context.getString(R.string.religion)
           if (bookInterest.religionChecked && !bookInterest.islamChecked && !bookInterest.christianityChecked){
               localDb.updateRecommendedBooksByCategory(religion)
           }else if (!bookInterest.religionChecked){
            localDb.updateRecommendedBooksByCategory(religion, false)
           }

        }

        return Result.success()

    }
}