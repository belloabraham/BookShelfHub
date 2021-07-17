package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "BookInterest")
data class BookInterest(
    @PrimaryKey
     var userId: String,
     var uploaded: Boolean = false,
     var historyChecked:Boolean = false,
     var comicChecked:Boolean = false,
     var musicChecked:Boolean = false,
     var entertainmentChecked:Boolean = false,
     var computerProgrammingChecked:Boolean = false,
     var biographyChecked:Boolean = false,
     var poetryChecked:Boolean = false,
     var religionChecked:Boolean = false,
     var humourChecked:Boolean = false,
     var sportChecked:Boolean = false,
     var travelChecked:Boolean = false,
     var lawChecked:Boolean = false,
     var transportationChecked:Boolean = false,
     var psychologyChecked:Boolean = false,
     var politicsChecked:Boolean = false,
     var dataAnalysisChecked:Boolean = false,
     var novelChecked:Boolean = false,
     var fashionChecked:Boolean = false,
     var newsChecked:Boolean = false,
     var christianityChecked:Boolean = false,
     var islamChecked:Boolean = false,
     var mathsAndEngineeringChecked: Boolean = false,
     var healthAndFitnessChecked: Boolean = false,
     var scienceAndTechnologyChecked: Boolean = false,
     var languagesAndReferenceChecked: Boolean = false,
     var examAndTestPrepChecked: Boolean = false,
     var howToAndManualsChecked: Boolean = false,
     var literatureChecked: Boolean = false,
     var artAndCraftChecked: Boolean = false,
     var fictionChecked: Boolean = false,
     var businessAndFinanceChecked: Boolean = false,
     var cookBooksChecked: Boolean = false,
     var educationChecked: Boolean = false,
     var civilEngChecked: Boolean = false,
     var archeologyChecked: Boolean = false,
     var added: Boolean = false
)