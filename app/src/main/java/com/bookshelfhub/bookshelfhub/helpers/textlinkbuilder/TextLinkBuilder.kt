package com.bookshelfhub.bookshelfhub.helpers.textlinkbuilder

import android.graphics.Color
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import java.util.regex.Pattern

class TextLinkBuilder() {

     fun getTextLink(text:String, onLinkClick: (String)->Unit): Link {

        val link = Link(text)
        return createTextLink(link){
            onLinkClick(it)
        }
    }

     fun getTextLink(pattern:Pattern, onLinkClick: (String)->Unit): Link {
        val link = Link(pattern)
       return  createTextLink(link){
            onLinkClick(it)
        }
    }

    private fun createTextLink(link:Link, onLinkClick: (String)->Unit): Link {

           link.setHighlightAlpha(.4f) // optional, defaults to .15f
            .setUnderlined(true) // optional, defaults to true
            .setOnClickListener {
                onLinkClick(it)
            }

        return link
    }

}