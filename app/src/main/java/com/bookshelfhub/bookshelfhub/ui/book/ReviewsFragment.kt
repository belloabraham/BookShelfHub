package com.bookshelfhub.bookshelfhub.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.bookshelfhub.bookshelfhub.adapters.recycler.ReviewListAdapter
import com.bookshelfhub.bookshelfhub.databinding.ReviewsFragmentBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.DbFields
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = ReviewsFragmentBinding.inflate(inflater, container, false)

        arguments?.let { it ->

            reviewsViewModel.getUserReviews().observe(viewLifecycleOwner, Observer { reviews ->

                if (reviews.isNotEmpty()){
                    layout.progressBar.visibility = GONE

                    layout.reviewRecView.addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    layout.reviewRecView.visibility = VISIBLE
                    val reviewsAdapter = ReviewListAdapter().getAdapter()
                    layout.reviewRecView.adapter = reviewsAdapter
                    reviewsAdapter.submitList(reviews)

                }

            })


        }


        return layout.root
    }

}