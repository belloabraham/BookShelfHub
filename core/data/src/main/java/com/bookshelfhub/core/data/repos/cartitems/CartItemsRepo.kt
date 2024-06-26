package com.bookshelfhub.core.data.repos.cartitems

import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.bookshelfhub.core.common.worker.Tag
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.database.AppDatabase
import com.bookshelfhub.core.model.entities.CartItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CartItemsRepo @Inject constructor(
    appDatabase: AppDatabase,
    private val worker: Worker,
   ) : ICartItemsRepo {

    private val ioDispatcher: CoroutineDispatcher = IO
    private val cartItemsDao = appDatabase.getCartItemsDao()

     override fun getLiveListOfCartItems(userId: String): LiveData<List<CartItem>> {
        return  cartItemsDao.getLiveListOfCartItems(userId)
    }

    override fun getLiveCartItem(bookId: String): LiveData<Optional<CartItem>> {
        return  cartItemsDao.getLiveCartItem(bookId)
    }

    override suspend fun getListOfCartItems(userId: String): List<CartItem> {
        return  withContext(ioDispatcher){cartItemsDao.getListOfCartItems(userId)}
    }

     override fun getLiveTotalCartItemsNo(userId: String): LiveData<Int> {
        return  cartItemsDao.getLiveTotalCartItemsNo(userId)
    }

     override suspend fun addToCart(cart: CartItem) {
         withContext(ioDispatcher){cartItemsDao.insertOrReplace(cart)}
         // Clear every Items in this cart in the next 15 hours
         val clearCart =
             OneTimeWorkRequestBuilder<
                     ClearCart>()
                 .setInitialDelay( 1, TimeUnit.DAYS)
                 .build()
         worker.enqueueUniqueWork(Tag.CLEAR_CART, ExistingWorkPolicy.REPLACE , clearCart)
    }

     override suspend fun deleteAllCartItems() {
         return withContext(ioDispatcher){cartItemsDao.deleteAllCartItems()}
    }

     override suspend fun deleteFromCart(isbnList: List<String>) {
         return withContext(ioDispatcher){ cartItemsDao.deleteFromCart(isbnList)}
    }

     override suspend fun deleteFromCart(cart: CartItem) {
        return withContext(ioDispatcher){cartItemsDao.delete(cart)}
    }
    
}