package com.xandrev.mbandroid.tiles.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;
import com.xandrev.mbandroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexa on 12/11/2015.
 */
public class NotificationTileData {

    private final Bitmap tileIconSmall;
    private final Bitmap tileIcon;
    private final UUID id;
    private final UUID pageId;
    private final String title;
    private final Context context;

    public NotificationTileData(Context context){
        this.context = context;
        id = UUID.fromString("a14f3e6c-a03c-11e5-8994-feff819cdc9f");
        pageId = UUID.fromString("de391572-a047-11e5-8994-feff819cdc9f");
        title = "Notifications";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        tileIcon = BitmapFactory.decodeResource(getContext().getResources(), R.raw.notification_center_icon, options);
        tileIconSmall = BitmapFactory.decodeResource(getContext().getResources(), R.raw.notification_center_icon_small, options);
    }


public Bitmap getTileIconSmall() {
        return tileIconSmall;
    }

    public Bitmap getTileIcon() {
        return tileIcon;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Context getContext() {
        return context;
    }


    public PageLayout getPageLayout() {
        return new PageLayout(
                new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL)
                        .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0).setId(21).setPressedColor(Color.BLUE))
        );
    }

    public PageData getPages() {

        return new PageData(pageId, 0)
                .update(new TextButtonData(21, "Dismiss All"));

    }
}
