package com.bookshelfhub.bookshelfhub.data.repos.cartitems

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem

interface ICartItemsRepo {
    fun getLiveListOfCartItems(userId: String): LiveData<List<CartItem>>
    fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>

    suspend fun addToCart(cart: CartItem)

    suspend fun deleteAllCartItems()

    suspend fun deleteFromCart(isbnList: List<String>)

    suspend fun deleteFromCart(cart: CartItem)
}