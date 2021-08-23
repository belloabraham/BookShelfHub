package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityMoreBinding
import com.bookshelfhub.bookshelfhub.ui.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMoreBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(layout.root)
        val fragmentId = intent.getIntExtra(Fragment.ID.KEY,0)

        setSupportActionBar(layout.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigateTo(fragmentId)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun navigateTo(fragmentId:Int){
        //***Remove existing fragment as the fragment set as the initial in res/navigation will load first
        navController.popBackStack()
        navController.navigate(fragmentId)
    }

}