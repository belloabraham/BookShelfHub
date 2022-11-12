package com.bookshelfhub.core.model.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ApiKeys(
    var fixerAccessKey: String? = "",
    var flutterEncKey: String? = "",
    var flutterPublicKey: String? = "",
    var payStackLivePublicKey: String? = "",
    var perspectiveKey: String? = "",
)