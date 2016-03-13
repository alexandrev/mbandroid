package com.xandrev.mbandroid.gui.logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.gui.logger.helper.NotificationLogAdapter;
import com.xandrev.mbandroid.services.NotificationLogger;

public class NotificationLoggerActivity extends Activity {

    private NotificationLogger logger;
    private Button cleanLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_logger);

        logger = NotificationLogger.getInstance();
        refreshLogViewer();
        cleanLogs = (Button)findViewById(R.id.cleanNotificationLogs);
        cleanLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.clean();
                refreshLogViewer();
            }


        });

    }

    @Override
    protected void onResume() {
        refreshLogViewer();
        super.onResume();
    }

    private void refreshLogViewer() {
        ListView tl=(ListView)findViewById(R.id.listView3);
        if(logger.getItems() != null) {
            tl.setAdapter(new NotificationLogAdapter(this, logger.getItems()));
        }
    }
}
