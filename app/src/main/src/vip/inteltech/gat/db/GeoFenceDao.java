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

import vip.inteltech.gat.model.GeoFenceModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class GeoFenceDao {
	public static final String TABLE_NAME = "Geofence";
	public static final String COLUMN_NAME_DEVICEID = "DeviceId";
	public static final String COLUMN_NAME_GEOFENCEID = "GeofenceID";
	public static final String COLUMN_NAME_FENCENAME = "FenceName";
	public static final String COLUMN_NAME_ENTRY = "Entry";
	public static final String COLUMN_NAME_EXIT = "Exit";
	public static final String COLUMN_NAME_UPDATETIME = "UpdateTime";
	public static final String COLUMN_NAME_ENABLE = "Enable";
	public static final String COLUMN_NAME_DESCRIPTION = "Description";
	public static final String COLUMN_NAME_LAT = "Lat";
	public static final String COLUMN_NAME_LNG = "Lng";
	public static final String COLUMN_NAME_RADIUS = "Radius";
	
	private DbOpenHelper dbHelper;
	
	public GeoFenceDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存围栏list
	 * 
	 * @param DeviceList
	 */
	public void saveGeoFenceList(List<GeoFenceModel> DeviceList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (GeoFenceModel mGeoFenceModel : DeviceList) {
				//System.out.println(mGeoFenceModel.getName() +"  "+ mGeoFenceModel.getBirthday());
				ContentValues values = new ContentValues();
				if(mGeoFenceModel.getDeviceId() != null)
				    values.put(COLUMN_NAME_DEVICEID, mGeoFenceModel.getDeviceId());
				if(mGeoFenceModel.getGeofenceID() != null)
				    values.put(COLUMN_NAME_GEOFENCEID, mGeoFenceModel.getGeofenceID());
				if(mGeoFenceModel.getFenceName() != null)
				    values.put(COLUMN_NAME_FENCENAME, mGeoFenceModel.getFenceName());
				if(mGeoFenceModel.getEntry() != null)
					values.put(COLUMN_NAME_ENTRY, mGeoFenceModel.getEntry());
				if(mGeoFenceModel.getEntry() != null)
				    values.put(COLUMN_NAME_EXIT, mGeoFenceModel.getExit());
				if(mGeoFenceModel.getUpdateTime() != null)
				    values.put(COLUMN_NAME_UPDATETIME, mGeoFenceModel.getUpdateTime());
				if(mGeoFenceModel.getEnable() != null)
				    values.put(COLUMN_NAME_ENABLE, mGeoFenceModel.getEnable());
				if(mGeoFenceModel.getDescription() != null)
				    values.put(COLUMN_NAME_DESCRIPTION, mGeoFenceModel.getDescription());
				values.put(COLUMN_NAME_LAT, mGeoFenceModel.getLat());
				values.put(COLUMN_NAME_LNG, mGeoFenceModel.getLng());
				values.put(COLUMN_NAME_RADIUS, mGeoFenceModel.getRadius());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	/**
	 * 获取围栏map
	 * 
	 * @return
	 */
	public Map<String, GeoFenceModel> getGeoFenceMap() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, GeoFenceModel> users = new HashMap<String, GeoFenceModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String DeviceId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String GeofenceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GEOFENCEID));
				String FenceName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FENCENAME));
				String Entry = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENTRY));
				String Exit = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EXIT));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String Enable = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENABLE));
				String Description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
				double Lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LAT));
				double Lng = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LNG));
				int Radius = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RADIUS));

				GeoFenceModel mGeoFenceModel = new GeoFenceModel();
				mGeoFenceModel.setDeviceId(DeviceId);
				mGeoFenceModel.setGeofenceID(GeofenceID);
				mGeoFenceModel.setFenceName(FenceName);
				mGeoFenceModel.setEntry(Entry);
				mGeoFenceModel.setExit(Exit);
				mGeoFenceModel.setUpdateTime(UpdateTime);
				mGeoFenceModel.setEnable(Enable);
				mGeoFenceModel.setDescription(Description);
				mGeoFenceModel.setLat(Lat);
				mGeoFenceModel.setLng(Lng);
				mGeoFenceModel.setRadius(Radius);
				users.put(DeviceId, mGeoFenceModel);
			}
			cursor.close();
		}
		return users;
	}
	/**
	 * 获取围栏list
	 * 
	 * @return
	 */
	public List<GeoFenceModel> getGeoFenceLists(int deviceId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<GeoFenceModel> list = new ArrayList<GeoFenceModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_DEVICEID + " = ? ", new String[]{String.valueOf(deviceId)});
			while (cursor.moveToNext()) {
				String DeviceId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String GeofenceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GEOFENCEID));
				String FenceName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FENCENAME));
				String Entry = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENTRY));
				String Exit = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EXIT));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String Enable = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENABLE));
				String Description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
				double Lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LAT));
				double Lng = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LNG));
				int Radius = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RADIUS));

				GeoFenceModel mGeoFenceModel = new GeoFenceModel();
				mGeoFenceModel.setDeviceId(DeviceId);
				mGeoFenceModel.setGeofenceID(GeofenceID);
				mGeoFenceModel.setFenceName(FenceName);
				mGeoFenceModel.setEntry(Entry);
				mGeoFenceModel.setExit(Exit);
				mGeoFenceModel.setUpdateTime(UpdateTime);
				mGeoFenceModel.setEnable(Enable);
				mGeoFenceModel.setDescription(Description);
				mGeoFenceModel.setLat(Lat);
				mGeoFenceModel.setLng(Lng);
				mGeoFenceModel.setRadius(Radius);
				list.add(mGeoFenceModel);
			}
			cursor.close();
		}
		return list;
	}
	public GeoFenceModel getGeoFence(int Id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		GeoFenceModel mGeoFenceModel = new GeoFenceModel();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_GEOFENCEID + " = ? ", new String[]{String.valueOf(Id)});
			//db.query(TABLE_NAME, new String[]{}, "wId = ?", new String[]{wId}, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				String DeviceId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String GeofenceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GEOFENCEID));
				String FenceName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FENCENAME));
				String Entry = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENTRY));
				String Exit = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EXIT));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String Enable = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ENABLE));
				String Description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
				double Lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LAT));
				double Lng = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LNG));
				int Radius = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RADIUS));

				mGeoFenceModel.setDeviceId(DeviceId);
				mGeoFenceModel.setGeofenceID(GeofenceID);
				mGeoFenceModel.setFenceName(FenceName);
				mGeoFenceModel.setEntry(Entry);
				mGeoFenceModel.setExit(Exit);
				mGeoFenceModel.setUpdateTime(UpdateTime);
				mGeoFenceModel.setEnable(Enable);
				mGeoFenceModel.setDescription(Description);
				mGeoFenceModel.setLat(Lat);
				mGeoFenceModel.setLng(Lng);
				mGeoFenceModel.setRadius(Radius);
			}
			cursor.close();
		}
		return mGeoFenceModel;
		
	}
	/**
	 * 更新围栏
	 * @param msgId
	 * @param values
	 */
	public void updateGeoFence(String wId,GeoFenceModel mGeoFenceModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mGeoFenceModel.getDeviceId() != null)
		    values.put(COLUMN_NAME_DEVICEID, mGeoFenceModel.getDeviceId());
		if(mGeoFenceModel.getGeofenceID() != null)
		    values.put(COLUMN_NAME_GEOFENCEID, mGeoFenceModel.getGeofenceID());
		if(mGeoFenceModel.getFenceName() != null)
		    values.put(COLUMN_NAME_FENCENAME, mGeoFenceModel.getFenceName());
		if(mGeoFenceModel.getEntry() != null)
			values.put(COLUMN_NAME_ENTRY, mGeoFenceModel.getEntry());
		if(mGeoFenceModel.getEntry() != null)
		    values.put(COLUMN_NAME_EXIT, mGeoFenceModel.getExit());
		if(mGeoFenceModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mGeoFenceModel.getUpdateTime());
		if(mGeoFenceModel.getEnable() != null)
		    values.put(COLUMN_NAME_ENABLE, mGeoFenceModel.getEnable());
		if(mGeoFenceModel.getDescription() != null)
		    values.put(COLUMN_NAME_DESCRIPTION, mGeoFenceModel.getDescription());
		values.put(COLUMN_NAME_LAT, mGeoFenceModel.getLat());
		values.put(COLUMN_NAME_LNG, mGeoFenceModel.getLng());
		values.put(COLUMN_NAME_RADIUS, mGeoFenceModel.getRadius());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_GEOFENCEID + " = ?", new String[]{wId});
		}
	}
	public void updateGeoFence(String wId,ContentValues values){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_GEOFENCEID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个围栏
	 * @param username
	 */
	public void deleteGeoFence(String wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_GEOFENCEID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 清空围栏
	 * @param username
	 */
	public void clearGeoFence(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			String sql = "DELETE FROM " + TABLE_NAME +";";
			db.execSQL(sql);		
		}
	}
	/**
	 * 保存一个围栏
	 * @param user
	 */
	public void saveGeoFence(GeoFenceModel mGeoFenceModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mGeoFenceModel.getDeviceId() != null)
		    values.put(COLUMN_NAME_DEVICEID, mGeoFenceModel.getDeviceId());
		if(mGeoFenceModel.getGeofenceID() != null)
		    values.put(COLUMN_NAME_GEOFENCEID, mGeoFenceModel.getGeofenceID());
		if(mGeoFenceModel.getFenceName() != null)
		    values.put(COLUMN_NAME_FENCENAME, mGeoFenceModel.getFenceName());
		if(mGeoFenceModel.getEntry() != null)
			values.put(COLUMN_NAME_ENTRY, mGeoFenceModel.getEntry());
		if(mGeoFenceModel.getEntry() != null)
		    values.put(COLUMN_NAME_EXIT, mGeoFenceModel.getExit());
		if(mGeoFenceModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mGeoFenceModel.getUpdateTime());
		if(mGeoFenceModel.getEnable() != null)
		    values.put(COLUMN_NAME_ENABLE, mGeoFenceModel.getEnable());
		if(mGeoFenceModel.getDescription() != null)
		    values.put(COLUMN_NAME_DESCRIPTION, mGeoFenceModel.getDescription());
		values.put(COLUMN_NAME_LAT, mGeoFenceModel.getLat());
		values.put(COLUMN_NAME_LNG, mGeoFenceModel.getLng());
		values.put(COLUMN_NAME_RADIUS, mGeoFenceModel.getRadius());
		    
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
