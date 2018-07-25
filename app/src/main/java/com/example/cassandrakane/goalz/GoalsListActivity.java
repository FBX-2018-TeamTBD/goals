package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.GoalSimpleAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.example.cassandrakane.goalz.models.Video;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsListActivity extends AppCompatActivity {

    List<Goal> goals;
    GoalSimpleAdapter goalSimpleAdapter;
    File file;
    ArrayList<File> videos;
    Date currentDate;

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.noGoals) RelativeLayout noGoals;
    @BindView(R.id.btnConfirm) Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        currentDate = new Date();
        ButterKnife.bind(this);

        file = (File) getIntent().getSerializableExtra("image");
        goals = (List) getIntent().getSerializableExtra("goals");
        videos = (ArrayList) getIntent().getSerializableExtra("videos");

        if (goals != null && goals.size() != 0) {
            goalSimpleAdapter = new GoalSimpleAdapter(goals);
            rvGoals.setLayoutManager(new LinearLayoutManager(this));
            rvGoals.setAdapter(goalSimpleAdapter);
        } else {
            noGoals.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.GONE);
        }
    }

    public void addImage(View v) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (file != null) {
            final ParseFile parseFile = new ParseFile(file);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("GoalsListActivity", "ParseFile has been saved");
                    } else {
                        e.printStackTrace();
                    }
                }

            });
            int selected = 0;
            for (final Goal goal : goals) {
                if (goal.isSelected()) {
                    selected += 1;
                    final ArrayList<ParseObject> story = goal.getStory();
                    final Image image = new Image(parseFile, "");
                    image.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            story.add(image);
                            goal.setStory(story);
                            goal.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                                    notificationHelper.cancelReminder(goal);
                                    notificationHelper.setReminder(goal);
                                    if (goal.getStory().size() == 1) {
                                        goal.setProgress(1);
                                        goal.setStreak(1);
                                        goal.setItemAdded(false);
                                        goal.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        if (!goal.getIsItemAdded()) {
                                            goal.setProgress(goal.getProgress() + 1);
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
            if (selected == 0) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please select a goal to add to.", Toast.LENGTH_LONG).show();
            }
        } else if (videos != null){
            for (File video : videos){
                ParseFile parseFile = new ParseFile(video);
                parseFile.saveInBackground();
                for (final Goal goal : goals) {
                    if (goal.isSelected()) {
                        final ArrayList<ParseObject> story = goal.getStory();
                        final Video videoFile = new Video(parseFile, "");
                        videoFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Intent intent = new Intent(GoalsListActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }
    }

    public void addGoal(View v) {
        Intent i = new Intent(this, AddGoalActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    public void goBack(View v) {
        finish();
    }
}
