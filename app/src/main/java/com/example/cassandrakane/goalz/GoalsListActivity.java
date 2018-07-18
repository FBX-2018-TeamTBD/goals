package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cassandrakane.goalz.adapters.GoalSimpleAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsListActivity extends AppCompatActivity {

    List<Goal> goals;
    GoalSimpleAdapter goalSimpleAdapter;
    File file;

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.ivStory) ImageView ivStory;
    @BindView(R.id.tvTitle) ImageView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        file = (File) getIntent().getSerializableExtra("image");
        goals = (List) getIntent().getSerializableExtra("goals");

        goalSimpleAdapter = new GoalSimpleAdapter(goals);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalSimpleAdapter);

    }

    public void addImage(View v) {
        String text = "";
        final ParseFile parseFile = new ParseFile(file);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("GoalsListActivity", "ParseFile has been saved");
                } else{
                    e.printStackTrace();
                }
            }
        });
        for (Goal goal : goals){
            if (goal.isSelected()){
                text += goal.getTitle() + " ";
                ArrayList<ParseFile> story = goal.getStory();
                story.add(parseFile);
                goal.setStory(story);
                goal.saveInBackground();
            }
        }
        Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
        startActivity(intent);
        Log.d("GoalsListActivity", "Output : " + text);
    }
}
