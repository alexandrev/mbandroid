package com.xandrev.mbandroid.tiles.mail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.manager.MSBandManager;
import com.xandrev.mbandroid.settings.mail.MailSettings;
import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.TilesManager;
import com.xandrev.mbandroid.utils.Util;

import java.util.UUID;

/**
 * Created by alexa on 14/12/2015.
 */
public class MailTile implements CommonTile {

    private final Bitmap tileIconSmall;
    private final Bitmap tileIcon;
    private final UUID id;
    private final String title;
    private final Context context;
    private TilesManager manager;
    private MailSettings settings;

    private static MailTile instance;

    public static final MailTile getInstance(TilesManager manager){
        if(instance == null){
            instance = new MailTile(manager);
        }
        return instance;
    }

    public MailTile(TilesManager manager){
        this.manager = manager;
        this.context = manager.getContext();
        settings = MailSettings.getInstance(context);
        id = UUID.fromString("b14f3e6c-a03c-11e5-8994-feff819cdc9e");
        title = "Mail+";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        tileIcon = BitmapFactory.decodeResource(context.getResources(), R.raw.mail_center_icon, options);
        tileIconSmall = BitmapFactory.decodeResource(context.getResources(), R.raw.mail_center_icon_small, options);
    }

    @Override
    public PageData updatePages() {
        return null;
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
        if (extras != null) {

            String title = extras.getString("android.title");
            if(extras.getCharSequence("android.text") != null) {
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

    @Override
    public boolean isAffected(String pack) {
        return settings.isEnabledPackage(pack);
    }

    @Override
    public PageData getPage() {
        return null;
    }

    @Override
    public PageLayout getPageLayout() {
        return null;
    }

    @Override
    public void manageAction(Intent intent) {

    }
}
