package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
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

    ImageView ivProfile;
    TextView tvProgress;
    TextView tvCompleted;
    TextView tvFriends;
    TextView tvUsername;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;

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

        user = ParseUser.getCurrentUser();
        progressBar.setVisibility(ProgressBar.VISIBLE);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(FeedActivity.this) {
            @Override
            public void onSwipeRight() {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        };

        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.menu));

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
                                finish();
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                break;
                            case R.id.nav_feed:
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
        populateGoals();
        populateFriends();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFriends();
        populateGoals();
    }

    public void populateFriends() {
        List<ParseUser> arr = null;
        try {
            arr = user.fetch().getList("friends");
        } catch(ParseException e) {
            e.printStackTrace();
        }
        friends.clear();
        if (arr != null) {
            try {
                ParseUser.fetchAllIfNeeded(arr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            friends.addAll(arr);
        }
        friendAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    public void populateGoals() {
        List<ParseObject> arr = user.getList("goals");
        completedGoals = 0;
        progressGoals = 0;
        if (arr != null) {
            try {
                ParseObject.fetchAllIfNeeded(arr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < arr.size(); i++) {
                Goal goal = (Goal) arr.get(i);
                if (goal.getCompleted()) {
                    completedGoals += 1;
                } else {
                    progressGoals += 1;
                }
            }
            tvProgress.setText(String.valueOf(progressGoals));
            tvCompleted.setText(String.valueOf(completedGoals));
            tvFriends.setText(String.valueOf(user.getList("friends").size()));
            tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        }
        Util.setImage(user, "image", getResources(), ivProfile, 16.0f);
    }

    public void addFriend(View v) {
        Intent i = new Intent(this, SearchFriendsActivity.class);
        startActivityForResult(i, ADD_FRIEND_ACTIVITY_REQUEST_CODE);
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void toCamera() {
        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
        if (requestCode == ADD_FRIEND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ParseUser friend = data.getParcelableExtra(ParseUser.class.getSimpleName());
                friends.add(0, friend);
                friendAdapter.notifyItemInserted(0);
                rvFriends.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void selectImage(View v) {
//        final CharSequence[] items = { "Take Photo", "Choose from Library",
//                "Cancel" };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//
//                if (items[item].equals("Take Photo")) {
//                    onLaunchCamera();
//
//                } else if (items[item].equals("Choose from Library")) {
//                    onLaunchGallery();
//
//                } else if (items[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
    }
//
//    public void onLaunchCamera() {
//        // create Intent to take a picture and return control to the calling application
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Create a File reference to access to future access
//        photoFile = getPhotoFileUri(photoFileName);
//
//        // wrap File object into a content provider
//        // required for API >= 24
//        Uri fileProvider = FileProvider.getUriForFile(this, "com.fbu.fileprovider", photoFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
//        try {
//            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//            // So as long as the result is not null, it's safe to use the intent.
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                if (Build.VERSION.SDK_INT >= 23) {
//                    int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
//                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//                    }
//                }
//                // Start the image capture intent to take photo
//                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//            }
//        } catch(SecurityException e) {
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                if (Build.VERSION.SDK_INT >= 23) {
//                    int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
//                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//                    }
//                }
//                // Start the image capture intent to take photo
//                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//            }
//        }
//    }
//
//    public void onLaunchGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            // Start the image capture intent to take photo
//            startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
//        }
//    }
//
//    // Returns the File for a photo stored on disk given the fileName
//    public File getPhotoFileUri(String fileName) {
//        // Get safe storage directory for photos
//        // Use `getExternalFilesDir` on Context to access package-specific directories.
//        // This way, we don't need to request external read/write runtime permissions.
//        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Goals");
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
//            Log.d("Goals", "failed to create directory");
//        }
//
//        // Return the file target for the photo based on filename
//        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
//
//        return file;
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
//            startActivity(i);
//            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
}
