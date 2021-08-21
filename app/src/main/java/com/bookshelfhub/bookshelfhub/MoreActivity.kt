package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityMoreBinding
import com.bookshelfhub.bookshelfhub.enums.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMoreBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(layout.root)
        val title = intent.getIntExtra(Fragment.TITLE.KEY, 0)
        val fragmentId = intent.getIntExtra(Fragment.ID.KEY,0)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.setTitle(title)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

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

    /* override fun onSupportNavigateUp(): Boolean {
         val navController = findNavController(R.id.nav_host_fragment_content_profile)
         return navController.navigateUp(appBarConfiguration)
                 || super.onSupportNavigateUp()
     }*/
}