package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.ReactionAdapter;
import com.parse.ParseObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReactionModalActivity extends Activity {

    @BindView(R.id.rvReactions) RecyclerView rvReactions;
    @BindView(R.id.tvThumbCount) TextView tvThumbCount;
    @BindView(R.id.tvGoalsCount) TextView tvGoalsCount;
    @BindView(R.id.tvClapCount) TextView tvClapCount;
    @BindView(R.id.tvOkCount) TextView tvOkCount;
    @BindView(R.id.tvBumpCount) TextView tvBumpCount;
    @BindView(R.id.tvRockCount) TextView tvRockCount;
    ReactionAdapter reactionAdapter;
    List<ParseObject> reactions;
    List<Integer> reactionCounts;

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
        reactionCounts = (List) getIntent().getSerializableExtra("reactionCounts");

        if (reactions != null) {
            reactionAdapter = new ReactionAdapter(reactions);
            rvReactions.setLayoutManager(new LinearLayoutManager(this));
            rvReactions.setAdapter(reactionAdapter);
        }

        if (reactionCounts != null){
            tvThumbCount.setText(Integer.toString(reactionCounts.get(0)));
            tvGoalsCount.setText(Integer.toString(reactionCounts.get(1)));
            tvClapCount.setText(Integer.toString(reactionCounts.get(2)));
            tvOkCount.setText(Integer.toString(reactionCounts.get(3)));
            tvBumpCount.setText(Integer.toString(reactionCounts.get(4)));
            tvRockCount.setText(Integer.toString(reactionCounts.get(5)));
        }
    }

    public void goBack (View v){
        finish();
    }
}
