package vip.inteltech.gat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionsUtil;
import com.tencent.analytics.sdk.Adx_Tool;
import com.tencent.analytics.sdk.Listener;
import com.ytb.logic.external.NativeResource;
import com.ytb.logic.interfaces.AdNativeLoadListener;
import com.ytb.logic.view.HmNativeAd;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import vip.inteltech.coolbaby.BuildConfig;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.ad.AdBean401;
import vip.inteltech.gat.ad.AdBean402;
import vip.inteltech.gat.ad.SplashAd;
import vip.inteltech.gat.inter.CommCallback;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.CommUtil;
import vip.inteltech.gat.utils.Contents;

public class Loading extends Activity {
    private static final String TAG = "Loading";
    private RelativeLayout loadingView;
    private ViewGroup loadingShow;

    protected static final int MSG_INIT_OK = 0;
    private TextView tv_version;

    private ImageView mIVSplash;
    private TextView mTVCountTime;
    private View mPassView;
    private HmNativeAd mHmNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) == Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) {
            finish();
            return;
        }
//        if (PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            if (BuildConfig.SHOW_ADS) {
//                initAD();
//            } else {
//                afterLoaded();
//            }
//        } else {
//            CommUtil.delayExecute(3000, new CommCallback() {
//                @Override
//                public void execute() {
//                    afterLoaded();
//                }
//            });
//        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loading);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(getResources().getString(R.string.version) + " : " + getVersionName());
        loadingView = findViewById(R.id.loading_view);
        loadingShow = findViewById(R.id.loading_show);

        mTVCountTime = findViewById(R.id.tvCountTime);
        mPassView = findViewById(R.id.clPass);
        mIVSplash = findViewById(R.id.ivSplash);
//        loadingShow = findViewById(R.id.clContainer);
        mPassView.setOnClickListener(v -> {
//            onPass();
            afterLoaded();
        });
        mTVCountTime.setText("5s");

        if (AppData.GetInstance(this).getFirstInit()) {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String operator = manager.getNetworkOperator();
            // if phone without a sim card,getNetworkOperator return an empty string
            try {
                if (!operator.equals("")) {
                    /**通过operator获取 MCC 和MNC */
                    int mcc = Integer.parseInt(operator.substring(0, 3));
                    int mnc = Integer.parseInt(operator.substring(3));

                    if (mcc == 460) //mcc: mobile country code ,460 represent CHINA
                    {
                        //if in CHINA,use the GAODEMAP
                        AppData.GetInstance(this).setMapSelect(1);
                    } else {
                        //if not in CHINA ,use google map
                        AppData.GetInstance(this).setMapSelect(2);
                    }
                    /*		    GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
                     *//**通过GsmCellLocation获取中国移动和联通 LAC 和cellID *//*
				    int mcc = Integer.parseInt(operator.substring(0, 3));
				    int mnc = Integer.parseInt(operator.substring(3));
				    int cid = location.getCid();
				    int lac = location.getLac();		*/
                    AppData.GetInstance(this).setFirstInit(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //添加开屏
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            // Android6.0以下的机器
            startMain();
        }
    }

    /*private void initAD() {
        Adx_Tool.adInit(this, new Listener() {
            @Override
            public void onAdClick(String s) {
                afterLoaded();
            }

            @Override
            public void onAdClosed(String s) {
                afterLoaded();
            }

            @Override
            public void onAdFailed(String s) {
                afterLoaded();
            }

            @Override
            public void onAdInitFailed(String s) {
                afterLoaded();
            }

            @Override
            public void onAdInitSucessed(String s) {
                View view = Adx_Tool.ad_getSplashView();
                if (view != null) {
                    loadingShow.removeAllViews();
                    loadingShow.addView(view);
                } else {
                    afterLoaded();
                }
            }

            @Override
            public void onAdNoAd(String s) {
                afterLoaded();
            }

            @Override
            public void onAdPresent(String s) {
                loadingView.setVisibility(View.GONE);
                loadingShow.setVisibility(View.VISIBLE);
            }
        });
    }*/

    //新加广告方法
    private void startMain() {
        if (BuildConfig.SHOW_ADS) {
            Log.d(TAG, "startMain: SHOW_ADS package=" + getPackageName());
            startAd();
        } else {
            afterLoaded();
        }
    }

    private void startAd() {
        try {
            initAd();
            mHmNativeAd.loadAd(Contents.SPACE_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Ad
     */
    private void initAd() {
        mHmNativeAd = new HmNativeAd(this, new AdNativeLoadListener() {
            @Override
            public boolean onAdLoaded(NativeResource nativeResource) {
                //原生广告加载成功
                //自定义处理 NativeResource
                loadingView.setVisibility(View.GONE);
                loadingShow.setVisibility(View.VISIBLE);
                showAd(nativeResource);
                return true;
            }

            @Override
            public boolean onFailed(int i, String s) {
                //原生广告加载失败
                Log.d(TAG,"onFailed i=" + i + ", s=" +s);
//                onPass();
                afterLoaded();
                return true;
            }

            @Override
            public void onAdClosed() {
                //原生广告落地页被关闭
//                onPass();
                afterLoaded();
            }
        });
    }

    /**
     * 解析json，并展示Ad
     *
     * @param nativeResource 返回包含Ad 的对象，404 以外的其他情况手动解析
     */
    private void showAd(final NativeResource nativeResource) {
        Log.d(TAG,"type = " + nativeResource.getTemplate());
        Log.d(TAG,"json = " + nativeResource.assets);
        try {
            switch (nativeResource.getTemplate()) {
                case "401":
                    AdBean401 adBean401 = new SplashAd.Strategy401().parseJson(nativeResource
                            .assets);
                    handleByParse(nativeResource, adBean401.getImgUrl());
                    break;
                case "402":
                    AdBean402 adBean402 = new SplashAd.Strategy402().parseJson(nativeResource
                            .assets);
                    int index = (int) Math.random() * adBean402.getImgUrls().size();
                    Log.d(TAG,"showAd: 402 img index=" + index);
                    handleByParse(nativeResource, adBean402.getImgUrls().get(index));
                    break;
                case "404":
                    handleType404(nativeResource);
                    break;
                case "403":
                    // 视频广告，跳过
                default:
                    mTimer.start();
//                    mPassView.setVisibility(View.GONE);
//                    onPass();
                    break;
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Type：401， 402
     *
     * @param nativeResource
     * @param imgUrl
     */
    private void handleByParse(final NativeResource nativeResource, String imgUrl) {
        try {
            mTimer.start();
            Log.d(TAG,"showAd: img url=" + imgUrl);
            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(this)
                        .load(imgUrl)
                        .into(mIVSplash);
            }
            //广告展示成功,需调用 SDK 广告曝光接口
            nativeResource.onExposured(loadingShow);
            loadingShow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //广告被 touch，需调用 SDK 广告 touch 接口
                    Log.d(TAG, "onTouch: ");
                    mTimer.cancel();
                    nativeResource.onTouch(loadingShow, event);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleType404(final NativeResource nativeResource) {
        try {
            mTimer.start();
            String title = nativeResource.getTextForLabel("title");//获取标题文本
            String desc = nativeResource.getTextForLabel("description");//获取描述文本
            NativeResource.Img img = nativeResource.getImgForLabel("ad");//获取图片对象

            if (img != null) {
//                KLog.d("showAd: img url=" + img.getUrl());
                Glide.with(this)
                        .load(img.getUrl())
                        .into(mIVSplash);
            }
            //广告展示成功,需调用 SDK 广告曝光接口
            nativeResource.onExposured(loadingShow);
            loadingShow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //广告被 touch，需调用 SDK 广告 touch 接口
                    Log.d(TAG, "onTouch: ");
                    mTimer.cancel();
                    nativeResource.onTouch(loadingShow, event);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void afterLoaded() {
        Intent _intent = new Intent();
        if (AppData.GetInstance(Loading.this).getLoginAuto()) {
            _intent.setClass(Loading.this, Main.class);
        } else {
            _intent.setClass(Loading.this, Login.class);
        }

        Loading.this.startActivity(_intent);
        Loading.this.finish();
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    private String getVersionName() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String versionName = info.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @TargetApi(23)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager
                .PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                .PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            Log.d(TAG, "checkAndRequestPermission: 权限都已经有了");
//            startAd();
            startMain();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 倒计时
     */
    private CountDownTimer mTimer = new CountDownTimer(5000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            mTVCountTime.setText(String.format("%ds", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            mTVCountTime.setText("0s");
//            onPass();
            afterLoaded();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
//            startAd();
            Log.d(TAG, "onRequestPermissionsResult: 获取");
            startMain();
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            mTimer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
    }
}