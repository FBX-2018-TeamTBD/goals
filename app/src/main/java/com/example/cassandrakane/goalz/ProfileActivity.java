package com.example.cassandrakane.goalz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.content.Intent;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

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


        /* STORY FRAGMENT EXAMPLE
        ArrayList<String> testImages = new ArrayList<String>();
        testImages.add("https://picsum.photos/500/150/?image=392");
        testImages.add("https://picsum.photos/23/150/?image=393");
        testImages.add("https://picsum.photos/532/150/?image=397");
        testImages.add("https://picsum.photos/76/150/?image=395");
        testImages.add("https://picsum.photos/700/150/?image=396");

        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
        fragTransStory.add(R.id.root_layout, StoryFragment.newInstance(testImages, 0)).commit();
        */
    }
}
