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
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.FeedActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.StoryFragment;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<Goal> mGoals;
    Context context;

    public StoryAdapter(List<Goal> goals) {
        this.mGoals = goals;
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

        List<Image> imageList = goal.getList("images");
        final ArrayList<String> imageUrls = getImageUrls(imageList);

        if (imageUrls.size() > 0) {
            Glide.with(context)
                    .load(imageUrls.get(imageUrls.size() - 1))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivStory);
            holder.ivStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Image> imageList = goal.getList("images");
                    ArrayList<String> imageUrls = getImageUrls(imageList);
                    FeedActivity activity = (FeedActivity) context;
                    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                    fragTransStory.add(R.id.drawer_layout, StoryFragment.newInstance(imageUrls, imageUrls.size() - 1)).commit();
                    activity.toolbar.setVisibility(View.INVISIBLE);
                }
            });
        }
        holder.tvTitle.setText(goal.getTitle());
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivStory) ImageView ivStory;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){
//            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION){
//                Goal goal = mGoals.get(position);
//                goal.setSelected(!(goal.isSelected()));
//                itemView.setBackgroundColor(goal.isSelected() ? Color.CYAN : Color.WHITE);
//            }
        }
    }

    public ArrayList<String> getImageUrls(List<Image> imageList) {
        ArrayList<Image> images = new ArrayList<Image>();
        if (imageList != null) {
            images.addAll(imageList);
        }
        ArrayList<String> imageUrls = new ArrayList<String>();
        for (Image i : images) {
            try {
                imageUrls.add(i.fetchIfNeeded().getParseFile("image").getUrl());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return imageUrls;
    }
}
