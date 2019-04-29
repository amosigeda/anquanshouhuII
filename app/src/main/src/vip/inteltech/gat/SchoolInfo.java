package vip.inteltech.gat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.ChangeTimeDialog;
import vip.inteltech.gat.viewutils.ChangeTimeDialog.OnTimeListener;
import vip.inteltech.gat.viewutils.MToast;

public class SchoolInfo extends BaseActivity implements OnClickListener,
        WebServiceListener {
    private SchoolInfo mContext;
    private Button btn_time_a, btn_time_b, btn_time_c, btn_time_d;
    private CheckBox cb_sunday, cb_monday, cb_thesday, cb_wednesday,
            cb_thursday, cb_friday, cb_saturday;
    private TextView tv_adress;

    private WatchSetModel mWatchSetModel;
    private WatchModel mWatchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.schoolinfo);
        mContext = this;

        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        mWatchModel = AppContext.getInstance().getmWatchModel();

        tv_adress = (TextView) findViewById(R.id.tv_adress);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.rl_location).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        btn_time_a = (Button) findViewById(R.id.btn_time_a);
        btn_time_b = (Button) findViewById(R.id.btn_time_b);
        btn_time_c = (Button) findViewById(R.id.btn_time_c);
        btn_time_d = (Button) findViewById(R.id.btn_time_d);
        btn_time_a.setOnClickListener(this);
        btn_time_b.setOnClickListener(this);
        btn_time_c.setOnClickListener(this);
        btn_time_d.setOnClickListener(this);

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

        String[] classDisableA = mWatchSetModel.getClassDisableda().split("-");
        if (classDisableA.length >= 2) {
            btn_time_a.setText(classDisableA[0]);
            btn_time_b.setText(classDisableA[1]);
        }
        String[] classDisableB = mWatchSetModel.getClassDisabledb().split("-");
        if (classDisableB.length >= 2) {
            btn_time_c.setText(classDisableB[0]);
            btn_time_d.setText(classDisableB[1]);
        }

        String str = mWatchSetModel.getWeekDisabled();
        cb_sunday.setChecked(isHave(str, "7"));
        cb_monday.setChecked(isHave(str, "1"));
        cb_thesday.setChecked(isHave(str, "2"));
        cb_wednesday.setChecked(isHave(str, "3"));
        cb_thursday.setChecked(isHave(str, "4"));
        cb_friday.setChecked(isHave(str, "5"));
        cb_saturday.setChecked(isHave(str, "6"));

    }

    private boolean isHave(String str, String index) {
        if (str.contains(index)) {
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
                //UpdateDeviceSet();
                break;
            case R.id.rl_location:
                Intent intent_a = new Intent(mContext, SetLocation.class);
                intent_a.putExtra("isHome", false);
                startActivity(intent_a);
                break;
            case R.id.btn_time_a:
                chooseGradeDialog(btn_time_a);
                break;
            case R.id.btn_time_b:
                chooseGradeDialog(btn_time_b);
                break;
            case R.id.btn_time_c:
                chooseGradeDialog(btn_time_c);
                break;
            case R.id.btn_time_d:
                chooseGradeDialog(btn_time_d);
                break;
            case R.id.save:
                UpdateDeviceSet();
                break;
        }
    }

    private ChangeTimeDialog mChangeTimeDialog;

    public void chooseGradeDialog(final Button btn) {
        if (mChangeTimeDialog != null)
            mChangeTimeDialog.cancel();
        if ((btn.getId() == R.id.btn_time_a) || (btn.getId() == R.id.btn_time_b)) {
            mChangeTimeDialog = new ChangeTimeDialog(this, R.string.set_time, 1);
        } else {
            mChangeTimeDialog = new ChangeTimeDialog(this, R.string.set_time, 2);
        }
        String hour, min;
        String str = btn.getText().toString().trim();
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
        mChangeTimeDialog.setHour(hour);
        mChangeTimeDialog.setMin(min);
        mChangeTimeDialog.show();
        mChangeTimeDialog.setTimeListener(new OnTimeListener() {

            @Override
            public void onClick(String hour, String min) {
                // TODO Auto-generated method stub
                /*Toast.makeText(SchoolInfo.this, hour + ":" + min,
						Toast.LENGTH_LONG).show();*/
                btn.setText(hour + ":" + min);
            }

        });
    }

    private boolean isClassA = false, isClassB = false, isWeek = false;

    private void UpdateDeviceSet() {
        if (!DateConversion.timeComparison(btn_time_a.getText().toString().trim(), btn_time_b.getText().toString().trim())) {
            MToast.makeText(R.string.select_right_time).show();
            return;
        }
        if (!DateConversion.timeComparison(btn_time_c.getText().toString().trim(), btn_time_d.getText().toString().trim())) {
            MToast.makeText(R.string.select_right_time).show();
            return;
        }
//        if (!DateConversion.timeComparison(btn_time_b.getText().toString().trim(), btn_time_c.getText().toString().trim())) {
//            MToast.makeText(R.string.select_right_time).show();
//            return;
//        }
//        if (!DateConversion.timeComparison(btn_time_d.getText().toString().trim(), mWatchModel.getLastestTime())) {
//            MToast.makeText(R.string.lasttime_early_classover).show();
//            return;
//        }
        String classDisable1 = btn_time_a.getText().toString().trim() + "-" + btn_time_b.getText().toString().trim();
        String classDisable2 = btn_time_c.getText().toString().trim() + "-" + btn_time_d.getText().toString().trim();
        String WeekDisabled;
        WeekDisabled = (cb_monday.isChecked() ? "1" : "")
                + (cb_thesday.isChecked() ? "2" : "") + (cb_wednesday.isChecked() ? "3" : "") +
                (cb_thursday.isChecked() ? "4" : "") + (cb_friday.isChecked() ? "5" : "") +
                (cb_saturday.isChecked() ? "6" : "") + (cb_sunday.isChecked() ? "7" : "");
        if (classDisable1.equals(mWatchSetModel.getClassDisableda())) {
            isClassA = false;
        } else {
            isClassA = true;
        }
        if (classDisable2.equals(mWatchSetModel.getClassDisabledb())) {
            isClassB = false;
        } else {
            isClassB = true;
        }
        if (WeekDisabled.equals(mWatchSetModel.getWeekDisabled())) {
            isWeek = false;
        } else {
            isWeek = true;
        }
        if (isClassA || isClassB || isWeek) {
            WebService ws = new WebService(mContext, _UpdateDeviceSet, true, "UpdateDeviceSet");
            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
            Map<String, String> map = new HashMap<String, String>();
            property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
            property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
            if (isClassA) {
                property.add(new WebServiceProperty("classDisable1", classDisable1));
                map.put("classDisable1", classDisable1);
            }
            if (isClassB) {
                property.add(new WebServiceProperty("classDisable2", classDisable2));
                map.put("classDisable2", classDisable2);
            }
            if (isWeek) {
                property.add(new WebServiceProperty("weekDisable", WeekDisabled));
                map.put("weekDisable", WeekDisabled);
            }
            //后台运行
            //WebServiceUtils.updateDeviceForSchoolInfo(mContext, _UpdateDeviceSet, property, map, isClassA, isClassB, isWeek);
            ws.addWebServiceListener(mContext);
            ws.SyncGet(property);
        } else {
            finish();
        }
		/*finish();
		overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);*/
    }

    private final int _UpdateDeviceSet = 0;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        // TODO Auto-generated method stub
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _UpdateDeviceSet) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //MToast.makeText(jsonObject.getString("Message")).show();
                    mWatchSetModel.setClassDisableda(btn_time_a.getText().toString().trim() + "-" + btn_time_b.getText().toString().trim());
                    mWatchSetModel.setClassDisabledb(btn_time_c.getText().toString().trim() + "-" + btn_time_d.getText().toString().trim());
                    mWatchSetModel.setWeekDisabled((cb_monday.isChecked() ? "1" : "")
                            + (cb_thesday.isChecked() ? "2" : "") + (cb_wednesday.isChecked() ? "3" : "") +
                            (cb_thursday.isChecked() ? "4" : "") + (cb_friday.isChecked() ? "5" : "") +
                            (cb_saturday.isChecked() ? "6" : "") + (cb_sunday.isChecked() ? "7" : ""));

                    WatchSetDao mWatchSetDao = new WatchSetDao(this);
                    mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
                    MToast.makeText(R.string.edit_suc).show();
                    AppContext.getInstance().setSelectWatchSet(mWatchSetModel);
                    finish();
                } else {
                    // -1输入参数错误，2取不到数据，其他小于0系统异常，大于0常规异常
                    MToast.makeText(R.string.edit_fail).show();
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        tv_adress.setText(mWatchModel.getSchoolAddress());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // UpdateDeviceSet();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
