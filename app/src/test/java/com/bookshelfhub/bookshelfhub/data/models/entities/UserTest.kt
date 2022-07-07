package com.bookshelfhub.bookshelfhub.data.models.entities

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserTest{

    @Test
    fun userUploadedDefaultsToFalse(){
        val user = User("", "")
        assertThat(user.uploaded).isFalse()
    }
}