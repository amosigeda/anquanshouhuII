package vip.inteltech.gat.maputil;

import java.util.List;

import vip.inteltech.gat.model.HistoryPointModel;

public class MapUtil {
	private BasicMapUtil mBasicMapUtil;
	public MapUtil(BasicMapUtil mapUtil) {
		mBasicMapUtil = mapUtil;
	}
	
	public void initMap(){
		mBasicMapUtil.initMap();
	}
	
	public void clear(){
		mBasicMapUtil.clear();
	}
	
	public void animateCam(double lat,double lon,float zoom,float tilt,float bearing,long x1,Object x2){
		mBasicMapUtil.animateCam(lat,lon,zoom,tilt,bearing,x1,x2);
	}
	
	public void animateCam(Boolean ifZoomIn,float x,Object o2){
		mBasicMapUtil.animateCam(ifZoomIn, x, o2);
	}
	
	public void addMarker(double lat,double lon,int markerPicID,boolean ifAddress,boolean ifJump){
		mBasicMapUtil.addMarker(lat,lon,markerPicID,ifAddress,ifJump);
	}
	
	public void addMarkerMe(double lat,double lon,int markerPicID,boolean ifAddress,boolean ifJump){
		mBasicMapUtil.addMarkerMe(lat, lon, markerPicID, ifAddress, ifJump);
	}
	
	public void setMapTypeToSatellite(boolean ifSatellite){
		mBasicMapUtil.setMapTypeToSatellite(ifSatellite);
	}
	
	public void addCircle(double lat,double lon,int markerPicID,int radius,int strokeColor,int fillColor,int strokeWidth,boolean ifAddress,boolean ifJump){
		mBasicMapUtil.addCircle(lat, lon, markerPicID, radius,strokeColor,fillColor,strokeWidth,ifAddress, ifJump);
	}
	
	public float getZoom()
	{
		return mBasicMapUtil.getZoom();
	}
	
	public void drawLine(double lat_a,double lon_a,double lat_b,double lon_b,String title,boolean ifCamFollow)
	{
		mBasicMapUtil.drawLine(lat_a, lon_a, lat_b, lon_b, title, ifCamFollow);
	}
	
	public void addStartMarker(double lat,double lon,int markerPicID,boolean ifJump){
		mBasicMapUtil.addStartMarker(lat, lon, markerPicID, ifJump);
	}
	
	public void addHistoryPoint(int markerPicID,List<HistoryPointModel> ptList){
		mBasicMapUtil.addHistoryPoint(markerPicID,ptList);
	}
}
