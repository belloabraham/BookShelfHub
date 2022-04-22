package com.bookshelfhub.bookshelfhub.domain.usecases

import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import javax.inject.Inject

class GetBookShareLinkUseCase @Inject constructor(private val dynamicLink: IDynamicLink) {

    operator fun invoke() {

    }
}