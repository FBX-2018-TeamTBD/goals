package com.example.cassandrakane.goalz.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;

import java.util.ArrayList;
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
        ArrayList<String> storyUrls = goal.getStoryUrls();
        if (storyUrls.size() > 0) {
            Glide.with(context)
                    .load(storyUrls.get(storyUrls.size() - 1))
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
                    enterReveal(ivCheck);
                } else {
                    exitReveal(ivCheck);
                }
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
