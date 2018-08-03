package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalFriendAdapter extends RecyclerView.Adapter<GoalFriendAdapter.ViewHolder> {

    List<ParseUser> goalFriends;
    Context context;

    public GoalFriendAdapter(List<ParseUser> friends) {
        goalFriends = friends;
    }

    @NonNull
    @Override
    public GoalFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ParseUser user = goalFriends.get(position);
        holder.tvUsername.setText(user.getUsername());

        ParseFile image = (ParseFile) user.get("image");
        Util.setImage(image, context.getResources(), holder.ivProfile, R.color.orange);

        //set item click here
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FriendActivity.class);
                i.putExtra(ParseUser.class.getSimpleName(), user);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goalFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
