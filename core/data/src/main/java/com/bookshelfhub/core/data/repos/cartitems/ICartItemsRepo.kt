package com.bookshelfhub.core.data.repos.cartitems

import androidx.lifecycle.LiveData
import com.bookshelfhub.core.model.entities.CartItem
import java.util.*

interface ICartItemsRepo {
    fun getLiveListOfCartItems(userId: String): LiveData<List<CartItem>>
    fun getLiveTotalCartItemsNo(userId: String): LiveData<Int>

    fun getLiveCartItem(bookId: String): LiveData<Optional<CartItem>>
    suspend fun getListOfCartItems(userId: String): List<CartItem>

    suspend fun addToCart(cart: CartItem)

    suspend fun deleteAllCartItems()

    suspend fun deleteFromCart(isbnList: List<String>)

    suspend fun deleteFromCart(cart: CartItem)
}