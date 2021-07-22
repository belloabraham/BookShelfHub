package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityProfileBinding
import com.bookshelfhub.bookshelfhub.enums.Profile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreActivity : AppCompatActivity() {

    private lateinit var layout: ActivityProfileBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(layout.root)
        val title = intent.getIntExtra(Profile.TITLE.KEY, 0)
        val fragmentId = intent.getIntExtra(Profile.FRAGMENT_ID.KEY,0)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.setTitle(title)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        navigateTo(fragmentId)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    private fun navigateTo(fragmentId:Int){
        navController.popBackStack()
        navController.navigate(fragmentId)
    }

   /* override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_profile)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }*/
}