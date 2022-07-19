package com.bookshelfhub.bookshelfhub.data.models.entities

import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.google.common.truth.Truth
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class UserReviewTest {

    private lateinit var userReview: UserReview

    @Before
    fun setUp() {
        userReview = UserReview("","", 0.0, "", false, "", false)
    }

    @Test
    fun isVerifiedTimeSameAsRemoteDataFieldsVerified(){
        val verified =  userReview::class.members.find {
            it.name == "verified"
        }
        Truth.assertThat(verified!!.name == RemoteDataFields.VERIFIED).isTrue()
    }
}