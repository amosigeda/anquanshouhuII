package vip.inteltech.gat.maputil;

import java.util.List;

import vip.inteltech.gat.model.HistoryPointModel;


public abstract class BasicMapUtil {
	public BasicMapUtil() {
	}
	public abstract void animateCam(double lat,double lon,float zoom,float tilt,float bearing,long x1,Object x2);
	public abstract void animateCam(boolean ifZoomIn,float x,Object o2);
	public abstract void initMap();
	public abstract void clear();
	public abstract void addMarker(double lat,double lon,int markerPicID,boolean ifAddress,boolean ifJump);
	public abstract void addMarkerMe(double lat,double lon,int markerPicID,boolean ifAddress,boolean ifJump);
	public abstract void setMapTypeToSatellite(boolean ifSatellite);
	public abstract void addCircle(double lat,double lon,int markerPicID,int radius,int strokeColor,int fillColor,int strokeWidth,boolean ifAddress,boolean ifJump);
	public abstract float getZoom();
	public abstract void drawLine(double lat_a,double lon_a,double lat_b,double lon_b,String title,boolean ifCamFollow);
	public abstract void addStartMarker(double lat,double lon,int markerPicID,boolean ifJump);
	public abstract void addHistoryPoint(int markerPicID,List<HistoryPointModel> ptList);
}
