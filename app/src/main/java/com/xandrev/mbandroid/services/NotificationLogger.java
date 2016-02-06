package com.xandrev.mbandroid.services;

import android.service.notification.StatusBarNotification;

import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.NotificationLog;
import com.xandrev.mbandroid.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexa on 2/6/2016.
 */
public class NotificationLogger {


    public static final int NOTIFICATION_LOGGER_MAX_SIZE = 50;

    private List<NotificationLog> notificationLog;

    private static  NotificationLogger instance;

    private Date latestNotification;

    public static final NotificationLogger getInstance(){
        if(instance == null){
            instance = new NotificationLogger();
        }
        return instance;
    }


    private NotificationLogger(){
        notificationLog = new ArrayList<>();
    }

    public void addNotificationLog(CommonTile tile, StatusBarNotification sbn) {
        NotificationLog log = new NotificationLog();
        log.setTimestamp(new Date());
        log.setTile(tile.getName());
        log.setText(Util.getTextFromNotification(sbn));
        log.setPackage(sbn.getPackageName());
        log.setTitle(Util.getTitleFromNotification(sbn));
        if(notificationLog.size() > NOTIFICATION_LOGGER_MAX_SIZE){
            notificationLog.remove(0);
        }
        notificationLog.add(log);
        latestNotification = new Date();
    }

    public String getLog(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String out = "";
        if(notificationLog != null) {
            for (int i = 0; i < notificationLog.size(); i++) {
                NotificationLog log = notificationLog.get(i);
                String item = sdf.format(log.getTimestamp());
                item += " - "+log.getTile() + " - " + log.getText();
            }
        }
        return out;
    }

    public List<NotificationLog> getItems() {
        return notificationLog;
    }
}
