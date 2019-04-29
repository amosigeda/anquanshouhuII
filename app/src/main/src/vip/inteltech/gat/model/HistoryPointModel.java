package vip.inteltech.gat.model;

public class HistoryPointModel {
	private String Time;
	private double Latitude;
	private double Longitude;
	private String LocationType;
	private String CreateTime;
	private String UpdateTime;
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public String getLocationType() {
		return LocationType;
	}
	public void setLocationType(String locationType) {
		LocationType = locationType;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getUpdateTime() {
		return UpdateTime;
	}
	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}
}
