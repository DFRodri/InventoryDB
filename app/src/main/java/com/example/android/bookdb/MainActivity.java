package com.example.android.bookdb;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.bookdb.adapter.BookAdapter;
import com.example.android.bookdb.custom_class.Book;
import com.example.android.bookdb.data.BookContract.BookEntry;
import com.example.android.bookdb.data.BookDBHelper;
import com.example.android.bookdb.fragment.EditData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //call to our db helper
    private BookDBHelper dbHelper = new BookDBHelper(this);

    //global variables
    private String book;
    private String price;
    private String quantity;
    private String supplierName;
    private String supplierPhone;

    private Uri currentBook;

    private static final int INVENTORY_LOADER = 0;

    private

    private RecyclerView.Adapter bookListAdapter;
    private RecyclerView.LayoutManager bookListLayoutManager;

    //bind views with butterknife
    @BindView(R.id.bookList)
    RecyclerView bookListRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton addBookFAB;

    @BindView(R.id.emptyView)
    TextView emptyStateTextView;

    private String[] dummyData = {"Book One", "12.99", "2", "Dude With Books", "123123123"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        FragmentManager fragmentManager = getFragmentManager();
        EditData editData = new EditData();

        bookListLayoutManager = new LinearLayoutManager(this);
        bookListRecyclerView.setLayoutManager(bookListLayoutManager);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        bookListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        bookListAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListRecyclerView.setAdapter(bookListAdapter);

        addBookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - go to fragment "add new book"
            }

        });
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
    /**private void readBook() {
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

        //attempt to read the data of the db if there is any and display it in our text view
        try {
            //TextView textView = findViewById(R.id.inventory);
            //textView.setText(null);

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

    }**/

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                currentBook,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (cursor.moveToFirst()){

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
