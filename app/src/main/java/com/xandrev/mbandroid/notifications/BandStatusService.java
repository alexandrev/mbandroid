package com.xandrev.mbandroid.notifications;


import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by alexa on 25/12/2015.
 */
public class BandStatusService  extends IntentService {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Band Status Service started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
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

    }
}
