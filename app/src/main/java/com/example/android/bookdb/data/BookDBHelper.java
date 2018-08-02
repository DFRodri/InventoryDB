package com.example.android.bookdb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookdb.R;
import com.example.android.bookdb.data.BookContract.BookEntry;

public class BookDBHelper extends SQLiteOpenHelper{

    //our global variables that control the db name and version
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    //Constructor of our class
    public BookDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //when the db is created for the first time, this happens
    @Override
    public void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER);";

        database.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    //when the db is updated, this happens
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String tableExists = Integer.toString(R.string.unknownSupplierPhone);
        database.execSQL(tableExists + BookEntry.TABLE_NAME);
    }
}
