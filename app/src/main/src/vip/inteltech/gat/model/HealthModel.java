package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "health")
public class HealthModel {
    @Column(name = "wId")
    private int DeviceId;
    @Column(name = "Pedometer")
    private String Pedometer;
    @Column(name = "Latitude")
    private double Latitude = 0;
    @Column(name = "Longitude")
    private double Longitude = 0;
    @Column(name = "DeviceTime")
    private String DeviceTime;
    @Column(name = "LocationType")
    private String LocationType;
    @Column(name = "Address")
    private String Address;

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public String getPedometer() {
        return Pedometer;
    }

    public void setPedometer(String pedometer) {
        Pedometer = pedometer;
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

    public String getDeviceTime() {
        return DeviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.DeviceTime = deviceTime;
    }

    public String getLocationType() {
        return LocationType;
    }

    public void setLocationType(String locationType) {
        this.LocationType = locationType;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}