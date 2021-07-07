package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.databinding.ActivityContentBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.ShelfSearchHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ContentActivity : AppCompatActivity() {

    private lateinit var layout: ActivityContentBinding
    @Inject
    lateinit var localDb: LocalDb
    @Inject
    lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout =   ActivityContentBinding.inflate(layoutInflater)
        setContentView(layout.root)

        val title = intent.getStringExtra(Book.TITLE.KEY)
        val isbn = intent.getStringExtra(Book.ISBN.KEY)
        val isSearchItem = intent.getBooleanExtra(Book.IS_SEARCH_ITEM.KEY, false)
        if (isSearchItem){
            lifecycleScope.launch(IO){
                localDb.addShelfSearchHistory(ShelfSearchHistory(isbn!!, title!!, userAuth.getUserId()))
            }
        }


    }
}