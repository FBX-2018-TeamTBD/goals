package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class AddGoalActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etDescription;
    EditText etDuration;
    int frequency = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDuration = findViewById(R.id.etDuration);

        getSupportActionBar().hide();
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
    }

    public void goBack(View v) {
        finish();
    }

    public void postGoal(View v) {
        final Goal goal = new Goal(etTitle.getText().toString(), etDescription.getText().toString(), Integer.parseInt(etDuration.getText().toString()), frequency, 0, 0, new ArrayList<ParseFile>(), ParseUser.getCurrentUser());
        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("sdf", "son");
                    Intent data  = new Intent();
                    data.putExtra(Goal.class.getSimpleName(), goal);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    e.printStackTrace();
                    Log.i("sdf", "bad");
                }
            }
        });
    }
}
