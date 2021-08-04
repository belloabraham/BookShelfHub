package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.BookActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import com.bookshelfhub.bookshelfhub.extensions.load
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class OrderedBooksAdapter (private val activity: Activity) {

    fun getOrderedBooksAdapter():ListAdapter<OrderedBooks, RecyclerViewHolder<OrderedBooks>>{

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.isbn == new.isbn  },
                areItemsTheSame = { old, new -> old.isbn == new.isbn }
            )

            register(
                layoutResource = R.layout.ordered_books_item,
                viewHolder = OrderedBooksAdapter::OrderedBookViewHolder,
                onBindViewHolder = { vh, _, model ->
                   vh.bindToView(model, activity)
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View):RecyclerViewHolder<OrderedBooks>(view){
       private val title: TextView = view.findViewById(R.id.title)
       private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
       private val imageView: ImageView = view.findViewById(R.id.itemImageView)
        fun bindToView(model:OrderedBooks, activity: Activity){
            title.text = model.title
            imageView.load(model.bookCoverUrl, R.drawable.ic_store_item_place_holder)
            itemCardView.setOnClickListener {
                val intent = Intent(activity, BookActivity::class.java)
                with(intent){
                    putExtra(Book.TITLE.KEY, model.title)
                    putExtra(Book.ISBN.KEY, model.isbn)
                }
                val options = ActivityOptions.makeSceneTransitionAnimation(activity, itemCardView, activity.getString(R.string.trans_book))
                activity.startActivity(intent, options.toBundle())
            }
        }
    }

}