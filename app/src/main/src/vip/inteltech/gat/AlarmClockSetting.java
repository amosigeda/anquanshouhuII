package vip.inteltech.gat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.CommUtil;
import vip.inteltech.gat.viewutils.ChangeTimeDialog;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.ChangeTimeDialog.OnTimeListener;


public class AlarmClockSetting extends BaseActivity implements OnClickListener {
    private AlarmClockSetting mContext;
    private Button btn_time;
    private CheckBox cb_sunday, cb_monday, cb_thesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday;
    private WatchSetModel mWatchSetModel;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarm_clock_setting);
        mContext = this;

        Intent intent = getIntent();
        value = intent.getStringExtra("alarmIntent");
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();

        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        btn_time = (Button) findViewById(R.id.btn_time);
        btn_time.setOnClickListener(this);

        cb_sunday = (CheckBox) findViewById(R.id.cb_sunday);
        cb_monday = (CheckBox) findViewById(R.id.cb_monday);
        cb_thesday = (CheckBox) findViewById(R.id.cb_thesday);
        cb_wednesday = (CheckBox) findViewById(R.id.cb_wednesday);
        cb_thursday = (CheckBox) findViewById(R.id.cb_thursday);
        cb_friday = (CheckBox) findViewById(R.id.cb_friday);
        cb_saturday = (CheckBox) findViewById(R.id.cb_saturday);
        initView();
    }

    private void initView() {
        String str = "";

        if (value.equals("1")) {
            btn_time.setText(mWatchSetModel.getAlarm1());
            str = mWatchSetModel.getWeekAlarm1().substring(mWatchSetModel.getWeekAlarm1().indexOf(":") + 1, mWatchSetModel.getWeekAlarm1().length());
        } else if (value.equals("2")) {
            btn_time.setText(mWatchSetModel.getAlarm2());
            str = mWatchSetModel.getWeekAlarm2().substring(mWatchSetModel.getWeekAlarm2().indexOf(":") + 1, mWatchSetModel.getWeekAlarm2().length());
        } else if (value.equals("3")) {
            btn_time.setText(mWatchSetModel.getAlarm3());
            str = mWatchSetModel.getWeekAlarm3().substring(mWatchSetModel.getWeekAlarm3().indexOf(":") + 1, mWatchSetModel.getWeekAlarm3().length());
        }

        cb_sunday.setChecked(isHave(str, "7"));
        cb_monday.setChecked(isHave(str, "1"));
        cb_thesday.setChecked(isHave(str, "2"));
        cb_wednesday.setChecked(isHave(str, "3"));
        cb_thursday.setChecked(isHave(str, "4"));
        cb_friday.setChecked(isHave(str, "5"));
        cb_saturday.setChecked(isHave(str, "6"));
    }

    private boolean isHave(String str, String index) {
        if (str.indexOf(index) != -1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.save:
                SaveSetting();
                break;
            case R.id.btn_time:
                chooseGradeDialog(0, btn_time.getText().toString().trim());
                break;
        }
    }

    private ChangeTimeDialog mChangeTimeDialog;

    public void chooseGradeDialog(final int btnIndex, String str) {
        if (mChangeTimeDialog != null) {
            mChangeTimeDialog.cancel();
        }
        mChangeTimeDialog = new ChangeTimeDialog(this, R.string.set_time);
        String hour, min;
        if (!TextUtils.isEmpty(str)) {
            String[] strs = str.split(":");
            if (strs.length != 2) {
                hour = "00";
                min = "00";
            } else {
                hour = strs[0];
                min = strs[1];
            }
        } else {
            hour = "00";
            min = "00";
        }
        //mChangeTimeDialog.setTitle(R.string.set_time);

        mChangeTimeDialog.setHour(hour);
        mChangeTimeDialog.setMin(min);
        mChangeTimeDialog.show();
        mChangeTimeDialog.setTimeListener(new OnTimeListener() {

            @Override
            public void onClick(String hour, String min) {
                // TODO Auto-generated method stub
                switch (btnIndex) {
                    case 0:
                        btn_time.setText(hour + ":" + min);
                        break;
                }
            }

        });
    }

    private void SaveSetting() {
        String str = "";

        if (cb_monday.isChecked()) {
            str = str + "1";
        }
        if (cb_thesday.isChecked()) {
            str = str + "2";
        }
        if (cb_wednesday.isChecked()) {
            str = str + "3";
        }
        if (cb_thursday.isChecked()) {
            str = str + "4";
        }
        if (cb_friday.isChecked()) {
            str = str + "5";
        }
        if (cb_saturday.isChecked()) {
            str = str + "6";
        }
        if (cb_sunday.isChecked()) {
            str = str + "7";
        }

        if (TextUtils.isEmpty(str)) {
            str = "0";
        }

        if (value.equals("1")) {
            mWatchSetModel.setAlarm1(btn_time.getText().toString().trim());
            mWatchSetModel.setWeekAlarm1((CommUtil.isBlank(mWatchSetModel.getWeekAlarm1()) ? "0" : mWatchSetModel.getWeekAlarm1()).charAt(0) + ":" + str);
        } else if (value.equals("2")) {
            mWatchSetModel.setAlarm2(btn_time.getText().toString().trim());
            mWatchSetModel.setWeekAlarm2((CommUtil.isBlank(mWatchSetModel.getWeekAlarm2()) ? "0" : mWatchSetModel.getWeekAlarm2()).charAt(0) + ":" + str);
        } else if (value.equals("3")) {
            mWatchSetModel.setAlarm3(btn_time.getText().toString().trim());
            mWatchSetModel.setWeekAlarm3((CommUtil.isBlank(mWatchSetModel.getWeekAlarm3()) ? "0" : mWatchSetModel.getWeekAlarm3()).charAt(0) + ":" + str);
        }

        MToast.makeText(R.string.save_suc).show();
        finish();
    }
}
