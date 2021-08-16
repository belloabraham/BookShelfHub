package com.bookshelfhub.bookshelfhub.ui.book

import androidx.lifecycle.ViewModel
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookInfoViewModel @Inject constructor(private var localDb: ILocalDb): ViewModel() {


}