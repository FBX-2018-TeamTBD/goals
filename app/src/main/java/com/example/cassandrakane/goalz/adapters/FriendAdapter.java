package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.FeedActivity;
import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private final List<ParseUser> friends;
    Context context;
    StoryAdapter storyAdapter;
    List<Goal> goals;

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
        Util.setImage(friend, "image", context.getResources(), holder.ivProfile, 16.0f);
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
        holder.pokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You poked " + friend.getUsername() + "!", Toast.LENGTH_LONG).show();
                Log.i("Friend Added", "You poked " + friend.getUsername() + "!");
            }
        });

        goals = friend.getList("goals");
        if (goals == null){
            goals = new ArrayList<Goal>();
        }
        storyAdapter = new StoryAdapter(goals);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.rvStory.setLayoutManager(layoutManager);
        holder.rvStory.setAdapter(storyAdapter);
        holder.rvStory.setVisibility(View.GONE);

        final FeedActivity activity = (FeedActivity) context;

        if (goals.size() != 0) {
            holder.ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rvStory.setVisibility(holder.rvStory.isShown()
                            ? View.GONE
                            : View.VISIBLE);
//                    holder.ivMenu.setImageDrawable(holder.rvStory.isShown()
//                            ? );
                    activity.overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ParseUser> list) {
        friends.addAll(list);
        notifyDataSetChanged();
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
        @BindView(R.id.pokeBtn) Button pokeBtn;
        @BindView(R.id.rvStory) RecyclerView rvStory;
        @BindView(R.id.ivMenu) ImageView ivMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
