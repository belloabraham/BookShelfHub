package com.bookshelfhub.bookshelfhub.data.models.entities

import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class PublishedBookTest {

    private lateinit var publishedBook: PublishedBook

    @Before
    fun setUp() {
        publishedBook = PublishedBook("")
    }

    @Test
    fun isPublishedFieldSameAsRemoteDatabasePublishedField(){
        val published =  publishedBook::class.members.find {
            it.name == "published"
        }
        assertThat(published!!.name == RemoteDataFields.PUBLISHED).isTrue()
    }

    @Test
    fun isApprovedFieldSameAsRemoteDatabaseApprovedField(){
        val approved =  publishedBook::class.members.find {
            it.name == "approved"
        }
        assertThat(approved!!.name == RemoteDataFields.APPROVED).isTrue()
    }

    @Test
    fun isSerialNoFieldSameAsRemoteDatabaseSerialNoField(){
        val serialNo =  publishedBook::class.members.find {
            it.name == "serialNo"
        }
        assertThat(serialNo!!.name == RemoteDataFields.SERIAL_NO).isTrue()
    }
}