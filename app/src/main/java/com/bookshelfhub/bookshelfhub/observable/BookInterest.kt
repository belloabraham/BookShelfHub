package com.bookshelfhub.bookshelfhub.observable


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import javax.inject.Inject


class BookInterest @Inject constructor(private val bkInts:BookInterestRecord): BaseObservable() {

    @Bindable
    fun getIsHistoryChecked():Boolean{
        return bkInts.historyChecked
    }

    fun setIsHistoryChecked(value:Boolean){
        if(bkInts.historyChecked!=value){
            bkInts.historyChecked=value
            notifyPropertyChanged(BR.isHistoryChecked)
        }
    }



}