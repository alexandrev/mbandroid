package com.xandrev.mbandroid.services;

import android.content.Context;
import android.util.Log;

import com.xandrev.mbandroid.settings.base.GeneralSettings;

import java.util.Date;

/**
 * Created by alexa on 28/12/2015.
 */
public class LogViewer {

    GeneralSettings settings;

    private static final int MAX_SIZE_LOGS = 100;
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

    public void addMessage(final String msg) {
        Log.d(TAG, "Starting run method to update the log viewer in the GUI");
        if (msg != null) {
            int size = getSize();
            if (size >= MAX_SIZE_LOGS) {
                removeFirstLog();
            }
            settings.addLog(new Date().toString() + "-" + msg + "\n");
        }
        Log.d(TAG, "Completed run method to update the log viewer in the GUI");
    }

    private void removeFirstLog() {
        String log = getLog();
        int idx = log.lastIndexOf("\n",log.length()-1);
        log = log.substring(0,idx+1);
        settings.setLog(log);

    }

    public int getSize() {
        String log = getLog();
        if(log != null) {
            return log.split("\n").length;
        }
        return 0;
    }

    public void clean() {
        settings.setLog(null);
    }
}
