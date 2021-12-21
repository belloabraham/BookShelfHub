package com.bookshelfhub.bookshelfhub.services.database

import com.bookshelfhub.bookshelfhub.helpers.Json
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class Util @Inject constructor(private val json:Json) {


     fun <T: Any> queryToListType(querySnapshot: QuerySnapshot?, type:Class<T>): List<T> {
        var dataList = emptyList<T>()

        querySnapshot?.let {
            if (!it.isEmpty) {
                for (doc in it) {
                    if (doc.exists()) {
                        val data = doc.data
                        dataList = dataList.plus(json.fromAny(data, type))
                    }
                }
            }
        }
        return dataList

    }

}