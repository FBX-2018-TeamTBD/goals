package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;

import org.parceler.Parcels;

public class SplashActivity extends AppCompatActivity {

    DataFetcher dataFetcher;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        currentUser = ParseUser.getCurrentUser();
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
                    dataFetcher = new DataFetcher(currentUser);
                    Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
                    i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(currentUser));
                    startActivity(i);
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
