package com.bookshelfhub.bookshelfhub.helpers.webapi

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType


class MediaType {

    companion object{
        fun getJson(): MediaType {
             return  "application/json; charset=utf-8".toMediaType()
        }
    }

}