package com.example.android.myfyp;

import android.app.Application;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
