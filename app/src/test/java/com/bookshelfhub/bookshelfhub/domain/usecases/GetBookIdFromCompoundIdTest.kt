package com.bookshelfhub.bookshelfhub.domain.usecases

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetBookIdFromCompoundIdTest {

    private lateinit var getBookIdFromCompoundId: GetBookIdFromCompoundId

    @Before
    fun setup(){
        getBookIdFromCompoundId = GetBookIdFromCompoundId()
    }

      @Test
      fun outputShouldEqualsInputIfInputNotContainsHyphen(){
         Assert.assertTrue("Hello" == getBookIdFromCompoundId("Hello"))
      }

    @Test
    fun inputLengthShouldBeGreaterThanOutputLengthIfInputContainsHyphen(){
        val id = "Hello-12345325235"
        Assert.assertNotEquals(id.length, getBookIdFromCompoundId(id).length)
    }

}