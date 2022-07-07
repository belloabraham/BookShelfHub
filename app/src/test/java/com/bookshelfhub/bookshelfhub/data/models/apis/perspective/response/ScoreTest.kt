package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ScoreTest{
    private lateinit var score: Score

    @Before
    fun setUp() {
        score = Score(1.0, "")
    }

    @Test
    fun classNameIsScore(){
        val className =  score::class.simpleName
        assertThat(className).isEqualTo("Score")
    }

    @Test
    fun contains_value_Field(){
        val value =  score::class.members.find {
            it.name == "value"
        }
        assertThat(value).isNotNull()
    }

    @Test
    fun contains_type_Field(){
        val type =  score::class.members.find {
            it.name == "type"
        }
        assertThat(type).isNotNull()
    }
}