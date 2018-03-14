package com.example.douban.NetUtils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by uwei on 2018/2/1.
 */

public class CheckNet {
    public static boolean hasNetAccess(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager.getActiveNetworkInfo()!=null&&manager.getActiveNetworkInfo().isConnected()){
            return true;
        }
        return false;
    }
}
