package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class WatchFareEdit extends Activity implements OnClickListener,WebServiceListener {
	private WatchFareEdit mContext;
	private EditText et_operator_number, et_fare_order, et_flow_order;
	private ImageView iv1, iv2, iv3;
	private WatchModel mWatchModel;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.watch_fare_edit);
		mContext = this;
		mWatchModel = AppContext.getInstance().getmWatchModel();
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_next).setOnClickListener(this);
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv3 = (ImageView) findViewById(R.id.iv3);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.textView_Title);
		
		et_operator_number = (EditText) findViewById(R.id.et_operator_number);
		et_fare_order = (EditText) findViewById(R.id.et_fare_order);
		et_flow_order = (EditText) findViewById(R.id.et_flow_order);
		
		et_operator_number.addTextChangedListener(new TextWatcher() {
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
				if (TextUtils.isEmpty(et_operator_number.getText().toString().trim())) {
					iv1.setVisibility(View.INVISIBLE);
				}
			}
		});
		et_fare_order.addTextChangedListener(new TextWatcher() {
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
				if (TextUtils.isEmpty(et_fare_order.getText().toString().trim())) {
					iv2.setVisibility(View.INVISIBLE);
				}
			}
		});
		et_flow_order.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				iv3.setVisibility(View.VISIBLE);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(et_flow_order.getText().toString().trim())) {
					iv3.setVisibility(View.INVISIBLE);
				}
			}
		});
		initView();
	}
	private void initView(){
		et_operator_number.setText(mWatchModel.getSmsNumber());
		et_fare_order.setText(mWatchModel.getSmsBalanceKey());
		et_flow_order.setText(mWatchModel.getSmsFlowKey());
        if(!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
    		tv_title.setText(R.string.locator_fare);
    	}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_next:
			UpdateSmsOrder();
			break;
		case R.id.iv1:
			et_operator_number.getText().clear();
			break;
		case R.id.iv2:
			et_fare_order.getText().clear();
			break;
		case R.id.iv3:
			et_flow_order.getText().clear();
			break;
		}
	}

	private void UpdateSmsOrder() {
		String smsNumber = et_operator_number.getText().toString().trim();
		String smsBalanceKey = et_fare_order.getText().toString().trim();
		String smsFlowKey = et_flow_order.getText().toString().trim();
		if(TextUtils.isEmpty(smsNumber)){
			return;
		}else if(TextUtils.isEmpty(smsBalanceKey)){
			return;
		}else if(TextUtils.isEmpty(smsFlowKey)){
			return;
		}
		WebService ws = new WebService(mContext, _UpdateSmsOrder, true, "UpdateSmsOrder");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		property.add(new WebServiceProperty("smsNumber", smsNumber));
		property.add(new WebServiceProperty("smsBalanceKey", smsBalanceKey));
		property.add(new WebServiceProperty("smsFlowKey", smsFlowKey));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}

	private int _UpdateSmsOrder = 0;

	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _UpdateSmsOrder) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					mWatchModel.setSmsNumber(et_operator_number.getText().toString().trim());
					mWatchModel.setSmsBalanceKey(et_fare_order.getText().toString().trim());
					mWatchModel.setSmsFlowKey(et_flow_order.getText().toString().trim());
					WatchDao mWatchDao = new WatchDao(mContext);
					mWatchDao.updateWatch(mWatchModel.getId(), mWatchModel);
					setResult(RESULT_OK);
					finish();
				} else {
					MToast.makeText(R.string.edit_fail).show();
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
}
