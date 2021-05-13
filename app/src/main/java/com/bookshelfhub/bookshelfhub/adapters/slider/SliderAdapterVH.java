package com.bookshelfhub.bookshelfhub.adapters.slider;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookshelfhub.bookshelfhub.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

    View itemView;
    ImageView imageView;
    TextView textViewDescription;
    TextView firstTitle;
    TextView secondTitle;

    public SliderAdapterVH(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        firstTitle = itemView.findViewById(R.id.firstTitle);
        secondTitle = itemView.findViewById(R.id.secondTitle);
        textViewDescription = itemView.findViewById(R.id.description);
        this.itemView = itemView;
    }

}
