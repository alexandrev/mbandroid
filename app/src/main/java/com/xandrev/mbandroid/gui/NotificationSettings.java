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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.xandrev.mbandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationSettings extends Activity {

    private static final String TAG = "NotificationSettings";
    private com.xandrev.mbandroid.settings.notifications.NotificationSettings settings;
    private Button btnCheckAll;
    private Button btnUncheckAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = com.xandrev.mbandroid.settings.notifications.NotificationSettings.getInstance(this);
        setContentView(R.layout.activity_notification_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        btnCheckAll = (Button) findViewById(R.id.btnCheckAll);
        btnUncheckAll = (Button) findViewById(R.id.btnUncheckAll);


        final ListView lv = (ListView) findViewById(R.id.listView);
        final List<String> your_array_list =settings.getApplicationNames();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, your_array_list );
        Log.i(TAG, "Recovering application list: " + your_array_list.size());
        lv.setAdapter(arrayAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final CheckBox cbox = (CheckBox) findViewById(R.id.checkBox);

        List list = settings.getEnabledApps();
        if(list != null) {
            Log.i(TAG,"Recovering enabled application list: "+list.size());
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                String value = arrayAdapter.getItem(i);
                lv.setItemChecked(i,settings.isEnabledApplication(value));
            }
        }else{
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                lv.setItemChecked(i, true);
            }
        }

        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayAdapter.getCount(); i++) {
                    lv.setItemChecked(i, true);
                }
            }
        });

        btnUncheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayAdapter.getCount(); i++) {
                    lv.setItemChecked(i, false);
                }
            }
        });


        cbox.setChecked(settings.getEnabledTile());

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
                Log.i(TAG, "Recovering checked items..");
                List<String> newEnabledAppList = new ArrayList<String>();
                if(checkedItems != null){
                    Log.i(TAG,"Total of checked items: "+checkedItems.size());
                    for(int i=0;i<checkedItems.size();i++){
                        int idx = checkedItems.keyAt(i);
                        Log.i(TAG, "Adding new value: "+lv.getItemAtPosition(idx));
                        if(checkedItems.get(idx)){
                            Log.i(TAG, "Added new value: "+lv.getItemAtPosition(idx));
                            newEnabledAppList.add((String)lv.getItemAtPosition(idx));
                        }
                    }
                }
                settings.setEnabledApps(newEnabledAppList);
                settings.setEnabledTile(cbox.isChecked());

            }
        });
    }

}
