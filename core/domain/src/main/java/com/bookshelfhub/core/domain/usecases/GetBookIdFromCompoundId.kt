package com.bookshelfhub.core.domain.usecases

class GetBookIdFromCompoundId {

    operator fun invoke(compoundId:String): String {
        return if(compoundId.contains("-")){
            val pubIdAndOrBookId = compoundId.split("-").toTypedArray()
            pubIdAndOrBookId[1]
        }else{
            compoundId
        }
    }

}