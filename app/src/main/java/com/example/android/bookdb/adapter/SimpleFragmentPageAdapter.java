package com.example.android.bookdb.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.bookdb.fragment.DetailsData;
import com.example.android.bookdb.fragment.EditData;

public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {

    //global variable needed to make the Fragment Page Adapter work
    private final Context context;

    public SimpleFragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //TODO fragment dos details
                return new DetailsData();
            case 1:
                //TODO fragment do edit
                return new EditData();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
