package com.example.cassandrakane.goalz;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.NotificationsPagerAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends AppCompatActivity {

    public NotificationsPagerAdapter pagerAdapter;
    ImageView ivProfile;
    public TextView tvProgress;
    public TextView tvCompleted;
    public TextView tvFriends;
    TextView tvUsername;
    List<Goal> goals;
    List<Goal> incompleted;

    @BindView(R.id.nav_view) public NavigationView navigationView;
    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.pager) public ViewPager viewPager;
    @BindView(R.id.tablayout) TabLayout tabLayout;
    @BindView(R.id.toolbar) public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_notifications);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        progressBar.setVisibility(ProgressBar.VISIBLE);
        setSupportActionBar(toolbar);

        pagerAdapter = new NotificationsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

//        final NavigationHelper navigationHelper = new NavigationHelper(this);
//        navigationView = findViewById(R.id.nav_view);
//        navigationView.getMenu().getItem(3).setChecked(true);
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        // close drawer when item is tapped
//                        drawerLayout.closeDrawers();
//
//                        switch (menuItem.getItemId()) {
//                            case R.id.nav_camera:
//                                navigationHelper.toCamera();
//                                break;
//                            case R.id.nav_goals:
//                                navigationHelper.toGoals();
//                                break;
//                            case R.id.nav_feed:
//                                navigationHelper.toFeed();
//                                break;
//                            case R.id.nav_notifications:
//                                break;
//                            case R.id.nav_logout:
//                                navigationHelper.logout();
//                                break;
//                        }
//
//                        return true;
//                    }
//                });

        ivProfile = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);
        tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvFriends = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvFriends);
        tvProgress = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvProgress);
        tvCompleted = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvCompleted);
        goals = new ArrayList<>();
        incompleted = new ArrayList<>();

        Util.populateGoals(this, ParseUser.getCurrentUser(), tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, incompleted);
        Util.setNotifications(ParseUser.getCurrentUser());
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
