package com.example.douban.DbUtils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.douban.DbUtils.DbSchema.*;

/**
 * Created by uwei on 2018/3/5.
 */

public class BookCollectHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bookCollect.db";
    public BookCollectHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookTable.NAME + "("
                + BookTable.Cols.ID + " integer primary key autoincrement,"
                + BookTable.Cols.BOOK_ID + ","
                + BookTable.Cols.DATE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
