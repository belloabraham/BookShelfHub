package com.bookshelfhub.feature.onboarding.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bookshelfhub.feature.onboarding.R
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapterVH(itemView: View):SliderViewAdapter.ViewHolder(itemView) {

    var imageView: ImageView = itemView.findViewById(R.id.imageView)
    var textViewDescription: TextView = itemView.findViewById(R.id.description)
    var firstTitle: TextView = itemView.findViewById(R.id.firstTitle)
    var secondTitle: TextView = itemView.findViewById(R.id.secondTitle)

}