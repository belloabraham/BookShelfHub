package com.bookshelfhub.core.remote.cloud_functions

import com.google.firebase.functions.HttpsCallableResult

interface ICloudFunctions {
    suspend fun call(functionName: String, data: HashMap<String, Any?>): HttpsCallableResult?
}