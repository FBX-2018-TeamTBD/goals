package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddGoalActivity extends AppCompatActivity {

    /*enum Frequency {
        DAILY(R.id.rbDay),
        WEEKLY(R.id.rbWeek),
        MONTHLY(R.id.rbMonth);

        private int value;

        private Frequency(int val) {
            value = val;
        }
    }*/

    @BindView(R.id.etTitle) EditText etTitle;
    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.etDuration) EditText etDuration;

    Date currentDate;
    int frequency = R.integer.FREQUENCY_DAILY;

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ButterKnife.bind(this);

        user = ParseUser.getCurrentUser();
        Log.i("sdf", ""+user.getACL().getPublicReadAccess());

        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        etDuration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
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

        hideKeyboard(v);
    }

    public void goBack(View v) {
        finish();
    }

    public void postGoal(View v) {
        try {
            long sum = currentDate.getTime() + TimeUnit.DAYS.toMillis(frequency);
            Date updateBy = new Date(sum);
            final Goal goal = new Goal(etTitle.getText().toString(), etDescription.getText().toString(), Integer.parseInt(etDuration.getText().toString()), frequency, 0, 0, new ArrayList<ParseObject>(), ParseUser.getCurrentUser(), false, updateBy);
            List<ParseObject> goals = user.getList("goals");
            goals.add(goal);
            user.put("goals", goals);
            ParseACL acl = user.getACL();
            if (!acl.getPublicReadAccess()) {
                acl.setPublicReadAccess(true);
                user.setACL(acl);
            }
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        try {
                            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                            int timeRunningOutHours = getApplicationContext().getResources().getInteger(R.integer.TIME_RUNNING_OUT_HOURS);
                            long delay = TimeUnit.DAYS.toMillis(goal.getFrequency()) - TimeUnit.HOURS.toMillis(timeRunningOutHours);
                            notificationHelper.sendNotification(goal.getObjectId(), goal.getTitle(), goal.getDescription(), goal.getIntId(), delay);
                            user.fetch();
                            Intent data = new Intent();
                            data.putExtra(Goal.class.getSimpleName(), goal);
                            setResult(RESULT_OK, data);
                            finish();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Log.i("Profile Activity", "Failed to update object, with error code: " + e.toString());
                    }
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Make sure to fill out each field!", Toast.LENGTH_LONG).show();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
