package com.xandrev.mbandroid.settings.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexa on 13/12/2015.
 */
public class NotificationSettings {

    private SharedPreferences shared;
    private Map<String,String> packageAppMap;
    private Map<String,String> appMap;
    private Context context;
    private static final String TAG = "NotificationSettings";

    private static NotificationSettings instance;

    public static NotificationSettings getInstance(Context ctx){
        if(instance == null){
            instance = new NotificationSettings(ctx);
        }
        return instance;
    }

    public NotificationSettings(Context context){
        this.context = context;
        shared = context.getSharedPreferences("notificationSettings", Context.MODE_APPEND);
    }

    public List<String> getApplicationNames(){
        List<String> out = new ArrayList<>();
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        packageAppMap = new HashMap<String, String>();
        appMap = new HashMap<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String app = packageInfo.loadLabel(pm).toString();
            if(app != null) {
                out.add(app);
                packageAppMap.put(app, packageInfo.packageName);
                appMap.put(packageInfo.packageName, app);
            }
        }
        return out;
    }

    public List<String> getEnabledApps() {
        String enabledAppsStr = shared.getString("enabledApps", null);
        Log.i(TAG, "Recovering enabled apps list: " + enabledAppsStr);
        List<String> list = null;
        if(enabledAppsStr != null) {
            String[] apps = enabledAppsStr.split(",");
            list = Arrays.asList(apps);
        }
        return list;
    }

    public boolean isEnabledApplication(String value) {
        Log.d(TAG,"Recovered value:"+value);
        String transformedValue = packageAppMap.get(value);
        Log.d(TAG,"Transformed value: "+transformedValue);
        List list = getEnabledApps();
        return list.contains(transformedValue);
    }

    public void setEnabledApps(List<String> newEnabledAppList) {
        String items = "";
        if(newEnabledAppList != null){
            for(String enabledApp : newEnabledAppList){
                items+=packageAppMap.get(enabledApp)+",";
            }
        }
        shared.edit().putString("enabledApps",items).apply();
    }

    public void setEnabledTile(boolean checked) {
        Log.d(TAG,"Tile Activated: "+checked);
        shared.edit().putBoolean("enabled",checked).apply();
    }

    public boolean getEnabledTile() {
        return shared.getBoolean("enabled",false);
    }
}
