package com.bookshelfhub.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.core.data.Fragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreActivityViewModel @Inject constructor(
     savedState: SavedStateHandle
): ViewModel() {

    private val fragmentId = savedState.get<Int>(Fragment.ID)

    fun getFragmentId(): Int? {
        return fragmentId
    }
}