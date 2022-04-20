package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.Cart
import com.google.common.base.Optional

@Dao
abstract class CartItemsDao : BaseDao<Cart> {
    
    @Query("SELECT COUNT(*) FROM Cart WHERE userId =:userId")
    abstract fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>

    @Query("DELETE FROM Cart WHERE isbn in (:isbnList)")
    abstract suspend fun deleteFromCart(isbnList: List<String>)

    @Query("DELETE FROM Cart")
    abstract suspend fun deleteAllCartItems()

    @Query("SELECT * FROM Cart WHERE userId =:userId")
    abstract fun getLiveListOfCartItems(userId: String):LiveData<List<Cart>>

    @Query("SELECT * FROM Cart WHERE userId =:userId")
    abstract suspend fun getListOfCartItems(userId: String):List<Cart>
}