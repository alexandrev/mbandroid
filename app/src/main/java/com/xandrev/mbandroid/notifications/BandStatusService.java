package com.xandrev.mbandroid.notifications;


import android.app.*;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.band.BandException;
import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.manager.MSBandManager;

import de.greenrobot.event.EventBus;

/**
 * Created by alexa on 25/12/2015.
 */
public class BandStatusService  extends IntentService {

    private static MSBandManager client;
    private static boolean flagExist;
    private static final String TAG = "BandStatusService";

    public BandStatusService(){
        super("BandStatusService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BandStatusService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Starting onHandleIntent");
        Toast.makeText(this, "Band Status Service started", Toast.LENGTH_SHORT).show();
        client = EventBus.getDefault().getStickyEvent(MSBandManager.class);
        boolean currentStatus = false;
        if(intent != null) {
            Log.i(TAG,"Client object: "+client);

            String command = intent.getStringExtra("command");
            Log.i(TAG,"Command recovered: "+command);
            if("end".equals(command)){
                flagExist = true;
            }
            if("start".equals(command)){
                flagExist = false;
            }

            int notificationId = 1;
            while(!flagExist){
                if (client != null) {
                    try {
                        Log.i(TAG, "Checking if the band is still connected: " + client.isConnected());
                        if (!client.isConnected()) {
                            client.connect();
                            Toast.makeText(this, "Reconnecting the Microsoft Band", Toast.LENGTH_SHORT).show();
                        }
                        if(currentStatus && !client.isConnected()){
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(client.getMainActivity()).setTicker("mbandroid: Connection Status").setContentText("MS Band is disconected!").setSmallIcon(R.drawable.ic_launcher);
                            android.app.NotificationManager mNotifyMgr = (android.app.NotificationManager) client.getMainActivity().getSystemService(client.getMainActivity().NOTIFICATION_SERVICE);
                            mNotifyMgr.notify(notificationId, mBuilder.build());
                            notificationId++;
                        }
                        currentStatus = client.isConnected();
                        Thread.sleep(60000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }catch(BandException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.i(TAG,"onHandleIntent Ended");
    }
}
