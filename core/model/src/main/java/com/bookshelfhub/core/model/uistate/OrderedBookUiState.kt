package com.bookshelfhub.core.model.uistate

import com.bookshelfhub.core.model.ISearchResult

data class OrderedBookUiState(
    override val bookId:String,
    override val name:String,
    override val coverDataUrl:String,
    override val pubId: String,
    override val serialNo:Long,
) : IOrderedBookUiState, ISearchResult