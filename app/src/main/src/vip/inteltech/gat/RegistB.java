package vip.inteltech.gat;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.CodeBitmap;
import vip.inteltech.gat.utils.Contents;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class RegistB extends Activity implements OnClickListener, WebServiceListener {
	private MyCount mc;  
	private RegistB mContext;
	private TextView tv_phone_num;
	private EditText et_code;
	private Button btn_get_code;
	String checkCode, phoneNum;
	
	private EditText et_pwd, et_pwds;
	private ImageView iv1, iv2, iv_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regist_b);
		mContext = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);
		btn_get_code = (Button) findViewById(R.id.btn_get_code);
		btn_get_code.setOnClickListener(this);
		btn_get_code.setPaintFlags(btn_get_code.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
		et_code = (EditText) findViewById(R.id.et_code);
		Intent intent = getIntent();
		//checkCode = intent.getStringExtra("checkCode");
		phoneNum = intent.getStringExtra("phoneNum");
		tv_phone_num.setText(phoneNum);
		et_code.setText(checkCode);
		//mc = new MyCount(60000, 1000);  
	    //mc.start();

	    iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		et_pwds = (EditText) findViewById(R.id.et_pwds);
		et_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				iv1.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

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
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				iv2.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(et_pwds.getText().toString().trim())) {
					iv2.setVisibility(View.INVISIBLE);
				}
			}
		});
		iv_code = (ImageView) findViewById(R.id.iv_code);
		iv_code.setImageBitmap(CodeBitmap.getInstance().createBitmap());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_next:
			Register();
			/*String checkCodes = et_code.getText().toString().trim();
			if (TextUtils.isEmpty(checkCodes)) {
				return;
			} else if (!checkCode.equals(checkCodes)) {
				return;
			}
			
			Intent intent_a = new Intent(mContext, RegistC.class);
			intent_a.putExtra("phoneNum", phoneNum);
			intent_a.putExtra("checkCode", checkCode);
			startActivity(intent_a);
			finish();*/
			break;
		case R.id.btn_get_code:
			ChangeCode();
			//RegisterCheck();
			break;
		case R.id.iv1:
			et_pwd.getText().clear();
			break;
		case R.id.iv2:
			et_pwds.getText().clear();
			break;
		}
	}

	private void RegisterCheck() {
		if (TextUtils.isEmpty(phoneNum)) {
			return;
		}
		WebService ws = new WebService(RegistB.this, _RegisterCheck, true, "RegisterCheck");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("phoneNumber", phoneNum));
		property.add(new WebServiceProperty("phoneCode", md5(phoneNum)));
		property.add(new WebServiceProperty("project", Contents.APPName));
		ws.addWebServiceListener(RegistB.this);
		ws.SyncGet(property);
	}
	private void ChangeCode() {
		iv_code.setImageBitmap(CodeBitmap.getInstance().createBitmap());
	}
	private void Register() {
		String checkCodes = et_code.getText().toString().trim();
		String pwd = et_pwd.getText().toString().trim();
		String pwds = et_pwds.getText().toString().trim();
		//MToast.makeText(checkCodes + " "+ pwd + " "+ pwds).show();
		if (!CodeBitmap.getInstance().getCode().equals(checkCodes)) {
			MToast.makeText(R.string.check_codes_empty).show();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			MToast.makeText(R.string.psw_empty).show();
			return;
		} else if (TextUtils.isEmpty(pwds)) {
			MToast.makeText(R.string.psw_confirm_empty).show();
			return;
		} else if (!pwd.equals(pwds)) {
			MToast.makeText(R.string.pwd_different).show();
			return;
		}

		WebService ws = new WebService(mContext, _Register, true, "Register");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("phoneNumber", phoneNum));
		property.add(new WebServiceProperty("phoneCode", md5(phoneNum)));
		//property.add(new WebServiceProperty("checkNumber", checkCodes));
		property.add(new WebServiceProperty("passWord", pwd));
		property.add(new WebServiceProperty("appleId", ""));
		property.add(new WebServiceProperty("project", Contents.APPName));
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
	private int _Register = 1;
	private int _RegisterCheck = 0;

	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _RegisterCheck) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					//MToast.makeText(R.string.have_send_verification_code).show();
					mc = new MyCount(60000, 1000);  
				    mc.start();
				} else if (code == 3) {
					// 3表示手机已经存在
					MToast.makeText(R.string.phone_have_reg).show();
				} else if (code == 4) {
					// 4验证码请求过于平凡，需要等待1分钟
					MToast.makeText(R.string.have_send_verification_code_more).show();
				} /*else if (code == -1) {
					// -1表示输入参数错误
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -2) {
					// -2系统错误
					MToast.makeText(jsonObject.getString("Message")).show();
				}else{
					MToast.makeText(jsonObject.getString("Message")).show();
				}*/
			}else if (id == _Register) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					AppData.GetInstance(this).setLoginId(jsonObject.getString("LoginId"));
					AppData.GetInstance(this).setUserId(jsonObject.getInt("UserId"));
					AppData.GetInstance(this).setUserId(jsonObject.getInt("UserType"));
					AppData.GetInstance(this).setName(jsonObject.getString("Name"));
					AppData.GetInstance(this).setNotification(jsonObject.getBoolean("Notification"));
					AppData.GetInstance(this).setNotificationSound(jsonObject.getBoolean("NotificationSound"));
					AppData.GetInstance(this).setNotificationVibration(jsonObject.getBoolean("NotificationVibration"));
					startActivity(new Intent(mContext, RegistD.class));
					finish();
				} else if (code == 3) {
					// 3表示手机已经存在
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == 4) {
					// 4验证码错误
					MToast.makeText(R.string.verification_code_error).show();
				}  else {
					// 系统错误
					String err = jsonObject.getString("Message");
					if (TextUtils.isEmpty(err)) {
						MToast.makeText(mContext.getResources().getString(R.string.unknow_error_code) + " " + code).show();
					} else {
						MToast.makeText(jsonObject.getString("Message")).show();
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*定义一个倒计时的内部类*/
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);   
            btn_get_code.setClickable(false);
    		btn_get_code.setFocusable(false);
        }
        @Override
        public void onFinish() {
        	btn_get_code.setText(mContext.getResources().getString(R.string.get_code));  
        	btn_get_code.setClickable(true);
    		btn_get_code.setFocusable(true);
        }
        @Override
        public void onTick(long millisUntilFinished) {
        	btn_get_code.setText(mContext.getResources().getString(R.string.get_code)
        			+"(" + millisUntilFinished / 1000 + mContext.getResources().getString(R.string.second) + ")");     
            //Toast.makeText(mContext, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延迟       
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

	private static String md5(String string) {
	     byte[] hash;
	     try {
	         hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	     } catch (NoSuchAlgorithmException e) {
	         throw new RuntimeException("Huh, MD5 should be supported?", e);
	     } catch (UnsupportedEncodingException e) {
	         throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	     }

	     StringBuilder hex = new StringBuilder(hash.length * 2);
	     for (byte b : hash) {
	         if ((b & 0xFF) < 0x10) hex.append("0");
	         hex.append(Integer.toHexString(b & 0xFF));
	     }
	     return hex.toString();
	 }
}