package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.MainPagerAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.NavigationHelper;
import utils.Util;

public class MainActivity extends AppCompatActivity {

    public MainPagerAdapter pagerAdapter;
    ImageView ivProfile;
    public TextView tvProgress;
    public TextView tvCompleted;
    public TextView tvFriends;
    TextView tvUsername;
    List<Goal> goals;
    List<Goal> completed;
    List<Goal> incompleted;
    ParseUser user;
    public int completedGoals = 0;
    public int progressGoals = 0;

    @BindView(R.id.nav_view) public NavigationView navigationView;
    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.pager) public ViewPager viewPager;
    @BindView(R.id.toolbar) public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        // start on profile
        viewPager.setCurrentItem(1);

        final NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                navigationHelper.toCamera(true);
                                break;
                            case R.id.nav_goals:
                                navigationHelper.toGoals(true);
                                break;
                            case R.id.nav_feed:
                                navigationHelper.toFeed(true);
                                break;
                            case R.id.nav_notifications:
                                navigationHelper.toNotifications(true);
                                break;
                            case R.id.nav_logout:
                                navigationHelper.logout();
                                break;
                        }

                        return true;
                    }
                });

        ivProfile = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);
        tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvFriends = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvFriends);
        tvProgress = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvProgress);
        tvCompleted = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvCompleted);
        goals = new ArrayList<>();
        completed = new ArrayList<>();
        incompleted = new ArrayList<>();

        user = ParseUser.getCurrentUser();
        Util.populateGoals(this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, incompleted);
        ParseFile file = (ParseFile) user.get("image");
        Util.setImage(user, file, getResources(), ivProfile, 16.0f);
        Util.setNotifications(user, navigationView);
    }

    public void refreshAsync(SwipeRefreshLayout swipeContainer) {
        Util.populateGoalsAsync(MainActivity.this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, completed, swipeContainer);
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
