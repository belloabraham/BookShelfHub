package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentInterestBinding
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.observables.BookInterestObservable
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.views.Toast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class BookInterestFragment : Fragment() {

    private lateinit var bookInterestObservable:BookInterestObservable
    private val bookInterestViewModel: BookInterestViewModel by viewModels()
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var userAuth: IUserAuth
    private lateinit var oldBookInterest:BookInterest
    private lateinit var layout: FragmentInterestBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentInterestBinding.inflate(inflater, container, false)

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
                AlertDialogBuilder.with(requireActivity(),  R.string.unsaved_interest_msg)
                    .setCancelable(true)
                    .setPositiveAction(R.string.save){
                        saveBookInterest(layout.saveBtn)
                    }
                    .setNegativeAction(R.string.cancel){
                        requireActivity().finish()
                    }.build()
                    .showDialog(R.string.unsaved_interest_title)
            }else{
                requireActivity().finish()
            }

        }

        layout.saveBtn.setOnClickListener {
            saveBookInterest(it)
        }
        return layout.root
    }

    private fun saveBookInterest(view:View){
       if (layout.chipGroup.checkedChipIds.size<4){
           Snackbar.make(view, R.string.select_more_than_3_msg, Snackbar.LENGTH_LONG)
               .show()
        }else{
            lifecycleScope.launch(IO){
                val bookInterest = bookInterestObservable.getBookInterestRecord()
                 bookInterest.uploaded = false
                 bookInterest.added=true
                localDb.addBookInterest(bookInterest)
                withContext(Main){
                    activity?.let {
                        showToast(R.string.interest_Saved)
                        it.finish()
                    }
                }
            }
        }
    }

}