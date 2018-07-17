package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(CameraActivity.this) {
            @Override
            public void onSwipeLeft() {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        };

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);
    }
}
