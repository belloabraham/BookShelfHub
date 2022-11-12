package com.bookshelfhub.feature.book.item.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.feature.book.item.BookItemActivity
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.model.entities.StoreSearchHistory
import com.bookshelfhub.core.model.uistate.PublishedBookUiState
import com.bookshelfhub.core.resources.R
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class SimilarBooksSearchResultAdapter (private val context:Context) {

    fun startBookItemActivity(name:String, isbn:String, author:String, context: Context){
        val intent = Intent(context, BookItemActivity::class.java)
        with(intent){
            putExtra(Book.NAME, name)
            putExtra(Book.ID, isbn)
            putExtra(Book.AUTHOR, author)
            putExtra(Book.IS_SEARCH_ITEM, true)
        }
        context.startActivity(intent)
    }

    fun getSearchResultAdapter(): ListAdapter<Any, RecyclerViewHolder<Any>> {
        return adapterOf {

            register(
                layoutResource = R.layout.category_search_item,
                viewHolder = SimilarBooksSearchResultAdapter::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, this@SimilarBooksSearchResultAdapter)
                }
            )

        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<PublishedBookUiState>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model: PublishedBookUiState, context: Context, storeSearchResultAdapter: SimilarBooksSearchResultAdapter){
            title.text = model.name
            author.text = model.author
            itemCardView.setOnClickListener {
                storeSearchResultAdapter.startBookItemActivity(model.name, model.bookId, model.author, context)
            }
        }
    }

}