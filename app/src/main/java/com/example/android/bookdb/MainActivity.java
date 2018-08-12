package com.example.android.bookdb;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.android.bookdb.adapter.BookAdapter;
import com.example.android.bookdb.data.BookContract.BookEntry;
import com.example.android.bookdb.data.BookDBHelper;
import com.example.android.bookdb.fragment.Credits;
import com.example.android.bookdb.fragment.EditData;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //call to our db helper
    private BookDBHelper dbHelper = new BookDBHelper(this);

    //global variables
    Context context;

    private String book;
    private String price;
    private String quantity;
    private String supplierName;
    private String supplierPhone;

    private Uri currentBook;

    private static final int INVENTORY_LOADER = 0;

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

        bookListLayoutManager = new LinearLayoutManager(this);
        bookListRecyclerView.setLayoutManager(bookListLayoutManager);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        bookListRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        bookListAdapter = new BookAdapter(this, null);
        bookListRecyclerView.setAdapter(bookListAdapter);

        addBookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditData.class);
                startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeALL:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(R.string.warning);
                alertDialogBuilder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                        Toast.makeText(context, R.string.dbCleaned, Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.credits:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("credits");
                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                fragmentTransaction.addToBackStack(null);

                DialogFragment creditsFragment = new Credits();
                creditsFragment.show(fragmentTransaction, "credits");
                break;

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
        //TODO - Something missing here??
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.notifyDataSetChanged();
    }
}
