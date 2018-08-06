package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cassandrakane.goalz.adapters.ChatAdapter;
import com.example.cassandrakane.goalz.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    static final String FROM_USER_KEY = "fromUser";
    static final String TO_USER_KEY = "toUser";
    static final String BODY_KEY = "body";

    @BindView(R.id.etMessage) EditText etMessage;
    @BindView(R.id.btSend) Button btSend;
    @BindView(R.id.rvChat) RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    ParseUser toUser;
    boolean mFirstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        toUser = getIntent().getParcelableExtra(ParseUser.class.getSimpleName());

        setupMessagePosting();

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        parseQuery.whereEqualTo(FROM_USER_KEY, toUser);
        parseQuery.whereEqualTo(TO_USER_KEY, ParseUser.getCurrentUser());

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {
                        mMessages.add(0, object);

                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                rvChat.scrollToPosition(0);
                            }
                        });
                    }
                });

        refreshMessages();
    }

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final ParseUser user = ParseUser.getCurrentUser();
        mAdapter = new ChatAdapter(ChatActivity.this, user, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        rvChat.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);


        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                Message message = new Message();
                message.setBody(data);
                message.setFromUser(ParseUser.getCurrentUser());
                message.setToUser(toUser);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        refreshMessages();
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        query1.whereEqualTo(FROM_USER_KEY, ParseUser.getCurrentUser());
        query1.whereEqualTo(TO_USER_KEY, toUser);

        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
        query2.whereEqualTo(FROM_USER_KEY, toUser);
        query2.whereEqualTo(TO_USER_KEY, ParseUser.getCurrentUser());

        List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<Message> mainQuery = ParseQuery.or(queries);
        // Configure limit and sort order
        mainQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        mainQuery.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        mainQuery.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

}

