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
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.ReviewListAdapter
import com.bookshelfhub.bookshelfhub.databinding.ReviewsFragmentBinding
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.UserReview
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class ReviewsFragment : Fragment() {

    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private var binding: ReviewsFragmentBinding?=null
    private var reviewsAdapter: ListAdapter<UserReview, RecyclerViewHolder<UserReview>>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReviewsFragmentBinding.inflate(inflater, container, false)
        val layout = binding!!

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
                reviewsAdapter = ReviewListAdapter().getAdapter()
                layout.reviewRecView.adapter = reviewsAdapter
                reviewsAdapter!!.submitList(reviews)

            }

        })

        return layout.root
    }

    override fun onDestroyView() {
        binding=null
        reviewsAdapter=null
        super.onDestroyView()
    }
}