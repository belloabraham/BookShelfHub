package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityCartBinding
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
  //Used to load cart Fragment and card Info fragment
  private lateinit var layout: ActivityCartBinding
  private lateinit var navController: NavController
  private lateinit var appBarConfiguration: AppBarConfiguration
  @Inject
  lateinit var userAuth:IUserAuth
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    layout = ActivityCartBinding.inflate(layoutInflater)
    setContentView(layout.root)
    setSupportActionBar(layout.toolbar)

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    navController = navHostFragment.findNavController()
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

  }

   override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
