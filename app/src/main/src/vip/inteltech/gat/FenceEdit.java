package vip.inteltech.gat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

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
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.GeoFenceDao;
import vip.inteltech.gat.maputil.GaoDeMapUtil;
import vip.inteltech.gat.maputil.GoogleMapUtil;
import vip.inteltech.gat.maputil.MapUtil;
import vip.inteltech.gat.model.GeoFenceModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.VerticalSeekBar;

public class FenceEdit extends BaseFragmentActivity implements OnClickListener,
        LocationSource, AMapLocationListener, OnGeocodeSearchListener,
		WebServiceListener ,TextWatcher,OnSeekBarChangeListener, OnMapReadyCallback{
	private FenceEdit mContext;
	private AutoCompleteTextView et_search;
	private TextView tv_radius;
	private VerticalSeekBar sb;

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
	private UiSettings mUiSettings;
	private int mMapSelect;

	private int position;
	private GeoFenceModel mGeoFenceModel;
	private WatchStateModel mWatchStateModel;
	WatchModel mWatchModel;
	private List<GeoFenceModel> mGeoFenceList;
	
	private MapUtil mMapUtil;
	private GaoDeMapUtil mGaoDeMapUtil;
	private GoogleMapUtil mGoogleMapUtil;
	
	private List<String> listString = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fence_edit);
		mMapSelect = AppData.GetInstance(this).getMapSelect();
		mContext = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_amplification).setOnClickListener(this);
		findViewById(R.id.btn_shrink).setOnClickListener(this);
		findViewById(R.id.btn_submit).setOnClickListener(this);
		findViewById(R.id.btn_clear).setOnClickListener(this);
		findViewById(R.id.btn_search).setOnClickListener(this);
		mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
		initData();

		et_search = (AutoCompleteTextView) findViewById(R.id.et_search);
		et_search.addTextChangedListener(this);
		tv_radius = (TextView) findViewById(R.id.tv_radius);

		sb = (VerticalSeekBar) findViewById(R.id.sb);
		sb.setMax(800);
		sb.setProgress(0);
		sb.setOnSeekBarChangeListener(this);
		
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
			GaoDeMapListenerInit();
			mMapUtil.animateCam(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude(),16,0,0,1000,null);
			mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, true, true);
			
			Intent intent = getIntent();
			position = intent.getIntExtra("position", -1);
			if (position != -1) {
				initGeoFenceData();
				mGeoFenceModel = mGeoFenceList.get(position);
				centerLatLng = new LatLng(mGeoFenceModel.getLat(),mGeoFenceModel.getLng());
			//	mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(centerLatLng, 16, 0,30)), 1000, null);
				mMapUtil.animateCam(centerLatLng.latitude, centerLatLng.longitude, 16, 0, 0, 1000, null);
				sb.setVisibility(View.VISIBLE);
				sb.setProgress(mGeoFenceModel.getRadius() - 500);
				createFence();
				isEditLatLng = true;
			}
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



	}
	
	private void GaoDeMapListenerInit(){
		mGaoDeMap.setOnMapClickListener(new AMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				// TODO Auto-generated method stub
				if (centerLatLng == null) {
					sb.setVisibility(View.VISIBLE);
					tv_radius.setText(getResources().getString(R.string.radius) + getResources().getString(R.string.mh) + "500" + getResources().getString(R.string.m));
					centerLatLng = latLng;
					createFence();
					isEditLatLng = true;
				} else {
					return;
				}
			}
		});
	}

	private void initGeoFenceData() {
		mGeoFenceList = AppContext.getInstance().getmGeoFenceList();
	}


	private void initData() {
		mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_amplification:
			//mGaoDeMap.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
			mMapUtil.animateCam(true, 1000, null);
			break;
		case R.id.btn_shrink:
			//mGaoDeMap.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
			mMapUtil.animateCam(false, 1000, null);
			break;
		case R.id.btn_submit:
			if (centerLatLng == null) {
				return;
			}
			editFenceDialog();
			break;
		case R.id.btn_clear:
			mMapUtil.clear();
			sb.setVisibility(View.INVISIBLE);
			centerLatLng = null;
			sb.setProgress(0);
			tv_radius.setText(R.string.electronic_fence_PS);

			/*
			 * if(mWatchStateModel.getLatitude() != 0 &&
			 * mWatchStateModel.getLongitude() != 0) addMarker( new
			 * LatLng(mWatchStateModel.getLatitude(),
			 * mWatchStateModel.getLongitude()), R.drawable.location_watch,
			 * false, false); else{ }
			 */
			if (mLatLng != null)
			{
				//addMarker(mLatLng, R.drawable.location_watch, false, false);
				mMapUtil.addMarker(mLatLng.latitude, mLatLng.longitude, R.drawable.location_watch, false, false);
			}
			latLngList = new ArrayList<LatLng>();
			isEditLatLng = true;
			break;
		case R.id.btn_search:
			String keyWord = et_search.getText().toString().trim();
			if (TextUtils.isEmpty(keyWord))
				return;
			Intent intent_c = new Intent(mContext, SearchResult.class);
			intent_c.putExtra("keyWord", keyWord);
			intent_c.putExtra("latitude", mLatLng.latitude);
			intent_c.putExtra("longitude", mLatLng.longitude);
			startActivityForResult(intent_c, REARCH);
			break;
		}
	}

	private Dialog dialog;

	private void editFenceDialog() {
		if (dialog != null)
			dialog.cancel();
		View view = mContext.getLayoutInflater().inflate(R.layout.dialog_fence_edit, null);
		dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		// window.setWindowAnimations(R.style.slide_up_down);
		/*
		 * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
		 * getWindowManager().getDefaultDisplay().getHeight()/2;
		 */
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv = (TextView) view.findViewById(R.id.tv_title);
		final EditText et = (EditText) view.findViewById(R.id.et);
		final CheckBox cb_a, cb_b, cb_c;
		cb_a = (CheckBox) view.findViewById(R.id.cb_a);
		cb_b = (CheckBox) view.findViewById(R.id.cb_b);
		cb_c = (CheckBox) view.findViewById(R.id.cb_c);
		if (position != -1) {
			et.setText(mGeoFenceList.get(position).getFenceName());
			cb_a.setChecked(mGeoFenceList.get(position).getEntry().equals("1") ? true : false);
			cb_b.setChecked(mGeoFenceList.get(position).getExit().equals("1") ? true : false);
			cb_c.setChecked(mGeoFenceList.get(position).getEnable().equals("1") ? true : false);
		}
		Button btn_cancel, btn_OK;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String fenceName = et.getText().toString().trim();
				if (TextUtils.isEmpty(fenceName)) {
					return;
				}
				if (position != -1) {
					SaveGeoFence(mGeoFenceList.get(position).getGeofenceID(),
							fenceName, cb_a.isChecked() ? "1" : "0", cb_b.isChecked() ? "1" : "0", cb_c.isChecked() ? "1" : "0");
				} else {
					SaveGeoFence("0", fenceName, cb_a.isChecked() ? "1" : "0", cb_b.isChecked() ? "1" : "0", cb_c.isChecked() ? "1" : "0");
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

	private boolean isEditFenceMame = false, isEditEntry = false, isEditExit = false, isEditLatLng = false;
	private String fenceName, entry, exit, latAndLng;

	private void GetGeoFenceList() {
		WebService ws = new WebService(mContext, _GetGeoFenceList, true, "GetGeoFenceList");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}

	private void SaveGeoFence(String geoFenceId, String fenceName, String entry, String exit, String enable) {
		WebService ws = new WebService(mContext, _SaveGeoFence, true, "SaveGeoFence");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		if (!geoFenceId.equals("0")) {
			property.add(new WebServiceProperty("geoFenceId", geoFenceId));
		}
		property.add(new WebServiceProperty("fenceName", fenceName));
		property.add(new WebServiceProperty("entry", entry));
		property.add(new WebServiceProperty("exit", exit));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		String latAndLng = "";
		latAndLng = centerLatLng.latitude + "," + centerLatLng.longitude + "-" + (sb.getProgress() + 500);
		property.add(new WebServiceProperty("latAndLng", latAndLng));
		property.add(new WebServiceProperty("enable", enable));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}

	private final int _SaveGeoFence = 0;
	private final int _GetGeoFenceList = 1;
	private final int _UpdateGeoFence = 2;

	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _SaveGeoFence) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					MToast.makeText(R.string.save_suc).show();
					setResult(RESULT_OK);
					finish();
					// GetGeoFenceList();
				} else {
					MToast.makeText(R.string.save_fail).show();
				}
			} else if (id == _GetGeoFenceList) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					mGeoFenceList = new ArrayList<GeoFenceModel>();
					JSONArray arr = jsonObject.getJSONArray("GeoFenceList");
					for (int j = 0; j < arr.length(); j++) {
						JSONObject item = arr.getJSONObject(j);
						GeoFenceModel mGeoFenceModel = new GeoFenceModel();
						mGeoFenceModel.setDeviceId(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
						mGeoFenceModel.setGeofenceID(item.getString("GeofenceID"));
						mGeoFenceModel.setEntry(item.getString("Entry"));
						mGeoFenceModel.setExit(item.getString("Exit"));
						mGeoFenceModel.setCreateTime(item.getString("CreateTime"));
						mGeoFenceModel.setUpdateTime(item.getString("UpdateTime"));
						mGeoFenceModel.setEnable(item.getString("Enable"));
						mGeoFenceModel.setDescription(item.getString("Description"));
						mGeoFenceModel.setLat(item.getDouble("Lat"));
						mGeoFenceModel.setLng(item.getDouble("Lng"));
						mGeoFenceModel.setRadius(item.getInt("Radii"));
						mGeoFenceList.add(mGeoFenceModel);
					}
					AppContext.getInstance().setmGeoFenceList(mGeoFenceList);
					finish();
				} else {
					//MToast.makeText(jsonObject.getString("Message")).show();
				}
			} else if (id == _UpdateGeoFence) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {

					GeoFenceDao mGeoFenceDao = new GeoFenceDao(mContext);
					ContentValues values = new ContentValues();
					if (isEditFenceMame) {
						values.put(GeoFenceDao.COLUMN_NAME_FENCENAME, fenceName);
						mGeoFenceList.get(position).setFenceName(fenceName);
					}
					if (isEditEntry) {
						values.put(GeoFenceDao.COLUMN_NAME_ENTRY, entry);
						mGeoFenceList.get(position).setEntry(entry);
					}
					if (isEditExit) {
						values.put(GeoFenceDao.COLUMN_NAME_EXIT, exit);
						mGeoFenceList.get(position).setExit(exit);
					}
					mGeoFenceDao.updateGeoFence(mGeoFenceList.get(position)
							.getGeofenceID(), values);
					MToast.makeText(R.string.edit_suc).show();
					finish();

				} else {
					MToast.makeText(R.string.edit_fail).show();
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

/*	private void aMapInit() {
		if (mGaoDeMap == null) {
			mUiSettings = mGaoDeMap.getUiSettings();
			setUpMap();
		}
		mGaoDeMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				// TODO Auto-generated method stub
				if (centerLatLng == null) {
					sb.setVisibility(View.VISIBLE);
					tv_radius.setText(getResources().getString(R.string.radius) + getResources().getString(R.string.mh) + "500" + getResources().getString(R.string.m));
					centerLatLng = latLng;
					createFence();
					isEditLatLng = true;
				} else {
					return;
				}
			}
		});
	}*/

	private List<LatLng> latLngList = new ArrayList<LatLng>();

	private LatLng centerLatLng = null;

	private void createFence() {
	//	mMapUtil.clear();
		/*
		 * if(mWatchStateModel.getLatitude() != 0 &&
		 * mWatchStateModel.getLongitude() != 0) addMarker(new
		 * LatLng(mWatchStateModel.getLatitude(),
		 * mWatchStateModel.getLongitude()), R.drawable.location_watch, false,
		 * false);
		 */
		if (mLatLng != null){
		//	addMarker(mLatLng, R.drawable.location_watch, false, false);
			mMapUtil.addMarker(mLatLng.latitude, mLatLng.longitude, R.drawable.location_watch, false, false);
		}
		if(centerLatLng!=null){
			mMapUtil.addCircle(centerLatLng.latitude, centerLatLng.longitude, 0,
					sb.getProgress() + 500, mContext.getResources().getColor(R.color.reds),
					mContext.getResources().getColor(R.color.t_grey),10,false, false);
		}
	}


/*	private void addMarker(LatLng latLng, int MarkerIndex, boolean isAddress, boolean isJump) {
		if (isAddress) {
			getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
		}
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(MarkerIndex));
		mMarker = mGaoDeMap.addMarker(markerOption);
		mMarker.setPosition(latLng);
		if (isJump)
			jumpPoint(mMarker, latLng);
	}*/

	/**
	 * marker点击时跳动一下
	 */
/*	public void jumpPoint(final Marker marker, final LatLng latLng) {
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
				float t = interpolator.getInterpolation((float) elapsed/ duration);
				double lng = t * latLng.longitude + (1 - t)* startLatLng.longitude;
				double lat = t * latLng.latitude + (1 - t)* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				mGaoDeMap.moveCamera(CameraUpdateFactory.zoomTo(16));
				mGaoDeMap.invalidate();// 刷新地图
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});

	}*/

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 设置一些amap的属性
	 */
/*	private void setUpMap() {
		// 自定义系统定位小蓝点
		
		 * MyLocationStyle myLocationStyle = new MyLocationStyle();
		 * myLocationStyle.myLocationIcon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		 * myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		 * myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));//
		 * 设置圆形的填充颜色 // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		 * myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细 //
		 * aMap.setMyLocationStyle(myLocationStyle);
		 
		mGaoDeMap.setLocationSource(this);// 设置定位监听
		mGaoDeMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		// aMap.getUiSettings().setZoomGesturesEnabled(false);// 禁止通过手势缩放地图
		// aMap.getUiSettings().setScrollGesturesEnabled(false);// 禁止通过手势移动地图
		mGaoDeMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
		// aMap.moveCamera(CameraUpdateFactory.zoomTo(13f));
		mGaoDeMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		mUiSettings.setScaleControlsEnabled(true);
		// aMap.setMyLocationType()
		geocoderSearch = new GeocodeSearch(mContext);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}*/


	/**
	 * 定位成功后回调函数
	 */
	boolean firstLoad = true;
	LatLng mLatLng = null;

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
	//	Log.v("kkk", "onLocationChangeddd");
		if (/*mListener != null &&*/ aLocation != null) {
			if (firstLoad) {
				mLatLng = new LatLng(aLocation.getLatitude(),
						aLocation.getLongitude());
				if (mWatchStateModel.getLatitude() == 0
						&& mWatchStateModel.getLongitude() == 0) {
					if (mLatLng.latitude != 0 && mLatLng.latitude != 0) {
						if (position == -1) {
						/*	mGaoDeMap.animateCamera(CameraUpdateFactory
									.newCameraPosition(new CameraPosition(
											mLatLng, 18, 0, 30)), 1000, null);*/
							mMapUtil.animateCam(mLatLng.latitude, mLatLng.longitude, 16, 0, 0, 1000, null);
						}
					}

				} else {
					/*
					 * aMap.clear(); if(mWatchStateModel.getLatitude() == 0 &&
					 * mWatchStateModel.getLongitude() == 0){ if(mLatLng !=
					 * null) aMap.animateCamera(CameraUpdateFactory
					 * .newCameraPosition(new CameraPosition(mLatLng, 18, 0,
					 * 30)), 1000, null); }else{ }
					 */
					mLatLng = new LatLng(mWatchStateModel.getLatitude(),
							mWatchStateModel.getLongitude());
					if (position == -1) {
			/*			mGaoDeMap.animateCamera(
								CameraUpdateFactory.newCameraPosition(new CameraPosition(
										new LatLng(mWatchStateModel
												.getLatitude(),
												mWatchStateModel.getLongitude()),
										18, 0, 30)), 1000, null);*/
						mMapUtil.animateCam(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), 16, 0, 0, 1000, null);
					}
/*					addMarker(new LatLng(mWatchStateModel.getLatitude(),
							mWatchStateModel.getLongitude()),
							R.drawable.location_watch, false, true);*/
					mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, false, false);
				}
				if (mLatLng.latitude != 0 && mLatLng.latitude != 0){
					firstLoad = false;
					mGaoDeMap.setMyLocationEnabled(false);
				}
				/*
				 * mListener.onLocationChanged(aLocation);// 显示系统小蓝点
				 * aMap.animateCamera(CameraUpdateFactory .newCameraPosition(new
				 * CameraPosition(new LatLng( aLocation.getLatitude(), aLocation
				 * .getLongitude()), 18, 0, 30)), 1000, null); firstLoad =
				 * false;
				 */
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
					&& !TextUtils.isEmpty(result.getRegeocodeAddress()
							.getFormatAddress())) {
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
		// 注销广播
	}

	private final int REARCH = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REARCH:
			if (resultCode == RESULT_OK) {
				mMapUtil.clear();
				sb.setVisibility(View.VISIBLE);
				sb.setProgress(0);
				tv_radius.setText(getResources().getString(R.string.radius) + getResources().getString(R.string.mh) + "500" + getResources().getString(R.string.m));
				centerLatLng = new LatLng(data.getDoubleExtra("Lat", 0d), data.getDoubleExtra("Lng", 0d));
				if (mWatchStateModel.getLatitude() == 0
						&& mWatchStateModel.getLongitude() == 0) {

				} else {
					if (mLatLng != null)
					{
					/*	addMarker(mLatLng, R.drawable.location_watch, false,
								false);*/
						mMapUtil.addMarker(mLatLng.latitude, mLatLng.longitude, R.drawable.location_watch, false, false);
					}
					/*
					 * addMarker( new LatLng(mWatchStateModel.getLatitude(),
					 * mWatchStateModel.getLongitude()),
					 * R.drawable.location_watch, false, false);
					 */
				}
				isEditLatLng = true;
	/*			mGaoDeMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(centerLatLng, 18,
								0, 30)), 1000, null);*/
				mMapUtil.animateCam(centerLatLng.latitude, centerLatLng.longitude, 16, 0, 0, 1000, null);
				createFence();
			}
			break;
		default:
			break;

		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	
	private class searchAddressThread extends Thread{
		private String address;
		private double lat,lon;
		public searchAddressThread(String text,double latitude,double longitude) {
			// TODO Auto-generated constructor stub
			address = text;
			lat = latitude;
			lon = longitude;
		}
		
        public void run() {
            HttpURLConnection connection = null;
            try {
            	//URLEncoder解决参数乱码
            	String urlString;
            	int retCode;
            	urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
            	urlString += "query="+URLEncoder.encode(address);
            	urlString += "&key="+URLEncoder.encode("AIzaSyDMG1nXJI_hMtYCZnwinlJp-yErbhBpPkM");
            	urlString += "&location="+URLEncoder.encode(String.valueOf(lat))+","+URLEncoder.encode(String.valueOf(lon));
            	urlString += "&radius="+URLEncoder.encode("3000");
            	Log.v("kkk", urlString);
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                // 设置请求方法，默认是GET
                connection.setRequestMethod("GET");
                // 设置字符集
              //  connection.setRequestProperty("Charset", "UTF-8");
                // 设置文件类型
           //     connection.setRequestProperty("Content-Type", "json/application");
                // 设置请求参数，可通过Servlet的getHeader()获取
            //    connection.setRequestProperty("Cookie", "AppName=" + URLEncoder.encode("你好", "UTF-8"));
                // 设置自定义参数
           //     connection.setRequestProperty("query", URLEncoder.encode("雪象", "UTF-8"));
            //    connection.setRequestProperty("key", URLEncoder.encode("AIzaSyDMG1nXJI_hMtYCZnwinlJp-yErbhBpPkM", "UTF-8"));
           //     Log.v("kkk", "222");
                retCode = connection.getResponseCode();
                if(retCode == 200){
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
                    byte[] data = new byte[1024];  
                    int count = -1;  
              //      Log.v("kkk", "111");
                    while((count = is.read(data,0,1024)) != -1)  
                        outStream.write(data, 0, count);  
                      
                    data = null;
                    String json = new String(outStream.toByteArray());
                    Log.v("kkk", "json = "+json);
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray array = jsonObject.getJSONArray("results");
                    listString.clear();
					for (int i = 0; i < array.length(); i++) {
						listString.add(array.getJSONObject(i).getString("name"));
			//			Log.v("kkk", array.getJSONObject(i).getString("name"));
					}
					textChangedHandler.sendEmptyMessage(1);
 
                }
                else {
					Log.v("kkk", "getResponseCode code = "+retCode);
				}
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
        };
    };
    
    
    private Handler textChangedHandler = new Handler(){
    	public void handleMessage(Message msg) {
			ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.route_inputs, listString);
			et_search.setAdapter(aAdapter);
			aAdapter.notifyDataSetChanged();
    	};
    };
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		String newText = s.toString().trim();
		
		if(!newText.equals("")){
			if(AppData.GetInstance(this).getMapSelect() == 1)
			{
				Inputtips inputTips = new Inputtips(FenceEdit.this,
						new InputtipsListener() {
		
							@Override
							public void onGetInputtips(List<Tip> tipList, int rCode) {
								if (rCode == 0) {// 正确返回
									listString.clear();
									for (int i = 0; i < tipList.size(); i++) {
										listString.add(tipList.get(i).getName());
									}
									ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.route_inputs, listString);
									et_search.setAdapter(aAdapter);
									aAdapter.notifyDataSetChanged();
								}
							}
						});
				try {
					inputTips.requestInputtips(newText, "");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
		
				} catch (AMapException e) {
					e.printStackTrace();
				}
			}
			else if(AppData.GetInstance(this).getMapSelect() == 2){
				new searchAddressThread(newText,mLatLng.latitude,mLatLng.longitude).start();
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		createFence();
		tv_radius.setText(getResources().getString(R.string.radius) + getResources().getString(R.string.mh) 
			+ String.valueOf(progress + 500) + getResources().getString(R.string.m));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
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
			
			mMapUtil.animateCam(mWatchStateModel.getLatitude(),mWatchStateModel.getLongitude(),16,0,0,1000,null);
			mMapUtil.addMarker(mWatchStateModel.getLatitude(), mWatchStateModel.getLongitude(), R.drawable.location_watch, true, true);
			GoogleMapListenerInit();
			Intent intent = getIntent();
			position = intent.getIntExtra("position", -1);
			if (position != -1) {
				initGeoFenceData();
				mGeoFenceModel = mGeoFenceList.get(position);
				centerLatLng = new LatLng(mGeoFenceModel.getLat(),mGeoFenceModel.getLng());
			//	mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(centerLatLng, 16, 0,30)), 1000, null);
				mMapUtil.animateCam(centerLatLng.latitude, centerLatLng.longitude, 16, 0, 0, 1000, null);
				sb.setVisibility(View.VISIBLE);
				sb.setProgress(mGeoFenceModel.getRadius() - 500);
				createFence();
				isEditLatLng = true;
			}
		}
	}
	
	private void GoogleMapListenerInit(){
		mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
				// TODO Auto-generated method stub
				if (centerLatLng == null) {
					sb.setVisibility(View.VISIBLE);
					tv_radius.setText(getResources().getString(R.string.radius) + getResources().getString(R.string.mh) + "500" + getResources().getString(R.string.m));
					centerLatLng = new LatLng(latLng.latitude, latLng.longitude);
					createFence();
					isEditLatLng = true;
				} else {
					return;
				}
			}
		});
		
		mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(com.google.android.gms.maps.model.Marker arg0) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
}
