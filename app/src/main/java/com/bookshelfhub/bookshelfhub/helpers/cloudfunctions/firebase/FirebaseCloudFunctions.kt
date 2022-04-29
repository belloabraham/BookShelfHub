package com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.firebase

import com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.CloudFunctions
import com.bookshelfhub.bookshelfhub.helpers.cloudfunctions.ICloudFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseCloudFunctions : ICloudFunctions {

    private var functions = Firebase.functions(CloudFunctions.defaultLocation)

    override suspend fun call(functionName:String, data:HashMap<String, String?>?): HttpsCallableResult? {
      return  functions.getHttpsCallable(functionName)
            .call(data).await()
    }

}