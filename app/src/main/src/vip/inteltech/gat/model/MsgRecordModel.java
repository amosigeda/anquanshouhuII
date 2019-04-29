package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "msgrecord")
public class MsgRecordModel {
	@Column(name = "id",isId = true)
	private String id;
	@Column(name = "Type")
	private String Type;
	@Column(name = "DeviceID")
	private String DeviceID;
	@Column(name = "UserID")
	private String UserID;
	@Column(name = "Content")
	private String Content;
	@Column(name = "Message")
	private String Message;
	@Column(name = "CreateTime")
	private String CreateTime;
	@Column(name = "isHandle")
	private boolean isHandle = false;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
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
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public boolean isHandle() {
		return isHandle;
	}
	public void setHandle(boolean isHandle) {
		this.isHandle = isHandle;
	}
	
}
