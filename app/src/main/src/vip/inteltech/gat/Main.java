package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.Projection;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.analytics.sdk.Adx_Tool;
import com.tencent.analytics.sdk.Listener;
import com.zbar.lib.MCaptureActivity;

import vip.inteltech.coolbaby.BuildConfig;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.ResideMenu.ResideMenu;
import vip.inteltech.gat.chatutil.ChatMsgEntity;
import vip.inteltech.gat.db.*;
import vip.inteltech.gat.inter.CommCallback;
import vip.inteltech.gat.loadview.AVLoadingIndicatorView;
import vip.inteltech.gat.maputil.GaoDeMapUtil;
import vip.inteltech.gat.maputil.GoogleMapUtil;
import vip.inteltech.gat.maputil.MapUtil;
import vip.inteltech.gat.model.*;
import vip.inteltech.gat.service.MService;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.CallingView;
import vip.inteltech.gat.viewutils.MProgressDialog;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.MToast2;

public class Main extends BaseFragmentActivity implements OnClickListener,
        LocationSource, AMapLocationListener, WebServiceListener, OnGeocodeSearchListener, OnMapReadyCallback, OnCheckedChangeListener {
    public static final String TAG = Main.class.getName();

    private ResideMenu resideMenu;
    public static Main mContext = null;

    // 地图相关
    private AMap mGaoDeMap;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mGaoDeMapFragment;
    private com.google.android.gms.maps.SupportMapFragment mGoogleMapFragment;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MarkerOptions markerOption;
    private Marker mMarker;
    private GeocodeSearch geocoderSearch;
    private int mMapSelect;

    private Button btn_phone;
    private LinearLayout layout_chat, school_defend;
    private ImageButton btn_find_watch;
    private TextView tv_unread, tv_state, tv_Title, tv_time;
    private ImageView iv_head, iv_Electricity, iv_unRead_msg_record, iv_Location_stype;
    private WatchStateModel mWatchStateModel;
    private CheckBox cb_layers;
    private AVLoadingIndicatorView avl;
    private int oldDeviceId;
    private HealthModel mHealthModel;

    private MapUtil mMapUtil;
    private GaoDeMapUtil mGaoDeMapUtil;
    private GoogleMapUtil mGoogleMapUtil;

    public static final int callDevice = 933;
    public static final int callDeviceCancel = 934;
    private String callID = null;
    private String messageID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mContext = this;
        oldDeviceId = AppData.GetInstance(this).getSelectDeviceId();
        mMapSelect = AppData.GetInstance(this).getMapSelect();

        findViewById(R.id.btn_amplification).setOnClickListener(this);
        findViewById(R.id.btn_shrink).setOnClickListener(this);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
        findViewById(R.id.btn_watch_location).setOnClickListener(this);
        findViewById(R.id.btn_my_location).setOnClickListener(this);
        btn_phone = (Button) findViewById(R.id.btn_phone);
        layout_chat = (LinearLayout) findViewById(R.id.layout_chat);
        school_defend = (LinearLayout) findViewById(R.id.school_defend);
        btn_find_watch = (ImageButton) findViewById(R.id.btn_find_watch);
        tv_unread = (TextView) findViewById(R.id.tv_unread);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_unRead_msg_record = (ImageView) findViewById(R.id.iv_unRead_msg_record);
        iv_Electricity = (ImageView) findViewById(R.id.iv_Electricity);
        iv_Location_stype = (ImageView) findViewById(R.id.iv_Location_stype);
        avl = (AVLoadingIndicatorView) findViewById(R.id.avl);

        cb_layers = (CheckBox) findViewById(R.id.cb_layers);

        btn_phone.setOnClickListener(this);
        layout_chat.setOnClickListener(this);
        school_defend.setOnClickListener(this);
        btn_find_watch.setOnClickListener(this);
        cb_layers.setOnCheckedChangeListener(this);

        tv_state.setText(null);

        Intent intentFromSevice = getIntent();
        int type = 0;
        if (!TextUtils.isEmpty(intentFromSevice.getStringExtra("type"))) {
            type = Integer.valueOf(intentFromSevice.getStringExtra("type"));
        }
        int deviceId = 0;
        if (!TextUtils.isEmpty(intentFromSevice.getStringExtra("deviceId"))) {
            deviceId = Integer.valueOf(intentFromSevice.getStringExtra("deviceId"));
        }
        if (deviceId != 0) {
            AppData.GetInstance(mContext).setSelectDeviceId(deviceId);
        }
        WatchDao mWatchDao = new WatchDao(this);
        AppContext.getInstance().setWatchMap(mWatchDao.getWatchMap());
        AppContext.getInstance().setmWatchModel(mWatchDao.getWatch(AppData.GetInstance(mContext).getSelectDeviceId()));
        /*AppContext.getInstance().setmWatchModel(AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));*/
        ContactDao mContactDao = new ContactDao(this);
        AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(this).getSelectDeviceId()));
        /*
         * AppContext.getInstance().setContactList(mContactDao.getContactList(100
         * )); if( AppContext.getInstance().getContactList() == null){
         * System.out.println("null"); }else{ System.out.println("nonull"); }
         */
        WatchSetDao mWatchSetDao = new WatchSetDao(this);
        AppContext.getInstance().setSelectWatchSet(mWatchSetDao.getWatchSet(AppData.GetInstance(this).getSelectDeviceId()));

        WatchStateDao mWatchStateDao = new WatchStateDao(this);
        AppContext.getInstance().setmWatchStateModel(mWatchStateDao.getWatchState(AppData.GetInstance(this).getSelectDeviceId()));
        ChatMsgDao mChatMsgDao = new ChatMsgDao(mContext);
        List<ChatMsgEntity> allDataArrays = mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId());
        if (allDataArrays.size() > Contents.CHATMSGINITIAL) {
            AppContext.getInstance().setChatMsgList(allDataArrays.subList(allDataArrays.size() - Contents.CHATMSGINITIAL, allDataArrays.size()));
        } else {
            AppContext.getInstance().setChatMsgList(mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId()));
        }

        mGaoDeMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.GaoDeMap);
        //	mGaoDeMap = mGaoDeMapFragment.getMap();
        //	aMapInit();
        //GoogleMap member aquire in the function---OnMapReady
        mGoogleMapFragment = ((com.google.android.gms.maps.SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.GoogleMap));
        mGoogleMapFragment.getMapAsync(this);

        if (mMapSelect == 1) {
            mGaoDeMap = mGaoDeMapFragment.getMap();
            mGaoDeMapUtil = new GaoDeMapUtil(mGaoDeMap);
            mMapUtil = new MapUtil(mGaoDeMapUtil);
            mMapUtil.initMap();
            mGaoDeMap.setLocationSource(this);// 设置定位监听
        }

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        //hide one map of the two
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mMapSelect == 1) {
            transaction.hide(mGoogleMapFragment);
        } else {
            transaction.hide(mGaoDeMapFragment);
        }
        transaction.commit();

        setUpMenu();

        initReceiver();
        Intent intent = new Intent(this, MService.class);
        startService(intent);
        if (type != 0) {
            if (type == 1) {
                startActivity(new Intent(mContext, Chat.class));
            } else if (type == 2 || type == 5 || type == 6 || type == 7 || type == 9 || type == 10 || type > 100) {
                startActivity(new Intent(mContext, MsgRecord.class));
            } else if (type == 8) {
                startActivity(new Intent(mContext, WatchFare.class));
            } else if (type == 11) {
                startActivity(new Intent(mContext, Album.class));
            }
        }
        CheckAppVersion();
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        String firmware = AppContext.getInstance().getmWatchModel().getCurrentFirmware();
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2") ||
                (firmware != null && firmware.contains("D8_CH"))) {
            findViewById(R.id.school_defend).setVisibility(View.GONE);
            findViewById(R.id.layout_chat).setVisibility(View.GONE);
            findViewById(R.id.tv_unread).setVisibility(View.GONE);
        }

        mLBSUtil = new LBSUtil(mContext);

        mWifiUtil = new WifiUtil(mContext);

        WebServiceUtils.GetDeviceDetail(mContext, _GetDeviceDetail, String.valueOf(AppData.GetInstance(this).getSelectDeviceId()), mContext, false);

        initView();
//        if (BuildConfig.SHOW_ADS) {
//            initAD();
//        }
    }

    private void initAD() {
        Adx_Tool.adBannerAdd(this, 1, new Listener() {
            @Override
            public void onAdClick(String s) {

            }

            @Override
            public void onAdClosed(String s) {

            }

            @Override
            public void onAdFailed(String s) {

            }

            @Override
            public void onAdInitFailed(String s) {

            }

            @Override
            public void onAdInitSucessed(String s) {

            }

            @Override
            public void onAdNoAd(String s) {

            }

            @Override
            public void onAdPresent(String s) {
                CommUtil.delayExecute(3000, new CommCallback() {
                    @Override
                    public void execute() {
                        Adx_Tool.adBannerRemove(Main.this);
                    }
                });
            }
        });
    }

    private void initView() {
        if (mMarker != null) {
            //		mMarker.remove();
        }
        mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
        mHealthModel = AppContext.getInstance().getSelectHealth();
        if (mWatchStateModel.getLatitude() == 0 && mWatchStateModel.getLongitude() == 0) {
            RefreshDeviceState();
            if (mHealthModel.getLatitude() != 0 && mHealthModel.getLongitude() != 0) {
                mWatchStateModel.setLatitude(mHealthModel.getLatitude());
                mWatchStateModel.setLongitude(mHealthModel.getLongitude());
                mAddress = mHealthModel.getAddress();
                mWatchStateModel.setDeviceTime(mHealthModel.getDeviceTime());
                mWatchStateModel.setLocationType(mHealthModel.getLocationType());
                tv_state.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null) + " " + mAddress);
                tv_time.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null));
            } else {
                mWatchStateModel.setLatitude(39.908692);
                mWatchStateModel.setLongitude(116.397477);
                tv_state.setText(R.string.get_location);
            }
        } else {
            if (mWatchStateModel.getLatitude() == mHealthModel.getLatitude() && mWatchStateModel.getLongitude() == mHealthModel.getLongitude()) {
                mAddress = mHealthModel.getAddress();
                mWatchStateModel.setDeviceTime(mHealthModel.getDeviceTime());
                mWatchStateModel.setLocationType(mHealthModel.getLocationType());
                tv_state.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null) + " " + mAddress);
                tv_time.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null));
            } else {
                tv_state.setText(R.string.get_location);
            }
        }

        //	if(mLatLng != null);
        //		addMarkerMe(mLatLng, R.drawable.mylocation_icon, false, false);
        if (!TextUtils.isEmpty(mWatchStateModel.getElectricity())) {
            initElectricity(Integer.valueOf(mWatchStateModel.getElectricity()));
        } else {
            initElectricity(0);
        }
        tv_Title.setText(AppContext.getInstance().getmWatchModel().getName());
        refreshLocationType();

        //first locate operation
        //googleMap do this in function(onMaReady)
        if (mMapSelect == 1) {
            mMapUtil.clear();
            mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), 18, 0, 0, 1000, null);
            //	addMarker(new LatLng(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude()),R.drawable.location_watch,true,true);
            mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, true, true);
            GetAddress(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude());
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        final AppData appData = AppData.GetInstance(this);
        if (CommUtil.isBlank(appData.getPhoneNumber()) && !AppContext.dialogShown) {
            Utils.showNotifyDialog(this, 0, R.string.user_phone_number_blank, R.string.go, R.string.later, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddContactsA.class);
                    intent.putExtra("bindNumber", appData.getBindNumber());
                    startActivity(intent);
                    Utils.closeNotifyDialog();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.closeNotifyDialog();
                }
            }, 0, null);
            AppContext.dialogShown = true;
        }
    }

    private void refreshLocationType() {
        Log.v("zzz", "refreshLocationType");
        if (!TextUtils.isEmpty(mWatchStateModel.getLocationType())) {
            WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
            if (mWatchStateModel.getLocationType().equals("1") ||
                    (mWatchModel != null && mWatchModel.getCurrentFirmware() != null && (
                            mWatchModel.getCurrentFirmware().contains("D9_CHUANGMT_V") ||
                                    mWatchModel.getCurrentFirmware().contains("D10_CHUANGMT_V") ||
                                    mWatchModel.getCurrentFirmware().contains("D9_TP_CHUANGMT_V")))) {
                iv_Location_stype.setImageResource(R.drawable.gps_icon);
            } else if (mWatchStateModel.getLocationType().equals("2")) {
                iv_Location_stype.setImageResource(R.drawable.lbs_icon);
            } else if (mWatchStateModel.getLocationType().equals("3")) {
                iv_Location_stype.setImageResource(R.drawable.wifi_icon);
            }
        }
    }

    private void initElectricity(int level) {
        Log.v("zzz", "initElectricity");
        if (level > 75 && level <= 100) {
            iv_Electricity.setImageResource(R.drawable.battery4);
        } else if (level <= 75 && level > 50) {
            iv_Electricity.setImageResource(R.drawable.battery3);
        } else if (level <= 50 && level > 25) {
            iv_Electricity.setImageResource(R.drawable.battery2);
        } else if (level <= 25) {
            iv_Electricity.setImageResource(R.drawable.battery1);
        } else if (level == 255) {
            iv_Electricity.setImageResource(R.drawable.battery5);
        }
    }

    private LatLng getRandomLatlng(LatLng oldLatLng, double distance) {
        double r = 6371;//地球半径千米
        double dis = distance;//0.5千米距离
        double dlng = 2 * Math.asin(Math.sin(dis / (2 * r)) / Math.cos(oldLatLng.latitude * Math.PI / 180));
        dlng = dlng * 180 / Math.PI;//角度转为弧度
        double dlat = dis / r;
        dlat = dlat * 180 / Math.PI;

        double minLat = oldLatLng.latitude - dlat;
        double maxLat = oldLatLng.latitude + dlat;
        double minLng = oldLatLng.longitude - dlng;
        double maxLng = oldLatLng.longitude + dlng;

//        Log.e(TAG,"dlng : "+dlng+"  dlat : "+dlat);
//        Log.e(TAG,"minLat : "+minLat+"  maxLat : "+maxLat);
//        Log.e(TAG,"minLng : "+minLng+"  maxLng : "+maxLng);

        double randomLat = Math.random() * (2 * dlat) + minLat;
        double randomLng = Math.random() * (2 * dlng) + minLng;


        return new LatLng(randomLat, randomLng);
    }


    private LatLng lbs2LatLng, lbs5LatLng;
    private LatLng wifi1LatLng, wifi5LatLng;

    private BroadcastReceiver ChangeStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("zzz", "=========ChangeStateReceiver==============");
            /*if(mMarker != null){
                mMarker.remove();
			}*/
            mMapUtil.clear();
            mWatchStateModel = AppContext.getInstance().getmWatchStateModel();

            if (mLatLng != null) {
                mMapUtil.addMarkerMe(mLatLng.latitude, mLatLng.longitude, R.drawable.mylocation_icon, false, false);
            } else if (mWatchStateModel != null) {
                mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude
                        (), R.drawable.location_watch, true, true);
                GetAddress(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude());
                return;
            } else {
                return;
            }

            if (mWatchStateModel == null) {
                return;
            }

            float distance = 0;
            LatLng mMobileLatLng = null;

            if (mLatLng != null) {
                mMobileLatLng = new LatLng(mWatchStateModel.getLatitude(),
                        mWatchStateModel.getLongitude());
                distance = AMapUtils.calculateLineDistance(mLatLng, mMobileLatLng);
//                Log.e(TAG, "distance : " + distance);
            }
            //判断是否为LBS定位
            if ("2".equals(mWatchStateModel.getLocationType())) {
//                mLastIsLbs = true;
                //手机和手表距离小于1.2公里，则计算两次基站定位之间的距离
                if (1500 > distance) {
                    //计算连续两次手表基站定位之间的距离
//                    float ddistance = AMapUtils.calculateLineDistance(mLastLbsLatLng, mMobileLatLng);
//                    Log.e(TAG, "ddistance : " + ddistance);
                    if (distance > 0 && distance < 550) {
                        //使用手机定位500米距离的位置,得到随机经纬度
                        lbs5LatLng = null;
                        if (null == lbs2LatLng) {
                            lbs2LatLng = getRandomLatlng(mLatLng, 0.03);
                        }
//                        LatLng randomLatLng = getRandomLatlng(mLatLng, 0.1);
                        mWatchStateModel.setLatitude(lbs2LatLng.latitude);
                        mWatchStateModel.setLongitude(lbs2LatLng.longitude);
                    } else {
                        lbs2LatLng = null;
                        if (null == lbs5LatLng) {
                            lbs5LatLng = getRandomLatlng(mLatLng, 0.3);
                        }
                        mWatchStateModel.setLatitude(lbs5LatLng.latitude);
                        mWatchStateModel.setLongitude(lbs5LatLng.longitude);
                    }
                }
//                mLastLbsLatLng = mMobileLatLng;
            }


            if ("3".equals(mWatchStateModel.getLocationType()) && 500 < distance) {

                //10到100米距离，随机到手机附近10米距离内
                if (100 > distance && 10 < distance) {
                    wifi5LatLng = null;
                    if (null == wifi1LatLng) {
                        wifi1LatLng = getRandomLatlng(mLatLng, 0.005);
                    }
                    mWatchStateModel.setLatitude(wifi1LatLng.latitude);
                    mWatchStateModel.setLongitude(wifi1LatLng.longitude);

                } else if (100 < distance && 550 > distance) {
                    //100到500距离内，手机附近随机100距离
                    wifi1LatLng = null;
                    if (null == wifi5LatLng) {
                        wifi5LatLng = getRandomLatlng(mLatLng, 0.03);
                    }
                    mWatchStateModel.setLatitude(wifi5LatLng.latitude);
                    mWatchStateModel.setLongitude(wifi5LatLng.longitude);
                }
//                if (null != wifiLatLng) {
//                    mWatchStateModel.setLatitude(wifiLatLng.latitude);
//                    mWatchStateModel.setLongitude(wifiLatLng.longitude);
//                }
            }



            if (mWatchStateModel.getLatitude() != 0 && mWatchStateModel.getLongitude() != 0) {
                //	addMarker(new LatLng(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude()),R.drawable.location_watch,true, true);
                mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, true, true);
                //GetAddress(40.8694240429,-74.0720558167);
                GetAddress(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude());
            }
//            if (mLatLng != null) {
//                mMapUtil.addMarkerMe(mLatLng.latitude, mLatLng.longitude, R.drawable.mylocation_icon, false, false);
//            }
            initElectricity(Integer.valueOf(mWatchStateModel.getElectricity()));
            refreshLocationType();
            mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), 18, 0, 0, 1000, null);
            if (mMainFrameTask != null)
                mMainFrameTask.cancel(true);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btn_phone) {
            callDialog();
        } else if (v == layout_chat) {
            unreadChatMsg = 0;
            tv_unread.setVisibility(View.INVISIBLE);
            startActivity(new Intent(mContext, Chat.class));
        } else if (v == school_defend) {
            startActivity(new Intent(mContext, SchoolDefend.class));
        } else if (v == btn_find_watch) {
            makeSureDialog(1);
        } else if (v.getId() == R.id.btn_amplification) {
            if (mMapUtil != null) {
                mMapUtil.animateCam(true, 1000, null);
            }
        } else if (v.getId() == R.id.btn_shrink) {
            if (mMapUtil != null) {
                mMapUtil.animateCam(false, 1000, null);
            }
        } else if (v.getId() == R.id.btn_refresh) {
            //	getAddress(new LatLonPoint(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude()));
            RefreshDeviceState();
        } else if (v.getId() == R.id.btn_watch_location) {
            if (mWatchStateModel != null && mMapUtil != null) {
                mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude
                        (), R.drawable.location_watch, true, true);
                GetAddress(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude());
                mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), 18, 0, 0, 1000, null);
            }
        } else if (v.getId() == R.id.btn_my_location) {
            if (mLatLng != null && mMapUtil != null) {
                mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 18, 0, 0, 1000, null);
                //	mMapUtil.animateCam(mLatLng.latitude,mLatLng.longitude,18,0,30,1000,null);
            } else {
                CommUtil.showMsgShort(R.string.no_locationinfo_phone);
            }
        }
        //resideMenu.closeMenu();
    }

    private void RefreshDeviceState() {
        Log.v("zzz", "RefreshDeviceState");
        //WebServiceUtils.GetDeviceState(mContext, _GetDeviceState, String.valueOf(AppData.GetInstance(this).getSelectDeviceId()), mContext);
        WebService ws = new WebService(mContext, _RefreshDeviceState, false, "RefreshDeviceState");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void callDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_call, null);
        TextView tv_number = (TextView) view.findViewById(R.id.btn_watch_no);
        WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
        if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_number.setText(R.string.locator_no);
        }
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        Button btn_watch_no, btn_cornet_family, btn_cancel;
        btn_watch_no = (Button) view.findViewById(R.id.btn_watch_no);
        btn_cornet_family = (Button) view.findViewById(R.id.btn_cornet_family);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_watch_no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AppContext.getInstance().getmWatchModel().getPhone())) {
                    WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                    if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                        MToast.makeText(R.string.edit_watch_no_first_1).show();
                    } else {
                        MToast.makeText(R.string.edit_watch_no_first).show();
                    }
                    return;
                }
                requestCall(AppContext.getInstance().getmWatchModel().getPhone());
                dialog.cancel();
            }
        });
        btn_cornet_family.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AppContext.getInstance().getmWatchModel().getCornet())) {
                    WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                    if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                        MToast.makeText(R.string.edit_watch_cornet_first_1).show();
                    } else {
                        MToast.makeText(R.string.edit_watch_cornet_first).show();
                    }
                    return;
                }
                requestCall(AppContext.getInstance().getmWatchModel().getCornet());
                dialog.cancel();
            }
        });
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

    private void requestCall(final String phone) {
        WatchModel watchModel = AppContext.getInstance().getmWatchModel();
        if (watchModel.getCloudPlatform() > 0) {
            callCloudPlatform(watchModel);
            return;
        }
        PermissionsUtil.requestPermission(this, Manifest.permission.CALL_PHONE, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                CommUtil.showMsgShort("You reject call permission!");
            }
        });
    }

    private void callCloudPlatform(WatchModel watchModel) {
        String title = watchModel.getName();
        if (CommUtil.isBlank(title)) {
            title = watchModel.getPhone();
        }
        CallingView.show(this, title, new CommCallback() {
            @Override
            public void execute() {
                if (CommUtil.isBlank(messageID) || CommUtil.isBlank(callID)) {
                    return;
                }
                WebService ws = new WebService(Main.this, callDeviceCancel, getResources().getString(R.string.callingCancel), "CallDeviceCancel");
                List<WebServiceProperty> property = new LinkedList<>();
                property.add(new WebServiceProperty("loginId", AppData.GetInstance(Main.this).getLoginId()));
                property.add(new WebServiceProperty("deviceId", CommUtil.toStr(watchModel.getId())));
                property.add(new WebServiceProperty("messageID", messageID));
                property.add(new WebServiceProperty("callID", callID));
                ws.addWebServiceListener(webServiceListener);
                ws.SyncGet(property);
            }
        });
        WebService ws = new WebService(Main.this, callDevice, null, "CallDevice");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", CommUtil.toStr(watchModel.getId())));
        ws.addWebServiceListener(webServiceListener);
        ws.SyncGet(property);
    }

    private WebService.WebServiceListener webServiceListener = new WebService.WebServiceListener() {

        @Override
        public void onWebServiceReceive(String method, int id, String result) {
            switch (id) {
                case callDevice:
                    processCallDevice(method, id, result);
                    break;
                case callDeviceCancel:
                    break;
            }
        }
    };

    private void processCallDevice(String method, int id, String result) {
        JSONObject json = JSONObject.parseObject(result);
        if (json == null) {
            return;
        }
        int code = json.getIntValue("Code");
        if (code != 1) {
            String msg = json.getString("Message");
            MToast2.makeText(this, msg).show();
        } else {
            MToast2.makeText(this, getString(R.string.dial_succ)).show();
            JSONObject body = json.getJSONObject("Body");
            if (body != null) {
                messageID = body.getString("messageID");
                callID = body.getString("callID");
            }
        }
    }

    private void setUpMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.bg_menu);
        resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        // valid scale factor is between 0.0f and 1.0f. leftmenu'width is
        // 150dip.
//        resideMenu.setScaleValue(0.6f);
        resideMenu.setTranslationX(Utils.dp2px(this, 220));

        // create menu items;

        resideMenu.setBtn_addClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().getWatchMap().size() >= 5) {
                    WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                    if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                        MToast.makeText(R.string.max_bind_1).show();
                    } else {
                        MToast.makeText(R.string.max_bind).show();
                    }
                    return;
                }
                requestToCapture();

            }
        });
        resideMenu.setIv_headClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, WatchList.class), CHANGEWATCH);
            }
        });

        findViewById(R.id.rl_watch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.msg_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, MsgRecord.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void requestToCapture() {
        PermissionsUtil.requestPermission(this, Manifest.permission.CAMERA, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                startActivityForResult(new Intent(mContext, MCaptureActivity.class), ADDWATCH);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                CommUtil.showMsgShort(R.string.permission_camera_denied);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private boolean menuClose = true;
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            menuClose = false;
        }

        @Override
        public void closeMenu() {
            menuClose = true;
        }
    };
    // ------------------------------------------------------地图相关---------------------------------------------------------------//
    /**
     * 初始化AMap对象
     */
/*	private boolean zoomIndex = true;

	private void aMapInit() {
		if (mGaoDeMap == null) {
			setUpMap();
		}
	}

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		mGaoDeMap.animateCamera(update, 1000, callback);
	}*/
/*	private void addMarker(LatLng latLng, int MarkerIndex, boolean isAddress, boolean isJump){
        if(isAddress){
			//getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
			GetAddress(latLng.latitude, latLng.longitude);
		}
		markerOption = new MarkerOptions();
		//markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(MarkerIndex));
		mMarker = mGaoDeMap.addMarker(markerOption);
		mMarker.setPosition(latLng);
		if(isJump)
			jumpPoint(mMarker, latLng);
	}*/
/*	private void addMarkerMe(LatLng latLng, int MarkerIndex, boolean isAddress, boolean isJump){
        markerOption = new MarkerOptions();
		//markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(MarkerIndex));
		mMarker = mGaoDeMap.addMarker(markerOption);
		mMarker.setPosition(latLng);
		if(isJump)
			jumpPoint(mMarker, latLng);
	}*/

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker, final LatLng latLng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGaoDeMap.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * latLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * latLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                mGaoDeMap.reloadMap();// 刷新地图
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {

        Log.v("zzz", "getAddress");
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        //	RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(-74.0059731,40.7143528), 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 定位成功后回调函数
     */
    private boolean firstLoad = true;
    private LatLng mLatLng = null;
    private WifiUtil mWifiUtil;
    private LBSUtil mLBSUtil;
    private String wifis = "", bts = "";

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        Log.e("zzz", "onLocationChanged : "+aLocation.getLatitude()+"   aLocation Longitude : "+aLocation.getLongitude());
        //MToast.makeText(mWatchStateModel.getLatitude() + "  " + mWatchStateModel.getLongitude()).show();
        //	MToast.makeText(aLocation.getLatitude()+"  "+aLocation.getLongitude()).show();
        if (aLocation != null) {
            /*if(mListener != null)
                mListener.onLocationChanged(aLocation);// 显示系统小蓝点*/
//            if (firstLoad) {
//                if (aLocation.getLatitude() != 0 && aLocation.getLongitude() != 0) {
//                    mLatLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
//                    if (mWatchStateModel.getLatitude() == 0 && mWatchStateModel.getLongitude() == 0) {
//                        if (mLatLng.latitude != 0 && mLatLng.longitude != 0) {
//                            mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 18, 0, 0, 1000, null);
//                        }
//                    }
//                    firstLoad = false;
//                }
//            }
//            if (mLatLng != null) ;
            if (aLocation.getLatitude() != 0 && aLocation.getLongitude() != 0) {
                mLatLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
            }
            mMapUtil.addMarkerMe(aLocation.getLatitude(), aLocation.getLongitude(), R.drawable.mylocation_icon, false, false);
            mWifiUtil.startScan();
            List<ScanResult> list = mWifiUtil.getWifiList();
            wifis = "";
            if (list != null) {
                ScanResult mScanResult;
                for (int i = 0; i < list.size(); i++) {
                    //得到扫描结果
                    mScanResult = list.get(i);
                    //if(!mScanResult.SSID.contains("CMCC") && !mScanResult.SSID.contains("ChinaNet") && !mScanResult.SSID.contains("ChinaUnicom")){
                    wifis = wifis + mScanResult.BSSID + "," + mScanResult.level + "," + mScanResult.SSID.replace(',', '_');
                    if (i < list.size() - 1)
                        wifis = wifis + "|";
                    //}
                    //System.out.println(wifis);
                }
            }

			/*if(!TextUtils.isEmpty(bts)||!TextUtils.isEmpty(wifis))
                if(aLocation.getLatitude() != 0 && aLocation.getLongitude() != 0){
					WIFILBS(bts, wifis, aLocation.getLatitude(), aLocation.getLongitude(),(int) aLocation.getAccuracy());
				}*/
        }
    }

    private final int _WIFILBS = 99;

    private void WIFILBS(String bts, String wifis, double Lat, double Lng, int radius) {
        Log.v("zzz", "WIFILBS");
        WebService ws = new WebService(mContext, _WIFILBS, false, "WIFILBS");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("mapType", "1"));
        property.add(new WebServiceProperty("bts", bts));
        property.add(new WebServiceProperty("wifis", wifis));
        property.add(new WebServiceProperty("lat", String.valueOf(Lat)));
        property.add(new WebServiceProperty("lng", String.valueOf(Lng)));
        property.add(new WebServiceProperty("radius", String.valueOf(radius)));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
//		Log.v("zjy", "activate");
        mListener = listener;
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
//		Log.v("zjy", "deactivate");
        mListener = null;

    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
        Log.v("zzz", "");

    }

    private String mAddress;

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        Log.v("zzz", "onRegeocodeSearched");
//        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress())) {
                if (TextUtils.isEmpty(mWatchStateModel.getDeviceTime())) {
                    tv_state.setText(result.getRegeocodeAddress().getFormatAddress() + "附近");
                } else {
                    tv_state.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), "") + result.getRegeocodeAddress().getFormatAddress() + "附近");
                    tv_time.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null));
                }
                mAddress = result.getRegeocodeAddress().getFormatAddress() + "附近";
            } else {
                tv_state.setText(R.string.no_result);
                tv_time.setText("");
                //System.out.println(R.string.no_result);
                //暂无结果
            }
//        } else if (rCode == 27) {
//            tv_state.setText(R.string.no_result);
//            tv_time.setText("");
//            //网络错误
//        } else if (rCode == 32) {
//            tv_state.setText(R.string.no_result);
//            tv_time.setText("");
//            //key错误
//        } else {
//            tv_state.setText(R.string.no_result);
//            tv_time.setText("");
//            //其他错误
//        }
    }
    // ------------------------------------------------------生命周期---------------------------------------------------------------//

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL + AppContext.getInstance().getmWatchModel().getAvatar(), resideMenu.iv_head, new AnimateFirstDisplayListener());
            ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL + AppContext.getInstance().getmWatchModel().getAvatar(), iv_head, new AnimateFirstDisplayListener());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (new Contents().canCamera && AppContext.getInstance().isSupportCamera()) {
            resideMenu.ll_album.setVisibility(View.VISIBLE);
        } else {
            resideMenu.ll_album.setVisibility(View.GONE);
        }
        if (new Contents().canHistoryTrack) {
            resideMenu.ll_history_track.setVisibility(View.VISIBLE);
        } else {
            resideMenu.ll_history_track.setVisibility(View.GONE);
        }
        if (AppContext.getInstance().isSupportBT3()) {
            resideMenu.ll_friend_list.setVisibility(View.VISIBLE);
        } else {
            resideMenu.ll_friend_list.setVisibility(View.GONE);
        }
        if (oldDeviceId != AppData.GetInstance(mContext).getSelectDeviceId()) {
            initView();
            oldDeviceId = AppData.GetInstance(mContext).getSelectDeviceId();
            if (mMainFrameTask != null)
                mMainFrameTask.cancel(true);
        }
        if (mWatchStateModel.getDeviceId() == 0) {
            WebServiceUtils.RefreshDeviceState(mContext, _RefreshDeviceState, String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
        }
        /*
        mLBSUtil.setSignalStrengthsListener(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                if (mLBSUtil.getResult() != null)
                    bts = mLBSUtil.getResult() + signalStrength.getGsmSignalStrength();
                else
                    bts = "";
                //System.out.println(bts);
            }
        });*/
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(15*1000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        }
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        PermissionsUtil.requestPermission(this, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                if (mlocationClient != null) {
                    mlocationClient.startLocation();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                CommUtil.showMsgShort(R.string.permission_location_denied);
            }
        });
        if (!tv_Title.getText().toString().trim().equals(AppContext.getInstance().getmWatchModel().getName())) {
            tv_Title.setText(AppContext.getInstance().getmWatchModel().getName());
        }
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        String firmware = AppContext.getInstance().getmWatchModel().getCurrentFirmware();
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2") ||
                (CommUtil.isNotBlank(firmware) && firmware.contains("D8_CH"))) {
            findViewById(R.id.school_defend).setVisibility(View.GONE);
            findViewById(R.id.layout_chat).setVisibility(View.GONE);
            findViewById(R.id.tv_unread).setVisibility(View.GONE);
        } else {
            findViewById(R.id.school_defend).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_chat).setVisibility(View.VISIBLE);
        }
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            resideMenu.tv_about_watch.setText(R.string.about_locator);
            resideMenu.tv_watch_setting.setText(R.string.locator_setting);
            resideMenu.tv_watch_fare.setText(R.string.locator_fare);
            resideMenu.tv_change_watch.setText(R.string.change_locator);
            resideMenu.tv_watch_album.setText(R.string.locator_album);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
//        mLBSUtil.clearSignalStrengthsListener();
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient.onDestroy();
//        }
//        mlocationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unReceiver();
    }

    private void SendDeviceCommand(String commandType) {
        Log.v("zzz", "SendDeviceCommand");
        WebService ws = new WebService(mContext, _SendDeviceCommand, true, "SendDeviceCommand");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        property.add(new WebServiceProperty("commandType", commandType));
        if (commandType.equals("Monitor"))
            property.add(new WebServiceProperty("paramter", AppData.GetInstance(mContext).getLoginName()));

        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void LinkDeviceCheck() {
        WebServiceUtils.LinkDeviceCheck(mContext, _LinkDeviceCheck, serialNumber, mContext);
        /*
         * WebService ws = new WebService(mContext, _LinkDeviceCheck,true,
         * "LinkDeviceCheck"); List<WebServiceProperty> property = new
         * LinkedList<WebServiceProperty>(); property.add(new
         * WebServiceProperty("loginId",
         * AppData.GetInstance(this).getLoginId())); property.add(new
         * WebServiceProperty("serialNumber", serialNumber));
         * ws.addWebServiceListener(mContext); ws.SyncGet(property);
         */
    }

    private void LinkDevice(String photo, String name) {
        Log.v("zzz", "LinkDevice");
        WebService ws = new WebService(mContext, _LinkDevice, true, "LinkDevice");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        if (!photo.equals("-1")) {
            property.add(new WebServiceProperty("photo", photo));
        }
        property.add(new WebServiceProperty("name", name));
        property.add(new WebServiceProperty("bindNumber", serialNumber));
        String language;
        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
            language = "2";
        } else if (getResources().getConfiguration().locale.getCountry().equals("TW")) {
            language = "3";
        } else {
            language = "1";
        }
        property.add(new WebServiceProperty("language", language));
        property.add(new WebServiceProperty("timeZone", DateConversion.getTimeZoneMinute()));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void LinkDeviceConfirm(String deviceId, String userId, String photo, String name, String confirm) {
        Log.v("zzz", "LinkDeviceConfirm");
        WebService ws = new WebService(mContext, _LinkDeviceConfirm, true, "LinkDeviceConfirm");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", deviceId));
        property.add(new WebServiceProperty("userId", userId));
        if (confirm.equals("1")) {
            property.add(new WebServiceProperty("name", name));
            property.add(new WebServiceProperty("photo", photo));
        }
        property.add(new WebServiceProperty("confirm", confirm));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void GetAddress(double lat, double lng) {
        Log.v("zzz", "GetAddress");
        AddressDao mAddressDao = new AddressDao(this);
        String address = mAddressDao.getAddress(lat, lng);

        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 0,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);



//        if (TextUtils.isEmpty(address)) {
//            WebService ws = new WebService(mContext, _GetAddress, true, "GetAddress");
//            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
//            property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
//            property.add(new WebServiceProperty("mapType", "1"));
//            property.add(new WebServiceProperty("lat", String.valueOf(lat)));
//            property.add(new WebServiceProperty("lng", String.valueOf(lng)));
//            ws.addWebServiceListener(mContext);
//            ws.SyncGet(property);
//        } else {
//            mAddress = address;
//            tv_state.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null) + " " + mAddress);
//            tv_time.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null));
//            saveBackupState();
//        }
    }

    private final int _LinkDevice = 1;
    private final int _LinkDeviceCheck = 2;
    private final int _GetDeviceDetail = 3;
    private final int _GetDeviceSet = 4;
    private final int _GetDeviceState = 5;
    private final int _GetDeviceContact = 6;
    private final int _RefreshDeviceState = 7;
    private final int _LinkDeviceConfirm = 8;
    private final int _SendDeviceCommand = 9;
    private final int _CheckAppVersion = 10;
    private final int _GetAddress = 11;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        Log.v("zzz", "onWebServiceReceive");
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String deviceId = null;
            if (id == _LinkDeviceCheck) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1表示未关联，选择头像输入名字就完成首次关联
                    Intent intent = new Intent(mContext, AddContactsA.class);
                    intent.putExtra("typeIndex", 2);
                    // intent.putExtra("serialNumber", serialNumber);
                    startActivityForResult(intent, ADDWATCHS);
                } else if (code == 2) {
                    // 2表示已经关联，输入名字后请求管理员确认
                    editDialog();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                } else if (code == 3) {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(R.string.device_no_exist).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _LinkDevice) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1成功
                    deviceId = jsonObject.getString("DeviceID");
                    if (deviceId.equals("-1")) {
                        MToast.makeText(jsonObject.getString("Message")).show();
                    } else {
                        WebServiceUtils.getDeviceList(mContext);
                        /*WebServiceUtils.GetDeviceContact(mContext, _GetDeviceContact, deviceId, null, false, false);
						WebServiceUtils.GetDeviceSet(mContext, _GetDeviceSet, deviceId, null,false);
						WebServiceUtils.GetDeviceState(mContext, _GetDeviceState, deviceId, null);*/
                        MToast.makeText(R.string.bind_suc).show();
                    }
                    // MToast.makeText(R.string.wait_admin_confirm).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(R.string.bind_fail).show();
                }
            } else if (id == _LinkDeviceConfirm) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.deleteUnconfirmed(userId, this.deviceId);
                    /*WebServiceUtils.GetDeviceContact(mContext, _GetDeviceContact, String.valueOf(mMsgRecordList.get(selectPosition).getDeviceID()), null, true, false);*/
                    AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(mContext).getSelectDeviceId()));
                    /*WebServiceUtils.GetDeviceContact(mContext, _GetDeviceContact, String.valueOf(this.deviceId), mContext, false, false);*/
                } else if (code == -1 || code == 8) {
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else {
                    MToast.makeText(R.string.add_contacts_fail).show();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceDetail) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1成功
                    WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
                    mWatchModel.setUserId(jsonObject.getIntValue("UserId"));
                    mWatchModel.setModel(jsonObject.getString("DeviceModelID"));
                    mWatchModel.setName(jsonObject.getString("BabyName"));
                    mWatchModel.setAvatar(jsonObject.getString("Photo"));
                    mWatchModel.setPhone(jsonObject.getString("PhoneNumber"));
                    mWatchModel.setCornet(jsonObject.getString("PhoneCornet"));
                    mWatchModel.setGender(jsonObject.getString("Gender"));
                    mWatchModel.setBirthday(jsonObject.getString("Birthday"));
                    mWatchModel.setGrade(jsonObject.getIntValue("Grade"));
                    mWatchModel.setHomeAddress(jsonObject.getString("HomeAddress"));
                    mWatchModel.setHomeLat(jsonObject.getDouble("HomeLat"));
                    mWatchModel.setHomeLng(jsonObject.getDouble("HomeLng"));
                    mWatchModel.setSchoolAddress(jsonObject.getString("SchoolAddress"));
                    mWatchModel.setSchoolLat(jsonObject.getDouble("SchoolLat"));
                    mWatchModel.setSchoolLng(jsonObject.getDouble("SchoolLng"));
                    mWatchModel.setLastestTime(jsonObject.getString("LatestTime"));
                    mWatchModel.setActiveDate(jsonObject.getString("ActiveDate"));
                    mWatchModel.setCreateTime(jsonObject.getString("CreateTime"));
                    mWatchModel.setBindNumber(jsonObject.getString("BindNumber"));
                    mWatchModel.setCurrentFirmware(jsonObject.getString("CurrentFirmware"));
                    mWatchModel.setFirmware(jsonObject.getString("Firmware"));
                    mWatchModel.setHireExpireDate(jsonObject.getString("HireExpireDate"));
                    mWatchModel.setUpdateTime(jsonObject.getString("UpdateTime"));
                    mWatchModel.setSerialNumber(jsonObject.getString("SerialNumber"));
                    mWatchModel.setPassword(jsonObject.getString("Password"));
                    mWatchModel.setIsGuard(jsonObject.getString("IsGuard").equals("1") ? true : false);
                    WatchDao mWatchDao = new WatchDao(this);
                    mWatchDao.saveWatch(mWatchModel);

                    initBabyInfo();
                    // MToast.makeText(R.string.wait_admin_confirm).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    //sMToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceSet) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1成功
                    WatchSetModel mWatchSetModel = new WatchSetModel();
                    mWatchSetModel.setDeviceId(Integer.valueOf(deviceId));
                    String[] strs = jsonObject.getString("SetInfo").split("-");
                    mWatchSetModel.setAutoAnswer(strs[0]);
                    mWatchSetModel.setReportLocation(strs[1]);
                    mWatchSetModel.setSomatoAnswer(strs[2]);
                    mWatchSetModel.setReservedPower(strs[3]);
                    mWatchSetModel.setClassDisabled(strs[4]);
                    mWatchSetModel.setTimeSwitch(strs[5]);
                    mWatchSetModel.setRefusedStranger(strs[6]);
                    mWatchSetModel.setCallSound(strs[7]);
                    mWatchSetModel.setCallVibrate(strs[8]);
                    mWatchSetModel.setMsgSound(strs[9]);
                    mWatchSetModel.setMsgVibrate(strs[10]);
                    mWatchSetModel.setClassDisableda(jsonObject.getString("ClassDisabled1"));
                    mWatchSetModel.setClassDisabledb(jsonObject.getString("ClassDisabled2"));
                    mWatchSetModel.setWeekDisabled(jsonObject.getString("WeekDisabled"));
                    mWatchSetModel.setTimerOpen(jsonObject.getString("TimerOpen"));
                    mWatchSetModel.setTimerClose(jsonObject.getString("TimerClose"));
                    mWatchSetModel.setBrightScreen(jsonObject.getString("BrightScreen"));
                    mWatchSetModel.setWeekAlarm1(jsonObject.getString("WeekAlarm1"));
                    mWatchSetModel.setWeekAlarm2(jsonObject.getString("WeekAlarm2"));
                    mWatchSetModel.setWeekAlarm3(jsonObject.getString("WeekAlarm3"));
                    mWatchSetModel.setAlarm1(jsonObject.getString("Alarm1"));
                    mWatchSetModel.setAlarm2(jsonObject.getString("Alarm2"));
                    mWatchSetModel.setAlarm3(jsonObject.getString("Alarm3"));
                    mWatchSetModel.setLocationMode(jsonObject.getString("LocationMode"));
                    mWatchSetModel.setLocationTime(jsonObject.getString("LocationTime"));
                    mWatchSetModel.setFlowerNumber(jsonObject.getString("FlowerNumber"));
                    //mWatchSetModel.setLanguage(jsonObject.getString("Language"));
                    //mWatchSetModel.setTimeZone(jsonObject.getString("TimeZone"));
                    mWatchSetModel.setCreateTime(jsonObject.getString("CreateTime"));
                    mWatchSetModel.setUpdateTime(jsonObject.getString("UpdateTime"));
                    mWatchSetModel.setVersionNumber(jsonObject.getString("VersionNumber"));
                    mWatchSetModel.setSleepCalculate(jsonObject.getString("SleepCalculate"));
                    mWatchSetModel.setStepCalculate(jsonObject.getString("StepCalculate"));
                    mWatchSetModel.setHrCalculate(jsonObject.getString("HrCalculate"));
                    mWatchSetModel.setSosMsgswitch(jsonObject.getString("SosMsgswitch"));
                    WatchSetDao mWatchDao = new WatchSetDao(this);
                    mWatchDao.saveWatchSet(mWatchSetModel);
                    // MToast.makeText(R.string.wait_admin_confirm).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceState) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1成功
                    WatchStateModel mWatchStateModel = new WatchStateModel();
                    mWatchStateModel.setDeviceId(Integer.valueOf(deviceId));
                    mWatchStateModel.setAltitude(jsonObject.getDouble("Altitude"));
                    mWatchStateModel.setLatitude(jsonObject.getDouble("Latitude"));
                    mWatchStateModel.setLongitude(jsonObject.getDouble("Longitude"));
                    mWatchStateModel.setCourse(jsonObject.getString("Course"));
                    mWatchStateModel.setElectricity(jsonObject.getString("Electricity"));
                    mWatchStateModel.setStep(jsonObject.getString("Step"));
                    mWatchStateModel.setHealth(jsonObject.getString("Health"));
                    mWatchStateModel.setOnline(jsonObject.getString("Online"));
                    mWatchStateModel.setSpeed(jsonObject.getString("Speed"));
                    mWatchStateModel.setSatelliteNumber(jsonObject.getString("SatelliteNumber"));
                    mWatchStateModel.setSocketId(jsonObject.getString("SocketId"));
                    mWatchStateModel.setCreateTime(jsonObject.getString("CreateTime"));
                    mWatchStateModel.setServerTime(jsonObject.getString("ServerTime"));
                    mWatchStateModel.setUpdateTime(jsonObject.getString("UpdateTime"));
                    mWatchStateModel.setDeviceTime(jsonObject.getString("DeviceTime"));
                    mWatchStateModel.setLBS(jsonObject.getString("LBS"));
                    mWatchStateModel.setGSM(jsonObject.getString("GSM"));
                    mWatchStateModel.setWifi(jsonObject.getString("Wifi"));

                    WatchStateDao mWatchStateDao = new WatchStateDao(this);
                    mWatchStateDao.saveWatchState(mWatchStateModel);

                    // MToast.makeText(R.string.wait_admin_confirm).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceContact) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1成功
                    JSONArray arrContact = jsonObject.getJSONArray("ContactArr");
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.deleteWatchContact(this.deviceId);
                    for (int j = 0; j < arrContact.size(); j++) {
                        JSONObject item = arrContact.getJSONObject(j);
                        ContactModel mContactModel = new ContactModel();
                        mContactModel.setId(item.getString("DeviceContactId"));
                        mContactModel.setFromId(this.deviceId);
                        mContactModel.setObjectId(item.getString("ObjectId"));
                        mContactModel.setRelationShip(item.getString("Relationship"));
                        mContactModel.setAvatar(item.getString("Photo"));
                        mContactModel.setAvatarUrl(item.getString("HeadImg"));
                        mContactModel.setPhone(item.getString("PhoneNumber"));
                        mContactModel.setCornet(item.getString("PhoneShort"));
                        mContactModel.setType(item.getString("Type"));

                        mContactDao.saveContact(mContactModel);
                    }
                    if (this.deviceId == AppData.GetInstance(this).getSelectDeviceId()) {
                        AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(this).getSelectDeviceId()));
                    }
                    // MToast.makeText(R.string.wait_admin_confirm).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _SendDeviceCommand) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    // 1成功
                    MToast.makeText(R.string.send_order_suc).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    MToast.makeText(R.string.send_order_fail).show();
                }
            } else if (id == _RefreshDeviceState) {
                int code = jsonObject.getIntValue("Code");
                //		Log.v("zjy", "_RefreshDeviceState code:"+code);
                if (code == 1) {
                    mMainFrameTask = new MainFrameTask(this);
                    mMainFrameTask.execute();
                } else {
                    MToast.makeText(R.string.send_order_fail).show();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _CheckAppVersion) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    if (getVersionId() < jsonObject.getIntValue("AndroidVersion")) {
                        downUrl = jsonObject.getString("AndroidUrl");
                        updateDialog(mContext.getResources().getString(R.string.have_new_version) + jsonObject.getDouble("AndroidVersion"), jsonObject.getString("AndroidDescription"));
                    } else {
                        //MToast.makeText(R.string.is_new_version).show();
                    }
                } else {
                    // 系统异常小于0，常规异常大于0
                    /*if (TextUtils.isEmpty(jsonObject.getString("Message")))
                     * MToast.makeText(jsonObject.getString("Message")).show();*/
                }
            } else if (id == _GetAddress) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
					/*if(jsonObject.getString("Address").contains(",")){
						mAddress = jsonObject.getString("Address").split(",")[0] + "," + jsonObject.getString("Address").split(",")[1] + " 附近";
					}else{
						mAddress = jsonObject.getString("Address") + " 附近";
					}*/
                    mAddress = jsonObject.getString("Province") + jsonObject.getString("City") + jsonObject.getString("District") + jsonObject.getString("Road");
                    JSONArray array = jsonObject.getJSONArray("Nearby");
					/*for(int i = 0;i < array.length();i++){
						JSONObject item = array.getJSONObject(i);
						mAddress = mAddress + "," + item.getString("POI");
					}*/
                    if (array.size() > 0)
                        mAddress = mAddress + "," + array.getJSONObject(0).getString("POI");
                    if (TextUtils.isEmpty(mAddress) || mAddress.equals("null")) {
                        mAddress = getResources().getString(R.string.no_result);
                    } else {
                        saveBackupState();
                        saveLocationAddress();
                    }
                    tv_state.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null) + " " + mAddress);
                    tv_time.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null));
                } else {
                    // 系统异常小于0，常规异常大于0
                    tv_state.setText(R.string.no_result);
                    tv_time.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "出错：", e);
        }
    }

    private void saveBackupState() {
        mHealthModel.setDeviceId(AppData.GetInstance(this).getSelectDeviceId());
        mHealthModel.setLatitude(mWatchStateModel.getLatitude());
        mHealthModel.setLongitude(mWatchStateModel.getLongitude());
        mHealthModel.setDeviceTime(mWatchStateModel.getDeviceTime());
        mHealthModel.setLocationType(mWatchStateModel.getLocationType());
        mHealthModel.setAddress(mAddress);
        HealthDao mHealthDao = new HealthDao(mContext);
        mHealthDao.saveHealth(mHealthModel);
    }

    private void saveLocationAddress() {
        AddressModel mAddressModel = new AddressModel();
        mAddressModel.setLatitude(mWatchStateModel.getLatitude());
        mAddressModel.setLongitude(mWatchStateModel.getLongitude());
        mAddressModel.setAddress(mAddress);
        AddressDao mAddressDao = new AddressDao(mContext);
        mAddressDao.saveAddress(mAddressModel);
    }

    private void CheckAppVersion() {
        if (AppData.GetInstance(mContext).isHintUpdate()) {
            WebService ws = new WebService(mContext, _CheckAppVersion, true, "CheckAppVersion");
            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
            property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
            ws.addWebServiceListener(mContext);
            ws.SyncGet(property);
            //AppData.GetInstance(mContext).setHintUpdate(false);
        }
    }

    private void updateDialog(String title, String content) {
        View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        /*
         * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
         * getWindowManager().getDefaultDisplay().getHeight()/2;
         */
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(title);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(content);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downUrl));
                AppData.GetInstance(mContext).setLoginAuto(false);
                startActivity(intent);
				/*Intent intent = new Intent(mContext, UpdateService.class);
				intent.putExtra("Key_App_Name", appName);
				intent.putExtra("Key_Down_Url", downUrl);
				startService(intent);*/
                dialog.cancel();
            }
        });
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

    /******* down APP name ******/
    public static String appName = "ZJT";
    /******* down APP address *******/
    public static String downUrl = "";

    public int getVersionId() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void initReceiver() {
		/*IntentFilter IntentFilter_a = new IntentFilter(Contents.chatBrodcastForSelectWatch);
		IntentFilter_a.setPriority(4);
		registerReceiver(chatReceiverForSelectWatch, IntentFilter_a);*/

        IntentFilter IntentFilter_b = new IntentFilter(Contents.askBindingBrodcast);
        IntentFilter_b.setPriority(5);
        registerReceiver(askBindingReceiver, IntentFilter_b);

        IntentFilter IntentFilter_c = new IntentFilter(Contents.BrodcastForUnread);
        IntentFilter_c.setPriority(4);
        registerReceiver(ReceiverForUnread, IntentFilter_c);

        IntentFilter IntentFilter_d = new IntentFilter(Contents.changeStateBrodcastForSelectWatch);
        IntentFilter_d.setPriority(5);
        registerReceiver(ChangeStateReceiver, IntentFilter_d);
    }

    private void unReceiver() {
        try {
            unregisterReceiver(askBindingReceiver);
            unregisterReceiver(ReceiverForUnread);
            unregisterReceiver(ChangeStateReceiver);

        } catch (Exception e) {
        }
    }

    private int unreadChatMsg = 0;
    private BroadcastReceiver ReceiverForUnread = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceId = intent.getStringExtra("deviceId");
            String New = intent.getStringExtra("New");
            String message = intent.getStringExtra("Message");
            String SMS = intent.getStringExtra("SMS");
            String voice = intent.getStringExtra("Voice");
            String Photo = intent.getStringExtra("Photo");
            //System.out.println(deviceId+"  "+message +" " + SMS +" " + voice);
            if (!TextUtils.isEmpty(voice)) {
                WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                if (mWatchModel == null) {
                    return;
                }
                String firmware = mWatchModel.getCurrentFirmware();
                if (Integer.valueOf(voice) > 0 &&
                        (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2") ||
                                (CommUtil.isNotBlank(firmware) && !firmware.contains("D8_CH")))) {
                    tv_unread.setText(voice);
                    tv_unread.setVisibility(View.VISIBLE);
                } else {
                    tv_unread.setVisibility(View.GONE);
                }
            }
            if (!TextUtils.isEmpty(message)) {
                if (Integer.valueOf(message) > 0) {
                    iv_unRead_msg_record.setVisibility(View.VISIBLE);
                } else {
                    iv_unRead_msg_record.setVisibility(View.GONE);
                }
            }
            if (!TextUtils.isEmpty(SMS)) {
                if (Integer.valueOf(SMS) > 0) {
                    resideMenu.iv_unRead_watch_fare.setVisibility(View.VISIBLE);
                } else {
                    resideMenu.iv_unRead_watch_fare.setVisibility(View.GONE);
                }
            }
            if (!TextUtils.isEmpty(Photo)) {
                if (Integer.valueOf(Photo) > 0) {
                    resideMenu.iv_unRead_album.setVisibility(View.VISIBLE);
                } else {
                    resideMenu.iv_unRead_album.setVisibility(View.GONE);
                }
            }
        }
    };
    private BroadcastReceiver chatReceiverForSelectWatch = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
            if (mWatchModel == null) {
                return;
            }
            if (type == 1 && (!mWatchModel.getCurrentFirmware().contains("D8_CH") || (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")))) {
                unreadChatMsg++;
                tv_unread.setText(String.valueOf(unreadChatMsg));
                tv_unread.setVisibility(View.VISIBLE);
            }
        }
    };
    private int deviceId;
    private String userId;
    private String name;
    private BroadcastReceiver askBindingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            if (type == 2) {
                deviceId = intent.getIntExtra("deviceId", 0);
                userId = intent.getStringExtra("userId");
                name = intent.getStringExtra("name");
                String msg = intent.getStringExtra("Msg");
                askBindingDialog(msg, intent.getStringExtra("userId"), intent.getIntExtra("deviceId", 0));
            }
        }
    };
    private Dialog dialog;

    private void askBindingDialog(String msg, final String userId, final int device) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        /*
         * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
         * getWindowManager().getDefaultDisplay().getHeight()/2;
         */
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv.setText(R.string.ask_binding_locator);
        } else {
            tv.setText(R.string.ask_binding_watch);
        }
        tv_content.setText(msg);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setText(R.string.agree);
        btn_cancel.setText(R.string.refuse);
        btn_OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddContactsA.class);
                intent.putExtra("typeIndex", 3);
                intent.putExtra("deviceId", mContext.deviceId);
                intent.putExtra("userId", userId);
                // intent.putExtra("serialNumber", serialNumber);
                startActivityForResult(intent, AGREEBIND);

                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinkDeviceConfirm(String.valueOf(deviceId), userId, null, null, "0");
                dialog.cancel();
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void editDialog() {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
		/*wl.x = getWindowManager().getDefaultDisplay().getWidth()/2;
		wl.y = getWindowManager().getDefaultDisplay().getHeight()/2;
		*/
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(R.string.input_your_name);
        final EditText et = (EditText) view.findViewById(R.id.et);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    return;
                }
                LinkDevice("-1", name);
                dialog.cancel();
            }
        });
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

    private void makeSureDialog(final int index) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
		/*wl.x = getWindowManager().getDefaultDisplay().getWidth()/2;
		wl.y = getWindowManager().getDefaultDisplay().getHeight()/2;
		*/
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        switch (index) {
            case 0:
                tv.setText(R.string.remote_monitoring);
                tv_content.setText(R.string.sure_remote_monitoring);
                break;
            case 1:
                WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    tv.setText(R.string.find_locator);
                    tv_content.setText(R.string.sure_find_locator);
                } else {
                    tv.setText(R.string.find_watch);
                    tv_content.setText(R.string.sure_find_watch);
                }
                break;
            case 2:
                tv.setText(R.string.remote_shutdown);
                tv_content.setText(R.string.sure_remote_shutdown);
                break;
        }
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (index) {
                    case 0:
                        SendDeviceCommand("Monitor");
                        break;
                    case 1:
                        SendDeviceCommand("Find");
                        break;
                    case 2:
                        SendDeviceCommand("PowerOff");
                        break;
                }
                dialog.cancel();
            }
        });
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

    private final int ADDWATCH = 0;
    private final int ADDWATCHS = 1;
    private final int AGREEBIND = 2;
    private final int CHANGEWATCH = 3;
    private String serialNumber;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADDWATCH:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    //MToast.makeText(scanResult).show();
                    // scanResult = "1212121212";
                    serialNumber = scanResult;
                    LinkDeviceCheck();
                }
                break;
            case ADDWATCHS:
                if (resultCode == RESULT_OK) {
                    String photo = data.getStringExtra("photo");
                    String name = data.getStringExtra("name");
                    LinkDevice(photo, name);
                    //WebServiceUtils.LinkDevice(_LinkDevice, photo, name, serialNumber, mContext);
                }
                break;
            case AGREEBIND:
                if (resultCode == RESULT_OK) {
                    //System.out.println("AGREEBIND");
                    String photo = data.getStringExtra("photo");
                    String name = data.getStringExtra("name");
                    LinkDeviceConfirm(String.valueOf(deviceId), userId, photo, name, "1");
                    // WebServiceUtils.LinkDevice(_LinkDevice, photo, name,
                    // serialNumber, mContext);
                }
                break;
            case CHANGEWATCH:
                if (resultCode == RESULT_OK) {

                }
                break;
            default:
                break;
        }
    }

    private MProgressDialog mProgressDialog = null;

    private void startProgressDialog() {
        avl.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_refresh).setVisibility(View.INVISIBLE);
        tv_state.setText(R.string.get_location);
		/*if (mProgressDialog == null) {
			mProgressDialog = MProgressDialog.createDialog(this);
			mProgressDialog.setMessage(getResources().getString(R.string.load_location));
			mProgressDialog.setCancelable(true);
		}
		mProgressDialog.show();*/
    }

    private void stopProgressDialog() {
        avl.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_refresh).setVisibility(View.VISIBLE);
		/*if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}*/
    }

    private MainFrameTask mMainFrameTask = null;

    public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
        private Main main = null;

        public MainFrameTask(Main main) {
            this.main = main;
        }

        @Override
        protected void onCancelled() {
            stopProgressDialog();
            super.onCancelled();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            startProgressDialog();
        }

        @Override
        protected void onPostExecute(Integer result) {
            stopProgressDialog();
            if (TextUtils.isEmpty(mAddress) || mAddress.equals("null")) {
                mAddress = getResources().getString(R.string.no_result);
            }
            tv_state.setText(DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), "") + mAddress);
            //MToast.makeText(R.string.get_location_fail).show();
        }
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 判断菜单是否关闭
            if (menuClose) {
                // 判断两次点击的时间间隔（默认设置为2秒）
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    MToast.makeText(R.string.press_exit).show();
                    mExitTime = System.currentTimeMillis();
                    if (BuildConfig.SHOW_ADS) {
                        initIntervalAD();
                    }
                } else {
                    finish();
                    System.exit(0);
                    super.onBackPressed();
                }
            } else {
                resideMenu.closeMenu();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initIntervalAD() {
        Adx_Tool.adIntervalInit(this, new Listener() {
            @Override
            public void onAdClick(String s) {

            }

            @Override
            public void onAdClosed(String s) {

            }

            @Override
            public void onAdFailed(String s) {

            }

            @Override
            public void onAdInitFailed(String s) {

            }

            @Override
            public void onAdInitSucessed(String s) {
                Adx_Tool.adIntervalShow(Main.this, 0);
            }

            @Override
            public void onAdNoAd(String s) {

            }

            @Override
            public void onAdPresent(String s) {

            }
        });
    }

    private void initBabyInfo() {
        if (!tv_Title.getText().toString().trim().equals(AppContext.getInstance().getmWatchModel().getName())) {
            tv_Title.setText(AppContext.getInstance().getmWatchModel().getName());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
//		Log.v("zjy", "onMapReady");
        if (mMapSelect == 1) {
/*			mGaoDeMap = mGaoDeMapFragment.getMap();	
			mGaoDeMapUtil = new GaoDeMapUtil(mGaoDeMap);
			mGaoDeMapUtil.initMap();
			mMapUtil = new MapUtil(mGaoDeMapUtil);*/
        } else {
            mGoogleMap = map;
            mGoogleMapUtil = new GoogleMapUtil(mGoogleMap);
            //	mGoogleMapUtil.initMap();
            mMapUtil = new MapUtil(mGoogleMapUtil);
            mMapUtil.initMap();
            //	mGoogleMap.setLocationSource(this);// 设置定位监听  <<<<<<<<<<<<===================================

            //first locate operation
            mMapUtil.clear();
            mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), 18, 0, 0, 1000, null);
            //addMarker(new LatLng(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude()),R.drawable.location_watch,true,true);
            mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, true, true);
            //GetAddress(40.8694240429,-74.0720558167);
            GetAddress(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_layers:
                if (mMapUtil == null) {
                    break;
                }
                if (isChecked) {
                    mMapUtil.setMapTypeToSatellite(true);// 卫星地图模式
                } else {
                    mMapUtil.setMapTypeToSatellite(false);// 矢量地图模式
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (resideMenu != null && resideMenu.isOpened()) {
            resideMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}