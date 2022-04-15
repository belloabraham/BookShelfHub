package com.bookshelfhub.bookshelfhub.data.repos.sources.remote

object RemoteDataFields{

    const val VIDEO_LIST = "video_list"

    //Collections
    const val USERS = "users"
    const val PUBLISHED_BOOKS = "published_books"
    const val ORDERED_BOOKS = "ordered_books"
    const val EARNINGS = "earnings"

    //Sub Collections
    const val BOOKMARKS = "bookmarks"
    const val REVIEWS_COLL = "reviews"
    const val TRANSACTIONS = "transactions"

    //Fields
    //Must be same as .services.database.local.room.entities.UserReview.verified
    const val VERIFIED = "verified"

    const val REFERRER_ID = "referrerId"

    //Must be same as .services.database.local.room.entities.PublishedBook.totalReviews and totalRatings
    const val TOTAL_REVIEWS = "totalReviews"
    const val TOTAL_RATINGS = "totalRatings"

    const val USER = "user"
    const val BOOK_INTEREST = "book_interest"

    //Must be same as .services.database.local.room.entities.OrderedBooks.downloadUrl
    const val DOWNLOAD_URL = "downloadUrl"

    //Must be same as .services.database.local.room.entities.PublishedBook.published
    const val PUBLISHED = "published"

    //Must be same as .services.database.local.room.entities.PublishedBook.dateTime
    const val DATE_TIME_PUBLISHED = "dateTime"

    //Must be same as .services.database.local.room.entities.OrderedBooks.userId
    const val USER_ID = "userId"

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    const val ORDER_DATE_TIME = "dateTime"

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    const val REVIEW_DATE_TIME = "dateTime"







}