package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebServiceUtils;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;
import com.zbar.lib.MCaptureActivity;


public class RegistD extends Activity implements OnClickListener, WebServiceListener{
	private RegistD mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.regist_d);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_bound).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_bound:
			Intent intent_a = new Intent(mContext, MCaptureActivity.class);
			startActivityForResult(intent_a, ADDWATCH);
			break;
		}
	}
	private void LinkDeviceCheck(){
		WebServiceUtils.LinkDeviceCheck(mContext, _LinkDeviceCheck, serialNumber, mContext);
	}
	private void getWatchList(){
		WebService ws = new WebService(mContext, _GetDeviceList, getResources().getString(R.string.loading_watch_list), "GetDeviceList");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _LinkDeviceCheck = 2;
	private final int _LinkDevice = 1;
	private final int _GetDeviceList = 3;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _LinkDeviceCheck) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					//1表示未关联，选择头像输入名字就完成首次关联
					Intent intent = new Intent(mContext, AddContactsA.class);
					intent.putExtra("typeIndex", 2);
					intent.putExtra("serialNumber", serialNumber);
					startActivityForResult(intent, ADDWATCHS);
				} else if (code == 2) {
					// 2表示已经关联，输入名字后请求管理员确认
					editDialog();
					//MToast.makeText(jsonObject.getString("Message")).show();
				}   else if(code == 3) {
					// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
					MToast.makeText(R.string.device_no_exist).show();
				}/*else {
					// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
					MToast.makeText(jsonObject.getString("Message")).show();
				} */
			}else if (id == _LinkDevice) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					//1成功
					String deviceId = jsonObject.getString("DeviceID");
					if(Integer.valueOf(deviceId)<=0){
						//MToast.makeText(R.string.wait_admin_confirm).show();
						MToast.makeText(jsonObject.getString("Message")).show();
						finish();
					}else{
						MToast.makeText(R.string.bind_suc).show();
						getWatchList();
					}
				} else {
					// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
					MToast.makeText(R.string.bind_fail).show();
				} 
			}else if(id == _GetDeviceList){
				int code = jsonObject.getInt("Code");
				if(code == 1){
					List<WatchModel> mWatchList = new ArrayList<WatchModel>();
					List<ContactModel> mContactModelList = new ArrayList<ContactModel>();
					JSONArray arr = jsonObject.getJSONArray("deviceList");
					AppData.GetInstance(mContext).setSelectDeviceId(arr.getJSONObject(0).getInt("DeviceID"));
					
					for(int i = 0; i < arr.length();i++){
						JSONObject item = arr.getJSONObject(i);
						WatchModel mWatchModel = new WatchModel();
						mWatchModel.setId(item.getInt("DeviceID"));
						mWatchModel.setUserId(item.getInt("UserId"));
						mWatchModel.setModel(item.getString("DeviceModelID"));
						mWatchModel.setName(item.getString("BabyName"));
						mWatchModel.setAvatar(item.getString("Photo"));
						mWatchModel.setPhone(item.getString("PhoneNumber"));
						mWatchModel.setCornet(item.getString("PhoneCornet"));
						mWatchModel.setGender(item.getString("Gender"));
						mWatchModel.setBirthday(item.getString("Birthday"));
						mWatchModel.setGrade(item.getInt("Grade"));
						mWatchModel.setHomeAddress(item.getString("HomeAddress"));
						mWatchModel.setHomeLat(item.getDouble("HomeLat"));
						mWatchModel.setHomeLng(item.getDouble("HomeLng"));
						mWatchModel.setSchoolAddress(item.getString("SchoolAddress"));
						mWatchModel.setSchoolLat(item.getDouble("SchoolLat"));
						mWatchModel.setSchoolLng(item.getDouble("SchoolLng"));
						mWatchModel.setLastestTime(item.getString("LatestTime"));
						mWatchModel.setSetVersionNO(item.getString("SetVersionNO"));
						mWatchModel.setContactVersionNO(item.getString("ContactVersionNO"));
						mWatchModel.setOperatorType(item.getString("OperatorType"));
						mWatchModel.setSmsNumber(item.getString("SmsNumber"));
						mWatchModel.setSmsBalanceKey(item.getString("SmsBalanceKey"));
						mWatchModel.setSmsFlowKey(item.getString("SmsFlowKey"));
						mWatchModel.setActiveDate(item.getString("ActiveDate"));
						mWatchModel.setCreateTime(item.getString("CreateTime"));
						mWatchModel.setBindNumber(item.getString("BindNumber"));
						mWatchModel.setCurrentFirmware(item.getString("CurrentFirmware"));
						mWatchModel.setFirmware(item.getString("Firmware"));
						mWatchModel.setHireExpireDate(item.getString("HireExpireDate"));
						mWatchModel.setUpdateTime(item.getString("UpdateTime"));
						mWatchModel.setSerialNumber(item.getString("SerialNumber"));
						mWatchModel.setPassword(item.getString("Password"));
						mWatchModel.setIsGuard(item.getString("IsGuard").equals("1")?true:false);
						mWatchModel.setDeviceType(item.getString("DeviceType"));
						mWatchList.add(mWatchModel);

						JSONObject deviceSet = item.getJSONObject("DeviceSet");
						WatchSetModel mWatchSetModel = new WatchSetModel();
						mWatchSetModel.setDeviceId(item.getInt("DeviceID"));
						String[] strs = deviceSet.getString("SetInfo").split("-");
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
						mWatchSetModel.setClassDisableda(deviceSet.getString("ClassDisabled1"));
						mWatchSetModel.setClassDisabledb(deviceSet.getString("ClassDisabled2"));
						mWatchSetModel.setWeekDisabled(deviceSet.getString("WeekDisabled"));
						mWatchSetModel.setTimerOpen(deviceSet.getString("TimerOpen"));
						mWatchSetModel.setTimerClose(deviceSet.getString("TimerClose"));
						mWatchSetModel.setBrightScreen(deviceSet.getString("BrightScreen"));
						mWatchSetModel.setWeekAlarm1(deviceSet.getString("WeekAlarm1"));
						mWatchSetModel.setWeekAlarm2(deviceSet.getString("WeekAlarm2"));
						mWatchSetModel.setWeekAlarm3(deviceSet.getString("WeekAlarm3"));
						mWatchSetModel.setAlarm1(deviceSet.getString("Alarm1"));
						mWatchSetModel.setAlarm2(deviceSet.getString("Alarm2"));
						mWatchSetModel.setAlarm3(deviceSet.getString("Alarm3"));
						mWatchSetModel.setLocationMode(deviceSet.getString("LocationMode"));
						mWatchSetModel.setLocationTime(deviceSet.getString("LocationTime"));
						mWatchSetModel.setFlowerNumber(deviceSet.getString("FlowerNumber"));
						//mWatchSetModel.setLanguage(deviceSet.getString("Language"));
						//mWatchSetModel.setTimeZone(deviceSet.getString("TimeZone"));
						mWatchSetModel.setCreateTime(deviceSet.getString("CreateTime"));
						mWatchSetModel.setUpdateTime(deviceSet.getString("UpdateTime"));
						//mWatchSetModel.setVersionNumber(deviceSet.getString("VersionNumber"));
						mWatchSetModel.setSleepCalculate(deviceSet.getString("SleepCalculate"));
						mWatchSetModel.setStepCalculate(deviceSet.getString("StepCalculate"));
						mWatchSetModel.setHrCalculate(deviceSet.getString("HrCalculate"));
						mWatchSetModel.setSosMsgswitch(deviceSet.getString("SosMsgswitch"));
						
						WatchSetDao mWatchDao = new WatchSetDao(this);
						mWatchDao.saveWatchSet(mWatchSetModel);
						
						JSONObject deviceState = item.getJSONObject("DeviceState");
						WatchStateModel mWatchStateModel = new WatchStateModel();
						mWatchStateModel.setDeviceId(item.getInt("DeviceID"));
						if(!TextUtils.isEmpty(deviceState.getString("Altitude"))){
							mWatchStateModel.setAltitude(deviceState.getDouble("Altitude"));
						}
						if(!TextUtils.isEmpty(deviceState.getString("Latitude"))){
							mWatchStateModel.setLatitude(deviceState.getDouble("Latitude"));
						}
						if(!TextUtils.isEmpty(deviceState.getString("Longitude"))){
							mWatchStateModel.setLongitude(deviceState.getDouble("Longitude"));
						}
						mWatchStateModel.setCourse(deviceState.getString("Course"));
						mWatchStateModel.setElectricity(deviceState.getString("Electricity"));
						mWatchStateModel.setStep(deviceState.getString("Step"));
						mWatchStateModel.setHealth(deviceState.getString("Health"));
						mWatchStateModel.setOnline(deviceState.getString("Online"));
						mWatchStateModel.setSpeed(deviceState.getString("Speed"));
						mWatchStateModel.setSatelliteNumber(deviceState.getString("SatelliteNumber"));
						//mWatchStateModel.setSocketId(deviceState.getString("SocketId"));
						mWatchStateModel.setCreateTime(deviceState.getString("CreateTime"));
						mWatchStateModel.setServerTime(deviceState.getString("ServerTime"));
						mWatchStateModel.setUpdateTime(deviceState.getString("UpdateTime"));
						mWatchStateModel.setDeviceTime(deviceState.getString("DeviceTime"));
						mWatchStateModel.setLocationType(deviceState.getString("LocationType"));
						/*mWatchStateModel.setLBS(deviceState.getString("LBS"));
						mWatchStateModel.setGSM(deviceState.getString("GSM"));
						mWatchStateModel.setWifi(deviceState.getString("Wifi"));*/
						
						WatchStateDao mWatchStateDao = new WatchStateDao(this);
						mWatchStateDao.saveWatchState(mWatchStateModel);
						
						JSONArray arrContact = item.getJSONArray("ContactArr");
						for(int j = 0; j < arrContact.length(); j++){
							JSONObject items = arrContact.getJSONObject(j);
							ContactModel mContactModel = new ContactModel();
							mContactModel.setId(items.getString("DeviceContactId"));
							mContactModel.setFromId(item.getInt("DeviceID"));
							mContactModel.setObjectId(items.getString("ObjectId"));
							mContactModel.setRelationShip(items.getString("Relationship"));
							mContactModel.setAvatar(items.getString("Photo"));
							mContactModel.setAvatarUrl(items.getString("HeadImg"));
							mContactModel.setPhone(items.getString("PhoneNumber"));
							mContactModel.setCornet(items.getString("PhoneShort"));
							mContactModel.setType(items.getString("Type"));
							mContactModelList.add(mContactModel);
						}
						ContactDao mContactDao = new ContactDao(this);
						mContactDao.deleteWatchContact(item.getInt("DeviceID"));
					}
					WatchDao dao = new WatchDao(this);
			        dao.saveWatchList(mWatchList);
			        ContactDao mContactDao = new ContactDao(this);
					mContactDao.saveContactList(mContactModelList);
					
			        AppData.GetInstance(this).setLoginAuto(true);
					startActivity(new Intent(mContext, Main.class));
					finish();
					Login.mContext.finish();
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				} else if (code == 2) {
					// 2未取到数据
					//MToast.makeText(jsonObject.getString("Message")).show();
				} 
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Dialog dialog;
	private void editDialog(){
		View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
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
		tv.setText(R.string.input_your_name);
		final EditText et = (EditText) view.findViewById(R.id.et);
		Button btn_OK,btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = et.getText().toString().trim();
				if(TextUtils.isEmpty(name)){
					return;
				}
				WebServiceUtils.LinkDevice(mContext, _LinkDevice, "-1", name, serialNumber, mContext);
				//LinkDevice("-1", name);
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
	private final int ADDWATCH = 0;
	private final int ADDWATCHS = 1;
	private String serialNumber;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADDWATCH:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("result");
				//MToast.makeText(scanResult).show();
				//scanResult = "1212121212";
				serialNumber = scanResult;
				LinkDeviceCheck();
			} 
			break;
		case ADDWATCHS:
			if (resultCode == RESULT_OK) {
				String photo = data.getStringExtra("photo");
				String name = data.getStringExtra("name");
				WebServiceUtils.LinkDevice(mContext, _LinkDevice, photo, name, serialNumber, mContext);
			} 
			break;
		default:
			break;

		}
	}
}