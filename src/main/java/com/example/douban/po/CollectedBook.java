package com.example.douban.po;

/**
 * Created by uwei on 2018/3/5.
 */

public class CollectedBook {
    private String mBookId;
    private String mDate;

    public CollectedBook(String bookId, String date)
    {
        mBookId = bookId;
        mDate = date;
    }

    public String getBookId() {
        return mBookId;
    }

    public String getDate() {
        return mDate;
    }
}
