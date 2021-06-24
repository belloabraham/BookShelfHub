package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.databinding.FragmentInterestBinding
import com.bookshelfhub.bookshelfhub.observable.BookInterest
import com.bookshelfhub.bookshelfhub.services.database.Database
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class InterestFragment : Fragment() {

    @Inject
    lateinit var database: Database
    @Inject
    lateinit var bookInterest: BookInterest

    private lateinit var layout: FragmentInterestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentInterestBinding.inflate(inflater, container, false)

         layout.bookInterest = bookInterest
         layout.lifecycleOwner = viewLifecycleOwner

        return layout.root
    }


}