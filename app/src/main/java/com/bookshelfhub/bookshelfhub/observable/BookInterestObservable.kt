package com.bookshelfhub.bookshelfhub.observable


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class BookInterestObservable (private val bkInts:BookInterestRecord): BaseObservable() {


    @Bindable
    fun getIsHistory():Boolean{
        return bkInts.historyChecked
    }

    fun setIsHistory(value:Boolean){
        if(bkInts.historyChecked!=value){
            bkInts.historyChecked=value
            notifyPropertyChanged(BR.isHistory)
        }
    }

    @Bindable
    fun getIsChristianity():Boolean{
        return bkInts.christianityChecked
    }

    fun setIsChristianity(value:Boolean){
        if(bkInts.christianityChecked!=value){
            bkInts.christianityChecked=value
            notifyPropertyChanged(BR.isChristianity)
        }
    }

    @Bindable
    fun getIsBusinessAndFinance():Boolean{
        return bkInts.biographyChecked
    }

    fun setIsBusinessAndFinance(value:Boolean){
        if(bkInts.businessAndFinanceChecked!=value){
            bkInts.biographyChecked=value
            notifyPropertyChanged(BR.isBusinessAndFinance)
        }
    }

    @Bindable
    fun getIsTravel():Boolean{
        return bkInts.travelChecked
    }

    fun setIsTravel(value:Boolean){
        if(bkInts.travelChecked!=value){
            bkInts.travelChecked=value
              notifyPropertyChanged(BR.isTravel)
        }
    }

    @Bindable
    fun getIsReligion():Boolean{
        return bkInts.religionChecked
    }

    fun setIsReligion(value:Boolean){
        if(bkInts.religionChecked!=value){
            bkInts.religionChecked=value
              notifyPropertyChanged(BR.isReligion)
        }
    }

    @Bindable
    fun getIsComic():Boolean{
        return bkInts.comicChecked
    }

    fun setIsComic(value:Boolean){
        if(bkInts.comicChecked!=value){
            bkInts.comicChecked=value
            notifyPropertyChanged(BR.isComic)
        }
    }

    @Bindable
    fun getIsScienceAndTechnology():Boolean{
        return bkInts.scienceAndTechnologyChecked
    }

    fun setIsScienceAndTechnology(value:Boolean){
        if(bkInts.scienceAndTechnologyChecked!=value){
            bkInts.scienceAndTechnologyChecked=value
              notifyPropertyChanged(BR.isScienceAndTechnology)
        }
    }

    @Bindable
    fun getIsSport():Boolean{
        return bkInts.sportChecked
    }

    fun setIsSport(value:Boolean){
        if(bkInts.sportChecked!=value){
            bkInts.sportChecked=value
              notifyPropertyChanged(BR.isSport)
        }
    }

    @Bindable
    fun getIsFiction():Boolean{
        return bkInts.fictionChecked
    }

    fun setIsFiction(value:Boolean){
        if(bkInts.fictionChecked!=value){
            bkInts.fictionChecked=value
            notifyPropertyChanged(BR.isFiction)
        }
    }

    @Bindable
    fun getIsMusic():Boolean{
        return bkInts.musicChecked
    }

    fun setIsMusic(value:Boolean){
        if(bkInts.musicChecked!=value){
            bkInts.musicChecked=value
            notifyPropertyChanged(BR.isMusic)
        }
    }

    @Bindable
    fun getIsLaw():Boolean{
        return bkInts.lawChecked
    }

    fun setIsLaw(value:Boolean){
        if(bkInts.lawChecked!=value){
            bkInts.lawChecked=value
            notifyPropertyChanged(BR.isLaw)
        }
    }

    @Bindable
    fun getIsArtAndCraft():Boolean{
        return bkInts.artAndCraftChecked
    }

    fun setIsArtAndCraft(value:Boolean){
        if(bkInts.artAndCraftChecked!=value){
            bkInts.artAndCraftChecked=value
            notifyPropertyChanged(BR.isArtAndCraft)
        }
    }

    @Bindable
    fun getIsPoetry():Boolean{
        return bkInts.poetryChecked
    }

    fun setIsPoetry(value:Boolean){
        if(bkInts.poetryChecked!=value){
            bkInts.poetryChecked=value
            notifyPropertyChanged(BR.isPoetry)
        }
    }

    @Bindable
    fun getIsHealthAndFitness():Boolean{
        return bkInts.healthAndFitnessChecked
    }

    fun setIsHealthAndFitness(value:Boolean){
        if(bkInts.healthAndFitnessChecked!=value){
            bkInts.healthAndFitnessChecked=value
            notifyPropertyChanged(BR.isHealthAndFitness)
        }
    }

    @Bindable
    fun getIsHowToAndManuals():Boolean{
        return bkInts.howToAndManualsChecked
    }

    fun setIsHowToAndManuals(value:Boolean){
        if(bkInts.howToAndManualsChecked!=value){
            bkInts.howToAndManualsChecked=value
            notifyPropertyChanged(BR.isHowToAndManuals)
        }
    }

    @Bindable
    fun getIsIslam():Boolean{
        return bkInts.islamChecked
    }

    fun setIsIslam(value:Boolean){
        if(bkInts.islamChecked!=value){
            bkInts.islamChecked=value
            notifyPropertyChanged(BR.isIslam)
        }
    }

    @Bindable
    fun getIsPhotography():Boolean{
        return bkInts.photographyChecked
    }

    fun setIsPhotography(value:Boolean){
        if(bkInts.photographyChecked!=value){
            bkInts.photographyChecked=value
            notifyPropertyChanged(BR.isPhotography)
        }
    }

    @Bindable
    fun getIsNovel():Boolean{
        return bkInts.novelChecked
    }

    fun setIsNovel(value:Boolean){
        if(bkInts.novelChecked!=value){
            bkInts.novelChecked=value
            notifyPropertyChanged(BR.isNovel)
        }
    }

    @Bindable
    fun getIsLiterature():Boolean{
        return bkInts.literatureChecked
    }

    fun setIsLiterature(value:Boolean){
        if(bkInts.literatureChecked!=value){
            bkInts.literatureChecked=value
            notifyPropertyChanged(BR.isLiterature)
        }
    }

    @Bindable
    fun getIsTransportation():Boolean{
        return bkInts.transportationChecked
    }

    fun setIsTransportation(value:Boolean){
        if(bkInts.transportationChecked!=value){
            bkInts.transportationChecked=value
             notifyPropertyChanged(BR.isTransportation)
        }
    }

    @Bindable
    fun getIsComputerProgramming():Boolean{
        return bkInts.computerProgrammingChecked
    }

    fun setIsComputerProgramming(value:Boolean){
        if(bkInts.computerProgrammingChecked!=value){
            bkInts.computerProgrammingChecked=value
            notifyPropertyChanged(BR.isComputerProgramming)
        }
    }

    @Bindable
    fun getIsDataAnalysis():Boolean{
        return bkInts.dataAnalysisChecked
    }

    fun setIsDataAnalysis(value:Boolean){
        if(bkInts.dataAnalysisChecked!=value){
            bkInts.dataAnalysisChecked=value
            notifyPropertyChanged(BR.isDataAnalysis)
        }
    }

    @Bindable
    fun getIsLanguagesAndReference():Boolean{
        return bkInts.languagesAndReferenceChecked
    }

    fun setIsLanguagesAndReference(value:Boolean){
        if(bkInts.languagesAndReferenceChecked!=value){
            bkInts.languagesAndReferenceChecked=value
            notifyPropertyChanged(BR.isLanguagesAndReference)
        }
    }

    @Bindable
    fun getIsMathsAndEngineering():Boolean{
        return bkInts.mathsAndEngineeringChecked
    }

    fun setIsMathsAndEngineering(value:Boolean){
        if(bkInts.mathsAndEngineeringChecked!=value){
            bkInts.mathsAndEngineeringChecked=value
             notifyPropertyChanged(BR.isMathsAndEngineering)
        }
    }

    @Bindable
    fun getIsHumour():Boolean{
        return bkInts.humourChecked
    }

    fun setIsHumour(value:Boolean){
        if(bkInts.humourChecked!=value){
            bkInts.humourChecked=value
            notifyPropertyChanged(BR.isHumour)
        }
    }

    @Bindable
    fun getIsFashion():Boolean{
        return bkInts.fashionChecked
    }

    fun setIsFashion(value:Boolean){
        if(bkInts.fashionChecked!=value){
            bkInts.fashionChecked=value
            notifyPropertyChanged(BR.isFashion)
        }
    }

    @Bindable
    fun getIsMagazine():Boolean{
        return bkInts.magazineChecked
    }

    fun setIsMagazine(value:Boolean){
        if(bkInts.magazineChecked!=value){
            bkInts.magazineChecked=value
             notifyPropertyChanged(BR.isMagazine)
        }
    }

    @Bindable
    fun getIsNews():Boolean{
        return bkInts.newsChecked
    }

    fun setIsNews(value:Boolean){
        if(bkInts.newsChecked!=value){
            bkInts.newsChecked=value
             notifyPropertyChanged(BR.isNews)
        }
    }

    @Bindable
    fun getIsPsychology():Boolean{
        return bkInts.psychologyChecked
    }

    fun setIsPsychology(value:Boolean){
        if(bkInts.psychologyChecked!=value){
            bkInts.psychologyChecked=value
            notifyPropertyChanged(BR.isPsychology)
        }
    }

    @Bindable
    fun getIsPolitics():Boolean{
        return bkInts.politicsChecked
    }

    fun setIsPolitics(value:Boolean){
        if(bkInts.politicsChecked!=value){
            bkInts.politicsChecked=value
            notifyPropertyChanged(BR.isPolitics)
        }
    }

    @Bindable
    fun getIsCookBooks():Boolean{
        return bkInts.cookBooksChecked
    }

    fun setIsCookBooks(value:Boolean){
        if(bkInts.cookBooksChecked!=value){
            bkInts.cookBooksChecked=value
             notifyPropertyChanged(BR.isCookBooks)
        }
    }

    @Bindable
    fun getIsRomance():Boolean{
        return bkInts.romanceChecked
    }

    fun setIsRomance(value:Boolean){
        if(bkInts.romanceChecked!=value){
            bkInts.romanceChecked=value
            notifyPropertyChanged(BR.isRomance)
        }
    }

    @Bindable
    fun getIsExamAndTestPrep():Boolean{
        return bkInts.examAndTestPrepChecked
    }

    fun setIsExamAndTestPrep(value:Boolean){
        if(bkInts.examAndTestPrepChecked!=value){
            bkInts.examAndTestPrepChecked=value
             notifyPropertyChanged(BR.isExamAndTestPrep)
        }
    }

    @Bindable
    fun getIsBiography():Boolean{
        return bkInts.biographyChecked
    }

    fun setIsBiography(value:Boolean){
        if(bkInts.biographyChecked!=value){
            bkInts.biographyChecked=value
            notifyPropertyChanged(BR.isBiography)
        }
    }


    @Bindable
    fun getIsCivilEng():Boolean{
        return bkInts.civilEngChecked
    }

    fun setIsCivilEng(value:Boolean){
        if(bkInts.civilEngChecked!=value){
            bkInts.civilEngChecked=value
             notifyPropertyChanged(BR.isCivilEng)
        }
    }

    @Bindable
    fun getIsArcheology():Boolean{
        return bkInts.archeologyChecked
    }

    fun setIsArcheology(value:Boolean){
        if(bkInts.archeologyChecked!=value){
            bkInts.archeologyChecked=value
            notifyPropertyChanged(BR.isArcheology)
        }
    }

    @Bindable
    fun getIsEducation():Boolean{
        return bkInts.educationChecked
    }

    fun setIsEducation(value:Boolean){
        if(bkInts.educationChecked!=value){
            bkInts.educationChecked=value
            notifyPropertyChanged(BR.isEducation)
        }
    }

    fun getBookInterestRecord():BookInterestRecord{
        return bkInts
    }

}