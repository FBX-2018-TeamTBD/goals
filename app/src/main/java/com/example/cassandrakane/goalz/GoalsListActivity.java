package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.models.Goal;

import java.util.ArrayList;

public class GoalsListActivity extends AppCompatActivity {

    ArrayList<Goal> goals;
//    RecyclerView rvGoals;
    Goal firstGoal;
    ImageView ivStory;
    TextView tvGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_goal_simple);

        ivStory = findViewById(R.id.ivStory);
        tvGoal = findViewById(R.id.tvGoal);

        populateTimeline();
    }

    public void populateTimeline(){

    }
}
