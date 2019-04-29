package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.ChooseDialog;
import vip.inteltech.gat.viewutils.ChooseDialog.OnListener;
import vip.inteltech.gat.viewutils.MToast;

public class WatchSetting extends BaseActivity implements OnClickListener, WebServiceListener {
    private WatchSetting mContext;
    private CheckBox cb_automatic_answer, cb_report_call_location, cb_somatosensory_answer,
            cb_reserved_power, cb_class_disabled, cb_timing_starting, cb_refused_stranger, cb_watch_off_alarm, cb_sos_message;
    private TextView tv_bright_time, tv_class_disable_time, tv_switch_time, tv_timezone, tv_language, tv_flower, tv_work_mode,
            tv_title, tv_setting_ps, tv_watch_off_alarm, tv_watch_timezone, tv_watch_language;
    private String[] mDatas, timezoneDatas, languageDatas, timeZoneMintue, flowerDatas, workmodeDatas;
    private WatchSetModel mWatchSetModel;
    private Button save;
    private RelativeLayout rl_timezone, rl_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.watch_setting);
        mContext = this;

        //Hide remote listen when logined user is not the administrator.
        View remoteListen = findViewById(R.id.rl_remote_monitoring);
        AppData appData = AppData.GetInstance(this);
        WatchModel wm = AppContext.getInstance().getmWatchModel();
        if (wm != null && wm.getUserId() != appData.getUserId()) {
            remoteListen.setVisibility(View.GONE);
        }
        remoteListen.setOnClickListener(this);

        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.ll_class_disabled).setOnClickListener(this);
        findViewById(R.id.ll_timing_starting).setOnClickListener(this);
        findViewById(R.id.rl_bright_time).setOnClickListener(this);
        findViewById(R.id.rl_sound_vibrate).setOnClickListener(this);
        findViewById(R.id.rl_remote_shutdown).setOnClickListener(this);
        findViewById(R.id.rl_flower).setOnClickListener(this);
        findViewById(R.id.rl_alarm_clock).setOnClickListener(this);
        findViewById(R.id.rl_remote_reset).setOnClickListener(this);
        findViewById(R.id.rl_remote_restore).setOnClickListener(this);
        findViewById(R.id.rl_work_mode).setOnClickListener(this);

        rl_timezone = (RelativeLayout) findViewById(R.id.rl_timezone);
        rl_language = (RelativeLayout) findViewById(R.id.rl_language);
        rl_timezone.setOnClickListener(this);
        rl_language.setOnClickListener(this);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
        if (!(new Contents().canDropAlarm) || !AppContext.getInstance().isSupportProximity()) {
            findViewById(R.id.rl_watch_off_alarm).setVisibility(View.GONE);
        }
        cb_automatic_answer = (CheckBox) findViewById(R.id.cb_automatic_answer);
        cb_report_call_location = (CheckBox) findViewById(R.id.cb_report_call_location);
        cb_somatosensory_answer = (CheckBox) findViewById(R.id.cb_somatosensory_answer);
        cb_reserved_power = (CheckBox) findViewById(R.id.cb_reserved_power);
        cb_class_disabled = (CheckBox) findViewById(R.id.cb_class_disabled);
        cb_timing_starting = (CheckBox) findViewById(R.id.cb_timing_starting);
        cb_refused_stranger = (CheckBox) findViewById(R.id.cb_refused_stranger);
        cb_watch_off_alarm = (CheckBox) findViewById(R.id.cb_watch_off_alarm);
        cb_sos_message = (CheckBox) findViewById(R.id.cb_sos_message);

        tv_bright_time = (TextView) findViewById(R.id.tv_bright_time);
        tv_class_disable_time = (TextView) findViewById(R.id.tv_class_disable_time);
        tv_switch_time = (TextView) findViewById(R.id.tv_switch_time);
        tv_flower = (TextView) findViewById(R.id.tv_flower);
        tv_work_mode = (TextView) findViewById(R.id.tv_work_mode);
        tv_title = (TextView) findViewById(R.id.textView_Title);
        tv_setting_ps = (TextView) findViewById(R.id.tv_setting_ps);
        tv_watch_off_alarm = (TextView) findViewById(R.id.tv_watch_off_alarm);
        tv_watch_timezone = (TextView) findViewById(R.id.tv_watch_timezone);
        tv_watch_language = (TextView) findViewById(R.id.tv_watch_language);

        mDatas = new String[]{
                "5" + mContext.getResources().getString(R.string.second),
                "10" + mContext.getResources().getString(R.string.second),
                "15" + mContext.getResources().getString(R.string.second),
                "20" + mContext.getResources().getString(R.string.second),
                "30" + mContext.getResources().getString(R.string.second),
                "60" + mContext.getResources().getString(R.string.second),};
        timeZoneMintue = new String[]{"-720", "-660", "-600", "-540", "-480", "-420", "-360", "-300", "-270", "-240", "-210", "-180", "-120", "-60", "0",
                "60", "120", "180", "210", "240", "270", "300", "330", "345", "360", "390", "420", "480", "540", "570", "600", "660", "720", "780", "840"};
        /*@"-12:00",@"-11:00",@"-10:00",@"-09:00",@"-08:00",@"-07:00",@"-06:00",
        @"-05:00",@"-04:30",@"-04:00",@"-03:30",@"-03:00",@"-02:00",@"-01:00",
        @"00:00",@"+01:00",@"+02:00",@"+03:00",@"+03:30",@"+04:00",@"+04:30",
        @"+05:00",@"+05:30",@"+05:45",@"+06:00",@"+06:30",@"+07:00",@"+08:00",
        @"+09:00",@"+09:30",@"+10:00",@"+11:00",@"+12:00",@"+13:00",*/
        timezoneDatas = new String[]{"UTC-12:00", "UTC-11:00", "UTC-10:00", "UTC-09:00", "UTC-08:00", "UTC-07:00",
                "UTC-06:00", "UTC-05:00", "UTC-04:30", "UTC-04:00", "UTC-03:30", "UTC-03:00", "UTC-02:00", "UTC-01:00",
                "UTC", "UTC+01:00", "UTC+02:00", "UTC+03:00", "UTC+03:30", "UTC+04:00", "UTC+04:30", "UTC+05:00", "UTC+05:30",
                "UTC+05:45", "UTC+06:00", "UTC+06:30", "UTC+07:00", "UTC+08:00", "UTC+09:00", "UTC+09:30", "UTC+10:00", "UTC+11:00",
                "UTC+12:00", "UTC+13:00", "UTC+14:00"};
        languageDatas = new String[]{
                getResources().getString(R.string.english),
                getResources().getString(R.string.simplified_Chinese),
                getResources().getString(R.string.traditional_Chinese)
        };
        flowerDatas = new String[]{
                getResources().getString(R.string.flower_count_0),
                getResources().getString(R.string.flower_count_1),
                getResources().getString(R.string.flower_count_2),
                getResources().getString(R.string.flower_count_3),
                getResources().getString(R.string.flower_count_4),
                getResources().getString(R.string.flower_count_5)
        };
        workmodeDatas = new String[]{
                getResources().getString(R.string.work_mode_0),
                getResources().getString(R.string.work_mode_1),
                getResources().getString(R.string.work_mode_2),
                getResources().getString(R.string.work_mode_3)
        };

        isChangeData = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false};
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        isChanageDataListener(cb_automatic_answer, mWatchSetModel.getAutoAnswer(), 0);
        isChanageDataListener(cb_report_call_location, mWatchSetModel.getReportLocation(), 1);
        isChanageDataListener(cb_somatosensory_answer, mWatchSetModel.getSomatoAnswer(), 2);
        isChanageDataListener(cb_reserved_power, mWatchSetModel.getReservedPower(), 3);
        isChanageDataListener(cb_class_disabled, mWatchSetModel.getClassDisabled(), 4);
        isChanageDataListener(cb_timing_starting, mWatchSetModel.getTimeSwitch(), 5);
        isChanageDataListener(cb_refused_stranger, mWatchSetModel.getRefusedStranger(), 6);
        isChanageDataListener(cb_watch_off_alarm, mWatchSetModel.getWatchOffAlarm(), 7);
        isChanageDataListener(cb_sos_message, mWatchSetModel.getSosMsgswitch(), 13);
        if (new Contents().canLanguageTimeZone) {
            rl_timezone.setVisibility(View.VISIBLE);
            rl_language.setVisibility(View.VISIBLE);
        } else {
            rl_timezone.setVisibility(View.GONE);
            rl_language.setVisibility(View.GONE);
        }

        if (!AppContext.getInstance().isSupportGsensor() || AppContext.getInstance().getDeviceSvn().equals("640") || AppContext.getInstance().getDeviceSvn().equals("647")) {
            findViewById(R.id.rl_somatosensory_answer).setVisibility(View.GONE);
        }
        if (!AppContext.getInstance().isSupportLcd()) {
            findViewById(R.id.rl_bright_time).setVisibility(View.GONE);
        }
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2") ||
                AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D8_CH") != -1) {
            findViewById(R.id.rl_class_disabled).setVisibility(View.GONE);
            findViewById(R.id.rl_flower).setVisibility(View.GONE);
            findViewById(R.id.rl_somatosensory_answer).setVisibility(View.GONE);
        }
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_title.setText(R.string.locator_setting);
            tv_setting_ps.setText(R.string.locator_setting_PS);
            tv_watch_off_alarm.setText(R.string.locator_off_alarm);
            tv_watch_timezone.setText(R.string.locator_timezone);
            tv_watch_language.setText(R.string.locator_language);
        }
        GetDeviceSet();
    }

    private void initData() {
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        cb_automatic_answer.setChecked(mWatchSetModel.getAutoAnswer().equals("1"));
        cb_report_call_location.setChecked(mWatchSetModel.getReportLocation().equals("1"));
        cb_somatosensory_answer.setChecked(mWatchSetModel.getSomatoAnswer().equals("1"));
        cb_reserved_power.setChecked(mWatchSetModel.getReservedPower().equals("1"));
        cb_class_disabled.setChecked(mWatchSetModel.getClassDisabled().equals("1"));
        cb_timing_starting.setChecked(mWatchSetModel.getTimeSwitch().equals("1"));
        cb_refused_stranger.setChecked(mWatchSetModel.getRefusedStranger().equals("1"));
        cb_watch_off_alarm.setChecked(mWatchSetModel.getWatchOffAlarm().equals("1"));
        cb_sos_message.setChecked(!mWatchSetModel.getSosMsgswitch().equals("1"));
        tv_bright_time.setText(mWatchSetModel.getBrightScreen() + getResources().getString(R.string.second));
        if (new Contents().canLanguageTimeZone) {
            tv_language.setText(mWatchSetModel.getLanguage());
            tv_timezone.setText(mWatchSetModel.getTimeZone());
        }
        tv_class_disable_time.setText(mWatchSetModel.getClassDisableda()
                + getResources().getString(R.string.dh)
                + mWatchSetModel.getClassDisabledb());
        tv_switch_time.setText(getResources().getString(R.string.turn_on_watch) + getResources().getString(R.string.mh)
                + mWatchSetModel.getTimerOpen()
                + getResources().getString(R.string.turn_off_watch) + getResources().getString(R.string.mh)
                + mWatchSetModel.getTimerClose());
        if (TextUtils.isEmpty(mWatchSetModel.getLocationMode())) {
            mWatchSetModel.setLocationMode("0");
            mWatchSetModel.setLocationTime("0");
        }
        if (TextUtils.isEmpty(mWatchSetModel.getFlowerNumber())) {
            mWatchSetModel.setFlowerNumber("0");
        }
        if (mWatchSetModel.getLocationMode().equals("0")) {
            tv_work_mode.setText(workmodeDatas[0]);
        } else if (mWatchSetModel.getLocationTime().equals("3")) {
            tv_work_mode.setText(workmodeDatas[1]);
        } else if (mWatchSetModel.getLocationTime().equals("10")) {
            tv_work_mode.setText(workmodeDatas[2]);
        } else if (mWatchSetModel.getLocationTime().equals("60")) {
            tv_work_mode.setText(workmodeDatas[3]);
        } else {
            tv_work_mode.setText(workmodeDatas[0]);
        }
        if (Integer.parseInt(mWatchSetModel.getFlowerNumber()) >= 0 && Integer.parseInt(mWatchSetModel.getFlowerNumber()) <= 5) {
            tv_flower.setText(flowerDatas[Integer.parseInt(mWatchSetModel.getFlowerNumber())]);
        }
    }

    private boolean[] isChangeData;

    private void isChanageDataListener(final CheckBox cb, final String str, final int i) {
        cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 13) {
                    if ((cb.isChecked() ? "0" : "1").equals(str)) {
                        isChangeData[i] = false;
                    } else {
                        isChangeData[i] = true;
                    }
                } else {
                    if ((cb.isChecked() ? "1" : "0").equals(str)) {
                        isChangeData[i] = false;
                    } else {
                        isChangeData[i] = true;
                    }
                }
                isShowSave();
            }
        });
    }

    private void isShowSave() {
        boolean a = false;
        for (int i = 0; i < isChangeData.length; i++) {
            if (isChangeData[i]) {
                a = true;
                continue;
            }
        }
        if (a) {
            save.setVisibility(View.VISIBLE);
        } else {
            save.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.save:
                UpdateDeviceSet();
                break;
            case R.id.ll_class_disabled:
                startActivity(new Intent(mContext, ClassDisable.class));
                break;
            case R.id.ll_timing_starting:
                startActivity(new Intent(mContext, TimeSwitch.class));
                break;
            case R.id.rl_timezone:
                chooseDialog(0);
                break;
            case R.id.rl_language:
                chooseDialog(1);
                break;
            case R.id.rl_bright_time:
                chooseDialog(2);
                break;
            case R.id.rl_sound_vibrate:
                startActivity(new Intent(mContext, SoundVibrate.class));
                break;
            case R.id.rl_remote_monitoring:
                if(AppContext.getInstance().isSupportVideoSoundRecording()){
                    Intent intent=new Intent(this,Album.class);
                    intent.putExtra("SHOW_DIALOG",true);
                    startActivity(intent);
                }else {
                    makeSureDialog(0);
                }
                break;
            case R.id.rl_remote_shutdown:
                makeSureDialog(2);
                break;
            case R.id.rl_flower:
                chooseDialog(3);
                break;
            case R.id.rl_alarm_clock:
                startActivity(new Intent(mContext, AlarmClock.class));
                break;
            case R.id.rl_remote_reset:
                makeSureDialog(3);
                break;
            case R.id.rl_remote_restore:
                makeSureDialog(4);
                break;
            case R.id.rl_work_mode:
                chooseDialog(4);
                break;
        }
    }

    private ChooseDialog mChooseDialog;

    public void chooseDialog(final int i) {
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));

        if (mChooseDialog != null)
            mChooseDialog.cancel();

        switch (i) {
            case 0:
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    mChooseDialog = new ChooseDialog(this, timezoneDatas, R.string.locator_timezone);
                } else {
                    mChooseDialog = new ChooseDialog(this, timezoneDatas, R.string.watch_timezone);
                }
                mChooseDialog.setChoose(tv_timezone.getText().toString());
                break;
            case 1:
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    mChooseDialog = new ChooseDialog(this, languageDatas, R.string.locator_language);
                } else {
                    mChooseDialog = new ChooseDialog(this, languageDatas, R.string.watch_language);
                }
                mChooseDialog.setChoose(tv_language.getText().toString());
                break;
            case 2:
                mChooseDialog = new ChooseDialog(this, mDatas, R.string.bright_time);
                if (mWatchSetModel.getBrightScreen().equals("0")) {
                    mChooseDialog.setChoose("5" + getResources().getString(R.string.second));
                } else {
                    mChooseDialog.setChoose(tv_bright_time.getText().toString());
                }
                break;
            case 3:
                mChooseDialog = new ChooseDialog(this, flowerDatas, R.string.flower);
                mChooseDialog.setChoose(tv_flower.getText().toString());
                break;
            case 4:
                mChooseDialog = new ChooseDialog(this, workmodeDatas, R.string.work_mode);
                mChooseDialog.setChoose(tv_work_mode.getText().toString());
                break;
        }
        mChooseDialog.show();
        mChooseDialog.setListener(new OnListener() {
            @Override
            public void onClick(String grade, int index) {
                switch (i) {
                    case 0:
                        tv_timezone.setText(grade);
                        if (tv_timezone.getText().toString().equals(mWatchSetModel.getTimeZone())) {
                            isChangeData[9] = false;
                        } else {
                            isChangeData[9] = true;
                        }
                        break;
                    case 1:
                        tv_language.setText(grade);
                        if (tv_language.getText().toString().equals(mWatchSetModel.getLanguage())) {
                            isChangeData[10] = false;
                        } else {
                            isChangeData[10] = true;
                        }
                        break;
                    case 2:
                        tv_bright_time.setText(grade);
                        if (tv_bright_time.getText().toString().equals(mWatchSetModel.getBrightScreen() + getResources().getString(R.string.second))) {
                            isChangeData[8] = false;
                        } else {
                            isChangeData[8] = true;
                        }
                        break;
                    case 3:
                        tv_flower.setText(grade);
                        if (isHave(tv_flower.getText().toString(), mWatchSetModel.getFlowerNumber())) {
                            isChangeData[11] = false;
                        } else {
                            isChangeData[11] = true;
                        }
                        break;
                    case 4:
                        tv_work_mode.setText(grade);
                        if (tv_work_mode.getText().toString().equals(workmodeDatas[0]) && mWatchSetModel.getLocationMode().equals("0")) {
                            isChangeData[12] = false;
                        } else if (mWatchSetModel.getLocationMode().equals("0")) {
                            isChangeData[12] = true;
                        } else if (tv_work_mode.getText().toString().equals(workmodeDatas[1]) && mWatchSetModel.getLocationTime().equals("3")) {
                            isChangeData[12] = false;
                        } else if (tv_work_mode.getText().toString().equals(workmodeDatas[2]) && mWatchSetModel.getLocationTime().equals("10")) {
                            isChangeData[12] = false;
                        } else if (tv_work_mode.getText().toString().equals(workmodeDatas[3]) && mWatchSetModel.getLocationTime().equals("60")) {
                            isChangeData[12] = false;
                        } else {
                            isChangeData[12] = true;
                        }
                        break;
                }
                isShowSave();
            }
        });
    }

    private int getItem(String[] strs, String str) {
        for (int j = 0; j < strs.length; j++) {
            if (strs[j].equals(str)) {
                return j;
            }
        }
        return 0;
    }

    private Dialog dialog;

    private void makeSureDialog(final int index) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
		/*wl.x = getWindowManager().getDefaultDisplay().getWidth()/2;
		wl.y = getWindowManager().getDefaultDisplay().getHeight()/2;
		*/
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        switch (index) {
            case 0:
                tv.setText(R.string.defend_remind);
                tv_content.setText(R.string.sure_remote_monitoring);
                break;
            case 1:
                WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    tv.setText(R.string.find_locator);
                    tv_content.setText(R.string.sure_find_locator);
                } else {
                    tv.setText(R.string.find_watch);
                    tv_content.setText(R.string.sure_find_watch);
                }
                break;
            case 2:
                tv.setText(R.string.remote_shutdown);
                tv_content.setText(R.string.sure_remote_shutdown);
                break;
            case 3:
                tv.setText(R.string.remote_reset);
                tv_content.setText(R.string.sure_remote_reset);
                break;
            case 4:
                tv.setText(R.string.remote_restore);
                tv_content.setText(R.string.sure_remote_restore);
                break;
        }
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (index) {
                    case 0:
                        checkPhoneNumber();
                        break;
                    case 1:
                        SendDeviceCommand("Find");
                        break;
                    case 2:
                        SendDeviceCommand("PowerOff");
                        break;
                    case 3:
                        SendDeviceCommand("DeviceReset", "1");
                        break;
                    case 4:
                        SendDeviceCommand("DeviceRecovery", "1");
                        break;
                }
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void checkPhoneNumber() {
        final AppData appData = AppData.GetInstance(this);
        if (CommUtil.isBlank(appData.getPhoneNumber())) {
            Utils.showNotifyDialog(this, 0, R.string.user_phone_number_monitor, R.string.confirm, R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WatchSetting.this, AddContactsA.class);
                    intent.putExtra("bindNumber", appData.getBindNumber());
                    startActivity(intent);
                    Utils.closeNotifyDialog();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.closeNotifyDialog();
                }
            }, 0, null);
        } else {
            SendDeviceCommand("Monitor", AppData.GetInstance(mContext).getLoginName());
        }
    }

    private void SendDeviceCommand(String commandType) {
        WebService ws = new WebService(mContext, _SendDeviceCommand, true, "SendDeviceCommand");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        property.add(new WebServiceProperty("commandType", commandType));

        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void SendDeviceCommand(String commandType, String paramter) {
        WebService ws = new WebService(mContext, _SendDeviceCommand, true, "SendDeviceCommand");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        property.add(new WebServiceProperty("commandType", commandType));
        property.add(new WebServiceProperty("paramter", paramter));

        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void GetDeviceSet() {
        WebService ws = new WebService(mContext, _GetDeviceSet, false, "GetDeviceSet");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void UpdateDeviceSet() {
        String setInfo;

        setInfo = mWatchSetModel.getMsgVibrate() + "-"
                + mWatchSetModel.getMsgSound() + "-"
                + mWatchSetModel.getCallVibrate() + "-"
                + mWatchSetModel.getCallSound() + "-"
                + (cb_watch_off_alarm.isChecked() ? "1" : "0") + "-"
                + (cb_refused_stranger.isChecked() ? "1" : "0") + "-"
                + (cb_timing_starting.isChecked() ? "1" : "0") + "-"
                + (cb_class_disabled.isChecked() ? "1" : "0") + "-"
                + (cb_reserved_power.isChecked() ? "1" : "0") + "-"
                + (cb_somatosensory_answer.isChecked() ? "1" : "0") + "-"
                + (cb_report_call_location.isChecked() ? "1" : "0") + "-"
                + (cb_automatic_answer.isChecked() ? "1" : "0");

        WebService ws = new WebService(mContext, _UpdateDeviceSet, true, "UpdateDeviceSet");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
        property.add(new WebServiceProperty("setInfo", setInfo));
        if (!tv_bright_time.getText().toString().trim().equals(mWatchSetModel.getBrightScreen() + getResources().getString(R.string.second))) {
            String brightScreen;
            brightScreen = tv_bright_time.getText().toString().trim().replaceAll(getResources().getString(R.string.second), "");
            property.add(new WebServiceProperty("brightScreen", brightScreen));
        }
        if (isChangeData[10]) {
            property.add(new WebServiceProperty("language", String.valueOf(getItem(languageDatas, tv_language.getText().toString()) + 1)));
        }
        if (isChangeData[9]) {
            property.add(new WebServiceProperty("timeZone", String.valueOf(timeZoneMintue[getItem(timezoneDatas, tv_timezone.getText().toString())])));
        }
        if (isChangeData[11]) {
            property.add(new WebServiceProperty("flowerNumber", String.valueOf(getItem(flowerDatas, tv_flower.getText().toString()))));
        }
        if (isChangeData[12]) {
            switch (getItem(workmodeDatas, tv_work_mode.getText().toString())) {
                case 0:
                    property.add(new WebServiceProperty("locationMode", "0"));
                    property.add(new WebServiceProperty("locationTime", "0"));
                    break;
                case 1:
                    property.add(new WebServiceProperty("locationMode", "1"));
                    property.add(new WebServiceProperty("locationTime", "3"));
                    break;
                case 2:
                    property.add(new WebServiceProperty("locationMode", "1"));
                    property.add(new WebServiceProperty("locationTime", "10"));
                    break;
                case 3:
                    property.add(new WebServiceProperty("locationMode", "1"));
                    property.add(new WebServiceProperty("locationTime", "60"));
                    break;
            }
        }
        property.add(new WebServiceProperty("sosMsgswitch", cb_sos_message.isChecked() ? "0" : "1"));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _UpdateDeviceSet = 0;
    private final int _GetDeviceSet = 1;
    private final int _SendDeviceCommand = 2;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _UpdateDeviceSet) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    MToast.makeText(R.string.edit_suc).show();
                    mWatchSetModel.setAutoAnswer((cb_automatic_answer.isChecked() ? "1" : "0"));
                    mWatchSetModel.setReportLocation((cb_report_call_location.isChecked() ? "1" : "0"));
                    mWatchSetModel.setSomatoAnswer((cb_somatosensory_answer.isChecked() ? "1" : "0"));
                    mWatchSetModel.setReservedPower((cb_reserved_power.isChecked() ? "1" : "0"));
                    mWatchSetModel.setClassDisabled((cb_class_disabled.isChecked() ? "1" : "0"));
                    mWatchSetModel.setTimeSwitch((cb_timing_starting.isChecked() ? "1" : "0"));
                    mWatchSetModel.setRefusedStranger((cb_refused_stranger.isChecked() ? "1" : "0"));
                    mWatchSetModel.setWatchOffAlarm(cb_watch_off_alarm.isChecked() ? "1" : "0");
                    mWatchSetModel.setBrightScreen(tv_bright_time.getText().toString().trim().replaceAll(getResources().getString(R.string.second), ""));
                    mWatchSetModel.setFlowerNumber(String.valueOf(getItem(flowerDatas, tv_flower.getText().toString())));
                    switch (getItem(workmodeDatas, tv_work_mode.getText().toString())) {
                        case 0:
                            mWatchSetModel.setLocationMode("0");
                            mWatchSetModel.setLocationTime("0");
                            break;
                        case 1:
                            mWatchSetModel.setLocationMode("1");
                            mWatchSetModel.setLocationTime("3");
                            break;
                        case 2:
                            mWatchSetModel.setLocationMode("1");
                            mWatchSetModel.setLocationTime("10");
                            break;
                        case 3:
                            mWatchSetModel.setLocationMode("1");
                            mWatchSetModel.setLocationTime("60");
                            break;
                    }
                    mWatchSetModel.setSosMsgswitch(cb_sos_message.isChecked() ? "0" : "1");
                    WatchSetDao mWatchSetDao = new WatchSetDao(this);
                    mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    // -1输入参数错误，2取不到数据，其他小于0系统异常，大于0常规异常
                    MToast.makeText(R.string.edit_fail).show();
                }
            } else if (id == _GetDeviceSet) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    String[] strs = jsonObject.getString("SetInfo").split("-");
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
                    mWatchSetModel.setClassDisableda(jsonObject.getString("ClassDisabled1"));
                    mWatchSetModel.setClassDisabledb(jsonObject.getString("ClassDisabled2"));
                    mWatchSetModel.setWeekDisabled(jsonObject.getString("WeekDisabled"));
                    mWatchSetModel.setTimerOpen(jsonObject.getString("TimerOpen"));
                    mWatchSetModel.setTimerClose(jsonObject.getString("TimerClose"));
                    mWatchSetModel.setBrightScreen(jsonObject.getString("BrightScreen"));
                    mWatchSetModel.setWeekAlarm1(jsonObject.getString("WeekAlarm1"));
                    mWatchSetModel.setWeekAlarm2(jsonObject.getString("WeekAlarm2"));
                    mWatchSetModel.setWeekAlarm3(jsonObject.getString("WeekAlarm3"));
                    mWatchSetModel.setAlarm1(jsonObject.getString("Alarm1"));
                    mWatchSetModel.setAlarm2(jsonObject.getString("Alarm2"));
                    mWatchSetModel.setAlarm3(jsonObject.getString("Alarm3"));
                    mWatchSetModel.setLocationMode(jsonObject.getString("LocationMode"));
                    mWatchSetModel.setLocationTime(jsonObject.getString("LocationTime"));
                    mWatchSetModel.setFlowerNumber(jsonObject.getString("FlowerNumber"));
                    if (new Contents().canLanguageTimeZone) {
                        mWatchSetModel.setLanguage(jsonObject.getString("Language"));
                        mWatchSetModel.setTimeZone(jsonObject.getString("TimeZone"));
                    }
                    mWatchSetModel.setCreateTime(jsonObject.getString("CreateTime"));
                    mWatchSetModel.setUpdateTime(jsonObject.getString("UpdateTime"));
                    mWatchSetModel.setSleepCalculate(jsonObject.getString("SleepCalculate"));
                    mWatchSetModel.setStepCalculate(jsonObject.getString("StepCalculate"));
                    mWatchSetModel.setHrCalculate(jsonObject.getString("HrCalculate"));
                    mWatchSetModel.setSosMsgswitch(jsonObject.getString("SosMsgswitch"));
                    WatchSetDao mWatchSetDao = new WatchSetDao(this);
                    mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
                    initData();
                } else {
                    // -1输入参数错误，2取不到数据，其他小于0系统异常，大于0常规异常
                }
            } else if (id == _SendDeviceCommand) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    // 1成功
                    MToast.makeText(R.string.send_order_suc).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(R.string.send_order_fail).show();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isHave(String str, String index) {
        if (str.indexOf(index) != -1) {
            return true;
        } else {
            return false;
        }
    }
}