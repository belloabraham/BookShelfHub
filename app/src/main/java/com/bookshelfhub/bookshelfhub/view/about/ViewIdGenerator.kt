package com.bookshelfhub.bookshelfhub.view.about

import android.view.View
import java.util.concurrent.atomic.AtomicInteger


class ViewIdGenerator {

    private val sNextGeneratedId: AtomicInteger = AtomicInteger(1)

    companion object {
        fun generateViewId(): Int {
            return View.generateViewId()
        }
    }
}