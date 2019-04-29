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
import android.widget.CheckBox;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.DateConversion;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.ChangeTimeDialog;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.ChangeTimeDialog.OnTimeListener;


public class ClassDisable extends BaseActivity implements OnClickListener, WebServiceListener{
	private ClassDisable mContext;
	private Button btn_time_a, btn_time_b, btn_time_c, btn_time_d;
	private CheckBox cb_sunday, cb_monday, cb_thesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday;
	private WatchSetModel mWatchSetModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.class_disabled);
        mContext = this;

        mWatchSetModel = AppContext.getInstance().getSelectWatchSet();

        findViewById(R.id.btn_left).setOnClickListener(this);
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
    private void initView(){
    	
    	String[] ClassDisableA = mWatchSetModel.getClassDisableda().split("-");
    	btn_time_a.setText(ClassDisableA[0]);
    	btn_time_b.setText(ClassDisableA[1]);
    	String[] ClassDisableB = mWatchSetModel.getClassDisabledb().split("-");
    	btn_time_c.setText(ClassDisableB[0]);
    	btn_time_d.setText(ClassDisableB[1]);
    	
    	String str = mWatchSetModel.getWeekDisabled();
    	cb_sunday.setChecked(isHave(str,"7"));
    	cb_monday.setChecked(isHave(str,"1"));
    	cb_thesday.setChecked(isHave(str,"2"));
    	cb_wednesday.setChecked(isHave(str,"3"));
    	cb_thursday.setChecked(isHave(str,"4"));
    	cb_friday.setChecked(isHave(str,"5"));
    	cb_saturday.setChecked(isHave(str,"6"));

    }
    private boolean isHave(String str,String index){
    	if(str.indexOf(index) != -1){
    		return true;
    	}else{
    		return false;
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
		case R.id.btn_time_a:
			chooseGradeDialog(0, btn_time_a.getText().toString().trim());
			break;
		case R.id.btn_time_b:
			chooseGradeDialog(1, btn_time_b.getText().toString().trim());
			break;
		case R.id.btn_time_c:
			chooseGradeDialog(2, btn_time_c.getText().toString().trim());
			break;
		case R.id.btn_time_d:
			chooseGradeDialog(3, btn_time_d.getText().toString().trim());
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
					btn_time_a.setText(hour + ":" + min);
					break;
				case 1:
					btn_time_b.setText(hour + ":" + min);
					break;
				case 2:
					btn_time_c.setText(hour + ":" + min);
					break;
				case 3:
					btn_time_d.setText(hour + ":" + min);
					break;
				}
			}

		});
	}
	private void UpdateDeviceSet() {
		if(!DateConversion.timeComparison(btn_time_a.getText().toString().trim(), btn_time_b.getText().toString().trim())){
			MToast.makeText(R.string.select_right_time).show();
			return;
		}
		if(!DateConversion.timeComparison(btn_time_c.getText().toString().trim(), btn_time_d.getText().toString().trim())){
			MToast.makeText(R.string.select_right_time).show();
			return;
		}
//		if(!DateConversion.timeComparison(btn_time_b.getText().toString().trim(), "12:01")){
//			MToast.makeText(R.string.select_right_time).show();
//			return;
//		}
//		if(!DateConversion.timeComparison("11:59", btn_time_c.getText().toString().trim())){
//			MToast.makeText(R.string.select_right_time).show();
//			return;
//		}
		WebService ws = new WebService(mContext, _UpdateDeviceSet, true, "UpdateDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		property.add(new WebServiceProperty("classDisable1", btn_time_a.getText().toString().trim() + "-" + btn_time_b.getText().toString().trim()));
		property.add(new WebServiceProperty("classDisable2", btn_time_c.getText().toString().trim() + "-" + btn_time_d.getText().toString().trim()));
		
		String WeekDisabled;
		WeekDisabled = (cb_monday.isChecked()?"1":"") 
				+ (cb_thesday.isChecked()?"2":"") + (cb_wednesday.isChecked()?"3":"") + 
				(cb_thursday.isChecked()?"4":"") + (cb_friday.isChecked()?"5":"") + 
				(cb_saturday.isChecked()?"6":"") + (cb_sunday.isChecked()?"7":"");
		property.add(new WebServiceProperty("weekDisable", WeekDisabled));
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
					MToast.makeText(R.string.edit_suc).show();
					mWatchSetModel.setClassDisableda(btn_time_a.getText().toString().trim() + "-" + btn_time_b.getText().toString().trim());
					mWatchSetModel.setClassDisabledb(btn_time_c.getText().toString().trim() + "-" + btn_time_d.getText().toString().trim());
					mWatchSetModel.setWeekDisabled((cb_monday.isChecked()?"1":"") 
						+ (cb_thesday.isChecked()?"2":"") + (cb_wednesday.isChecked()?"3":"") + 
						(cb_thursday.isChecked()?"4":"") + (cb_friday.isChecked()?"5":"") + 
						(cb_saturday.isChecked()?"6":"") + (cb_sunday.isChecked()?"7":""));
					
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
