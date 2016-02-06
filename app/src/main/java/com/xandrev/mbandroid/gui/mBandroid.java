//Copyright (c) Microsoft Corporation All rights reserved.  
// 
//MIT License: 
// 
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
// 
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software. 
// 
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package com.xandrev.mbandroid.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.band.ConnectionState;
import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.gui.logger.LogViewerActivity;
import com.xandrev.mbandroid.manager.LogViewer;
import com.xandrev.mbandroid.tiles.TilesManager;

public class mBandroid extends Activity {

    private Button btnStart;
    private ImageButton btnMail;
    private ImageButton btnNotifications;
	private TilesManager manager;
	private final mBandroid act = this;
    private static final String TAG = "mBandroid";
    private ImageView statusView;
    private TextView versionView;
    private MenuItem logViewerButton;
    private MenuItem settingsButton;
    private LogViewer logViewer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        new MenuInflater(this).inflate(R.menu.main,menu);
        logViewerButton = (MenuItem) menu.findItem(R.id.action_logViewer);
        if(logViewerButton != null) {
            logViewerButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(mBandroid.this, LogViewerActivity.class);
                    intent.putExtra("log",logViewer.getLog());
                    startActivity(intent);
                    return true;
                }
            });
        }

        settingsButton = (MenuItem) menu.findItem(R.id.action_settings);
        if(settingsButton != null) {
            settingsButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(mBandroid.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = TilesManager.getInstance(this);
        manager.setActivity(this);
        btnStart = (Button) findViewById(R.id.btnStart);
        statusView = (ImageView) findViewById(R.id.textView2);
        btnNotifications = (ImageButton) findViewById(R.id.notificationsBtn);
        btnMail = (ImageButton) findViewById(R.id.emailBtn);
        versionView = (TextView) findViewById(R.id.versionTextView);
        logViewer = LogViewer.getInstance(this);

        if(manager.getBand() == null || !manager.getBand().isConnected()) {
            manager.activate();
            manager.start(act);
        }


        btnNotifications.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mBandroid.this, NotificationSettings.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.emailBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mBandroid.this, MailSettings.class);
                startActivity(intent);
            }
        });

        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnStart.getText().equals("Start")) {
                    manager.start(act);
                }else{
                    manager.stop();
                }

            }
        });
    }

	@Override
	protected void onResume() {
        Log.d(TAG,"Starting onResume operation");
        super.onResume();
        updateBandStatus(null);
        Log.d(TAG,"onResume operation completed");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(manager != null) {
            manager.deactivate();
        }
    }



    public void updateBandStatus(final String state){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                manager = TilesManager.getInstance(mBandroid.this);
                boolean connected = false || ConnectionState.CONNECTED.toString().equals(state);
                String version = "Unknown";
                if(manager.getBand() != null) {
                    Log.d(TAG,"Band is not null referenced");
                  connected = manager.getBand().isConnected();
                    Log.d(TAG,"Connected: "+connected);
                  version = manager.getBand().getVersion();
                    Log.d(TAG,"Version: "+version);
                }

                Log.d(TAG,"Band is connected: "+connected);
                if(connected){
                    statusView.setImageResource(android.R.drawable.button_onoff_indicator_on);
                    btnStart.setText("Stop");
                    versionView.setText(version);
                }
                else{
                    statusView.setImageResource(android.R.drawable.button_onoff_indicator_off);
                    btnStart.setText("Start");
                    versionView.setText(version);
                }
                Log.d(TAG,"Completed run method to update the band state in the GUI");
            }
        });
    }


}

