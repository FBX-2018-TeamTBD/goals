package com.example.cassandrakane.goalz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername) EditText tvUsername;
    @BindView(R.id.tvPassword) EditText tvPassword;
    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 134;

    private ParseFile imageFile;
    private String photoFileName;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        tvUsername.setText("");
        tvPassword.setText("");

        tvUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(v, SignupActivity.this);
                }
            }
        });

        tvPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(v, SignupActivity.this);
                }
            }
        });
    }

    public void signup(View v) {
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
                    Toast.makeText(SignupActivity.this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(SignupActivity.this, "Sign up failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void initializeUser() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("friends", new ArrayList<ParseUser>());
        currentUser.put("goals", new ArrayList<Goal>());
        if (imageFile != null) {
            currentUser.put("image", imageFile);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        currentUser.fetch();
                        Intent i = new Intent(SignupActivity.this, MainActivity.class);
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

    public void selectImage(View v) {
        Util.selectImage(this, photoFileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Util.getImageFromCamera(this, imageFile, ivProfile);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                    }
                    Uri uri = data.getData();
                    Util.getImageFromGallery(this, uri, imageFile, ivProfile);
                }
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goBack(View v) {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}
