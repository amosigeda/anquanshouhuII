package vip.inteltech.gat.listener;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;

import vip.inteltech.gat.db.AddressDao;
import vip.inteltech.gat.db.AlbumDao;
import vip.inteltech.gat.db.ChatMsgDao;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.FriendDao;
import vip.inteltech.gat.db.GeoFenceDao;
import vip.inteltech.gat.db.HealthDao;
import vip.inteltech.gat.db.MsgRecordDao;
import vip.inteltech.gat.db.SMSDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.Utils;


/**
 * Created by HH
 * Date: 2015/7/15 0015
 * Time: 上午 11:55
 */
public class DBOperationListener implements DbManager.DbUpgradeListener {

    public void checkIfTableCreated(Context context, DbManager dbm) {
        final String key = "FIRST_OPEN_DB";
        Boolean isFirstOpen = Utils.get(context, key);
        if (isFirstOpen != null && !isFirstOpen) {
            return;
        }
        SQLiteDatabase db = dbm.getDatabase();
        try {
            db.execSQL(WATCH_TABLE_CREATE);
            db.execSQL(CONTACT_TABLE_CREATE);
            db.execSQL(WATCHSET_TABLE_CREATE);
            db.execSQL(WATCHSTATE_TABLE_CREATE);
            db.execSQL(CHATMSG_TABLE_CREATE);
            db.execSQL(MSGRECORD_TABLE_CREATE);
            db.execSQL(SMS_TABLE_CREATE);
            db.execSQL(GEOFENCE_TABLE_CREATE);
            db.execSQL(ALBUM_TABLE_CREATE);
            db.execSQL(HEALTH_TABLE_CREATE);
            db.execSQL(ADDRESS_TABLE_CREATE);
            db.execSQL(FRIEND_TABLE_CREATE);
        } catch (SQLException e) {
            //
        } finally {
            Utils.save(context, key, false);
        }
    }

    @Override
    public void onUpgrade(DbManager dbm, int oldVersion, int newVersion) {
        checkIfTableCreated(AppContext.getContext(), dbm);
        SQLiteDatabase db = dbm.getDatabase();
        switch (oldVersion) {
            case 1:
                try {
                    db.execSQL(ALBUM_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 2:
                try {
                    upgradeWatchSetTables(db);
                    upgradeWatchStateTables(db);
                    upgradeChatMsgTables(db);
                    db.execSQL(HEALTH_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 3:
            case 4:
                try {
                    db.execSQL("DROP TABLE " + WatchSetDao.TABLE_NAME + ";");
                    db.execSQL(WATCHSET_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 5:
                try {
                    db.execSQL("DROP TABLE " + WatchDao.TABLE_NAME + ";");
                    db.execSQL(WATCH_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 6:
                try {
                    db.execSQL("DROP TABLE " + HealthDao.TABLE_NAME + ";");
                    db.execSQL(HEALTH_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 7:
                try {
                    db.execSQL(ADDRESS_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 8:
                try {
                    db.execSQL(FRIEND_TABLE_CREATE);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 9:
                try {
                    db.execSQL(UPGRADE_WATCH_MODEL);
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                try {
                    db.execSQL(UPGRADE_ALBUM_MODEL);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            case 15:
                updateFriendTable(db);
        }
    }

    private void updateFriendTable(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            String sql = "ALTER TABLE friends RENAME TO _friends_old";
            db.execSQL(sql);
            sql = "CREATE TABLE friends (wId INTEGER,deviceFriendId INTEGER NOT NULL,relationShip TEXT,friendDeviceId INTEGER,name TEXT,phone TEXT,PRIMARY KEY (deviceFriendId))";
            db.execSQL(sql);
            sql = "INSERT INTO friends (wId, deviceFriendId, relationShip, friendDeviceId, name, phone) SELECT wId, deviceFriendId, relationShip, friendDeviceId, name, phone FROM _friends_old";
            db.execSQL(sql);
            sql = "DROP TABLE _friends_old";
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void upgradeWatchSetTables(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            String sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_WEEKALARM1 + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_WEEKALARM2 + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_WEEKALARM3 + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_ALARM1 + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_ALARM2 + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_ALARM3 + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_LOCATIONMODE + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_LOCATIONTIME + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchSetDao.TABLE_NAME + " ADD " + WatchSetDao.COLUMN_NAME_FLOWERNUMBER + " TEXT";
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void upgradeWatchStateTables(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            String sql = "ALTER TABLE " + WatchStateDao.TABLE_NAME + " ADD " + WatchStateDao.COLUMN_NAME_STEP + " TEXT";
            db.execSQL(sql);
            sql = "ALTER TABLE " + WatchStateDao.TABLE_NAME + " ADD " + WatchStateDao.COLUMN_NAME_HEALTH + " TEXT";
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void upgradeChatMsgTables(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            String sql = "ALTER TABLE " + ChatMsgDao.TABLE_NAME + " ADD " + ChatMsgDao.COLUMN_NAME_MSGTYPE + " TEXT";
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    private static final String WATCH_TABLE_CREATE = "CREATE TABLE "
            + WatchDao.TABLE_NAME + " ("
            + WatchDao.COLUMN_NAME_USERID + " INTEGER, "
            + WatchDao.COLUMN_NAME_NAME + " TEXT, "
            + WatchDao.COLUMN_NAME_AVATAR + " TEXT, "
            + WatchDao.COLUMN_NAME_PHONE + " TEXT, "
            + WatchDao.COLUMN_NAME_CORNET + " TEXT, "
            + WatchDao.COLUMN_NAME_GENDER + " TEXT, "
            + WatchDao.COLUMN_NAME_BIRTHDAY + " TEXT, "
            + WatchDao.COLUMN_NAME_GRADE + " TEXT, "
            + WatchDao.COLUMN_NAME_SCHOOLADRESS + " TEXT, "
            + WatchDao.COLUMN_NAME_SCHOOLLAT + " DOUBLE, "
            + WatchDao.COLUMN_NAME_SCHOOLLNG + " DOUBLE, "
            + WatchDao.COLUMN_NAME_HOMEADRESS + " TEXT, "
            + WatchDao.COLUMN_NAME_HOMELAT + " DOUBLE, "
            + WatchDao.COLUMN_NAME_HOMELNG + " DOUBLE, "
            + WatchDao.COLUMN_NAME_ACTIVEDATE + " TEXT, "
            + WatchDao.COLUMN_NAME_LASTESTTIME + " TEXT, "
            + WatchDao.COLUMN_NAME_SETVERSIONNO + " TEXT, "
            + WatchDao.COLUMN_NAME_CONTACTVERSIONNO + " TEXT, "
            + WatchDao.COLUMN_NAME_OPERATORTYPE + " TEXT, "
            + WatchDao.COLUMN_NAME_SMSNUMBER + " TEXT, "
            + WatchDao.COLUMN_NAME_SMSBALANCEKEY + " TEXT, "
            + WatchDao.COLUMN_NAME_SMSFLOWKEY + " TEXT, "
            + WatchDao.COLUMN_NAME_MODEL + " TEXT, "
            + WatchDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + WatchDao.COLUMN_NAME_BINDNUMBER + " TEXT, "
            + WatchDao.COLUMN_NAME_CURENTFIRMWARE + " TEXT, "
            + WatchDao.COLUMN_NAME_FIRMWARE + " TEXT, "
            + WatchDao.COLUMN_NAME_HIREEXPIREDATE + " TEXT, "
            + WatchDao.COLUMN_NAME_HIRESTARTDATE + " TEXT, "
            + WatchDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + WatchDao.COLUMN_NAME_SERIALNUMBER + " TEXT, "
            + WatchDao.COLUMN_NAME_PASSWORD + " TEXT, "
            + WatchDao.COLUMN_NAME_ISGUARD + " TEXT, "
            + WatchDao.COLUMN_NAME_DEVICETYPE + " TEXT, "
            + WatchDao.COLUMN_NAME_CLOUDPLATFORM + " INTEGER, "
            + WatchDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    private static final String CONTACT_TABLE_CREATE = "CREATE TABLE "
            + ContactDao.TABLE_NAME + " ("
            + ContactDao.COLUMN_NAME_FROMID + " INTEGER, "
            + ContactDao.COLUMN_NAME_OBJECTID + " TEXT, "
            + ContactDao.COLUMN_NAME_RELATIONSHIP + " TEXT, "
            + ContactDao.COLUMN_NAME_AVATAR + " TEXT, "
            + ContactDao.COLUMN_NAME_AVATARURL + " TEXT, "
            + ContactDao.COLUMN_NAME_PHONE + " TEXT, "
            + ContactDao.COLUMN_NAME_CORNET + " TEXT, "
            + ContactDao.COLUMN_NAME_TYPE + " INTEGER, "
            + ContactDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    private static final String WATCHSET_TABLE_CREATE = "CREATE TABLE "
            + WatchSetDao.TABLE_NAME + " ("
            + WatchSetDao.COLUMN_NAME_AUTOANSWER + " TEXT, "
            + WatchSetDao.COLUMN_NAME_REPORTLOCATION + " TEXT, "
            + WatchSetDao.COLUMN_NAME_SOMATOANSWER + " TEXT, "
            + WatchSetDao.COLUMN_NAME_RESERVEDPOWER + " TEXT, "
            + WatchSetDao.COLUMN_NAME_CLASSDISABLED + " TEXT, "
            + WatchSetDao.COLUMN_NAME_TIMESWITCH + " TEXT, "
            + WatchSetDao.COLUMN_NAME_REFUSEDSTRANGER + " TEXT, "
            + WatchSetDao.COLUMN_NAME_WATCHOFFALARM + " TEXT, "
            + WatchSetDao.COLUMN_NAME_CALLSOUND + " TEXT, "
            + WatchSetDao.COLUMN_NAME_CALLVIBRATE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_MSGSOUND + " TEXT, "
            + WatchSetDao.COLUMN_NAME_MSGVIBRATE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_CLASSDISABLEDA + " TEXT, "
            + WatchSetDao.COLUMN_NAME_CLASSDISABLEDB + " TEXT, "
            + WatchSetDao.COLUMN_NAME_WEEKDISABLED + " TEXT, "
            + WatchSetDao.COLUMN_NAME_TIMEROPEN + " TEXT, "
            + WatchSetDao.COLUMN_NAME_TIMERCLOSE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_BRIGHTSCREEN + " TEXT, "
            + WatchSetDao.COLUMN_NAME_WEEKALARM1 + " TEXT, "
            + WatchSetDao.COLUMN_NAME_WEEKALARM2 + " TEXT, "
            + WatchSetDao.COLUMN_NAME_WEEKALARM3 + " TEXT, "
            + WatchSetDao.COLUMN_NAME_ALARM1 + " TEXT, "
            + WatchSetDao.COLUMN_NAME_ALARM2 + " TEXT, "
            + WatchSetDao.COLUMN_NAME_ALARM3 + " TEXT, "
            + WatchSetDao.COLUMN_NAME_LOCATIONMODE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_LOCATIONTIME + " TEXT, "
            + WatchSetDao.COLUMN_NAME_FLOWERNUMBER + " TEXT, "
            + WatchSetDao.COLUMN_NAME_LANGUAGE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_TIMEZONE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + WatchSetDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + WatchSetDao.COLUMN_NAME_VERSIONNUMBER + " TEXT, "
            + WatchSetDao.COLUMN_NAME_SLEEPCALCULATE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_STEPCALCULATE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_HRCALCULATE + " TEXT, "
            + WatchSetDao.COLUMN_NAME_SOSMSGSWITCH + " TEXT, "
            + WatchSetDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    private static final String WATCHSTATE_TABLE_CREATE = "CREATE TABLE "
            + WatchStateDao.TABLE_NAME + " ("
            + WatchStateDao.COLUMN_NAME_ALTITUDE + " DOUBLE, "
            + WatchStateDao.COLUMN_NAME_LATITUDE + " DOUBLE, "
            + WatchStateDao.COLUMN_NAME_LONGITUDE + " DOUBLE, "
            + WatchStateDao.COLUMN_NAME_COURSE + " TEXT, "
            + WatchStateDao.COLUMN_NAME_ELECTRICITY + " TEXT, "
            + WatchStateDao.COLUMN_NAME_STEP + " TEXT, "
            + WatchStateDao.COLUMN_NAME_HEALTH + " TEXT, "
            + WatchStateDao.COLUMN_NAME_ONLINE + " TEXT, "
            + WatchStateDao.COLUMN_NAME_SPEED + " TEXT, "
            + WatchStateDao.COLUMN_NAME_SATELLITENUMBER + " TEXT, "
            + WatchStateDao.COLUMN_NAME_SOCKETID + " TEXT, "
            + WatchStateDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + WatchStateDao.COLUMN_NAME_SERVERTIME + " TEXT, "
            + WatchStateDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + WatchStateDao.COLUMN_NAME_DEVICETIME + " TEXT, "
            + WatchStateDao.COLUMN_NAME_LOCATIONTYPE + " TEXT, "
            + WatchStateDao.COLUMN_NAME_LBS + " TEXT, "
            + WatchStateDao.COLUMN_NAME_GSM + " TEXT, "
            + WatchStateDao.COLUMN_NAME_WIFI + " TEXT, "
            + WatchStateDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    private static final String CHATMSG_TABLE_CREATE = "CREATE TABLE "
            + ChatMsgDao.TABLE_NAME + " ("
            + ChatMsgDao.COLUMN_NAME_DEVICEID + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_USERID + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_STATE + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_TOTALPACKAGE + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_CURRENTPACKAGE + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_TYPE + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_OBJECTID + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_MARK + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_PATH + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_LENGTH + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_MSGTYPE + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + ChatMsgDao.COLUMN_NAME_ISREAD + " INTEGER, "
            + ChatMsgDao.COLUMN_NAME_DEVICEVOICEID + " TEXT PRIMARY KEY);";
    private static final String MSGRECORD_TABLE_CREATE = "CREATE TABLE "
            + MsgRecordDao.TABLE_NAME + " ("
            + MsgRecordDao.COLUMN_NAME_TYPE + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_DEVICEID + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_USERID + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_CONTENT + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_MESSAGE + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_ISHANDLE + " TEXT, "
            + MsgRecordDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT);";
    private static final String SMS_TABLE_CREATE = "CREATE TABLE "
            + SMSDao.TABLE_NAME + " ("
            + SMSDao.COLUMN_NAME_DEVICESMSID + " TEXT, "
            + SMSDao.COLUMN_NAME_DEVICEID + " TEXT, "
            + SMSDao.COLUMN_NAME_USERID + " TEXT, "
            + SMSDao.COLUMN_NAME_TYPE + " TEXT, "
            + SMSDao.COLUMN_NAME_STATE + " TEXT, "
            + SMSDao.COLUMN_NAME_PHONE + " TEXT, "
            + SMSDao.COLUMN_NAME_SMS + " TEXT, "
            + SMSDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + SMSDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + SMSDao.COLUMN_NAME_SORT + " INTEGER PRIMARY KEY AUTOINCREMENT);";
    private static final String GEOFENCE_TABLE_CREATE = "CREATE TABLE "
            + GeoFenceDao.TABLE_NAME + " ("
            + GeoFenceDao.COLUMN_NAME_DEVICEID + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_FENCENAME + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_ENTRY + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_EXIT + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_ENABLE + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_DESCRIPTION + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_LAT + " DOUBLE, "
            + GeoFenceDao.COLUMN_NAME_LNG + " DOUBLE, "
            + GeoFenceDao.COLUMN_NAME_RADIUS + " TEXT, "
            + GeoFenceDao.COLUMN_NAME_GEOFENCEID + " TEXT PRIMARY KEY);";
    private static final String ALBUM_TABLE_CREATE = "CREATE TABLE "
            + AlbumDao.TABLE_NAME + " ("
            + AlbumDao.COLUMN_NAME_DEVICEID + " INTEGER, "
            + AlbumDao.COLUMN_NAME_USERID + " INTEGER, "
            + AlbumDao.COLUMN_NAME_SOURCE + " TEXT, "
            + AlbumDao.COLUMN_NAME_DEVICETIME + " TEXT, "
            + AlbumDao.COLUMN_NAME_LATITUDE + " DOUBLE, "
            + AlbumDao.COLUMN_NAME_LONGITUDE + " DOUBLE, "
            + AlbumDao.COLUMN_NAME_ADDRESS + " TEXT, "
            + AlbumDao.COLUMN_NAME_MARK + " TEXT, "
            + AlbumDao.COLUMN_NAME_PATH + " TEXT, "
            + AlbumDao.COLUMN_NAME_THUMB + " TEXT, "
            + AlbumDao.COLUMN_NAME_LOCAL + " TEXT, "
            + AlbumDao.COLUMN_NAME_LENGTH + " TEXT, "
            + AlbumDao.COLUMN_NAME_CREATETIME + " TEXT, "
            + AlbumDao.COLUMN_NAME_UPDATETIME + " TEXT, "
            + AlbumDao.COLUMN_NAME_DEVICEPHOTOID + " TEXT PRIMARY KEY);";
    private static final String HEALTH_TABLE_CREATE = "CREATE TABLE "
            + HealthDao.TABLE_NAME + " ("
            + HealthDao.COLUMN_NAME_ID + " INTEGER, "
            + HealthDao.COLUMN_NAME_PEDOMETER + " TEXT, "
            + HealthDao.COLUMN_NAME_LATITUDE + " DOUBLE, "
            + HealthDao.COLUMN_NAME_LONGITUDE + " DOUBLE, "
            + HealthDao.COLUMN_NAME_DEVICETIME + " TEXT, "
            + HealthDao.COLUMN_NAME_LOCATIONTYPE + " TEXT, "
            + HealthDao.COLUMN_NAME_ADDRESS + " TEXT PRIMARY KEY);";
    private static final String ADDRESS_TABLE_CREATE = "CREATE TABLE "
            + AddressDao.TABLE_NAME + " ("
            + AddressDao.COLUMN_NAME_LATITUDE + " DOUBLE, "
            + AddressDao.COLUMN_NAME_LONGITUDE + " DOUBLE, "
            + AddressDao.COLUMN_NAME_ADDRESS + " TEXT PRIMARY KEY);";
    private static final String FRIEND_TABLE_CREATE = "CREATE TABLE "
            + FriendDao.TABLE_NAME + " ("
            + FriendDao.COLUMN_NAME_ID + " INTEGER, "
            + FriendDao.COLUMN_NAME_DEVICEFRIENDID + " INTEGER PRIMARY KEY, "
            + FriendDao.COLUMN_NAME_RELATIONSHIP + " TEXT, "
            + FriendDao.COLUMN_NAME_FRIENDDEVICEID + " INTEGER, "
            + FriendDao.COLUMN_NAME_NAME + " TEXT, "
            + FriendDao.COLUMN_NAME_PHONE + " TEXT);";

    public static final String UPGRADE_WATCH_MODEL = "ALTER TABLE " + WatchDao.TABLE_NAME + " ADD " + WatchDao.COLUMN_NAME_CLOUDPLATFORM + " INTEGER";
    public static final String UPGRADE_ALBUM_MODEL = "ALTER TABLE " + AlbumDao.TABLE_NAME + " ADD " + AlbumDao.COLUMN_NAME_THUMB + " TEXT";
}
