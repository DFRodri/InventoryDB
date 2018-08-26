package com.example.android.bookdb.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookdb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.android.bookdb.data.BookContract.BookEntry;

import static android.content.ContentValues.TAG;

public class BookAdapter extends CursorAdapter {

    static class ViewHolder {
        @BindView(R.id.bookTitle)
        TextView bookTitle;
        @BindView(R.id.bookPrice)
        TextView bookPrice;
        @BindView(R.id.bookQuantity)
        TextView bookQuantity;
        @BindView(R.id.bookSale)
        TextView bookSale;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public BookAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        int id =
                cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        String title =
                cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String price =
                cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
        final int quantity =
                cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));

        final Uri currentBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
        holder.bookTitle.setText(title);
        holder.bookPrice.setText(price);
        holder.bookQuantity.setText(quantity);

        holder.bookSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, String.valueOf(quantity));
                int newQuantity = quantity;
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                if (quantity > 0) {
                    contentValues.put(BookEntry.COLUMN_QUANTITY, newQuantity--);
                    contentResolver.update(
                            currentBook,
                            contentValues,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentBook, null);
                    Log.i(TAG, String.valueOf(newQuantity));
                }else{
                    Toast.makeText(context, R.string.noStockAvailable, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
