package com.bookshelfhub.bookshelfhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bookshelfhub.bookshelfhub.databinding.ActivityBookCategoryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookCategoryActivity : AppCompatActivity() {

    private lateinit var layout:ActivityBookCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookCategoryBinding.inflate(layoutInflater)
        setContentView(layout.root)

    }
}