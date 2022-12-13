package com.bookshelfhub.feature.about.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bookshelfhub.core.common.extensions.applyLinks
import com.bookshelfhub.core.common.helpers.textlinkbuilder.TextLinkBuilder
import com.bookshelfhub.core.common.helpers.utils.IntentUtil
import com.bookshelfhub.feature.about.book.databinding.ActivityBookInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import com.bookshelfhub.core.common.helpers.utils.Regex
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class BookInfoActivity : AppCompatActivity() {

    private lateinit var layout: ActivityBookInfoBinding
    private val bookInfoActivityViewModel by viewModels<BookInfoActivityViewModel>()
    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var textLinkBuilder: TextLinkBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = ActivityBookInfoBinding.inflate (layoutInflater)
        setContentView(layout.root)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = bookInfoActivityViewModel.getTitle()

        bookInfoActivityViewModel.getLivePublishedBook().observe(this) { pubBook ->

            val book = pubBook.get()
            val links = listOf(textLinkBuilder.getTextLink(Pattern.compile(Regex.WEB_LINK)) { link ->
                    openLink(link)
            })

            layout.authorTxt.text = String.format(getString(R.string.author), book.author)
            layout.bookIdTxt.text = String.format(
                getString(R.string.isbn),
                bookInfoActivityViewModel.getBookIdFromCompoundId()
            )
            layout.categoryTxt.text = String.format(getString(R.string.category), book.category)

            layout.descriptionTxt.text = book.description
            layout.descriptionTxt.applyLinks(links)

        }

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