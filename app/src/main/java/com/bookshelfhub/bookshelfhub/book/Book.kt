package com.bookshelfhub.bookshelfhub.book

/**
 * Used as Key for key value pairs
 */
enum class Book(val KEY:String) {
    TITLE("title"),
    ISBN("isbn"),
    IS_SEARCH_ITEM("is_search_item"),
    AUTHOR("author"),
    RATING_DIFF("rating_diff")
}