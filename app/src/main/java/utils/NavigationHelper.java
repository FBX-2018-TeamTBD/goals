package utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.cassandrakane.goalz.CameraActivity;
import com.example.cassandrakane.goalz.FeedActivity;
import com.example.cassandrakane.goalz.LoginActivity;
import com.example.cassandrakane.goalz.NotificationsActivity;
import com.example.cassandrakane.goalz.ProfileActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseUser;

import java.util.List;

public class NavigationHelper {

    Activity activity;

    public NavigationHelper(Activity act) {
        activity = act;
    }

    public void toCamera(boolean toRight) {
        Intent i = new Intent(activity, CameraActivity.class);
        activity.startActivity(i);
        animateTransition(toRight);
    }

    public void toGoals(boolean toRight) {
        Intent i = new Intent(activity, ProfileActivity.class);
        activity.startActivity(i);
        animateTransition(toRight);
    }

    public void toFeed(boolean toRight) {
        Intent i = new Intent(activity, FeedActivity.class);
        activity.startActivity(i);
        animateTransition(toRight);
    }

    public void toNotifications(boolean toRight) {
        Intent i = new Intent(activity, NotificationsActivity.class);
        activity.startActivity(i);
        animateTransition(toRight);
    }

    public void logout() {
        List<Goal> goals = ParseUser.getCurrentUser().getList("goals");
        NotificationHelper notificationHelper = new NotificationHelper(activity);
        if (goals != null) {
            for (Goal goal : goals) {
                notificationHelper.cancelReminder(goal);
            }
        }
        ParseUser.logOut();
        Toast.makeText(activity, "Successfully logged out.", Toast.LENGTH_LONG);
        Intent i = new Intent(activity, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        activity.finish();
    }

    public void animateTransition(boolean toRight) {
        if (toRight) {
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        } else {
            activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
    }
}
