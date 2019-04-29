package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.viewutils.MToast;


public class ChangePwd extends BaseActivity implements OnClickListener,
		WebServiceListener {
	private ChangePwd mContext;
	private EditText et_pwd,et_pwd_new,et_pwds_new;
	private ImageView iv1,iv2,iv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_pwd);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_new = (EditText) findViewById(R.id.et_pwd_new);
        et_pwds_new = (EditText) findViewById(R.id.et_pwds_new);
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
				if(TextUtils.isEmpty(et_pwd.getText().toString().trim())){
					iv1.setVisibility(View.INVISIBLE);
				}
			}
		});
        et_pwd_new.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				iv2.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(et_pwd_new.getText().toString().trim())){
					iv2.setVisibility(View.INVISIBLE);
				}
			}
		});
        et_pwds_new.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				iv3.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(et_pwds_new.getText().toString().trim())){
					iv3.setVisibility(View.INVISIBLE);
				}
			}
		});
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_next:
			changePwd();
			break;
		case R.id.iv1:
			et_pwd.getText().clear();
			break;
		case R.id.iv2:
			et_pwd_new.getText().clear();
			break;
		case R.id.iv3:
			et_pwds_new.getText().clear();
			break;
		}
	}
	private void changePwd(){
		String pwd = et_pwd.getText().toString().trim();
		String pwd_new = et_pwd_new.getText().toString().trim();
		String pwds_new = et_pwds_new.getText().toString().trim();
		if (TextUtils.isEmpty(pwd)) {
			return;
		} else if (TextUtils.isEmpty(pwd_new)) {
			return;
		} else if (TextUtils.isEmpty(pwds_new)) {
			return;
		} else if (!pwd_new.equals(pwds_new)) {
			MToast.makeText(R.string.pwd_different).show();
			return;
		}
		WebService ws = new WebService(mContext, _ChangePassword, true, "ChangePassword");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("passWord", pwd));
		property.add(new WebServiceProperty("newPassword", pwd_new));
		// property.add(new WebServiceProperty("appleId", ""));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _ChangePassword = 0;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _ChangePassword) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					AppData.GetInstance(mContext).setPwd(et_pwd_new.getText().toString().trim());
					MToast.makeText(R.string.change_pwd_success).show();
					finish();
                } else if (code >= -50) {
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else {
                    // -1表示输入参数错误
					MToast.makeText(R.string.edit_fail).show();
				} /*else if (code == -1) {
					// -1表示输入参数错误
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == 3) {
					// 3原密码错误
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code < 0){
					// 系统异常小于0
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code <= 0){
					// 常规异常大于0
					MToast.makeText(jsonObject.getString("Message")).show();
				}*/

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
