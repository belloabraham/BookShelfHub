package com.bookshelfhub.bookshelfhub.adapters.search

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bookshelfhub.bookshelfhub.ContentActivity
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.enums.Book
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.OrderedBooks
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class OrderedBooksAdapter (private val context: Context) {

    fun getOrderedBooksAdapter():ListAdapter<Any, RecyclerViewHolder<Any>>{
        return adapterOf {
            register(
                layoutResource = R.layout.ordered_books_item,
                viewHolder = ::OrderedBookViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.title.text = model.bookName

                    vh.imageView.load(model.bookCoverUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_book_sample)
                        transformations(RoundedCornersTransformation())
                    }

                    vh.itemCardView.setOnClickListener {
                        val intent = Intent(context, ContentActivity::class.java)
                        with(intent){
                            putExtra(Book.TITLE.KEY, model.bookName)
                            putExtra(Book.ISBN.KEY, model.isbn)
                        }
                        context.startActivity(intent)
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