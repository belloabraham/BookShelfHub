package com.bookshelfhub.bookshelfhub.data.models.entities

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BookInterestTest{

    @Test
    fun isBookInterestUploadedDefaultsToFalse(){
        val bookInterest = BookInterest("56789")
        assertThat(bookInterest.uploaded).isFalse()
    }
}