package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.DataFetcher;
import utils.Util;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private final List<ParseUser> friends;
    Context context;
    StoryAdapter storyAdapter;
    List<Goal> goals;
    List<Goal> friendGoals;
    DataFetcher dataFetcher;

    public FriendAdapter(List<ParseUser> friends) {
        this.friends = friends;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_friend, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        final ParseUser friend = friends.get(position);

        try {
            holder.tvUsername.setText(friend.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseFile file = (ParseFile) friend.get("image");
        Util.setImage(friend, file, context.getResources(), holder.ivProfile, 16.0f);
        holder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toFriendActivity(friend);
            }
        });
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toFriendActivity(friend);
            }
        });

        goals = new ArrayList<Goal>();
        List<Goal> lGoals = friend.getList("goals");
        if (lGoals != null) {
            goals.addAll(lGoals);
        }
        storyAdapter = new StoryAdapter(goals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.rvStory.setLayoutManager(layoutManager);
        holder.rvStory.setAdapter(storyAdapter);
        holder.rvStory.setVisibility(View.GONE);

        if (goals.size() != 0) {
            holder.ivMenu.setVisibility(View.VISIBLE);
            holder.ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.rvStory.isShown()){
                        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_counterclockwise_180);
                        holder.ivMenu.startAnimation(rotateAnimation);
                        rotateAnimation.setFillAfter(true);
                        holder.rvStory.setVisibility(View.GONE);
                    } else {
                        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise_180);
                        holder.ivMenu.startAnimation(rotateAnimation);
                        rotateAnimation.setFillAfter(true);
                        holder.rvStory.setVisibility(View.INVISIBLE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.rvStory.setVisibility(View.VISIBLE);
                                holder.rvStory.startAnimation(AnimationUtils.loadAnimation(context, R.anim.menu_slide_up));
                            }
                        }, 100);
                    }
                }
            });
        } else {
            holder.ivMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void toFriendActivity(ParseUser friend) {
        Intent i = new Intent(context, FriendActivity.class);
        i.putExtra(ParseUser.class.getSimpleName(), friend);
        context.startActivity(i);
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.rvStory) RecyclerView rvStory;
        @BindView(R.id.ivMenu) ImageView ivMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void getFriendGoals(ParseUser friend){
        List<ParseObject> arr = new ArrayList<>();
        friendGoals = new ArrayList<>();
        arr = friend.getList("goals");
        for(int i = 0; i < arr.size(); i++) {
            Goal goal = null;
            try {
                goal = arr.get(i).fetch();
            } catch(ParseException e) {
                e.printStackTrace();
            }
            if (goal.getCompleted()) {
                friendGoals.add(goal);
            } else {
                friendGoals.add(0, goal);
            }
        }

        ParseObject.pinAllInBackground("friendGoals", friendGoals);
    }
}
