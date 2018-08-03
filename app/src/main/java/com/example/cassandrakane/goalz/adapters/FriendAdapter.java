package com.example.cassandrakane.goalz.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.DataFetcher;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ParseUser> friends;
    private final List<ParseUser> suggestedFriends;
    Context context;
    StoryAdapter storyAdapter;
    List<Goal> goals;
    List<Goal> friendGoals;
    List<ParseUser> selectedFriends;
    DataFetcher dataFetcher;

    public FriendAdapter(List<ParseUser> friends, List<ParseUser> suggestedFriends) {
        this.friends = friends;
        this.suggestedFriends = suggestedFriends;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        selectedFriends = new ArrayList<>();

        switch (viewType) {
            case 0: return new ViewHolder(inflater.inflate(R.layout.item_friend, parent, false));
            case 1: return new NoFriendViewHolder(inflater.inflate(R.layout.item_friend, parent, false));
            default: return null;
        }

    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        // get the data according to position

        switch (holder.getItemViewType()) {
            case 0:
                final ParseUser friend = friends.get(position);
                ViewHolder viewHolder = (ViewHolder) holder;
                try {
                    viewHolder.tvUsername.setText(friend.fetchIfNeeded().getUsername());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final ParseFile file = (ParseFile) friend.get("image");
                Util.setImage(file, context.getResources(), viewHolder.ivProfile, 27.0f);
                viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toFriendActivity(friend);
                    }
                });
                viewHolder.tvUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toFriendActivity(friend);
                    }
                });
                break;
            case 1:
                final ParseUser friend1 = suggestedFriends.get(position);
                final NoFriendViewHolder viewHolder1 = (NoFriendViewHolder) holder;
                try {
                    viewHolder1.tvUsername.setText(friend1.fetchIfNeeded().getUsername());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseFile file1 = (ParseFile) friend1.get("image");
                Util.setImage(file1, context.getResources(), viewHolder1.ivProfile, 27.0f);
                viewHolder1.ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position != RecyclerView.NO_POSITION){
//                            ParseUser friend = sfriends.get(position);
                            if (!selectedFriends.contains(friend1)) {
                                selectedFriends.add(friend1);
                                enterReveal(viewHolder1.ivCheck);
                            } else {
                                selectedFriends.remove(friend1);
                                exitReveal(viewHolder1.ivCheck);
                            }
                        }
                    }
                });
                break;
            }

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (position >= friends.size()) {
            return 1;
        }
        return 0;
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class NoFriendViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.rvStory) RecyclerView rvStory;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public NoFriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    void enterReveal(ImageView view) {
        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    void exitReveal(final ImageView view) {
        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = view.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
}
