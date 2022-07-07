package com.bookshelfhub.bookshelfhub.data.models.apis.convertion

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class FixerTest{

    private  lateinit var fixer:Fixer

    @Before
    fun setUp() {
        fixer = Fixer(false, Query("", "", 8), Info(1, 1.0), "", 1.0)
    }

    @Test
    fun classNameIsFixer(){
        val fixer =  fixer::class.simpleName
        assertThat(fixer).isEqualTo("Fixer")
    }

    @Test
    fun contains_success_Field(){
        val success =  fixer::class.members.find {
            it.name == "success"
        }
        assertThat(success).isNotNull()
    }

    @Test
    fun contains_info_Field(){
        val info =  fixer::class.members.find {
            it.name == "info"
        }
        assertThat(info).isNotNull()
    }

    @Test
    fun contains_query_Field(){
        val query =  fixer::class.members.find {
            it.name == "query"
        }
        assertThat(query).isNotNull()
    }

    @Test
    fun contains_date_Field(){
        val date =  fixer::class.members.find {
            it.name == "date"
        }
        assertThat(date).isNotNull()
    }

    @Test
    fun contains_result_Field(){
        val result =  fixer::class.members.find {
            it.name == "result"
        }
        assertThat(result).isNotNull()
    }


}