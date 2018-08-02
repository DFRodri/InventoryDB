package com.example.android.bookdb.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bookdb.R;
import com.example.android.bookdb.custom_class.Book;
import com.example.android.bookdb.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {
    //global variable for our logs to check where the error comes from
    public static final String TAG = BookProvider.class.getSimpleName();

    //setup of variables to use the URI Matcher
    private static final int BOOKS = 100;//URI Matcher code for the inventory table
    private static final int BOOKS_ID = 101;//URI Matcher code for a single item from the inventory table
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    //initialize the dbHelper
    private BookDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new BookDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //creation of a cursor to hold the query result
        Cursor cursor;
        //check if URI matcher matches the code and if positive does the query for the info that we want/need
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS://query on the entire table
                cursor = database.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BOOKS_ID://query on a specific row
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI, can't query" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                String uriUnknown = Integer.toString(R.string.unknownURI);
                String matchCodeUnknown = Integer.toString(R.string.unknownMatchCode);
                throw new IllegalStateException(uriUnknown + uri + matchCodeUnknown + match);
        }
    }

    //insert new data
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException(R.string.insertionError + uri.toString());
        }
    }

    public Uri insertBook(Uri uri, ContentValues values) {
        //Sanity check for null book names to prevent errors
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String title = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (title == null) {
                String nameError = Integer.toString(R.string.unknownName);
                throw new IllegalArgumentException(nameError);
            }
        }
        //Sanity check for null quantity values to prevent undesirable results
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                String quantityError = Integer.toString(R.string.unknownQuantity);
                throw new IllegalArgumentException(quantityError);
            }
        }
        //Sanity check for null and negatives prices
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                String priceError = Integer.toString(R.string.unknownPrice);
                throw new IllegalArgumentException(priceError);
            }
        }
        //Sanity check for the supplier name of the book being different from a null
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                String supplierError = Integer.toString(R.string.unknownSupplier);
                throw new IllegalArgumentException(supplierError);
            }
        }
        //Sanity check for the supplier contact (phone number) of the book being different from a null
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String phone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (phone == null) {
                String supplierPhoneError = Integer.toString(R.string.unknownSupplierPhone);
                throw new IllegalArgumentException(supplierPhoneError);
            }
        }

        //calls the database in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //insert the new product when valid
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            String insertError = Integer.toString(R.string.insertDataFailed);
            Log.e(TAG, insertError + uri);
            return null;
        }
        //notify the app that data changed in the URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //calls the database in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                String errorDelete = Integer.toString(R.string.deleteDataFailed);
                throw new IllegalArgumentException(errorDelete);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                String updateFailed = Integer.toString(R.string.updateFailed);
                throw new IllegalArgumentException(updateFailed + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Sanity check for null book names to prevent errors
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String title = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (title == null) {
                String nameError = Integer.toString(R.string.unknownName);
                throw new IllegalArgumentException(nameError);
            }
        }
        //Sanity check for null quantity values to prevent undesirable results
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                String quantityError = Integer.toString(R.string.unknownQuantity);
                throw new IllegalArgumentException(quantityError);
            }
        }
        //Sanity check for null and negatives prices
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                String priceError = Integer.toString(R.string.unknownPrice);
                throw new IllegalArgumentException(priceError);
            }
        }
        //Sanity check for the supplier name of the book being different from a null
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                String supplierError = Integer.toString(R.string.unknownSupplier);
                throw new IllegalArgumentException(supplierError);
            }
        }
        //Sanity check for the supplier contact (phone number) of the book being different from a null
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String phone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (phone == null) {
                String supplierPhoneError = Integer.toString(R.string.unknownSupplierPhone);
                throw new IllegalArgumentException(supplierPhoneError);
            }
        }
        //insert the new product only when valid
        if (values.size() == 0) {
            return 0;
        }
        //calls the database in writable mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        //notify the app that data changed in the URI
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
