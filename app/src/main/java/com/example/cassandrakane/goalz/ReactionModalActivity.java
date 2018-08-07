package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.cassandrakane.goalz.adapters.ReactionAdapter;
import com.parse.ParseObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReactionModalActivity extends Activity {

    @BindView(R.id.rvReactions) RecyclerView rvReactions;
    ReactionAdapter reactionAdapter;
    List<ParseObject> reactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_reaction_modal);

        ButterKnife.bind(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) (dm.widthPixels * 0.75);
        int height = (int) (dm.heightPixels * 0.65);

        getWindow().setLayout(width, height);

        reactions = (List) getIntent().getSerializableExtra("reactions");

        if (reactions != null) {
            reactionAdapter = new ReactionAdapter(reactions);
            rvReactions.setLayoutManager(new LinearLayoutManager(this));
            rvReactions.setAdapter(reactionAdapter);
        }
//        goalFriendAdapter = new GoalFriendAdapter(goalFriends);
//        rvGoalFriends.setLayoutManager(new LinearLayoutManager(this));
//        rvGoalFriends.setAdapter(goalFriendAdapter);
//        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
//        rvGoalFriends.addItemDecoration(itemDecor);
//
//        if (!personal) {
//            btnAdd.setVisibility(View.GONE);
//        }
//    }

    }

    public void goBack (View v){
        finish();
    }
}
