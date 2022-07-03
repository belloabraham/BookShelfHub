package com.bookshelfhub.bookshelfhub.extensions

import com.bookshelfhub.bookshelfhub.helpers.utils.Regex
import org.junit.Assert
import org.junit.Test

class StringTest {

    @Test
    fun stringContainsUriOrUrl(){
        Assert.assertTrue("goo.le".containsUrl(Regex.WEB_LINK_IN_TEXT))
    }

    @Test
    fun stringDoesNotContainsUriOrUrl(){
        Assert.assertFalse("Hello its me".containsUrl(Regex.WEB_LINK_IN_TEXT))
    }
}