package com.example.android.myfyp;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

public class tabs extends Activity {
    private static final String TAG = tabs.class.getSimpleName();
    private LocalActivityManager mLocalActivityManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Club Members");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TabHost host = (TabHost)findViewById(R.id.tabhost);
        mLocalActivityManager = new LocalActivityManager(tabs.this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        host.setup(mLocalActivityManager);

        TabHost.TabSpec spec = host.newTabSpec("List of pending");
        spec.setIndicator("List of pending");
        spec.setContent(new Intent(this, List_of_pending.class));
        host.addTab(spec);

        spec = host.newTabSpec("List of successful");
        spec.setIndicator("List of successful");
        spec.setContent(new Intent(this, List_of_successful.class));
        host.addTab(spec);

        host.setCurrentTab(0);
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

            }
        });
//        tabs.addTab(tabs.newTabSpec("one").setContent(R.id.tab1content).setIndicator("List of pending").setContent(new Intent(this, List_of_pending.class)));
//        tabs.addTab(tabs.newTabSpec("two").setContent(R.id.tab2content).setIndicator("List of successful").setContent(new Intent(this, List_of_pending.class)));
//        tabs.setCurrentTab(0);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume ()
    {
        mLocalActivityManager.dispatchResume();
        super.onResume ();
    }

    @Override
    protected void onPause ()
    {
        mLocalActivityManager.dispatchPause(isFinishing());
        super.onPause ();
    }

    @Override
    protected void onStop ()
    {
        mLocalActivityManager.dispatchStop ();
        super.onStop ();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        mLocalActivityManager.saveInstanceState ();

    }

}
