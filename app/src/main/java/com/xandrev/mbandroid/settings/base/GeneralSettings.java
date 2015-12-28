package com.xandrev.mbandroid.settings.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.TilesManager;
import com.xandrev.mbandroid.tiles.mail.MailTile;
import com.xandrev.mbandroid.tiles.notifications.NotificationTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alexa on 25/12/2015.
 */
public class GeneralSettings {

    private SharedPreferences shared;
    private Context context;
    private static final String TAG = "GeneralSettings";

    private static GeneralSettings instance;

    public static GeneralSettings getInstance(Context ctx){
        if(instance == null){
            instance = new GeneralSettings(ctx);
        }
        return instance;
    }

    public GeneralSettings(Context context){
        this.context = context;
        shared = context.getSharedPreferences("settings", Context.MODE_APPEND);
        initSpecificSettings();
    }

    private void initSpecificSettings() {
        shared.edit().putString("tiles","notification,mail").apply();
    }


    public List<String> getEnabledTiles(){
        Log.d(TAG,"Starting to get the enabled tiles");
        String tiles = shared.getString("tiles","");
        Log.d(TAG,"Tiles: "+tiles);
        ArrayList<String> out = new ArrayList<>();
        if(tiles != null){
            String[] tilesArray = tiles.split(",");
            if(tilesArray != null && tilesArray.length > 0){
                Log.d(TAG,"Tiles Length: "+tilesArray.length);
                for(int i=0;i<tilesArray.length;i++) {
                    SharedPreferences sharedPrefs = context.getSharedPreferences(tilesArray[i] + "Settings", Context.MODE_APPEND);
                    Log.d(TAG,"Shared Preferences: "+sharedPrefs);
                    if(sharedPrefs != null) {
                        boolean outFlag = sharedPrefs.getBoolean("enabled", false);
                        Log.d(TAG,"Enabled: "+outFlag);
                        if (outFlag) {
                            Log.d(TAG, "Tile Activated: " + tilesArray[i]);
                            out.add(tilesArray[i]);
                        }
                    }
                }
            }
        }
        Log.d(TAG,"Completed to get the enabled tiles: "+out);
        return out;
    }


    public CommonTile getClassFromTile(TilesManager manager, String s) {
        CommonTile out = null;
        if(s != null){
            if("mail".equals(s)){
                out = MailTile.getInstance(manager);
            }else if("notification".equals(s)){
                out = NotificationTile.getInstance(manager);
            }
        }
        return out;
    }

    public String getLog() {
        return shared.getString("log","");
    }

    public void addLog(String s) {
        String currentLog = getLog();
        shared.edit().putString("log",currentLog+s).apply();
    }
}
