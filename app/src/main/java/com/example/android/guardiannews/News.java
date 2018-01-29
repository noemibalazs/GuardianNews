package com.example.android.guardiannews;

import android.graphics.Bitmap;

/**
 * Created by Noemi on 1/26/2018.
 */

public class News {

    private String mTitle;

    private String mText;

    private String mDate;

    private Bitmap mThumbnail;

    private String mAuthor;

    private String mUrl;


    public News(String title, String text, String date, Bitmap thumbnail, String author, String url){

        mTitle = title;
        mText = text;
        mDate = date;
        mThumbnail = thumbnail;
        mAuthor= author;
        mUrl = url;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getText(){
        return mText;
    }

    public String getDate(){
        return mDate;
    }

    public String getUrl(){
        return mUrl;
    }

    public String getAuthor(){
        return mAuthor;
    }
    public Bitmap getThumbnail(){
        return mThumbnail;
    }

}
