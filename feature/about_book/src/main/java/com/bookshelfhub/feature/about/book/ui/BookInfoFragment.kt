package com.bookshelfhub.feature.about.book.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bookshelfhub.core.common.extensions.applyLinks
import com.bookshelfhub.core.common.helpers.textlinkbuilder.TextLinkBuilder
import com.bookshelfhub.core.common.helpers.utils.IntentUtil
import com.bookshelfhub.core.common.helpers.utils.datetime.DateFormat
import com.bookshelfhub.core.common.helpers.utils.datetime.DateUtil
import com.bookshelfhub.feature.about.book.BookInfoActivityViewModel
import com.bookshelfhub.feature.about.book.R
import com.bookshelfhub.feature.about.book.databinding.BookInfoFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import com.bookshelfhub.core.common.helpers.utils.Regex
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class BookInfoFragment : Fragment() {

    private val bookInfoActivityViewModel: BookInfoActivityViewModel by activityViewModels()
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

            bookInfoActivityViewModel.getLivePublishedBook().observe(viewLifecycleOwner) { pubBook ->

                val book = pubBook.get()
                val links =
                    listOf(textLinkBuilder.getTextLink(Pattern.compile(Regex.WEB_LINK)) { link ->
                        openLink(link)
                    })

                book.publishedDate?.let {
                    val localDate = DateUtil.getHumanReadable(
                        it.toDate(),
                        DateFormat.DD_MM_YYYY.completeFormatValue
                    )
                    layout.publishedDateTxt.text = localDate
                }

                layout.authorTxt.text = String.format(getString(R.string.author), book.author)
                layout.isbnTxt.text = String.format(
                    getString(R.string.isbn),
                    bookInfoActivityViewModel.getBookIdFromCompoundId()
                )
                layout.categoryTxt.text = String.format(getString(R.string.category), book.category)

                layout.descriptionTxt.text = book.description
                layout.descriptionTxt.applyLinks(links)

            }

        return layout.root
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
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