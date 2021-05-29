package com.bookshelfhub.bookshelfhub.models

data class User(var name:String, var email: String, var phone:String, var photoUri:String, var isUploaded: Boolean = false, var authType:String)
