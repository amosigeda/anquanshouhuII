package vip.inteltech.gat;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.simple.eventbus.Subscriber;

public class BaseActivity extends Activity{
    public static Boolean networkErrorShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContext.getInstance().addActivity(this);
    }
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppContext.getInstance().setShow(true);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppContext.getInstance().removeActivity(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		AppContext.getInstance().setShow(false);
	}

	@Subscriber(tag = Constants.EVENT_NETWORK_ERROR)
	private void onNetworkError(Object obj) {
		synchronized (Constants.DEFAULT_OBJECT) {
			if (!networkErrorShown) {
				networkErrorShown = true;
				Utils.showNotifyDialog(this, 0, R.string.network_failed, R.string.known, 0, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Utils.closeNotifyDialog();
					}
				}, null);
			}
		}
	}
}