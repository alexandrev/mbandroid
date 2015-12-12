package com.xandrev.mbandroid.utils;

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
}
