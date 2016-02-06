package com.xandrev.mbandroid.gui.logger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.xandrev.mbandroid.R;
import com.xandrev.mbandroid.gui.logger.helper.NotificationLogAdapter;
import com.xandrev.mbandroid.services.NotificationLogger;

public class NotificationLoggerActivity extends Activity {

    private NotificationLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_logger);

        logger = NotificationLogger.getInstance();

        ListView tl=(ListView)findViewById(R.id.listView3);
        if(logger.getItems() != null) {
            tl.setAdapter(new NotificationLogAdapter(this, logger.getItems()));
        }

    }
}
