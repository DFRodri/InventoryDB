package com.example.android.bookdb;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookdb.adapter.BookAdapter;
import com.example.android.bookdb.data.BookContract.BookEntry;
import com.example.android.bookdb.data.BookDBHelper;
import com.example.android.bookdb.other_activities.CreditsInfo;
import com.example.android.bookdb.other_activities.BookEdit;
import com.example.android.bookdb.other_activities.BookInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //call to our db helper
    private final BookDBHelper dbHelper = new BookDBHelper(this);

    private BookAdapter bookAdapter;

    private static final int INVENTORY_LOADER = 0;

    //bind views with ButterKnife
    @BindView(R.id.bookList)
    ListView bookList;
    @BindView(R.id.fab)
    FloatingActionButton addBookFAB;
    @BindView(R.id.emptyView)
    LinearLayout emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        bookList.setEmptyView(emptyStateTextView);

        bookAdapter = new BookAdapter(this, null, 0);
        bookList.setAdapter(bookAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, BookInfo.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        addBookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookEdit.class);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
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
                alertDialogBuilder.setMessage(R.string.warningRemoveAll);
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                        Toast.makeText(getApplicationContext(), R.string.dbCleaned, Toast.LENGTH_SHORT).show();
                        Log.i("Entry Removed", rowsDeleted + "rows deleted from your database");
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.credits:
                Intent intent = new Intent(MainActivity.this, CreditsInfo.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
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
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        bookAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }
}
