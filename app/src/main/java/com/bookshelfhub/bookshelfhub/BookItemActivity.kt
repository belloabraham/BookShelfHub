package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.Utils.LocalDateTimeUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookItemBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.StoreSearchHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BookItemActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookItemBinding
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var userAuth: IUserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

          layout =  ActivityBookItemBinding.inflate(layoutInflater)
          setContentView(layout.root)

        val title = intent.getStringExtra(Book.TITLE.KEY)!!
        val isbn = intent.getStringExtra(Book.ISBN.KEY)!!
        val author = intent.getStringExtra(Book.AUTHOR.KEY)!!
        val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)

        if (isSearchItem){
            lifecycleScope.launch(IO){
                localDb.addStoreSearchHistory(StoreSearchHistory(isbn, title, userAuth.getUserId(), author, LocalDateTimeUtil.getDateTimeAsString()))
            }
        }




    }
}