package vip.inteltech.gat;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import vip.inteltech.gat.utils.AppContext;

public class BaseFragmentActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.getInstance().addActivity(this);
    }
    @Override
	protected void onResume() {
		super.onResume();
		AppContext.getInstance().setShow(true);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppContext.getInstance().removeActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppContext.getInstance().setShow(false);
	}
}