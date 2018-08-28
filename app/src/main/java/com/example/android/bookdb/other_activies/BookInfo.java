package com.example.android.bookdb.other_activies;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookdb.R;
import com.example.android.bookdb.data.BookContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookInfo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentBook;
    private Uri link;

    private String currentSupplierPhone;

    private static final int INVENTORY_LOADER_ID = 0;

    //Bind views with Butterknife
    @BindView(R.id.displayData)
    LinearLayout displayData;
    @BindView(R.id.editData)
    LinearLayout editData;
    @BindView(R.id.bookTitle)
    TextView bookTitle;
    @BindView(R.id.bookPrice)
    TextView bookPrice;
    @BindView(R.id.bookQuantity)
    TextView bookQuantity;
    @BindView(R.id.bookSupplier)
    TextView bookSupplier;
    @BindView(R.id.bookSupplierPhone)
    TextView bookPhoneSupplier;

    @BindView(R.id.fabSave)
    FloatingActionButton fabSave;
    @BindView(R.id.fabEdit)
    FloatingActionButton fabEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);

        ButterKnife.bind(this);

        displayData.setVisibility(View.VISIBLE);
        editData.setVisibility(View.GONE);
        fabSave.setVisibility(View.GONE);
        fabEdit.setVisibility(View.VISIBLE);
        setTitle(R.string.detailsMenu);

        Intent getBook = getIntent();
        currentBook = getBook.getData();

        if (currentBook != null) {
            getLoaderManager().initLoader(INVENTORY_LOADER_ID, null, this);
        }

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookInfo.this, BookEdit.class);
                intent.setData(currentBook);
                if (currentBook != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.emptyBook, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                currentBook,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int title = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int price = data.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantity = data.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplier = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int phone = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String currentBookTitle = data.getString(title);
            String currentBookPrice = data.getString(price);
            String currentBookQuantity = data.getString(quantity);
            String currentSupplierName = data.getString(supplier);
            currentSupplierPhone = data.getString(phone);

            currentBookPrice += " " + getString(R.string.euroSymbol);
            currentBookQuantity += " " + getString(R.string.availableUnities);

            bookTitle.setText(currentBookTitle);
            bookPrice.setText(currentBookPrice);
            bookQuantity.setText(currentBookQuantity);
            bookSupplier.setText(currentSupplierName);
            bookPhoneSupplier.setText(currentSupplierPhone);
        }

        bookPhoneSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = Uri.parse("tel:"+currentSupplierPhone+"\"");
                Intent openDial = new Intent(Intent.ACTION_DIAL);
                openDial.setData(link);
                if (link != null) {
                    startActivity(openDial);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.appNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.details_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteEntry:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(R.string.warningSingleEntry);
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentBook != null) {
                            getContentResolver().delete(currentBook, null, null);
                            Toast.makeText(getApplicationContext(), R.string.entryCleaned, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.emptyBook, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
