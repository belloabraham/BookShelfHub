package com.bookshelfhub.bookshelfhub.helpers.cloudfunctions

import com.google.firebase.functions.HttpsCallableResult

interface ICloudFunctions {
    suspend fun call(functionName: String, data: HashMap<String, Any?>): HttpsCallableResult?
}