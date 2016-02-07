package com.xandrev.mbandroid.tiles.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;
import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.manager.MSBandManager;
import com.xandrev.mbandroid.settings.notifications.NotificationSettings;
import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.TilesManager;
import com.xandrev.mbandroid.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexa on 12/11/2015.
 */
public class NotificationTile implements CommonTile {

    private static final String TAG = "NotificaitonTile";

    private final Bitmap tileIconSmall;
    private final Bitmap tileIcon;
    private final UUID id;
    private final UUID pageId;
    private final String title;

    private Context context;

    private TilesManager manager;

    private NotificationSettings settings;

    private List<String> notificationsArrived;
    private int notificationId = 1;

    private static NotificationTile instance;

    public static final NotificationTile getInstance(TilesManager manager){
        if(instance == null){
            instance = new NotificationTile(manager);
        }
        return instance;
    }



    public NotificationTile( TilesManager manager){
        this.manager = manager;
        this.context = manager.getContext();
        notificationsArrived = new ArrayList<>();
        settings = NotificationSettings.getInstance(context);
        id = UUID.fromString("a14f3e6c-a03c-11e5-8994-feff819cdc9f");
        pageId = UUID.fromString("de391572-a047-11e5-8994-feff819cdc9f");
        title = "Notifications+";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        tileIcon = BitmapFactory.decodeResource(context.getResources(), R.raw.notification_center_icon, options);
        tileIconSmall = BitmapFactory.decodeResource(context.getResources(), R.raw.notification_center_icon_small, options);

    }

    @Override
    public PageData updatePages() {
       return getPages();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public Bitmap getIcon() {
        return tileIcon;
    }

    @Override
    public Bitmap getSmallIcon() {
        return tileIconSmall;
    }

    @Override
    public void executeNotification(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        Log.d(TAG,"Key Here: "+sbn.getKey());
        if (extras != null) {

            String title = extras.getString("android.title");
            CharSequence notifText = extras.getCharSequence("android.text");
            if(notifText != null) {
                String text = notifText.toString();
                boolean bigText = false;
                notifText = extras.getCharSequence("android.bigText");
                if (notifText != null) {
                    text = notifText.toString();
                    bigText = true;
                }

                if (!avoidSendMessage(text, bigText)) {
                    MSBandManager client = manager.getBand();
                    if (client != null && client.isConnected()) {
                        client.sendMessage(this, title, text);
                        notificationsArrived.add(sbn.getKey());
                        Log.d(TAG, "notification size: " + notificationsArrived.size());
                        client.addPage(this);
                    }
                }
            }
        }
    }

    @Override
    public boolean isAffected(String pack) {
        return settings.getEnabledApps().contains(pack);
    }

    @Override
    public PageData getPage() {
        return getPages();
    }

    public PageLayout getPageLayout() {
        return new PageLayout(
                new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL)
                        .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0).setId(21))
                        .addElements(new TextButton(0, 50, 210, 45).setMargins(0, 5, 0, 0).setId(22))
        );
    }

    public PageData getPages() {
        return new PageData(pageId, 0)
                .update(new TextButtonData(21, "Dismiss Notifs."))
                .update(new TextButtonData(22, "Clear Pages"));

    }

    @Override
    public void manageAction(Intent intent) {
        if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
            TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
            Log.i(TAG,""+buttonData.getElementID());
            if(buttonData.getElementID() == 21) {
                clearNotifications();
            }else if(buttonData.getElementID() == 22){
                manager.getBand().clearPages(this);
            }
        }
    }

    private void clearNotifications() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setTicker("cleaning_notifications").setContentText(generateKeyNotifications()).setSmallIcon(R.drawable.ic_launcher);
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, mBuilder.build());
        notificationId++;
        notificationsArrived.clear();
    }

    private CharSequence generateKeyNotifications() {
        String item = "";
        if(notificationsArrived != null) {
            for (String sbn : notificationsArrived) {
                Log.d(TAG,"StatusBar Notification:"+sbn);
                item+=sbn+",";
            }
        }
        Log.d(TAG,"Key Notification: "+item);
        return item;
    }


    private boolean avoidSendMessage(String message, boolean bigMessage) {
        if (message != null && !"".equals(message)) {
            String tokens[] = message.split(" ");
            if (tokens.length >= 3 && Util.isNumeric(tokens[0]) && !bigMessage) {
                return true;
            }
        }
        return false;
    }


}
