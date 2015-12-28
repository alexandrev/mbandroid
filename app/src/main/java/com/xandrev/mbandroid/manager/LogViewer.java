package com.xandrev.mbandroid.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.xandrev.mbandroid.gui.mBandroid;
import com.xandrev.mbandroid.settings.base.GeneralSettings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexa on 28/12/2015.
 */
public class LogViewer {

    GeneralSettings settings;

    private static final String TAG = "LogViewer";
    private static LogViewer instance;

    public LogViewer(Context ctx) {
        settings = GeneralSettings.getInstance(ctx);

    }
    public static final LogViewer getInstance(Context ctx){
        if(instance == null){
            instance = new LogViewer(ctx);
        }
        return instance;
    }

    public String getLog(){
        return settings.getLog();
    }

    public void addMessage(final String msg){
        Log.d(TAG, "Starting run method to update the log viewer in the GUI");
        if(msg != null){
            settings.addLog("["+new SimpleDateFormat("HH:MM:ss").format(new Date())+"] - "+msg+"\n");
        }
        Log.d(TAG, "Completed run method to update the log viewer in the GUI");
    }
}
