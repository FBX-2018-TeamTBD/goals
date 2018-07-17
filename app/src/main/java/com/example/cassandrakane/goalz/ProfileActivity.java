package com.example.cassandrakane.goalz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvProgress;
    TextView tvCompleted;
    TextView tvFriends;
    ParseUser user;

    List<Goal> goals;
    RecyclerView rvGoals;
    GoalAdapter goalAdapter;

    int completedGoals = 0;
    int progressGoals = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        user = ParseUser.getCurrentUser();
        goals = new ArrayList<>();
        goalAdapter = new GoalAdapter(goals);
        rvGoals = findViewById(R.id.rvGoals);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalAdapter);

        ivProfile = findViewById(R.id.ivProfile);
        tvProgress = findViewById(R.id.tvProgress);
        tvCompleted = findViewById(R.id.tvCompleted);
        tvFriends = findViewById(R.id.tvFriends);

        tvFriends.setText(user.getList("friends").size() + "\nFriends");

        populateGoals();
    }

    public void populateGoals() {
        JSONArray arr = user.getJSONArray("goals");
        Log.i("sdf", arr.toString());
        goals.clear();
        for (int i = 0; i < arr.length(); i++) {
            try {
                Goal goal = (Goal) arr.get(i);
                Log.i("sdf", goal.getDescription());
                if (goal.getCompleted()) {
                    completedGoals += 1;
                } else {
                    progressGoals += 1;
                }
                goals.add(goal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        goalAdapter.notifyDataSetChanged();
        tvProgress.setText(progressGoals + " Current\nGoals");
        tvCompleted.setText(completedGoals + " Completed\nGoal");
    }
}
