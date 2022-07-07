package com.bookshelfhub.bookshelfhub.data.models.apis.perspective.post

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class PostBodyTest{

    private lateinit var postBody:PostBody

    @Before
    fun setUp() {
        postBody = PostBody(Comment(""), emptyList(), RequestedAttributes(TOXICITY()))
    }

    @Test
    fun classNameIsPostBody(){
        val className =  postBody::class.simpleName
        assertThat(className).isEqualTo("PostBody")
    }

    @Test
    fun contains_comment_Field(){
        val comment =  postBody::class.members.find {
            it.name == "comment"
        }
        assertThat(comment).isNotNull()
    }

    @Test
    fun contains_languages_Field(){
        val languages =  postBody::class.members.find {
            it.name == "languages"
        }
        assertThat(languages).isNotNull()
    }

    @Test
    fun contains_requestedAttributes_Field(){
        val requestedAttributes =  postBody::class.members.find {
            it.name == "requestedAttributes"
        }
        assertThat(requestedAttributes).isNotNull()
    }


}