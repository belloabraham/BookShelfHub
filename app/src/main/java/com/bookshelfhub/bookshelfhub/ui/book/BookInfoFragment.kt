package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.databinding.BookInfoFragmentBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class BookInfoFragment : Fragment() {

    private val bookInfoViewModel: BookInfoViewModel by viewModels()
    private lateinit var layout: BookInfoFragmentBinding
    @Inject
    lateinit var localDb: ILocalDb


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout = BookInfoFragmentBinding.inflate(inflater, container, false)

      arguments?.let {
          val isbn = it.getString(Book.ISBN.KEY)!!
          lifecycleScope.launch {
              val book =   localDb.getPublishedBook(isbn)
              withContext(Main){
                  layout.descriptionTxt.text = book.description
                  layout.publishedDateTxt.text = String.format(getString(R.string.published_date),book.description)
              }
          }
      }

        return layout.root
    }


}