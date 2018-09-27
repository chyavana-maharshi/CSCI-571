package com.example.sunqingyu.hw9;


import android.graphics.Bitmap;



public class FeedItem {
    private Bitmap thumbnail;

    public FeedItem(){}

    public FeedItem(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}


