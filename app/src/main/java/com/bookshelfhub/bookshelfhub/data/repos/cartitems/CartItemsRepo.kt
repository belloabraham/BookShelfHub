package com.bookshelfhub.bookshelfhub.data.repos.cartitems

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.sources.local.CartItemsDao
import com.bookshelfhub.bookshelfhub.workers.ClearCart
import com.bookshelfhub.bookshelfhub.workers.Tag
import com.bookshelfhub.bookshelfhub.workers.Worker
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CartItemsRepo @Inject constructor(
    private val cartItemsDao: CartItemsDao,
    private val worker: Worker) :
    ICartItemsRepo {


     override fun getLiveListOfCartItems(userId: String): LiveData<List<CartItem>> {
        return  cartItemsDao.getLiveListOfCartItems(userId)
    }

     override fun getLiveTotalCartItemsNo(userId: String): LiveData<Int> {
        return  cartItemsDao.getLiveTotalCartItemsNo(userId)
    }

     override suspend fun addToCart(cart: CartItem) {
         withContext(IO){cartItemsDao.insertOrReplace(cart)}
         // Clear every Items in this cart in the next 15 hours
         val clearCart =
             OneTimeWorkRequestBuilder<ClearCart>()
                 .setInitialDelay( 24, TimeUnit.HOURS)
                 .build()
         worker.enqueueUniqueWork(Tag.CLEAR_CART, ExistingWorkPolicy.REPLACE , clearCart)
    }

     override suspend fun deleteAllCartItems() {
         withContext(IO){cartItemsDao.deleteAllCartItems()}
    }

     override suspend fun deleteFromCart(isbnList: List<String>) {
         withContext(IO){ cartItemsDao.deleteFromCart(isbnList)}
    }

     override suspend fun deleteFromCart(cart: CartItem) {
         withContext(IO){cartItemsDao.delete(cart)}
    }
    
}