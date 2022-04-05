package com.bookshelfhub.bookshelfhub.data.models

/**
 * An interface that all model for main book search result must implement
 */
interface ISearchResult {
    val isbn: String
    val title: String
}