package com.bookshelfhub.bookshelfhub.data.repos.sources.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.Cart
import com.google.common.base.Optional

@Dao
interface CartItemsDao {
    @Query("SELECT COUNT(*) FROM Cart WHERE userId =:userId")
    fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>

    @Query("DELETE FROM Cart WHERE isbn in (:isbnList)")
    suspend fun deleteFromCart(isbnList: List<String>)

    @Query("DELETE FROM Cart")
    suspend fun deleteAllCartItems()

    @Delete
    suspend fun deleteFromCart(cart: Cart)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart: Cart)

    @Query("SELECT * FROM Cart WHERE userId =:userId")
    fun getLiveListOfCartItems(userId: String):LiveData<List<Cart>>

    @Query("SELECT * FROM Cart WHERE userId =:userId")
    suspend fun getListOfCartItems(userId: String):List<Cart>
}