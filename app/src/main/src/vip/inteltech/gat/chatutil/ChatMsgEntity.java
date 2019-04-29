
package vip.inteltech.gat.chatutil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "chatMsgs")
public class ChatMsgEntity {
    @Column(name = "DeviceVoiceId")
	private String DeviceVoiceId;
    @Column(name = "DeviceID")
	private String DeviceID;
    @Column(name = "UserID")
	private String UserID;
    @Column(name = "State")
	private String State;
    @Column(name = "TotalPackage")
	private String TotalPackage;
    @Column(name = "CurrentPackage")
	private String CurrentPackage;
    @Column(name = "Type")
	private String Type;
    @Column(name = "ObjectId")
	private String ObjectId;
    @Column(name = "Mark")
	private String Mark;
    @Column(name = "Path")
	private String Path;
	@Column(name = "Length")
	private String Length;
	@Column(name = "CreateTime")
	private String CreateTime;
	@Column(name = "UpdateTime")
	private String UpdateTime;
	@Column(name = "MsgType")
	private String MsgType;
	@Column(name = "isRead")
	private boolean isRead = false;

	public String getDeviceVoiceId() {
		return DeviceVoiceId;
	}
	public void setDeviceVoiceId(String deviceVoiceId) {
		DeviceVoiceId = deviceVoiceId;
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
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public String getTotalPackage() {
		return TotalPackage;
	}
	public void setTotalPackage(String totalPackage) {
		TotalPackage = totalPackage;
	}
	public String getCurrentPackage() {
		return CurrentPackage;
	}
	public void setCurrentPackage(String currentPackage) {
		CurrentPackage = currentPackage;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getObjectId() {
		return ObjectId;
	}
	public void setObjectId(String objectId) {
		ObjectId = objectId;
	}
	public String getMark() {
		return Mark;
	}
	public void setMark(String mark) {
		Mark = mark;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public String getLength() {
		return Length;
	}
	public void setLength(String length) {
		Length = length;
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
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
}
