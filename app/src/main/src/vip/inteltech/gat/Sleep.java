package vip.inteltech.gat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.Window;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.DateConversion;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.ChangeTimeDialog;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.ChangeTimeDialog.OnTimeListener;

public class Sleep extends BaseActivity implements OnClickListener, WebServiceListener{
	private Sleep mContext;
	private Button btn_time_1_1, btn_time_1_2, btn_time_2_1, btn_time_2_2;
	private Button save;
	private TextView tv_sleep_quality;
	private WatchSetModel mWatchSetModel;
	private WatchStateModel mWatchStateModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sleep);
        mContext = this;

        findViewById(R.id.btn_left).setOnClickListener(this);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);

        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
        mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
        tv_sleep_quality = (TextView) findViewById(R.id.tv_sleep_quality);
        btn_time_1_1 = (Button) findViewById(R.id.btn_time_1_1);
        btn_time_1_2 = (Button) findViewById(R.id.btn_time_1_2);
        btn_time_2_1 = (Button) findViewById(R.id.btn_time_2_1);
        btn_time_2_2 = (Button) findViewById(R.id.btn_time_2_2);
        btn_time_1_1.setOnClickListener(this);
        btn_time_1_2.setOnClickListener(this);
        btn_time_2_1.setOnClickListener(this);
        btn_time_2_2.setOnClickListener(this);

        initUI();
	}
	private void initUI(){
		String str = mWatchSetModel.getSleepCalculate();
		String sleepTime = str.substring(str.indexOf("|") + 1, str.length());
		String[] sleepTimes = sleepTime.split("\\|");
		String[] sleepTimes_1 = sleepTimes[0].split("-");
		String[] sleepTimes_2 = sleepTimes[1].split("-");
		btn_time_1_1.setText(sleepTimes_1[0]);
		btn_time_1_2.setText(sleepTimes_1[1]);
    	btn_time_2_1.setText(sleepTimes_2[0]);
    	btn_time_2_2.setText(sleepTimes_2[1]);

    	if (str.toCharArray()[0] == '1' && !TextUtils.isEmpty(mWatchStateModel.getHealth())) {
			if (mWatchStateModel.getHealth().toCharArray()[1] == ':') {
				String[] heartRate = mWatchStateModel.getHealth().split(":");
				if (heartRate[0].equals("1")) {
					tv_sleep_quality.setText(R.string.sleep_quality_good);
				} else if (heartRate[0].equals("2")) {
					tv_sleep_quality.setText(R.string.sleep_quality_normal);
				} else if (heartRate[0].equals("3")) {
					tv_sleep_quality.setText(R.string.sleep_quality_bad);
				}
			}
    	}

    	if (!isInTimes()) {
    		tv_sleep_quality.setText(R.string.unknow);
    	}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		case R.id.save:
			String str;
			if(!DateConversion.timeComparison(btn_time_1_1.getText().toString().trim(), btn_time_1_2.getText().toString().trim())){
				MToast.makeText(R.string.select_right_time).show();
				return;
			}
			if(!DateConversion.timeComparison(btn_time_2_1.getText().toString().trim(), btn_time_2_2.getText().toString().trim())){
				MToast.makeText(R.string.select_right_time).show();
				return;
			}
			if (mWatchSetModel.getSleepCalculate().toCharArray()[0] == '1') {
				str = "1|" + btn_time_1_1.getText() + "-" + btn_time_1_2.getText() + "|" + btn_time_2_1.getText() + "-" + btn_time_2_2.getText();
			} else {
				str = "0|" + btn_time_1_1.getText() + "-" + btn_time_1_2.getText() + "|" + btn_time_2_1.getText() + "-" + btn_time_2_2.getText();
			}
			if (!mWatchSetModel.getSleepCalculate().equals(str)) {
				mWatchSetModel.setSleepCalculate(str);
				WatchSetDao mWatchSetDao = new WatchSetDao(this);
				mWatchSetDao.updateWatchSet(AppData.GetInstance(this).getSelectDeviceId(), mWatchSetModel);
				if (str.toCharArray()[0] == '1') {
					UpdateDeviceSet();
				}
			}
			MToast.makeText(R.string.save_suc).show();
			break;
		case R.id.btn_time_1_1:
			chooseTimeDialog(0, btn_time_1_1.getText().toString().trim());
			break;
		case R.id.btn_time_1_2:
			chooseTimeDialog(1, btn_time_1_2.getText().toString().trim());
			break;
		case R.id.btn_time_2_1:
			chooseTimeDialog(2, btn_time_2_1.getText().toString().trim());
			break;
		case R.id.btn_time_2_2:
			chooseTimeDialog(3, btn_time_2_2.getText().toString().trim());
			break;
		}
	}
	private ChangeTimeDialog mChangeTimeDialog;
	public void chooseTimeDialog(final int btnIndex, String str)
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
					btn_time_1_1.setText(hour + ":" + min);
					break;
				case 1:
					btn_time_1_2.setText(hour + ":" + min);
					break;
				case 2:
					btn_time_2_1.setText(hour + ":" + min);
					break;
				case 3:
					btn_time_2_2.setText(hour + ":" + min);
					break;
				}
			}
		});
	}

	private final int _UpdateDeviceSet = 1;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(id == _UpdateDeviceSet){
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					// 1成功
					if (!isInTimes()) {
			    		tv_sleep_quality.setText(R.string.unknow);
			    	}
				} else {
					// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void UpdateDeviceSet() {
		WebService ws = new WebService(mContext, _UpdateDeviceSet,true, "UpdateDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		property.add(new WebServiceProperty("sleepCalculate", mWatchSetModel.getSleepCalculate()));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	
	private boolean isInTimes() {
		boolean result1 = false, result2 = false;
		String currTime = DateConversion.getTime().substring(DateConversion.getTime().indexOf(" ") + 1, DateConversion.getTime().length());
		String[] str = currTime.split(":");
		currTime = str[0] + ":" + str[1];

		if (btn_time_1_1.getText().toString().trim().equals(currTime)) {
			result1 = true;
		} else if (DateConversion.timeComparison(btn_time_1_1.getText().toString().trim(), currTime) && 
				DateConversion.timeComparison(currTime, btn_time_1_2.getText().toString().trim())) {
			result1 = true;
		}
		if (btn_time_2_1.getText().toString().trim().equals(currTime)) {
			result2 = true;
		} else if (DateConversion.timeComparison(btn_time_2_1.getText().toString().trim(), currTime) && 
				DateConversion.timeComparison(currTime, btn_time_2_2.getText().toString().trim())) {
			result2 = true;
		}

		if (result1 || result2) {
			return true;
		}
		return false;
	}
}