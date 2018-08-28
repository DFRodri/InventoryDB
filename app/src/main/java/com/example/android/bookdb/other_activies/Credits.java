package com.example.android.bookdb.other_activies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.bookdb.R;


public class Credits extends AppCompatActivity {

    //TODO - completar isto como actividade
    //to make sure that the fragment knows where we're working
    //making it this way to keep the API used as lower as possible
    Context context;


    public void Banana(Bundle savedInstanceState) {
        //setup the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.credits);

        //add a list of items to our fragment
        String[] credits = {
                Integer.toString(R.string.urlName0),
                Integer.toString(R.string.urlName1),
                Integer.toString(R.string.urlName2),
                Integer.toString(R.string.urlName3),
                Integer.toString(R.string.urlName4),
                Integer.toString(R.string.urlName5),
                Integer.toString(R.string.urlName6),
                Integer.toString(R.string.urlName7)
        };
        builder.setItems(credits, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri link = null;
                switch (which) {
                    case 0:
                        link = Uri.parse(Integer.toString(R.string.url0));
                        break;
                    case 1:
                        link = Uri.parse(Integer.toString(R.string.url1));
                        break;
                    case 2:
                        link = Uri.parse(Integer.toString(R.string.url2));
                        break;
                    case 3:
                        link = Uri.parse(Integer.toString(R.string.url3));
                        break;
                    case 4:
                        link = Uri.parse(Integer.toString(R.string.url4));
                        break;
                    case 5:
                        link = Uri.parse(Integer.toString(R.string.url5));
                        break;
                    case 6:
                        link = Uri.parse(Integer.toString(R.string.url6));
                        break;
                    case 7:
                        link = Uri.parse(Integer.toString(R.string.url7));
                        break;
                }
                Intent openLink = new Intent(Intent.ACTION_VIEW, link);
                if (openLink.resolveActivity(getPackageManager()) != null) {
                    startActivity(openLink);
                } else {
                    Toast.makeText(Credits.this, getString(R.string.appNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
