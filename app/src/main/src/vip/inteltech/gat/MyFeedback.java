package vip.inteltech.gat;

import vip.inteltech.coolbaby.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class MyFeedback extends BaseActivity implements OnClickListener{
	private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_feedback);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.btn_right:
			settingDialog();
			break;
		}
	}
	private Dialog dialog;
	private void settingDialog(){
		View view = getLayoutInflater().inflate(R.layout.dialog_school_defend_setting, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
		WindowManager.LayoutParams wl = window.getAttributes();
		/*wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();*/
		
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		
		TextView tv_schoolinfo = (TextView) view.findViewById(R.id.tv_schoolinfo);
		TextView tv_homeinfo = (TextView) view.findViewById(R.id.tv_homeinfo);
		tv_schoolinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
				Intent intent_a = new Intent(mContext,SchoolInfo.class);
				startActivity(intent_a);
				
			}
		});
		tv_homeinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
				Intent intent_a = new Intent(mContext,HomeInfo.class);
				startActivity(intent_a);
			}
		});
		Button btn_cancel;
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
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
	
}
