package vip.inteltech.gat.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.bugly.crashreport.CrashReport;
import com.ytb.logic.external.CustomLandingTitleStyle;

import org.apache.commons.lang3.math.NumberUtils;
import org.simple.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.http.loader.LoaderFactory;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import vip.inteltech.coolbaby.BuildConfig;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.chatutil.ChatMsgEntity;
import vip.inteltech.gat.comm.CommHandler;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.db.ChatMsgDao;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.FriendDao;
import vip.inteltech.gat.db.HealthDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.fix.FastJSONObjectLoader;
import vip.inteltech.gat.fix.HttpManagerImplOK;
import vip.inteltech.gat.inter.HttpCallback;
import vip.inteltech.gat.inter.HttpCallbackOK;
import vip.inteltech.gat.listener.DBOperationListener;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.FriendModel;
import vip.inteltech.gat.model.GeoFenceModel;
import vip.inteltech.gat.model.HealthModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;

public class AppContext extends MultiDexApplication {
    private static AppContext mInstance = null;
    private static Context context;
    private static Map<String, WatchModel> mWatchMap;
    private static WatchModel mWatchModel;
    private static List<ContactModel> mContactList;
    private static List<FriendModel> mFriendList;
    private static WatchSetModel mWatchSetModel;
    private static WatchStateModel mWatchStateModel;
    private static List<ChatMsgEntity> ChatMsgList;
    private static List<GeoFenceModel> mGeoFenceList;
    private static List<Activity> activities = new ArrayList<Activity>();
    private static HealthModel mHealthModel;
    private static boolean isShow;
    private static boolean isChatShow = false;
    private static boolean isSMSShow = false;
    private static boolean isMsgRecordShow = false;
    private static boolean isAddressBookShow = false;
    private static boolean isFriendListShow = false;
    private static boolean isAlbumShow = false;
    private static int firmwareFeature;
    private static String DeviceSvn;
    public static boolean dialogShown = false;

    private static EventBus eventBus;
    public static DbManager db = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();

        x.Ext.init(this);
        x.Ext.setHttpManager(HttpManagerImplOK.INSTANCE);
        x.Ext.setDebug(true);

        eventBus = EventBus.getDefault();

        LoaderFactory.registerLoader(JSONObject.class, new FastJSONObjectLoader());

        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_APPID, BuildConfig.DEBUG);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.head_empty)
                .showImageOnFail(R.drawable.head_empty).cacheInMemory(true)
                .cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);

        CommHandler.getHandler();

        String dbName = Utils.getMetaData(this, "DB_NAME");
        int dbVersion = CommUtil.toInteger(Utils.getMetaData(this, "DB_VERSION"), 0);
        File dbFile = Utils.copyAttachedDatabase(this, dbName);

        DBOperationListener dbOperationListener = new DBOperationListener();
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName(dbName)
                .setDbDir(dbFile.getParentFile())
                .setDbVersion(dbVersion)
                .setDbUpgradeListener(dbOperationListener);
        db = x.getDb(daoConfig);
        dbOperationListener.checkIfTableCreated(this, db);

        initServerUrl();

        String curProcessName = getCurProcessName(this);

        if (!TextUtils.isEmpty(curProcessName) && curProcessName.equals(getPackageName())) {
            com.ytb.logic.CMain.setAppId(this, Contents.APP_ID, Contents.APP_SECRET);
        }

    }

    private void initServerUrl() {
        HttpUtil.INSTANCE.get(Contents.TEST_URL, null, new HttpCallback<String>() {
            @Override
            public void onSuccess(String res) {
                if (!res.contains("ClientClient")) {
                    changeServerUrl();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                changeServerUrl();
            }

            private void changeServerUrl() {
                String[] mainUrl = BuildConfig.SERVER_URL.split(",");
                boolean isMain = Contents.TEST_URL.equals(mainUrl[0]);
                Contents.SERVER_INFO = isMain ? BuildConfig.SERVER_URL_BAK.split(",") : mainUrl;
            }

        });
    }

    public static Context getContext() {
        return context;
    }

    public static AppContext getInstance() {
        return mInstance;
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public void setWatchMap(Map<String, WatchModel> mWatchMap) {
        AppContext.mWatchMap = mWatchMap;
    }

    public Map<String, WatchModel> getWatchMap() {
        if (mWatchMap == null) {
            WatchDao mWatchDao = new WatchDao(this);
            mWatchMap = mWatchDao.getWatchMap();
        }
        return mWatchMap;
    }

    public WatchModel getmWatchModel() {
        if (mWatchModel == null) {
            WatchDao mWatchDao = new WatchDao(this);
            mWatchModel = mWatchDao.getWatch(AppData.GetInstance(context).getSelectDeviceId());
        }
        return mWatchModel;
    }

    public void setmWatchModel(WatchModel mWatchModel) {
        AppContext.mWatchModel = mWatchModel;
        String fm = mWatchModel.getCurrentFirmware();
        if (TextUtils.isEmpty(fm)) {
            firmwareFeature = 0;
        } else {
            String[] versions = fm.split("\\.");
            if (versions.length > 2 && NumberUtils.isDigits(versions[2])) {
                firmwareFeature = CommUtil.toInteger(versions[2]);
            } else if (versions.length > 1 && NumberUtils.isDigits(versions[1])) {
                firmwareFeature = CommUtil.toInteger(versions[1]);
            }
        }
    }

    public void setContactList(List<ContactModel> mContactList) {
        AppContext.mContactList = mContactList;
    }

    public List<ContactModel> getContactList() {
        if (mContactList == null) {
            ContactDao mContactDao = new ContactDao(this);
            mContactList = mContactDao.getContactList(AppData.GetInstance(this).getSelectDeviceId());
        }
        return mContactList;
    }

    public void setFriendList(List<FriendModel> mFriendList) {
        AppContext.mFriendList = mFriendList;
    }

    public List<FriendModel> getFriendList() {
        if (mFriendList == null) {
            FriendDao mFriendDao = new FriendDao(this);
            mFriendList = mFriendDao.getWatchFriendList(AppData.GetInstance(this).getSelectDeviceId());
        }
        return mFriendList;
    }

    public void setSelectWatchSet(WatchSetModel mWatchSetModel) {
        AppContext.mWatchSetModel = mWatchSetModel;
    }

    public WatchSetModel getSelectWatchSet() {
        if (mWatchSetModel == null) {
            WatchSetDao mWatchSetDao = new WatchSetDao(this);
            mWatchSetModel = mWatchSetDao.getWatchSet(AppData.GetInstance(this).getSelectDeviceId());
        }
        return mWatchSetModel;
    }

    public WatchStateModel getmWatchStateModel() {
        if (mWatchStateModel == null) {
            WatchStateDao mWatchStateDao = new WatchStateDao(this);
            mWatchStateModel = mWatchStateDao.getWatchState(AppData.GetInstance(this).getSelectDeviceId());
        }
        return mWatchStateModel;
    }

    public void setmWatchStateModel(WatchStateModel mWatchStateModel) {
        AppContext.mWatchStateModel = mWatchStateModel;

        HealthDao mHealthDao = new HealthDao(this);
        mHealthModel = mHealthDao.getHealth(AppData.GetInstance(this).getSelectDeviceId());
    }

    public List<ChatMsgEntity> getChatMsgList() {
        //if(ChatMsgList == null){
        ChatMsgDao mChatMsgDao = new ChatMsgDao(this);
        List<ChatMsgEntity> allDataArrays = mChatMsgDao.getChatMsgLists(AppData.GetInstance(this).getSelectDeviceId(), AppData.GetInstance(this).getUserId());
        if (allDataArrays.size() > Contents.CHATMSGINITIAL) {
            Log.v("kkk", "allDataArrays size = " + allDataArrays.size() + " " + allDataArrays.get(allDataArrays.size() - 1));
            ChatMsgList = allDataArrays.subList(allDataArrays.size() - Contents.CHATMSGINITIAL, allDataArrays.size());
        } else {
            ChatMsgList = mChatMsgDao.getChatMsgLists(AppData.GetInstance(this).getSelectDeviceId(), AppData.GetInstance(this).getUserId());
        }
        //	}
        return ChatMsgList;
    }

    public void setChatMsgList(List<ChatMsgEntity> chatMsgList) {
        AppContext.ChatMsgList = chatMsgList;
    }

    public List<GeoFenceModel> getmGeoFenceList() {
        return mGeoFenceList;
    }

    public void setmGeoFenceList(List<GeoFenceModel> mGeoFenceList) {
        AppContext.mGeoFenceList = mGeoFenceList;
    }

    public HealthModel getSelectHealth() {
        if (mHealthModel == null) {
            HealthDao mHealthDao = new HealthDao(this);
            mHealthModel = mHealthDao.getHealth(AppData.GetInstance(this).getSelectDeviceId());
        }
        return mHealthModel;
    }

    public int getActivitiesSize() {
        return activities.size();
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public Activity getActivity() {
        return activities.get(getActivitiesSize() - 1);
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        AppContext.isShow = isShow;
    }

    public boolean isChatShow() {
        return isChatShow;
    }

    public void setChatShow(boolean isChatShow) {
        AppContext.isChatShow = isChatShow;
    }

    public boolean isSMSShow() {
        return isSMSShow;
    }

    public void setSMSShow(boolean isSMSShow) {
        AppContext.isSMSShow = isSMSShow;
    }

    public boolean isMsgRecordShow() {
        return isMsgRecordShow;
    }

    public void setMsgRecordShow(boolean isMsgRecordShow) {
        AppContext.isMsgRecordShow = isMsgRecordShow;
    }

    public boolean isAddressBookShow() {
        return isAddressBookShow;
    }

    public void setAddressBookShow(boolean isAddressBookShow) {
        AppContext.isAddressBookShow = isAddressBookShow;
    }

    public boolean isFriendListShow() {
        return isFriendListShow;
    }

    public void setFriendListShow(boolean isFriendListShow) {
        AppContext.isFriendListShow = isFriendListShow;
    }

    public boolean isAlbumShow() {
        return isAlbumShow;
    }

    public void setAlbumShow(boolean isAlbumShow) {
        AppContext.isAlbumShow = isAlbumShow;
    }

    //右起第3位
    public boolean isSupportGsensor() {
        return (firmwareFeature & 0x04) != 0;
    }

    //右起第4位
    public boolean isSupportProximity() {
        return (firmwareFeature & 0x08) != 0;
    }

    //右起第6位
    public boolean isSupportHeartrate() {
        return (firmwareFeature & 0x20) != 0;
    }

    //右起第8位
    public boolean isSupportCamera() {
        return (firmwareFeature & 0x80) != 0;
    }

    //右起第9位
    public boolean isSupportLcd() {
        return (firmwareFeature & 0x100) != 0;
    }

    //右起第2位
    public boolean isSupportGps() {
        return (firmwareFeature & 0x02) != 0;
    }

    //右起第10位
    public boolean isSupportWifi() {
        return (firmwareFeature & 0x200) != 0;
    }

    //右起第11位
    public boolean isSupportBT3() {
        return (firmwareFeature & 0x400) != 0;
    }

    //右起第12位
    public boolean isSupportVideoSoundRecording() {
        return (firmwareFeature >>> 11 & 0x01) != 0;
    }

    public String getDeviceSvn() {
        if (TextUtils.isEmpty(DeviceSvn) && !TextUtils.isEmpty(getmWatchModel().getCurrentFirmware())) {
            String[] strs = getmWatchModel().getCurrentFirmware().split("\\.");
            if (strs.length > 3) {
                DeviceSvn = strs[3];
            } else {
                DeviceSvn = "";
            }
        } else {
            DeviceSvn = "";
        }
        return DeviceSvn;
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}