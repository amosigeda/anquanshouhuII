package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.Projection;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.maputil.GaoDeMapUtil;
import vip.inteltech.gat.maputil.GoogleMapUtil;
import vip.inteltech.gat.maputil.MapUtil;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.viewutils.MToast;

public class SetLocation extends BaseFragmentActivity implements OnClickListener, LocationSource,
        AMapLocationListener, OnGeocodeSearchListener, WebServiceListener, OnMapReadyCallback {
    private SetLocation mContext;
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
    private Circle circle;
    private GeocodeSearch geocoderSearch;
    private int mMapSelect;

    private TextView tv_Title, tv_adress;
    private boolean isHome;
    private LatLng mLatLng = null;
    private String mAddress;

    private WatchModel mWatchModel;

    private MapUtil mMapUtil;
    private GaoDeMapUtil mGaoDeMapUtil;
    private GoogleMapUtil mGoogleMapUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_location);
        mWatchModel = AppContext.getInstance().getmWatchModel();
        mMapSelect = AppData.GetInstance(mContext).getMapSelect();

        Intent intent = getIntent();
        isHome = intent.getBooleanExtra("isHome", false);

        tv_Title = (TextView) findViewById(R.id.tv_Title);
        tv_Title.setText(isHome ? R.string.set_home_location : R.string.set_school_location);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_amplification).setOnClickListener(this);
        findViewById(R.id.btn_shrink).setOnClickListener(this);

        tv_adress = (TextView) findViewById(R.id.tv_adress);

        mGaoDeMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.GaoDeMap);
        //GoogleMap member aquire in the function---OnMapReady
        mGoogleMapFragment = ((com.google.android.gms.maps.SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.GoogleMap));
        mGoogleMapFragment.getMapAsync(this);

        if (mMapSelect == 1) {
            mGaoDeMap = mGaoDeMapFragment.getMap();
            mGaoDeMapUtil = new GaoDeMapUtil(mGaoDeMap);
            mMapUtil = new MapUtil(mGaoDeMapUtil);
            mMapUtil.initMap();
            mGaoDeMap.setLocationSource(this);// 设置定位监听
            GaoDeMapListenerInit();
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

        mContext = this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                Intent intent_a = new Intent(mContext, SearchLocation.class);
                if (!isHome) {
                    intent_a.putExtra("isHome", isHome);
                    startActivityForResult(intent_a, SETSCHOOLLOCATION);
                } else {
                    intent_a.putExtra("isHome", isHome);
                    startActivityForResult(intent_a, SETHOMELOCATION);
                }
                break;
            case R.id.btn_save:
                UpdateDeviceSet();
                break;
            case R.id.btn_amplification:
                //	mGaoDeMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
                mMapUtil.animateCam(true, 1000, null);
                break;
            case R.id.btn_shrink:
                //	mGaoDeMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
                mMapUtil.animateCam(false, 1000, null);
                break;
        }
    }

    private void UpdateDeviceSet() {
        Log.v("kkk", "UpdateDeviceSet: " + mLatLng.latitude + " " + mLatLng.longitude);
        if (TextUtils.isEmpty(mAddress) || mLatLng == null) {
            MToast.makeText(R.string.select_right_location).show();
            return;
        }
        WebService ws = new WebService(mContext, _UpdateDevice, true, "UpdateDevice");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
        if (isHome) {
            property.add(new WebServiceProperty("homeAddress", mAddress));
            property.add(new WebServiceProperty("homeLat", String.valueOf(mLatLng.latitude)));
            property.add(new WebServiceProperty("homeLng", String.valueOf(mLatLng.longitude)));
        } else {
            property.add(new WebServiceProperty("schoolAddress", mAddress));
            property.add(new WebServiceProperty("schoolLat", String.valueOf(mLatLng.latitude)));
            property.add(new WebServiceProperty("schoolLng", String.valueOf(mLatLng.longitude)));
        }
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void GetAddress(double lat, double lng) {
        WebService ws = new WebService(mContext, _GetAddress, true,
                "GetAddress");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData
                .GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("mapType", "1"));
        property.add(new WebServiceProperty("lat", String.valueOf(lat)));
        property.add(new WebServiceProperty("lng", String.valueOf(lng)));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _UpdateDevice = 0;
    private final int _GetAddress = 1;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _UpdateDevice) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //MToast.makeText(jsonObject.getString("Message")).show();
                    if (isHome) {
                        mWatchModel.setHomeAddress(mAddress);
                        mWatchModel.setHomeLat(mLatLng.latitude);
                        mWatchModel.setHomeLng(mLatLng.longitude);
                    } else {
                        mWatchModel.setSchoolAddress(mAddress);
                        mWatchModel.setSchoolLat(mLatLng.latitude);
                        mWatchModel.setSchoolLng(mLatLng.longitude);
                    }

                    WatchDao mWatchDao = new WatchDao(this);
                    mWatchDao.updateWatch(AppData.GetInstance(mContext).getSelectDeviceId(), mWatchModel);
                } else {
                    // -2系统异常
                    MToast.makeText(R.string.edit_fail).show();
                }/*else if (code == -2) {
					// -2系统异常
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -3) {
					// -3无权操作设备
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -1){
					// -1设备参数错误
				} else if (code == 0){
					// 0登录异常
				}*/
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            } else if (id == _GetAddress) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    mAddress = jsonObject.getString("Province") +
                            jsonObject.getString("City") +
                            jsonObject.getString("District") +
                            jsonObject.getString("Road");
                    JSONArray array = jsonObject.getJSONArray("Nearby");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        mAddress = mAddress + "," + item.getString("POI");
                    }
                    mAddress = mAddress + " 附近 ";
                    tv_adress.setText(mAddress);
                } else {
                    // 系统异常小于0，常规异常大于0
                    tv_adress.setText(R.string.no_result);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // ------------------------------------------------------地图相关---------------------------------------------------------------//

    /**
     * 初始化AMap对象
     */

    private void GaoDeMapListenerInit() {
/*		if (mGaoDeMap == null) {
			setUpMap();
		}*/
        // 对amap添加单击地图事件监听器
        mGaoDeMap.setOnMapClickListener(latLng -> {
            /*if (aMap != null) {
                aMap.clear();
            }*/
            //	mMarker.remove();
            //	circle.remove();
            //addMarker(latLng);
            mMapUtil.addCircle(latLng.latitude, latLng.longitude, isHome ? R.drawable.location_home : R.drawable.location_school, 500, Color.argb(55, 253, 15, 222), Color.argb(55, 255, 80, 80), 10, false, false);
            mLatLng = latLng;
            tv_adress.setText(R.string.get_location);
            GetAddress(mLatLng.latitude, mLatLng.longitude);
        });
        mGaoDeMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(mMarker)) {

                }
                return false;
            }
        });
    }

    private void addMarker(LatLng latLng) {
        Log.v("kkk", "addMarker");
        mAddress = "";
        tv_adress.setText(R.string.get_location);
        GetAddress(latLng.latitude, latLng.longitude);
        //getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
        markerOption = new MarkerOptions();
        //markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory
                .fromResource(isHome ? R.drawable.location_home : R.drawable.location_school));
        mMarker = mGaoDeMap.addMarker(markerOption);
        mMarker.setPosition(latLng);
        // 绘制一个圆形
        circle = mGaoDeMap.addCircle(new CircleOptions().center(latLng)
                .radius(500).strokeColor(Color.argb(55, 253, 15, 222))
                .fillColor(Color.argb(55, 255, 80, 80)).strokeWidth(10));
        //	jumpPoint(mMarker, latLng);
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }
    /**
     * 设置一些amap的属性
     */
/*	private void setUpMap() {
		Log.v("kkk", "setUpMap");
		// marker旋转90度
		//mMarker.setRotateAngle(90);
		
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_me));// 设置小蓝点的图标
		myLocationStyle.strokeColor(R.color.transparent);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
		//myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		mGaoDeMap.setMyLocationStyle(myLocationStyle);
		mGaoDeMap.setLocationSource(this);// 设置定位监听
		mGaoDeMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
		mGaoDeMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		mGaoDeMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
		mGaoDeMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

		// aMap.setMyLocationType()
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
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

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress())) {
                tv_adress.setText(result.getRegeocodeAddress().getFormatAddress() + "附近");
                mAddress = result.getRegeocodeAddress().getFormatAddress() + "附近";
            } else {
                tv_adress.setText(R.string.no_result);
                //暂无结果
            }
        } else if (rCode == 27) {
            tv_adress.setText(R.string.no_result);
            //网络错误
        } else if (rCode == 32) {
            tv_adress.setText(R.string.no_result);
            //key错误
        } else {
            tv_adress.setText(R.string.no_result);
            //其他错误
        }
    }

    /**
     * 定位成功后回调函数
     */
    private boolean firstLoad = true;

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        //	Log.v("kkk", "onLocationChanged");
        if (aLocation != null) {

        }

        if (/*mListener != null &&*/ aLocation != null) {
            //mListener.onLocationChanged(aLocation);// 显示系统小蓝点

            if (firstLoad) {
                if (isHome) {
                    if ((mWatchModel.getHomeLat() == 0) && (mWatchModel.getHomeLng() == 0)) {
                        mLatLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
                    } else {
                        mLatLng = new LatLng(mWatchModel.getHomeLat(), mWatchModel.getHomeLng());
                    }
                    mMapUtil.addCircle(mLatLng.latitude, mLatLng.longitude, R.drawable.location_home, 500, Color.argb(55, 253, 15, 222), Color.argb(55, 255, 80, 80), 10, false, false);
                } else {
                    if ((mWatchModel.getSchoolLat() == 0) && (mWatchModel.getSchoolLng() == 0)) {
                        mLatLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
                    } else {
                        mLatLng = new LatLng(mWatchModel.getSchoolLat(), mWatchModel.getSchoolLng());
                    }
                    mMapUtil.addCircle(mLatLng.latitude, mLatLng.longitude, R.drawable.location_school, 500, Color.argb(55, 253, 15, 222), Color.argb(55, 255, 80, 80), 10, false, false);
                }
                //System.out.println(aLocation.getLatitude()+ "  "+ aLocation.getLongitude());
                //System.out.println(mWatchModel.getSchoolLat() + "--"+ mWatchModel.getSchoolLng());
                tv_adress.setText(R.string.get_location);
                GetAddress(mLatLng.latitude, mLatLng.longitude);
                //	addMarker(mLatLng);
                //	mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                //			mLatLng, 16, 0, 30)), 1000, null);
                mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 16, 0, 0, 1000, null);
                //	Log.v("kkk", "onLocationChanged: "+mLatLng.latitude+" "+mLatLng.longitude);
                if (mLatLng.latitude != 0 && mLatLng.longitude != 0) {
                    firstLoad = false;
                    mGaoDeMap.setMyLocationEnabled(false);
                }
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        Log.v("kkk", "activate");
        mListener = listener;
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        Log.v("kkk", "deactivate");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    // ------------------------------------------------------生命周期---------------------------------------------------------------//

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        deactivate();
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
    }

    private final int SETSCHOOLLOCATION = 0;
    private final int SETHOMELOCATION = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETSCHOOLLOCATION:
                if (resultCode == RESULT_OK) {
                    mLatLng = new LatLng(data.getDoubleExtra("Lat", 0d), data.getDoubleExtra("Lng", 0d));
                    //	mGaoDeMap.clear();
                    //	addMarker(mLatLng);
                    //	mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    //			mLatLng, 18, 0, 30)), 1000, null);
                    tv_adress.setText(R.string.get_location);
                    GetAddress(mLatLng.latitude, mLatLng.longitude);
                    mMapUtil.addCircle(mLatLng.latitude, mLatLng.longitude, R.drawable.location_school, 500, Color.argb(55, 253, 15, 222), Color.argb(55, 255, 80, 80), 10, false, false);
                    mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 16, 0, 0, 1000, null);
                }
                break;
            case SETHOMELOCATION:
                if (resultCode == RESULT_OK) {
                    mLatLng = new LatLng(data.getDoubleExtra("Lat", 0d), data.getDoubleExtra("Lng", 0d));
                    //	mGaoDeMap.clear();
                    //	addMarker(mLatLng);
                    //	mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    //			mLatLng, 18, 0, 30)), 1000, null);
                    tv_adress.setText(R.string.get_location);
                    GetAddress(mLatLng.latitude, mLatLng.longitude);
                    mMapUtil.addCircle(mLatLng.latitude, mLatLng.longitude, R.drawable.location_home, 500, Color.argb(55, 253, 15, 222), Color.argb(55, 255, 80, 80), 10, false, false);
                    mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 16, 0, 0, 1000, null);
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
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
            GoogleMapListenerInit();
        }
    }

    private void GoogleMapListenerInit() {
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
                mMapUtil.addCircle(latLng.latitude, latLng.longitude, isHome ? R.drawable.location_home : R.drawable.location_school, 500, Color.argb(55, 253, 15, 222), Color.argb(55, 255, 80, 80), 10, false, false);
                mLatLng = new LatLng(latLng.latitude, latLng.longitude);
                tv_adress.setText(R.string.get_location);
                GetAddress(mLatLng.latitude, mLatLng.longitude);
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker arg0) {
                return true;//change from false to true,so that to prevent  googletools from showing when perform clicking on the marker
            }
        });
    }

}
