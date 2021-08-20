package com.bookshelfhub.bookshelfhub.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.DateUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.const.Regex
import com.bookshelfhub.bookshelfhub.databinding.BookInfoFragmentBinding
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.enums.DateFormat
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.wrappers.textlinkbuilder.TextLinkBuilder
import com.klinker.android.link_builder.applyLinks
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class BookInfoFragment : Fragment() {

    private lateinit var layout: BookInfoFragmentBinding
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var textLinkBuilder:TextLinkBuilder


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout = BookInfoFragmentBinding.inflate(inflater, container, false)

      arguments?.let { it ->
          val isbn = it.getString(Book.ISBN.KEY)!!
          lifecycleScope.launch {
              val book =   localDb.getPublishedBook(isbn)

              withContext(Main){
                  val links =  listOf(textLinkBuilder.getTextLink(Pattern.compile(Regex.URL)) {link ->
                        openLink(link)
                  })

                  book.dateTimePublished?.let {
                      val  date = DateUtil.dateToString(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                      layout.publishedDateTxt.text = date
                  }

                  layout.authorTxt.text = String.format(getString(R.string.author), book.author)
                  layout.isbnTxt.text = String.format(getString(R.string.isbn),book.isbn)
                  layout.categoryTxt.text = String.format(getString(R.string.category),book.category)

                  layout.descriptionTxt.text =  book.description
                  layout.descriptionTxt.applyLinks(links)

              }
          }
      }

        return layout.root
    }

    private fun openLink(link:String){
      val url =  if (!link.contains("http")){
          "https://$link"
        }else{
            link
        }
         startActivity(intentUtil.intent(url))
    }

}