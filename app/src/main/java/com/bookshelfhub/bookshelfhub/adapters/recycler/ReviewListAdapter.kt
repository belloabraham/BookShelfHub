package com.bookshelfhub.bookshelfhub.adapters.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateUtil
import com.bookshelfhub.bookshelfhub.Utils.datetime.DateFormat
import com.bookshelfhub.bookshelfhub.extensions.load
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.UserReview
import com.bookshelfhub.bookshelfhub.views.LetterIcon
import com.github.ivbaranov.mli.MaterialLetterIcon
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import me.zhanghai.android.materialratingbar.MaterialRatingBar

/**
 * Custom Recycler View Adapter using Kiel Library @https://github.com/ibrahimyilmaz/kiel
 */

class ReviewListAdapter () {

    fun getAdapter(): ListAdapter<UserReview, RecyclerViewHolder<UserReview>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.isbn == new.isbn  },
                areItemsTheSame = { old, new -> old.isbn == new.isbn }
            )

            register(
                layoutResource = R.layout.user_review_item,
                viewHolder = ::ReviewListViewHolder,
                onBindViewHolder = { vh, pos, model ->
                    vh.bindToView(model, pos)
                }
            )

        }
    }

    private class ReviewListViewHolder (view: View): RecyclerViewHolder<UserReview>(view) {
        private val letterIcon: MaterialLetterIcon = itemView.findViewById(R.id.letterIcon)
        private val userImage: ImageView = itemView.findViewById(R.id.userImage)
        private val userNameText: TextView = itemView.findViewById(R.id.userNameText)
        private val dateTxt: TextView = itemView.findViewById(R.id.date)
        private val userRatingBar: MaterialRatingBar = itemView.findViewById(R.id.userRatingBar)
        private val userReviewTxt: TextView = itemView.findViewById(R.id.userReviewTxt)

        fun bindToView(model:UserReview, pos:Int){
           userNameText.text = model.userName
            userRatingBar.rating = model.userRating.toFloat()
            userReviewTxt.text = model.review
            model.dateTime?.let {
                val  date = DateUtil.dateToString(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
                dateTxt.text = date
            }
            if (model.userPhoto!=null){
                userImage.visibility = View.VISIBLE
                userImage.load(model.userPhoto!!){
                    showLetterIcon(model.userName, pos)
                }
            }else{
                showLetterIcon(model.userName, pos)
            }
        }

        private fun showLetterIcon(value:String, pos:Int){
            userImage.visibility = View.GONE
            letterIcon.visibility = View.VISIBLE
            letterIcon.letter = value
            letterIcon.letterColor = LetterIcon.getColor(pos)
        }
    }


}