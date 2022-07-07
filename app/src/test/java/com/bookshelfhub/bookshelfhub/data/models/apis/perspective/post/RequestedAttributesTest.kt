package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RequestedAttributesTest{

    private lateinit var requestedAttributes: RequestedAttributes

    @Before
    fun setUp() {
        requestedAttributes = RequestedAttributes(TOXICITY())
    }

    @Test
    fun classNameIsInfo(){
        val requestedAttributes =  requestedAttributes::class.simpleName
        assertThat(requestedAttributes).isEqualTo("RequestedAttributes")
    }

    @Test
    fun contains_TOXICITY_Field(){
        val toxicity =  requestedAttributes::class.members.find {
            it.name == "TOXICITY"
        }
        assertThat(toxicity).isNotNull()
    }
}