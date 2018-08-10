package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.ShareFriendAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.example.cassandrakane.goalz.utils.NotificationHelper;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddGoalActivity extends AppCompatActivity {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.etTitle) EditText etTitle;
    @BindView(R.id.rbDay) RadioButton rbDay;
    @BindView(R.id.rbWeek) RadioButton rbWeek;
    @BindView(R.id.rbMonth) RadioButton rbMonth;
    @BindView(R.id.sbDuration) SeekBar sbDuration;
    @BindView(R.id.tvDuration) TextView tvDuration;
    @BindView(R.id.rvShareFriends) RecyclerView rvShareFriends;
    @BindView(R.id.tvShare) TextView tvShare;

    Date currentDate;
    int frequency;
    List<ParseUser> selectedFriends = new ArrayList<>();
    List<ParseUser> shareFriends = new ArrayList<>();
    ShareFriendAdapter shareFriendAdapter;

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_goal);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);

        user = ParseUser.getCurrentUser();

        shareFriends = user.getList("friends");

        shareFriendAdapter = new ShareFriendAdapter(shareFriends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvShareFriends.setLayoutManager(layoutManager);
        rvShareFriends.setAdapter(shareFriendAdapter);

        if (shareFriends.size() == 0) {
            tvShare.setVisibility(View.GONE);
            rvShareFriends.setVisibility(View.GONE);
        } else {
            tvShare.setVisibility(View.VISIBLE);
            rvShareFriends.setVisibility(View.VISIBLE);
        }

        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(v, AddGoalActivity.this);
                }
            }
        });

        sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvDuration.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        currentDate = new Date();

    }

    public void onRadioButtonClicked(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.rbDay:
                if (checked)
                    frequency = getResources().getInteger(R.integer.FREQUENCY_DAILY);
                    break;
            case R.id.rbWeek:
                if (checked)
                    frequency = getResources().getInteger(R.integer.FREQUENCY_WEEKLY);
                    break;
            case R.id.rbMonth:
                if (checked)
                    frequency = getResources().getInteger(R.integer.FREQUENCY_MONTHLY);
                    break;
        }

        Util.hideKeyboard(v, this);
    }

    public void goBack(View v) {
        finish();
    }

    public void postGoal(View v) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            long sum = currentDate.getTime() + TimeUnit.DAYS.toMillis(frequency);
            Date updateBy = new Date(sum);
            List<ParseUser> pendingFriends = new ArrayList<>();
            List<ParseObject> allReactions = new ArrayList<>();
            selectedFriends = shareFriendAdapter.selectedFriends;

            Map<String, String> usersAdded = new HashMap<String, String>(){{
                put(user.getObjectId(), "false");
            }};

            pendingFriends.addAll(selectedFriends);
            selectedFriends.add(ParseUser.getCurrentUser());
            List<ParseUser> approved = new ArrayList<>();
            approved.add(ParseUser.getCurrentUser());
            Goal goal = new Goal(etTitle.getText().toString(),
                    sbDuration.getProgress(), frequency, 0, 0,
                    new ArrayList<ParseObject>(), ParseUser.getCurrentUser(), false, updateBy,
                    selectedFriends, approved, pendingFriends, usersAdded, allReactions);

            List<ParseObject> goals = user.getList("goals");
            sendGoalRequest(goal, pendingFriends);
            goals.add(0, goal);
            goal.pinInBackground();
            user.put("goals", goals);

            ParseACL acl = user.getACL();
            if (!acl.getPublicWriteAccess()) {
                acl.setPublicWriteAccess(true);
                user.setACL(acl);
            }
            final Goal finalGoal = goal;
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                        notificationHelper.setReminder(finalGoal);
                        Intent data = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(data);
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    } else {
                        Log.i("Profile Activity", "Failed to update object, with error code: " + e.toString());
                    }
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Make sure to fill out each field!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void sendGoalRequest(Goal goal, List<ParseUser> pending) {
        for (int i = 0; i < pending.size(); i++) {
            GoalRequests request = new GoalRequests(pending.get(i), ParseUser.getCurrentUser(), (Goal) goal);
            request.saveInBackground();
        }
    }

    public void autofill(View v) {
        etTitle.setText("spread positivity");
        etTitle.setSelection(17);
        sbDuration.setProgress(16);
        rbDay.toggle();
    }

}
