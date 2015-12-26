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
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.tiles.TilesManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class mBandroid extends Activity {

    private Button btnStart;
    private ImageButton btnMail;
    private ImageButton btnNotifications;
	private TilesManager manager;
	private final mBandroid act = this;
    private static final String TAG = "mBandroid";
    private TextView statusView;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = TilesManager.getInstance(this);
        manager.setActivity(this);
        btnStart = (Button) findViewById(R.id.btnStart);
        statusView = (TextView) findViewById(R.id.textView2);
        logView = (TextView) findViewById(R.id.textView4);
        btnNotifications = (ImageButton) findViewById(R.id.notificationsBtn);
        btnMail = (ImageButton) findViewById(R.id.emailBtn);


        if(manager != null) {
            manager.activate();
            manager.start(act);
        }
        updateBandStatus();


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
                updateBandStatus();

            }
        });
    }

	@Override
	protected void onResume() {
        Log.d(TAG,"Starting onResume operation");
        super.onResume();
        if(manager != null) {
            manager.activate();
            manager.start(act);
        }
        updateBandStatus();
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

    public void addMessage(final String msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Starting run method to update the log viewer in the GUI");
                String txt = logView.getText().toString();
                if(msg != null){
                    txt+="["+new SimpleDateFormat("HH:MM:ss").format(new Date())+"] - "+msg+"\n";
                }
                logView.setText(txt);
                Log.d(TAG, "Completed run method to update the log viewer in the GUI");
            }
        });
    }

    public void updateBandStatus(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Starting run method to update the band state in the GUI");
                manager = TilesManager.getInstance(mBandroid.this);
                boolean connected = false;
                if(manager.getBand() != null) {
                  connected = manager.getBand().isConnected();
                }
                Log.d(TAG,"Band is connected: "+connected);
                if(connected){
                    statusView.setText("Connected");
                    btnStart.setText("Stop");
                }
                else{
                    statusView.setText("Disconnected");
                    btnStart.setText("Start");
                }
                Log.d(TAG,"Completed run method to update the band state in the GUI");
            }
        });
    }


}

