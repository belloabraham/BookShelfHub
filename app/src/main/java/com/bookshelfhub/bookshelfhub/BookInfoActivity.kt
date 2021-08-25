package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookInfoBinding
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.ui.Fragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookInfoActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookInfoBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(layout.root)

        val title = intent.getStringExtra(Book.TITLE.KEY)
        val fragmentId = intent.getIntExtra(Fragment.ID.KEY,0)
        val isbn = intent.getStringExtra(Book.ISBN.KEY)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = title


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.popBackStack()

        val args = Bundle()
        args.putString(Book.ISBN.KEY, isbn)
        navController.navigate(fragmentId, args)
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }



}