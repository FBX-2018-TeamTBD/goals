package com.example.cassandrakane.goalz;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.parse.ParseInstallation;

import static android.support.constraint.Constraints.TAG;

public class ParseFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO implement method to send registration to app's servers
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        if(token != null) {
            final String fcmSenderId = "token";
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("GCMSenderId", fcmSenderId);
            installation.put("deviceToken", token);
            installation.saveInBackground();
        } else {
            Log.d("IdService", "Failure");
        }
    }
}
