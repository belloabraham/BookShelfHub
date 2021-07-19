package com.bookshelfhub.bookshelfhub.extensions

import org.apache.commons.text.WordUtils


@JvmSynthetic
fun String.capitalize(): String? {
    return WordUtils.capitalize(this)
}