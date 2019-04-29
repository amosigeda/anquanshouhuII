package vip.inteltech.gat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.chatutil.AudioRecorder;
import vip.inteltech.gat.chatutil.ChatMsgEntity;
import vip.inteltech.gat.chatutil.ChatMsgViewAdapter;
import vip.inteltech.gat.chatutil.RecordButton;
import vip.inteltech.gat.chatutil.RecordButton.OnFinish;
import vip.inteltech.gat.chatutil.RecordButton.StartListener;
import vip.inteltech.gat.db.ChatMsgDao;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MListView;
import vip.inteltech.gat.viewutils.MListView.OnRefreshListener;
import vip.inteltech.gat.viewutils.MToast;

public class Chat extends BaseActivity implements OnClickListener, WebServiceListener {

    private RecordButton button;
    private MListView mListView;
    private ImageView iv_chat_setmode;
    private Button btn_send;
    private EditText et_message;
    private boolean btn_voice = false;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private ChatMsgViewAdapter mAdapter;
    private Chat mContext;
    private int pageIndex;
    private int SumPageSize;
    List<ChatMsgEntity> allDataArrays;
    ChatMsgDao mChatMsgDao;
    private AudioRecorder mAudioRecorder = new AudioRecorder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat);
        mContext = this;

        //get 30 latest msgs
        mDataArrays = AppContext.getInstance().getChatMsgList();

        mChatMsgDao = new ChatMsgDao(mContext);
        pageIndex = mDataArrays.size();

        //get all the msgs
        allDataArrays = mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId());

        //the total count of msgs
        SumPageSize = allDataArrays.size();
       /* if(allDataArrays.size() > 0)
        	System.out.println(allDataArrays.get(0).getCreateTime()+"   "+allDataArrays.get(SumPageSize-1).getCreateTime());
        System.out.println(SumPageSize+"");*/
        findViewById(R.id.btn_left).setOnClickListener(this);
        button = (RecordButton) findViewById(R.id.btn_record);
        button.setAudioRecord(new AudioRecorder());
        button.setActivity(this);

        //when start to record the voice msg,stop the media play
        button.setStartListener(new StartListener() {
            @Override
            public void recordStart() {
                mAdapter.stopPlay();
            }
        });
        button.setOnFinish(new OnFinish() {
            @Override
            public void Finish(String fileName, float recodeTime) {
                if (recodeTime < 15f)
                    recodeTime = recodeTime + 1f;
                SendVoice(recodeTime);
            }
        });
        mListView = (MListView) findViewById(R.id.lv);
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mListView.getCount() - 1);
        mListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void toRefresh() {
                if (SumPageSize - pageIndex <= 0) {
                    mListView.onRefreshFinished();
                    return;
                }
                if (SumPageSize > 0) {
                    if (SumPageSize > pageIndex + Contents.CHATMSGREFRESH) {
                        mDataArrays.addAll(0, allDataArrays.subList(SumPageSize - pageIndex - Contents.CHATMSGREFRESH, SumPageSize - pageIndex));
                    } else {
                        mDataArrays.addAll(0, allDataArrays.subList(0, SumPageSize - pageIndex));
                    }
                    pageIndex = mDataArrays.size();
                }
                //System.out.println(mDataArrays.get(0).getCreateTime()+"   "+mDataArrays.get(pageIndex-1).getCreateTime());
                mAdapter.notifyDataSetChanged();
                mListView.onRefreshFinished();
            }
        });
        iv_chat_setmode = (ImageView) findViewById(R.id.iv_chat_setmode);
        iv_chat_setmode.setOnClickListener(this);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        et_message = (EditText) findViewById(R.id.et_message);
        GetDeviceVoice();
        //Text();
        IntentFilter IntentFilter = new IntentFilter(Contents.chatBrodcastForSelectWatch);
        IntentFilter.setPriority(5);
        registerReceiver(chatReceiver, IntentFilter);
    }

    private void Text() {
        for (int i = 0; i < 10; i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDeviceVoiceId(i + "");
            entity.setDeviceID("2");
            entity.setState("1");
            //entity.setTotalPackage(item.getString("TotalPackage"));
            //entity.setCurrentPackage(item.getString("CurrentPackage"));
            entity.setType("3");
            if (i == 1)
                entity.setObjectId("5");
            else if (i == 3) {
                entity.setObjectId("5");
            } else {
                entity.setObjectId("4");
            }
            entity.setMark("11");
            entity.setPath("22");
            entity.setCreateTime("2015/01/01 11:11:11");
            entity.setUpdateTime("2015/01/01 11:11:11");
            entity.setLength("1");
            mDataArrays.add(entity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.iv_chat_setmode:
                if (btn_voice) {
                    button.setVisibility(View.VISIBLE);
                    et_message.setVisibility(View.GONE);
                    btn_send.setVisibility(View.GONE);
                    btn_voice = false;
                    iv_chat_setmode.setImageResource(R.drawable.chat_setmode_msg);
                    HideInputMethod();
                } else {
                    button.setVisibility(View.GONE);
                    et_message.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.VISIBLE);
                    btn_voice = true;
                    iv_chat_setmode.setImageResource(R.drawable.chat_setmode_voice);
                    et_message.requestFocus();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            InputMethodManager inputManager = (InputMethodManager) et_message.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.showSoftInput(et_message, 0);
                        }
                    }, 300);
                }
                break;
            case R.id.btn_send:
                send();
                break;
        }
    }

    private void HideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
    }

    private void send() {
        String contString = et_message.getText().toString();
        if (!TextUtils.isEmpty(contString)) {
            WebService ws = new WebService(mContext, _SendDeviceVoice, false, "SendDeviceVoice");
            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
            property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
            property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
            property.add(new WebServiceProperty("voice", contString));
            property.add(new WebServiceProperty("length", String.valueOf(contString.length())));
            property.add(new WebServiceProperty("msgtype", "1"));
            ws.addWebServiceListener(mContext);
            ws.SyncGet(property);
            et_message.setText("");
        }
    }

    private void SendVoice(float recodeTime) {
        String voice = "";
        File file = new File(AppContext.getInstance().getContext().getFilesDir().getAbsolutePath() + "/TestRecord/"
                + "SendVoice" + ".amr");
        try {
            FileInputStream instream = new FileInputStream(file);
            if (instream != null) {
                byte[] buffer = new byte[1024];
                int n = 0;
                while ((n = instream.read(buffer)) != -1) {
                    voice += bytesToHexString(buffer, n);
                    //voice += bytesToHexString(buffer).substring(0, n);
                }
                instream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //voice = file2String(file, "utf-8");

        WebService ws = new WebService(mContext, _SendDeviceVoice, false, "SendDeviceVoice");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        property.add(new WebServiceProperty("voice", voice));
        property.add(new WebServiceProperty("length", String.valueOf((int) recodeTime)));
        property.add(new WebServiceProperty("msgtype", "0"));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
        mAudioRecorder.deleteOldFile();
    }

    public static String bytesToHexString(byte[] src, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0 || length <= 0 || length > src.length) {
            return null;
        }
        for (int i = 0; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private void GetDeviceVoice() {
        WebService ws = new WebService(mContext, _GetDeviceVoice, false, "GetDeviceVoice");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _SendDeviceVoice = 0;
    private final int _GetDeviceVoice = 1;
    private boolean isSend = false;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            int code = jsonObject.getInt("Code");
            if (id == _SendDeviceVoice) {
                if (code == 1) {
                    isSend = true;
                    GetDeviceVoice();
                } else {
                    MToast.makeText(R.string.send_fail).show();
                }
            } else if (id == _GetDeviceVoice) {
                if (code == 1) {
                    JSONArray arr = jsonObject.getJSONArray("VoiceList");
                    ChatMsgDao mChatMsgDao = new ChatMsgDao(mContext);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = arr.getJSONObject(i);
                        ChatMsgEntity entity = new ChatMsgEntity();
                        entity.setDeviceVoiceId(item.getString("DeviceVoiceId"));
                        entity.setDeviceID(item.getString("DeviceID"));
                        entity.setUserID(String.valueOf(AppData.GetInstance(mContext).getUserId()));
                        entity.setState(item.getString("State"));
                        entity.setType(item.getString("Type"));
                        entity.setObjectId(item.getString("ObjectId"));
                        entity.setMark(item.getString("Mark"));
                        entity.setPath(item.getString("Path"));
                        entity.setLength(item.getString("Length"));
                        entity.setMsgType(item.getString("MsgType"));
                        entity.setCreateTime(item.getString("CreateTime"));
                        entity.setUpdateTime(item.getString("UpdateTime"));
                        entity.setRead(isSend);
                        isSend = false;
                        Log.v("kkk", "111");
                        //		mDataArrays.add(entity);
                        mChatMsgDao.saveChatMsg(entity);
                        //       mDataArrays = new ArrayList<ChatMsgEntity>();
                        mDataArrays.clear();
                        mDataArrays.addAll(AppContext.getInstance().getChatMsgList());
                        //    Log.v("kkk", "size = "+mDataArrays.size()+" "+mDataArrays.get(mDataArrays.size()-1));
                    }
                    pageIndex = mDataArrays.size();
                    allDataArrays = mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId());
                    SumPageSize = allDataArrays.size();
                    //   Log.v("kkk", "all size = "+SumPageSize+" "+allDataArrays.get(SumPageSize-1));
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mListView.getBottom());
                } else {
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            if (type == 1) {
                //abortBroadcast();
                GetDeviceVoice();
                //playSoundAndVibrate();
            }
        }
    };

    private void playSoundAndVibrate() {
        if (AppData.GetInstance(mContext).getNotificationVibration()) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {0, 300}; // {间隔时间，震动持续时间}
            vibrator.vibrate(pattern, -1);
        }
        if (AppData.GetInstance(mContext).getNotificationSound()) {
            playSound();
        }
    }

    private void playSound() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.getInstance().setChatShow(true);
        PermissionsUtil.requestPermission(this, Manifest.permission.RECORD_AUDIO, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {

            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                CommUtil.showMsgShort(R.string.permission_record_denied);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销广播
        AppContext.getInstance().setChatShow(false);
        try {
            unregisterReceiver(chatReceiver);
        } catch (Exception e) {
        }
        mAdapter.stopPlay();
    }
}
