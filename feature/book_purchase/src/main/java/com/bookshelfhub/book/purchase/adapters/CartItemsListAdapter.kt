package com.bookshelfhub.book.purchase.adapters

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.book.purchase.R
import com.bookshelfhub.core.common.helpers.utils.IconUtil
import com.bookshelfhub.core.model.entities.CartItem
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


class CartItemsListAdapter(private val context: Context) {

     fun getCartListAdapter(onDeleteClickListener:(CartItem, Int)->Unit): ListAdapter<CartItem, RecyclerViewHolder<CartItem>> {
        return adapterOf {

          diff(
                areContentsTheSame = { old, new -> old.bookId == new.bookId  },
                areItemsTheSame = { old, new -> old.bookId == new.bookId }
            )

            register(
                layoutResource = R.layout.cart_list_item,
                viewHolder = CartItemsListAdapter::CarItemsListViewHolder,
                onBindViewHolder = { vh, position, model ->

                    vh.bindToView(model, context, position, onDeleteClickListener)
                }
            )

        }
    }

    private class CarItemsListViewHolder (view: View): RecyclerViewHolder<CartItem>(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val price: TextView = view.findViewById(R.id.price)
        private val author: TextView = view.findViewById(R.id.author)
        private val cover: ImageView = view.findViewById(R.id.cover)
        private val deleteBtn: ImageButton = view.findViewById(R.id.deleteCartItem)
        fun bindToView(model: CartItem, context: Context, position:Int, onDeleteClickListener:(CartItem, Int)->Unit) {
            title.text =  model.name

            deleteBtn.setOnClickListener {
                onDeleteClickListener(model, position)
            }

            price.text = String.format(context.getString(R.string.local_price), model.sellerCurrency, model.price)
            author.text = String.format(context.getString(R.string.by), model.author)
            cover.setImageBitmap(IconUtil.getBitmap(model.coverDataUrl))

        }
    }

}