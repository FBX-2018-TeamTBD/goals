package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.example.cassandrakane.goalz.models.Video;
import com.example.cassandrakane.goalz.utils.AnimationHelper;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalSimpleAdapter extends RecyclerView.Adapter<GoalSimpleAdapter.ViewHolder> {

    private List<Goal> mGoals;
    Context context;

    public GoalSimpleAdapter(List<Goal> goals) {
        this.mGoals = goals;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_goal_simple, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        final Goal goal = mGoals.get(position);

        holder.tvTitle.setText(goal.getTitle());
        List<ParseObject> story = goal.getStory();
        if (story.size() > 0) {
            ParseObject parseObject = story.get(story.size() - 1);
            String imageUrl = "";
            if (Util.isImage(parseObject)) {
                imageUrl = ((Image) parseObject).getImage().getUrl();
            } else {
                imageUrl = ((Video) parseObject).getImage().getUrl();
            }
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                    .into(holder.ivStory);
        } else {
            holder.vGradient.setVisibility(View.INVISIBLE);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.orange));
            holder.ivAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivCheck) ImageView ivCheck;
        @BindView(R.id.vGradient) View vGradient;
        @BindView(R.id.ivAdd) ImageView ivAdd;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Goal goal = mGoals.get(position);
                goal.setSelected(!(goal.isSelected()));
                if (goal.isSelected()) {
                    AnimationHelper.enterReveal(ivCheck);
                } else {
                    AnimationHelper.exitReveal(ivCheck);
                }
            }
        }
    }
}
