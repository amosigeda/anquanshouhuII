package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class AddWatchInput extends BaseActivity implements OnClickListener, WebServiceListener{
	private AddWatchInput mContext;
	private EditText et_bind_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_watch_input);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_OK).setOnClickListener(this);
        et_bind_no = (EditText) findViewById(R.id.et_bind_no);
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.btn_OK:
			String bindNumber = et_bind_no.getText().toString().trim();
			if(TextUtils.isEmpty(bindNumber)){
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("bindNumber", bindNumber);
			setResult(RESULT_OK,intent);
			finish();
			break;
		}
	}
	private final int _SaveDeviceSMS = 0;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _SaveDeviceSMS) {
				int code = jsonObject.getInt("Code");
				if(code == 1){
					
				} else {
					//-1输入参数错误，0登录异常，6无权修改管理员信息
					MToast.makeText(jsonObject.getString("Message")).show();
				} 
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
