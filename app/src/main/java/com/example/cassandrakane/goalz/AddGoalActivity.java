package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.models.AddGoalForm;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

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

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.etTitle) EditText etTitle;
    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.etDuration) EditText etDuration;
    @BindView(R.id.rbDay) RadioButton rbDay;
    @BindView(R.id.rbWeek) RadioButton rbWeek;
    @BindView(R.id.rbMonth) RadioButton rbMonth;
    @BindView(R.id.btnShare) Button btnShare;
    @BindView(R.id.tvShareFriends) TextView tvShareFriends;

    Date currentDate;
    int frequency;
    List<ParseUser> selectedFriends = new ArrayList<ParseUser>();

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);

        AddGoalForm form = Parcels.unwrap(getIntent().getParcelableExtra("form"));
        if (form != null) {
            etTitle.setText(form.getTitle());
            etDescription.setText(form.getDescription());
            etDuration.setText(form.getDuration());
            if (form.getFrequency() == getResources().getInteger(R.integer.FREQUENCY_DAILY)) {
                rbDay.setChecked(true);
            }
            if (form.getFrequency() == getResources().getInteger(R.integer.FREQUENCY_WEEKLY)) {
                rbWeek.setChecked(true);
            }
            if (form.getFrequency() == getResources().getInteger(R.integer.FREQUENCY_MONTHLY)) {
                rbMonth.setChecked(true);
            }
            frequency = form.getFrequency();
            selectedFriends = form.getSelectedFriends();
            tvShareFriends.setText(getSharedFriendsListString());
        }

        user = ParseUser.getCurrentUser();

        try {
            user = user.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i("sdf", "" + user.getACL().getPublicReadAccess());

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

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchFriendsActivity.class);
                AddGoalForm currentForm = getCurrentForm();
                i.putExtra("form", Parcels.wrap(currentForm));
                i.putExtra("requestActivity", AddGoalActivity.class.getSimpleName());
                startActivity(i);
                finish();
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

    public AddGoalForm getCurrentForm() {
        return new AddGoalForm(etTitle.getText().toString(), etDescription.getText().toString(), etDuration.getText().toString(), frequency, selectedFriends);
    }

    public String getSharedFriendsListString() {
        if (selectedFriends.size() > 0) {
            String str = "Shared with ";
            for (ParseUser friend : selectedFriends) {
                str = str + friend.getUsername() + ", ";
            }
            return str.substring(0, str.length() - 2);
        }
        return "";
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
            pendingFriends.addAll(selectedFriends);
            selectedFriends.add(ParseUser.getCurrentUser());
            List<ParseUser> approved = new ArrayList<>();
            approved.add(ParseUser.getCurrentUser());
            Goal goal = new Goal(etTitle.getText().toString(), etDescription.getText().toString(),
                    Integer.parseInt(etDuration.getText().toString()), frequency, 0, 0,
                    new ArrayList<ParseObject>(), ParseUser.getCurrentUser(), false, updateBy,
                    selectedFriends, pendingFriends, approved);
            List<ParseObject> goals = user.getList("goals");
            sendGoalRequest(goal, pendingFriends);
            goals.add(goal);
            goal.pinInBackground();
            user.put("goals", goals);

            ParseACL acl = user.getACL();
            if (!acl.getPublicReadAccess()) {
                acl.setPublicReadAccess(true);
                user.setACL(acl);
            }
            final Goal finalGoal = goal;
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        try {
                            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                            notificationHelper.setReminder(finalGoal);
                            user.fetch();
                            Intent data = new Intent();
                            data.putExtra(Goal.class.getSimpleName(), finalGoal);
                            setResult(RESULT_OK, data);
                            progressBar.setVisibility(View.GONE);
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

    public void sendGoalRequest(Goal goal, List<ParseUser> pending) {
        for (int i = 0; i < pending.size(); i++) {
            GoalRequests request = new GoalRequests(pending.get(i), (Goal) goal);
            request.saveInBackground();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
