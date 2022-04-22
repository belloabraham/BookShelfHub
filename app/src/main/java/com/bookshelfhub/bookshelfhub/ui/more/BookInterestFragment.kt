package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentInterestBinding
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.views.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class BookInterestFragment : Fragment() {

    private lateinit var bookInterestObservable: BookInterestObservable
    private val bookInterestViewModel: BookInterestViewModel by viewModels()

    @Inject
    lateinit var userAuth: IUserAuth

    private lateinit var oldBookInterest: BookInterest
    private var binding: FragmentInterestBinding?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentInterestBinding.inflate(inflater, container, false)
        val layout = binding!!

        bookInterestViewModel.getBookInterest().observe(viewLifecycleOwner, Observer { bookInterest ->
            bookInterestObservable = if(bookInterest.isPresent && bookInterest.get().added){
                BookInterestObservable(bookInterest.get())
            }else{
                BookInterestObservable(BookInterest(userAuth.getUserId()))
            }
            oldBookInterest = bookInterestObservable.getBookInterestRecord().copy()
            layout.bookInterest = bookInterestObservable
            layout.lifecycleOwner = viewLifecycleOwner
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(oldBookInterest!=bookInterestObservable.getBookInterestRecord()){
                AlertDialogBuilder.with(R.string.unsaved_interest_msg, requireActivity())
                    .setCancelable(true)
                    .setPositiveAction(getString(R.string.save)){
                        saveBookInterest(layout.saveBtn, layout)
                    }
                    .setNegativeAction(getString(R.string.cancel)){
                        requireActivity().finish()
                    }
                    .build()
                    .showDialog(R.string.unsaved_interest_title)
            }else{
                requireActivity().finish()
            }

        }

        layout.saveBtn.setOnClickListener {
            saveBookInterest(it, layout)
        }
        return layout.root
    }

    private fun saveBookInterest(view:View, layout:FragmentInterestBinding){
       if (layout.chipGroup.checkedChipIds.size<4){
           Snackbar.make(view, R.string.select_more_than_3_msg, Snackbar.LENGTH_LONG)
               .show()
        }else{
                val bookInterest = bookInterestObservable.getBookInterestRecord()
                 bookInterest.uploaded = false
                 bookInterest.added=true
                 bookInterestViewModel.addBookInterest(bookInterest)
                    activity?.let {
                        showToast(R.string.interest_Saved)
                        it.finish()
                    }
        }
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

}