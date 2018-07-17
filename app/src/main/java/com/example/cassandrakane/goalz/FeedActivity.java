package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(FeedActivity.this) {
            @Override
            public void onSwipeRight() {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        };

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);
    }
}
