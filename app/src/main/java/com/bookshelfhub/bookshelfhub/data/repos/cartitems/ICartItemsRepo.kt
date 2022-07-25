package com.bookshelfhub.bookshelfhub.data.repos.cartitems

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.google.common.base.Optional

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