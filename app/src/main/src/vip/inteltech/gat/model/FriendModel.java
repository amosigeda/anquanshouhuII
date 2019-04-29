package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "friends")
public class FriendModel {
	@Column(name = "id")
	private int id;
	@Column(name = "deviceFriendId")
	private int deviceFriendId;
	@Column(name = "friendDeviceId")
	private int friendDeviceId;
	@Column(name = "relationShip")
	private String relationShip;
	@Column(name = "name")
	private String name;
	@Column(name = "phone")
	private String phone;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeviceFriendId() {
		return deviceFriendId;
	}
	public void setDeviceFriendId(int deviceFriendId) {
		this.deviceFriendId = deviceFriendId;
	}
	public int getFriendDeviceId() {
		return friendDeviceId;
	}
	public void setFriendDeviceId(int friendDeviceId) {
		this.friendDeviceId = friendDeviceId;
	}
	public String getRelationShip() {
		return relationShip;
	}
	public void setRelationShip(String relationShip) {
		this.relationShip = relationShip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}