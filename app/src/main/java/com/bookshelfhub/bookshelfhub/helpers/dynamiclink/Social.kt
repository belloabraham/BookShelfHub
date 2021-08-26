package com.bookshelfhub.bookshelfhub.helpers.dynamiclink

/**
 * Used as key to get Dynamic Link social attributes value from Remote Config
 */
enum class Social(val KEY:String){
    DESC("user_ref_description"),
    TITLE("user_ref_title"),
    IMAGE_URL("user_ref_image_uri")
}
