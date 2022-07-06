package com.bookshelfhub.bookshelfhub.extensions

import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringTest {

    @Test
    fun stringContainsUriOrUrl(){
        assertThat("goo.le".containsUrl(Regex.WEB_LINK_IN_TEXT)).isTrue()
    }

    @Test
    fun stringDoesNotContainsUriOrUrl(){
        assertThat("Hello its me".containsUrl(Regex.WEB_LINK_IN_TEXT)).isFalse()
    }
}