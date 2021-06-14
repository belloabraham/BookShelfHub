package com.bookshelfhub.bookshelfhub.models

import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.IPaymentInfo

class PaymentInfo:IPaymentInfo {
    override val nameOnCard: String = ""
        get() = field
    override val cardNumber: String = ""
        get() = field
    override val ccv: String = ""
        get() = field
    override val address: String = ""
        get() = field
    override val expDate: String = ""
        get() = field
}