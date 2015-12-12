package com.xandrev.mbandroid.tiles.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.xandrev.mbandroid.gui.mBandroid;
import com.xandrev.mbandroid.manager.MSBandManager;
import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.TilesManager;
import com.xandrev.mbandroid.utils.Util;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexa on 12/11/2015.
 */
public class NotificationTile implements CommonTile {

    private static final String TAG = "NotificaitonTile";

    private NotificationTileData data;

    private Context context;

    private TilesManager manager;

    public NotificationTile( TilesManager manager){
        this.manager = manager;
        this.context = manager.getContext();
        data = new NotificationTileData(context);

    }

    @Override
    public PageData updatePages() {
       return data.getPages();
    }

    @Override
    public UUID getId() {
        return data.getId();
    }

    @Override
    public String getName() {
        return data.getTitle();
    }

    @Override
    public Bitmap getIcon() {
        return data.getTileIcon();
    }

    @Override
    public Bitmap getSmallIcon() {
        return data.getTileIconSmall();
    }

    @Override
    public void executeNotification(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        if (extras != null) {

            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
            boolean bigText = false;
            if (extras.getCharSequence("android.bigText") != null) {
                text = extras.getCharSequence("android.bigText").toString();
                bigText = true;
            }

            if (!avoidSendMessage(text, bigText)) {
                MSBandManager client = manager.getBand();
                if (client != null && client.isConnected()) {
                    client.sendMessage(this, title, text);
                }
            }
        }
    }

    @Override
    public boolean isAffected(String pack) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("notificationSettings", Context.MODE_APPEND);
        String enabledApps = sharedPreferences.getString("enabledApps", "");
        List<String> allowedAppList = Arrays.asList(enabledApps.split(","));
        return allowedAppList.contains(pack);
    }

    @Override
    public PageData getPage() {
        return data.getPages();
    }

    @Override
    public PageLayout getPageLayout() {
        return data.getPageLayout();
    }

    @Override
    public void manageAction(Intent intent) {
        if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
            TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
            mBandroid mb = (mBandroid) manager.getBand().getMainActivity();
            mb.cancellNotificaitons();
        }
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
