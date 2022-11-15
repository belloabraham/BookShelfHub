package com.bookshelfhub.core.model.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ApiKeys(
    var fixerAccessKey: String? = "",
    var payStackLivePublicKey: String? = "",
    var perspectiveKey: String? = "",
)