package com.example.cassandrakane.goalz.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.R;
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

        holder.tvTitle.setText(friend.getUsername());
        ParseFile file = (ParseFile) friend.get("image");
        if (file != null) {
            try {
                Glide.with(context)
                        .load(Uri.fromFile(file.getFile()))
                        .apply(new RequestOptions().centerCrop())
                        .into(holder.ivProfile);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvTitle) TextView tvTitle;
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
                    enterReveal(ivCheck);
                    selectedFriends.add(friends.get(position));
                } else {
                    ivProfile.setTag(R.drawable.add);
                    exitReveal(ivCheck);
                    selectedFriends.remove(friends.get(position));
                }
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
}
