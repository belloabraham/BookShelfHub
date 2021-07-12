package com.bookshelfhub.bookshelfhub.adapters

import android.app.Activity
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
import com.bookshelfhub.bookshelfhub.wrapper.image.load
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
                    vh.title.text = model.title
                    vh.imageView.load(model.bookCoverUrl, R.drawable.ic_book_sample, true)

                    vh.itemCardView.setOnClickListener {
                        val intent = Intent(activity, BookActivity::class.java)
                        with(intent){
                            putExtra(Book.TITLE.KEY, model.title)
                            putExtra(Book.ISBN.KEY, model.isbn)
                        }
                        activity.startActivity(intent)
                    }
                }
            )
        }
    }

    private class OrderedBookViewHolder(view: View):RecyclerViewHolder<OrderedBooks>(view){
        val title: TextView = view.findViewById(R.id.title)
        val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        val imageView: ImageView = view.findViewById(R.id.itemImageView)
    }

}