package com.bookshelfhub.feature.webview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewActivityViewModel @Inject constructor(
     savedState: SavedStateHandle
): ViewModel() {

    private val title = savedState.get<String>(WebView.TITLE)
    private val url = savedState.get<String>(WebView.URL)

    fun getTitle(): String? {
        return title
    }

    fun getUrl(): String? {
        return url
    }

}