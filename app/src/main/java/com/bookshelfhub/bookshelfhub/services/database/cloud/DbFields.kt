package com.bookshelfhub.bookshelfhub.services.database.cloud

enum class DbFields(val KEY:String) {
    USERS("users"),
    USER("user"),
    BOOK_INTEREST("book_interest"),
    PUBLISHED_BOOKS("published_books"),
    PUBLISHED("published"),
    DATE_TIME_PUBLISHED("dateTimePublished"),
    BOOKMARKS("bookmarks"),
    REVIEWS("reviews"),
    VERIFIED("verified"),
    TOTAL_REVIEWS("totalReviews"),
    TOTAL_RATINGS("totalRatings"),
    SHELF("shelf"),
    REVIEW_DATE("reviewDate")

}