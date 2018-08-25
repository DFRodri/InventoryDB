package com.example.android.bookdb.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.bookdb.R;
import com.example.android.bookdb.data.BookContract.BookEntry;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditData extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.data, container, false);

        ButterKnife.bind(this, rootView);

        editData.setVisibility(View.VISIBLE);
        displayData.setVisibility(View.GONE);

        currentBook = getActivity().getIntent().getData();

        if (currentBook == null) {
            getActivity().setTitle(R.string.addMenu);
            getActivity().invalidateOptionsMenu();
        } else {
            getActivity().setTitle(R.string.editMenu);
            currentBook = getActivity().getIntent().getData();
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

        return rootView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(getContext(),
                currentBook,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
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
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookEditTitle.setText("");
        bookEditPrice.setText("");
        bookEditQuantity.setText("");
        bookEditSupplier.setText("");
        bookEditPhoneSupplier.setText("");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBook == null) {
            MenuItem menuItem = menu.findItem(R.id.deleteEntry);
            menuItem.setVisible(false);
        }
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(R.string.removeEntry);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
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

    private void deletePet() {
        if (currentBook != null) {
            int rowsDeleted = getContext().getContentResolver().delete(currentBook, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(getContext(), R.string.removeFailed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.removeConfirmed, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), R.string.missingTitle, Toast.LENGTH_SHORT).show();
                bookEditTitle.requestFocus();
            } else if (TextUtils.isEmpty(bookPrice)) {
                Toast.makeText(getContext(), R.string.missingPrice, Toast.LENGTH_SHORT).show();
                bookEditPrice.requestFocus();
            } else if (TextUtils.isEmpty(bookQuantity)) {
                Toast.makeText(getContext(), R.string.missingQuantity, Toast.LENGTH_SHORT).show();
                bookEditQuantity.requestFocus();
            } else if (TextUtils.isEmpty(bookSupplier)) {
                Toast.makeText(getContext(), R.string.missingSupplier, Toast.LENGTH_SHORT).show();
                bookEditSupplier.requestFocus();
            } else if (TextUtils.isEmpty(bookSupplierPhoneNumber)) {
                Toast.makeText(getContext(), R.string.missingSupplierPhone, Toast.LENGTH_SHORT).show();
                bookEditPhoneSupplier.requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, bookTitle);
            contentValues.put(BookEntry.COLUMN_PRICE, bookPrice);
            contentValues.put(BookEntry.COLUMN_QUANTITY, bookQuantity);
            contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplier);
            contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumber);

            Uri addNewBook =  getContext().getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
            getActivity().finish();
            Toast.makeText(getContext(), R.string.insertConfirmation, Toast.LENGTH_SHORT).show();
        }
    }
}
