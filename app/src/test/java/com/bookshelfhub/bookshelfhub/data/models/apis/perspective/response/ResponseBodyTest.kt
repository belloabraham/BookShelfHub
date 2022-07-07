package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ResponseBodyTest{
    private lateinit var responseBody: ResponseBody

    @Before
    fun setUp() {
        responseBody = ResponseBody(AttributeScores(TOXICITY(emptyList(), SummaryScore(1.0 , ""))), emptyList(), emptyList())
    }

    @Test
    fun classNameIsResponseBody(){
        val className =  responseBody::class.simpleName
        assertThat(className).isEqualTo("ResponseBody")
    }

    @Test
    fun contains_attributeScores_Field(){
        val attributeScores =  responseBody::class.members.find {
            it.name == "attributeScores"
        }
        assertThat(attributeScores).isNotNull()
    }

    @Test
    fun contains_detectedLanguages_Field(){
        val detectedLanguages =  responseBody::class.members.find {
            it.name == "detectedLanguages"
        }
        assertThat(detectedLanguages).isNotNull()
    }

    @Test
    fun contains_languages_Field(){
        val languages =  responseBody::class.members.find {
            it.name == "languages"
        }
        assertThat(languages).isNotNull()
    }

}