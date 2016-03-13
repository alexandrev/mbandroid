package com.xandrev.mbandroid.gui.logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.xandrev.mbandroid.gui.logger.helper.GeneralLogAdapter;

import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.services.LogViewer;

public class GeneralLoggerActivity extends Activity {


    private Button cleanLogs;
    private LogViewer logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_logger);

        logger = LogViewer.getInstance(this);
        cleanLogs = (Button)findViewById(R.id.cleanLogs);
        cleanLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.clean();
                refreshLogViewer();
            }
        });

        refreshLogViewer();

    }

    private void refreshLogViewer(){
        ListView tl=(ListView)findViewById(R.id.listView2);
        String txt = logger.getLog();
        String[] values = null;
        if(txt != null) {
            values = txt.split("\n");
        }
        tl.setAdapter(new GeneralLogAdapter(this, values));
    }
}
