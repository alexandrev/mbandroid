package com.xandrev.mbandroid.gui.logger;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

import com.xandrev.mbandroid.R;

public class LogViewerActivity extends TabActivity {

    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("General");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Notifications");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("General");
        Intent intent = new Intent(this, GeneralLoggerActivity.class);
        String txt = getIntent().getStringExtra("log");
        intent.putExtra("log",txt);
        tab1.setContent(intent);


        tab2.setIndicator("Notifications");
        tab2.setContent(new Intent(this, NotificationLoggerActivity.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
