package com.bookshelfhub.feature.home.adapters.recycler

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.core.model.ISearchResult
import com.bookshelfhub.core.model.entities.ShelfSearchHistory
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.bookshelfhub.feature.home.R
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


class ShelfSearchResultAdapter(private val activity: Activity) {

     fun getSearchResultAdapter(): ListAdapter<ISearchResult, RecyclerViewHolder<ISearchResult>> {
        return adapterOf {

            register(
                layoutResource = R.layout.shelf_history_search_item,
                viewHolder = ::SearchHistoryViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model.bookId, model.name, activity)
                }
            )

            register(
                layoutResource = R.layout.shelf_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model.bookId, model.name, activity)
                }
            )

        }
    }


    companion object{
        fun startBookActivity(bookId:String, title:String, activity: Activity){
            val intent = Intent(activity, BookActivity::class.java)
            with(intent){
                putExtra(com.bookshelfhub.core.data.Book.NAME, title)
                putExtra(com.bookshelfhub.core.data.Book.ID, bookId)
                putExtra(com.bookshelfhub.core.data.Book.IS_SEARCH_ITEM, true)
            }
            activity.startActivity(intent)
        }
    }


    private class SearchHistoryViewHolder (view: View): RecyclerViewHolder<ShelfSearchHistory>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(bookId:String, name:String, activity: Activity){
            title.text = name
            itemCardView.setOnClickListener {
                startBookActivity(bookId, name, activity)
            }
        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<OrderedBookUiState>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(bookId:String, name:String, activity: Activity){
            title.text = name
            itemCardView.setOnClickListener {
                startBookActivity(bookId, name, activity)
            }
        }
    }
}