package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bookshelfhub.bookshelfhub.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private lateinit var layout: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityCartBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)

        layout.makePaymentFab.setOnClickListener {

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}