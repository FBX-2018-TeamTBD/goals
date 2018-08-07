package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.MainActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.StoryFragment;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<Goal> mGoals;
    private List<ParseUser> friends;
    int startIndex = 0;
    Context context;

    public StoryAdapter(List<Goal> goals, List<ParseUser> friends) {
        this.mGoals = goals;
        this.friends = friends;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new StoryAdapter.ViewHolder(
                inflater.inflate(R.layout.item_single_story, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final StoryAdapter.ViewHolder holder, int position) {
        // get the data according to position
        final Goal goal = mGoals.get(position);
        final ArrayList<String> imageUrls = goal.getStoryUrls();
        final ArrayList<ParseObject> story = goal.getStory();
        if (story.size() > 0 && imageUrls.size() > 0) {

            for (int i = 0; i < story.size(); i++){
                boolean seen = false;
                ParseObject image = story.get(i);
                List<ParseUser> users = image.getList("viewedBy");
                if (users != null) {
                    if (users.contains(ParseUser.getCurrentUser())){
                        seen = true;
                    }
                }
                if (!seen) {
                    startIndex = i;
                    // TODO - show blue dot under story
                    holder.ivDot.setVisibility(View.VISIBLE);
                    break;
                } else {
                    holder.ivDot.setVisibility(View.GONE);
                }
            }

            Glide.with(context)
                    .load(imageUrls.get(startIndex))
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                    .into(holder.ivStory);
            holder.ivStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) context;
                    holder.ivDot.setVisibility(View.GONE);
                    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                    fragTransStory.add(R.id.main_central_fragment, StoryFragment.newInstance(story, startIndex, ParseUser.getCurrentUser())).commit();
                }
            });
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity activity = (MainActivity) context;
                    holder.ivDot.setVisibility(View.GONE);
                    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                    fragTransStory.add(R.id.main_central_fragment, StoryFragment.newInstance(story, startIndex, ParseUser.getCurrentUser())).commit();
                }
            });
        }

        Util.setImage(friends.get(position).getParseFile("image"), context.getResources(), holder.ivProfile, R.color.white);
        holder.tvTitle.setText(goal.getTitle());
        startIndex = 0;
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.ivDot) ImageView ivDot;
        @BindView(R.id.gradient) View view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){ }
    }
}
