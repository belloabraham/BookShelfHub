package com.bookshelfhub.core.domain.usecases

import android.content.Context
import com.bookshelfhub.core.common.helpers.storage.AppExternalStorage
import com.bookshelfhub.core.common.helpers.storage.FileExtension
import java.io.File

object LocalFile {

    fun getBookFile(bookId:String, pubId:String, context: Context, fileExt:String = FileExtension.DOT_PDF): File {
        val getBookIdFromCompoundId = GetBookIdFromCompoundId()
        val unMergedBookId =  getBookIdFromCompoundId(bookId)
        val localDir = AppExternalStorage.getDocumentDir(pubId, context)
        return AppExternalStorage.getDocumentFile(localDir, "$unMergedBookId$fileExt")
    }
}