package com.bookshelfhub.feature.home.adapters.recycler

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.book.page.BookActivity
import com.bookshelfhub.core.data.Book
import com.bookshelfhub.core.domain.usecases.LocalFile
import com.bookshelfhub.core.model.ISearchResult
import com.bookshelfhub.core.model.uistate.OrderedBookUiState
import com.bookshelfhub.feature.home.R
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


class ShelfSearchResultAdapter(private val activity: Activity) {

     fun getSearchResultAdapter(): ListAdapter<ISearchResult, RecyclerViewHolder<ISearchResult>> {
        return adapterOf {

            register(
                layoutResource = R.layout.shelf_result_search_item,
                viewHolder = ::SearchResultViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model.bookId, model.pubId, model.name, activity)
                }
            )

        }
    }

    companion object{
        fun startBookActivity(bookId:String, title:String, activity: Activity){
                val intent = Intent(activity, BookActivity::class.java)
                with(intent){
                    putExtra(Book.NAME, title)
                    putExtra(Book.ID, bookId)
                }
                activity.startActivity(intent)
        }
    }

    private class SearchResultViewHolder(view: View) : RecyclerViewHolder<OrderedBookUiState>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(bookId:String, pubId:String, name:String, activity: Activity){
            title.text = name
            itemCardView.setOnClickListener {
                val bookFileExist = LocalFile.getBookFile(bookId, pubId, activity).exists()
                if (bookFileExist) {
                    startBookActivity(bookId, name, activity)
                }
            }
        }
    }
}