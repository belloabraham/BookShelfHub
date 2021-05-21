package com.bookshelfhub.bookshelfhub.wrapper.textlinkbuilder

import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bookshelfhub.bookshelfhub.R
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder

open class Klinker() {

    open fun createTextLink(textView:TextView, text:String, color:Int, event: ()->Unit){

        val link: Link = Link(text)
            .setTextColor(Color.parseColor("#ff320066")) // optional, defaults to holo blue
            .setTextColorOfHighlightedLink(color) // optional, defaults to holo blue
            .setHighlightAlpha(.4f) // optional, defaults to .15f
            .setUnderlined(true) // optional, defaults to true
            .setBold(true)
            .setOnClickListener {
                event()
            }
          LinkBuilder.on(textView)
            .addLink(link)
            .build();
    }

}