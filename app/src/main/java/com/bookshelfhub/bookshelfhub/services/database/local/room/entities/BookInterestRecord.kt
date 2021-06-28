package com.bookshelfhub.bookshelfhub.services.database.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bookshelfhub.bookshelfhub.models.IBookInterest

@Entity(tableName= "BookInterest")
data class BookInterestRecord(
    @PrimaryKey
    override var userId: String,
    override var uploaded: Boolean = false,
    override var historyChecked:Boolean = false,
    override var comicChecked:Boolean = false,
    override var musicChecked:Boolean = false,
    override var computerProgrammingChecked:Boolean = false,
    override var biographyChecked:Boolean = false,
    override var magazineChecked:Boolean = false,
    override var poetryChecked:Boolean = false,
    override var romanceChecked:Boolean = false,
    override var religionChecked:Boolean = false,
    override var humourChecked:Boolean = false,
    override var sportChecked:Boolean = false,
    override var travelChecked:Boolean = false,
    override var lawChecked:Boolean = false,
    override var transportationChecked:Boolean = false,
    override var psychologyChecked:Boolean = false,
    override var politicsChecked:Boolean = false,
    override var dataAnalysisChecked:Boolean = false,
    override var novelChecked:Boolean = false,
    override var fashionChecked:Boolean = false,
    override var newsChecked:Boolean = false,
    override var photographyChecked:Boolean = false,
    override var christianityChecked:Boolean = false,
    override var islamChecked:Boolean = false,
    override var mathsAndEngineeringChecked: Boolean = false,
    override var healthAndFitnessChecked: Boolean = false,
    override var scienceAndTechnologyChecked: Boolean = false,
    override var languagesAndReferenceChecked: Boolean = false,
    override var examAndTestPrepChecked: Boolean = false,
    override var howToAndManualsChecked: Boolean = false,
    override var literatureChecked: Boolean = false,
    override var artAndCraftChecked: Boolean = false,
    override var fictionChecked: Boolean = false,
    override var businessAndFinanceChecked: Boolean = false,
    override var cookBooksChecked: Boolean = false,
    override var educationChecked: Boolean = false,
    override var civilEngChecked: Boolean = false,
    override var archeologyChecked: Boolean = false,
    override var added: Boolean = false
) : IBookInterest