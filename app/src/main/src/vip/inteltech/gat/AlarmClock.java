package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class AlarmClock extends BaseActivity implements OnClickListener, WebServiceListener {
    private AlarmClock mContext;
    private CheckBox cb_alarm_clock_1, cb_alarm_clock_2, cb_alarm_clock_3;
    private TextView tv_clock_time_1, tv_clock_time_2, tv_clock_time_3;
    private TextView tv_clock_week_1, tv_clock_week_2, tv_clock_week_3;
    private WatchSetModel mWatchSetModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarm_clock);
        mContext = this;

        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.rl_alarm_clock_1).setOnClickListener(this);
        findViewById(R.id.rl_alarm_clock_2).setOnClickListener(this);
        findViewById(R.id.rl_alarm_clock_3).setOnClickListener(this);

        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();

        tv_clock_time_1 = (TextView) findViewById(R.id.tv_clock_time_1);
        tv_clock_time_2 = (TextView) findViewById(R.id.tv_clock_time_2);
        tv_clock_time_3 = (TextView) findViewById(R.id.tv_clock_time_3);
        tv_clock_week_1 = (TextView) findViewById(R.id.tv_clock_week_1);
        tv_clock_week_2 = (TextView) findViewById(R.id.tv_clock_week_2);
        tv_clock_week_3 = (TextView) findViewById(R.id.tv_clock_week_3);

        cb_alarm_clock_1 = (CheckBox) findViewById(R.id.cb_alarm_clock_1);
        cb_alarm_clock_2 = (CheckBox) findViewById(R.id.cb_alarm_clock_2);
        cb_alarm_clock_3 = (CheckBox) findViewById(R.id.cb_alarm_clock_3);

        //initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        if (TextUtils.isEmpty(mWatchSetModel.getAlarm1())) {
            mWatchSetModel.setAlarm1("00:00");
        }
        if (TextUtils.isEmpty(mWatchSetModel.getAlarm2())) {
            mWatchSetModel.setAlarm2("00:00");
        }
        if (TextUtils.isEmpty(mWatchSetModel.getAlarm3())) {
            mWatchSetModel.setAlarm3("00:00");
        }
        if (TextUtils.isEmpty(mWatchSetModel.getWeekAlarm1())) {
            mWatchSetModel.setWeekAlarm1("0:0");
        }
        if (mWatchSetModel.getWeekAlarm1().indexOf(":") == -1) {
            mWatchSetModel.setWeekAlarm1("0:0");
        }
        if (TextUtils.isEmpty(mWatchSetModel.getWeekAlarm2())) {
            mWatchSetModel.setWeekAlarm2("0:0");
        }
        if (mWatchSetModel.getWeekAlarm2().indexOf(":") == -1) {
            mWatchSetModel.setWeekAlarm2("0:0");
        }
        if (TextUtils.isEmpty(mWatchSetModel.getWeekAlarm3())) {
            mWatchSetModel.setWeekAlarm3("0:0");
        }
        if (mWatchSetModel.getWeekAlarm3().indexOf(":") == -1) {
            mWatchSetModel.setWeekAlarm3("0:0");
        }
        tv_clock_time_1.setText(mWatchSetModel.getAlarm1());
        tv_clock_time_2.setText(mWatchSetModel.getAlarm2());
        tv_clock_time_3.setText(mWatchSetModel.getAlarm3());
        tv_clock_week_1.setText(getWeekString(mWatchSetModel.getWeekAlarm1().substring(mWatchSetModel.getWeekAlarm1().indexOf(":") + 1, mWatchSetModel.getWeekAlarm1().length())));
        tv_clock_week_2.setText(getWeekString(mWatchSetModel.getWeekAlarm2().substring(mWatchSetModel.getWeekAlarm2().indexOf(":") + 1, mWatchSetModel.getWeekAlarm2().length())));
        tv_clock_week_3.setText(getWeekString(mWatchSetModel.getWeekAlarm3().substring(mWatchSetModel.getWeekAlarm3().indexOf(":") + 1, mWatchSetModel.getWeekAlarm3().length())));
        cb_alarm_clock_1.setChecked(mWatchSetModel.getWeekAlarm1().toCharArray()[0] == '0' ? false : true);
        cb_alarm_clock_2.setChecked(mWatchSetModel.getWeekAlarm2().toCharArray()[0] == '0' ? false : true);
        cb_alarm_clock_3.setChecked(mWatchSetModel.getWeekAlarm3().toCharArray()[0] == '0' ? false : true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.save:
                UpdateDeviceSet();
                break;
            case R.id.rl_alarm_clock_1:
                startIntent("1");
                break;
            case R.id.rl_alarm_clock_2:
                startIntent("2");
                break;
            case R.id.rl_alarm_clock_3:
                startIntent("3");
                break;
        }
    }

    private final int _UpdateDeviceSet = 0;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _UpdateDeviceSet) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    MToast.makeText(R.string.edit_suc).show();
                    if (cb_alarm_clock_1.isChecked()) {
                        mWatchSetModel.setWeekAlarm1("1" + mWatchSetModel.getWeekAlarm1().substring(mWatchSetModel.getWeekAlarm1().indexOf(":"), mWatchSetModel.getWeekAlarm1().length()));
                    } else {
                        mWatchSetModel.setWeekAlarm1("0" + mWatchSetModel.getWeekAlarm1().substring(mWatchSetModel.getWeekAlarm1().indexOf(":"), mWatchSetModel.getWeekAlarm1().length()));
                    }
                    if (cb_alarm_clock_2.isChecked()) {
                        mWatchSetModel.setWeekAlarm2("1" + mWatchSetModel.getWeekAlarm2().substring(mWatchSetModel.getWeekAlarm2().indexOf(":"), mWatchSetModel.getWeekAlarm2().length()));
                    } else {
                        mWatchSetModel.setWeekAlarm2("0" + mWatchSetModel.getWeekAlarm2().substring(mWatchSetModel.getWeekAlarm2().indexOf(":"), mWatchSetModel.getWeekAlarm2().length()));
                    }
                    if (cb_alarm_clock_3.isChecked()) {
                        mWatchSetModel.setWeekAlarm3("1" + mWatchSetModel.getWeekAlarm3().substring(mWatchSetModel.getWeekAlarm3().indexOf(":"), mWatchSetModel.getWeekAlarm3().length()));
                    } else {
                        mWatchSetModel.setWeekAlarm3("0" + mWatchSetModel.getWeekAlarm3().substring(mWatchSetModel.getWeekAlarm3().indexOf(":"), mWatchSetModel.getWeekAlarm3().length()));
                    }

                    WatchSetDao mWatchSetDao = new WatchSetDao(this);
                    mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
                    AppContext.getInstance().setSelectWatchSet(mWatchSetModel);
                    finish();
                } else {
                    // -1输入参数错误，2取不到数据，其他小于0系统异常，大于0常规异常
                    MToast.makeText(R.string.edit_fail).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getWeekString(String str) {
        int i;
        String week_str = "";
        for (i = 0; i < str.length(); i++) {
            switch (str.toCharArray()[i]) {
                case '1':
                    week_str = week_str + getResources().getString(R.string.week_monday) + " ";
                    break;
                case '2':
                    week_str = week_str + getResources().getString(R.string.week_thesday) + " ";
                    break;
                case '3':
                    week_str = week_str + getResources().getString(R.string.week_wednesday) + " ";
                    break;
                case '4':
                    week_str = week_str + getResources().getString(R.string.week_thursday) + " ";
                    break;
                case '5':
                    week_str = week_str + getResources().getString(R.string.week_friday) + " ";
                    break;
                case '6':
                    week_str = week_str + getResources().getString(R.string.week_saturday) + " ";
                    break;
                case '7':
                    week_str = week_str + getResources().getString(R.string.week_sunday) + " ";
                    break;
            }
        }
        return week_str;
    }

    private void startIntent(String para) {
        Intent intent = new Intent();
        intent.putExtra("alarmIntent", para);
        intent.setClass(mContext, AlarmClockSetting.class);
        startActivity(intent);
    }

    private void UpdateDeviceSet() {
        int idx1 = mWatchSetModel.getWeekAlarm1().indexOf(":");
        int idx2 = mWatchSetModel.getWeekAlarm2().indexOf(":");
        int idx3 = mWatchSetModel.getWeekAlarm3().indexOf(":");
        if (idx1 < 0 || idx2 < 0 || idx3 < 0) {
            return;
        }

        WebService ws = new WebService(mContext, _UpdateDeviceSet, true, "UpdateDeviceSet");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));

        property.add(new WebServiceProperty("weekAlarm1", (cb_alarm_clock_1.isChecked() ? "1" : "0") + mWatchSetModel.getWeekAlarm1().substring(idx1, mWatchSetModel.getWeekAlarm1().length())));
        property.add(new WebServiceProperty("weekAlarm2", (cb_alarm_clock_2.isChecked() ? "1" : "0") + mWatchSetModel.getWeekAlarm2().substring(idx2, mWatchSetModel.getWeekAlarm2().length())));
        property.add(new WebServiceProperty("weekAlarm3", (cb_alarm_clock_3.isChecked() ? "1" : "0") + mWatchSetModel.getWeekAlarm3().substring(idx3, mWatchSetModel.getWeekAlarm3().length())));

        property.add(new WebServiceProperty("alarm1", mWatchSetModel.getAlarm1()));
        property.add(new WebServiceProperty("alarm2", mWatchSetModel.getAlarm2()));
        property.add(new WebServiceProperty("alarm3", mWatchSetModel.getAlarm3()));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }
}
