package com.example.douban.po;

import java.io.Serializable;

/**
 * Created by uwei on 2018/2/8.
 */

public class ActorInfo implements Serializable{
    private String mActorImg;  //演员头像url
    private String mActorName; //演员名字
    private String mActorId;   //演员id
    private String mAltUrl;    //演员对应的网页
    public ActorInfo(String actorImg, String actorName, String actorId,String altUrl) {
        mActorImg = actorImg;
        mActorName = actorName;
        mActorId = actorId;
        mAltUrl = altUrl;
    }

    public String getActorImg() {
        return mActorImg;
    }

    public String getActorName() {
        return mActorName;
    }

    public String getActorId() {
        return mActorId;
    }

    public String getAltUrl() {
        return mAltUrl;
    }
}
