package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(ProfileActivity.this) {
            @Override
            public void onSwipeLeft() {
                Intent i = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
            @Override
            public void onSwipeRight() {
                Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        };

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);
    }
}
