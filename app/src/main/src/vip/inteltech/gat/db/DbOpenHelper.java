package vip.inteltech.gat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import vip.inteltech.gat.utils.AppContext;


public class DbOpenHelper {
    private static DbOpenHelper instance;

    private DbOpenHelper(Context context) {
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context);
        }
        return instance;
    }

    public SQLiteDatabase getWritableDatabase(){
        return AppContext.db.getDatabase();
    }

    public SQLiteDatabase getReadableDatabase(){
        return AppContext.db.getDatabase();
    }
}