package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response

import com.bookshelfhub.bookshelfhub.data.models.apis.perspective.response.TOXICITY
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class TOXICITYTest{
    private lateinit var toxicity: TOXICITY

    @Before
    fun setUp() {
        toxicity = TOXICITY(emptyList(), SummaryScore(1.0, ""))
    }

    @Test
    fun classNameIsTOXICITY(){
        val className =  toxicity::class.simpleName
        assertThat(className).isEqualTo("TOXICITY")
    }

}