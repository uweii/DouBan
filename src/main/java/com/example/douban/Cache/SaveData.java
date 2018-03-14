package com.example.douban.Cache;

import android.content.Context;
import android.util.Log;

import com.example.douban.po.BookInfo;
import com.example.douban.po.MovieInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/3/13.
 */

public class SaveData {
    private static String TAG = "saveData";
    private static String Cache_Movie = "cache_movie_";
    private static String Cache_Book = "cache_book_";
    private static String Cache_Top_Movie_ = "cache_top_movie_";
    public static <T> void setData(Context context, List<T> list,int type,String tag)
    {
        File file = context.getCacheDir();
        File Cache = null;
        String name;
        if(type==0){
             name = Cache_Movie + tag;
            Cache = new File(file,name);
        }else if(type==1){
            name = Cache_Book + tag;
            Cache = new File(file,name);
        }else {
            name = Cache_Top_Movie_ + tag;
            Cache = new File(file,name);
        }
        if(Cache.exists()){
            Cache.delete();
        }
        try {
            ObjectOutputStream outputStream =
                    new ObjectOutputStream(new FileOutputStream(Cache));
            outputStream.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> getData(Context context,String tag, int type) throws IllegalAccessException, InstantiationException {
        File file = context.getCacheDir();
        String name;
        File cache;
        List<T> list = null;
        if(type==0){
            name = Cache_Movie + tag;
            cache = new File(file,name);
            if(!cache.exists()){
                return null;
            }
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
                list = (List<T>) inputStream.readObject();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(type==1){
            name = Cache_Book + tag;
            cache = new File(file,name);
            if(!cache.exists()){
                return null;
            }
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
                list = (List<T>) inputStream.readObject();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            name = Cache_Top_Movie_ + tag;
            cache = new File(file,name);
            if(!cache.exists()){
                return null;
            }
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
                list = (List<T>) inputStream.readObject();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
