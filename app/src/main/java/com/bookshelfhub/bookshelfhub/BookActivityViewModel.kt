package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.*
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookActivityViewModel @Inject constructor(
  private val localDb: ILocalDb,
  val savedState: SavedStateHandle
  ): ViewModel(){

  private var liveOrderedBook: LiveData<OrderedBooks> = MutableLiveData()


  init {
      liveOrderedBook = localDb.getLiveOrderedBooks("")
  }

  fun addShelfSearchHistory(shelfSearchHistory: ShelfSearchHistory){
    viewModelScope.launch(IO){
      localDb.addShelfSearchHistory(shelfSearchHistory)
    }
  }

  fun addReadHistory(readHistory: History){
    viewModelScope.launch(IO){
      localDb.addReadHistory(readHistory)
    }
  }


}