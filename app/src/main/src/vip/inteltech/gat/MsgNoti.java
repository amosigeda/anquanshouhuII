package vip.inteltech.gat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebServiceUtils;
import vip.inteltech.gat.utils.WebService.WebServiceListener;


public class MsgNoti extends BaseActivity implements OnClickListener, WebServiceListener{
	private CheckBox cb_sound, cb_vibrate;
	private MsgNoti mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.msg_noti);
        mContext = this;
        
        findViewById(R.id.btn_left).setOnClickListener(this);
        
        cb_sound = (CheckBox) findViewById(R.id.cb_sound);
        cb_vibrate = (CheckBox) findViewById(R.id.cb_vibrate);
        
        initData();
    }
    private void initData(){
    	cb_sound.setChecked(AppData.GetInstance(mContext).getNotificationSound());
    	cb_vibrate.setChecked(AppData.GetInstance(mContext).getNotificationVibration());
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			UpdateNotification();
			finish();
			//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		}
	}
	private void UpdateNotification(){
		WebServiceUtils.UpdateNotification(mContext, _UpdateNotification, "1", cb_sound.isChecked()?"1":"0", cb_vibrate.isChecked()?"1":"0");
    	/*WebService ws = new WebService(mContext, _UpdateNotification,false, "UpdateNotification");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("notification", "1"));
		property.add(new WebServiceProperty("notificationSound", cb_sound.isChecked()?"1":"0"));
		property.add(new WebServiceProperty("notificationVibration", cb_vibrate.isChecked()?"1":"0"));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);*/
    }
	private final int _UpdateNotification = 0;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
		 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
			 UpdateNotification();
			 finish();
		     return true;   
		 }
		    return super.onKeyDown(keyCode, event);
    }
}
