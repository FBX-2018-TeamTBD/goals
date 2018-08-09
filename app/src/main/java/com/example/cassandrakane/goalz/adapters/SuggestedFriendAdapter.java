package com.example.cassandrakane.goalz.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.utils.AnimationHelper;
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
    private List<ParseUser> selectedFriends;

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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
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
                    AnimationHelper.enterReveal(viewHolder.ivCheck);
                    selectedFriends.add(friend);
                } else {
                    AnimationHelper.exitReveal(viewHolder.ivCheck);
                    selectedFriends.remove(friend);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestedFriends.size();
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
}
