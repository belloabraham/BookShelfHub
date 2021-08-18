package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.recycler.ReviewListAdapter
import com.bookshelfhub.bookshelfhub.databinding.ReviewsFragmentBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.DbFields
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ReviewsFragment : Fragment() {

    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private lateinit var layout: ReviewsFragmentBinding
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = ReviewsFragmentBinding.inflate(inflater, container, false)

        val reviewsLimit = 300L
        val userId = userAuth.getUserId()
        arguments?.let { it ->
            val isbn = it.getString(Book.ISBN.KEY)!!

            cloudDb.getListOfDataAsync(DbFields.PUBLISHED_BOOKS.KEY, isbn, DbFields.REVIEWS.KEY, UserReview::class.java, DbFields.VERIFIED.KEY, whereValue = true, userId, reviewsLimit){ reviews, _->

                if (reviews.isNotEmpty()){
                    layout.progressBar.visibility = View.GONE

                    val reviewsAdapter = ReviewListAdapter().getAdapter()
                    layout.reviewRecView.adapter = reviewsAdapter
                    reviewsAdapter.submitList(reviews)

                }
            }

        }


        return layout.root
    }

}