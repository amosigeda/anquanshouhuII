package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.Contents;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.viewutils.MToast;

public class ForgotPwdB extends Activity implements OnClickListener, WebServiceListener {
	private ForgotPwdB mContext;
	String scanResult, phoneNum;

	private EditText et_pwd, et_pwds;
	private ImageView iv1, iv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgot_pwd_b);
		mContext = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);
		Intent intent = getIntent();
		phoneNum = intent.getStringExtra("phoneNum");
		scanResult = intent.getStringExtra("scanResult");
	    iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		et_pwds = (EditText) findViewById(R.id.et_pwds);
		et_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				iv1.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(et_pwd.getText().toString().trim())) {
					iv1.setVisibility(View.INVISIBLE);
				}
			}
		});
		et_pwds.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				iv2.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(et_pwds.getText().toString().trim())) {
					iv2.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_next:
			Forgot();
			break;
		case R.id.iv1:
			et_pwd.getText().clear();
			break;
		case R.id.iv2:
			et_pwds.getText().clear();
			break;
		}
	}

	private void Forgot() {
		String pwd = et_pwd.getText().toString().trim();
		String pwds = et_pwds.getText().toString().trim();
		if (TextUtils.isEmpty(scanResult)) {
			return;
		}else if (TextUtils.isEmpty(pwd)) {
			MToast.makeText(R.string.psw_empty).show();
			return;
		} else if (TextUtils.isEmpty(pwds)) {
			MToast.makeText(R.string.psw_confirm_empty).show();
			return;
		} else if (!pwd.equals(pwds)) {
			MToast.makeText(R.string.pwd_different).show();
			return;
		}

		WebService ws = new WebService(mContext, _Forgot, true, "Forgot");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("phoneNumber", phoneNum));
		property.add(new WebServiceProperty("checkNumber", ""));
		property.add(new WebServiceProperty("passWord", pwd));
		property.add(new WebServiceProperty("appleId", ""));
		property.add(new WebServiceProperty("project", Contents.APPName));
		property.add(new WebServiceProperty("SerialNumber", scanResult));
		String language;
		if(getResources().getConfiguration().locale.getCountry().equals("CN")){
			language = "2";
		}else if(getResources().getConfiguration().locale.getCountry().equals("TW")){
			language = "3";
		}else{
			language = "1";
		}
		property.add(new WebServiceProperty("language", language));
		property.add(new WebServiceProperty("version", String.valueOf(getVersionId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	public int getVersionId() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			int version = info.versionCode;
			return  version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private int _Forgot = 1;

	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _Forgot) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					AppData.GetInstance(this).setLoginId(jsonObject.getString("LoginId"));
					AppData.GetInstance(this).setUserId(jsonObject.getInt("UserId"));
					AppData.GetInstance(this).setUserId(jsonObject.getInt("UserType"));
					AppData.GetInstance(this).setName(jsonObject.getString("Name"));
					AppData.GetInstance(this).setNotification(jsonObject.getBoolean("Notification"));
					AppData.GetInstance(this).setNotificationSound(jsonObject.getBoolean("NotificationSound"));
					AppData.GetInstance(this).setNotificationVibration(jsonObject.getBoolean("NotificationVibration"));
					finish();
				}
//				else if (code == 3) {
//					// 3用户不存在
//					MToast.makeText(jsonObject.getString("Message")).show();
//				} else if (code == 4) {
//					// 4验证码错误
//					MToast.makeText(jsonObject.getString("Message")).show();
//				} else if (code == 5) {
//					// 5验证码过期
//					MToast.makeText(jsonObject.getString("Message")).show();
//				} else if (code == -1) {
//					// -1表示输入参数错误
//					MToast.makeText(jsonObject.getString("Message")).show();
//				} else if (code == -2) {
//					// -2系统错误
//					MToast.makeText(jsonObject.getString("Message")).show();
//				}
				else if (code >= -50) {
                    MToast.makeText(jsonObject.getString("Message")).show();
                }else {
                    MToast.makeText(R.string.edit_fail).show();
                }
            }
		} catch (JSONException e) {
			e.printStackTrace();
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
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
}