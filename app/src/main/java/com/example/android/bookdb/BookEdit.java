package com.example.android.bookdb;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.android.bookdb.data.BookContract.BookEntry;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookEdit extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentBook;

    private static final int INVENTORY_LOADER_ID = 0;

    //Bind views with Butterknife
    @BindView(R.id.displayData)
    LinearLayout displayData;
    @BindView(R.id.editData)
    LinearLayout editData;

    @BindView(R.id.insertBookTitle)
    EditText bookEditTitle;
    @BindView(R.id.insertBookPrice)
    EditText bookEditPrice;
    @BindView(R.id.insertBookQuantity)
    EditText bookEditQuantity;
    @BindView(R.id.insertBookSupplierName)
    EditText bookEditSupplier;
    @BindView(R.id.insertBookSupplierPhone)
    EditText bookEditPhoneSupplier;

    @BindView(R.id.fabSave)
    FloatingActionButton saveBookFAB;

    private boolean bookHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);

        ButterKnife.bind(this);

        editData.setVisibility(View.VISIBLE);
        displayData.setVisibility(View.GONE);

        currentBook = getIntent().getData();

        if (currentBook == null) {
            setTitle(R.string.addMenu);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editMenu);
            currentBook = getIntent().getData();
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(INVENTORY_LOADER_ID, null, this);
        }

        bookEditTitle.setOnTouchListener(touchListener);
        bookEditPrice.setOnTouchListener(touchListener);
        bookEditQuantity.setOnTouchListener(touchListener);
        bookEditSupplier.setOnTouchListener(touchListener);
        bookEditPhoneSupplier.setOnTouchListener(touchListener);

        saveBookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData(currentBook);

            }
        });
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new android.content.CursorLoader(this,
                currentBook,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() <= 0) {
            return;
        }

        if (data.moveToFirst()) {
            int id = data.getColumnIndex(BookEntry._ID);
            int title = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int price = data.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantity = data.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplier = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int phone = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            currentBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

            String currentBookTitle = data.getString(title);
            String currentBookPrice = Double.toString(data.getDouble(price));
            int currentBookQuantity = data.getInt(quantity);
            String currentSupplierName = data.getString(supplier);
            int currentSupplierPhone = data.getInt(phone);

            //format phone numbers to be easier to read
            DecimalFormat decimalFormat = new DecimalFormat("### ### ### ###");
            String displayPhoneNumber = decimalFormat.format(currentSupplierPhone);

            bookEditTitle.setText(currentBookTitle);
            bookEditPrice.setText(currentBookPrice);
            bookEditQuantity.setText(currentBookQuantity);
            bookEditSupplier.setText(currentSupplierName);
            bookEditPhoneSupplier.setText(displayPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        bookEditTitle.setText("");
        bookEditPrice.setText("");
        bookEditQuantity.setText("");
        bookEditSupplier.setText("");
        bookEditPhoneSupplier.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBook == null) {
            MenuItem menuItem = menu.findItem(R.id.deleteEntry);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteEntry:
                showDeleteConfirmationDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.removeEntry);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (currentBook != null) {
            int rowsDeleted = getContentResolver().delete(currentBook, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.removeFailed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.removeConfirmed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkData(Uri currentBook) {
        String bookTitle = bookEditTitle.getText().toString().trim();
        String bookPrice = bookEditPrice.getText().toString().trim();
        String bookQuantity = bookEditQuantity.getText().toString().trim();
        String bookSupplier = bookEditSupplier.getText().toString().trim();
        String bookSupplierPhoneNumber = bookEditPhoneSupplier.getText().toString().trim();

        if (currentBook == null) {
            if (TextUtils.isEmpty(bookTitle)) {
                Toast.makeText(this, R.string.missingTitle, Toast.LENGTH_SHORT).show();
                bookEditTitle.requestFocus();
            } else if (TextUtils.isEmpty(bookPrice)) {
                Toast.makeText(this, R.string.missingPrice, Toast.LENGTH_SHORT).show();
                bookEditPrice.requestFocus();
            } else if (TextUtils.isEmpty(bookQuantity)) {
                Toast.makeText(this, R.string.missingQuantity, Toast.LENGTH_SHORT).show();
                bookEditQuantity.requestFocus();
            } else if (TextUtils.isEmpty(bookSupplier)) {
                Toast.makeText(this, R.string.missingSupplier, Toast.LENGTH_SHORT).show();
                bookEditSupplier.requestFocus();
            } else if (TextUtils.isEmpty(bookSupplierPhoneNumber)) {
                Toast.makeText(this, R.string.missingSupplierPhone, Toast.LENGTH_SHORT).show();
                bookEditPhoneSupplier.requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, bookTitle);
            contentValues.put(BookEntry.COLUMN_PRICE, bookPrice);
            contentValues.put(BookEntry.COLUMN_QUANTITY, bookQuantity);
            contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplier);
            contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumber);

            Uri addNewBook = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
            finish();
            Toast.makeText(this, R.string.insertConfirmation, Toast.LENGTH_SHORT).show();
        }
    }

}
