package com.xandrev.mbandroid.gui;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import com.xandrev.mbandroid.R;

public class LogViewerActivity extends Activity {

    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tView = (TextView) findViewById(R.id.textView8);

        String txt = getIntent().getStringExtra("log");
        tView.setText(txt);


    }

}
