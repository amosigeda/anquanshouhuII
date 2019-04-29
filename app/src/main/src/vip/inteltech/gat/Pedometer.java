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

public class Pedometer extends BaseActivity implements OnClickListener{
	private TextView tv_pedometer_count, tv_total_milegle, tv_burn_calories;
	private WatchStateModel mWatchStateModel;
	private final static double milegle_uint = 0.00055;
	private final static double calories_uint = 0.044;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pedometer);

        mWatchStateModel = AppContext.getInstance().getmWatchStateModel();

        findViewById(R.id.btn_left).setOnClickListener(this);

        tv_pedometer_count = (TextView) findViewById(R.id.tv_pedometer_count);
        tv_total_milegle = (TextView) findViewById(R.id.tv_total_milegle);
        tv_burn_calories = (TextView) findViewById(R.id.tv_burn_calories);

        initUI();
	}
	private void initUI(){
		boolean isShow;
		if (AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D9_CHUANGMT_V0.1") != -1) {
			isShow = AppContext.getInstance().getSelectHealth().getPedometer().equals("1");
		} else {
			isShow = AppContext.getInstance().getSelectWatchSet().getStepCalculate().equals("1");
		}
		if (isShow && !TextUtils.isEmpty(mWatchStateModel.getStep())) {
			tv_pedometer_count.setText(mWatchStateModel.getStep());
			tv_total_milegle.setText(String.format("%f", Integer.parseInt(mWatchStateModel.getStep()) * milegle_uint));
			tv_burn_calories.setText(String.format("%f", Integer.parseInt(mWatchStateModel.getStep()) * calories_uint));
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