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
import android.widget.Button;
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
import vip.inteltech.gat.viewutils.ChangeTimeDialog;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.ChangeTimeDialog.OnTimeListener;


public class TimeSwitch extends BaseActivity implements OnClickListener, WebServiceListener{
	private TimeSwitch mContext;
	private Button btn_time_on, btn_time_off;
	private WatchSetModel mWatchSetModel;
	private TextView tv_time_on, tv_time_off, tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.time_switch);
        mContext = this;
        
        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        btn_time_on = (Button) findViewById(R.id.btn_time_on);
        btn_time_off = (Button) findViewById(R.id.btn_time_off);
        btn_time_on.setOnClickListener(this);
        btn_time_off.setOnClickListener(this);

        tv_time_on = (TextView) findViewById(R.id.tv_time_on);
        tv_time_off = (TextView) findViewById(R.id.tv_time_off);
        tv_title = (TextView) findViewById(R.id.textView_Title);

        initData();
    }
    private void initData(){
    	WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
    	btn_time_on.setText(mWatchSetModel.getTimerOpen());
    	btn_time_off.setText(mWatchSetModel.getTimerClose());
    	if(mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
    		tv_time_on.setText(R.string.turn_on_time_1);
    		tv_time_off.setText(R.string.turn_off_time_1);
    		tv_title.setText(R.string.time_turn_locator);
    	}
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
		case R.id.btn_time_on:
			chooseGradeDialog(0, btn_time_on.getText().toString().trim());
			break;
		case R.id.btn_time_off:
			chooseGradeDialog(1, btn_time_off.getText().toString().trim());
			break;
		}
	}
	private ChangeTimeDialog mChangeTimeDialog;
	public void chooseGradeDialog(final int btnIndex, String str)
	{
		if(mChangeTimeDialog != null)
			mChangeTimeDialog.cancel();
		mChangeTimeDialog = new ChangeTimeDialog(this, R.string.set_time);
		String hour,min;
		if(!TextUtils.isEmpty(str)){
			
			String[] strs = str.split(":");
			if(strs.length != 2){
				hour = "00";
				min = "00";
			}else{
				hour = strs[0];
				min = strs[1];
			}
		}else{
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
				switch (btnIndex){
				case 0:
					btn_time_on.setText(hour + ":" + min);
					break;
				case 1:
					btn_time_off.setText(hour + ":" + min);
					break;
				}
			}

		});
	}
	private void UpdateDeviceSet() {
		WebService ws = new WebService(mContext, _UpdateDeviceSet,true, "UpdateDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		property.add(new WebServiceProperty("timeClose", btn_time_off.getText().toString().trim()));
		property.add(new WebServiceProperty("timeOpen", btn_time_on.getText().toString().trim()));
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
					mWatchSetModel.setTimerOpen(btn_time_on.getText().toString().trim());
					mWatchSetModel.setTimerClose(btn_time_off.getText().toString().trim());
					
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
