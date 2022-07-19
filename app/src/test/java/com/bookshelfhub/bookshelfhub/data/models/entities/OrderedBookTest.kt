package com.bookshelfhub.bookshelfhub.data.models.entities

import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class OrderedBookTest {

    private lateinit var orderedBook: OrderedBook

    @Before
    fun setUp() {
        orderedBook = OrderedBook("",
            0.0, "",
            "", "",
            " ","",
            "", null,
            0, 0 , 0, "", 0.0, "", 0.0)
    }

    @Test
    fun isDateTimeSameAsRemoteDataFieldsDateTime(){
        val dateTime =  orderedBook::class.members.find {
            it.name == "dateTime"
        }
        assertThat(dateTime!!.name == RemoteDataFields.REVIEW_DATE_TIME).isTrue()
    }
}