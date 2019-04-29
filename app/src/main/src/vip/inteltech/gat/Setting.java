package vip.inteltech.gat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.db.ChatMsgDao;
import vip.inteltech.gat.db.MsgRecordDao;
import vip.inteltech.gat.db.SMSDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.service.MService;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.viewutils.MToast;

public class Setting extends BaseActivity implements OnClickListener,
		WebServiceListener {
	private Setting mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		mContext = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.rl_msg_notification).setOnClickListener(this);
		findViewById(R.id.rl_change_pwd).setOnClickListener(this);
		findViewById(R.id.rl_cache).setOnClickListener(this);
		findViewById(R.id.rl_update).setOnClickListener(this);
		findViewById(R.id.rl_common_problem).setOnClickListener(this);
		findViewById(R.id.rl_about).setOnClickListener(this);
		findViewById(R.id.rl_help).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);
		findViewById(R.id.rl_map_setting).setOnClickListener(this);
		findViewById(R.id.rl_version).setOnClickListener(this);
		/*if(!Contents.isZJT){
			findViewById(R.id.rl_common_problem).setVisibility(View.GONE);
			findViewById(R.id.rl_about).setVisibility(View.GONE);
		}*/
		findViewById(R.id.rl_common_problem).setVisibility(View.GONE);
		findViewById(R.id.rl_about).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_msg_notification:
			startActivity(new Intent(mContext, MsgNoti.class));
			break;
		case R.id.rl_change_pwd:
			startActivity(new Intent(mContext, ChangePwd.class));
			break;
		case R.id.rl_cache:
			cacheDialog();
			break;
		case R.id.rl_update:
			CheckAppVersion();
			break;
		case R.id.rl_common_problem:
			break;
		case R.id.rl_about:
			break;
		case R.id.rl_help:
			startActivity(new Intent(mContext, Help.class));
			break;
		case R.id.btn_logout:
			makeSureDialog();
			break;
		case R.id.rl_map_setting:
			startActivity(new Intent(mContext, MapSetting.class));
			break;
		case R.id.rl_version:
			versionDialog();
			break;
		}
	}

	private Dialog dialog;
	
	private void versionDialog(){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_info, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
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
		tv.setText(R.string.app_version);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(R.string.version)+" : "+getVersionName());
		Button btn_OK;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_OK.setOnClickListener(new OnClickListener() {
			
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
	
	private void makeSureDialog(){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
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
		tv.setText(R.string.logout);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(R.string.sure_logout);
		Button btn_OK, btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				logout();
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
	
	private void updateDialog(String title, String content) {
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
		/*
		 * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
		 * getWindowManager().getDefaultDisplay().getHeight()/2;
		 */
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv = (TextView) view.findViewById(R.id.tv);
		tv.setText(title);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(content);
		Button btn_OK, btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downUrl));
				AppData.GetInstance(mContext).setLoginAuto(false);
				startActivity(intent);
				/*Intent intent = new Intent(mContext, UpdateService.class);
				intent.putExtra("Key_App_Name", appName);
				intent.putExtra("Key_Down_Url", downUrl);
				startService(intent);*/
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

	/******* down APP name ******/
	public static String appName = "ZJT";
	/******* down APP address *******/
	public static String downUrl = "";

	private void cacheDialog() {
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
		/*
		 * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
		 * getWindowManager().getDefaultDisplay().getHeight()/2;
		 */
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv = (TextView) view.findViewById(R.id.tv);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv.setText(R.string.cache);
		WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
		if(mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
			tv_content.setText(R.string.cache_PS_1);
		}else{
			tv_content.setText(R.string.cache_PS);
		}
		Button btn_OK, btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				File file = new File(getFilesDir().getAbsolutePath() + "/TestRecord");
				DeleteVoiceFile(file);
				ChatMsgDao mChatMsgDao = new ChatMsgDao(mContext);
				mChatMsgDao.clearChatMsg();
				AppContext.getInstance().setChatMsgList(mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId()));
				MsgRecordDao mMsgRecordDao = new MsgRecordDao(mContext);
				mMsgRecordDao.clearMsgRecord();
				SMSDao mSMSDao = new SMSDao(mContext);
				mSMSDao.clearChatMsg();
				MToast.makeText(R.string.cache_success).show();
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

	public void DeleteVoiceFile(File file) {

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					DeleteVoiceFile(f);
				}
				file.delete();
			}
		}
	}

	private void CheckAppVersion() {
		WebService ws = new WebService(mContext, _CheckAppVersion, true, "CheckAppVersion");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}

	private void logout() {
		stopService(new Intent(this, MService.class));
		WebService ws = new WebService(mContext, _Logout, true, "LoginOut");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
		AppContext.getEventBus().post(Constants.DEFAULT_OBJECT,Constants.EVENT_LOGOUT_DIRECTLY);
	}

	private final int _Logout = 0;
	private final int _CheckAppVersion = 1;

	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		if (id == _Logout) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					
				} else {
					// 系统异常小于0，常规异常大于0
					/*if (TextUtils.isEmpty(jsonObject.getString("Message")))
						MToast.makeText(jsonObject.getString("Message")).show();*/
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
			}
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (id == _CheckAppVersion) {
					int code = jsonObject.getInt("Code");
					if (code == 1) {
						if(getVersionId() < jsonObject.getInt("AndroidVersion")){
							downUrl = jsonObject.getString("AndroidUrl");
							updateDialog(mContext.getResources().getString(R.string.have_new_version), jsonObject.getString("AndroidDescription"));
						}else{
							MToast.makeText(R.string.is_new_version).show();
						}
					} else {
                        MToast.makeText(R.string.is_new_version).show();
						// 系统异常小于0，常规异常大于0
						/*if (TextUtils.isEmpty(jsonObject.getString("Message")))
							MToast.makeText(jsonObject.getString("Message")).show();*/
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				//updateDialog("","");
			}
		}
		
	}

	public double getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			double version = Double.valueOf(info.versionCode);
		return  version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
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
	public String getVersionName() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String versionName = info.versionName;
		return  versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
		 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
			 finish();
			 overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		     return true;   
		 }
		    return super.onKeyDown(keyCode, event);
    }
}
