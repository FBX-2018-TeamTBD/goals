package com.example.cassandrakane.goalz;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.ApprovedFriendRequests;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.RemovedFriends;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.NavigationHelper;
import utils.OnSwipeTouchListener;
import utils.Util;

public class ProfileActivity extends AppCompatActivity {

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 134;

    ImageView ivProfile;
    public TextView tvProgress;
    public TextView tvCompleted;
    TextView tvFriends;
    TextView tvUsername;

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.noGoals) RelativeLayout noGoalPage;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    private ParseUser user = ParseUser.getCurrentUser();

    private List<Goal> goals;
    private List<Goal> completed;
    private GoalAdapter goalAdapter;

    private ParseFile imageFile;
    private String photoFileName;
    private File photoFile;

    public int completedGoals = 0;
    public int progressGoals = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        progressBar.setVisibility(ProgressBar.VISIBLE);
        setSupportActionBar(toolbar);

        final NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                navigationHelper.toCamera();
                                break;
                            case R.id.nav_goals:
                                break;
                            case R.id.nav_feed:
                                navigationHelper.toFeed();
                                break;
                            case R.id.nav_friend_request:
                                navigationHelper.toFriendRequests();
                                break;
                            case R.id.nav_goal_request:
                                navigationHelper.toGoalRequests();
                                break;
                            case R.id.nav_logout:
                                navigationHelper.logout();
                                break;
                        }

                        return true;
                    }
                });

        ivProfile = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);
        tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvFriends = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvFriends);
        tvProgress = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvProgress);
        tvCompleted = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvCompleted);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(ProfileActivity.this) {
            @Override
            public void onSwipeLeft() {
                navigationHelper.toFeed();
            }
            @Override
            public void onSwipeRight() {
                navigationHelper.toCamera();
            }
        };

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);

        goals = new ArrayList<>();
        completed = new ArrayList<>();
        goalAdapter = new GoalAdapter(goals, true);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalAdapter);
        rvGoals.setOnTouchListener(onSwipeTouchListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Util.populateGoalsAsync(ProfileActivity.this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, completed, swipeContainer);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light);

        ParseFile file = (ParseFile) user.get("image");
        Util.setImage(user, file, getResources(), ivProfile, 16.0f);

        ParseACL acl = user.getACL();
        if (!acl.getPublicReadAccess()) {
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);
            user.setACL(acl);
        }

        Util.populateGoals(this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, completed);
        updateFriends();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(1).setChecked(true);
        if (goals.size() == 0) {
            noGoalPage.setVisibility(View.VISIBLE);
        } else {
            noGoalPage.setVisibility(View.GONE);
        }
    }

    public void updateFriends() {
        ParseQuery<ApprovedFriendRequests> query = ParseQuery.getQuery("ApprovedFriendRequests");
        query.include("toUser");
        query.include("fromUser");
        query.whereEqualTo("fromUser", user);

        Util.setRequests(user, navigationView);

        final List<ParseUser> friends = user.getList("friends");
        final List<ParseUser> newFriends = new ArrayList<>();
        query.findInBackground(new FindCallback<ApprovedFriendRequests>() {
            @Override
            public void done(List<ApprovedFriendRequests> objects, ParseException e) {
                newFriends.clear();
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ApprovedFriendRequests request = objects.get(i);
                        try {
                            deleteApprovedRequest(request.getObjectId());
                            ParseUser user = request.getParseUser("toUser").fetch();
                            newFriends.add(user);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                ParseQuery<RemovedFriends> query3 = ParseQuery.getQuery("RemovedRequests");
                query3.include("removedFriend");
                query3.include("remover");
                query3.whereEqualTo("removedFriend", user);
                final List<String> removedFriends = new ArrayList<>();
                query3.findInBackground(new FindCallback<RemovedFriends>() {
                    @Override
                    public void done(List<RemovedFriends> objects, ParseException e) {
                        if (objects != null) {
                            for (int i = 0; i < objects.size(); i++) {
                                RemovedFriends request = objects.get(i);
                                deleteRemoveRequest(request.getObjectId());
                                removedFriends.add(request.getParseUser("remover").getUsername());
                            }
                        }
                        friends.addAll(newFriends);
                        for (int i = friends.size() - 1; i >= 0; i--) {
                            try {
                                if (removedFriends.contains(friends.get(i).fetch().getUsername())) {
                                    friends.remove(i);
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        user.put("friends", friends);

                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    try {
                                        user.fetch();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    Log.i("Profile Activity", "Failed to update object, with error code: " + e.toString());
                                }
                            }
                        });
                        tvFriends.setText(String.valueOf(friends.size()));
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void deleteApprovedRequest(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ApprovedFriendRequests");
        query.whereEqualTo("objectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    object.delete();
                    object.saveInBackground();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void deleteRemoveRequest(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RemovedRequests");
        query.whereEqualTo("objectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    object.delete();
                    object.saveInBackground();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void selectImage(View v) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    onLaunchCamera();

                } else if (items[item].equals("Choose from Library")) {
                    onLaunchGallery();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = Util.getPhotoFileUri(photoFileName, this);

        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(this, "com.fbu.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        try {
            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getPackageManager()) != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        } catch(SecurityException e) {
            if (intent.resolveActivity(getPackageManager()) != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    public void onLaunchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String imagePath = photoFile.getAbsolutePath();
                Bitmap bitmap = Util.scaleCenterCrop(BitmapFactory.decodeFile(imagePath), 80, 80);
                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imagePath);
                    // Write the bytes of the bitmap to
                    try {
                        fos.write(bytes.toByteArray());
                        fos.close();
                        imageFile = new ParseFile(new File(imagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    Log.i("asdf", "error");
                    e.printStackTrace();
                }
                Util.setImageBitmap(bitmap, this, ivProfile);
                Util.onProfile(user, imageFile, this);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                    }
                    Uri uri = data.getData();
                    File file = new File(Util.getPath(this, uri));

                    Bitmap bitmap = null;
                    try {
                        bitmap = Util.scaleCenterCrop(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 80, 80);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Configure byte output stream
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // Compress the image further
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file.getAbsolutePath());
                        // Write the bytes of the bitmap to file
                        try {
                            fos.write(bytes.toByteArray());
                            fos.close();
                            imageFile = new ParseFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        Log.i("asdf", "error");
                        e.printStackTrace();
                    }
                    Util.setImageBitmap(bitmap, this, ivProfile);
                    Util.onProfile(user, imageFile, this);
                }

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addGoal(View v) {
        Intent i = new Intent(this, AddGoalActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

}
