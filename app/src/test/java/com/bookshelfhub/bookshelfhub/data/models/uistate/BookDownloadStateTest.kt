package com.bookshelfhub.bookshelfhub.data.models.uistate

import com.google.common.truth.Truth.assertThat

class BookDownloadStateTest{

    fun bookDownloadStateHasErrorDefaultsToFalse(){
        val bookDownloadState = BookDownloadState("",0)
        assertThat(bookDownloadState.hasError).isFalse()
    }
}