package vip.inteltech.gat;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppContext;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.os.Bundle;

public class Help extends BaseActivity implements OnClickListener{
	private WebView wv_help;
    private int image_width = 888;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help);

        findViewById(R.id.btn_left).setOnClickListener(this);

        wv_help = (WebView) findViewById(R.id.wv_help);
        if (AppContext.getInstance().getmWatchModel().getCurrentFirmware().indexOf("D8_CH") != -1) {
        	wv_help.loadUrl("http://121.37.58.122/download/d8_app_help.jpg");
        } else {
        	wv_help.loadUrl("http://121.37.58.122/download/d9_app_help.jpg");
        }

        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        wv_help.setInitialScale(wm.getDefaultDisplay().getWidth() * 100 / image_width);

        WebSettings settings = wv_help.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
	}
}
