package com.xandrev.mbandroid.gui;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.xandrev.mbandroid.R;

import java.util.ArrayList;
import java.util.List;

public class MailSettings extends Activity {

    private static final String TAG = "MailSettings";
    private com.xandrev.mbandroid.settings.mail.MailSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        settings = com.xandrev.mbandroid.settings.mail.MailSettings.getInstance(this);
        final ListView lv = (ListView) findViewById(R.id.listView);
        final List<String> your_array_list =settings.getInstalledMailApps();

        final CheckBox cbox = (CheckBox) findViewById(R.id.checkBox);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, your_array_list );
        Log.i(TAG, "Recovering application list: " + your_array_list.size());
        lv.setAdapter(arrayAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        cbox.setChecked(settings.getEnabledTile());

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