package com.example.android.bookdb.Data;

import android.provider.BaseColumns;

/**
 * API for the books app
 **/
public class BookContract {

    //empty constructor because what we need from this class doesn't need to be accessed from the outside
    private BookContract() {
    }

    //nested class that holds the constants for our db
    public static final class BookEntry implements BaseColumns {

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
