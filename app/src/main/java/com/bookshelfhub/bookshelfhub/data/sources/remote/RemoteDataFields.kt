package com.bookshelfhub.bookshelfhub.data.sources.remote

object RemoteDataFields{

    const val VIDEO_LIST = "video_list"

    //Collections
    //users/userId/{userdata}
    const val USERS_COLL = "users"
    //published_books/bookId/{book}
    const val PUBLISHED_BOOKS_COLL = "published_books"

    //users/userId/{userdata}/ordered_books/{docIds}/{docData}
    const val ORDERED_BOOKS_COLL = "ordered_books"

    //users/userId/{userdata}/bookmarks/{docIds}/{docData}
    const val BOOKMARKS_COLL = "bookmarks"



    const val REVIEWS_COLL = "reviews"


    const val EARNINGS = "earnings"


    const val NOTIFICATION_TOKEN="notificationToken"


    //Sub Collections


    const val TRANSACTIONS_COLL = "transactions"

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
    const val APPROVED ="approved"
    const val SERIAL_NO ="serialNo"

    //Must be same as .services.database.local.room.entities.PublishedBook.dateTime
    const val DATE_TIME_PUBLISHED = "dateTime"

    //Must be same as .services.database.local.room.entities.OrderedBooks.userId
    const val USER_ID = "userId"

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    const val ORDER_DATE_TIME = "dateTime"

    //Used Must be same as .services.database.local.room.entities.OrderedBooks.dateTime
    const val REVIEW_DATE_TIME = "dateTime"







}