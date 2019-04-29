package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import vip.inteltech.gat.comm.Constants;

@Table(name = "watchs")
public class WatchModel {
    @Column(name = "wId", isId = true, autoGen = true)
    private int id;
    @Column(name = "userId")
    private int userId = 0;
    @Column(name = "name")
    private String name;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "phone")
    private String phone;
    @Column(name = "cornet")
    private String cornet;
    @Column(name = "gender")
    private String gender;
    @Column(name = "birthday")
    private String birthday;
    @Column(name = "grade")
    private int grade = 0;

    @Column(name = "homeAddress")
    private String HomeAddress;
    @Column(name = "homeLat")
    private double HomeLat = 0;
    @Column(name = "homeLng")
    private double HomeLng = 0;
    @Column(name = "schoolAddress")
    private String SchoolAddress;
    @Column(name = "schoolLat")
    private double SchoolLat = 0;
    @Column(name = "schoolLng")
    private double SchoolLng = 0;
    @Column(name = "lastestTime")
    private String LastestTime;

    @Column(name = "setVersionNO")
    private String SetVersionNO;
    @Column(name = "contactVersionNO")
    private String ContactVersionNO;

    @Column(name = "operatorType")
    private String OperatorType;
    @Column(name = "smsNumber")
    private String SmsNumber;
    @Column(name = "smsBalanceKey")
    private String SmsBalanceKey;
    @Column(name = "smsFlowKey")
    private String SmsFlowKey;

    @Column(name = "activeDate")
    private String ActiveDate;
    @Column(name = "model")
    private String model;
    @Column(name = "createTime")
    private String CreateTime;
    @Column(name = "bindNumber")
    private String BindNumber;
    @Column(name = "currentFirmware")
    private String CurrentFirmware;
    @Column(name = "firmware")
    private String Firmware;
    @Column(name = "hireExpireDate")
    private String HireExpireDate;
    @Column(name = "hireStartDate")
    private String HireStartDate;
    @Column(name = "updateTime")
    private String UpdateTime;
    @Column(name = "serialNumber")
    private String SerialNumber;
    @Column(name = "password")
    private String Password;
    @Column(name = "isGuard")
    private boolean IsGuard = false;
    @Column(name = "deviceType")
    private String DeviceType;
    @Column(name = "cloudPlatform")
    private int cloudPlatform;

	/*
	 "ActiveDate",设备活动时间
	 "BabyName",
	 "BindNumber",绑定号
	 "Birthday",
	 "CreateTime",
	 "CurrentFirmware",当前固件版本号
	 "DeviceID",
	 "DeviceModelID",
	 "Firmware",需要升级的固件版本号
	 "Gender",
	 "Grade",
	 "HireExpireDate",设备到期时间
	 "HireStartDate",设备激活时间
	 "HomeAddress",
	 "HomeLat":"0.0",
	 "HomeLng":"0.0",
	 "IsGuard":"0",是否开启宝贝守护功能
	 "Password":"123456",
	 "PhoneCornet":"",
	 "PhoneNumber":"",
	 "Photo":"",
	 "SchoolAddress":"",
	 "SchoolLat":"0.0",
	 "SchoolLng":"0.0",
	 "SerialNumber":"1111111111",
	 "UpdateTime":"2015/08/26 16:55:10",
	 "UserId":"3"
	 * */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        HomeAddress = homeAddress;
    }

    public double getHomeLat() {
        return HomeLat;
    }

    public void setHomeLat(double homeLat) {
        HomeLat = homeLat;
    }

    public double getHomeLng() {
        return HomeLng;
    }

    public void setHomeLng(double homeLng) {
        HomeLng = homeLng;
    }

    public String getSchoolAddress() {
        return SchoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        SchoolAddress = schoolAddress;
    }

    public double getSchoolLat() {
        return SchoolLat;
    }

    public void setSchoolLat(double schoolLat) {
        SchoolLat = schoolLat;
    }

    public double getSchoolLng() {
        return SchoolLng;
    }

    public void setSchoolLng(double schoolLng) {
        SchoolLng = schoolLng;
    }

    public String getLastestTime() {
        return LastestTime;
    }

    public void setLastestTime(String lastestTime) {
        LastestTime = lastestTime;
    }

    public String getSetVersionNO() {
        return SetVersionNO;
    }

    public void setSetVersionNO(String setVersionNO) {
        SetVersionNO = setVersionNO;
    }

    public String getContactVersionNO() {
        return ContactVersionNO;
    }

    public void setContactVersionNO(String contactVersionNO) {
        ContactVersionNO = contactVersionNO;
    }

    public String getOperatorType() {
        return OperatorType;
    }

    public void setOperatorType(String operatorType) {
        OperatorType = operatorType;
    }

    public String getSmsNumber() {
        return SmsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        SmsNumber = smsNumber;
    }

    public String getSmsBalanceKey() {
        return SmsBalanceKey;
    }

    public void setSmsBalanceKey(String smsBalanceKey) {
        SmsBalanceKey = smsBalanceKey;
    }

    public String getSmsFlowKey() {
        return SmsFlowKey;
    }

    public void setSmsFlowKey(String smsFlowKey) {
        SmsFlowKey = smsFlowKey;
    }

    public String getActiveDate() {
        return ActiveDate;
    }

    public void setActiveDate(String activeDate) {
        ActiveDate = activeDate;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getBindNumber() {
        return BindNumber;
    }

    public void setBindNumber(String bindNumber) {
        BindNumber = bindNumber;
    }

    public String getCurrentFirmware() {
        if (CurrentFirmware == null) {
            return Constants.DEFAULT_BLANK;
        }
        return CurrentFirmware;
    }

    public void setCurrentFirmware(String currentFirmware) {
        CurrentFirmware = currentFirmware;
    }

    public String getFirmware() {
        return Firmware;
    }

    public void setFirmware(String firmware) {
        Firmware = firmware;
    }

    public String getHireExpireDate() {
        return HireExpireDate;
    }

    public void setHireExpireDate(String hireExpireDate) {
        HireExpireDate = hireExpireDate;
    }

    public String getHireStartDate() {
        return HireStartDate;
    }

    public void setHireStartDate(String hireStartDate) {
        HireStartDate = hireStartDate;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isIsGuard() {
        return IsGuard;
    }

    public void setIsGuard(boolean isGuard) {
        IsGuard = isGuard;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public int getCloudPlatform() {
        return cloudPlatform;
    }

    public void setCloudPlatform(int cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }
}