package com.example.douban.Cache;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;

/**
 * Created by uwei on 2018/3/14.
 */

public class DeleteData {
    private static String TAG ="DeleteData";
    public static void del(Context context)
    {
        Log.i(TAG,"del");
        File file = context.getCacheDir();
        if(file.exists()){
            del(file);
        }
    }

    public static void del(File file)
    {
        if(file.isDirectory()){
            File[] fileList = file.listFiles();
            for(File f: fileList){
                del(f);
            }
        }else {
            file.delete();
        }
    }

    public static String getDataSize(Context context)
    {
        String result = null;
        File cache = context.getCacheDir();
        if(!cache.exists()){
            return "Ok";
        }
        long cacheSize = getFolderSize(cache);
        double Kbyte = cacheSize/1024;
        double Mbyte = Kbyte/1024;
        if(Mbyte<1){
            result= "" + String.format("%.2f",Kbyte) + "KB";
        }else {
            result = "" + String.format("%.2f",Mbyte) + "MB";
        }
        return result;
    }

    private static long getFolderSize(File file)
    {
        long size = 0;
        File[] files = file.listFiles();  //获取file目录的所有file
        for(int i=0; i< files.length;i++)
        {
            if(files[i].isDirectory()){
                size = size + getFolderSize(files[i]);
            }else {
                size = size + files[i].length();
            }
        }
        return size;
    }
}
