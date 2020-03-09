package com.example.seriesapp;

public class Movie {
    private String mTitle;
    private String mImgUrl;

    public Movie(String title, String imgUrl){
        mTitle = title;
        mImgUrl = imgUrl;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getImgUrl(){
        return mImgUrl;
    }
}
