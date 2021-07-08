package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentInterestBinding
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.observables.BookInterestObservable
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterest
import com.bookshelfhub.bookshelfhub.view.toast.Toast
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
class InterestFragment : Fragment() {

    private lateinit var bookInterestObservable:BookInterestObservable
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var localDb: LocalDb
    @Inject
    lateinit var userAuth: UserAuth
    private lateinit var oldBookInterest:BookInterest
    private lateinit var layout: FragmentInterestBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentInterestBinding.inflate(inflater, container, false)

        lifecycleScope.launch(IO){
            val userId = userAuth.getUserId()
            val  bkInterestRecord = localDb.getBookInterest(userId)

            bookInterestObservable = if (localDb.getBookInterest(userId).isPresent){
                BookInterestObservable(bkInterestRecord.get())
            }else{
                BookInterestObservable(BookInterest(userId))
            }
            withContext(Main){
                oldBookInterest = bookInterestObservable.getBookInterestRecord().copy()
                layout.bookInterest = bookInterestObservable
                layout.lifecycleOwner = viewLifecycleOwner
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(oldBookInterest!=bookInterestObservable.getBookInterestRecord()){
                AlertDialogHelper(requireActivity(),{
                    saveBookInterest(layout.saveBtn)
                },{
                    requireActivity().finish()
                }, cancelable = true)
                    .showAlertDialog(R.string.unsaved_interest_title, R.string.unsaved_interest_msg, R.string.save, R.string.cancel)
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
                database.addBookInterest(bookInterest)
                withContext(Main){
                    activity?.let {
                        Toast(it).showToast(R.string.interest_Saved)
                        it.finish()
                    }
                }
            }
        }
    }

}