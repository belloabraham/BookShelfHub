package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class SummaryScoreTest{
    private lateinit var summaryScore: SummaryScore

    @Before
    fun setUp() {
        summaryScore = SummaryScore(1.0, "")
    }

    @Test
    fun classNameIsSummaryScore(){
        val className =  summaryScore::class.simpleName
        assertThat(className).isEqualTo("SummaryScore")
    }

    @Test
    fun contains_value_Field(){
        val value =  summaryScore::class.members.find {
            it.name == "value"
        }
        assertThat(value).isNotNull()
    }

    @Test
    fun contains_type_Field(){
        val type =  summaryScore::class.members.find {
            it.name == "type"
        }
        assertThat(type).isNotNull()
    }

}