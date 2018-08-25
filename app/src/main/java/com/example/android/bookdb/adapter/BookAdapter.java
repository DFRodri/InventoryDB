package com.example.android.bookdb.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.example.android.bookdb.fragment.DetailsData;

import static android.content.ContentValues.TAG;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    @BindView(R.id.bookTitle)
    TextView bookTitle;
    @BindView(R.id.bookPrice)
    TextView bookPrice;
    @BindView(R.id.bookQuantity)
    TextView bookQuantity;
    @BindView(R.id.bookSale)
    TextView bookSale;

    private CursorAdapter cursorAdapter;

    private Context context;

    public BookAdapter(Context context, Cursor cursor) {
        this.context = context;

        cursorAdapter = new CursorAdapter(context, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list, parent, false);
            }

            @Override
            public void bindView(final View view, final Context context, Cursor cursor) {
                int id =
                        cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
                String title =
                        cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
                double price =
                        cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
                final int quantity =
                        cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));

                final Uri currentBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                bookSale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, String.valueOf(quantity));
                        int newQuantity = quantity;
                        ContentResolver contentResolver = view.getContext().getContentResolver();
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
                        }
                    }
                });

                bookTitle.setText(title);
                String finalPrice = price + Integer.toString(R.string.euroSymbol);
                bookPrice.setText(finalPrice);
                String finalQuantity = Integer.toString(R.string.quantity) + quantity;
                bookQuantity.setText(finalQuantity);
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = cursorAdapter.newView(context, cursorAdapter.getCursor(), parent);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        cursorAdapter.getCursor().moveToPosition(position);
        cursorAdapter.bindView(holder.itemView, context, cursorAdapter.getCursor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                Intent intent = new Intent(context, DetailsData.class);
                Uri currentBook = ContentUris.withAppendedId(BookEntry.CONTENT_URI, adapterPosition);
                intent.setData(currentBook);
                if (currentBook != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.emptyBook, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }
}
