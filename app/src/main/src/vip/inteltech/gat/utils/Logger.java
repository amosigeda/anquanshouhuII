package vip.inteltech.gat.utils;

import android.util.Log;

/**
 * 自定义日志类，用于解决某些Android机型从底层屏蔽debug层次日志问题
 * <p>
 * Created by Steven Hua on 2016/12/15.
 */

public final class Logger {
    private static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean dg) {
        debug = dg;
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.i(tag, msg, tr);
        }
    }
}
