package com.bookshelfhub.bookshelfhub.adapters.viewpager.onboarding

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.extensions.loadUnCompressed
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.*


class SliderAdapter: SliderViewAdapter<SliderAdapterVH>(){

    private val mSliderItems: MutableList<SliderItem> = ArrayList()


    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        if (viewHolder != null) {
            val sliderItem = mSliderItems[position]
            viewHolder.firstTitle.text = sliderItem.firstTitle
            viewHolder.secondTitle.text = sliderItem.secondTitle
            viewHolder.textViewDescription.text = sliderItem.description
            viewHolder.imageView.loadUnCompressed(sliderItem.resource)
        }
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

}