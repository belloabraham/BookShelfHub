package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem

@Dao
abstract class CartItemsDao : BaseDao<CartItem> {
    
    @Query("SELECT COUNT(*) FROM CartItems WHERE userId =:userId")
    abstract fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>

    @Query("DELETE FROM CartItems WHERE bookId in (:isbnList)")
    abstract suspend fun deleteFromCart(isbnList: List<String>)

    @Query("DELETE FROM CartItems")
    abstract suspend fun deleteAllCartItems()

    @Query("SELECT * FROM CartItems WHERE userId =:userId")
    abstract fun getLiveListOfCartItems(userId: String):LiveData<List<CartItem>>

    @Query("SELECT * FROM CartItems WHERE userId =:userId")
    abstract suspend fun getListOfCartItems(userId: String):List<CartItem>
}