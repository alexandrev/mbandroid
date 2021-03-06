package com.xandrev.mbandroid.notifications;

/**
 * Created by alexa on 12/8/2015.
 */


import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.xandrev.mbandroid.services.NotificationLogger;
import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.TilesManager;

import java.util.ArrayList;
import java.util.List;


public class NotificationManager extends NotificationListenerService {

    private NotificationLogger notificationLogger;
    private List<StatusBarNotification> dupId;
    private TilesManager tilesManager;
    private static final String TAG = "NotificationManager";


    @Override
    public void onCreate() {
        super.onCreate();
        tilesManager = TilesManager.getInstance(this);
        notificationLogger = NotificationLogger.getInstance();
        dupId = new ArrayList<>();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "New notification detected");
        if (sbn != null) {

            if(!dupId.contains(sbn)) {
                String pack = sbn.getPackageName();
                Log.d(TAG, "Notification source: " + pack);
                Log.d(TAG, "Key: " + sbn.getKey());
                Log.d(TAG, "Id: " + sbn.getId());
                dupId.add(sbn);

                if (!isInternalNotification(sbn)) {
                    List<CommonTile> tileList = tilesManager.getTilesAffected(pack);
                    if (tileList != null) {
                        Log.d(TAG, "Tiles affected: " + tileList.size());
                        for (CommonTile tile : tileList) {
                            tile.executeNotification(sbn);
                            notificationLogger.addNotificationLog(tile, sbn);
                        }
                    }
                } else {
                    String tickerText = sbn.getNotification().tickerText.toString();
                    if (tickerText != null && !"".equals(tickerText)) {
                        if ("cleaning_notifications".equals(tickerText)) {
                            clearAllNotifications(sbn.getNotification());
                        }
                    }
                    cancelNotification(sbn.getKey());
                }
            }
        }
    }

    private void clearAllNotifications(Notification notification) {
        if(notification != null) {
            Bundle extras = notification.extras;
            if(extras.getCharSequence("android.text") != null) {
                String text = extras.getCharSequence("android.text").toString();
                Log.d(TAG,"Notification Keys: "+text);
                if(text != null) {
                    String[] keys = text.split(",");

                    if (keys != null) {
                        for (String key : keys) {
                            Log.d(TAG, "Notification Key: " + key);
                            cancelNotification(key);
                        }
                    }
                }
            }
        }
    }

    private boolean isInternalNotification(StatusBarNotification sbn) {
        boolean out = false;
        if(sbn != null){
            String packageName = sbn.getPackageName();
            if(packageName != null && !"".equals(packageName)){
                out = "com.xandrev.mbandroid".equals(packageName);
                String tickerText = sbn.getNotification().tickerText.toString();
                if (tickerText != null && !"".equals(tickerText)) {
                    out = "cleaning_notifications".equals(tickerText) && out;
                }
            }
        }
        return out;

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "Notification Removed");

    }
}


