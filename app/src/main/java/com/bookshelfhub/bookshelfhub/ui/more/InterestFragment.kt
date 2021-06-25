package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentInterestBinding
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.observable.BookInterestObservable
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.services.database.Database
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.bookshelfhub.bookshelfhub.view.toast.Toasty
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

    private lateinit var bookInterest:BookInterestObservable
    @Inject
    lateinit var database: Database
    @Inject
    lateinit var localDb: LocalDb
    @Inject
    lateinit var userAuth: UserAuth
    @Inject
    lateinit var connectionUtil: ConnectionUtil
    private lateinit var layout: FragmentInterestBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentInterestBinding.inflate(inflater, container, false)

        lifecycleScope.launch(IO){
            val userId = userAuth.getUserId()
            val  bkInterestRecord = localDb.getBookInterest(userId)

            bookInterest = if (localDb.getBookInterest(userId).isPresent){
                BookInterestObservable(bkInterestRecord.get())
            }else{
                BookInterestObservable(BookInterestRecord(userId))
            }

            withContext(Main){
                layout.bookInterest = bookInterest
                layout.lifecycleOwner = viewLifecycleOwner
            }
        }


        layout.saveBtn.setOnClickListener {

            if (!connectionUtil.isConnected()){
                Snackbar.make(it, R.string.interest_internet_save_error, Snackbar.LENGTH_LONG)
                    .show()
            }else if (layout.chipGroup.checkedChipIds.size<4){
                AlertDialogHelper(requireActivity(), cancelable = true).showAlertDialog(R.string.select_more_than_3, R.string.select_more_than_3_msg)
            }else{
                lifecycleScope.launch(IO){
                    database.addBookInterest(bookInterest.getBookInterestRecord())
                    withContext(Main){
                        activity?.finish()
                    }
                }
            }

        }


        return layout.root
    }


    override fun onStart() {
        super.onStart()

    }


}