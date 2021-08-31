package com.bookshelfhub.bookshelfhub.services.payment

enum class Payment(val KEY:String) {
    USER_DATA("user_data"),
    USER_ID("user_id"),
    PAY_STACK_PUBLIC_KEY("paystack_public_key"),
    BOOKS_AND_REF("books_bought_and_referrers")
}