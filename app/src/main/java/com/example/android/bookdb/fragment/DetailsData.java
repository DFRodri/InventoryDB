package com.example.android.bookdb.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.ContentUris;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookdb.R;
import com.example.android.bookdb.data.BookContract.BookEntry;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsData extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentBook;
    private Uri link = null;

    private CursorAdapter bookAdapter;

    private static final int INVENTORY_LOADER_ID = 0;

    //Bind views with Butterknife
    private @BindView(R.id.displayData)
    LinearLayout displayData;
    private @BindView(R.id.editData)
    LinearLayout editData;
    private @BindView(R.id.bookTitle)
    TextView bookTitle;
    private @BindView(R.id.bookPrice)
    TextView bookPrice;
    private @BindView(R.id.bookQuantity)
    TextView bookQuantity;
    private @BindView(R.id.bookSupplier)
    TextView bookSupplier;
    private @BindView(R.id.bookSupplierPhone)
    TextView bookPhoneSupplier;

    private int currentSupplierPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.data, container, false);

        ButterKnife.bind(this, rootView);

        displayData.setVisibility(View.VISIBLE);
        editData.setVisibility(View.GONE);
        getActivity().setTitle(R.string.detailsMenu);

        currentBook = getActivity().getIntent().getData();

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(INVENTORY_LOADER_ID, null, this);

        return rootView;
    }

    @Override
    @NonNull
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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

        bookPhoneSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = Integer.toString(currentSupplierPhone);
                link = Uri.parse(phoneNumber);
                Intent openLink = new Intent(Intent.ACTION_VIEW, link);
                if (openLink.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(openLink);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.appNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }
}
