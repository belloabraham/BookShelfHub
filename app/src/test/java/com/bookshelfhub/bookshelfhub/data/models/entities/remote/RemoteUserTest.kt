package com.bookshelfhub.bookshelfhub.data.models.entities.remote

import com.bookshelfhub.bookshelfhub.data.models.entities.BookInterest
import com.bookshelfhub.bookshelfhub.data.models.entities.User
import com.bookshelfhub.bookshelfhub.data.sources.remote.RemoteDataFields
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class RemoteUserTest{
     private lateinit var remoteUser: RemoteUser


    @Before
    fun setUp() {
        val user = User("", "")
        val bookInterest = BookInterest("")
        remoteUser = RemoteUser(user, bookInterest, "utrytdfuigo")
    }

    @Test
    fun isRemoteUserBookInterestFieldNameSameAsRemoteDatabaseBookInterestFieldName(){
        val bookInterest =  remoteUser::class.members.find {
            it.name == "bookInterest"
        }
        assertThat(bookInterest!!.name == RemoteDataFields.BOOK_INTEREST).isTrue()
    }

    @Test
    fun isRemoteUserNotificationTokenFieldNameSameAsRemoteDatabaseNotificationFieldName(){
        val notificationToken =  remoteUser::class.members.find {
            it.name == "notificationToken"
        }
        assertThat(notificationToken!!.name == RemoteDataFields.NOTIFICATION_TOKEN).isTrue()
    }

    @Test
    fun isRemoteUser_user_FieldNameSameAsRemoteDatabaseUserFieldName(){
        val user =  remoteUser::class.members.find {
            it.name == "user"
        }
        assertThat(user!!.name == RemoteDataFields.USER).isTrue()
    }

}