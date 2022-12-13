package com.bookshelfhub.core.remote.cloud_functions.firebase

import com.bookshelfhub.core.remote.cloud_functions.CloudFunctions
import com.bookshelfhub.core.remote.cloud_functions.ICloudFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseCloudFunctions : ICloudFunctions {

    private var functions = Firebase.functions(CloudFunctions.defaultLocation)

    override suspend fun call(functionName:String, data: HashMap<String, Any?>): HttpsCallableResult? {
      return  functions.getHttpsCallable(functionName).call(data).await()
    }

}