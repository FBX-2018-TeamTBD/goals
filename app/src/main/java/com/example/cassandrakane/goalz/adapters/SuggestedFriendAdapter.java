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
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SuggestedFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ParseUser> suggestedFriends;
    Context context;
    List<ParseUser> selectedFriends;

    public SuggestedFriendAdapter(List<ParseUser> suggestedFriends) {
        this.suggestedFriends = suggestedFriends;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        selectedFriends = new ArrayList<>();

        return new com.example.cassandrakane.goalz.adapters.SuggestedFriendAdapter.ViewHolder(inflater.inflate(R.layout.item_suggested_friend, parent, false));
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        // get the data according to position
        final ParseUser friend = suggestedFriends.get(position);

        final ViewHolder viewHolder = (ViewHolder) holder;

        try {
            viewHolder.tvUsername.setText(friend.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final ParseFile file = friend.getParseFile("image");
        Util.setImage(file, context.getResources(), viewHolder.ivProfile, R.color.orange);

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser friend = suggestedFriends.get(position);

                if (!selectedFriends.contains(friend)) {
                    enterReveal(viewHolder.ivCheck);
                    selectedFriends.add(friend);
                } else {
                    exitReveal(viewHolder.ivCheck);
                    selectedFriends.remove(friend);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestedFriends.size();
    }


    public void toFriendActivity(ParseUser friend) {
        Intent i = new Intent(context, FriendActivity.class);
        i.putExtra(ParseUser.class.getSimpleName(), friend);
        context.startActivity(i);
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

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
