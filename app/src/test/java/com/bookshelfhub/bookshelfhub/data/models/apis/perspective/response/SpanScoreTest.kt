package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class SpanScoreTest{

    private lateinit var spanScore: SpanScore

    @Before
    fun setUp() {
        spanScore = SpanScore(1, 1, Score(1.0, ""))
    }

    @Test
    fun classNameIsSpanScore(){
        val className =  spanScore::class.simpleName
        assertThat(className).isEqualTo("SpanScore")
    }

    @Test
    fun contains_begin_Field(){
        val begin =  spanScore::class.members.find {
            it.name == "begin"
        }
        assertThat(begin).isNotNull()
    }

    @Test
    fun contains_end_Field(){
        val end =  spanScore::class.members.find {
            it.name == "end"
        }
        assertThat(end).isNotNull()
    }

    @Test
    fun contains_score_Field(){
        val score =  spanScore::class.members.find {
            it.name == "score"
        }
        assertThat(score).isNotNull()
    }



}