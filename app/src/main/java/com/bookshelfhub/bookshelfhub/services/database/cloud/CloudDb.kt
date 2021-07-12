package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.wrapper.Json
import javax.inject.Inject

class CloudDb : Firestore {
    @Inject constructor( json: Json) : super(json)
    constructor() : super()
}