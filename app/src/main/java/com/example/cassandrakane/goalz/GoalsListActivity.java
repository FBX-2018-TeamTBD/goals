package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.cassandrakane.goalz.models.Reaction;
import com.example.cassandrakane.goalz.models.Video;
import com.example.cassandrakane.goalz.utils.NotificationHelper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalsListActivity extends AppCompatActivity {

    List<Goal> goals;
    ArrayList<ParseObject> parseVideos;
    GoalSimpleAdapter goalSimpleAdapter;
    File file;
    ArrayList<File> videos;
    Date currentDate;
    String caption;
    ParseUser currentUser;
    Goal selectedGoal;

    private int mTasksComplete = 0;
    private int mTasksRequired;
    private boolean allAdded = true;

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.noGoals) RelativeLayout noGoals;
    @BindView(R.id.btnConfirm) Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_goals_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        currentDate = new Date();
        currentUser = ParseUser.getCurrentUser();
        ButterKnife.bind(this);

        goals = new ArrayList<Goal>();
        List<Goal> allGoals = ParseUser.getCurrentUser().getList("goals");
        for (Goal goal : allGoals){
            if (!goal.getCompleted()){
                goals.add(goal);
            }
        }

        Collections.reverse(goals);

        file = (File) getIntent().getSerializableExtra("image");
        videos = (ArrayList) getIntent().getSerializableExtra("videos");
        caption = getIntent().getStringExtra("caption");

        parseVideos = new ArrayList<>();

        if (goals.size() != 0) {
            goalSimpleAdapter = new GoalSimpleAdapter(goals);
            rvGoals.setLayoutManager(new GridLayoutManager(this, 3));
            rvGoals.setAdapter(goalSimpleAdapter);
        } else {
            noGoals.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.GONE);
        }
        mTasksRequired = videos != null ? videos.size() : 0;
    }

    public void addImage(View v) {
        btnConfirm.setOnClickListener(null);
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
                    final List<ParseObject> story = goal.getStory();
                    List<Reaction> reactions = new ArrayList<>();
                    final Image image = new Image(parseFile, caption, ParseUser.getCurrentUser(),
                            new ArrayList<ParseUser>(), reactions);
                    selectedGoal = goal;
                    image.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            story.add(image);
                            goal.setStory(story);

                            Map<String, String> userAdded = goal.getUserAdded();
                            if (userAdded == null){
                                userAdded = new HashMap<>();
                            }
                            userAdded.put(currentUser.getObjectId(), "true");
                            for (String value : userAdded.values()) {
                                if (value.equals("false")) {
                                    allAdded = false;
                                }
                            }

                            goal.setUserAdded(userAdded);
                            goal.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                                    notificationHelper.cancelReminder(goal);
                                    notificationHelper.setReminder(goal);

                                    if (!goal.getItemAdded()) {
                                        if (allAdded) {
                                            goal.setItemAdded(true);
                                            goal.setProgress(goal.getProgress() + 1);
                                            if (currentDate.getTime() <= goal.getUpdateStoryBy().getTime()) {
                                                goal.setStreak(goal.getStreak() + 1);
                                                goal.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        toMain();
                                                    }
                                                });
                                            }
                                        } else{
                                            toMain();
                                        }
                                    } else{
                                        toMain();
                                    }
                                }
                            });
                        }
                    });
                }
            }
            if (selected == 0) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please select a goal.", Toast.LENGTH_LONG).show();
            }
        } else if (videos != null){
            for (final File video : videos){
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] imageByte = byteArrayOutputStream.toByteArray();
                final ParseFile parseFileThumbnail = new ParseFile("image_file.png",imageByte);
                parseFileThumbnail.saveInBackground();
                final ParseFile parseFile = new ParseFile(video);
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        List<Reaction> reactions = new ArrayList<>();
                        final Video videoFile = new Video(parseFile, caption, parseFileThumbnail,
                                ParseUser.getCurrentUser(), new ArrayList<ParseUser>(), reactions);
                        parseVideos.add(videoFile);
                        videoFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                mTasksComplete++;
                                if (mTasksComplete == mTasksRequired) {
                                    addToGoal(parseVideos);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    public void addGoal(View v) {
        Intent i = new Intent(this, AddGoalActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    public void goBack(View v) {
        finish();
    }

    public void addToGoal(ArrayList<ParseObject> parseVideos){
        int selected = 0;
        for (final Goal goal : goals) {
            selected += 1;
            if (goal.isSelected()) {
                final List<ParseObject> story = goal.getStory();
                Collections.reverse(parseVideos);
                    for (ParseObject video: parseVideos) {
                        story.add(video);
                    }
                        goal.setStory(story);
                        Map<String, String> userAdded = goal.getUserAdded();
                        if (userAdded == null){
                            userAdded = new HashMap<>();
                        }
                        userAdded.put(currentUser.getObjectId(), "true");
                        for (String value : userAdded.values()) {
                            if (value.equals("false")) {
                                allAdded = false;
                            }
                        }

                        goal.setUserAdded(userAdded);
                        goal.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                                notificationHelper.cancelReminder(goal);
                                notificationHelper.setReminder(goal);

                                if (!goal.getItemAdded()) {
                                    if (allAdded) {
                                        goal.setItemAdded(true);
                                        goal.setProgress(goal.getProgress() + 1);
                                        if (currentDate.getTime() <= goal.getUpdateStoryBy().getTime()) {
                                            goal.setStreak(goal.getStreak() + 1);
                                            goal.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    toMain();
                                                }
                                            });
                                        }
                                    } else{
                                        toMain();
                                    }
                                } else{
                                    toMain();
                                }
                            }
                });
            }
        }
        if (selected == 0) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void toMain() {
        Intent intent = new Intent(GoalsListActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
