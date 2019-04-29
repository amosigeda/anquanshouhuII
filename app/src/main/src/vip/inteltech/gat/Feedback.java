package vip.inteltech.gat;


import vip.inteltech.coolbaby.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;


public class Feedback extends BaseActivity implements OnClickListener{
	private Feedback mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_idea).setOnClickListener(this);
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.btn_idea:
			startActivity(new Intent(mContext, FeedbackIdea.class));
			break;
		}
		
	}
}
