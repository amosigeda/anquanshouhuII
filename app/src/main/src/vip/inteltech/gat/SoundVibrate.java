package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class SoundVibrate extends BaseActivity implements OnClickListener,
		WebServiceListener{
	private SoundVibrate mContext;
	private CheckBox cb_call_sound, cb_call_vibrate, cb_msg_sound, cb_msg_vibrate;
	private WatchSetModel mWatchSetModel;
	private TextView tv_watch_call, tv_watch_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sound_vibrate);
        mContext = this;
        
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        
        cb_call_sound = (CheckBox) findViewById(R.id.cb_call_sound);
        cb_call_vibrate = (CheckBox) findViewById(R.id.cb_call_vibrate);
        cb_msg_sound = (CheckBox) findViewById(R.id.cb_msg_sound);
        cb_msg_vibrate = (CheckBox) findViewById(R.id.cb_msg_vibrate);
        tv_watch_call = (TextView) findViewById(R.id.tv_watch_call);
        tv_watch_msg = (TextView) findViewById(R.id.tv_watch_msg);
        
        initData();
    }
    private void initData(){
    	WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
    	cb_call_sound.setChecked(mWatchSetModel.getCallSound().equals("1")?true:false);
    	cb_call_vibrate.setChecked(mWatchSetModel.getCallVibrate().equals("1")?true:false);
    	cb_msg_sound.setChecked(mWatchSetModel.getMsgSound().equals("1")?true:false);
    	cb_msg_vibrate.setChecked(mWatchSetModel.getMsgVibrate().equals("1")?true:false);
    	if(mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
    		tv_watch_call.setText(R.string.locator_call);
    		tv_watch_msg.setText(R.string.locator_msg);
    	}
    	findViewById(R.id.rl_call_vibrate).setVisibility(View.GONE);
    	findViewById(R.id.rl_msg_vibrate).setVisibility(View.GONE);
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		case R.id.save:
			UpdateDeviceSet();
			break;
		}
	}
	private void UpdateDeviceSet() {
		String setInfo;
		setInfo = mWatchSetModel.getAutoAnswer() + "-"
				+ mWatchSetModel.getReportLocation() + "-"
				+ mWatchSetModel.getSomatoAnswer() + "-"
				+ mWatchSetModel.getReservedPower() + "-"
				+ mWatchSetModel.getClassDisabled() + "-"
				+ mWatchSetModel.getTimeSwitch() + "-"
				+ mWatchSetModel.getRefusedStranger() + "-"
				+ mWatchSetModel.getWatchOffAlarm() + "-"
				+ (cb_call_sound.isChecked()?"1":"0") + "-"
				+ (cb_call_vibrate.isChecked()?"1":"0") + "-"
				+ (cb_msg_sound.isChecked()?"1":"0") + "-"
				+ (cb_msg_vibrate.isChecked()?"1":"0");
		
		setInfo = (cb_msg_vibrate.isChecked()?"1":"0") + "-"
				+ (cb_msg_sound.isChecked()?"1":"0") + "-"
				+ (cb_call_vibrate.isChecked()?"1":"0") + "-"
				+ (cb_call_sound.isChecked()?"1":"0") + "-"
				+ mWatchSetModel.getWatchOffAlarm() + "-"
				+ mWatchSetModel.getRefusedStranger() + "-"
				+ mWatchSetModel.getTimeSwitch() + "-"
				+ mWatchSetModel.getClassDisabled() + "-"
				+ mWatchSetModel.getReservedPower() + "-"
				+ mWatchSetModel.getSomatoAnswer() + "-"
				+ mWatchSetModel.getReportLocation() + "-"
				+ mWatchSetModel.getAutoAnswer();
		
		WebService ws = new WebService(mContext, _UpdateDeviceSet,true, "UpdateDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		property.add(new WebServiceProperty("setInfo", setInfo));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
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
					mWatchSetModel.setCallSound((cb_call_sound.isChecked()?"1":"0"));
					mWatchSetModel.setCallVibrate((cb_call_vibrate.isChecked()?"1":"0"));
					mWatchSetModel.setMsgSound((cb_msg_sound.isChecked()?"1":"0"));
					mWatchSetModel.setMsgVibrate((cb_msg_vibrate.isChecked()?"1":"0"));
					
					WatchSetDao mWatchSetDao = new WatchSetDao(this);
					mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
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
}
