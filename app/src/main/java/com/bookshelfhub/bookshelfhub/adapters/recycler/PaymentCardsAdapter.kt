package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.IconUtil
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.services.payment.CardUtil
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class PaymentCardsAdapter(private val context: Context) {

     fun getPaymentListAdapter(onItemClickListener:(PaymentCard)->Unit): ListAdapter<PaymentCard, RecyclerViewHolder<PaymentCard>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.cardNo == new.cardNo  },
                areItemsTheSame = { old, new -> old.cardNo == new.cardNo }
            )

            register(
                layoutResource = R.layout.payment_card_item,
                viewHolder = PaymentCardsAdapter::PaymentCardsViewHolder,
                onBindViewHolder = { vh, _, model ->
                    vh.bindToView(model, context, onItemClickListener)
                }
            )

        }
    }


    private class PaymentCardsViewHolder (view: View): RecyclerViewHolder<PaymentCard>(view) {
        private val cardTemplateTx: TextView = view.findViewById(R.id.cardTemplateTxt)
        private val imageView: ImageView = view.findViewById(R.id.cardLogo)
        private val itemCardView: CardView = view.findViewById(R.id.itemCardView)

        fun bindToView(card:PaymentCard, context: Context, onItemClickListener:(PaymentCard)->Unit) {

            cardTemplateTx.text =  String.format(context.getString(R.string.template_card_no), card.cardType, card.lastFourDigit)
            val cardDraw = IconUtil.getDrawable(context,CardUtil.getDrawableRes(card.cardType))
            imageView.setImageDrawable(cardDraw)

            itemCardView.setOnClickListener {
                onItemClickListener(card)
            }
        }
    }

}