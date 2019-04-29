package vip.inteltech.gat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.simple.eventbus.Subscriber;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.Login;
import vip.inteltech.gat.Main;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.AppMsg;
import vip.inteltech.gat.viewutils.MToast;

public class MService extends Service implements WebServiceListener {
    private final static String TAG = MService.class.getName();
    private Thread notiThread = null;
    String MessageO = "-1";
    String VoiceO = "-1";
    String SMSO = "-1";
    String PhotoO = "-1";
    private boolean firstLoad = true;
    private MService mContext;
    AlarmManager alarm;
    PendingIntent pi;

    // private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AppContext.getEventBus().register(this);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String action = this.getPackageName() + ".MService";
        Intent intent = new Intent();
        intent.setAction(action);
        pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        registerReceiver(serverBroadcastReceive, filter);

        // PowerManager pm = (PowerManager)
        // getSystemService(Context.POWER_SERVICE);
        // wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
        // MService.class.getName());
        // wakeLock.acquire();
        //
        notiThread = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        Message msg = new Message();
                        msg.what = _GetNotification;
                        mhandler.sendMessage(msg);
                        if (!AppContext.getInstance().isShow()) {
                            Thread.sleep(30 * 1000);
                        } else {
                            Thread.sleep(5 * 1000);
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
    }

    private BroadcastReceiver serverBroadcastReceive = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Message msg = new Message();
            msg.what = _GetNotification;
            mhandler.sendMessage(msg);
        }
    };
    private Handler mhandler = new Handler() { // 更新UI的handler
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                switch (msg.what) {
                    case _GetNotification:
                        // WebServiceUtils.GetDeviceDetail(mContext,_GetDeviceDetail,
                        // "2", mContext);
                        WebService ws = new WebService(mContext, _GetNotification, false, "GetNotification");
                        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
                        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
                        ws.addWebServiceListener(mContext);
                        ws.SyncGet(property);
                        break;
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            notiThread.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                10 * 1000, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        AppContext.getEventBus().unregister(this);
        // if (wakeLock != null) { wakeLock.release(); wakeLock = null; }
        if (notiThread != null)
            notiThread.interrupt();
        alarm.cancel(pi);
        this.unregisterReceiver(serverBroadcastReceive);
        super.onDestroy();
    }

    private void playSoundAndVibrate() {
        if (AppData.GetInstance(mContext).getNotificationVibration()) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {0, 100}; // {间隔时间，震动持续时间}
            vibrator.vibrate(pattern, -1);
        }
        if (AppData.GetInstance(mContext).getNotificationSound()) {
            playSound();
        }
    }

    private void playSound() {
        // TODO Auto-generated method stub
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);

                player.setLooping(false);

                player.prepare();

                player.start();
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

    private void sendNotify(int deviceId, int type, String str) {
        // System.out.println("deviceId:" + deviceId +" type:" + type);
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap()
                .get(String.valueOf(deviceId));
        String watchName = getResources().getString(R.string.noti);
        PendingIntent pi;
        if (mWatchModel != null) {
            watchName = AppContext.getInstance().getWatchMap()
                    .get(String.valueOf(deviceId)).getName();
        }
        Intent intent = new Intent(mContext, Main.class);
        intent.setClass(mContext, Main.class);
        intent.putExtra("type", String.valueOf(type));
        intent.putExtra("deviceId", String.valueOf(deviceId));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pi = PendingIntent.getActivity(this, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		Bitmap btp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.noti_big_icon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.DEFAULT_BLANK);
        builder.setContentTitle(str)
                .setContentIntent(pi)
                .setTicker(str)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.noti_icon);

        int defaultsNotify = 0;
        if (!havePlaySoundAndVibrate) {
            if (AppData.GetInstance(mContext).getNotificationVibration()) {
                defaultsNotify |= Notification.DEFAULT_VIBRATE;
            }
            if (AppData.GetInstance(mContext).getNotificationSound()) {
                defaultsNotify |= Notification.DEFAULT_SOUND;
            }
            havePlaySoundAndVibrate = true;
        }
        builder.setDefaults(defaultsNotify);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, builder.build());
        }
    }

    private void getWatchList() {
        WebService ws = new WebService(mContext, _GetDeviceList, getResources()
                .getString(R.string.loading_watch_list), "GetDeviceList");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData
                .GetInstance(this).getLoginId()));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void showAppMsg(AppMsg.Style style, String str, int type) {
        if (!isShowAppMsg[type]) {
            AppMsg appMsg = AppMsg.makeText(AppContext.getInstance()
                    .getActivity(), str, style);
            appMsg.show();
            if (!havePlaySoundAndVibrate) {
                playSoundAndVibrate();
                havePlaySoundAndVibrate = true;
            }
            isShowAppMsg[type] = true;
        }
    }

    private final int _GetNotification = 0;
    private final int _GetDeviceDetail = 3;
    private final int _GetDeviceSet = 4;
    private final int _GetDeviceState = 5;
    private final int _GetDeviceContact = 6;
    private final int _GetDeviceList = 7;
    private boolean havePlaySoundAndVibrate = false;
    private boolean[] isSendBroadcast;
    private boolean[] isShowAppMsg;
    private int Chat = 0, AskBind = 1, AgreeBind = 2, RefuseBind = 3,
            DeviceUpdata = 4, DeviceSet = 5, AddressBook = 6, SMS = 7,
            Unbind = 8, BabyInfo = 9, Alarm = 10, TakePhoto = 11,
            SchoolDefend = 12;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        if (StringUtils.isBlank(result)) {
            Log.d(TAG, "=======Received NULL result!=======");
            return;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (id == _GetNotification) {
                // 消息Type:1 语音信息，2发信息给管理员关联确认，3管理员确认关联，4管理员拒绝关联
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    havePlaySoundAndVibrate = false;
                    // 1成功
                    JSONArray Notificationarr = jsonObject.getJSONArray("Notification");
                    isSendBroadcast = new boolean[]{false, false, false,
                            false, false, false, false, false, false, false,
                            false, false, false};
                    isShowAppMsg = new boolean[]{false, false, false, false,
                            false, false, false, false, false, false, false,
                            false, false};
                    for (int i = 0; i < Notificationarr.size(); i++) {
                        JSONObject item = Notificationarr.getJSONObject(i);
                        int type = item.getIntValue("Type");

                        if (type == 1) {
                            // 语音
                            int deviceId = item.getIntValue("DeviceID");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, getResources()
                                        .getString(R.string.get_chat));
                            } else {
                                showAppMsg(AppMsg.STYLE_INFO, getResources()
                                        .getString(R.string.get_chat), Chat);
                                if (deviceId == AppData.GetInstance(mContext)
                                        .getSelectDeviceId()) {
                                    if (!isSendBroadcast[Chat]) {
                                        Intent intent = new Intent(
                                                Contents.chatBrodcastForSelectWatch);
                                        intent.putExtra("type", type);
                                        sendBroadcast(intent);
                                        isSendBroadcast[Chat] = true;
                                    }
                                    if (!havePlaySoundAndVibrate) {
                                        playSoundAndVibrate();
                                        havePlaySoundAndVibrate = true;
                                    }
                                } else {
                                    sendNotify(deviceId, type, getResources()
                                            .getString(R.string.get_chat));
                                }
                            }
                        } else if (type == 2) {
                            // 绑定请求
                            int deviceId = item.getIntValue("DeviceID");
                            String Msg = item.getString("Message");
                            String Content = item.getString("Content");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, getResources()
                                        .getString(R.string.ask_binding));
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Msg, AskBind);
                                if (!isSendBroadcast[AskBind]) {
                                    Intent intent = new Intent(
                                            Contents.askBindingBrodcast);
                                    intent.putExtra("type", type);
                                    intent.putExtra("deviceId", deviceId);
                                    intent.putExtra("Msg", Msg);
                                    intent.putExtra("userId",
                                            Content.split(",")[0]);
                                    if (Content.split(",").length > 1)
                                        intent.putExtra("name",
                                                Content.split(",")[1]);
                                    sendBroadcast(intent);
                                    isSendBroadcast[AskBind] = true;
                                }
                                if (!havePlaySoundAndVibrate) {
                                    playSoundAndVibrate();
                                    havePlaySoundAndVibrate = true;
                                }
                            }
                            if (deviceId == AppData.GetInstance(mContext)
                                    .getSelectDeviceId()) {
                                if (AppContext.getInstance().isMsgRecordShow()) {
                                    Intent intent = new Intent(
                                            Contents.getMsgRecordBrodcast);
                                    sendBroadcast(intent);
                                }
                            }
                            ContactDao mContactDao = new ContactDao(mContext);

                            ContactModel mContactModel = new ContactModel();
                            mContactModel.setId("-1" + Content.split(",")[0]);
                            mContactModel.setFromId(deviceId);
                            mContactModel.setObjectId(Content.split(",")[0]);
                            mContactModel
                                    .setRelationShip(Content.split(",")[1]);
                            mContactModel.setAvatar("8");
                            mContactModel.setAvatarUrl("");
                            mContactModel.setPhone(getResources().getString(
                                    R.string.unconfirmed));
                            mContactModel.setCornet("");
                            mContactModel.setType("4");

                            mContactDao.saveContact(mContactModel);

                            AppContext.getInstance().setContactList(
                                    mContactDao.getContactList(AppData
                                            .GetInstance(mContext)
                                            .getSelectDeviceId()));
                        } else if (type == 3) {
                            // 管理员同意绑定
                            String deviceId = item.getString("DeviceID");
                            String Content = item.getString("Content");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(Integer.valueOf(deviceId), type,
                                        Message);
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Message,
                                        AgreeBind);
                            }
                            getWatchList();

                            if (deviceId == String.valueOf(AppData.GetInstance(
                                    mContext).getSelectDeviceId())) {
                                if (AppContext.getInstance().isMsgRecordShow()) {
                                    if (!isSendBroadcast[AgreeBind]) {
                                        Intent intent = new Intent(
                                                Contents.getMsgRecordBrodcast);
                                        sendBroadcast(intent);
                                        isSendBroadcast[AgreeBind] = true;
                                    }
                                }
                            }
                        } else if (type == 4) {
                            // 管理员拒绝绑定
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Message);
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Message,
                                        RefuseBind);
                            }
                            if (deviceId == AppData.GetInstance(mContext)
                                    .getSelectDeviceId()) {
                                if (AppContext.getInstance().isMsgRecordShow()) {
                                    if (!isSendBroadcast[RefuseBind]) {
                                        Intent intent = new Intent(
                                                Contents.getMsgRecordBrodcast);
                                        sendBroadcast(intent);
                                        isSendBroadcast[RefuseBind] = true;
                                    }
                                }
                            }
                        } else if (type == 5) {
                            // 5设备升级成功
                            int deviceId = item.getIntValue("DeviceID");
                            String Content = item.getString("Content");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Content);
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Content,
                                        DeviceUpdata);
                            }
                            if (AppContext.getInstance().isMsgRecordShow()) {
                                if (!isSendBroadcast[DeviceUpdata]) {
                                    Intent intent = new Intent(
                                            Contents.getMsgRecordBrodcast);
                                    sendBroadcast(intent);
                                    isSendBroadcast[DeviceUpdata] = true;
                                }
                            }
                        } else if (type == 6) {
                            // 6设备配置已经同步
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Message);
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Message,
                                        DeviceSet);
                            }
                        } else if (type == 7) {
                            // 通讯录同步
                            int deviceId = item.getIntValue("DeviceID");
                            String Content = item.getString("Content");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Message);
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Message, AddressBook);
                            }
                        } else if (type == 8) {
                            // 8设备收到短信
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Message);
                            } else {
                                showAppMsg(AppMsg.STYLE_CONFIRM, Message, SMS);
                            }
                            if (deviceId == AppData.GetInstance(mContext)
                                    .getSelectDeviceId()) {
                                if (AppContext.getInstance().isSMSShow()) {
                                    if (!isSendBroadcast[SMS]) {
                                        Intent intent = new Intent(
                                                Contents.getSMSBrodcast);
                                        sendBroadcast(intent);
                                        isSendBroadcast[SMS] = true;
                                    }
                                }
                            }
                        } else if (type == 9) {
                            // 9解除关联
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");
                            if (deviceId == AppData.GetInstance(mContext)
                                    .getSelectDeviceId()) {
                                if (!AppContext.getInstance().isShow()) {
                                    sendNotify(deviceId, type, Message);
                                    AppData.GetInstance(mContext).setLoginAuto(
                                            false);
                                } else {
                                    MToast.makeText(Message).show();
                                    AppContext.getInstance().finishAll();
                                    Intent intent = new Intent(mContext,
                                            Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AppData.GetInstance(mContext).setLoginAuto(
                                            false);
                                    startActivity(intent);
                                    Intent intent_a = new Intent(mContext,
                                            MService.class);
                                    stopService(intent_a);
                                }
                                if (AppContext.getInstance().isMsgRecordShow()) {
                                    if (!isSendBroadcast[Unbind]) {
                                        Intent intent = new Intent(
                                                Contents.getMsgRecordBrodcast);
                                        sendBroadcast(intent);
                                        isSendBroadcast[Unbind] = true;
                                    }
                                }
                            } else {
                                MToast.makeText(Message).show();
                                WatchDao mWatchDao = new WatchDao(this);
                                mWatchDao.deleteWatch(deviceId);
                                AppContext.getInstance().setWatchMap(
                                        mWatchDao.getWatchMap());
                            }
                        } else if (type == 10) {
                            // 10更新设备信息
                        } else if (type == 11) {
                            // 11拍照
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Message);
                            } else {
                                if (AppContext.getInstance().isAlbumShow()) {
                                    Intent intent = new Intent(Contents.getPhotoBrodcast);
                                    sendBroadcast(intent);
                                }
                                showAppMsg(AppMsg.STYLE_CONFIRM, Message,
                                        TakePhoto);
                            }
                        } else if (type > 100 && type < 200) {
                            // 100以上的都是报警信息
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");
                            if (!AppContext.getInstance().isShow()) {
                                sendNotify(deviceId, type, Message);
                            } else {
                                showAppMsg(AppMsg.STYLE_ALERT, Message, AgreeBind);
                            }
                            if (deviceId == AppData.GetInstance(mContext).getSelectDeviceId()) {
                                if (AppContext.getInstance().isMsgRecordShow()) {
                                    if (!isSendBroadcast[AgreeBind]) {
                                        Intent intent = new Intent(Contents.getMsgRecordBrodcast);
                                        sendBroadcast(intent);
                                        isSendBroadcast[AgreeBind] = true;
                                    }
                                }
                            }
                        } else if (type >= 200) {
                            int deviceId = item.getIntValue("DeviceID");
                            String Message = item.getString("Message");

                            if (type >= 200 && type < 210) {
                                if (!AppContext.getInstance().isShow()) {
                                    sendNotify(deviceId, type, Message);
                                } else {
                                    showAppMsg(AppMsg.STYLE_CONFIRM, Message,
                                            SchoolDefend);
                                }
                                if (deviceId == AppData.GetInstance(mContext)
                                        .getSelectDeviceId()) {
                                    if (AppContext.getInstance()
                                            .isMsgRecordShow()) {
                                        if (!isSendBroadcast[SchoolDefend]) {
                                            Intent intent = new Intent(
                                                    Contents.getMsgRecordBrodcast);
                                            sendBroadcast(intent);
                                            isSendBroadcast[SchoolDefend] = true;
                                        }
                                    }
                                }
                            } else if (type == 230) {
                                // 更新宝贝资料
                                WebServiceUtils.GetDeviceDetail(mContext,
                                        _GetDeviceDetail,
                                        String.valueOf(deviceId), null, true);
                                if (deviceId == AppData.GetInstance(mContext)
                                        .getSelectDeviceId()) {
                                    if (AppContext.getInstance()
                                            .isMsgRecordShow()) {
                                        if (!isSendBroadcast[BabyInfo]) {
                                            Intent intent = new Intent(
                                                    Contents.getMsgRecordBrodcast);
                                            sendBroadcast(intent);
                                            isSendBroadcast[BabyInfo] = true;
                                        }
                                    }
                                }
                            } else if (type == 231) {
                                // 更新设备设置
                                WebServiceUtils.GetDeviceSet(mContext,
                                        _GetDeviceSet,
                                        String.valueOf(deviceId), null, true);
                                if (deviceId == AppData.GetInstance(mContext)
                                        .getSelectDeviceId()) {
                                    if (AppContext.getInstance()
                                            .isMsgRecordShow()) {
                                        if (!isSendBroadcast[DeviceSet]) {
                                            Intent intent = new Intent(
                                                    Contents.getMsgRecordBrodcast);
                                            sendBroadcast(intent);
                                            isSendBroadcast[DeviceSet] = true;
                                        }
                                    }
                                }
                            } else if (type == 232) {
                                // 更新通讯录
                                if (deviceId == AppData.GetInstance(mContext)
                                        .getSelectDeviceId()) {
                                    if (AppContext.getInstance()
                                            .isMsgRecordShow()) {
                                        Intent intent = new Intent(
                                                Contents.getMsgRecordBrodcast);
                                        sendBroadcast(intent);
                                    }
                                    if (AppContext.getInstance()
                                            .isAddressBookShow()) {
                                        Intent intent = new Intent(
                                                Contents.refreshContactBrodcast);
                                        sendBroadcast(intent);
                                    } else {
                                        WebServiceUtils.GetDeviceContact(
                                                mContext, _GetDeviceContact,
                                                String.valueOf(deviceId), null,
                                                true, false);
                                    }
                                } else {
                                    WebServiceUtils.GetDeviceContact(mContext,
                                            _GetDeviceContact,
                                            String.valueOf(deviceId), null,
                                            false, false);
                                }
                            } else if (type == 241) {
                                // 请求管理员确认关联
                            } else if (type == 242) {
                                // 管理员确认关联
                            } else if (type == 243) {
                                // 管理员拒绝关联
                            } else if (type == 244) {
                                // 解除关联
                            }
                        }
                    }
                    JSONArray NewListarr = jsonObject.getJSONArray("NewList");
                    String New = jsonObject.getString("New");
                    for (int j = 0; j < NewListarr.size(); j++) {
                        JSONObject items = NewListarr.getJSONObject(j);
                        String DeviceID = items.getString("DeviceID");
                        String Message = items.getString("Message");
                        String Voice = items.getString("Voice");
                        String SMS = items.getString("SMS");
                        String Photo = items.getString("Photo");
                        if (DeviceID.equals(String.valueOf(AppData.GetInstance(
                                mContext).getSelectDeviceId()))) {
                            if (!MessageO.equals(Message)
                                    || !VoiceO.equals(Voice)
                                    || !SMSO.equals(SMS)
                                    || !PhotoO.equals(Photo)) {
                                Intent intent = new Intent(
                                        Contents.BrodcastForUnread);
                                intent.putExtra("New", New);
                                intent.putExtra("deviceId", DeviceID);
                                intent.putExtra("Message", Message);
                                intent.putExtra("SMS", SMS);
                                intent.putExtra("Photo", Photo);
                                if (!AppContext.getInstance().isChatShow())
                                    intent.putExtra("Voice", Voice);
                                sendBroadcast(intent);

                                MessageO = Message;
                                VoiceO = Voice;
                                SMSO = SMS;
                                PhotoO = Photo;
                            }
                        }
                    }
                    JSONArray DeviceStatearr = jsonObject.getJSONArray("DeviceState");
                    if (DeviceStatearr.size() > 0) {
                        for (int k = 0; k < DeviceStatearr.size(); k++) {
                            JSONObject items = DeviceStatearr.getJSONObject(k);
                            if (!AppData.GetInstance(mContext).getDeviceServiceTime(items.getIntValue("DeviceID")).equals(items.getString("ServerTime"))) {
                                AppData.GetInstance(mContext).setDeviceServiceTime(items.getIntValue("DeviceID"), items.getString("ServerTime"));
                                WatchStateModel mWatchStateModel = new WatchStateModel();
                                mWatchStateModel.setDeviceId(items
                                        .getIntValue("DeviceID"));
                                mWatchStateModel.setAltitude(items
                                        .getDouble("Altitude"));
                                mWatchStateModel.setLatitude(items
                                        .getDouble("Latitude"));
                                mWatchStateModel.setLongitude(items
                                        .getDouble("Longitude"));
                                mWatchStateModel.setCourse(items
                                        .getString("Course"));
                                mWatchStateModel.setElectricity(items
                                        .getString("Electricity"));
                                mWatchStateModel.setStep(items
                                        .getString("Step"));
                                mWatchStateModel.setHealth(items
                                        .getString("Health"));
                                mWatchStateModel.setOnline(items
                                        .getString("Online"));
                                mWatchStateModel.setSpeed(items
                                        .getString("Speed"));
                                mWatchStateModel.setSatelliteNumber(items
                                        .getString("SatelliteNumber"));
                                mWatchStateModel.setCreateTime(items
                                        .getString("CreateTime"));
                                mWatchStateModel.setServerTime(items
                                        .getString("ServerTime"));
                                mWatchStateModel.setUpdateTime(items
                                        .getString("UpdateTime"));
                                mWatchStateModel.setDeviceTime(items
                                        .getString("DeviceTime"));
                                mWatchStateModel.setLocationType(items
                                        .getString("LocationType"));

                                WatchStateDao mWatchStateDao = new WatchStateDao(this);
                                mWatchStateDao.saveWatchState(mWatchStateModel);
                                if (items.getIntValue("DeviceID") == AppData.GetInstance(mContext).getSelectDeviceId()) {
                                    Intent intent = new Intent(Contents.changeStateBrodcastForSelectWatch);
                                    sendBroadcast(intent);
                                    AppContext.getInstance().setmWatchStateModel(mWatchStateDao.getWatchState(AppData.GetInstance(this).getSelectDeviceId()));
                                }
                            }
                        }
                    }
                } else if (code == 2) {
                    JSONArray arrs = jsonObject.getJSONArray("NewList");
                    for (int j = 0; j < arrs.size(); j++) {
                        JSONObject items = arrs.getJSONObject(j);
                        String DeviceID = items.getString("DeviceID");
                        String Message = items.getString("Message");
                        String Voice = items.getString("Voice");
                        Intent intent = new Intent(Contents.BrodcastForUnread);
                        intent.putExtra("deviceId", DeviceID);
                        intent.putExtra("Message", Message);
                        intent.putExtra("Voice", Voice);
                        sendBroadcast(intent);
                    }
                } else if (code == 0) {
                    if (AppData.GetInstance(mContext).getLoginAuto()) {
                        MToast.makeText(jsonObject.getString("Message")).show();
                        AppContext.getInstance().finishAll();
                        Intent intent = new Intent(mContext, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        AppData.GetInstance(mContext).setLoginAuto(false);
                        startActivity(intent);
                        Intent intent_a = new Intent(mContext, MService.class);
                        stopService(intent_a);
                    }
                } else {
                    // 0登录异常，-1输入参数错误，2取不到数据，3无权限操作，-2系统异常；Type 1语音信息2系统消息
                    // MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceList) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    List<WatchModel> mWatchList = new ArrayList<WatchModel>();
                    List<ContactModel> mContactModelList = new ArrayList<ContactModel>();
                    JSONArray arr = jsonObject.getJSONArray("deviceList");
                    int oldDeviceId = AppData.GetInstance(mContext)
                            .getSelectDeviceId();
                    AppData.GetInstance(mContext).setSelectDeviceId(
                            arr.getJSONObject(0).getIntValue("DeviceID"));

                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject item = arr.getJSONObject(i);
                        WatchModel mWatchModel = new WatchModel();
                        if (oldDeviceId == item.getIntValue("DeviceID")) {
                            AppData.GetInstance(mContext).setSelectDeviceId(
                                    item.getIntValue("DeviceID"));
                        }
                        mWatchModel.setId(item.getIntValue("DeviceID"));
                        mWatchModel.setUserId(item.getIntValue("UserId"));
                        mWatchModel.setModel(item.getString("DeviceModelID"));
                        mWatchModel.setName(item.getString("BabyName"));
                        mWatchModel.setAvatar(item.getString("Photo"));
                        mWatchModel.setPhone(item.getString("PhoneNumber"));
                        mWatchModel.setCornet(item.getString("PhoneCornet"));
                        mWatchModel.setGender(item.getString("Gender"));
                        mWatchModel.setBirthday(item.getString("Birthday"));
                        mWatchModel.setGrade(item.getIntValue("Grade"));
                        mWatchModel.setHomeAddress(item
                                .getString("HomeAddress"));
                        mWatchModel.setHomeLat(item.getDouble("HomeLat"));
                        mWatchModel.setHomeLng(item.getDouble("HomeLng"));
                        mWatchModel.setSchoolAddress(item
                                .getString("SchoolAddress"));
                        mWatchModel.setSchoolLat(item.getDouble("SchoolLat"));
                        mWatchModel.setSchoolLng(item.getDouble("SchoolLng"));
                        mWatchModel
                                .setLastestTime(item.getString("LatestTime"));
                        mWatchModel.setSetVersionNO(item
                                .getString("SetVersionNO"));
                        mWatchModel.setContactVersionNO(item
                                .getString("ContactVersionNO"));
                        mWatchModel.setOperatorType(item
                                .getString("OperatorType"));
                        mWatchModel.setSmsNumber(item.getString("SmsNumber"));
                        mWatchModel.setSmsBalanceKey(item
                                .getString("SmsBalanceKey"));
                        mWatchModel.setSmsFlowKey(item.getString("SmsFlowKey"));
                        mWatchModel.setActiveDate(item.getString("ActiveDate"));
                        mWatchModel.setCreateTime(item.getString("CreateTime"));
                        mWatchModel.setBindNumber(item.getString("BindNumber"));
                        mWatchModel.setCurrentFirmware(item
                                .getString("CurrentFirmware"));
                        mWatchModel.setFirmware(item.getString("Firmware"));
                        mWatchModel.setHireExpireDate(item
                                .getString("HireExpireDate"));
                        mWatchModel.setUpdateTime(item.getString("UpdateTime"));
                        mWatchModel.setSerialNumber(item
                                .getString("SerialNumber"));
                        mWatchModel.setPassword(item.getString("Password"));
                        mWatchModel.setIsGuard("1".equals(item.getString("IsGuard")));
                        mWatchModel.setDeviceType(item.getString("DeviceType"));
                        mWatchList.add(mWatchModel);

                        JSONObject deviceSet = item.getJSONObject("DeviceSet");
                        WatchSetModel mWatchSetModel = new WatchSetModel();
                        mWatchSetModel.setDeviceId(item.getIntValue("DeviceID"));
                        String[] strs = deviceSet.getString("SetInfo").split(
                                "-");
                        mWatchSetModel.setAutoAnswer(strs[11]);
                        mWatchSetModel.setReportLocation(strs[10]);
                        mWatchSetModel.setSomatoAnswer(strs[9]);
                        mWatchSetModel.setReservedPower(strs[8]);
                        mWatchSetModel.setClassDisabled(strs[7]);
                        mWatchSetModel.setTimeSwitch(strs[6]);
                        mWatchSetModel.setRefusedStranger(strs[5]);
                        mWatchSetModel.setWatchOffAlarm(strs[4]);
                        mWatchSetModel.setCallSound(strs[3]);
                        mWatchSetModel.setCallVibrate(strs[2]);
                        mWatchSetModel.setMsgSound(strs[1]);
                        mWatchSetModel.setMsgVibrate(strs[0]);
                        mWatchSetModel.setClassDisableda(deviceSet
                                .getString("ClassDisabled1"));
                        mWatchSetModel.setClassDisabledb(deviceSet
                                .getString("ClassDisabled2"));
                        mWatchSetModel.setWeekDisabled(deviceSet
                                .getString("WeekDisabled"));
                        mWatchSetModel.setTimerOpen(deviceSet
                                .getString("TimerOpen"));
                        mWatchSetModel.setTimerClose(deviceSet
                                .getString("TimerClose"));
                        mWatchSetModel.setBrightScreen(deviceSet
                                .getString("BrightScreen"));
                        mWatchSetModel.setWeekAlarm1(deviceSet
                                .getString("WeekAlarm1"));
                        mWatchSetModel.setWeekAlarm2(deviceSet
                                .getString("WeekAlarm2"));
                        mWatchSetModel.setWeekAlarm3(deviceSet
                                .getString("WeekAlarm3"));
                        mWatchSetModel.setAlarm1(deviceSet.getString("Alarm1"));
                        mWatchSetModel.setAlarm2(deviceSet.getString("Alarm2"));
                        mWatchSetModel.setAlarm3(deviceSet.getString("Alarm3"));
                        mWatchSetModel.setLocationMode(deviceSet
                                .getString("LocationMode"));
                        mWatchSetModel.setLocationTime(deviceSet
                                .getString("LocationTime"));
                        mWatchSetModel.setFlowerNumber(deviceSet
                                .getString("FlowerNumber"));
                        // mWatchSetModel.setLanguage(deviceSet.getString("Language"));
                        // mWatchSetModel.setTimeZone(deviceSet.getString("TimeZone"));
                        mWatchSetModel.setCreateTime(deviceSet
                                .getString("CreateTime"));
                        mWatchSetModel.setUpdateTime(deviceSet
                                .getString("UpdateTime"));
                        mWatchSetModel.setSleepCalculate(deviceSet
                                .getString("SleepCalculate"));
                        mWatchSetModel.setStepCalculate(deviceSet
                                .getString("StepCalculate"));
                        mWatchSetModel.setHrCalculate(deviceSet
                                .getString("HrCalculate"));
                        mWatchSetModel.setSosMsgswitch(deviceSet
                                .getString("SosMsgswitch"));

                        WatchSetDao mWatchDao = new WatchSetDao(this);
                        mWatchDao.saveWatchSet(mWatchSetModel);

                        JSONObject deviceState = item
                                .getJSONObject("DeviceState");
                        WatchStateModel mWatchStateModel = new WatchStateModel();
                        mWatchStateModel.setDeviceId(item.getIntValue("DeviceID"));
                        if (!TextUtils.isEmpty(deviceState
                                .getString("Altitude"))) {
                            mWatchStateModel.setAltitude(deviceState
                                    .getDouble("Altitude"));
                        }
                        if (!TextUtils.isEmpty(deviceState
                                .getString("Latitude"))) {
                            mWatchStateModel.setLatitude(deviceState
                                    .getDouble("Latitude"));
                        }
                        if (!TextUtils.isEmpty(deviceState
                                .getString("Longitude"))) {
                            mWatchStateModel.setLongitude(deviceState
                                    .getDouble("Longitude"));
                        }
                        mWatchStateModel.setCourse(deviceState
                                .getString("Course"));
                        mWatchStateModel.setElectricity(deviceState
                                .getString("Electricity"));
                        mWatchStateModel.setStep(deviceState.getString("Step"));
                        mWatchStateModel.setHealth(deviceState
                                .getString("Health"));
                        mWatchStateModel.setOnline(deviceState
                                .getString("Online"));
                        mWatchStateModel.setSpeed(deviceState
                                .getString("Speed"));
                        mWatchStateModel.setSatelliteNumber(deviceState
                                .getString("SatelliteNumber"));
                        mWatchStateModel.setCreateTime(deviceState
                                .getString("CreateTime"));
                        mWatchStateModel.setServerTime(deviceState
                                .getString("ServerTime"));
                        mWatchStateModel.setUpdateTime(deviceState
                                .getString("UpdateTime"));
                        mWatchStateModel.setDeviceTime(deviceState
                                .getString("DeviceTime"));
                        mWatchStateModel.setLocationType(deviceState
                                .getString("LocationType"));

                        WatchStateDao mWatchStateDao = new WatchStateDao(this);
                        mWatchStateDao.saveWatchState(mWatchStateModel);

                        JSONArray arrContact = item.getJSONArray("ContactArr");
                        for (int j = 0; j < arrContact.size(); j++) {
                            JSONObject items = arrContact.getJSONObject(j);
                            ContactModel mContactModel = new ContactModel();
                            mContactModel.setId(items
                                    .getString("DeviceContactId"));
                            mContactModel.setFromId(item.getIntValue("DeviceID"));
                            mContactModel.setObjectId(items
                                    .getString("ObjectId"));
                            mContactModel.setRelationShip(items
                                    .getString("Relationship"));
                            mContactModel.setAvatar(items.getString("Photo"));
                            mContactModel.setAvatarUrl(items
                                    .getString("HeadImg"));
                            mContactModel.setPhone(items
                                    .getString("PhoneNumber"));
                            mContactModel.setCornet(items
                                    .getString("PhoneShort"));
                            mContactModel.setType(items.getString("Type"));
                            mContactModelList.add(mContactModel);
                        }
                        ContactDao mContactDao = new ContactDao(this);
                        mContactDao.deleteWatchContact(item.getIntValue("DeviceID"));
                    }
                    WatchDao dao = new WatchDao(this);
                    dao.saveWatchList(mWatchList);
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.saveContactList(mContactModelList);
                    AppContext.getInstance().setWatchMap(dao.getWatchMap());
                } else if (code == 2) {
                    // 2未取到数据
                    // MToast.makeText(jsonObject.getString("Message")).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscriber(tag = Constants.EVENT_LOGOUT_DIRECTLY)
    private void logoutImpl(Object obj) {
        AppContext.getInstance().finishAll();
        stopService(new Intent(mContext, MService.class));

        AppData appData = AppData.GetInstance(this);
        appData.setLoginAuto(false);
        appData.setPwd(Constants.DEFAULT_BLANK);
        appData.setPhoneNumber(Constants.DEFAULT_BLANK);
        appData.setBindNumber(Constants.DEFAULT_BLANK);

        Intent intent = new Intent(mContext, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}