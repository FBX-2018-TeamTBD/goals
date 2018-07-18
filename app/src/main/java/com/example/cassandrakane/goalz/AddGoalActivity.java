package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

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

    int frequency = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        ButterKnife.bind(this);

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

    }

    public void onRadioButtonClicked(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.rbDay:
                if (checked)
                    frequency = 1;
                    break;
            case R.id.rbWeek:
                if (checked)
                    frequency = 2;
                    break;
            case R.id.rbMonth:
                if (checked)
                    frequency = 3;
                    break;
        }

        hideKeyboard(v);
    }

    public void goBack(View v) {
        finish();
    }

    public void postGoal(View v) {
        final Goal goal = new Goal(etTitle.getText().toString(), etDescription.getText().toString(), Integer.parseInt(etDuration.getText().toString()), frequency, 0, 0, new ArrayList<ParseObject>(), ParseUser.getCurrentUser());
        final ParseUser user = ParseUser.getCurrentUser();
        List<ParseObject> goals = user.getList("goals");
        goals.add(goal);
        user.put("goals", goals);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        user.fetch();
                        Intent data  = new Intent();
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
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
