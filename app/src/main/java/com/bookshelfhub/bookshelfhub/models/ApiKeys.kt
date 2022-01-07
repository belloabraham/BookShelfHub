package com.bookshelfhub.bookshelfhub.models

data class ApiKeys(
    val fixerEndpoint:String,
    val payStackLivePublicKey:String,
    val payStackLivePrivateKey: String,
    val perspectiveKey:String,
    val flutterPublicKey:String,
    val flutterEncKey:String
)
