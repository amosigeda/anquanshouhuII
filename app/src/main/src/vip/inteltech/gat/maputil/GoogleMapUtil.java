package vip.inteltech.gat.maputil;

import java.util.List;

import android.graphics.Color;
import android.util.Log;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.HistoryPointModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class GoogleMapUtil extends BasicMapUtil {
	private Marker mMarkerMe,mMarker,startMarker;
	MarkerOptions markerOptions;
	private GoogleMap map;
	private Circle circle;

	public GoogleMapUtil(GoogleMap m) {
		map = m;
	}

	@Override
	public void initMap() {

/*		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
*/		// aMap.setMyLocationStyle(myLocationStyle);

		map.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		// aMap.getUiSettings().setZoomGesturesEnabled(false);// 禁止通过手势缩放地图
		// aMap.getUiSettings().setScrollGesturesEnabled(false);// 禁止通过手势移动地图
		map.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
		map.setBuildingsEnabled(true);
		map.moveCamera(CameraUpdateFactory.zoomTo(15f));
		map.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		map.getUiSettings().setCompassEnabled(false);//hide the compass 
	//	map.getUiSettings().setAllGesturesEnabled(true);
	//	map.getUiSettings().setScaleControlsEnabled(true);
		// aMap.setMyLocationType()

	}

	@Override
	public void clear() {
		if (map != null) {
			map.clear();
		}
	}

	@Override
	public void animateCam(double lat, double lon, float zoom, float tilt,
			float bearing, long x1, Object x2) {
		if(map != null){
			map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lon), zoom-1, tilt, bearing)), (int)200, (GoogleMap.CancelableCallback)x2);
		}
	}

	@Override
	public void addMarker(double lat, double lon, int markerPicID,
			boolean ifAddress, boolean ifJump) {
		if(map!=null){
			LatLng mLatLng = new LatLng(lat, lon);
/*			markerOption = new MarkerOptions();
			//markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
			markerOption.draggable(true);
			markerOption.icon(BitmapDescriptorFactory.fromResource(markerPicID));
			mMarker = map.addMarker(markerOption);
			mMarker.setPosition(mLatLng);*/

			if(mMarker!=null)
				mMarker.remove();
			mMarker = map.addMarker(new MarkerOptions()
            .position(mLatLng)
            .icon(BitmapDescriptorFactory.fromResource(markerPicID))
            .infoWindowAnchor(0.5f, 0.5f));
		}
	}
	
	

	@Override
	public void animateCam(boolean ifZoomIn, float x, Object o2) {
		if(ifZoomIn){
			map.animateCamera(CameraUpdateFactory.zoomIn(), 200, null);
		}
		else {
			map.animateCamera(CameraUpdateFactory.zoomOut(), 200, null);
		}
	}

	@Override
	public void addMarkerMe(double lat, double lon, int markerPicID,
			boolean ifAddress, boolean ifJump) {
		LatLng mLatLng = new LatLng(lat, lon);
/*		markerOption = new MarkerOptions();
		//markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(markerPicID));
		mMarker = map.addMarker(markerOption);
		mMarker.setPosition(mLatLng);*/
		if(mMarkerMe!=null)
			mMarkerMe.remove();
		mMarkerMe = map.addMarker(new MarkerOptions()
        .position(mLatLng)
        .icon(BitmapDescriptorFactory.fromResource(markerPicID))
        .infoWindowAnchor(0.5f, 0.5f));
	}

	@Override
	public void setMapTypeToSatellite(boolean ifSatellite) {
		if (ifSatellite) {
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	@Override
	public void addCircle(double lat, double lon, int markerPicID, int radius,
			int strokeColor,int fillColor,int strokeWidth,boolean ifAddress, boolean ifJump) {
		LatLng mLatLng = new LatLng(lat, lon);
	
		if(circle!=null)
			circle.remove();
		if(markerPicID != 0)
		{	
			if(mMarker!=null)
				mMarker.remove();
			mMarker = map.addMarker(new MarkerOptions()
	        .position(mLatLng)
	        .icon(BitmapDescriptorFactory.fromResource(markerPicID))
	        .infoWindowAnchor(0.5f, 0.5f));
		}
		// 绘制一个圆形
		circle = map.addCircle(new CircleOptions().center(new LatLng(lat, lon))
					.radius(radius).strokeColor(strokeColor)
					.fillColor(fillColor).strokeWidth(strokeWidth));
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
		map.addPolyline((new PolylineOptions()).add(new LatLng(lat_a, lon_a), new LatLng(lat_b, lon_b)).color(Color.RED)).setGeodesic(true);
		//aMap.clear();
		mMarker.remove();
		//mMarker = new Marker(null);
		addMarker(lat_a,lon_a,R.drawable.location_watch, false, false);
		mMarker.setTitle(title);
		mMarker.showInfoWindow();
	//	Log.v("kkk", "drawLine: "+lat_a+" "+lon_a);
		if(ifCamFollow){
			//mGaoDeMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng_a, mGaoDeMap.getCameraPosition().zoom, 0, 30)), 1000, null);
			animateCam(lat_a, lon_a, getZoom()+1, 0, 0, 200, null);
		}
	}

	@Override
	public void addStartMarker(double lat, double lon,int markerPicID,boolean ifJump) {
		clear();
		animateCam(lat, lon, 18, 0, 0, 200, null);

		startMarker = map.addMarker(new MarkerOptions()
        .position(new LatLng(lat, lon))
        .icon(BitmapDescriptorFactory.fromResource(markerPicID))
        .infoWindowAnchor(0.5f, 0.5f));
/*		if(ifJump)
			jumpPoint(startMarker, new LatLng(lat, lon));*/
	}

	@Override
	public void addHistoryPoint(int markerPicID,List<HistoryPointModel> ptList) {
		markerOptions = new MarkerOptions();
		// markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOptions.draggable(true);
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.history_point));
		for(int i = 0;i < ptList.size();i++){
			if(i > 0){
		//		float f = GoogleMapUtil.calculateLineDistance(new LatLng(ptList.get(i).getLatitude(), ptList.get(i).getLongitude()), new LatLng(ptList.get(i).getLatitude(), ptList.get(i-1).getLongitude()));
				//map.addMarker(markerOptions).setPosition(new LatLng(ptList.get(i).getLatitude()-0.00001d, ptList.get(i).getLongitude()));
				map.addMarker(new MarkerOptions()
				.position(new LatLng(ptList.get(i).getLatitude()-0.00001d, ptList.get(i).getLongitude()))
				.icon(BitmapDescriptorFactory.fromResource(markerPicID))
				.infoWindowAnchor(0.5f, 0.5f));
			//	if(f > 50){
			//	}
			}else{
				//aMap.addMarker(markerOption).setPosition(new LatLng(pointList.get(i).getLatitude(), pointList.get(i).getLongitude()));
			}
		}
	}

}
