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

import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.tiles.TilesManager;

public class mBandroid extends Activity {

	private Button btnStart;
	private TilesManager manager;
	private final mBandroid act = this;
    private static final String TAG = "mBandroid";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = TilesManager.getInstance(this.getApplicationContext());
        btnStart = (Button) findViewById(R.id.btnStart);
        findViewById(R.id.notificationsBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mBandroid.this, NotificationSettings.class);
                startActivity(intent);
            }
        });


        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.start(act);
                manager.activate();
            }
        });
    }

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void cancellNotificaitons(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                StatusBarNotification[] notificationList = manager.getActiveNotifications();
                Log.i(TAG, "##");
                if(notificationList != null){
                    Log.i(TAG,"## Size:"+notificationList.length);
                }
                manager.cancelAll();
            }
        });
    }


}

