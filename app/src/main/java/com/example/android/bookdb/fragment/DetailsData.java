package com.example.android.bookdb.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @BindView(R.id.fabEdit)
    FloatingActionButton editBookFAB;

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

        editBookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditData.class);
                intent.setData(currentBook);
                if (currentBook != null) {
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), R.string.emptyBook, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    @NonNull
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }

    //TODO - Is the following correct? Need to be tested.
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.details_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteEntry:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.warningSingleEntry);
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentBook != null) {
                            getActivity().getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                            Toast.makeText(getActivity(), R.string.entryCleaned, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.emptyBook, Toast.LENGTH_SHORT).show();
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
