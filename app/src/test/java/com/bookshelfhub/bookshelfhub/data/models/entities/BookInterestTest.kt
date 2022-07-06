package com.bookshelfhub.bookshelfhub.data.models.entities

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BookInterestTest{

    @Test(expected = IllegalArgumentException::class)
    fun userIdCanNotBeEmpty(){
        BookInterest("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun userIdCanNotBeBlank(){
        BookInterest(" ")
    }

    @Test
    fun isBookInterestUploadedDefaultsToFalse(){
        val bookInterest = BookInterest("56789")
        assertThat(bookInterest.uploaded).isFalse()
    }
}