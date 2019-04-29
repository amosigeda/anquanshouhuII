package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.HealthDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.HealthModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

import android.content.Intent;
import android.app.Activity;
import android.app.Dialog;

public class Health extends BaseActivity implements OnClickListener, WebServiceListener{
	private Health mContext;
	private CheckBox cb_pedometer, cb_sleep_detection, cb_heart_rate;
	private TextView tv_sleep_detection;
	private HealthModel mHealthModel;
	private WatchSetModel mWatchSetModel;
	private boolean isD9WithoutGsensor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.health);
        mContext = this;

        if (AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D9_CHUANGMT_V0.1") != -1 &&
        		!AppContext.getInstance().getDeviceSvn().equals("640") && !AppContext.getInstance().getDeviceSvn().equals("647")) {
        	isD9WithoutGsensor = true;
        } else {
        	isD9WithoutGsensor = false;
        }

        findViewById(R.id.rl_pedometer).setOnClickListener(this);
        findViewById(R.id.rl_sleep_detection).setOnClickListener(this);
        findViewById(R.id.rl_heart_rate).setOnClickListener(this);
        if (isD9WithoutGsensor) {
        	mHealthModel = AppContext.getInstance().getSelectHealth();
        }
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();

        findViewById(R.id.btn_left).setOnClickListener(this);
        tv_sleep_detection = (TextView)findViewById(R.id.tv_sleep_detection);

        cb_pedometer = (CheckBox) findViewById(R.id.cb_pedometer);
        cb_pedometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
            	if (isChecked) {
            		if (isD9WithoutGsensor) {
            			if (mHealthModel.getPedometer().equals("0")) {
            				makeSureDialog(0);
            			}
            		} else {
            			if (mWatchSetModel.getStepCalculate().equals("0")) {
            				makeSureDialog(0);
            			}
            		}
            	} else {
            		if (isD9WithoutGsensor) {
            			if (mHealthModel.getPedometer().equals("1")) {
            				SendDeviceCommand("StepCalculate", "0");
            			}
            		} else {
            			if (mWatchSetModel.getStepCalculate().equals("1")) {
            				UpdateDeviceSet(0);
            			}
            		}
            	}
            }
        });
        cb_sleep_detection = (CheckBox) findViewById(R.id.cb_sleep_detection);
        cb_sleep_detection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
        		String str = mWatchSetModel.getSleepCalculate();
            	if (isChecked) {
            		if (str.toCharArray()[0] == '0') {
	            		makeSureDialog(1);
            		}
            	} else {
            		if (str.toCharArray()[0] == '1') {
            			UpdateDeviceSet(1);
            		}
            	}
            }
        });
        cb_heart_rate = (CheckBox) findViewById(R.id.cb_heart_rate);
        cb_heart_rate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
            	if (isChecked) {
            		if (mWatchSetModel.getHrCalculate().equals("0")) {
            			UpdateDeviceSet(2);
            		}
            	} else {
            		if (mWatchSetModel.getHrCalculate().equals("1")) {
            			UpdateDeviceSet(2);
            		}
            	}
            }
        });

        if (!AppContext.getInstance().isSupportGsensor() || AppContext.getInstance().getDeviceSvn().equals("640") || AppContext.getInstance().getDeviceSvn().equals("647")) {
        	findViewById(R.id.rl_pedometer).setVisibility(View.GONE);
        	findViewById(R.id.rl_sleep_detection).setVisibility(View.GONE);
        }
        if (!AppContext.getInstance().isSupportHeartrate()) {
        	findViewById(R.id.rl_heart_rate).setVisibility(View.GONE);
        }

        if (isD9WithoutGsensor || AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D10_CHUANGMT_V") != -1 ||
        		AppContext.getInstance().getDeviceSvn().equals("640") || AppContext.getInstance().getDeviceSvn().equals("647") ||
        		AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D9_CHUANGMT_V0.3") != -1 ||
        		AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D9_TP_CHUANGMT_V") != -1) {
        	findViewById(R.id.rl_pedometer).setVisibility(View.VISIBLE);
        }

        if (!isD9WithoutGsensor) {
        	GetDeviceSet();
        }
    }
    private void initData() {
    	if (isD9WithoutGsensor) {
    		if (TextUtils.isEmpty(mHealthModel.getPedometer())) {
    			mHealthModel.setPedometer("0");
    		}
    	} else {
    		if (TextUtils.isEmpty(mWatchSetModel.getStepCalculate())) {
    			mWatchSetModel.setStepCalculate("0");
    		}
    	}
    	if (TextUtils.isEmpty(mWatchSetModel.getSleepCalculate())) {
    		mWatchSetModel.setSleepCalculate("0|22:00-23:59|05:00-06:00");
    	}
    	if (TextUtils.isEmpty(mWatchSetModel.getHrCalculate())) {
    		mWatchSetModel.setHrCalculate("0");
    	}

    	if (isD9WithoutGsensor) {
    		if (mHealthModel.getPedometer().equals("1")) {
    			cb_pedometer.setChecked(true);
    		} else {
    			cb_pedometer.setChecked(false);
    		}
    	} else {
    		if (mWatchSetModel.getStepCalculate().equals("1")) {
    			cb_pedometer.setChecked(true);
    		} else {
    			cb_pedometer.setChecked(false);
    		}
    	}
    	if (mWatchSetModel.getSleepCalculate().toCharArray()[0] == '1') {
    		cb_sleep_detection.setChecked(true);
    	} else {
    		cb_sleep_detection.setChecked(false);
    	}
    	if (mWatchSetModel.getHrCalculate().equals("1")) {
    		cb_heart_rate.setChecked(true);
    	} else {
    		cb_heart_rate.setChecked(false);
    	}

    	tv_sleep_detection.setText(mWatchSetModel.getSleepCalculate().substring(mWatchSetModel.getSleepCalculate().indexOf("|") + 1, mWatchSetModel.getSleepCalculate().length()));
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	initData();
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		case R.id.rl_pedometer:
			mContext.startActivity(new Intent(mContext, Pedometer.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.rl_sleep_detection:
			mContext.startActivity(new Intent(mContext, Sleep.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.rl_heart_rate:
			mContext.startActivity(new Intent(mContext, HeartRate.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		}
	}

	private final int _SendDeviceCommand = 1;
	private final int _UpdateDeviceSet = 2;
	private final int _GetDeviceSet = 3;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(id == _SendDeviceCommand){
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					// 1成功
					mHealthModel.setDeviceId(AppData.GetInstance(this).getSelectDeviceId());
					if (cb_pedometer.isChecked()) {
						mHealthModel.setPedometer("1");
					} else {
						mHealthModel.setPedometer("0");
					}
					HealthDao mHealthDao = new HealthDao(this);
					mHealthDao.saveHealth(mHealthModel);
					MToast.makeText(R.string.send_order_suc).show();
				} else {
					// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
					MToast.makeText(R.string.send_order_fail).show();
				}
			}else if(id == _UpdateDeviceSet){
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					// 1成功
					String str = mWatchSetModel.getSleepCalculate();
					mWatchSetModel.setStepCalculate((cb_pedometer.isChecked()?"1":"0"));
					if (cb_pedometer.isChecked()) {
						AppContext.getInstance().getmWatchStateModel().setStep("0");
					}
					if (cb_sleep_detection.isChecked()) {
						mWatchSetModel.setSleepCalculate("1" + str.substring(str.indexOf("|"), str.length()));
					} else {
						mWatchSetModel.setSleepCalculate("0" + str.substring(str.indexOf("|"), str.length()));
					}
					mWatchSetModel.setHrCalculate((cb_heart_rate.isChecked()?"1":"0"));
					WatchSetDao mWatchSetDao = new WatchSetDao(this);
					mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
					MToast.makeText(R.string.send_order_suc).show();
				} else {
					// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
					MToast.makeText(R.string.send_order_fail).show();
				}
			}else if(id == _GetDeviceSet){
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
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private Dialog dialog;
	private void makeSureDialog(final int index){
		if(dialog != null) dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		window.setWindowAnimations(R.style.slide_up_down);
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv = (TextView) view.findViewById(R.id.tv);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		switch(index){
		case 0:
			tv.setText(R.string.pedometer);
			tv_content.setText(R.string.sure_open_pedometer);
			break;
		case 1:
			tv.setText(R.string.sleep_detection);
			tv_content.setText(R.string.sure_open_sleep);
			break;
		}
		Button btn_OK, btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(index){
				case 0:
					if (isD9WithoutGsensor) {
						SendDeviceCommand("StepCalculate", "1");
					} else {
						UpdateDeviceSet(0);
					}
					break;
				case 1:
					UpdateDeviceSet(1);
					break;
				}
				dialog.cancel();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(index){
				case 0:
					cb_pedometer.setChecked(false);
					break;
				case 1:
					cb_sleep_detection.setChecked(false);
					break;
				}
				dialog.cancel();
			}
		});
		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void UpdateDeviceSet(int mode) {
		String str = mWatchSetModel.getSleepCalculate();
		WebService ws = new WebService(mContext, _UpdateDeviceSet,true, "UpdateDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		switch(mode){
		case 0:
			if (cb_pedometer.isChecked()) {
				property.add(new WebServiceProperty("stepCalculate", "1"));
			} else {
				property.add(new WebServiceProperty("stepCalculate", "0"));
			}
			break;
		case 1:
			if (cb_sleep_detection.isChecked()) {
				property.add(new WebServiceProperty("sleepCalculate", "1" + str.substring(str.indexOf("|"), str.length())));
			} else {
				property.add(new WebServiceProperty("sleepCalculate", "0" + str.substring(str.indexOf("|"), str.length())));
			}
			break;
		case 2:
			if (cb_heart_rate.isChecked()) {
				property.add(new WebServiceProperty("hrCalculate", "1"));
			} else {
				property.add(new WebServiceProperty("hrCalculate", "0"));
			}
			break;
		}
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}

	private void GetDeviceSet(){
    	WebService ws = new WebService(mContext, _GetDeviceSet, false, "GetDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
    }
}