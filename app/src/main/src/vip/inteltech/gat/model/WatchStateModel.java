package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "watchState")
public class WatchStateModel {
    @Column(name = "wId")
    private int DeviceId;
    @Column(name = "altitude")
    private double altitude = 0;
    @Column(name = "Latitude")
    private double Latitude = 0;
    @Column(name = "Longitude")
    private double Longitude = 0;
    @Column(name = "course")
    private String course;
    @Column(name = "electricity")
    private String electricity;
    @Column(name = "step")
    private String step;
    @Column(name = "health")
    private String health;
    @Column(name = "online")
    private String online;
    @Column(name = "speed")
    private String speed;
    @Column(name = "satelliteNumber")
    private String satelliteNumber;
    @Column(name = "socketId")
    private String socketId;
    @Column(name = "createTime")
    private String createTime;
    @Column(name = "serverTime")
    private String serverTime;
    @Column(name = "updateTime")
    private String updateTime;
    @Column(name = "deviceTime")
    private String deviceTime;
    @Column(name = "locationType")
    private String locationType;
    @Column(name = "LBS")
    private String LBS;
    @Column(name = "GSM")
    private String GSM;
    @Column(name = "Wifi")
    private String Wifi;

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(String satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLBS() {
        return LBS;
    }

    public void setLBS(String lBS) {
        LBS = lBS;
    }

    public String getGSM() {
        return GSM;
    }

    public void setGSM(String gSM) {
        GSM = gSM;
    }

    public String getWifi() {
        return Wifi;
    }

    public void setWifi(String wifi) {
        Wifi = wifi;
    }
}