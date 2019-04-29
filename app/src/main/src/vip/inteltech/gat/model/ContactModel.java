package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "contacts")
public class ContactModel {
	@Column(name = "fromId")
	private int fromId = 0;
	@Column(name = "id")
	private String id;
	@Column(name = "objectId")
	private String objectId;
	@Column(name = "relationShip")
	private String relationShip;
	@Column(name = "avatar")
	private String avatar;
	@Column(name = "avatarUrl")
	private String avatarUrl;
	@Column(name = "phone")
	private String phone;
	@Column(name = "cornet")
	private String cornet;
	@Column(name = "type")
	private String type;
	@Column(name = "wId")
	private int wId;
	
	public int getFromId() {
		return fromId;
	}
	public void setFromId(int fromId) {
		this.fromId = fromId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getRelationShip() {
		return relationShip;
	}
	public void setRelationShip(String relationShip) {
		this.relationShip = relationShip;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCornet() {
		return cornet;
	}
	public void setCornet(String cornet) {
		this.cornet = cornet;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
