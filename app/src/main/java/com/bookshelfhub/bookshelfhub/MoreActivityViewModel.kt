package com.bookshelfhub.bookshelfhub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.data.Fragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreActivityViewModel @Inject constructor(
    val savedState: SavedStateHandle
): ViewModel() {

    private val fragmentId = savedState.get<Int>(Fragment.ID)

    fun getFragmentId(): Int? {
        return fragmentId
    }


}