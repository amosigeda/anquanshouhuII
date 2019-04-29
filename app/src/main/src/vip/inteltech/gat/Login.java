package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.zbar.lib.MCaptureActivity;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MProgressDialog;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.MToast2;

@ContentView(R.layout.login)
public class Login extends BaseActivity implements OnClickListener, WebServiceListener {
    private EditText et_login_name;
    private EditText et_password;
    private ImageView iv1, iv2;
    private CheckBox cb_rem_pass;
    public static Login mContext;

    @ViewInject(R.id.login_qr_scan)
    private ImageView qrScan;

    private MProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        mContext = this;
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);

        qrScan.bringToFront();

        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_password = (EditText) findViewById(R.id.et_password);
        if (!TextUtils.isEmpty(AppData.GetInstance(mContext).getLoginName())) {
            et_login_name.setText(AppData.GetInstance(mContext).getLoginName());
        }
        if (!TextUtils.isEmpty(AppData.GetInstance(mContext).getPwd())) {
            et_password.setText(AppData.GetInstance(mContext).getPwd());
        }
        cb_rem_pass = (CheckBox) findViewById(R.id.cb_rem_pass);
        cb_rem_pass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable_Password = et_password.getText();
                    Selection.setSelection(etable_Password, etable_Password.length());
                } else {
                    //System.out.println("isChecked");
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Editable etable_Password = et_password.getText();
                    Selection.setSelection(etable_Password, etable_Password.length());
                }
            }
        });
        if (!TextUtils.isEmpty(et_login_name.getText().toString().trim())) {
            iv1.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(et_password.getText().toString().trim())) {
            iv2.setVisibility(View.VISIBLE);
        }
        et_login_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                iv1.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(et_login_name.getText().toString().trim())) {
                    iv1.setVisibility(View.INVISIBLE);
                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                iv2.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
                    iv2.setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_reg).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.dialogShown = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                logins();
                //TextDB();
            /*mMainFrameTask = new MainFrameTask(this);
            mMainFrameTask.execute();*/
                break;
            case R.id.tv_reg:
                startActivity(new Intent(mContext, RegistA.class));
                break;
            case R.id.tv_forget_pwd:
                startActivity(new Intent(mContext, ForgotPwdA.class));
                break;
            case R.id.iv1:
                et_login_name.getText().clear();
                break;
            case R.id.iv2:
                et_password.getText().clear();
        }
    }

    private void logins() {
        String login_name = et_login_name.getText().toString().trim();
        String pwd = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(login_name) || TextUtils.isEmpty(pwd)) {
            CommUtil.showMsgShort(R.string.account_note);
            return;
        }
        WebService ws = new WebService(Login.this, _Login, getResources().getString(R.string.logining), "Login");
        List<WebServiceProperty> property = new LinkedList<>();
        property.add(new WebServiceProperty("loginType", "1"));
        property.add(new WebServiceProperty("phoneNumber", login_name));
        property.add(new WebServiceProperty("passWord", pwd));
        property.add(new WebServiceProperty("project", Contents.APPName));
        String language;
        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
            language = "2";
        } else if (getResources().getConfiguration().locale.getCountry().equals("TW")) {
            language = "3";
        } else {
            language = "1";
        }
        property.add(new WebServiceProperty("language", language));
        property.add(new WebServiceProperty("version", String.valueOf(getVersionId())));
        // property.add(new WebServiceProperty("appleId", ""));
        ws.addWebServiceListener(Login.this);
        ws.SyncGet(property);
    }

    public int getVersionId() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void getWatchList() {
        WebService ws = new WebService(Login.this, _GetDeviceList, getResources().getString(R.string.loading_watch_list), "GetDeviceList");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        ws.addWebServiceListener(Login.this);
        ws.SyncGet(property);
    }

    private Dialog dialog;

    private void addWatchDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv.setText(R.string.bound_watch);
        tv_content.setText(R.string.u_no_bound_watch);

        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsUtil.requestPermission(Login.this, Manifest.permission.CAMERA, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                        dialog.cancel();
                        Intent intent_a = new Intent(mContext, MCaptureActivity.class);
                        startActivityForResult(intent_a, ADDWATCH);
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        Toast.makeText(mContext, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void startProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = MProgressDialog.createDialog(this);
            mProgressDialog.setMessage(getResources().getString(R.string.logining));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private MainFrameTask mMainFrameTask = null;

    public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
        private Login login = null;

        public MainFrameTask(Login login) {
            this.login = login;
        }

        @Override
        protected void onCancelled() {
            stopProgressDialog();
            super.onCancelled();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            startProgressDialog();
        }

        @Override
        protected void onPostExecute(Integer result) {
            stopProgressDialog();
            startActivity(new Intent(mContext, Main.class));
            finish();
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }
    }

    private final int _Login = 0;
    private final int _GetDeviceList = 1;
    private final int _LinkDeviceCheck = 2;
    private final int _LinkDevice = 3;
    private int oldUseId;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            if (result == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject(result);
            //		Log.v("kkk", ""+id+" code="+jsonObject.getInt("Code"));
            if (id == _Login) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    AppData appData = AppData.GetInstance(this);
                    oldUseId = appData.getUserId();
                    appData.setLoginName(et_login_name.getText().toString().trim());
                    appData.setPwd(et_password.getText().toString().trim());
                    appData.setLoginId(jsonObject.getString("LoginId"));
                    appData.setUserId(jsonObject.getInt("UserId"));
                    appData.setUserType(jsonObject.getInt("UserType"));
                    appData.setName(jsonObject.getString("Name"));
                    appData.setNotification(jsonObject.getBoolean("Notification"));
                    appData.setNotificationSound(jsonObject.getBoolean("NotificationSound"));
                    appData.setNotificationVibration(jsonObject.getBoolean("NotificationVibration"));
                    appData.setPhoneNumber(jsonObject.getString("PhoneNumber"));
                    appData.setBindNumber(jsonObject.getString("BindNumber"));
                    getWatchList();
                } else if (code == 0) {
                    // 0表示用户名或密码错误
                    //	MToast.makeText(R.string.login_info_error).show();
                    MToast2.makeText(this, R.string.login_info_error).show();
                } else {
                    // 系统错误
                    MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceList) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    List<WatchModel> mWatchList = new ArrayList<WatchModel>();
                    List<ContactModel> mContactModelList = new ArrayList<ContactModel>();
                    JSONArray arr = jsonObject.getJSONArray("deviceList");
                    int oldDeviceId = AppData.GetInstance(mContext).getSelectDeviceId();
                    for (int i = 0; i < arr.length(); i++) {
                        if (i == 0) {
                            AppData.GetInstance(mContext).setSelectDeviceId(arr.getJSONObject(0).getInt("DeviceID"));
                        }
                        JSONObject item = arr.getJSONObject(i);
                        WatchModel mWatchModel = new WatchModel();
                        if (oldDeviceId == item.getInt("DeviceID")) {
                            AppData.GetInstance(mContext).setSelectDeviceId(item.getInt("DeviceID"));
                        }
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
                        mWatchModel.setIsGuard(item.getString("IsGuard").equals("1") ? true : false);
                        mWatchModel.setDeviceType(item.getString("DeviceType"));
                        mWatchModel.setCloudPlatform(item.getInt("CloudPlatform"));
                        mWatchList.add(mWatchModel);

                        JSONObject deviceSet = item.getJSONObject("DeviceSet");
                        WatchSetModel mWatchSetModel = new WatchSetModel();
                        mWatchSetModel.setDeviceId(item.getInt("DeviceID"));
                        if (deviceSet.length() > 0) {
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
                            mWatchSetModel.setCreateTime(deviceSet.getString("CreateTime"));
                            mWatchSetModel.setUpdateTime(deviceSet.getString("UpdateTime"));
                            mWatchSetModel.setSleepCalculate(deviceSet.getString("SleepCalculate"));
                            mWatchSetModel.setStepCalculate(deviceSet.getString("StepCalculate"));
                            mWatchSetModel.setHrCalculate(deviceSet.getString("HrCalculate"));
                            mWatchSetModel.setSosMsgswitch(deviceSet.getString("SosMsgswitch"));
                        } else {
                            mWatchSetModel.setAutoAnswer("0");
                            mWatchSetModel.setReportLocation("0");
                            mWatchSetModel.setSomatoAnswer("0");
                            mWatchSetModel.setReservedPower("0");
                            mWatchSetModel.setClassDisabled("0");
                            mWatchSetModel.setTimeSwitch("0");
                            mWatchSetModel.setRefusedStranger("0");
                            mWatchSetModel.setWatchOffAlarm("0");
                            mWatchSetModel.setCallSound("0");
                            mWatchSetModel.setCallVibrate("0");
                            mWatchSetModel.setMsgSound("0");
                            mWatchSetModel.setMsgVibrate("0");
                            mWatchSetModel.setClassDisableda("0");
                            mWatchSetModel.setClassDisabledb("0");
                            mWatchSetModel.setWeekDisabled("0");
                            mWatchSetModel.setTimerOpen("0");
                            mWatchSetModel.setTimerClose("0");
                            mWatchSetModel.setBrightScreen("0");
                            mWatchSetModel.setWeekAlarm1("0");
                            mWatchSetModel.setWeekAlarm2("0");
                            mWatchSetModel.setWeekAlarm3("0");
                            mWatchSetModel.setAlarm1("0");
                            mWatchSetModel.setAlarm2("0");
                            mWatchSetModel.setAlarm3("0");
                            mWatchSetModel.setLocationMode("0");
                            mWatchSetModel.setLocationTime("0");
                            mWatchSetModel.setFlowerNumber("0");
                            mWatchSetModel.setCreateTime("0");
                            mWatchSetModel.setUpdateTime("0");
                            mWatchSetModel.setSleepCalculate("0");
                            mWatchSetModel.setStepCalculate("0");
                            mWatchSetModel.setHrCalculate("0");
                            mWatchSetModel.setSosMsgswitch("0");
                        }
                        WatchSetDao mWatchDao = new WatchSetDao(this);
                        mWatchDao.saveWatchSet(mWatchSetModel);

                        JSONObject deviceState = item.getJSONObject("DeviceState");
                        WatchStateModel mWatchStateModel = new WatchStateModel();
                        mWatchStateModel.setDeviceId(item.getInt("DeviceID"));
                        if (!TextUtils.isEmpty(deviceState.getString("Altitude"))) {
                            mWatchStateModel.setAltitude(deviceState.getDouble("Altitude"));
                        }
                        if (!TextUtils.isEmpty(deviceState.getString("Latitude"))) {
                            mWatchStateModel.setLatitude(deviceState.getDouble("Latitude"));
                        }
                        if (!TextUtils.isEmpty(deviceState.getString("Longitude"))) {
                            mWatchStateModel.setLongitude(deviceState.getDouble("Longitude"));
                        }
                        mWatchStateModel.setCourse(deviceState.getString("Course"));
                        mWatchStateModel.setElectricity(deviceState.getString("Electricity"));
                        mWatchStateModel.setOnline(deviceState.getString("Online"));
                        mWatchStateModel.setSpeed(deviceState.getString("Speed"));
                        mWatchStateModel.setSatelliteNumber(deviceState.getString("SatelliteNumber"));
                        mWatchStateModel.setCreateTime(deviceState.getString("CreateTime"));
                        mWatchStateModel.setServerTime(deviceState.getString("ServerTime"));
                        mWatchStateModel.setUpdateTime(deviceState.getString("UpdateTime"));
                        mWatchStateModel.setDeviceTime(deviceState.getString("DeviceTime"));
                        mWatchStateModel.setLocationType(deviceState.getString("LocationType"));

                        WatchStateDao mWatchStateDao = new WatchStateDao(this);
                        mWatchStateDao.saveWatchState(mWatchStateModel);

                        JSONArray arrContact = item.getJSONArray("ContactArr");
                        for (int j = 0; j < arrContact.length(); j++) {
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
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                } else if (code == 2) {
                    // 2未取到数据
                    addWatchDialog();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _LinkDeviceCheck) {
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
                } else if (code == 3) {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(R.string.device_no_exist).show();
                } else {
                    MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _LinkDevice) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //1成功
                    String deviceId = jsonObject.getString("DeviceID");
                    if (Integer.valueOf(deviceId) <= 0) {
                        //MToast.makeText(R.string.wait_admin_confirm).show();
                        MToast.makeText(jsonObject.getString("Message")).show();
                    } else {
                        getWatchList();
                    }
                    /*Login.mContext.finish();
                    finish();*/
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(R.string.bind_fail).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void editDialog() {
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
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
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
    private final int SCAN_QR_CODE = 2;
    private String serialNumber;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADDWATCH:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    serialNumber = scanResult;
                    WebServiceUtils.LinkDeviceCheck(mContext, _LinkDeviceCheck, serialNumber, mContext);
                }
                break;
            case ADDWATCHS:
                if (resultCode == RESULT_OK) {
                    String photo = data.getStringExtra("photo");
                    String name = data.getStringExtra("name");
                    WebServiceUtils.LinkDevice(mContext, _LinkDevice, photo, name, serialNumber, mContext);

                }
                break;
            case SCAN_QR_CODE:
                if (resultCode == RESULT_OK) {
                    et_login_name.setText(data.getExtras().getString("result"));
                }
                break;
            default:
                break;
        }
    }

    // 点击空白处隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Event(R.id.login_qr_scan)
    private void onQRScan(View v) {
        PermissionsUtil.requestPermission(this, Manifest.permission.CAMERA, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Intent intent_a = new Intent(mContext, MCaptureActivity.class);
                startActivityForResult(intent_a, SCAN_QR_CODE);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Toast.makeText(mContext, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.CAMERA.equalsIgnoreCase(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(mContext, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}