package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class TOXICITYTest{

    private lateinit var toxicity: TOXICITY

    @Before
    fun setUp() {
        toxicity = TOXICITY()
    }

    @Test
    fun classNameIsTOXICITY(){
        val className =  toxicity::class.simpleName
        assertThat(className).isEqualTo("TOXICITY")
    }

}