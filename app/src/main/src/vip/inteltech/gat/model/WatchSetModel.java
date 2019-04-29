package vip.inteltech.gat.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "watchSet")
public class WatchSetModel {
    @Column(name = "wId", isId = true)
    private int deviceId = 0;
    @Column(name = "autoAnswer")
    private String autoAnswer;
    @Column(name = "reportLocation")
    private String reportLocation;
    @Column(name = "somatoAnswer")
    private String somatoAnswer;
    @Column(name = "reservedPower")
    private String reservedPower;
    @Column(name = "classDisabled")
    private String classDisabled;
    @Column(name = "timeSwitch")
    private String timeSwitch;
    @Column(name = "refusedStranger")
    private String refusedStranger;
    @Column(name = "watchOffAlarm")
    private String watchOffAlarm;
    @Column(name = "callSound")
    private String callSound;
    @Column(name = "callVibrate")
    private String callVibrate;
    @Column(name = "msgSound")
    private String msgSound;
    @Column(name = "msgVibrate")
    private String msgVibrate;
    @Column(name = "classDisableda")
    private String classDisableda;
    @Column(name = "classDisabledb")
    private String classDisabledb;
    @Column(name = "weekDisabled")
    private String weekDisabled;
    @Column(name = "timerOpen")
    private String timerOpen;
    @Column(name = "timerClose")
    private String timerClose;
    @Column(name = "brightScreen")
    private String brightScreen;
    @Column(name = "language")
    private String language;
    @Column(name = "timeZone")
    private String timeZone;
    @Column(name = "createTime")
    private String createTime;
    @Column(name = "updateTime")
    private String updateTime;
    @Column(name = "VersionNumber")
    private String VersionNumber;
    @Column(name = "WeekAlarm1")
    private String WeekAlarm1;
    @Column(name = "WeekAlarm2")
    private String WeekAlarm2;
    @Column(name = "WeekAlarm3")
    private String WeekAlarm3;
    @Column(name = "Alarm1")
    private String Alarm1;
    @Column(name = "Alarm2")
    private String Alarm2;
    @Column(name = "Alarm3")
    private String Alarm3;
    @Column(name = "LocationMode")
    private String LocationMode;
    @Column(name = "LocationTime")
    private String LocationTime;
    @Column(name = "FlowerNumber")
    private String FlowerNumber;
    @Column(name = "SleepCalculate")
    private String SleepCalculate;
    @Column(name = "StepCalculate")
    private String StepCalculate;
    @Column(name = "HrCalculate")
    private String HrCalculate;
    @Column(name = "SosMsgswitch")
    private String SosMsgswitch;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getAutoAnswer() {
        return autoAnswer;
    }

    public void setAutoAnswer(String autoAnswer) {
        this.autoAnswer = autoAnswer;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public String getSomatoAnswer() {
        return somatoAnswer;
    }

    public void setSomatoAnswer(String somatoAnswer) {
        this.somatoAnswer = somatoAnswer;
    }

    public String getReservedPower() {
        return reservedPower;
    }

    public void setReservedPower(String reservedPower) {
        this.reservedPower = reservedPower;
    }

    public String getClassDisabled() {
        return classDisabled;
    }

    public void setClassDisabled(String classDisabled) {
        this.classDisabled = classDisabled;
    }

    public String getTimeSwitch() {
        return timeSwitch;
    }

    public void setTimeSwitch(String timeSwitch) {
        this.timeSwitch = timeSwitch;
    }

    public String getRefusedStranger() {
        return refusedStranger;
    }

    public void setRefusedStranger(String refusedStranger) {
        this.refusedStranger = refusedStranger;
    }

    public String getWatchOffAlarm() {
        return watchOffAlarm;
    }

    public void setWatchOffAlarm(String watchOffAlarm) {
        this.watchOffAlarm = watchOffAlarm;
    }

    public String getCallSound() {
        return callSound;
    }

    public void setCallSound(String callSound) {
        this.callSound = callSound;
    }

    public String getCallVibrate() {
        return callVibrate;
    }

    public void setCallVibrate(String callVibrate) {
        this.callVibrate = callVibrate;
    }

    public String getMsgSound() {
        return msgSound;
    }

    public void setMsgSound(String msgSound) {
        this.msgSound = msgSound;
    }

    public String getMsgVibrate() {
        return msgVibrate;
    }

    public void setMsgVibrate(String msgVibrate) {
        this.msgVibrate = msgVibrate;
    }

    public String getClassDisableda() {
        return classDisableda;
    }

    public void setClassDisableda(String classDisableda) {
        this.classDisableda = classDisableda;
    }

    public String getClassDisabledb() {
        return classDisabledb;
    }

    public void setClassDisabledb(String classDisabledb) {
        this.classDisabledb = classDisabledb;
    }

    public String getWeekDisabled() {
        return weekDisabled;
    }

    public void setWeekDisabled(String weekDisabled) {
        this.weekDisabled = weekDisabled;
    }

    public String getTimerOpen() {
        return timerOpen;
    }

    public void setTimerOpen(String timerOpen) {
        this.timerOpen = timerOpen;
    }

    public String getTimerClose() {
        return timerClose;
    }

    public void setTimerClose(String timerClose) {
        this.timerClose = timerClose;
    }

    public String getBrightScreen() {
        return brightScreen;
    }

    public void setBrightScreen(String brightScreen) {
        this.brightScreen = brightScreen;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getVersionNumber() {
        return VersionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        VersionNumber = versionNumber;
    }

    public String getWeekAlarm1() {
        return WeekAlarm1;
    }

    public void setWeekAlarm1(String WeekAlarm1) {
        this.WeekAlarm1 = WeekAlarm1;
    }

    public String getWeekAlarm2() {
        return WeekAlarm2;
    }

    public void setWeekAlarm2(String WeekAlarm2) {
        this.WeekAlarm2 = WeekAlarm2;
    }

    public String getWeekAlarm3() {
        return WeekAlarm3;
    }

    public void setWeekAlarm3(String WeekAlarm3) {
        this.WeekAlarm3 = WeekAlarm3;
    }

    public String getAlarm1() {
        return Alarm1;
    }

    public void setAlarm1(String Alarm1) {
        this.Alarm1 = Alarm1;
    }

    public String getAlarm2() {
        return Alarm2;
    }

    public void setAlarm2(String Alarm2) {
        this.Alarm2 = Alarm2;
    }

    public String getAlarm3() {
        return Alarm3;
    }

    public void setAlarm3(String Alarm3) {
        this.Alarm3 = Alarm3;
    }

    public String getLocationMode() {
        return LocationMode;
    }

    public void setLocationMode(String LocationMode) {
        this.LocationMode = LocationMode;
    }

    public String getLocationTime() {
        return LocationTime;
    }

    public void setLocationTime(String LocationTime) {
        this.LocationTime = LocationTime;
    }

    public String getFlowerNumber() {
        return FlowerNumber;
    }

    public void setFlowerNumber(String FlowerNumber) {
        this.FlowerNumber = FlowerNumber;
    }

    public String getSleepCalculate() {
        return SleepCalculate;
    }

    public void setSleepCalculate(String SleepCalculate) {
        this.SleepCalculate = SleepCalculate;
    }

    public String getStepCalculate() {
        return StepCalculate;
    }

    public void setStepCalculate(String StepCalculate) {
        this.StepCalculate = StepCalculate;
    }

    public String getHrCalculate() {
        return HrCalculate;
    }

    public void setHrCalculate(String HrCalculate) {
        this.HrCalculate = HrCalculate;
    }

    public String getSosMsgswitch() {
        return SosMsgswitch;
    }

    public void setSosMsgswitch(String SosMsgswitch) {
        this.SosMsgswitch = SosMsgswitch;
    }
}