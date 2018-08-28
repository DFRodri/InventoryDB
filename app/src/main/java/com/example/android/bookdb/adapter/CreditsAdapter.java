package com.example.android.bookdb.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.bookdb.R;
import com.example.android.bookdb.custom_class.Credits;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditsAdapter extends ArrayAdapter<Credits> {

    public CreditsAdapter(Activity context, ArrayList<Credits> creditsList) {
        super(context, 0, creditsList);
    }

    //The class that holds the views that will be recycled
    static class ViewHolder {
        //bind views with ButterKnife
        @BindView(R.id.resourceName)
        TextView creatorName;
        @BindView(R.id.resourceURL)
        TextView creatorURL;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //The getView method to display (return) each row of what we want as we want with a certain layout as a base
    //Makes use of a ViewHolder class created previously to recycle data even further
    //R.id.<layout name> -> the layout
    //getMethods -> what we want to be displayed in each recycled view
    //the views loaded with the holder -> what we want to populate with data
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.credits_list, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //each element (Item) of our list has data stored in different positions
        //this makes sure that we're accessing the right one
        final Credits currentCredits = getItem(position);

        //fetches the info from the CreditsInfo Object stored
        String newName = currentCredits.getNewCreditName();
        String newURL = currentCredits.getNewCreditURL();

        //sets the info fetched from the get methods so we can have something displayed
        holder.creatorName.setText(newName);
        holder.creatorURL.setText(newURL);

        //returns all the work made related the convertView to the caller
        return convertView;

    }
}
