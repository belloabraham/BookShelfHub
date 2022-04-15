package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import com.bookshelfhub.bookshelfhub.data.models.entities.Cart
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.CartItemsDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartItemsRepo @Inject constructor(private val cartItemsDao: CartItemsDao) {

     suspend fun getListOfCartItems(userId: String): List<Cart> {
        return  withContext(IO){cartItemsDao.getListOfCartItems(userId)}
    }

     fun getLiveListOfCartItems(userId: String): LiveData<List<Cart>> {
        return  cartItemsDao.getLiveListOfCartItems(userId)
    }

     fun getLiveTotalCartItemsNo(userId: String): LiveData<Int> {
        return  cartItemsDao.getLiveTotalCartItemsNo(userId)
    }

     suspend fun addToCart(cart: Cart) {
         withContext(IO){cartItemsDao.addToCart(cart)}
    }

     suspend fun deleteAllCartItems() {
         withContext(IO){cartItemsDao.deleteAllCartItems()}
    }

     suspend fun deleteFromCart(isbnList: List<String>) {
         withContext(IO){ cartItemsDao.deleteFromCart(isbnList)}
    }

     suspend fun deleteFromCart(cart: Cart) {
         withContext(IO){cartItemsDao.deleteFromCart(cart)}
    }
    
}