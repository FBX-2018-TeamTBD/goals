package com.example.cassandrakane.goalz.adapters;

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

public class ShareFriendAdapter extends RecyclerView.Adapter<ShareFriendAdapter.ViewHolder> {
    private List<ParseUser> friends;
    public List<ParseUser> selectedFriends;
    Context context;

    public ShareFriendAdapter(List<ParseUser> friends) {
        this.friends = friends;
        selectedFriends = new ArrayList<>();
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_share_friend, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        ParseUser friend = null;
        try {
            friend = friends.get(position).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile file = null;
        if (friend != null) {
            holder.tvUsername.setText(friend.getUsername());
            file = friend.getParseFile("image");
        }
        Util.setImage(file, context.getResources(), holder.ivProfile, R.color.orange);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivProfile.setTag(R.drawable.add);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                if (!ivProfile.getTag().equals(R.drawable.check)) {
                    ivProfile.setTag(R.drawable.check);
                    AnimationHelper.enterReveal(ivCheck);
                    selectedFriends.add(friends.get(position));
                } else {
                    ivProfile.setTag(R.drawable.add);
                    AnimationHelper.exitReveal(ivCheck);
                    selectedFriends.remove(friends.get(position));
                }
            }
        }

    }
}
