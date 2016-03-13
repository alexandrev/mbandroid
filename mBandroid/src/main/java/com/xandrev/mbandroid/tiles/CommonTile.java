package com.xandrev.mbandroid.tiles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.service.notification.StatusBarNotification;

import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;

import java.util.UUID;

/**
 * Created by alexa on 12/11/2015.
 */
public interface CommonTile {

    PageData updatePages();

    UUID getId();

    String getName();

    Bitmap getIcon();

    Bitmap getSmallIcon();

    void executeNotification(StatusBarNotification sbn);

    boolean isAffected(String pack);

    PageData getPage();

    PageLayout getPageLayout();

    void manageAction(Intent intent);
}
