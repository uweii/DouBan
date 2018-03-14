package com.example.douban.SharePre;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by uwei on 2018/3/9.
 */;import java.util.List;

public class SharePreUtil {
    private static String TAG = "SharePreUtil";
    private static String TAGNAME = "tag";  //sharepreference的名字tag.xml
    private static String THEMETAG = "theme";  //用来记录主题是 白天 还是 夜间
    private static String TAGMOVIEKEY = "tag_key_movie";  //保存电影的tag
    private static String TAGBOOKKEY = "tag_key_book";  //保存book标签的tag
    private static String CHANGEWALLPAPER = "changepaper";    //是否更换主题
    static String defaultMovieTag = "[爱情,喜剧,动画,科幻,动作,经典]";
    static String defaultBookTag = "[小说,名著,科幻,历史,爱情,编程]";
    public static String getSelectedMovieTag(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        String tag = sp.getString(TAGMOVIEKEY,null);
        if(tag == null){
            sp.edit().putString(TAGMOVIEKEY, defaultMovieTag).commit();
        }
        return sp.getString(TAGMOVIEKEY,null);
    }
    public static String getSelectedBookTag(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        String tag = sp.getString(TAGBOOKKEY,null);
        if(tag == null){
            sp.edit().putString(TAGBOOKKEY, defaultBookTag).commit();
        }
        return sp.getString(TAGBOOKKEY,null);
    }
    static String getStringFromArray(String[] arr)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<arr.length;i++)
        {
            sb.append(arr[i]);
            if(i!=arr.length-1)
            {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    /*type
    * 0.  代表movie
    * 1.  代表book
    * */
    public static void saveTags(int type, List<String> list, Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        if (type==0){
            //Log.i(TAG,"saveTags movieList ==> " + listToString(list));
            sp.edit().putString(TAGMOVIEKEY,listToString(list)).commit();
        }else {
            sp.edit().putString(TAGBOOKKEY,listToString(list)).commit();
        }
    }

    private static String listToString(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0;i<list.size();i++)
        {
            sb.append(list.get(i));
            if(i!=list.size()-1){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String getThemeTag(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        String tag = sp.getString(THEMETAG,null);
        if(tag==null){
            sp.edit().putString(THEMETAG,"0").commit();
            return "0";
        }
        return tag;
    }
    public static void setTheme(Context context,String tag)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        sp.edit().putString(THEMETAG,tag).commit();
    }

    /*-----------changeWallPaper--------------*/
    public static void setWhetherChangeWallPaper(Context context,int tag)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        sp.edit().putInt(CHANGEWALLPAPER,tag).commit();
    }

    public static int getWallPaperTag(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TAGNAME,Context.MODE_PRIVATE);
        int tag = sp.getInt(CHANGEWALLPAPER,0);
        if(tag==0){
            sp.edit().putInt(CHANGEWALLPAPER,1).commit();  //代表开启自动切换壁纸
            return 1;
        }
        return tag;
    }
    /*--------------------------------------------*/
}
