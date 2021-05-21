package com.bookshelfhub.bookshelfhub.wrapper.textlinkbuilder

import android.widget.TextView

open class TextLinkBuilder(): Klinker() {

    override fun createTextLink(textView: TextView, text: String, color: Int, event: () -> Unit) {
        super.createTextLink(textView, text, color, event)
    }
}