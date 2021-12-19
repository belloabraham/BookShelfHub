package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle
): ViewModel() {

    private val title = savedState.get<String>(WebView.TITLE.KEY)
    private val url = savedState.get<String>(WebView.URL.KEY)

    fun getTitle(): String? {
        return title
    }

    fun getUrl(): String? {
        return url
    }

}