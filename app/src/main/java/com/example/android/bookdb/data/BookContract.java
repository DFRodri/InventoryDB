package com.example.android.bookdb.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API for the books app
 **/
public class BookContract {

    //variables needed to create our CONTENT PROVIDER and URI later on
    public static final String CONTENT_AUTHORITY = "com.example.android.bookdb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    //empty constructor because what we need from this class doesn't need to be accessed from the outside
    private BookContract() {
    }

    //nested class that holds the constants for our db
    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        //MIME type of #ONTENT_URI for a list of books
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //MIME type of CONTENT_URI for a single book
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;

        /**
         * Type: TEXT
         **/
        public static final String COLUMN_PRODUCT_NAME = "product";
        /**
         * Type: TEXT
         **/
        public static final String COLUMN_PRICE = "price";
        /**
         * Type: INTEGER >= 0
         **/
        public static final String COLUMN_QUANTITY = "quantity";
        /**
         * Type: TEXT
         **/
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        /**
         * Type: TEXT
         **/
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "phone_number";

    }

}
