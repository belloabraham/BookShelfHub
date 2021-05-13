package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bookshelfhub.bookshelfhub.databinding.ActivityMainBinding
import com.bookshelfhub.bookshelfhub.databinding.ActivityWelcomeBinding
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuthViewModel
import com.bookshelfhub.bookshelfhub.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var layout: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(layout.root)
    }
}