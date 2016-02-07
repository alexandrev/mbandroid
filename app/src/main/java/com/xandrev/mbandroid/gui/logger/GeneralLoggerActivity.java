package com.xandrev.mbandroid.gui.logger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.xandrev.mbandroid.gui.logger.helper.GeneralLogAdapter;

import com.xandrev.mbandroid.R;

public class GeneralLoggerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_logger);
        ListView tl=(ListView)findViewById(R.id.listView2);


        String txt = getIntent().getStringExtra("log");
        String[] values = txt.split("\n");
        tl.setAdapter(new GeneralLogAdapter(this, values));



    }
}
