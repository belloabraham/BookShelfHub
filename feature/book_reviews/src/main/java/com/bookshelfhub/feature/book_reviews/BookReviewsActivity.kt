package com.bookshelfhub.feature.book_reviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.bookshelfhub.feature.book_reviews.adapters.UserReviewListAdapter
import com.bookshelfhub.feature.book_reviews.databinding.ActivityBookReviewsBinding

class BookReviewsActivity : AppCompatActivity() {
    private lateinit var layout: ActivityBookReviewsBinding
    private val bookReviewsViewModel by viewModels<BookReviewsActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookReviewsBinding.inflate (layoutInflater)
        setContentView(layout.root)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = getString(R.string.ratings_reviews)
        val reviewsAdapter = UserReviewListAdapter().getAdapter()
        layout.reviewRecView.adapter = reviewsAdapter
        layout.reviewRecView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        bookReviewsViewModel.getTop300UserReviews().observe(this) { reviews ->
            if (reviews.isNotEmpty()){
                layout.progressBar.visibility = View.GONE
                layout.reviewRecView.visibility = View.VISIBLE
                reviewsAdapter.submitList(reviews)
            }
        }

    }

}