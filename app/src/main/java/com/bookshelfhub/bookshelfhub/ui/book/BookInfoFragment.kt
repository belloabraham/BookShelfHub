package com.bookshelfhub.bookshelfhub.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.Regex
import com.bookshelfhub.bookshelfhub.databinding.BookInfoFragmentBinding
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateFormat
import com.bookshelfhub.bookshelfhub.helpers.textlinkbuilder.TextLinkBuilder
import com.klinker.android.link_builder.applyLinks
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class BookInfoFragment : Fragment() {

    private val bookInfoViewModel: BookInfoViewModel by viewModels()
    private var binding: BookInfoFragmentBinding? =null
    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var textLinkBuilder: TextLinkBuilder


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BookInfoFragmentBinding.inflate(inflater, container, false)
        val layout = binding!!

            bookInfoViewModel.getLivePublishedBook().observe(viewLifecycleOwner, Observer { pubBook->
                val book = pubBook.get()
                val links =  listOf(textLinkBuilder.getTextLink(Pattern.compile(Regex.WEB_LINK)) { link ->
                    openLink(link)
                })

                book.publishedDate?.let {
                    val  localDate = DateUtil.dateToString(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                    layout.publishedDateTxt.text = localDate
                }

                layout.authorTxt.text = String.format(getString(R.string.author), book.author)
                layout.isbnTxt.text = String.format(getString(R.string.isbn),book.isbn)
                layout.categoryTxt.text = String.format(getString(R.string.category),book.category)

                layout.descriptionTxt.text =  book.description
                layout.descriptionTxt.applyLinks(links)

            })

        return layout.root
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
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