package com.bookshelfhub.bookshelfhub.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.EditTextFormatter
import com.bookshelfhub.bookshelfhub.databinding.BookInfoFragmentBinding
import com.bookshelfhub.bookshelfhub.databinding.FragmentCardInfoBinding
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class CardInfoFragment : Fragment() {

    private lateinit var layout: FragmentCardInfoBinding
    @Inject
    lateinit var localDb: ILocalDb


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout = FragmentCardInfoBinding.inflate(inflater, container, false)


        layout.cardNoTxt.addTextChangedListener(EditTextFormatter(userInputLen = 16,
        inputChunkDivider = "-", inputChunkLen = 4))

        layout.cardNoTxt.addTextChangedListener(EditTextFormatter(userInputLen = 4,
            inputChunkDivider = "/", inputChunkLen = 2))

        return layout.root
    }


}