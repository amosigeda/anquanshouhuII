package vip.inteltech.gat.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

import vip.inteltech.gat.comm.Constants;

public class AppData {
	private static Lock lockHelper = new ReentrantLock();
	private static AppData _object;
	private SharedPreferences sp;
	public String PackageName;

    private String phoneNumber;
    private String bindNumber;

	public AppData(Context content)
	{
		content = AppContext.getContext();
		sp=content.getSharedPreferences("config", android.content.Context.MODE_PRIVATE);
		try {
			PackageName=content.getPackageManager().getPackageInfo(content.getPackageName(), 0).packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static AppData GetInstance(Context content)
	{
		lockHelper.lock();
		if(_object==null)
			_object=new AppData(content);
		lockHelper.unlock();
		return _object;
	}


    public String getPhoneNumber() {
        return sp.getString("phoneNumber", Constants.DEFAULT_BLANK);
    }

    public void setPhoneNumber(String phoneNumber) {
        sp.edit().putString("phoneNumber",phoneNumber).apply();
    }

    public String getBindNumber() {
        return sp.getString("bindNumber", Constants.DEFAULT_BLANK);
    }

    public void setBindNumber(String bindNumber) {
        sp.edit().putString("bindNumber",bindNumber).apply();
    }

    public void setFirstInit(boolean ifFirst){
		sp.edit().putBoolean("FirstInit", ifFirst).apply();
	}
	
	public boolean getFirstInit(){
		return sp.getBoolean("FirstInit", true);
	}
	/**
	 * MapSelect:
	 * 1:GaoDe
	 * 2:Google
	 * @param select
	 */
	public void setMapSelect(int select){
		sp.edit().putInt("MapSelect", select).apply();
	}
	public int getMapSelect(){
		return sp.getInt("MapSelect", 1);
	}
	public String getLoginName()
	{
		return sp.getString("LoginName", "");
	}
	public void setLoginName(String object)
	{
		sp.edit().putString("LoginName", object).apply();
	}
	public String getPwd()
	{
		return sp.getString("Pwd", "");
	}
	public void setPwd(String object)
	{
		sp.edit().putString("Pwd", object).apply();
	}
	public String getLoginId()
	{
		return sp.getString("LoginId", "");
	}
	public void setLoginId(String object)
	{
		sp.edit().putString("LoginId", object).apply();
	}
	public int getUserId()
	{
		return sp.getInt("userId", 0);
	}
	public void setUserId(int userId)
	{
		sp.edit().putInt("userId", userId).apply();
	}
	public int getSelectDeviceId()
	{
		return sp.getInt("SelectDeviceId", 0);
	}
	public void setSelectDeviceId(int SelectDeviceId)
	{
		sp.edit().putInt("SelectDeviceId", SelectDeviceId).apply();
	}
	public int getUserType()
	{
		return sp.getInt("UserType", 0);
	}
	public void setUserType(int UserType)
	{
		sp.edit().putInt("UserType", UserType).apply();
	}
	public String getName()
	{
		return sp.getString("Name", "");
	}
	public void setName(String object)
	{
		sp.edit().putString("Name", object).apply();
	}
	public boolean getLoginAuto()
	{
		return sp.getBoolean("LoginAuto", false);
	}
	public void setLoginAuto(boolean r)
	{
		sp.edit().putBoolean("LoginAuto", r).apply();
	}
	public boolean getNotification()
	{
		return sp.getBoolean("Notification", false);
	}
	public void setNotification(boolean r)
	{
		sp.edit().putBoolean("Notification", r).apply();
	}
	public boolean getNotificationSound()
	{
		return sp.getBoolean("NotificationSound", false);
	}
	public void setNotificationSound(boolean r)
	{
		sp.edit().putBoolean("NotificationSound", r).apply();
	}
	public boolean getNotificationVibration()
	{
		return sp.getBoolean("Notification", false);
	}
	public void setNotificationVibration(boolean r)
	{
		sp.edit().putBoolean("Notification", r).apply();
	}
	public boolean isHintUpdate(){
		if(sp.getString("isHintUpdate", "").equals(DateConversion.getToday())){
			return false;
		}else{
			sp.edit().putString("isHintUpdate", DateConversion.getToday()).apply();
			return true;
		}
	}
	public String getDeviceServiceTime(int deviceId)
	{
		return sp.getString(String.valueOf(deviceId) + "DeviceServiceTime", "");
	}
	public void setDeviceServiceTime(int deviceId, String object)
	{
		sp.edit().putString(String.valueOf(deviceId) + "DeviceServiceTime", object).apply();
	}
}