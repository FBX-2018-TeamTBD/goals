package com.example.cassandrakane.goalz;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.example.cassandrakane.goalz.utils.EventBus;
import com.example.cassandrakane.goalz.utils.VerticalPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // Start page index. 0 - top page, 1 - central page, 2 - bottom page.
    private static final int CENTRAL_PAGE_INDEX = 1;

    @BindView(R.id.activity_main_vertical_pager) VerticalPager verticalPager;
    public CentralFragment centralFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        centralFragment = (CentralFragment) getSupportFragmentManager().findFragmentById(R.id.main_central_fragment);

        snapPageWhenLayoutIsReady(verticalPager, CENTRAL_PAGE_INDEX);
    }

    private void snapPageWhenLayoutIsReady(final View pageView, final int page) {
        /*
         * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
         * layout and measure all its pages.
         */
        pageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                verticalPager.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    // recommended removeOnGlobalLayoutListener method is available since API 16 only
                    pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    removeGlobalOnLayoutListenerForJellyBean(pageView);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void addGoal(View v) {
        Intent i = new Intent(this, AddGoalActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    public void addFriend(View v) {
        Intent i = new Intent(this, SearchFriendsActivity.class);
        i.putExtra("requestActivity", this.getClass().getSimpleName());
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }
}
