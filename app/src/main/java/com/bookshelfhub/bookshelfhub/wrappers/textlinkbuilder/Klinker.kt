package com.bookshelfhub.bookshelfhub.wrappers.textlinkbuilder

import android.graphics.Color
import android.widget.TextView
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder

open class Klinker() {

    open fun createTextLink(textView:TextView, text:String, highLightColorRes:Int, linkColorCode:String = "#ff320066", onLinkClick: ()->Unit){

        val link: Link = Link(text)
            .setTextColor(Color.parseColor(linkColorCode)) // optional, defaults to holo blue
            .setTextColorOfHighlightedLink(highLightColorRes) // optional, defaults to holo blue
            .setHighlightAlpha(.4f) // optional, defaults to .15f
            .setUnderlined(true) // optional, defaults to true
            .setBold(true)
            .setOnClickListener {
                onLinkClick()
            }
          LinkBuilder.on(textView)
            .addLink(link)
            .build();
    }

}