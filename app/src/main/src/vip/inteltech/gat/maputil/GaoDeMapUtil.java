package vip.inteltech.gat.maputil;

import java.util.List;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.*;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.HistoryPointModel;

public class GaoDeMapUtil extends BasicMapUtil {
	private AMap map;
	private MarkerOptions markerOption;
	private Marker mMarker;
	private Marker mMarkerMe;
	private Circle circle;

	//historyTrack
	private Marker startMarker;
	public GaoDeMapUtil(AMap m) {
		map = m;
	}

	@Override
	public void initMap() {
		// if(map == null)
		{
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
			myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
			myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
			// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
			myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
			// aMap.setMyLocationStyle(myLocationStyle);

			map.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
			// aMap.getUiSettings().setZoomGesturesEnabled(false);// 禁止通过手势缩放地图
			// aMap.getUiSettings().setScrollGesturesEnabled(false);//
			// 禁止通过手势移动地图
			map.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
			map.moveCamera(CameraUpdateFactory.zoomTo(15f));
			map.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			map.getUiSettings().setScaleControlsEnabled(true);
			// aMap.setMyLocationType()
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public void animateCam(double lat, double lon, float zoom, float tilt,
			float bearing, long x1, Object x2) {
		map.animateCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(new LatLng(lat, lon),
						zoom, tilt, bearing)), x1, (AMap.CancelableCallback) x2);

	}

	@Override
	public void addMarker(double lat, double lon, int markerPicID,
			boolean ifAddress, boolean ifJump) {
		if (mMarker != null)
			mMarker.remove();
		LatLng mLatLng = new LatLng(lat, lon);
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(markerPicID));
		mMarker = map.addMarker(markerOption);
		mMarker.setPosition(mLatLng);
		if (ifJump)
			jumpPoint(mMarker, mLatLng);

	}

	@Override
	public void animateCam(boolean ifZoomIn, float x, Object o2) {
		if (ifZoomIn) {
			map.animateCamera(CameraUpdateFactory.zoomIn(), 1000, null);
		} else {
			map.animateCamera(CameraUpdateFactory.zoomOut(), 1000, null);
		}
	}

	@Override
	public void addMarkerMe(double lat, double lon, int markerPicID,
			boolean ifAddress, boolean ifJump) {
		if (mMarkerMe != null)
			mMarkerMe.remove();
		LatLng mLatLng = new LatLng(lat, lon);
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(markerPicID));
		mMarkerMe = map.addMarker(markerOption);
		mMarkerMe.setPosition(mLatLng);
		if (ifJump)
			jumpPoint(mMarkerMe, mLatLng);
	}

	/**
	 * marker点击时跳动一下
	 */
	private void jumpPoint(final Marker marker, final LatLng latLng) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = map.getProjection();
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
				map.reloadMap();// 刷新地图
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	@Override
	public void setMapTypeToSatellite(boolean ifSatellite) {
		if (ifSatellite) {
			map.setMapType(AMap.MAP_TYPE_SATELLITE);
		} else {
			map.setMapType(AMap.MAP_TYPE_NORMAL);
		}
	}

	@Override
	public void addCircle(double lat, double lon, int markerPicID, int radius,
			int strokeColor, int fillColor, int strokeWidth, boolean ifAddress,
			boolean ifJump) {

		if (circle != null)
		{
			circle.remove();
		}
		if( markerPicID != 0)
		{
			if (mMarker != null)
				mMarker.remove();
			markerOption = new MarkerOptions();
			// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
			markerOption.draggable(true);
			markerOption.icon(BitmapDescriptorFactory.fromResource(markerPicID));
			mMarker = map.addMarker(markerOption);
			mMarker.setPosition(new LatLng(lat, lon));
		}
		// 绘制一个圆形
		circle = map.addCircle(new CircleOptions().center(new LatLng(lat, lon))
				.radius(radius).strokeColor(strokeColor).fillColor(fillColor)
				.strokeWidth(strokeWidth));
	}

	@Override
	public float getZoom() {
		if (map!=null) {
			return map.getCameraPosition().zoom;
		}
		return 18;
	}

	@Override
	public void drawLine(double lat_a, double lon_a, double lat_b,
			double lon_b, String title,boolean ifCamFollow) {
		map.addPolyline((new PolylineOptions())
				.add(new LatLng(lat_a, lon_a), new LatLng(lat_b, lon_b)).color(Color.RED))
				.setGeodesic(true);
		//aMap.clear();
		mMarker.remove();
		//mMarker = new Marker(null);
		addMarker(lat_a,lon_a,R.drawable.location_watch, false, false);
		mMarker.setTitle(title);
		mMarker.showInfoWindow();
		
		if(ifCamFollow){
			//mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng_a, mGaoDeMap.getCameraPosition().zoom, 0, 30)), 1000, null);
			animateCam(lat_a, lon_a, getZoom(), 0, 0, 1000, null);
		}
	}

	@Override
	public void addStartMarker(double lat, double lon,int markerPicID,boolean ifJump) {
		clear();
		animateCam(lat, lon, 18, 0, 0, 1000, null);
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(markerPicID));
		startMarker = map.addMarker(markerOption);
		startMarker.setPosition(new LatLng(lat, lon));
		if(ifJump)
			jumpPoint(startMarker, new LatLng(lat, lon));
	}

	@Override
	public void addHistoryPoint(int markerPicID,List<HistoryPointModel> ptList) {
		markerOption = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.history_point));
		for(int i = 0;i < ptList.size();i++){
			if(i > 0){
				float f = AMapUtils.calculateLineDistance(new LatLng(ptList.get(i).getLatitude(), ptList.get(i).getLongitude()), new LatLng(ptList.get(i).getLatitude(), ptList.get(i-1).getLongitude()));
				map.addMarker(markerOption).setPosition(new LatLng(ptList.get(i).getLatitude()-0.00001d, ptList.get(i).getLongitude()));
				if(f > 50){
				}
			}else{
				//aMap.addMarker(markerOption).setPosition(new LatLng(pointList.get(i).getLatitude(), pointList.get(i).getLongitude()));
			}
		}
	}

}
