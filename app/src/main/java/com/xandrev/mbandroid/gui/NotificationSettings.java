package com.xandrev.mbandroid.gui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xandrev.mbandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationSettings extends Activity {

    private static final String TAG = "NotificationSettings";
    private SharedPreferences shared;
    private Map<String,String> packageAppMap;
    private Map<String,String> appMap;

    private List<String> getApplicationNames(){
        List<String> out = new ArrayList<>();
        final PackageManager pm = getPackageManager();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        shared = getSharedPreferences("notificationSettings", MODE_APPEND);


        final ListView lv = (ListView) findViewById(R.id.listView);
        final List<String> your_array_list =getApplicationNames();
        Log.i(TAG,"app map size: "+appMap.size());
        Log.i(TAG, "package map size: " + packageAppMap.size());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                your_array_list );
        Log.i(TAG, "Recovering application list: " + your_array_list.size());
        String enabledAppsStr = shared.getString("enabledApps", null);
        Log.i(TAG, "Recovering enabled apps list: " + enabledAppsStr);
        List<String> list = null;
        if(enabledAppsStr != null) {
            String[] apps = enabledAppsStr.split(",");
            list = Arrays.asList(apps);
        }

        lv.setAdapter(arrayAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if(list != null) {
            Log.i(TAG,"Recovering enabled application list: "+list.size());
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                String value = arrayAdapter.getItem(i);
                Log.d(TAG,"Recovered value:"+value);
                String transformedValue = packageAppMap.get(value);
                Log.d(TAG,"Transformed value: "+transformedValue);
                if (list.contains(transformedValue)) {
                    lv.setItemChecked(i, true);
                }
            }
        }else{
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                lv.setItemChecked(i, true);
            }
        }

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String items = "";
                SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
                Log.i(TAG, "Recovering checked items..");
                if(checkedItems != null){
                    Log.i(TAG,"Total of checked items: "+checkedItems.size());
                    for(int i=0;i<checkedItems.size();i++){
                        int idx = checkedItems.keyAt(i);
                        Log.i(TAG, "Adding new value: "+lv.getItemAtPosition(idx));
                        if(checkedItems.get(idx)){
                            Log.i(TAG, "Added new value: "+lv.getItemAtPosition(idx));
                            items+=packageAppMap.get(lv.getItemAtPosition(idx))+",";
                        }
                    }
                }
                Log.i(TAG, "Enabled apps: "+items);
                shared.edit().putString("enabledApps",items).apply();
            }
        });
    }

}
