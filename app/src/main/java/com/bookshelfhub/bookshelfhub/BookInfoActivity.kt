package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookInfoBinding
import com.bookshelfhub.bookshelfhub.book.Book
import com.bookshelfhub.bookshelfhub.ui.Fragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookInfoActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookInfoBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

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
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        //Remove the default fragment that was navigated to set in @res/navigation/book_info_activity_navigation
        navController.popBackStack()

        //Set value for the new fragment bundle to receive
        val args = Bundle()
        args.putString(Book.ISBN.KEY, isbn)

        //Navigate to the new fragment
        navController.navigate(fragmentId, args)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }



}