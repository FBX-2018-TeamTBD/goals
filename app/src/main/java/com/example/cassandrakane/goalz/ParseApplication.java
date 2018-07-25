package com.example.cassandrakane.goalz;

import android.app.Application;

import com.example.cassandrakane.goalz.models.ApprovedFriendRequests;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.example.cassandrakane.goalz.models.RemovedFriends;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.example.cassandrakane.goalz.models.SharedGoal;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //register classes
        ParseObject.registerSubclass(Goal.class);
        ParseObject.registerSubclass(SharedGoal.class);
        ParseObject.registerSubclass(Image.class);
        ParseObject.registerSubclass(SentFriendRequests.class);
        ParseObject.registerSubclass(ApprovedFriendRequests.class);
        ParseObject.registerSubclass(RemovedFriends.class);

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("FBUGoals") // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("http://fbu-goals.herokuapp.com/parse/")
                .enableLocalDataStore()
                .build());

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}