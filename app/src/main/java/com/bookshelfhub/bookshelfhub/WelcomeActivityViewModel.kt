package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle
): ViewModel() {

    private val referrer = savedState.get<String>(Referrer.ID.KEY)

    fun getReferrer(): String? {
        return referrer
    }

}