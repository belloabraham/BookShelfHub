package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartActivityViewModel @Inject constructor(private val localDb: ILocalDb): ViewModel(){


    fun getLiveCartItems(): LiveData<List<Cart>> {
        return localDb.getLiveCartItems()
    }

}