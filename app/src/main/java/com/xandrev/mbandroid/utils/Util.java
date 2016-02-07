package com.xandrev.mbandroid.utils;

import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by alexa on 12/11/2015.
 */
public class Util {

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String getTextFromNotification(StatusBarNotification notification){
        String text = "";
        if(notification != null) {
            Bundle extras = notification.getNotification().extras;
            if (extras != null) {
                CharSequence notifText = extras.getCharSequence("android.text");
                if (notifText != null) {
                    text = notifText.toString();
                    notifText = extras.getCharSequence("android.bigText");
                    if (notifText != null) {
                        text = notifText.toString();
                    }
                }
            }
        }
        return text;
    }

    public static String getTitleFromNotification(StatusBarNotification notification){
        String text = "";
        if(notification != null) {
            Bundle extras = notification.getNotification().extras;
            if (extras != null) {
               text = extras.getString("android.title");
            }
        }
        return text;
    }
}
