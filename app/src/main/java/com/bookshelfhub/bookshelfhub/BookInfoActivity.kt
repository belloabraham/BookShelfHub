package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookInfoBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookInfoActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookInfoBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val bookInfoActivityViewModel:BookInfoActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(layout.root)


        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = bookInfoActivityViewModel.getTitle()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        //Remove the default fragment that was navigated to set in @res/navigation/book_info_activity_navigation
        navController.popBackStack()

        //Set value for the new fragment bundle to receive
        val bundle = bundleOf(Book.ISBN.KEY to bookInfoActivityViewModel.getIsbn())

        //Navigate to the new fragment
        navController.navigate(bookInfoActivityViewModel.getFragmentId(), bundle)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }



}