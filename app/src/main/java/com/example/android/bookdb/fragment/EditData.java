package com.example.android.bookdb.fragment;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bookdb.R;
import com.example.android.bookdb.data.BookContract.BookEntry;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditData extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentBook;
    private Uri link = null;

    private CursorAdapter bookAdapter;

    private static final int INVENTORY_LOADER_ID = 0;

    //Bind views with Butterknife
    private @BindView(R.id.displayData)
    LinearLayout displayData;
    private @BindView(R.id.editData)
    LinearLayout editData;
    private @BindView(R.id.insertBookTitle)
    EditText bookTitle;
    private @BindView(R.id.insertBookPrice)
    EditText bookPrice;
    private @BindView(R.id.insertBookQuantity)
    EditText bookQuantity;
    private @BindView(R.id.insertBookSupplierName)
    EditText bookSupplier;
    private @BindView(R.id.insertBookSupplierPhone)
    EditText bookPhoneSupplier;

    private int currentSupplierPhone;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.data, container, false);

        ButterKnife.bind(this, rootView);

        editData.setVisibility(View.VISIBLE);
        displayData.setVisibility(View.GONE);
        getActivity().setTitle(R.string.editMenu);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(INVENTORY_LOADER_ID, null, this);

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

        return new CursorLoader(getActivity(),
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
            currentSupplierPhone = data.getInt(phone);

            //format phone numbers to be easier to read
            DecimalFormat decimalFormat = new DecimalFormat("### ### ### ###");
            String displayPhoneNumber = decimalFormat.format(currentSupplierPhone);

            bookTitle.setText(currentBookTitle);
            bookPrice.setText(currentBookPrice);
            bookQuantity.setText(currentBookQuantity);
            bookSupplier.setText(currentSupplierName);
            bookPhoneSupplier.setText(displayPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
    //Checkers to use from the stuff made wrongly on the adapter
    /**
     *
     if (TextUtils.isEmpty(title)){
     Toast.makeText(context,R.string.missingTitle, Toast.LENGTH_SHORT).show();
     }else{
     bookTitle.setText(title);
     }

     if (TextUtils.isEmpty(price)){
     Toast.makeText(context,R.string.missingPrice, Toast.LENGTH_SHORT).show();
     bookPrice.setText("0");
     }else{
     bookPrice.setText(price);
     }

     if (TextUtils.isEmpty(quantity)){
     //TODO - convert quantity to int and check if > 0
     Toast.makeText(context,R.string.missingQuantity, Toast.LENGTH_SHORT).show();
     bookQuantity.setText("0");
     }else{
     int finalQuantity = Integer.valueOf(quantity);
     if (finalQuantity <= 0){}
     bookQuantity.setText(quantity);
     }
     //force focus on edit and show keyboard
     editText.requestFocus();
     InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
     imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
     **/
}
