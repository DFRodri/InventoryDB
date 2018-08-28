package com.example.android.bookdb.other_activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.android.bookdb.R;
import com.example.android.bookdb.adapter.CreditsAdapter;
import com.example.android.bookdb.custom_class.Credits;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditsInfo extends AppCompatActivity {

    //global variable that holds the resources used
    private final ArrayList<Credits> credits = new ArrayList<>();


    @BindView(R.id.creditsList)
    ListView resourcesUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);

        ButterKnife.bind(this);

        setTitle(getString(R.string.creditsMenu));

        //call to method that add the objects with the info of the resources used to the array list
        setupCredits();

        //adapter of the credits' list view
        CreditsAdapter creditsAdapter = new CreditsAdapter(this, credits);
        resourcesUsed.setAdapter(creditsAdapter);

    }

    //method that adds the credits objects to the array list
    private void setupCredits() {
        credits.add(new Credits(getString(R.string.urlName0), getString(R.string.url0)));
        credits.add(new Credits(getString(R.string.urlName1), getString(R.string.url1)));
        credits.add(new Credits(getString(R.string.urlName2), getString(R.string.url2)));
    }

}
