package com.bookshelfhub.bookshelfhub.helpers.utils.settings

enum class Settings(val KEY:String) {
    SHOW_CONTINUE_POPUP("show_continue_popup"),
    LAST_BOOK_RED_TITLE("last_book_red_title"),
    LAST_BOOK_RED_ISBN("last_book_red_isbn"),
    LAST_BOOK_PERCENTAGE("last_book_percentage"),
    NO_OF_TIME_DISMISSED("no_of_time_dismissed"),

    FLUTTER_ENCRYPTION("flutterEncKey"),
    FLUTTER_PUBLIC("flutterPublicKey"),

     API_KEYS("api_keys"),
     FIXER_ENDPOINT("fixerEndpoint"),
     PERSPECTIVE_API("perspectiveKey")
}