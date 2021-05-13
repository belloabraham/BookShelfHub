package com.bookshelfhub.bookshelfhub.adapters.slider;

public class SliderItem {

    private final String title;
    private final String description;
    private final int resourceId;
    private final String secondTitle;


    public SliderItem(String firstTitle, String secondTitle, String description, int resource){
        this.title=firstTitle;
        this.secondTitle=secondTitle;
        this.description=description;
        this.resourceId=resource;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public String getFirstTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return resourceId;
    }
}
