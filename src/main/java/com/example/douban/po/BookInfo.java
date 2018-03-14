package com.example.douban.po;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by uwei on 2018/2/24.
 */

public class BookInfo implements Serializable{
    private String mImgUrl;
    private String mId;
    private String mAuthor;
    private String mPages;
    private String mAltUrl;
    private String mPublisher;
    private String mName;
    private String mApiUrl; //book的api网址
    private double mAverage;
    private String mAuthor_intro; //作者简介
    private String mBookSummary;   //书的简介
    public BookInfo(String imgUrl,double average,String id,String author,String pages,String altUrl,String publisher,String name,String apiUrl, String author_intro, String bookSummary){
        mImgUrl = imgUrl;
        mAverage = average;
        mId = id;
        mAuthor = author;
        mPages = pages;
        mAltUrl = altUrl;
        mPublisher = publisher;
        mName = name;
        mApiUrl = apiUrl;
        mAuthor_intro = author_intro;
        mBookSummary = bookSummary;
    }

    public BookInfo(String id, String imgUrl,String name){
        mId = id;
        mImgUrl = imgUrl;
        mName = name;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public String getAuthor_intro() {
        return mAuthor_intro;
    }

    public String getBookSummary() {
        return mBookSummary;
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPages() {
        return mPages;
    }

    public double getAverage() {
        return mAverage;
    }

    public String getAltUrl() {
        return mAltUrl;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getName() {
        return mName;
    }

    public String getApiUrl() {
        return mApiUrl;
    }
}
