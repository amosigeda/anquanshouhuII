package vip.inteltech.gat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.view.Window;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.AppContext;

public class HeartRate extends BaseActivity implements OnClickListener{
	private TextView tv_current_heart_rate;
	private WatchStateModel mWatchStateModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.heart_rate);

        findViewById(R.id.btn_left).setOnClickListener(this);

        mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
        tv_current_heart_rate = (TextView) findViewById(R.id.tv_current_heart_rate);

        initUI();
	}
	private void initUI(){
		if (AppContext.getInstance().getSelectWatchSet().getHrCalculate().equals("1") && !TextUtils.isEmpty(mWatchStateModel.getHealth())) {
			if (mWatchStateModel.getHealth().toCharArray()[1] == ':') {
				String[] heartRate = mWatchStateModel.getHealth().split(":");
				if (!TextUtils.isEmpty(heartRate[1])) {
					tv_current_heart_rate.setText(heartRate[1]);
				}
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		}
	}
}