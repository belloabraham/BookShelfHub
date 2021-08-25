package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class CartItemsListAdapter(private val context: Context) {

     fun getCartListAdapter(onItemLongClickListener:()->Boolean): ListAdapter<Cart, RecyclerViewHolder<Cart>> {
        return adapterOf {

          diff(
                areContentsTheSame = { old, new -> old.isbn == new.isbn  },
                areItemsTheSame = { old, new -> old.isbn == new.isbn }
            )

            register(
                layoutResource = R.layout.cart_list_item,
                viewHolder = CartItemsListAdapter::CarItemsListViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, onItemLongClickListener)
                }
            )

        }
    }

    private class CarItemsListViewHolder (view: View): RecyclerViewHolder<Cart>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val price: TextView = view.findViewById(R.id.price)
        private val author: TextView = view.findViewById(R.id.author)
        private val cover: ImageView = view.findViewById(R.id.cover)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)
        fun bindToView(model:Cart, context: Context, onLongClickListener:()->Boolean) {
            title.text =  model.title
            price.text =  String.format(context.getString(R.string.price), model.price)
            author.text = String.format(context.getString(R.string.by), model.author)
            cover.load(model.coverUrl, R.drawable.ic_store_item_place_holder)
            itemCardView.setOnClickListener {

            }
           itemCardView.setOnLongClickListener {
               onLongClickListener()
           }
        }
    }

}