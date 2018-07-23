package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.FriendRequestsActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.SearchFriendsActivity;
import com.example.cassandrakane.goalz.models.ApprovedFriendRequests;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<ParseUser> mFriends;
    private List<SentFriendRequests> requests;
    Context context;

    public FriendRequestAdapter(List<ParseUser> friends, List<SentFriendRequests> requests) {
        this.mFriends = friends;
        this.requests = requests;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_friend_request, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // get the data according to position
        final ParseUser friend = mFriends.get(position);

        try {
            holder.tvUsername.setText(friend.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Util.setImage(friend, "image", context.getResources(), holder.ivProfile, 16.0f);
        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend(friend, position);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSentRequest(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public void deleteSentRequest(final int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SentFriendRequests");
        query.whereEqualTo("objectId", requests.get(position).getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    object.delete();
                    object.saveInBackground();
                    requests.remove(position);
                    mFriends.remove(position);
                    if (requests.size() > 0) {
                        ((FriendRequestsActivity) context).noFriendsPage.setVisibility(View.GONE);
                    } else {
                        ((FriendRequestsActivity) context).noFriendsPage.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void addFriend(final ParseUser user, final int position) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList("friends");
        friends.add(0, user);
        currentUser.put("friends", friends);
        ApprovedFriendRequests request = new ApprovedFriendRequests(user, currentUser);
        request.saveInBackground();
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        currentUser.fetch();
                        ((FriendRequestsActivity) context).tvFriends.setText(String.valueOf(Integer.parseInt(((FriendRequestsActivity) context).tvFriends.getText().toString()) + 1));
                        deleteSentRequest(position);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Search Friend Activity", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.btnConfirm) Button btnConfirm;
        @BindView(R.id.btnDelete) Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
