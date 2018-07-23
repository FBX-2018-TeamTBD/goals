package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.SearchFriendsActivity;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

import static android.app.Activity.RESULT_OK;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.ViewHolder> implements Filterable {

    List<ParseUser> searchList;
    List<ParseUser> filteredList;
    Context context;

    public SearchFriendAdapter(List<ParseUser> list) {
        searchList = list;
        filteredList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SearchFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ParseUser user = filteredList.get(position);
        holder.tvUsername.setText(user.getUsername());
        Util.setImage(user, "image", context.getResources(), holder.ivProfile, 8.0f);
        holder.addBtn.setTag(R.drawable.add);
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.addBtn.getTag().equals(R.drawable.check)) {
                    holder.addBtn.setBackground(context.getDrawable(R.drawable.check));
                    holder.addBtn.setTag(R.drawable.check);
                    addFriend(user);
                }
            }
        });
    }

    public void addFriend(final ParseUser user) {
        SentFriendRequests request = new SentFriendRequests(ParseUser.getCurrentUser(), user);
        request.saveInBackground();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList.clear();
                } else {
                    List<ParseUser> filtered = new ArrayList<>();
                    for (ParseUser user : searchList) {
                        if (user.getUsername().toLowerCase().contains(charString)) {
                            filtered.add(user);
                        }
                    }
                    filteredList = filtered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<ParseUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.btnAdd) Button addBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
