package com.bookshelfhub.bookshelfhub.adapters

import android.view.View
import android.widget.TextView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Bookmarks
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.ernestoyaquello.dragdropswiperecyclerview.util.DragDropSwipeDiffCallback

class BookmarkListAdapter(data: List<Bookmarks> = emptyList()) : DragDropSwipeAdapter<Bookmarks, BookmarkListAdapter.ViewHolder>(data) {

    override fun getViewHolder(itemView: View) = ViewHolder(itemView)

    override fun onBindViewHolder(item: Bookmarks, viewHolder: ViewHolder, position: Int) {
        // Here we update the contents of the view holder's views to reflect the item's data
        viewHolder.bindToView(item)
    }

    override fun createDiffUtil(
        oldList: List<Bookmarks>,
        newList: List<Bookmarks>
    ): DragDropSwipeDiffCallback<Bookmarks> {
        return BookmarkDiffCallBack(oldList, newList)
    }

    override fun getViewToTouchToStartDraggingItem(
        item: Bookmarks,
        viewHolder: ViewHolder,
        position: Int
    ): View? {
        return null
    }

  class ViewHolder(itemView: View):DragDropSwipeAdapter.ViewHolder(itemView) {
     private val title: TextView = itemView.findViewById(R.id.title)
     private val pageNumb: TextView = itemView.findViewById(R.id.pageNumb)

      fun bindToView(item: Bookmarks){
          title.text = item.title
          pageNumb.text = "${item.pageNumb}"
      }
  }
}