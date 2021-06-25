package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.FragmentInterestBinding
import com.bookshelfhub.bookshelfhub.observable.BookInterestObservable
import com.bookshelfhub.bookshelfhub.services.database.local.LocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.BookInterestRecord
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.bookshelfhub.bookshelfhub.view.toast.Toasty
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
    lateinit var localDb: LocalDb
    private lateinit var layout: FragmentInterestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentInterestBinding.inflate(inflater, container, false)

        lifecycleScope.launch(IO){
           val  bkInterestRecord = localDb.getBookInterest()

            bookInterest = if (localDb.getBookInterest().isPresent){
                BookInterestObservable(bkInterestRecord.get())
            }else{
                BookInterestObservable(BookInterestRecord())
            }

            withContext(Main){
                layout.bookInterest = bookInterest
                layout.lifecycleOwner = viewLifecycleOwner
            }
        }

        layout.saveBtn.setOnClickListener {

            if (layout.chipGroup.checkedChipIds.size<4){
                Toast(requireActivity()).showToast(R.string.interest_erorr_msg)
            }else{
                lifecycleScope.launch(IO){
                    localDb.addBookInterest(bookInterest.getBookInterestRecord())
                    withContext(Main){
                        activity?.finish()
                    }
                }
            }

        }


        return layout.root
    }


}