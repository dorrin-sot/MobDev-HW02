package com.mobdev.locationapp;

import android.util.Log;

public class Logger {
    public static void d(String msg) {
        Log.d(tag(), msg);
    }

    public static void v(String msg) {
        Log.v(tag(), msg);
    }

    public static void i(String msg) {
        Log.i(tag(), msg);
    }

    public static void w(String msg) {
        Log.w(tag(), msg);
    }

    public static void e(String msg) {
        Log.e(tag(), msg);
    }

    private static String tag() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        String[] className = stackTraceElement.getClassName().split("\\.");
        return className[className.length - 1] + "." + stackTraceElement.getMethodName();
    }
}
