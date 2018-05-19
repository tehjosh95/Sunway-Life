package com.example.android.myfyp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;

class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private Context mContext;

    public NotificationOpenedHandler(Context context){
        mContext = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        startApp();
    }

    private void startApp() {
        Intent intent = new Intent(mContext, Chat.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("recipient", "atan@email,com");
        intent.putExtra("sender", "tei@email,com");
        mContext.startActivity(intent);
    }
}

