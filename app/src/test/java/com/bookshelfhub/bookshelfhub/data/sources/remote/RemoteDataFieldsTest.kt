package com.bookshelfhub.bookshelfhub.data.sources.remote

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RemoteDataFieldsTest{

    @Test
    fun isUsersCollectionFieldTheSame(){
        assertThat(RemoteDataFields.USERS_COLL == "users").isTrue()
    }

    @Test
    fun is_published_books_CollectionFieldTheSame(){
        assertThat(RemoteDataFields.PUBLISHED_BOOKS_COLL == "published_books").isTrue()
    }

    @Test
    fun is_ordered_books_CollectionFieldTheSame(){
        assertThat(RemoteDataFields.ORDERED_BOOKS_COLL == "ordered_books").isTrue()
    }

    @Test
    fun isTransactionsCollectionFieldTheSame(){
        assertThat(RemoteDataFields.TRANSACTIONS_COLL == "transactions").isTrue()
    }

    @Test
    fun is_reviews_CollectionFieldTheSame(){
        assertThat(RemoteDataFields.REVIEWS_COLL == "reviews").isTrue()
    }

    @Test
    fun is_bookmarks_CollectionFieldTheSame(){
        assertThat(RemoteDataFields.BOOKMARKS_COLL == "bookmarks").isTrue()
    }

}