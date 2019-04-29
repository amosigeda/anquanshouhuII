package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class FeedbackIdea extends BaseActivity implements OnClickListener,
		WebServiceListener {
	private FeedbackIdea mContext;
	private EditText et_content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback_idea);
		mContext = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_send).setOnClickListener(this);
		
		et_content = (EditText) findViewById(R.id.et_content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		case R.id.btn_right:
			startActivity(new Intent(mContext,FeedbackIdeaMe.class));
			break;
		case R.id.btn_send:
			Feedback();
			break;
		}
	}

	private void Feedback() {
		String content = et_content.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			return;
		} 
		WebService ws = new WebService(mContext, _Feedback, true, "Feedback");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("content", content));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _Feedback = 0;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		JSONObject jsonObject;
		try {
			if(id == _Feedback){
				jsonObject = new JSONObject(result);
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					MToast.makeText(R.string.send_success).show();
					startActivity(new Intent(mContext,FeedbackIdeaMe.class));
					finish();
				} else {
					MToast.makeText(R.string.send_fail).show();
				} 
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
