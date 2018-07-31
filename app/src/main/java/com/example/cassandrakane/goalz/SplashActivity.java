package com.example.cassandrakane.goalz;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.parse.ParseUser;

import com.example.cassandrakane.goalz.utils.DataFetcher;

public class SplashActivity extends AppCompatActivity {

    DataFetcher dataFetcher;
    ParseUser currentUser;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        currentUser = ParseUser.getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        scheduleSplashScreen();
    }

    private void scheduleSplashScreen(){
        int duration = 500;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    dataFetcher = new DataFetcher(currentUser, SplashActivity.this);
                    finish();
                }  else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, duration);
    }
}
