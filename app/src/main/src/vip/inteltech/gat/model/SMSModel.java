package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "sms")
public class SMSModel {
	@Column(name = "sort")
	private int sort;
	@Column(name = "DeviceSMSID")
	private String DeviceSMSID;
	@Column(name = "DeviceID")
	private String DeviceID;
	@Column(name = "UserID")
	private String UserID;
	@Column(name = "Type")
	private String Type;
	@Column(name = "State")
	private String State;
	@Column(name = "Phone")
	private String Phone;
	@Column(name = "Sms")
	private String Sms;
	@Column(name = "CreateTime")
	private String CreateTime;
	@Column(name = "UpdateTime")
	private String UpdateTime;
	
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getDeviceSMSID() {
		return DeviceSMSID;
	}
	public void setDeviceSMSID(String deviceSMSID) {
		DeviceSMSID = deviceSMSID;
	}
	public String getDeviceID() {
		return DeviceID;
	}
	public void setDeviceID(String deviceID) {
		DeviceID = deviceID;
	}
	
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public String getSms() {
		return Sms;
	}
	public void setSms(String Sms) {
		this.Sms = Sms;
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
