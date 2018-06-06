package com.example.android.myfyp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.onesignal.OneSignal;

import java.util.Timer;
import java.util.TimerTask;

public class welecomeActivity extends AppCompatActivity {
    LinearLayout l1, l2;
    Animation uptodown, downtoup;
    private int timerDelay = 1500;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welecome);

        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(welecomeActivity.this, MainActivity.class));
                finish();
            }
        };
        new Timer().schedule(timerTask, timerDelay);
    }
}
