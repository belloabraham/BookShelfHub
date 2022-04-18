package com.bookshelfhub.bookshelfhub.data.repos

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.Cart
import com.bookshelfhub.bookshelfhub.data.repos.sources.local.CartItemsDao
import com.bookshelfhub.bookshelfhub.workers.ClearCart
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.Worker
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CartItemsRepo @Inject constructor(
    private val cartItemsDao: CartItemsDao,
    private val worker: Worker) {

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
         // Clear every Items in this cart in the next 15 hours
         val clearCart =
             OneTimeWorkRequestBuilder<ClearCart>()
                 .setInitialDelay( 24, TimeUnit.HOURS)
                 .build()
         worker.enqueueUniqueWork(Tag.CLEAR_CART, ExistingWorkPolicy.REPLACE , clearCart)
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