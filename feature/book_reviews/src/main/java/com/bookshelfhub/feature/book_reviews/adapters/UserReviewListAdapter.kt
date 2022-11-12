package com.bookshelfhub.feature.book_reviews.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.core.common.extensions.load
import com.bookshelfhub.core.common.helpers.utils.LetterIcon
import com.bookshelfhub.core.common.helpers.utils.datetime.DateFormat
import com.bookshelfhub.core.common.helpers.utils.datetime.DateUtil
import com.bookshelfhub.core.model.entities.UserReview
import com.bookshelfhub.feature.book_reviews.R
import com.github.ivbaranov.mli.MaterialLetterIcon
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class UserReviewListAdapter {

    fun getAdapter(): ListAdapter<UserReview, RecyclerViewHolder<UserReview>> {

        return adapterOf {

            diff(
                areContentsTheSame = { old, new -> old.bookId == new.bookId  },
                areItemsTheSame = { old, new -> old.bookId == new.bookId }
            )

            register(
                layoutResource = R.layout.user_review_item,
                viewHolder = UserReviewListAdapter::ReviewListViewHolder,
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

        fun bindToView(model: UserReview, pos:Int){
           userNameText.text = model.userName
            userRatingBar.rating = model.userRating.toFloat()
            userReviewTxt.text = model.review
            model.dateTime?.let {
                val  date = DateUtil.getHumanReadable(it.toDate(), DateFormat.DD_MM_YYYY.completeFormatValue)
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