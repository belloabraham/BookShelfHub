package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class AttributeScoresTest{
    private lateinit var attributeScores: AttributeScores

    @Before
    fun setUp() {
        attributeScores = AttributeScores(TOXICITY(emptyList(), SummaryScore(1.0, "")))
    }

    @Test
    fun classNameIsComment(){
        val className =  attributeScores::class.simpleName
        assertThat(className).isEqualTo("AttributeScores")
    }

    @Test
    fun contains_TOXICITY_Field(){
        val toxicity =  attributeScores::class.members.find {
            it.name == "TOXICITY"
        }
        assertThat(toxicity).isNotNull()
    }
}