package com.xandrev.mbandroid.notifications;

/**
 * Created by alexa on 12/8/2015.
 */


import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.TilesManager;

import java.util.List;


public class NotificationManager extends NotificationListenerService {
    private TilesManager tilesManager;
    private static final String TAG = "NotificationManager";

    @Override
    public void onCreate() {
        super.onCreate();
        tilesManager = TilesManager.getInstance(getBaseContext());
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG, "New notification detected");
        if (sbn != null) {
            String pack = sbn.getPackageName();
            Log.i(TAG, "Notification source: " + pack);
            List<CommonTile> tileList = tilesManager.getTilesAffected(pack);
            if (tileList != null) {
                Log.i(TAG,"Tiles affected: "+tileList.size());
                for (CommonTile tile : tileList) {
                    tile.executeNotification(sbn);
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Notification Removed");

    }
}


