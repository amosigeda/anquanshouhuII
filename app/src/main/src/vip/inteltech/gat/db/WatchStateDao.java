/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vip.inteltech.gat.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vip.inteltech.gat.model.WatchStateModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WatchStateDao {
	public static final String TABLE_NAME = "watchState";
	public static final String COLUMN_NAME_ID = "wId";
	public static final String COLUMN_NAME_ALTITUDE = "altitude";
	public static final String COLUMN_NAME_LATITUDE = "Latitude";
	public static final String COLUMN_NAME_LONGITUDE = "Longitude";
	public static final String COLUMN_NAME_COURSE = "course";
	public static final String COLUMN_NAME_ELECTRICITY = "electricity";
	public static final String COLUMN_NAME_STEP = "step";
	public static final String COLUMN_NAME_HEALTH = "health";
	public static final String COLUMN_NAME_ONLINE = "online";
	public static final String COLUMN_NAME_SPEED = "speed";
	public static final String COLUMN_NAME_SATELLITENUMBER = "satelliteNumber";
	public static final String COLUMN_NAME_SOCKETID = "socketId";
	public static final String COLUMN_NAME_CREATETIME = "createTime";
	public static final String COLUMN_NAME_SERVERTIME = "serverTime";
	public static final String COLUMN_NAME_UPDATETIME = "updateTime";
	public static final String COLUMN_NAME_DEVICETIME = "deviceTime";
	public static final String COLUMN_NAME_LOCATIONTYPE = "locationType";
	public static final String COLUMN_NAME_LBS = "LBS";
	public static final String COLUMN_NAME_GSM = "GSM";
	public static final String COLUMN_NAME_WIFI = "Wifi";

	private DbOpenHelper dbHelper;

	public WatchStateDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存联系人list
	 * 
	 * @param WatchStateList
	 */
	public void saveWatchStateList(List<WatchStateModel> WatchStateList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (WatchStateModel mWatchStateModel : WatchStateList) {
				//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, mWatchStateModel.getDeviceId());
				if(mWatchStateModel.getAltitude() != 0)
					values.put(COLUMN_NAME_ALTITUDE, mWatchStateModel.getAltitude());
				if(mWatchStateModel.getLatitude() != 0)
				    values.put(COLUMN_NAME_LATITUDE, mWatchStateModel.getLatitude());
				if(mWatchStateModel.getLongitude() != 0)
				    values.put(COLUMN_NAME_LONGITUDE, mWatchStateModel.getLongitude());
				if(mWatchStateModel.getCourse() != null)
				    values.put(COLUMN_NAME_COURSE, mWatchStateModel.getCourse());
				if(mWatchStateModel.getElectricity() != null)
				    values.put(COLUMN_NAME_ELECTRICITY, mWatchStateModel.getElectricity());
				if(mWatchStateModel.getStep() != null)
				    values.put(COLUMN_NAME_STEP, mWatchStateModel.getStep());
				if(mWatchStateModel.getHealth() != null)
				    values.put(COLUMN_NAME_HEALTH, mWatchStateModel.getHealth());
				if(mWatchStateModel.getOnline() != null)
				    values.put(COLUMN_NAME_ONLINE, mWatchStateModel.getOnline());
				if(mWatchStateModel.getSpeed() != null)
				    values.put(COLUMN_NAME_SPEED, mWatchStateModel.getSpeed());
				if(mWatchStateModel.getSatelliteNumber() != null)
				    values.put(COLUMN_NAME_SATELLITENUMBER, mWatchStateModel.getSatelliteNumber());
				if(mWatchStateModel.getSocketId() != null)
				    values.put(COLUMN_NAME_SOCKETID, mWatchStateModel.getSocketId());
				if(mWatchStateModel.getCreateTime() != null)
				    values.put(COLUMN_NAME_CREATETIME, mWatchStateModel.getCreateTime());
				if(mWatchStateModel.getServerTime() != null)
				    values.put(COLUMN_NAME_SERVERTIME, mWatchStateModel.getServerTime());
				if(mWatchStateModel.getUpdateTime() != null)
				    values.put(COLUMN_NAME_UPDATETIME, mWatchStateModel.getUpdateTime());
				if(mWatchStateModel.getDeviceTime() != null)
				    values.put(COLUMN_NAME_DEVICETIME, mWatchStateModel.getDeviceTime());
				if(mWatchStateModel.getLocationType() != null)
				    values.put(COLUMN_NAME_LOCATIONTYPE, mWatchStateModel.getLocationType());
				if(mWatchStateModel.getLBS() != null)
				    values.put(COLUMN_NAME_LBS, mWatchStateModel.getLBS());
				if(mWatchStateModel.getGSM() != null)
				    values.put(COLUMN_NAME_GSM, mWatchStateModel.getGSM());
				if(mWatchStateModel.getWifi() != null)
				    values.put(COLUMN_NAME_WIFI, mWatchStateModel.getWifi());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	/**
	 * 获取联系人Map
	 * 
	 * @return
	 */
	public Map<String, WatchStateModel> getWatchStateMap(int fId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, WatchStateModel> WatchStates = new HashMap<String, WatchStateModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(fId)});
			while (cursor.moveToNext()) {
				int deviceId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				double altitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_ALTITUDE));
				double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String course = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_COURSE));
				String electricity = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ELECTRICITY));
				String step = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STEP));
				String health = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HEALTH));
				String online = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ONLINE));
				String speed = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SPEED));
				String satelliteNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SATELLITENUMBER));
				String socketId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOCKETID));
				String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String serverTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SERVERTIME));
				String updateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String deviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				String locationType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCATIONTYPE));
				String LBS = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LBS));
				String GSM = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GSM));
				String Wifi = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WIFI));

				WatchStateModel mWatchStateModel = new WatchStateModel();
				mWatchStateModel.setDeviceId(deviceId);
				mWatchStateModel.setAltitude(altitude);
				mWatchStateModel.setLatitude(latitude);
				mWatchStateModel.setLongitude(longitude);
				mWatchStateModel.setCourse(course);
				mWatchStateModel.setElectricity(electricity);
				mWatchStateModel.setStep(step);
				mWatchStateModel.setHealth(health);
				mWatchStateModel.setOnline(online);
				mWatchStateModel.setSpeed(speed);
				mWatchStateModel.setSatelliteNumber(satelliteNumber);
				mWatchStateModel.setSocketId(socketId);
				mWatchStateModel.setCreateTime(createTime);
				mWatchStateModel.setServerTime(serverTime);
				mWatchStateModel.setUpdateTime(updateTime);
				mWatchStateModel.setDeviceTime(deviceTime);
				mWatchStateModel.setLocationType(locationType);
				mWatchStateModel.setLBS(LBS);
				mWatchStateModel.setGSM(GSM);
				mWatchStateModel.setWifi(Wifi);

				WatchStates.put(String.valueOf(deviceId), mWatchStateModel);
			}
			cursor.close();
		}
		return WatchStates;
	}
	/**
	 * 获取联系人List
	 * 
	 * @return
	 */
	public List<WatchStateModel> getWatchStateList(int fId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<WatchStateModel> WatchStates = new ArrayList<WatchStateModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(fId)});
			while (cursor.moveToNext()) {
				int deviceId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				double altitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_ALTITUDE));
				double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String course = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_COURSE));
				String electricity = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ELECTRICITY));
				String step = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STEP));
				String health = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HEALTH));
				String online = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ONLINE));
				String speed = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SPEED));
				String satelliteNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SATELLITENUMBER));
				String socketId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOCKETID));
				String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String serverTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SERVERTIME));
				String updateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String deviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				String locationType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCATIONTYPE));
				String LBS = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LBS));
				String GSM = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GSM));
				String Wifi = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WIFI));

				WatchStateModel mWatchStateModel = new WatchStateModel();
				mWatchStateModel.setDeviceId(deviceId);
				mWatchStateModel.setAltitude(altitude);
				mWatchStateModel.setLatitude(latitude);
				mWatchStateModel.setLongitude(longitude);
				mWatchStateModel.setCourse(course);
				mWatchStateModel.setElectricity(electricity);
				mWatchStateModel.setStep(step);
				mWatchStateModel.setHealth(health);
				mWatchStateModel.setOnline(online);
				mWatchStateModel.setSpeed(speed);
				mWatchStateModel.setSatelliteNumber(satelliteNumber);
				mWatchStateModel.setSocketId(socketId);
				mWatchStateModel.setCreateTime(createTime);
				mWatchStateModel.setServerTime(serverTime);
				mWatchStateModel.setUpdateTime(updateTime);
				mWatchStateModel.setDeviceTime(deviceTime);
				mWatchStateModel.setLocationType(locationType);
				mWatchStateModel.setLBS(LBS);
				mWatchStateModel.setGSM(GSM);
				mWatchStateModel.setWifi(Wifi);
				WatchStates.add(mWatchStateModel);
			}
			cursor.close();
		}
		return WatchStates;
	}
	public WatchStateModel getWatchState(int Id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		WatchStateModel mWatchStateModel = new WatchStateModel();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(Id)});
			//db.query(TABLE_NAME, new String[]{}, "wId = ?", new String[]{wId}, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				int deviceId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				double altitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_ALTITUDE));
				double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String course = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_COURSE));
				String electricity = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ELECTRICITY));
				String step = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STEP));
				String health = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HEALTH));
				String online = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ONLINE));
				String speed = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SPEED));
				String satelliteNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SATELLITENUMBER));
				String socketId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOCKETID));
				String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String serverTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SERVERTIME));
				String updateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String deviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				String locationType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCATIONTYPE));
				String LBS = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LBS));
				String GSM = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GSM));
				String Wifi = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WIFI));

				mWatchStateModel.setDeviceId(deviceId);
				mWatchStateModel.setAltitude(altitude);
				mWatchStateModel.setLatitude(latitude);
				mWatchStateModel.setLongitude(longitude);
				mWatchStateModel.setCourse(course);
				mWatchStateModel.setElectricity(electricity);
				mWatchStateModel.setStep(step);
				mWatchStateModel.setHealth(health);
				mWatchStateModel.setOnline(online);
				mWatchStateModel.setSpeed(speed);
				mWatchStateModel.setSatelliteNumber(satelliteNumber);
				mWatchStateModel.setSocketId(socketId);
				mWatchStateModel.setCreateTime(createTime);
				mWatchStateModel.setServerTime(serverTime);
				mWatchStateModel.setUpdateTime(updateTime);
				mWatchStateModel.setDeviceTime(deviceTime);
				mWatchStateModel.setLocationType(locationType);
				mWatchStateModel.setLBS(LBS);
				mWatchStateModel.setGSM(GSM);
				mWatchStateModel.setWifi(Wifi);
			}
			//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
			cursor.close();
		}
		return mWatchStateModel;
	}
	/**
	 * 更新联系人
	 * @param msgId
	 * @param values
	 */
	public void updateWatchState(String wId,WatchStateModel mWatchStateModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, String.valueOf(mWatchStateModel.getDeviceId()));
		if(mWatchStateModel.getAltitude() != 0)
			values.put(COLUMN_NAME_ALTITUDE, mWatchStateModel.getAltitude());
		if(mWatchStateModel.getLatitude() != 0)
		    values.put(COLUMN_NAME_LATITUDE, mWatchStateModel.getLatitude());
		if(mWatchStateModel.getLongitude() != 0)
		    values.put(COLUMN_NAME_LONGITUDE, mWatchStateModel.getLongitude());
		if(mWatchStateModel.getCourse() != null)
		    values.put(COLUMN_NAME_COURSE, mWatchStateModel.getCourse());
		if(mWatchStateModel.getElectricity() != null)
		    values.put(COLUMN_NAME_ELECTRICITY, mWatchStateModel.getElectricity());
		if(mWatchStateModel.getStep() != null)
		    values.put(COLUMN_NAME_STEP, mWatchStateModel.getStep());
		if(mWatchStateModel.getHealth() != null)
		    values.put(COLUMN_NAME_HEALTH, mWatchStateModel.getHealth());
		if(mWatchStateModel.getOnline() != null)
		    values.put(COLUMN_NAME_ONLINE, mWatchStateModel.getOnline());
		if(mWatchStateModel.getSpeed() != null)
		    values.put(COLUMN_NAME_SPEED, mWatchStateModel.getSpeed());
		if(mWatchStateModel.getSatelliteNumber() != null)
		    values.put(COLUMN_NAME_SATELLITENUMBER, mWatchStateModel.getSatelliteNumber());
		if(mWatchStateModel.getSocketId() != null)
		    values.put(COLUMN_NAME_SOCKETID, mWatchStateModel.getSocketId());
		if(mWatchStateModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mWatchStateModel.getCreateTime());
		if(mWatchStateModel.getServerTime() != null)
		    values.put(COLUMN_NAME_SERVERTIME, mWatchStateModel.getServerTime());
		if(mWatchStateModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mWatchStateModel.getUpdateTime());
		if(mWatchStateModel.getDeviceTime() != null)
		    values.put(COLUMN_NAME_DEVICETIME, mWatchStateModel.getDeviceTime());
		if(mWatchStateModel.getLocationType() != null)
		    values.put(COLUMN_NAME_LOCATIONTYPE, mWatchStateModel.getLocationType());
		if(mWatchStateModel.getLBS() != null)
		    values.put(COLUMN_NAME_LBS, mWatchStateModel.getLBS());
		if(mWatchStateModel.getGSM() != null)
		    values.put(COLUMN_NAME_GSM, mWatchStateModel.getGSM());
		if(mWatchStateModel.getWifi() != null)
		    values.put(COLUMN_NAME_WIFI, mWatchStateModel.getWifi());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteWatchState(String wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}

	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveWatchState(WatchStateModel mWatchStateModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, String.valueOf(mWatchStateModel.getDeviceId()));
		if(mWatchStateModel.getAltitude() != 0)
			values.put(COLUMN_NAME_ALTITUDE, mWatchStateModel.getAltitude());
		if(mWatchStateModel.getLatitude() != 0)
		    values.put(COLUMN_NAME_LATITUDE, mWatchStateModel.getLatitude());
		if(mWatchStateModel.getLongitude() != 0)
		    values.put(COLUMN_NAME_LONGITUDE, mWatchStateModel.getLongitude());
		if(mWatchStateModel.getCourse() != null)
		    values.put(COLUMN_NAME_COURSE, mWatchStateModel.getCourse());
		if(mWatchStateModel.getElectricity() != null)
		    values.put(COLUMN_NAME_ELECTRICITY, mWatchStateModel.getElectricity());
		if(mWatchStateModel.getStep() != null)
		    values.put(COLUMN_NAME_STEP, mWatchStateModel.getStep());
		if(mWatchStateModel.getHealth() != null)
		    values.put(COLUMN_NAME_HEALTH, mWatchStateModel.getHealth());
		if(mWatchStateModel.getOnline() != null)
		    values.put(COLUMN_NAME_ONLINE, mWatchStateModel.getOnline());
		if(mWatchStateModel.getSpeed() != null)
		    values.put(COLUMN_NAME_SPEED, mWatchStateModel.getSpeed());
		if(mWatchStateModel.getSatelliteNumber() != null)
		    values.put(COLUMN_NAME_SATELLITENUMBER, mWatchStateModel.getSatelliteNumber());
		if(mWatchStateModel.getSocketId() != null)
		    values.put(COLUMN_NAME_SOCKETID, mWatchStateModel.getSocketId());
		if(mWatchStateModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mWatchStateModel.getCreateTime());
		if(mWatchStateModel.getServerTime() != null)
		    values.put(COLUMN_NAME_SERVERTIME, mWatchStateModel.getServerTime());
		if(mWatchStateModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mWatchStateModel.getUpdateTime());
		if(mWatchStateModel.getDeviceTime() != null)
		    values.put(COLUMN_NAME_DEVICETIME, mWatchStateModel.getDeviceTime());
		if(mWatchStateModel.getLocationType() != null)
		    values.put(COLUMN_NAME_LOCATIONTYPE, mWatchStateModel.getLocationType());
		if(mWatchStateModel.getLBS() != null)
		    values.put(COLUMN_NAME_LBS, mWatchStateModel.getLBS());
		if(mWatchStateModel.getGSM() != null)
		    values.put(COLUMN_NAME_GSM, mWatchStateModel.getGSM());
		if(mWatchStateModel.getWifi() != null)
		    values.put(COLUMN_NAME_WIFI, mWatchStateModel.getWifi());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}