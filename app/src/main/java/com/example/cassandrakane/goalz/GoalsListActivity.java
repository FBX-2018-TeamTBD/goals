package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.cassandrakane.goalz.adapters.GoalSimpleAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsListActivity extends AppCompatActivity {

    List<Goal> goals;
    GoalSimpleAdapter goalSimpleAdapter;
    File file;
    Date currentDate;
    Goal toGoal;

    @BindView(R.id.rvGoals) RecyclerView rvGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        currentDate = new Date();
        ButterKnife.bind(this);

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
        for (final Goal goal : goals){
            if (goal.isSelected()){
                text += goal.getTitle() + " ";
                final ArrayList<ParseObject> story = goal.getStory();
                final Image image = new Image(parseFile, "", goal);
                image.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        story.add(image);
                        goal.setStory(story);
                        goal.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (goal.getStory().size() == 1){
                                    Image lastUpdate = (Image) story.get(story.size() - 1);
                                    long sum = lastUpdate.getCreatedAt().getTime() + TimeUnit.DAYS.toMillis(goal.getFrequency());
                                    Date updateStoryBy = new Date(sum);
                                    goal.setUpdateStoryBy(updateStoryBy);
                                    goal.setItemAdded(false);
                                    goal.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
//                                    final Image lastImage = (Image) goal.getStory().get(goal.getStory().size() - 1);
                                    if (!goal.isItemAdded()) {
                                        if (currentDate.getTime() <= goal.getUpdateStoryBy().getTime()) {
                                            goal.setItemAdded(true);
                                            goal.setStreak(goal.getStreak() + 1);
                                            goal.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    } else {
                                        Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
        Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
