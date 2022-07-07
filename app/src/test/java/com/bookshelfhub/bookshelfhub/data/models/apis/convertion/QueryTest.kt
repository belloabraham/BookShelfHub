package com.bookshelfhub.bookshelfhub.data.models.apis.convertion

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class QueryTest{
    private lateinit var query: Query

    @Before
    fun setUp() {
        query = Query("", "", 1)
    }

    @Test
    fun classNameIsQuery(){
        val query =  query::class.simpleName
        assertThat(query).isEqualTo("Query")
    }

    @Test
    fun contains_from_Field(){
        val from =  query::class.members.find {
            it.name == "from"
        }
        assertThat(from).isNotNull()
    }

    @Test
    fun contains_to_Field(){
        val to =  query::class.members.find {
            it.name == "to"
        }
        assertThat(to).isNotNull()
    }

    @Test
    fun contains_amount_Field(){
        val amount =  query::class.members.find {
            it.name == "amount"
        }
        assertThat(amount).isNotNull()
    }

}