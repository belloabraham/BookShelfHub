package com.bookshelfhub.core.remote.database

object RemoteDataFields{

    const val VIDEO_LIST = "video_list"

    //Collections
    //users/userId/{userdata}
    const val USERS_COLL = "users"
    //published_books/bookId/{book}
    const val PUBLISHED_BOOKS_COLL = "published_books"

    //users/userId/{userdata}/ordered_books/{bookId}/{docData}
    const val ORDERED_BOOKS_COLL = "ordered_books"

    //users/userId/{userdata}/bookmarks/{docIds}/{docData}
    const val BOOKMARKS_COLL = "bookmarks"

    //published_books/bookId/reviews/userId
    const val REVIEWS_COLL = "reviews"


    //users/userId/earnings/userId/{total:0}
    const val EARNINGS = "earnings"


    const val NOTIFICATION_TOKEN="notificationToken"

    //Users/userId/Transaction
    const val TRANSACTIONS_COLL = "transactions"

    //Fields
    //Must be same as .services.database.local.room.entities.UserReview.verified
    const val VERIFIED = "verified"

    //Must be same as .services.database.local.room.entities.PublishedBook.totalReviews and totalRatings
    const val TOTAL_REVIEWS = "totalReviews"
    const val TOTAL_RATINGS = "totalRatings"

    const val USER = "user"
    const val BOOK_INTEREST = "bookInterest"

    //Must be same as .services.database.local.room.entities.PublishedBook.published
    const val PUBLISHED = "published"
    const val APPROVED ="approved"
    const val SERIAL_NO ="serialNo"

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    const val REVIEW_DATE_TIME = "dateTime"







}