package com.bookshelfhub.bookshelfhub.adapters

import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmarks
import com.ernestoyaquello.dragdropswiperecyclerview.util.DragDropSwipeDiffCallback

class BookmarkDiffCallBack(oldList: List<Bookmarks>, newList:List<Bookmarks>): DragDropSwipeDiffCallback<Bookmarks>(oldList, newList) {

    override fun isSameContent(oldItem: Bookmarks, newItem: Bookmarks): Boolean {
      return oldItem.isbn==newItem.isbn && oldItem.pageNumb == newItem.pageNumb
    }

    override fun isSameItem(oldItem: Bookmarks, newItem: Bookmarks): Boolean {
        return oldItem.isbn==newItem.isbn && oldItem.pageNumb == newItem.pageNumb
    }

}