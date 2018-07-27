package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cassandrakane.goalz.models.Goal;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    DataFetcher dataFetcher;
    Context context;

    @BindView(R.id.tvUsername) EditText tvUsername;
    @BindView(R.id.tvPassword) EditText tvPassword;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        tvUsername.setText("");
        tvPassword.setText("");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        }

        tvUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        tvPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void login(View v) {
        progressBar.setVisibility(View.VISIBLE);
        final String username = tvUsername.getText().toString();
        final String password = tvPassword.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // transition to home screen
                    dataFetcher = new DataFetcher(user, LoginActivity.this);
//                    Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
//                    startActivity(i);
//                    finish();
                    progressBar.setVisibility(View.GONE);
                    overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
                    Toast.makeText(LoginActivity.this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login failed. Try again or sign up.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signUp(View v) {
        // Create the ParseUser
        final ParseUser user = new ParseUser();
        // Set core properties
        final String username = tvUsername.getText().toString();
        final String password = tvPassword.getText().toString();
        user.setUsername(username);
        user.setPassword(password);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    initializeUser();
                    tvUsername.setText("");
                    tvPassword.setText("");
                    Toast.makeText(LoginActivity.this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(LoginActivity.this, "Sign up failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void initializeUser() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("friends", new ArrayList<ParseUser>());
        currentUser.put("goals", new ArrayList<Goal>());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        currentUser.fetch();
//                        dataFetcher = new DataFetcher(currentUser, LoginActivity.this);
                        // transition to home screen
                        Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
                        finish();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("LoginActivity", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

}
