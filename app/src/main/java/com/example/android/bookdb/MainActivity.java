package com.example.android.bookdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bookdb.Data.BookContract.BookEntry;
import com.example.android.bookdb.Data.BookDBHelper;

public class MainActivity extends AppCompatActivity {
    //call to our db helper
    private BookDBHelper dbHelper = new BookDBHelper(this);

    //global variables
    private String book;
    private String price;
    private String quantity;
    private String supplierName;
    private String supplierPhone;

    private String[] dummyData = {"Book One", "12.99", "2", "Dude With Books", "123123123"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    //method to add a book to our db
    private void insertBook() {
        //call the repository in write mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //create a map of values
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, book);
        contentValues.put(BookEntry.COLUMN_PRICE, price);
        contentValues.put(BookEntry.COLUMN_QUANTITY, quantity);
        contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        //add the data inserted
        database.insert(BookEntry.TABLE_NAME, null, contentValues);
    }

    //method to read our db
    private void readBook() {
        //call the repository in read mode
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //defines the columns we want from our db (projection)
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        //query to our db to return what we want
        Cursor cursor = database.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        //attempt to read the data of the db if there is any and display it in our text view
        try {
            TextView textView = findViewById(R.id.inventory);
            textView.setText(null);

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int idValue = cursor.getInt(idColumnIndex);
                String nameValue = cursor.getString(productNameColumnIndex);
                float priceValue = cursor.getFloat(priceColumnIndex);//XX.YY (e.g.:12.99€) prices exist
                int quantityValue = cursor.getInt(quantityColumnIndex);
                String supplierNameValue = cursor.getString(supplierNameColumnIndex);
                String supplierPhoneValue = cursor.getString(supplierPhoneColumnIndex);

                textView.append(idValue + " | "
                        + nameValue + " | "
                        + priceValue + "€" + " | "
                        + quantityValue + " | "
                        + supplierNameValue + " | "
                        + supplierPhoneValue + "\n");
            }
        } finally {
            //close the cursor to prevent memory leaks
            cursor.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dummyEntry:
                //add dummy data
                book = dummyData[0];
                price = dummyData[1];
                quantity = dummyData[2];
                supplierName = dummyData[3];
                supplierPhone = dummyData[4];

                //call the method to add the data to the db
                insertBook();

                //read the db and display it
                readBook();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
