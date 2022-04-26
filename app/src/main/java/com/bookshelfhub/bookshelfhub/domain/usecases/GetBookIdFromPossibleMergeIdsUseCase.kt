package com.bookshelfhub.bookshelfhub.domain.usecases

class GetBookIdFromPossibleMergeIdsUseCase {

    operator fun invoke(possiblyMergedIds:String): String {
        return if(possiblyMergedIds.contains("-")){
            val pubIdAndOrBookId = possiblyMergedIds.split("-").toTypedArray()
            pubIdAndOrBookId[1]
        }else{
            possiblyMergedIds
        }
    }

}