package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.ChangeTimeDialog;
import vip.inteltech.gat.viewutils.ChangeTimeDialog.OnTimeListener;
import vip.inteltech.gat.viewutils.MToast;


public class HomeInfo extends BaseActivity implements OnClickListener,
        WebServiceListener {
    private HomeInfo mContext;
    private Button btn_time;
    private TextView tv_adress;
    private WatchModel mWatchModel;
    private WatchSetModel mWatchSetModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.homeinfo);
        mContext = this;
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        mWatchModel = AppContext.getInstance().getmWatchModel();
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.rl_location).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        tv_adress = (TextView) findViewById(R.id.tv_adress);
        btn_time = (Button) findViewById(R.id.btn_time);
        btn_time.setOnClickListener(this);
        btn_time.setText(mWatchModel.getLastestTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                //updateDevice();
                finish();
                break;
            case R.id.btn_time:
                chooseGradeDialog(btn_time);
                break;
            case R.id.rl_location:
                Intent intent_a = new Intent(mContext, SetLocation.class);
                intent_a.putExtra("isHome", true);
                startActivity(intent_a);
                break;
            case R.id.save:
                updateDevice();
                break;
        }
    }

    private ChangeTimeDialog mChangeTimeDialog;

    public void chooseGradeDialog(final Button btn) {
        if (mChangeTimeDialog != null)
            mChangeTimeDialog.cancel();
        mChangeTimeDialog = new ChangeTimeDialog(this, R.string.set_time);
        String hour = "18", min = "00";
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
        }
        mChangeTimeDialog.setHour(hour);
        mChangeTimeDialog.setMin(min);
        mChangeTimeDialog.show();
        mChangeTimeDialog.setTimeListener(new OnTimeListener() {

            @Override
            public void onClick(String hour, String min) {
                // TODO Auto-generated method stub
                /*Toast.makeText(HomeInfo.this,
						hour+":"+min,
						Toast.LENGTH_LONG).show();*/

                btn.setText(hour + ":" + min);
                if ((hour + ":" + min).equals(mWatchModel.getLastestTime())) {
                    isLastTime = false;
                } else {
                    isLastTime = true;
                }
            }

        });
    }

    private boolean isLastTime = false;

    private void updateDevice() {
        if (isLastTime) {
            String tm = Constants.DEFAULT_BLANK;
            String[] sp = mWatchSetModel.getClassDisabledb().split("-");
            if (sp.length >= 2) {
                tm = sp[1];
            }
            if (!DateConversion.timeComparison(tm, btn_time.getText().toString().trim())) {
                MToast.makeText(R.string.lasttime_early_classover).show();
                return;
            }
            //WebServiceUtils.updateDeviceForHomeInfo(mContext, _UpdateDevice, btn_time.getText().toString().trim());
            WebService ws = new WebService(mContext, _UpdateDevice, true, "UpdateDevice");
            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
            property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
            property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
            property.add(new WebServiceProperty("latestTime", btn_time.getText().toString().trim()));
            ws.addWebServiceListener(mContext);
            ws.SyncGet(property);
        } else {
            finish();
        }
		/*finish();*/
    }

    private int _UpdateDevice = 0;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _UpdateDevice) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //MToast.makeText(jsonObject.getString("Message")).show();
                    mWatchModel.setLastestTime(btn_time.getText().toString().trim());

                    WatchDao mWatchDao = new WatchDao(this);
                    mWatchDao.updateWatch(AppData.GetInstance(mContext).getSelectDeviceId(), mWatchModel);
                    MToast.makeText(jsonObject.getString("Message")).show();
                    AppContext.getInstance().setmWatchModel(mWatchModel);
                    finish();
                    //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    // -2系统异常
                    MToast.makeText(R.string.edit_fail).show();
                } /*else if (code == -2) {
					// -2系统异常
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -3) {
					// -3无权操作设备
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -1){
					// -1设备参数错误
				} else if (code == 0){
					// 0登录异常
				}*/
				/*finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
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
        tv_adress.setText(mWatchModel.getHomeAddress());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //updateDevice();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
