package vip.inteltech.gat;

import java.util.*;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.json.JSONArray;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import vip.inteltech.calendar.CalendarPickerView;
import vip.inteltech.calendar.CalendarPickerView.SelectionMode;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.maputil.GaoDeMapUtil;
import vip.inteltech.gat.maputil.GoogleMapUtil;
import vip.inteltech.gat.maputil.MapUtil;
import vip.inteltech.gat.model.HistoryPointModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class HistoryTrack extends BaseFragmentActivity implements OnClickListener, LocationSource, AMapLocationListener, OnGeocodeSearchListener, WebServiceListener, OnMapReadyCallback {
	private HistoryTrack mContext;
	private TextView tv_time;
	private EditText et_content;
	private CheckBox cb_play,cb_follow,cb_lbs;
	private ProgressBar pb_play;
	private SeekBar sb_speed;
	private LinearLayout ll_bottom;
	private List<HistoryPointModel> pointList,pointList_t;
	private String select_date;
	private Thread playThread;
	private WatchStateModel mWatchStateModel;
	private WatchModel mWatchModel;
	
	private boolean isRun = false;
	// 地图相关
	private AMap mGaoDeMap;
	private GoogleMap mGoogleMap;
	private SupportMapFragment mGaoDeMapFragment;
	private com.google.android.gms.maps.SupportMapFragment mGoogleMapFragment;
	private int mMapSelect;
	
	private MapUtil mMapUtil;
	private GaoDeMapUtil mGaoDeMapUtil;
	private GoogleMapUtil mGoogleMapUtil;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private MarkerOptions markerOption;
	private Marker mMarker, startMarker;
	private GeocodeSearch geocoderSearch;
	
	//地图相关
	
	private Handler mhandler = new Handler() { // 更新UI的handler
		@Override
		public void handleMessage(Message msg) {
			try {
				if(cb_play.isChecked())
				{
					if(pb_play.getProgress()<pb_play.getMax()){
						pb_play.setProgress(pb_play.getProgress()+1);
						LatLng latLng_a = new LatLng(pointList.get(pb_play.getProgress()).getLatitude(), pointList.get(pb_play.getProgress()).getLongitude());
						LatLng latLng_b = new LatLng(pointList.get(pb_play.getProgress()-1).getLatitude(), pointList.get(pb_play.getProgress()-1).getLongitude());
						String str = mWatchModel.getName() + "\n" + getLocationType(pointList.get(pb_play.getProgress()).getLocationType()) + "\n"
								+ DateConversion.TimeChange(DateConversion.converTimes(pointList.get(pb_play.getProgress()).getUpdateTime()),"");
						//drawLine(latLng_a, latLng_b);
						mMapUtil.drawLine(latLng_a.latitude, latLng_a.longitude, latLng_b.latitude, latLng_b.longitude, str, cb_follow.isChecked());
						tv_time.setText(pointList.get(pb_play.getProgress()).getUpdateTime());
					}else{
						cb_play.setChecked(false);
						cb_play.setClickable(false);
						cb_play.setButtonDrawable(R.drawable.noplay);
					}
					//isPlay = false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.history_track);
		mContext = this;
		mMapSelect = AppData.GetInstance(this).getMapSelect();
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_amplification).setOnClickListener(this);
		findViewById(R.id.btn_shrink).setOnClickListener(this);
		findViewById(R.id.btn_replay).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);

		tv_time = (TextView) findViewById(R.id.tv_time);
		pb_play = (ProgressBar) findViewById(R.id.pb_play);
		sb_speed = (SeekBar) findViewById(R.id.sb_speed);
		cb_play = (CheckBox) findViewById(R.id.cb_play);
		cb_follow = (CheckBox) findViewById(R.id.cb_follow);
		cb_lbs = (CheckBox) findViewById(R.id.cb_lbs);
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);

		mGaoDeMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.GaoDeMap);
		//GoogleMap member aquire in the function---OnMapReady
		mGoogleMapFragment = ((com.google.android.gms.maps.SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.GoogleMap));
		mGoogleMapFragment.getMapAsync(this); 
		
		if(mMapSelect == 1)
		{
			mGaoDeMap = mGaoDeMapFragment.getMap();	
			mGaoDeMapUtil = new GaoDeMapUtil(mGaoDeMap);
			mMapUtil = new MapUtil(mGaoDeMapUtil);
			mMapUtil.initMap();
			mGaoDeMap.setLocationSource(this);// 设置定位监听
			GaoDeMapInfoWindowInit();
			initData();
		}
		
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
				
		//hide one map of the two
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if(mMapSelect == 1)
		{
			transaction.hide(mGoogleMapFragment);
		}
		else{
			transaction.hide(mGaoDeMapFragment);
		}
		transaction.commit();

		Log.v("kkk", "222");
		initMoment();
		Log.v("kkk", "333");
		initComponent();
		Log.v("kkk", "444");
		
	}
	
	
	
	private void initComponent(){
		sb_speed.setMax(100);
		sb_speed.setProgress(50);
		cb_play.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					if(!isRun){
						playThread.start();
						isRun = true;
					}
				}else{
					
				}
			}
		});
		cb_lbs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				pb_play.setProgress(0);
				filterLbs();
				pb_play.setMax(pointList.size()-1);
				if (cb_play.isChecked()) {
					cb_play.setChecked(true);
					cb_play.setClickable(true);
					cb_play.setButtonDrawable(R.drawable.cb_play);
				}
				//mGaoDeMap.clear();
				mMapUtil.clear();
				//addStartMarker(new LatLng(pointList.get(0).getLatitude(), pointList.get(0).getLongitude()));
				mMapUtil.addStartMarker(pointList.get(0).getLatitude(), pointList.get(0).getLongitude(), R.drawable.start_point, false);
				//addHistoryPoint();
				mMapUtil.addHistoryPoint(R.drawable.history_point, pointList);
			}
		});
		
		playThread=new Thread(new Runnable() {
			public void run() {
				while(true)
				{
					try {
						mhandler.sendEmptyMessage(0);
						//Thread.sleep((long) (Math.pow(2, ((100.0 -  sb_speed.getProgress()) / 100.0 * 5.0)) * 20.0));
						Thread.sleep(((100 - sb_speed.getProgress()) + 10) * 20);
						//System.out.println(((100-sb_speed.getProgress())+10)*20 + " Math");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			}
		});

	}
	
	private void GaoDeMapInfoWindowInit(){
		mGaoDeMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
				TextView tv_contents = ((TextView) infoWindow.findViewById(R.id.tv_contents));
				tv_contents.setText(marker.getTitle());
				return infoWindow;
			}
			
			@Override
			public View getInfoContents(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
	
	private void GoogleMapInfoWindowInit(){
		
		mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
				// TODO Auto-generated method stub
				View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
				TextView tv_contents = ((TextView) infoWindow.findViewById(R.id.tv_contents));
				tv_contents.setText(marker.getTitle());
				return infoWindow;
			}
			
			@Override
			public View getInfoContents(com.google.android.gms.maps.model.Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	private void initData() {
		mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
		mWatchModel = AppContext.getInstance().getmWatchModel();
	//	aMapInit();
		if(mWatchStateModel != null){
			if(mWatchStateModel.getLatitude() != 0 && mWatchStateModel.getLongitude() != 0){
/*				mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude()), 18, 0, 30)), 1000, null);
				mGaoDeMap.clear();*/
				mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), 18, 0, 0, 1000, null);
				mMapUtil.clear();
			}
		//	addMarker(new LatLng(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude()), R.drawable.location_watch, false, true);
			mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, false, false);
		}
		if (!AppContext.getInstance().isSupportGps() && !AppContext.getInstance().isSupportWifi()) {
			findViewById(R.id.cb_lbs).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			CalendarDialog();
			break;
		case R.id.btn_amplification:
			//mGaoDeMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
			mMapUtil.animateCam(true, 1000, null);
			
			
			break;
		case R.id.btn_shrink:
		//	mGaoDeMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
			mMapUtil.animateCam(false, 1000, null);
			break;
		case R.id.btn_replay:
			pb_play.setProgress(0);
			cb_play.setChecked(true);
			cb_play.setClickable(true);
			cb_play.setButtonDrawable(R.drawable.cb_play);
			//mGaoDeMap.clear();
			mMapUtil.clear();
			mMapUtil.addStartMarker(pointList.get(0).getLatitude(), pointList.get(0).getLongitude(), R.drawable.start_point, false);
			//addStartMarker(new LatLng(pointList.get(0).getLatitude(), pointList.get(0).getLongitude()));
			//addHistoryPoint();
			mMapUtil.addHistoryPoint(R.drawable.history_point, pointList);
			break;
		case R.id.btn_finish:
			finishPlay();
			break;
		}
	}

	private Dialog dialog;
	private void CalendarDialog(){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_calendar, null);
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
		final Calendar nextDay = Calendar.getInstance();
		nextDay.add(Calendar.DATE, 1);
	    final Calendar lastYear = Calendar.getInstance();
	    lastYear.add(Calendar.YEAR, -1);
		final CalendarPickerView calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
		calendar.init(lastYear.getTime(), nextDay.getTime()).inMode(SelectionMode.SINGLE).withSelectedDate(new Date());
		select_date = "";

		Button btn_OK, btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//MToast.makeText(DateConversion.DateConversionUtilC(calendar.getSelectedDate())).show();
				GetDevicesHistory(DateConversion.DateConversionUtilC(calendar.getSelectedDate()));
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
	private void finishPlay(){
		pb_play.setProgress(0);
		cb_play.setChecked(false);
		cb_play.setClickable(true);
		cb_play.setButtonDrawable(R.drawable.cb_play);
/*		mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude()),  mGaoDeMap.getCameraPosition().zoom, 0, 30)), 1000, null);
		mGaoDeMap.clear();*/
		mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), mMapUtil.getZoom(), 0, 0, 1000, null);
		mMapUtil.clear();
	//	addMarker(new LatLng(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude()), R.drawable.location_watch, false, true);
		mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, false, false);
		
		ll_bottom.setVisibility(View.GONE);
	}
	Calendar c_a,c_b ;

	private void initMoment(){
		c_a = Calendar.getInstance();
		c_b = Calendar.getInstance();
		c_b.add(Calendar.DATE, -1);
	}

	private void GetDevicesHistory(String date){
		WebService ws = new WebService(mContext, _GetDeviceHistory, true, "GetDeviceHistory");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
		property.add(new WebServiceProperty("startTime", date + " 00:00"));
		property.add(new WebServiceProperty("endTime", date + " 23:59"));
		property.add(new WebServiceProperty("pageIndex", "0"));
		property.add(new WebServiceProperty("pageSize", "10000"));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
		select_date = date;
	}
	private final int _GetDeviceHistory = 0;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(id == _GetDeviceHistory){
				int Code = jsonObject.getInt("Code");
				if(Code == 1){
					pointList = new ArrayList<HistoryPointModel>();
					pointList_t = new ArrayList<HistoryPointModel>();
					JSONArray array = jsonObject.getJSONArray("List");
					for(int i = 0; i < array.length(); i++){
						JSONObject item = array.getJSONObject(i);
						HistoryPointModel mHistoryPointModel = new HistoryPointModel();
						mHistoryPointModel.setTime(item.getString("Time"));
						mHistoryPointModel.setLatitude(item.getDouble("Latitude"));
						mHistoryPointModel.setLongitude(item.getDouble("Longitude"));
						mHistoryPointModel.setLocationType(item.getString("LocationType"));
						mHistoryPointModel.setCreateTime(item.getString("CreateTime"));
						mHistoryPointModel.setUpdateTime(item.getString("UpdateTime"));
						if (mHistoryPointModel.getCreateTime().indexOf(select_date) != -1) {
							pointList_t.add(mHistoryPointModel);
						}
					}
					correctTrack();
					filterLbs();
					pb_play.setMax(pointList.size()-1);
					ll_bottom.setVisibility(View.VISIBLE);
					pb_play.setProgress(0);
					cb_play.setChecked(false);
					cb_play.setClickable(true);
					cb_play.setButtonDrawable(R.drawable.cb_play);
					//mGaoDeMap.clear();
					mMapUtil.clear();
					mMapUtil.addStartMarker(pointList.get(0).getLatitude(), pointList.get(0).getLongitude(), R.drawable.start_point, false);
					//addStartMarker(new LatLng(pointList.get(0).getLatitude(), pointList.get(0).getLongitude()));
					//addHistoryPoint();
					mMapUtil.addHistoryPoint(R.drawable.history_point, pointList);
				}else{
					MToast.makeText(R.string.no_history_track).show();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------地图相关---------------------------------------------------------------//
	/**
	 * 初始化AMap对象
	 */
/*	private boolean zoomIndex = true;

	private void aMapInit() {
		if (mGaoDeMap == null) {
		//	aMap = mapView.getMap();
			setUpMap();
		}
		mGaoDeMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker mMarker) {
				// TODO Auto-generated method stub
				View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
				TextView tv_contents = ((TextView) infoWindow.findViewById(R.id.tv_contents));
				tv_contents.setText(mMarker.getTitle());
				return infoWindow;
			}

			@Override
			public View getInfoContents(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}*/


	private void addStartMarker(LatLng latLng){
/*		mGaoDeMap.clear();
		mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 0, 30)), 1000, null);*/
		mMapUtil.clear();
		mMapUtil.animateCam(latLng.latitude, latLng.longitude, 18, 0, 0, 1000, null);
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point));
		startMarker = mGaoDeMap.addMarker(markerOption);
		startMarker.setPosition(latLng);
		jumpPoint(startMarker, latLng);
	}
	
	private void addHistoryPoint(){
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.history_point));
		for(int i = 0;i < pointList.size();i++){
			if(i > 0){
				float f = AMapUtils.calculateLineDistance(new LatLng(pointList.get(i).getLatitude(), pointList.get(i).getLongitude()), new LatLng(pointList.get(i).getLatitude(), pointList.get(i-1).getLongitude()));
				mGaoDeMap.addMarker(markerOption).setPosition(new LatLng(pointList.get(i).getLatitude()-0.00001d, pointList.get(i).getLongitude()));
				if(f > 50){
				}
			}else{
				//aMap.addMarker(markerOption).setPosition(new LatLng(pointList.get(i).getLatitude(), pointList.get(i).getLongitude()));
			}
		}
	}
	private void drawLine(LatLng latLng_a, LatLng latLng_b){
		mGaoDeMap.addPolyline((new PolylineOptions()).add(latLng_a, latLng_b).color(Color.RED)).setGeodesic(true);
		//aMap.clear();
		mMarker.remove();
		//mMarker = new Marker(null);
		addMarker(latLng_a, R.drawable.location_watch, false, false);
		String str = mWatchModel.getName() + "\n" + getLocationType(pointList.get(pb_play.getProgress()).getLocationType()) + "\n"
		+ DateConversion.TimeChange(DateConversion.converTimes(pointList.get(pb_play.getProgress()).getUpdateTime()),"");
		mMarker.setTitle(str);
		mMarker.showInfoWindow();
		if(cb_follow.isChecked()){
			//mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng_a, mGaoDeMap.getCameraPosition().zoom, 0, 30)), 1000, null);
			mMapUtil.animateCam(latLng_a.latitude, latLng_a.longitude, mMapUtil.getZoom(), 0, 0, 1000, null);
		}
	}
	private String getLocationType(String type){
		WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
		if(type.equals("1") || 
				mWatchModel.getCurrentFirmware().indexOf("D9_CHUANGMT_V") != -1 || 
				mWatchModel.getCurrentFirmware().indexOf("D10_CHUANGMT_V") != -1 || 
				mWatchModel.getCurrentFirmware().indexOf("D9_TP_CHUANGMT_V") != -1){
			return "GPS";
		}else if(type.equals("2")){
			return "LBS";
		}else {
			return "WiFi";
		}
	}
	private void addMarker(LatLng latLng, int MarkerIndex, boolean isAddress, boolean isJump) {
		if (isAddress) {
			getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
		}
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(MarkerIndex));
		mMarker = mGaoDeMap.addMarker(markerOption);
		mMarker.setPosition(latLng);
		if(isJump)
			jumpPoint(mMarker, latLng);
	}

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
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);//第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		/*MyLocationStyle myLocationStyle = new MyLocationStyle();

		 * myLocationStyle.myLocationIcon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		 myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		// aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
*/		mGaoDeMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		// aMap.getUiSettings().setZoomGesturesEnabled(false);// 禁止通过手势缩放地图
		// aMap.getUiSettings().setScrollGesturesEnabled(false);// 禁止通过手势移动地图
		mGaoDeMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
		// aMap.moveCamera(CameraUpdateFactory.zoomTo(13f));
		if(mWatchStateModel.getLatitude() != 0 && mWatchStateModel.getLongitude() != 0){
			mGaoDeMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		}

		// aMap.setMyLocationType()
		geocoderSearch = new GeocodeSearch(mContext);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}

	/**
	 * 定位成功后回调函数
	 */
	boolean firstLoad = true;
	private LatLng mLatLng;
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mLatLng = new LatLng(aLocation.getLatitude(),aLocation.getLongitude());
			if(mLatLng.latitude != 0 && mLatLng.longitude != 0){
		/*		mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 18, 0, 30)), 1000, null);
				mGaoDeMap.clear();*/
				mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 18, 0, 0, 1000, null);
				mMapUtil.clear();
			//	addMarker(mLatLng, R.drawable.location_watch, false, true);
				mMapUtil.addMarker(mLatLng.latitude, mLatLng.longitude, R.drawable.location_watch, false, false);
				mGaoDeMap.setMyLocationEnabled(false);
			}

			/*
			 * if (firstLoad) {
			 * 
			 * mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			 * aMap.animateCamera(CameraUpdateFactory .newCameraPosition(new
			 * CameraPosition(new LatLng( aLocation.getLatitude(), aLocation
			 * .getLongitude()), 18, 0, 30)), 1000, null); firstLoad = false; }
			 */
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

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null
					&& result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null
					&& !TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress())) {
				// mAddress = result.getRegeocodeAddress().getFormatAddress() +
				// "附近";
			} else {
				// System.out.println(R.string.no_result);
				// 暂无结果
			}
		} else if (rCode == 27) {
			// 网络错误
		} else if (rCode == 32) {
			// key错误
		} else {
			// 其他错误
		}
	}

	// ------------------------------------------------------生命周期---------------------------------------------------------------//
	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		if(playThread!=null)
			playThread.interrupt();
	}

	private void filterTrack() {
		int dst = 0;
		if (!AppContext.getInstance().isSupportGps() && !AppContext.getInstance().isSupportWifi()) {
			dst = 200;
		} else {
			dst = 300;
		}
		for (int i = 1; i < pointList_t.size() - 1; i++) {
			double c = distancePoint(pointList_t.get(i-1).getLatitude(), pointList_t.get(i-1).getLongitude(), pointList_t.get(i).getLatitude(), pointList_t.get(i).getLongitude());
			if (c > dst) {
				int j = i + 1;
				for (; j < pointList_t.size() - 1; j++) {
					if (pointList_t.get(i).getLatitude() != pointList_t.get(j).getLatitude() || pointList_t.get(i).getLongitude() != pointList_t.get(j).getLongitude()) {
						break;
					}
				}
				int count = j - i;
				double b = distancePoint(pointList_t.get(i-1).getLatitude(), pointList_t.get(i-1).getLongitude(), pointList_t.get(j).getLatitude(), pointList_t.get(j).getLongitude());
				if (b > 0) {
					if (c / b >= 2) {
						for (int k = 0; k < count; k++)
						{
							pointList_t.get(i+k).setLatitude((pointList_t.get(i-1).getLatitude() + pointList_t.get(j).getLatitude()) / 2);
							pointList_t.get(i+k).setLongitude((pointList_t.get(i-1).getLongitude() + pointList_t.get(j).getLongitude()) / 2);
						}
					}
				}
				i += count - 1;
			}
		}
	}

	private void correctTrack() {
		if (pointList_t.size() <= 3) {
			return;
		}

		filterTrack();

		if (!AppContext.getInstance().isSupportGps() && !AppContext.getInstance().isSupportWifi()) {
			correctTrack1();
		} else {
			correctTrack2();
		}
	}

	private double distancePoint(double n1, double e1, double n2, double e2) {
		double jl_jd = 102834.74258026089786013677476285;
        double jl_wd = 111712.69150641055729984301412873;
        double b = Math.abs((e1 - e2) * jl_jd);
        double a = Math.abs((n1 - n2) * jl_wd);
        return Math.sqrt((a * a + b * b));
	}

	private void correctTrack1() {
		for (int i = 1; i < pointList_t.size() - 1; i++) {
			double c = distancePoint(pointList_t.get(i-1).getLatitude(), pointList_t.get(i-1).getLongitude(), pointList_t.get(i).getLatitude(), pointList_t.get(i).getLongitude());
			if (c < 800) {
				int j = 0;
				for (j = i + 1; j < pointList_t.size() - 1; j++) {
					double c_1 = distancePoint(pointList_t.get(i-1).getLatitude(), pointList_t.get(i-1).getLongitude(), pointList_t.get(j).getLatitude(), pointList_t.get(j).getLongitude());
					if (c_1 > 800) {
						j++;
						if (j < pointList_t.size() - 1) {
							double c_2 = distancePoint(pointList_t.get(i-1).getLatitude(), pointList_t.get(i-1).getLongitude(), pointList_t.get(j).getLatitude(), pointList_t.get(j).getLongitude());
							if (c_2 > 800) {
								break;
							}
						}
					}
				}
				int n = i - 1;
				double lat = 0;
				double lon = 0;
				for (; n < j; n++) {
					lat += pointList_t.get(n).getLatitude();
					lon += pointList_t.get(n).getLongitude();
				}
				lat /= j - i + 1;
				lon /= j - i + 1;
				for (; i <= j; i++) {
					pointList_t.get(i - 1).setLatitude(lat);
					pointList_t.get(i - 1).setLongitude(lon);
				}
			}
		}
	}

	private void correctTrack2() {
		int count = 0;
		List<HistoryPointModel> pointList_r = new ArrayList<HistoryPointModel>();
		List<HistoryPointModel> pointList_l = new ArrayList<HistoryPointModel>();
		for (int i = 1; i < pointList_t.size() - 1; i++) {
			if (!pointList_t.get(i).getLocationType().equals("2")) {
				pointList_r.add(pointList_t.get(i));
				count = 0;
			} else if (pointList_t.get(i-1).getLatitude() == pointList_t.get(i).getLatitude() && pointList_t.get(i-1).getLongitude() == pointList_t.get(i).getLongitude()) {
				count++;
			} else {
				if (count >= 2) {
					pointList_l.add(pointList_t.get(i));
				}
				count = 0;
			}
		}
		for (int i = 0; i < pointList_t.size(); i++) {
			if (pointList_t.get(i).getLocationType().equals("2")) {
				for (int j = 0; j < pointList_r.size(); j++) {
					double d = distancePoint(pointList_t.get(i).getLatitude(), pointList_t.get(i).getLongitude(), pointList_r.get(j).getLatitude(), pointList_r.get(j).getLongitude());
					if (d <= 400) {
						pointList_t.get(i).setLatitude(pointList_r.get(j).getLatitude());
						pointList_t.get(i).setLongitude(pointList_r.get(j).getLongitude());
						break;
					}
				}
			}
		}
		for (int i = 0; i < pointList_t.size(); i++) {
			if (pointList_t.get(i).getLocationType().equals("2")) {
				for (int j = 0; j < pointList_l.size(); j++) {
					double d = distancePoint(pointList_t.get(i).getLatitude(), pointList_t.get(i).getLongitude(), pointList_l.get(j).getLatitude(), pointList_l.get(j).getLongitude());
					if (d <= 400) {
						pointList_t.get(i).setLatitude(pointList_l.get(j).getLatitude());
						pointList_t.get(i).setLongitude(pointList_l.get(j).getLongitude());
						break;
					}
				}
			}
		}
		for (int i = 0; i < pointList_t.size() - 1; i++) {
			if (pointList_t.get(i).getLocationType().equals("2")) {
				double d = distancePoint(pointList_t.get(i).getLatitude(), pointList_t.get(i).getLongitude(), pointList_t.get(i+1).getLatitude(), pointList_t.get(i+1).getLongitude());
				if (d > 400) {
					if (i >= 1) {
						double d0 = distancePoint(pointList_t.get(i-1).getLatitude(), pointList_t.get(i-1).getLongitude(), pointList_t.get(i+1).getLatitude(), pointList_t.get(i+1).getLongitude());
						if (d0 <= 400) {
							pointList_t.get(i).setLatitude(pointList_t.get(i-1).getLatitude());
							pointList_t.get(i).setLongitude(pointList_t.get(i-1).getLongitude());
						}
					} else if (i + 2 < pointList_t.size()) {
						double d1 = distancePoint(pointList_t.get(i).getLatitude(), pointList_t.get(i).getLongitude(), pointList_t.get(i+2).getLatitude(), pointList_t.get(i+2).getLongitude());
						if (d1 <= 400) {
							pointList_t.get(i+1).setLatitude(pointList_t.get(i).getLatitude());
							pointList_t.get(i+1).setLongitude(pointList_t.get(i).getLongitude());
						}
					}
				} else {
					pointList_t.get(i+1).setLatitude(pointList_t.get(i).getLatitude());
					pointList_t.get(i+1).setLongitude(pointList_t.get(i).getLongitude());
				}
			}
		}
	}

	private void filterLbs() {
		pointList.clear();
		if (!cb_lbs.isChecked()) {
			for (int i = 0; i < pointList_t.size(); i++) {
				if (!pointList_t.get(i).getLocationType().equals("2")) {
					pointList.add(pointList_t.get(i));
				}
			}
			if (pointList.size() == 0) {
				pointList.addAll(pointList_t);
				cb_lbs.setChecked(true);
				MToast.makeText(R.string.filter_lbs_error).show();
			}
		} else {
			pointList.addAll(pointList_t);
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub
		if(mMapSelect == 1){
/*			mGaoDeMap = mGaoDeMapFragment.getMap();	
			mGaoDeMapUtil = new GaoDeMapUtil(mGaoDeMap);
			mGaoDeMapUtil.initMap();
			mMapUtil = new MapUtil(mGaoDeMapUtil);*/
		}
		else {
			mGoogleMap = map;
			mGoogleMapUtil = new GoogleMapUtil(mGoogleMap);
		//	mGoogleMapUtil.initMap();
			mMapUtil = new MapUtil(mGoogleMapUtil);
			mMapUtil.initMap();
		//	mGoogleMap.setLocationSource(this);// 设置定位监听  <<<<<<<<<<<<===================================
			initData();
			GoogleMapInfoWindowInit();
		}
	}
}