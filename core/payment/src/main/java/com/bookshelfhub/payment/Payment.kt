package com.bookshelfhub.payment

enum class Payment(val KEY:String) {
    USER_DATA("userData"),
    USER_ID("userId"),
    ORDERED_BOOKS("orderedBooks"),
    IDS_OF_BOOKS_BOUGHT("idsOfBooksBought"),
    TRANSACTION_REF("transactionReference"),
    BOOK_NAMES("bookNames"),
    SUBTRACTED_USER_EARNINGS("subtractedUserEarnings"),
    PAYMENT_SDK_TYPE("paymentSdkType"),
    CURRENCY_TO_CHARGE_FOR_BOOK_SALE("currencyToChargeForBookSale"),
    USER_REFERRER_ID("userReferrerId"),
    USER_REFERRER_COMMISSION("userReferrerCommission"),
}