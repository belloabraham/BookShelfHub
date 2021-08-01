package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartActivityViewModel @Inject constructor(private val localDb: ILocalDb, val userAuth:IUserAuth): ViewModel(){


    val userId = userAuth.getUserId()

    init {
        val books = listOf(
            Cart(userId,"1", name="A Quite place 1", coverUrl =  "https://i.ibb.co/ckJQXYC/stay-home-blue.png",
                category = "Cook Books", tag = ""),
            Cart(userId,"2", name="A Quite place 2", coverUrl =  "https://i.ibb.co/ph2hL56/time-for-yourself.png",
                category = "Cook Books", tag = ""),
            Cart(userId,"3", name="A Quite place 3", coverUrl =  "https://i.ibb.co/gMpTyLY/home-sweet-home.png",
                category = "Cook Books", tag = ""),
            Cart(userId,"4", name="A Quite place 4", coverUrl =  "https://i.ibb.co/YdZMYzW/best-place.png",
                category = "Cook Books", tag = ""),
            Cart(userId,"5", name="A Quite place 5", coverUrl =  "https://i.ibb.co/gMpTyLY/greate-weekend.png",
                category = "Cook Books", tag = "")
        )

        viewModelScope.launch {
            localDb.addToCarts(books)
        }
    }

    fun getListOfCartItems(userId:String): LiveData<List<Cart>> {
        return localDb.getLiveListOfCartItems(userId)
    }

}