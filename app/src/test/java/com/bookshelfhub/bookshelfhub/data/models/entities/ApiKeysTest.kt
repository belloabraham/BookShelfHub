package com.bookshelfhub.bookshelfhub.data.models.entities

import org.junit.Assert.*
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ApiKeysTest {

    private lateinit var apiKeys:ApiKeys

    @Before
    fun setUp() {
        apiKeys = ApiKeys()
    }

    @Test
    fun containsFixerAPIAccessKey(){
       val fixerAccessKey =  apiKeys::class.members.find {
            it.name == "fixerAccessKey"
        }
       assertThat(fixerAccessKey).isNotNull()
    }

    @Test
    fun containsPayStackLivePublicKey(){
        val payStackLivePublicKey =  apiKeys::class.members.find {
            it.name == "payStackLivePublicKey"
        }
        assertThat(payStackLivePublicKey).isNotNull()
    }

    @Test
    fun containsPerspectiveAPIKey(){
        val perspectiveKey =  apiKeys::class.members.find {
            it.name == "perspectiveKey"
        }
        assertThat(perspectiveKey).isNotNull()
    }

    @Test
    fun containsFlutterEncAPIKey(){
        val flutterEncKey =  apiKeys::class.members.find {
            it.name == "flutterEncKey"
        }
        assertThat(flutterEncKey).isNotNull()
    }

    @Test
    fun containsFlutterPublicKey(){
        val flutterPublicKey =  apiKeys::class.members.find {
            it.name == "flutterPublicKey"
        }
        assertThat(flutterPublicKey).isNotNull()
    }

}