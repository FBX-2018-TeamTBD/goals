package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cassandrakane.goalz.models.Goal;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CelebrateActivity extends AppCompatActivity {

    @BindView(R.id.ivCelebrate) ImageView ivCelebrate;
    @BindView(R.id.tvCompleteMessage) TextView tvCompleteMessage;
    @BindView(R.id.tvGoalTitle) TextView tvGoalTitle;
    @BindView(R.id.btnConfirm) Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebrate);

        ButterKnife.bind(this);

        Goal goal = Parcels.unwrap(getIntent().getParcelableExtra(Goal.class.getSimpleName()));

        Glide.with(this).load("https://thumbs.gfycat.com/FavoriteJitteryApe-max-1mb.gif").into(ivCelebrate);
        tvGoalTitle.setText(goal.getString("title"));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CelebrateActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
