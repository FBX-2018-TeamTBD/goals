package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class FriendsModal  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modal_friend);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int)(dm.widthPixels * 0.75);
        int height = (int)(dm.heightPixels * 0.75);

        getWindow().setLayout(width, height);
    }

}
