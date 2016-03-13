package com.xandrev.mbandroid.tiles;

import android.content.pm.ApplicationInfo;

import java.util.Date;

/**
 * Created by alexa on 2/6/2016.
 */
public class NotificationLog {

    private Date timestamp;

    private String tile;

    private String text;

    private String title;

    private String packageString;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getPackage() {
        return packageString;
    }

    public void setPackage(String pack){
        this.packageString = pack;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
