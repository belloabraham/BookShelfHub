package com.bookshelfhub.bookshelfhub.domain.usecases

import com.bookshelfhub.bookshelfhub.data.models.entities.PublishedBook
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import timber.log.Timber
import javax.inject.Inject

class GetBookShareLinkUseCase @Inject constructor(private val dynamicLink: IDynamicLink) {

     operator fun invoke(book:PublishedBook) {

    }
}