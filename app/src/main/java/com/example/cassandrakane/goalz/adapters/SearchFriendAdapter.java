package com.example.cassandrakane.goalz.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.FriendsModalActivity;
import com.example.cassandrakane.goalz.MainActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.example.cassandrakane.goalz.utils.AnimationHelper;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.ViewHolder> implements Filterable {

    private List<ParseUser> searchList;
    private List<ParseUser> filteredList;
    public List<ParseUser> selectedFriends;
    Context context;
    private String requestActivityName;

    public SearchFriendAdapter(List<ParseUser> list, List<ParseUser> selFriends, String rActivityName) {
        searchList = list;
        filteredList = new ArrayList<>();
        selectedFriends = selFriends;
        requestActivityName = rActivityName;
    }

    @NonNull
    @Override
    public SearchFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ParseUser user = filteredList.get(position);
        holder.tvUsername.setText(user.getUsername());

        ParseFile image = user.getParseFile("image");
        Util.setImage(image, context.getResources(), holder.ivProfile, R.color.orange);
    }

    private void addFriend(final ParseUser user) {
        SentFriendRequests request = new SentFriendRequests(ParseUser.getCurrentUser(), user);
        request.saveInBackground();
        searchList.remove(user);
        filteredList.remove(user);
        notifyDataSetChanged();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList != null ? filteredList.size() : 0;
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
                        if (user.getUsername().toLowerCase().startsWith(charString)) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            ivProfile.setTag(R.drawable.add);
        }

        public void onClick(View v){
            int position = getAdapterPosition();
            ParseUser user = filteredList.get(position);
            if (!ivProfile.getTag().equals(R.drawable.check)) {
                ivProfile.setTag(R.drawable.check);
                if (requestActivityName.equals(MainActivity.class.getSimpleName())) {
                    addFriend(user);
                    Toast.makeText(context, "Sent friend request to " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
                }
                else if (requestActivityName.equals(FriendsModalActivity.class.getSimpleName())) {
                    selectedFriends.add(user);
                    AnimationHelper.enterReveal(ivCheck);
                }
            } else {
                ivProfile.setTag(R.drawable.add);
                if (requestActivityName.equals(FriendsModalActivity.class.getSimpleName())) {
                    selectedFriends.remove(user);
                    AnimationHelper.exitReveal(ivCheck);
                }
            }
            hideKeyboard(v);
        }
    }

}
