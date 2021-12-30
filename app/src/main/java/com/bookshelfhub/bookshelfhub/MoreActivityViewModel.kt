package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.enums.Fragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle
): ViewModel() {

    private val fragmentId = savedState.get<Int>(Fragment.ID.KEY)

    fun getFragmentId(): Int? {
        return fragmentId
    }

}