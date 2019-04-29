package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "album")
public class AlbumModel implements Serializable {
    @Column(name = "DevicePhotoId")
    private String DevicePhotoId;
    @Column(name = "DeviceID")
    private int DeviceID = 0;
    @Column(name = "UserID")
    private int UserID = 0;
    @Column(name = "Source")
    private String Source;
    @Column(name = "DeviceTime")
    private String DeviceTime;
    @Column(name = "Latitude")
    private double Latitude;
    @Column(name = "Longitude")
    private double Longitude;
    @Column(name = "Address")
    private String Address;
    @Column(name = "Mark")
    private String Mark;
    @Column(name = "Path")
    private String Path;
    @Column(name = "Thumb")
    private String Thumb;
    @Column(name = "Local")
    private String Local;
    @Column(name = "Length")
    private String Length;
    @Column(name = "CreateTime")
    private String CreateTime;
    @Column(name = "UpdateTime")
    private String UpdateTime;


    public String getDevicePhotoId() {
        return DevicePhotoId;
    }

    public void setDevicePhotoId(String devicePhotoId) {
        DevicePhotoId = devicePhotoId;
    }

    public int getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getDeviceTime() {
        return DeviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        DeviceTime = deviceTime;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
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

    public String getLocal() {
        return Local;
    }

    public void setLocal(String local) {
        Local = local;
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

    public String getThumb() {
        return Thumb;
    }

    public void setThumb(String thumb) {
        Thumb = thumb;
    }
}
