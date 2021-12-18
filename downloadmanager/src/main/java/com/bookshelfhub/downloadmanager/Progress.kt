package com.bookshelfhub.downloadmanager

import java.io.Serializable

class Progress(private var currentBytes: Long, private var totalBytes: Long): Serializable {


    override fun toString(): String {
        return "Progress{" +
                "currentBytes=" + currentBytes +
                ", totalBytes=" + totalBytes +
                '}'
    }

}