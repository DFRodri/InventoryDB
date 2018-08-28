package com.example.android.bookdb.other_activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.content.CursorLoader;
import android.content.Loader;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bookdb.R;
import com.example.android.bookdb.data.BookContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookEdit extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentBook;
    private Uri uri;

    private static final int INVENTORY_LOADER_ID = 0;

    //Bind views with ButterKnife
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
    FloatingActionButton fabSave;
    @BindView(R.id.fabEdit)
    FloatingActionButton fabEdit;

    private boolean bookHasChanged;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);

        ButterKnife.bind(this);

        editData.setVisibility(View.VISIBLE);
        displayData.setVisibility(View.GONE);
        fabSave.setVisibility(View.VISIBLE);
        fabEdit.setVisibility(View.GONE);

        Intent getBook = getIntent();
        currentBook = getBook.getData();

        if (currentBook == null) {
            setTitle(R.string.addMenu);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editMenu);
            getLoaderManager().initLoader(INVENTORY_LOADER_ID, null, this);
        }

        bookEditTitle.setOnTouchListener(touchListener);
        bookEditPrice.setOnTouchListener(touchListener);
        bookEditQuantity.setOnTouchListener(touchListener);
        bookEditSupplier.setOnTouchListener(touchListener);
        bookEditPhoneSupplier.setOnTouchListener(touchListener);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();

            }
        });
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.notSavedData);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.keepEditing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        if (data == null || data.getCount() < 1) {
            return;
        }

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
            String currentSupplierPhone = data.getString(phone);

            bookEditTitle.setText(currentBookTitle);
            bookEditPrice.setText(currentBookPrice);
            bookEditQuantity.setText(currentBookQuantity);
            bookEditSupplier.setText(currentSupplierName);
            bookEditPhoneSupplier.setText(currentSupplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookEditTitle.setText("");
        bookEditPrice.setText("");
        bookEditQuantity.setText("");
        bookEditSupplier.setText("");
        bookEditPhoneSupplier.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.details_options, menu);
        if (currentBook == null) {
            MenuItem menuItem = menu.findItem(R.id.details);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteEntry:
                showDeleteConfirmationDialog();
                return true;
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
        finish();
    }

    private void checkData() {
        String bookTitle = bookEditTitle.getText().toString().trim();
        String bookPrice = bookEditPrice.getText().toString().trim();
        String bookQuantity = bookEditQuantity.getText().toString().trim();
        String bookSupplier = bookEditSupplier.getText().toString().trim();
        String bookSupplierPhoneNumber = bookEditPhoneSupplier.getText().toString().trim();

        if (currentBook == null) {
            if (TextUtils.isEmpty(bookTitle)) {
                Toast.makeText(this, R.string.missingTitle, Toast.LENGTH_SHORT).show();
                bookEditTitle.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(bookPrice)) {
                Toast.makeText(this, R.string.missingPrice, Toast.LENGTH_SHORT).show();
                bookEditPrice.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(bookQuantity)) {
                Toast.makeText(this, R.string.missingQuantity, Toast.LENGTH_SHORT).show();
                bookEditQuantity.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(bookSupplier)) {
                Toast.makeText(this, R.string.missingSupplier, Toast.LENGTH_SHORT).show();
                bookEditSupplier.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(bookSupplierPhoneNumber)) {
                Toast.makeText(this, R.string.missingSupplierPhone, Toast.LENGTH_SHORT).show();
                bookEditPhoneSupplier.requestFocus();
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, bookTitle);
            contentValues.put(BookEntry.COLUMN_PRICE, bookPrice);
            contentValues.put(BookEntry.COLUMN_QUANTITY, Integer.parseInt(bookQuantity));
            contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplier);
            contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumber);
            uri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
            if (uri == null) {
                Toast.makeText(this, getString(R.string.insertConfirmationFailed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insertConfirmation),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, bookTitle);
            contentValues.put(BookEntry.COLUMN_PRICE, bookPrice);
            contentValues.put(BookEntry.COLUMN_QUANTITY, Integer.parseInt(bookQuantity));
            contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplier);
            contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumber);
            int rowsAffected = getContentResolver().update(currentBook, contentValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.bookUpdatedFailed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.bookUpdatedSuccess),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}