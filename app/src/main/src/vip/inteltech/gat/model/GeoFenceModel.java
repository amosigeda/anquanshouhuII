package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "Geofence")
public class GeoFenceModel {
	@Column(name = "DeviceId")
	private String DeviceId;
	@Column(name = "GeofenceID")
	private String GeofenceID;
	@Column(name = "FenceName")
	private String FenceName;
	@Column(name = "Entry")
	private String Entry;
	@Column(name = "Exit")
	private String Exit;
	@Column(name = "CreateTime")
	private String CreateTime;
	@Column(name = "UpdateTime")
	private String UpdateTime;
	@Column(name = "Enable")
	private String Enable;
	@Column(name = "Description")
	private String Description;
	@Column(name = "Lat")
	private double Lat;
	@Column(name = "Lng")
	private double Lng;
	@Column(name = "Radius")
	private int Radius;
	public String getDeviceId() {
		return DeviceId;
	}
	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}
	public String getGeofenceID() {
		return GeofenceID;
	}
	public void setGeofenceID(String geofenceID) {
		GeofenceID = geofenceID;
	}
	public String getFenceName() {
		return FenceName;
	}
	public void setFenceName(String fenceName) {
		FenceName = fenceName;
	}
	public String getEntry() {
		return Entry;
	}
	public void setEntry(String entry) {
		Entry = entry;
	}
	public String getExit() {
		return Exit;
	}
	public void setExit(String exit) {
		Exit = exit;
	}
	public String getUpdateTime() {
		return UpdateTime;
	}
	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getEnable() {
		return Enable;
	}
	public void setEnable(String enable) {
		Enable = enable;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public double getLat() {
		return Lat;
	}
	public void setLat(double lat) {
		Lat = lat;
	}
	public double getLng() {
		return Lng;
	}
	public void setLng(double lng) {
		Lng = lng;
	}
	public int getRadius() {
		return Radius;
	}
	public void setRadius(int radius) {
		Radius = radius;
	}
	
	
	
}
