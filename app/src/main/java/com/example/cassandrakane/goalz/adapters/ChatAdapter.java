package com.example.cassandrakane.goalz.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Message;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> mMessages;
    private Context mContext;
    private ParseUser mUser;

    public String lastMessageSent = "";

    public ChatAdapter(Context context, ParseUser user, List<Message> messages) {
        mMessages = messages;
        this.mUser = user;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_chat, parent, false);

        return new ViewHolder(contactView);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        final boolean isMe = message.getFromUser() != null && message.getFromUser() == mUser;

        Log.i("isMe", ""+isMe);
        Log.i("lastMessage", lastMessageSent);
        Log.i("message", message.getBody());
        if (isMe) {
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.body.setBackground(mContext.getDrawable(R.drawable.rounded_sent_message));
            holder.body.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.leftSpace.setVisibility(View.VISIBLE);
            holder.rightSpace.setVisibility(View.GONE);
            holder.ivMarginRight.setVisibility(View.GONE);
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.body.setBackground(mContext.getDrawable(R.drawable.rounded_received_message));
            holder.body.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.leftSpace.setVisibility(View.GONE);
            holder.rightSpace.setVisibility(View.VISIBLE);
            holder.ivMarginLeft.setVisibility(View.GONE);
        }

        if (!lastMessageSent.equals("") && message.getFromUser().getUsername().equals(lastMessageSent)) {
            if (isMe) {
                holder.imageMe.setVisibility(View.GONE);
                holder.ivMarginRight.setVisibility(View.VISIBLE);
            } else {
                holder.imageOther.setVisibility(View.GONE);
                holder.ivMarginLeft.setVisibility(View.VISIBLE);
            }
        }

        lastMessageSent = message.getFromUser().getUsername();

        final ImageView profileView = isMe ? holder.imageMe : holder.imageOther;
        ParseFile image = message.getFromUser().getParseFile("image");
        Util.setImage(image, mContext.getResources(), profileView, R.color.orange);
        holder.body.setText(message.getBody());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileOther) ImageView imageOther;
        @BindView(R.id.ivProfileMe) ImageView imageMe;
        @BindView(R.id.tvBody) TextView body;
        @BindView(R.id.left_space) View leftSpace;
        @BindView(R.id.right_space) View rightSpace;
        @BindView(R.id.ivMarginLeft) ImageView ivMarginLeft;
        @BindView(R.id.ivMarginRight) ImageView ivMarginRight;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}