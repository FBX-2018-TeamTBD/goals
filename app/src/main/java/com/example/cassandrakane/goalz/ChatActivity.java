package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.ChatAdapter;
import com.example.cassandrakane.goalz.models.Message;
import com.example.cassandrakane.goalz.utils.Util;
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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ChatActivity extends AppCompatActivity {
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    static final String FROM_USER_KEY = "fromUser";
    static final String TO_USER_KEY = "toUser";

    @BindView(R.id.etMessage) EditText etMessage;
    @BindView(R.id.btSend) Button btnSend;
    @BindView(R.id.rvChat) RecyclerView rvChat;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ivCamera) ImageView ivCamera;
    @BindView(R.id.ivPicture) ImageView ivPicture;
    @BindView(R.id.ivMicrophone) ImageView ivMicrophone;
    @BindView(R.id.rlSend) RelativeLayout rlSend;

    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    ParseUser toUser;
    boolean mFirstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this);

        toUser = getIntent().getParcelableExtra(ParseUser.class.getSimpleName());
        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(toUser.getUsername());

        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                rvChat.requestFocus();
                Util.hideKeyboard(rvChat, ChatActivity.this);
                ivMicrophone.setVisibility(View.VISIBLE);
                ivCamera.setVisibility(View.VISIBLE);
                ivPicture.setVisibility(View.VISIBLE);
                slideRight(ivCamera);
                slideRight(ivPicture);
                slideRight(ivMicrophone);
                slideRight(etMessage);
                etMessage.setHint("Aa");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return true;
            }

        });

        rvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        animator.setAddDuration(1000);
        animator.setRemoveDuration(500);
        animator.setChangeDuration(500);
        animator.setMoveDuration(1000);
        rvChat.setItemAnimator(animator);

        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           public void onFocusChange(View v, boolean hasFocus) {
               if (hasFocus) {
                   slideLeft(ivCamera);
                   slideLeft(ivPicture);
                   slideLeft(ivMicrophone);
                   ivMicrophone.setVisibility(View.GONE);
                   ivCamera.setVisibility(View.GONE);
                   ivPicture.setVisibility(View.GONE);
                   etMessage.setHint("Type a message...");
               }
           }
        });
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

        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        rvChat.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);


        // When send button is clicked, create message object on Parse
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                if (!data.equals("")) {
                    final Message message = new Message();
                    message.setBody(data);
                    message.setFromUser(ParseUser.getCurrentUser());
                    message.setToUser(toUser);
                    mAdapter.lastMessageSent = "";
                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            //refreshMessages();
                            mMessages.add(0, message);
                            mAdapter.notifyItemInserted(0);
                        }
                    });
                    etMessage.setText("");
                }
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
                    int size = mMessages.size();
                    mMessages.clear();
                    mAdapter.notifyItemRangeRemoved(0, size);
                    mMessages.addAll(messages);
                    mAdapter.notifyItemRangeInserted(0, messages.size()); // update adapter
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

    public void goBack(View view) {
        finish();
    }

    // slide the view from below itself to the current position
    public void slideRight(View view){
        TranslateAnimation animate = new TranslateAnimation(
                -370,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                -370,                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideUp(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                -25); // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -25,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

}

