package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookBinding
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookInfoBinding
import com.bookshelfhub.bookshelfhub.enums.Fragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookInfoActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookInfoBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(layout.root)

        val title = intent.getStringExtra(Fragment.TITLE.KEY)
        val fragmentId = intent.getIntExtra(Fragment.ID.KEY,0)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = title


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.navigate(fragmentId)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}