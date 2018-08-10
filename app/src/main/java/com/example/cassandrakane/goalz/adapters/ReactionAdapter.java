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
import com.example.cassandrakane.goalz.models.Reaction;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ViewHolder> {

    private List<ParseObject> mReactions;
    Context context;

    public ReactionAdapter(List<ParseObject> reactions) { mReactions = reactions; }

    @NonNull
    @Override
    public ReactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend_reaction, viewGroup, false);
        return new ReactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionAdapter.ViewHolder viewHolder, int i) {
        final Reaction reaction = (Reaction) mReactions.get(i);
        ParseUser user = reaction.getParseUser("user");
        String username = user.getUsername();
        ParseFile image = user.getParseFile("image");
        String type = reaction.getString("type");

        viewHolder.tvUsername.setText(username);

        Util.setImage(image, context.getResources(), viewHolder.ivProfile, R.color.orange);

        switch (type) {
            case "thumbs":
                viewHolder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.thumbs_react));
                break;
            case "goals":
                viewHolder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.goals_react));
                break;
            case "clap":
                viewHolder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.clap_react));
                break;
            case "ok":
                viewHolder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.ok_react));
                break;
            case "bump":
                viewHolder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.bump_react));
                break;
            default:
                viewHolder.ivCheck.setVisibility(View.GONE);
        }
//        //set item click here
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, FriendActivity.class);
//                i.putExtra(ParseUser.class.getSimpleName(), user);
//                context.startActivity(i);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mReactions.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){

        }
    }
}
