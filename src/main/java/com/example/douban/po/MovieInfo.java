package com.example.douban.po;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uwei on 2018/1/31.
 */

public class MovieInfo implements Serializable{
    private String mImgUrl;
    private String mId;
    private String mName;
    private double mAverage;
    private String mGenres;    //电影的类型
    private String mYear;      //电影出品时间
    private String mAltUrl;   //点击详情打开的网址
    public MovieInfo(String imgUrl,String id,String name,double average, String altUrl,String genres,String year){
        mImgUrl = imgUrl;
        mId = id;
        mName = name;
        mAverage = average;
        mAltUrl = altUrl;
        mGenres = genres;
        mYear = year;
    }

    public MovieInfo(String imgUrl,String id,String name,String altUrl,String genres,String year){
        mImgUrl = imgUrl;
        mId = id;
        mName = name;
        mAltUrl = altUrl;
        mGenres = genres;
        mYear = year;
    }

    public String getAltUrl() {
        return mAltUrl;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public double getAverage() {
        return mAverage;
    }

    public String getGenres() {
        return mGenres;
    }

    public String getYear() {
        return mYear;
    }

    public MovieInfo(String imgUrl,String id, String altUrl,String name){
        mImgUrl = imgUrl;
        mId = id;
        mAltUrl = altUrl;
        mName = name;
    }
}
