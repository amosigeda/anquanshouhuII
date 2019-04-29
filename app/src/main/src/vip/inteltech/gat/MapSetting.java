package vip.inteltech.gat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class MapSetting extends BaseActivity implements OnClickListener,
		WebServiceListener {
	private ImageView iv_choose1, iv_choose2;
	private RelativeLayout rl_Google, rl_GaoDe;
	private MapSetting mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_setting);
		mContext = this;
		initComponent();

		setCheck();

	}

	private void initComponent() {
		iv_choose1 = (ImageView) findViewById(R.id.iv_choose1);
		iv_choose2 = (ImageView) findViewById(R.id.iv_choose2);

		rl_GaoDe = (RelativeLayout) findViewById(R.id.rl_GaoDe);
		rl_Google = (RelativeLayout) findViewById(R.id.rl_Google);

		findViewById(R.id.btn_left).setOnClickListener(this);
		rl_GaoDe.setOnClickListener(this);
		rl_Google.setOnClickListener(this);

	}

	private void setCheck() {
		int select = AppData.GetInstance(this).getMapSelect();
		if (select == 1) {
			iv_choose1.setVisibility(View.VISIBLE);
			iv_choose2.setVisibility(View.GONE);
		} else {
			iv_choose1.setVisibility(View.GONE);
			iv_choose2.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			UpdateNotification();
			finish();
			//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.rl_GaoDe:
			if(AppData.GetInstance(this).getMapSelect() != 1)
			{
				AppData.GetInstance(this).setMapSelect(1);
				setCheck();
				MToast.makeText(R.string.map_change_prompt).show();
			}
		//	System.exit(0);
			break;
		case R.id.rl_Google:
			
			boolean googleserviceFlag = true;
			GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
			int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
			if(resultCode != ConnectionResult.SUCCESS)
			{
/*				if(googleApiAvailability.isUserResolvableError(resultCode))
				{
					googleApiAvailability.getErrorDialog(this,resultCode, 2404).show();
				}*/
				googleserviceFlag=false;
			}
			if(googleserviceFlag==false){
				//	说明不支持google服务
				MToast.makeText(R.string.not_support_prompt).show();		
			}
			else
			{
				if(AppData.GetInstance(this).getMapSelect() != 2)
				{
					AppData.GetInstance(this).setMapSelect(2);
					setCheck();
					MToast.makeText(R.string.map_change_prompt).show();
				}
			}

		//	System.exit(0);
			break;
		}
	}

	private void UpdateNotification() {
		// WebServiceUtils.UpdateNotification(mContext, _UpdateNotification,
		// "1", cb_sound.isChecked()?"1":"0", cb_vibrate.isChecked()?"1":"0");
		/*
		 * WebService ws = new WebService(mContext, _UpdateNotification,false,
		 * "UpdateNotification"); List<WebServiceProperty> property = new
		 * LinkedList<WebServiceProperty>(); property.add(new
		 * WebServiceProperty("loginId",
		 * AppData.GetInstance(this).getLoginId())); property.add(new
		 * WebServiceProperty("notification", "1")); property.add(new
		 * WebServiceProperty("notificationSound",
		 * cb_sound.isChecked()?"1":"0")); property.add(new
		 * WebServiceProperty("notificationVibration",
		 * cb_vibrate.isChecked()?"1":"0")); ws.addWebServiceListener(mContext);
		 * ws.SyncGet(property);
		 */
	}

	private final int _UpdateNotification = 0;

	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			UpdateNotification();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
