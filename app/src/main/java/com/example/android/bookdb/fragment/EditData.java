package com.example.android.bookdb.fragment;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bookdb.R;

public class EditData extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.data, container, false);

        return rootView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

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
     //forçar o focus no edit text e mostrar keyboard
     editText.requestFocus();
     InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
     imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
     **/
}
