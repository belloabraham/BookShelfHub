package com.bookshelfhub.bookshelfhub.data.models.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ApiKeys(
    var fixerAccessKey: String? = "",
    var flutterEncKey: String? = "",
    var flutterPublicKey: String? = "",
    var payStackLivePrivateKey: String? = "",
    var payStackLivePublicKey: String? = "",
    var perspectiveKey: String? = "",
)