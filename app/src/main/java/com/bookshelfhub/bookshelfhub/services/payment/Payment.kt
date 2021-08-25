package com.bookshelfhub.bookshelfhub.services.payment

enum class Payment(val KEY:String) {
    USERDATA("user_data"),
    USER_ID("user_id"),
    BOOKS_AND_REF("books_bought_and_referrers")
}