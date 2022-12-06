package com.bookshelfhub.feature.home.ui.more

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.bookshelfhub.core.model.entities.BookInterest

/**
 * Used for two way data binding in BookInterestFragment
 */
class BookInterestObservable (private val bookInterest: BookInterest): BaseObservable() {


    @Bindable
    fun getIsHistory():Boolean{
        return bookInterest.historyChecked
    }

    fun setIsHistory(value:Boolean){
        if(bookInterest.historyChecked!=value){
            bookInterest.historyChecked=value
            notifyPropertyChanged(BR.isHistory)
        }
    }

    @Bindable
    fun getIsChristianity():Boolean{
        return bookInterest.christianityChecked
    }

    fun setIsChristianity(value:Boolean){
        if(bookInterest.christianityChecked!=value){
            bookInterest.christianityChecked=value
            notifyPropertyChanged(BR.isChristianity)
        }
    }

    @Bindable
    fun getIsBusinessAndFinance():Boolean{
        return bookInterest.businessAndFinanceChecked
    }

    fun setIsBusinessAndFinance(value:Boolean){
        if(bookInterest.businessAndFinanceChecked!=value){
            bookInterest.businessAndFinanceChecked=value
            notifyPropertyChanged(BR.isBusinessAndFinance)
        }
    }

    @Bindable
    fun getIsTravel():Boolean{
        return bookInterest.travelChecked
    }

    fun setIsTravel(value:Boolean){
        if(bookInterest.travelChecked!=value){
            bookInterest.travelChecked=value
            notifyPropertyChanged(BR.isTravel)
        }
    }

    @Bindable
    fun getIsReligion():Boolean{
        return bookInterest.religionChecked
    }

    fun setIsReligion(value:Boolean){
        if(bookInterest.religionChecked!=value){
            bookInterest.religionChecked=value
            notifyPropertyChanged(BR.isReligion)
        }
    }

    @Bindable
    fun getIsComic():Boolean{
        return bookInterest.comicChecked
    }

    fun setIsComic(value:Boolean){
        if(bookInterest.comicChecked!=value){
            bookInterest.comicChecked=value
            notifyPropertyChanged(BR.isComic)
        }
    }

    @Bindable
    fun getIsScienceAndTechnology():Boolean{
        return bookInterest.scienceAndTechnologyChecked
    }

    fun setIsScienceAndTechnology(value:Boolean){
        if(bookInterest.scienceAndTechnologyChecked!=value){
            bookInterest.scienceAndTechnologyChecked=value
            notifyPropertyChanged(BR.isScienceAndTechnology)
        }
    }

    @Bindable
    fun getIsFiction():Boolean{
        return bookInterest.fictionChecked
    }

    fun setIsFiction(value:Boolean){
        if(bookInterest.fictionChecked!=value){
            bookInterest.fictionChecked=value
            notifyPropertyChanged(BR.isFiction)
        }
    }


    @Bindable
    fun getIsLaw():Boolean{
        return bookInterest.lawChecked
    }

    fun setIsLaw(value:Boolean){
        if(bookInterest.lawChecked!=value){
            bookInterest.lawChecked=value
            notifyPropertyChanged(BR.isLaw)
        }
    }

    @Bindable
    fun getIsArtAndCraft():Boolean{
        return bookInterest.artAndCraftChecked
    }

    fun setIsArtAndCraft(value:Boolean){
        if(bookInterest.artAndCraftChecked!=value){
            bookInterest.artAndCraftChecked=value
            notifyPropertyChanged(BR.isArtAndCraft)
        }
    }

    @Bindable
    fun getIsHealthAndFitness():Boolean{
        return bookInterest.healthAndFitnessChecked
    }

    fun setIsHealthAndFitness(value:Boolean){
        if(bookInterest.healthAndFitnessChecked!=value){
            bookInterest.healthAndFitnessChecked=value
            notifyPropertyChanged(BR.isHealthAndFitness)
        }
    }

    @Bindable
    fun getIsHowToAndManuals():Boolean{
        return bookInterest.howToAndManualsChecked
    }

    fun setIsHowToAndManuals(value:Boolean){
        if(bookInterest.howToAndManualsChecked!=value){
            bookInterest.howToAndManualsChecked=value
            notifyPropertyChanged(BR.isHowToAndManuals)
        }
    }

    @Bindable
    fun getIsIslam():Boolean{
        return bookInterest.islamChecked
    }

    fun setIsIslam(value:Boolean){
        if(bookInterest.islamChecked!=value){
            bookInterest.islamChecked=value
            notifyPropertyChanged(BR.isIslam)
        }
    }

    @Bindable
    fun getIsLiterature():Boolean{
        return bookInterest.literatureChecked
    }

    fun setIsLiterature(value:Boolean){
        if(bookInterest.literatureChecked!=value){
            bookInterest.literatureChecked=value
            notifyPropertyChanged(BR.isLiterature)
        }
    }

    @Bindable
    fun getIsLanguagesAndReference():Boolean{
        return bookInterest.languagesAndReferenceChecked
    }

    fun setIsLanguagesAndReference(value:Boolean){
        if(bookInterest.languagesAndReferenceChecked!=value){
            bookInterest.languagesAndReferenceChecked=value
            notifyPropertyChanged(BR.isLanguagesAndReference)
        }
    }

    @Bindable
    fun getIsMathsAndEngineering():Boolean{
        return bookInterest.mathsAndEngineeringChecked
    }

    fun setIsMathsAndEngineering(value:Boolean){
        if(bookInterest.mathsAndEngineeringChecked!=value){
            bookInterest.mathsAndEngineeringChecked=value
             notifyPropertyChanged(BR.isMathsAndEngineering)
        }
    }


    @Bindable
    fun getIsFashion():Boolean{
        return bookInterest.fashionChecked
    }

    fun setIsFashion(value:Boolean){
        if(bookInterest.fashionChecked!=value){
            bookInterest.fashionChecked=value
            notifyPropertyChanged(BR.isFashion)
        }
    }

    @Bindable
    fun getIsNews():Boolean{
        return bookInterest.newsChecked
    }

    fun setIsNews(value:Boolean){
        if(bookInterest.newsChecked!=value){
            bookInterest.newsChecked=value
             notifyPropertyChanged(BR.isNews)
        }
    }

    @Bindable
    fun getIsEntertainment():Boolean{
        return bookInterest.entertainmentChecked
    }

    fun setIsEntertainment(value:Boolean){
        if(bookInterest.entertainmentChecked!=value){
            bookInterest.entertainmentChecked=value
            notifyPropertyChanged(BR.isEntertainment)
        }
    }

    @Bindable
    fun getIsPolitics():Boolean{
        return bookInterest.politicsChecked
    }

    fun setIsPolitics(value:Boolean){
        if(bookInterest.politicsChecked!=value){
            bookInterest.politicsChecked=value
            notifyPropertyChanged(BR.isPolitics)
        }
    }

    @Bindable
    fun getIsCookBooks():Boolean{
        return bookInterest.cookBooksChecked
    }

    fun setIsCookBooks(value:Boolean){
        if(bookInterest.cookBooksChecked!=value){
            bookInterest.cookBooksChecked=value
             notifyPropertyChanged(BR.isCookBooks)
        }
    }

    @Bindable
    fun getIsBiography():Boolean{
        return bookInterest.biographyChecked
    }

    fun setIsBiography(value:Boolean){
        if(bookInterest.biographyChecked!=value){
            bookInterest.biographyChecked=value
            notifyPropertyChanged(BR.isBiography)
        }
    }

   @Bindable
    fun getIsNutrition():Boolean{
        return bookInterest.nutritionChecked
    }

    fun setIsNutrition(value:Boolean){
        if(bookInterest.nutritionChecked!=value){
            bookInterest.nutritionChecked=value
            notifyPropertyChanged(BR.isNutrition)
        }
    }

    fun getBookInterestRecord(): BookInterest {
        return bookInterest
    }

}