package com.example.douban.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.douban.DbUtils.BookCollectHelper;
import com.example.douban.DbUtils.DbSchema;
import com.example.douban.DbUtils.DbSchema.BookTable;
import com.example.douban.po.CollectedBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uwei on 2018/3/5.
 * 对收藏的book进行  增--删--查 的工具类
 */

public class BookCollect {
    private static BookCollect mBookCollect;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public static BookCollect getBookCollect(Context context){
        if(mBookCollect == null)
        {
            mBookCollect = new BookCollect(context);
        }
        return mBookCollect;
    }

    private BookCollect(Context context){
        mContext = context;
        mDatabase = new BookCollectHelper(context).getWritableDatabase();
    }

    public void addBook(String book_id, String date)
    {
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.BOOK_ID,book_id);
        values.put(BookTable.Cols.DATE, date);
        mDatabase.insert(BookTable.NAME, null, values);
    }

    public void delBook(String book_id)
    {
        mDatabase.delete(BookTable.NAME,BookTable.Cols.BOOK_ID + "=?", new String[]{book_id});
    }

    public List<CollectedBook> getCollectedBooks()
    {
        List<CollectedBook> list = new ArrayList<>();
        Cursor cursor = mDatabase.query(BookTable.NAME,new String[]{BookTable.Cols.BOOK_ID,BookTable.Cols.DATE},
                null,null,null,null,null);
        while (cursor.moveToNext()){
            String book_id = cursor.getString(0);
            String date = cursor.getString(1);
            //Log.e("cursor", book_id + "===>"  + date);
            list.add(new CollectedBook(book_id,date));
        }
        cursor.close();
        return list;
    }

    public boolean hasCollected(String book_id)
    {
        Cursor cursor = mDatabase.query(BookTable.NAME,null,
                BookTable.Cols.BOOK_ID + "=?",new String[]{book_id},null,null,null);
        if (cursor.moveToFirst()){
            Log.i("ture?", "true");
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}
