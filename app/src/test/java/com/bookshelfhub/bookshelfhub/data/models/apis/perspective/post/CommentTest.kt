package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class CommentTest{

    private lateinit var comment: Comment

    @Before
    fun setUp() {
        comment = Comment("")
    }

    @Test
    fun classNameIsComment(){
        val className =  comment::class.simpleName
        assertThat(className).isEqualTo("Comment")
    }

    @Test
    fun contains_text_Field(){
        val text =  comment::class.members.find {
            it.name == "text"
        }
        assertThat(text).isNotNull()
    }
}