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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class FeedActivity extends AppCompatActivity {

    public final static int ADD_FRIEND_ACTIVITY_REQUEST_CODE = 14;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 134;

    List<ParseUser> friends;
    FriendAdapter friendAdapter;
    List<Goal> goals;
    List<Goal> incompleted;

    ImageView ivProfile;
    TextView tvProgress;
    TextView tvCompleted;
    TextView tvFriends;
    TextView tvUsername;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.noFriends) RelativeLayout noFriendsPage;

    ParseUser user;
    int completedGoals;
    int progressGoals;

    private ParseFile imageFile;
    private String photoFileName;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ButterKnife.bind(this);

        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));
        if (user == null) {
            user = ParseUser.getCurrentUser();
        }
        progressBar.setVisibility(ProgressBar.VISIBLE);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(FeedActivity.this) {
            @Override
            public void onSwipeRight() {
                toGoals();
            }
        };

        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                toCamera();
                                break;
                            case R.id.nav_goals:
                                toGoals();
                                break;
                            case R.id.nav_feed:
                                break;
                            case R.id.nav_friend_request:
                                toFriendRequests();
                                break;
                            case R.id.nav_goal_request:
                                toGoalRequests();
                                break;
                            case R.id.nav_logout:
                                logout();
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

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);

        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(friendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        rvFriends.addItemDecoration(itemDecor);
        rvFriends.setOnTouchListener(onSwipeTouchListener);

        goals = new ArrayList<>();
        incompleted = new ArrayList<>();

        populateFriends();
        Util.populateGoals(this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, incompleted);
        Util.setRequests(user, navigationView);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFriends();
//        populateGoals();
    }

//    public void populateFriends(){
//        ParseQuery<ParseUser> localUserQuery = ParseUser.getQuery();
//        localUserQuery.fromLocalDatastore();
//        localUserQuery.whereNotEqualTo("objectId", user.getObjectId());
//        localUserQuery.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                friends.clear();
//                friends.addAll(objects);
//                tvFriends.setText(String.valueOf(friends.size()));
//                friendAdapter.notifyDataSetChanged();
//                ParseObject.unpinAllInBackground("friends");
//                ParseObject.pinAllInBackground("friends", objects);
//                progressBar.setVisibility(ProgressBar.INVISIBLE);
//            }
//        });
//    }
    public void populateFriends() {
        List<ParseUser> arr = null;
//        try {
        arr = user.getList("friends");
//        } catch(ParseException e) {
//            e.printStackTrace();
//        }
        friends.clear();
        if (arr != null) {
//            try {
//                ParseUser.fetchAllIfNeeded(arr);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            friends.addAll(arr);
        }

        tvFriends.setText(String.valueOf(friends.size()));
        ParseObject.unpinAllInBackground(arr);
        ParseObject.pinAllInBackground(arr);

        if (friends.size() == 0) {
            noFriendsPage.setVisibility(View.VISIBLE);
        } else {
            noFriendsPage.setVisibility(View.GONE);
        }
        friendAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

   /* public void populateGoals(){
        goals = new ArrayList<>();
        ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Goal");
        localQuery.fromLocalDatastore();
        localQuery.whereEqualTo("user", user);
        localQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    goals.clear();
                    for (int i=0; i <objects.size(); i++){
                        Goal goal = (Goal) objects.get(i);

                        if (goal.getCompleted()) {
                            completedGoals += 1;
                            goals.add(goal);
                        } else {
                            progressGoals += 1;
                            goals.add(0, goal);
                        }
                    }
                    tvProgress.setText(String.valueOf(progressGoals));
                    tvCompleted.setText(String.valueOf(completedGoals));
                    tvUsername.setText(user.getUsername());
                }
            }
        });
    }
*/
//    public void populateGoals() {
//        List<ParseObject> arr = user.getList("goals");
//        goals = new ArrayList<>();
//        completedGoals = 0;
//        progressGoals = 0;
//        if (arr != null) {
////            try {
////                ParseObject.fetchAllIfNeeded(arr);
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
//            for(int i = 0; i < arr.size(); i++) {
//                Goal goal = (Goal) arr.get(i);
//                if (goal.getCompleted()) {
//                    completedGoals += 1;
//                    goals.add(goal);
//                } else {
//                    progressGoals += 1;
//                    goals.add(goal);
//                }
//            }
//            tvProgress.setText(String.valueOf(progressGoals));
//            tvCompleted.setText(String.valueOf(completedGoals));
//            tvUsername.setText(ParseUser.getCurrentUser().getUsername());
//        }
//        ParseFile file = (ParseFile) user.get("image");
//        Util.setImage(user, file, getResources(), ivProfile, 16.0f);
////        Util.setImage(user, "image", getResources(), ivProfile, 16.0f);
//
//    }

    public void addFriend(View v) {
        Intent i = new Intent(this, SearchFriendsActivity.class);
        i.putExtra("requestActivity", this.getClass().getSimpleName());
        startActivityForResult(i, ADD_FRIEND_ACTIVITY_REQUEST_CODE);
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void toCamera() {
        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        i.putExtra("goals", (Serializable) incompleted);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void toGoals() {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void toFriendRequests() {
        Intent i = new Intent(getApplicationContext(), FriendRequestsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void toGoalRequests() {
        Intent i = new Intent(getApplicationContext(), GoalRequestsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void logout() {
        ParseUser.logOut();
        Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_LONG);
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
                    int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        } catch(SecurityException e) {
            if (intent.resolveActivity(getPackageManager()) != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
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

}
