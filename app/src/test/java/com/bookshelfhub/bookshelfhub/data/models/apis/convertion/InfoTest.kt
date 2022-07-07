package com.bookshelfhub.bookshelfhub.data.models.apis.convertion

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class InfoTest{

    private lateinit var info: Info

    @Before
    fun setUp() {
        info = Info(1, 1.0)
    }

    @Test
    fun classNameIsInfo(){
        val info =  info::class.simpleName
        assertThat(info).isEqualTo("Info")
    }

    @Test
    fun contains_timestamp_Field(){
        val timestamp =  info::class.members.find {
            it.name == "timestamp"
        }
        assertThat(timestamp).isNotNull()
    }

    @Test
    fun contains_rate_Field(){
        val rate =  info::class.members.find {
            it.name == "rate"
        }
        assertThat(rate).isNotNull()
    }


}