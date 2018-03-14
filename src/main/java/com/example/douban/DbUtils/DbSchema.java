package com.example.douban.DbUtils;

/**
 * Created by uwei on 2018/3/5.
 */

public class DbSchema {
    public static final class BookTable{
        public static final String NAME = "books";

        public static final class Cols{
            public static final String ID = "_id";
            public static final String BOOK_ID = "book_id";
            public static final String DATE = "date";
        }
    }
}
