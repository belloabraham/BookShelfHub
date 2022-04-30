package com.bookshelfhub.bookshelfhub.helpers.payment

enum class Payment(val KEY:String) {
    USER_DATA("user_data"),
    USER_ID("user_id"),
    ORDERED_BOOKS("orderedBooks"),
    IDS_OF_BOOKS_BOUGHT("ids_of_books_bought"),
    TRANSACTION_REF("transactionReference"),
    BOOK_NAMES("bookNames"),
    USER_REFERRER_ID("userReferrerId"),
    USER_REFERRER_COMMISSION("userReferrerCommission"),
}