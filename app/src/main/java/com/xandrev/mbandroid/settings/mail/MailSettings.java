package com.xandrev.mbandroid.settings.mail;

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
public class MailSettings {

    private SharedPreferences shared;
    private Map<String,String> packageAppMap;
    private Map<String,String> appMap;
    private Context context;
    private static final String TAG = "MailSettings";

    private static MailSettings instance;

    public static MailSettings getInstance(Context ctx){
        if(instance == null){
            instance = new MailSettings(ctx);
        }
        return instance;
    }

    public MailSettings(Context context){
        this.context = context;
        shared = context.getSharedPreferences("mailSettings", Context.MODE_APPEND);
        initMailClients();
    }

    private void initMailClients() {
        //TODO Complete with another mail clients
        String[] mailClients = new String[]{"Gmail","Outlook"};
        String init = "";
        if(mailClients != null){
            for(String mail : mailClients){
                init+=mail+",";
            }
        }
        shared.edit().putString("mailClients",init).apply();
    }

    public List<String> getInstalledMailApps(){
        List<String> out = new ArrayList<>();
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        packageAppMap = new HashMap<String, String>();
        appMap = new HashMap<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String app = packageInfo.loadLabel(pm).toString();
            if(app != null && isMailApp(app)) {
                Log.i(TAG,"Added application: "+app + " / " + packageInfo.packageName);
                out.add(app);
                packageAppMap.put(app, packageInfo.packageName);
                appMap.put(packageInfo.packageName, app);
            }
        }
        return out;
    }

    private boolean isMailApp(String app) {
        boolean out = false;
        List<String> mailApps = getMailApps();
        if(mailApps != null){
            out = mailApps.contains(app);
        }
        return out;
    }

    private List<String> getMailApps() {
        List<String> list = null;
        String clientsApp = shared.getString("mailClients","");
        if(clientsApp != null){
           list =  Arrays.asList(clientsApp.split(","));
        }
        return list;
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
        init();
        Log.d(TAG,"Recovered value:"+value);
        String transformedValue = packageAppMap.get(value);
        Log.d(TAG,"Transformed value: "+transformedValue);
        List list = getEnabledApps();
        return list.contains(transformedValue);
    }

    public boolean isEnabledPackage(String value) {
        init();
        Log.d(TAG,"Recovered value:"+value);
        String transformedValue = appMap.get(value);
        Log.d(TAG,"Transformed value: "+transformedValue);
        List list = getEnabledApps();
        return list.contains(transformedValue);
    }

    private void init() {
        if(packageAppMap == null){
            getInstalledMailApps();
        }
    }

    public void setEnabledApps(List<String> newEnabledAppList) {
        String items = "";
        if(newEnabledAppList != null){
            for(String enabledApp : newEnabledAppList){
                items+=enabledApp+",";
            }
        }
        shared.edit().putString("enabledApps",items).apply();
    }

    public void setEnabledTile(boolean checked) {
        Log.d(TAG,"Tile Activated: "+checked);
        shared.edit().putBoolean("enabled",checked).apply();
    }

    public boolean getEnabledTile() {
        return shared.getBoolean("enabled",true);
    }
}
