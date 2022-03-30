package com.bookshelfhub.bookshelfhub.domain.data.repos.sources.remote

enum class DbFields(val KEY:String) {

    VIDEO_LIST("video_list"),

    //Collections
    USERS("users"),
    PUBLISHED_BOOKS("published_books"),
    ORDERED_BOOKS("ordered_books"),
    EARNINGS("earnings"),

    //Sub Collections
    BOOKMARKS("bookmarks"),
    REVIEWS("reviews"),
    TRANSACTIONS("transactions"),

    //Fields
    //Must be same as .services.database.local.room.entities.UserReview.verified
    VERIFIED("verified"),

    REFERRER_ID("referrerId"),

    //Must be same as .services.database.local.room.entities.PublishedBook.totalReviews and totalRatings
    TOTAL_REVIEWS("totalReviews"),
    TOTAL_RATINGS("totalRatings"),

    USER("user"),
    BOOK_INTEREST("book_interest"),

    //Must be same as .services.database.local.room.entities.OrderedBooks.downloadUrl
    DOWNLOAD_URL("downloadUrl"),

    //Must be same as .services.database.local.room.entities.PublishedBook.published
    PUBLISHED("published"),

    //Must be same as .services.database.local.room.entities.PublishedBook.dateTime
    DATE_TIME_PUBLISHED("dateTime"),

    //Must be same as .services.database.local.room.entities.OrderedBooks.userId
    USER_ID("userId"),

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    ORDER_DATE_TIME("dateTime"),

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    REVIEW_DATE_TIME("dateTime")







}