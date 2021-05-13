package com.bookshelfhub.bookshelfhub.adapters.slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookshelfhub.bookshelfhub.R;
import com.bookshelfhub.bookshelfhub.wrapper.imageloader.ImageLoader;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapterVH>  {

    private List<SliderItem> mSliderItems = new ArrayList<>();
    private final ImageLoader imageLoader;

    public SliderAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        SliderItem sliderItem = mSliderItems.get(position);

        viewHolder.firstTitle.setText(sliderItem.getFirstTitle());
        viewHolder.secondTitle.setText(sliderItem.getSecondTitle());
        viewHolder.textViewDescription.setText(sliderItem.getDescription());
        imageLoader.loadImageIntoView(sliderItem.getImageResourceId(), viewHolder.imageView);

    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

}
