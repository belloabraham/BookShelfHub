package com.bookshelfhub.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "BookInterests")
data class BookInterest(
     @PrimaryKey
     var userId: String,
     var uploaded: Boolean = false,
     var historyChecked:Boolean = false,
     var comicChecked:Boolean = false,
     var entertainmentChecked:Boolean = false,
     var biographyChecked:Boolean = false,
     var religionChecked:Boolean = false,
     var travelChecked:Boolean = false,
     var lawChecked:Boolean = false,
     var politicsChecked:Boolean = false,
     var fictionChecked:Boolean = false,
     var fashionChecked:Boolean = false,
     var nutritionChecked:Boolean = false,
     var newsChecked:Boolean = false,
     var christianityChecked:Boolean = false,
     var islamChecked:Boolean = false,
     var mathsAndEngineeringChecked: Boolean = false,
     var healthAndFitnessChecked: Boolean = false,
     var scienceAndTechnologyChecked: Boolean = false,
     var languagesAndReferenceChecked: Boolean = false,
     var howToAndManualsChecked: Boolean = false,
     var literatureChecked: Boolean = false,
     var artAndCraftChecked: Boolean = false,
     var businessAndFinanceChecked: Boolean = false,
     var cookBooksChecked: Boolean = false,
     var added: Boolean = false
)