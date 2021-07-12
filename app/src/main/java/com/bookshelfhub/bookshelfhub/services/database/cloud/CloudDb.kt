package com.bookshelfhub.bookshelfhub.services.database.cloud

import com.bookshelfhub.bookshelfhub.wrapper.Json
import javax.inject.Inject

class CloudDb @Inject constructor(val json: Json) : Firestore(json) {

}