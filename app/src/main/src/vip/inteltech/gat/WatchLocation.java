package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.*;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MProgressDialog;
import vip.inteltech.gat.viewutils.MToast;

public class WatchLocation extends BaseActivity implements OnClickListener, LocationSource,
		AMapLocationListener, OnGeocodeSearchListener, WebServiceListener {
	private WatchLocation mContext;
	// 地图相关
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private MarkerOptions markerOption;
	private Marker mMarker;
	private GeocodeSearch geocoderSearch;
	
	private TextView tv_adress;
	private ImageView iv_Location_stype;
	private CheckBox cb_layers;
	private Button btn_refresh;
	private String mAddress;
	
	private WatchModel mWatchModel;
	private WatchStateModel mWatchStateModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.watch_location);
		
		mWatchModel = AppContext.getInstance().getmWatchModel();
		mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
		findViewById(R.id.btn_left).setOnClickListener(this);
		//findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_amplification).setOnClickListener(this);
		findViewById(R.id.btn_shrink).setOnClickListener(this);
		btn_refresh = (Button) findViewById(R.id.btn_refresh);
		btn_refresh.setOnClickListener(this);
		
		tv_adress = (TextView) findViewById(R.id.tv_adress);
		iv_Location_stype = (ImageView) findViewById(R.id.iv_Location_stype);
		cb_layers = (CheckBox) findViewById(R.id.cb_layers);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		aMapInit();
        mContext = this;
        if(mWatchStateModel.getDeviceId() != 0){
        	//System.out.println(mWatchStateModel.getDeviceId() + " " + mWatchStateModel.getUpdateTime());
        	initView();
        }else{
        	RefreshDeviceState();
        }
        initReceiver();
        cb_layers.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
				}else{
					aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
				}
			}
		});
	}
	private void initView(){
		WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
		if(mWatchStateModel.getLocationType().equals("1") || 
				mWatchModel.getCurrentFirmware().indexOf("D9_CHUANGMT_V") != -1 || 
				mWatchModel.getCurrentFirmware().indexOf("D10_CHUANGMT_V") != -1 || 
				mWatchModel.getCurrentFirmware().indexOf("D9_TP_CHUANGMT_V") != -1){
			iv_Location_stype.setImageResource(R.drawable.gps_icon);
		}else if(mWatchStateModel.getLocationType().equals("2")){
			iv_Location_stype.setImageResource(R.drawable.lbs_icon);
		}else if(mWatchStateModel.getLocationType().equals("3")){
			iv_Location_stype.setImageResource(R.drawable.wifi_icon);
		}
	}
	private void initReceiver(){
		/*IntentFilter IntentFilter_a = new IntentFilter(
				Contents.chatBrodcastForSelectWatch);
		IntentFilter_a.setPriority(4);
		registerReceiver(chatReceiverForSelectWatch, IntentFilter_a);*/

		IntentFilter IntentFilter_a = new IntentFilter(
				Contents.changeStateBrodcastForSelectWatch);
		IntentFilter_a.setPriority(5);
		registerReceiver(ChangeStateReceiver, IntentFilter_a);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			if (aMap != null) {
				aMap.clear();
			}
			GetDeviceState();
			break;
		case R.id.btn_amplification:
			aMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
			break;
		case R.id.btn_shrink:
			aMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
			break;
		case R.id.btn_refresh:
			RefreshDeviceState();
			break;
		}
	}
	private void GetDeviceState() {
		//WebServiceUtils.GetDeviceState(mContext, _GetDeviceState, String.valueOf(AppData.GetInstance(this).getSelectDeviceId()), mContext);
		WebService ws = new WebService(mContext, _GetDeviceState,true, "GetDeviceState");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private void RefreshDeviceState() {
		//WebServiceUtils.GetDeviceState(mContext, _GetDeviceState, String.valueOf(AppData.GetInstance(this).getSelectDeviceId()), mContext);
		WebService ws = new WebService(mContext, _RefreshDeviceState,true, "RefreshDeviceState");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _GetDeviceState = 0;
	private final int _RefreshDeviceState = 1;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _GetDeviceState) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					//MToast.makeText(jsonObject.getString("Message")).show();
					//mWatchStateModel.setDeviceId(jsonObject.getInt("DeviceID"));
					mWatchStateModel.setAltitude(jsonObject.getDouble("Altitude"));
					mWatchStateModel.setLatitude(jsonObject.getDouble("Latitude"));
					mWatchStateModel.setLongitude(jsonObject.getDouble("Longitude"));
					mWatchStateModel.setCourse(jsonObject.getString("Course"));
					mWatchStateModel.setElectricity(jsonObject.getString("Electricity"));
					mWatchStateModel.setOnline(jsonObject.getString("Online"));
					mWatchStateModel.setSpeed(jsonObject.getString("Speed"));
					mWatchStateModel.setSatelliteNumber(jsonObject.getString("SatelliteNumber"));
					mWatchStateModel.setSocketId(jsonObject.getString("SocketId"));
					mWatchStateModel.setCreateTime(jsonObject.getString("CreateTime"));
					mWatchStateModel.setServerTime(jsonObject.getString("ServerTime"));
					mWatchStateModel.setUpdateTime(jsonObject.getString("UpdateTime"));
					mWatchStateModel.setDeviceTime(jsonObject.getString("DeviceTime"));
					mWatchStateModel.setLocationType(jsonObject.getString("LocationType"));
					/*mWatchStateModel.setLBS(jsonObject.getString("LBS"));
					mWatchStateModel.setGSM(jsonObject.getString("GSM"));
					mWatchStateModel.setWifi(jsonObject.getString("Wifi"));*/

					WatchStateDao mWatchStateDao = new WatchStateDao(this);
					mWatchStateDao.saveWatchState(mWatchStateModel);
					
					initMarker(false);
				} else if (code == -2) {
					// -2系统异常
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -3) {
					// -3无权操作设备
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -1){
					// -1设备参数错误
				} else if (code == 0){
					// 0登录异常
				}
				/*finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
			}else if(id == _RefreshDeviceState){
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					mMainFrameTask = new MainFrameTask(this);
				    mMainFrameTask.execute();
				}else{
					MToast.makeText(R.string.send_order_fail).show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private BroadcastReceiver ChangeStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//abortBroadcast();
			mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
			initView();
			initMarker(false);
			stopProgressDialog();
		}
	};
	private MProgressDialog mProgressDialog = null;
	private void startProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = MProgressDialog.createDialog(this);
			mProgressDialog.setMessage(getResources().getString(
					R.string.load_location));
			mProgressDialog.setCancelable(true);
		}
		mProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	private MainFrameTask mMainFrameTask = null;

	public class MainFrameTask extends AsyncTask<Integer, String, Integer> {
		private WatchLocation mWatchLocation = null;

		public MainFrameTask(WatchLocation mWatchLocation) {
			this.mWatchLocation = mWatchLocation;
		}

		@Override
		protected void onCancelled() {
			stopProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				Thread.sleep(30*1000);
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
		}

	}
	 /*定义一个倒计时的内部类*/  
    class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);   
            btn_refresh.setClickable(false);
            btn_refresh.setFocusable(false);
            btn_refresh.setBackgroundResource(R.drawable.refresh_location_unuse);
        }     
        @Override     
        public void onFinish() {     
        	btn_refresh.setClickable(true);
        	btn_refresh.setFocusable(true);
        	btn_refresh.setBackgroundResource(R.drawable.refresh_location);
        }     
        @Override     
        public void onTick(long millisUntilFinished) {     
        	btn_refresh.setText( String.valueOf(millisUntilFinished / 1000) );  
        	if(millisUntilFinished / 1000 == 1){
        		btn_refresh.setText("");  
        	}
            //Toast.makeText(mContext, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延迟       
        }    
    }     
	// ------------------------------------------------------地图相关---------------------------------------------------------------//
	/**
	 * 初始化AMap对象
	 */

	private void aMapInit() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		// 对amap添加单击地图事件监听器
		/*aMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				if (aMap != null) {
					aMap.clear();
				}
				mMarker.remove();
				circle.remove();
				addMarker(latLng);
				mLatLng = latLng;
			}
		});*/
		aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if(marker.equals(mMarker)){
					
				}
				return false;
			}
		});
		initMarker(true);
	}
	private void initMarker(boolean isMove){
		aMap.clear();
		if((mWatchModel.getHomeLat() != 0) && (mWatchModel.getHomeLng() != 0)){
			addMarker(new LatLng(mWatchModel.getHomeLat(),mWatchModel.getHomeLng()), R.drawable.location_home, false);
		}
		if((mWatchModel.getSchoolLat() != 0) && (mWatchModel.getSchoolLng() != 0)){
			addMarker(new LatLng(mWatchModel.getSchoolLat(),mWatchModel.getSchoolLng()), R.drawable.location_school, false);
		}
		if(mWatchStateModel.getLatitude() != 0 && mWatchStateModel.getLongitude() != 0){
			addMarker(new LatLng(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude()), R.drawable.location_watch, true);
			if(isMove)
				aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude()), 18, 0, 0)), 1000, null);
		}
		/*if((mWatchStateModel.getLatitude() != 0) && (mWatchStateModel.getLongitude() != 0)){
		}*/
	}
	private void addMarker(LatLng latLng, int MarkerIndex, boolean isAddress){
		if(isAddress){
			getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
		}
		markerOption = new MarkerOptions();
		//markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.fromResource(MarkerIndex));
		mMarker = aMap.addMarker(markerOption);
		mMarker.setPosition(latLng);
		jumpPoint(mMarker, latLng);
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
	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_me));// 设置小蓝点的图标
		myLocationStyle.strokeColor(R.color.transparent);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
		//myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setMyLocationButtonEnabled(false);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

		// aMap.setMyLocationType()
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}
	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker, final LatLng latLng) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		Point startPoint = proj.toScreenLocation(latLng);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * latLng.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * latLng.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				aMap.reloadMap();// 刷新地图
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
					&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
				if(TextUtils.isEmpty(mWatchStateModel.getDeviceTime())){
					tv_adress.setText(result.getRegeocodeAddress().getFormatAddress() + "附近");
				}else{
					tv_adress.setText(result.getRegeocodeAddress().getFormatAddress() + "附近" +DateConversion.TimeChange(mWatchStateModel.getDeviceTime(), null) );
				}
				mAddress = result.getRegeocodeAddress().getFormatAddress() + "附近";
			} else {
				System.out.println("暂无结果");
				//暂无结果
			}
		} else if (rCode == 27) {
			//网络错误
		} else if (rCode == 32) {
			//key错误
		} else {
			//其他错误
		}
	}

	/**
	 * 定位成功后回调函数
	 */
	private boolean firstLoad = true;
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		
		if (mListener != null && aLocation != null) {
			//mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			
			if(firstLoad){
				/*aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(aLocation.getLatitude(),aLocation.getLongitude()), 18, 0, 30)), 1000, null);*/
				if(mWatchStateModel.getLatitude() == 0 && mWatchStateModel.getLongitude() == 0){
					aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
							new LatLng(aLocation.getLatitude(),aLocation.getLongitude()), 18, 0, 0)), 1000, new AMap.CancelableCallback() {
								@Override
								public void onFinish() {
									firstLoad = false;
								}
								@Override
								public void onCancel() {

								}
							});
				}
				
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
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
	 * 停止定位
	 */
	@Override
	public void deactivate() {
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
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
}
