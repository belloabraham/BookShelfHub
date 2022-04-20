package com.bookshelfhub.bookshelfhub.workers

object Tag {
    const val postPendingUserReview ="postPendingUserReview"
    const val deleteBookmarks ="deleteBookmarks"
    const val removeUnPublishedBooks ="removeUnPublishedBooks"
    const val updatePublishedBooks ="updatePublishedBooks"
    const val recommendedBooksWorker ="recommendedBooksWorker"

    const val CLEAR_CART="clearCart"
    const val oneTimeBookInterestUpload =  "oneTimeBookInterestUpload"
    const val periodicUserDataUpload = "periodicUserDataUpload"
    const val addUserUniqueWorkDatUpload = "addUserUniqueWorkDatUpload"

    const val oneTimeBookmarksDataDownload = "oneTimeBookmarksDataDownload"
    const val oneTimeRemotePrivateKeyWorker ="oneTimeRemotePrivateKeyWorker"
}