package com.bookshelfhub.core.model.entities

/**
 * Used for records that use their local id in the database for document id in the cloud e.g Bookmarks
 */
interface IEntityId {
    val id: String
}