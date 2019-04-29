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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import vip.inteltech.gat.model.AlbumModel;

public class AlbumDao {
	public static final String TABLE_NAME = "album";
	public static final String COLUMN_NAME_DEVICEPHOTOID = "DevicePhotoId";
	public static final String COLUMN_NAME_DEVICEID = "DeviceID";
	public static final String COLUMN_NAME_USERID= "UserID";
	public static final String COLUMN_NAME_SOURCE = "Source";
	public static final String COLUMN_NAME_DEVICETIME = "DeviceTime";
	public static final String COLUMN_NAME_LATITUDE = "Latitude";
	public static final String COLUMN_NAME_LONGITUDE = "Longitude";
	public static final String COLUMN_NAME_ADDRESS = "Address";
	public static final String COLUMN_NAME_MARK = "Mark";
	public static final String COLUMN_NAME_PATH = "Path";
	public static final String COLUMN_NAME_THUMB = "Thumb";
	public static final String COLUMN_NAME_LOCAL = "Local";
	public static final String COLUMN_NAME_LENGTH = "Length";
	public static final String COLUMN_NAME_CREATETIME = "CreateTime";
	public static final String COLUMN_NAME_UPDATETIME = "UpdateTime";
	
	private DbOpenHelper dbHelper;

	public AlbumDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存相册list
	 * 
	 * @param AlbumList
	 */
	public void saveAlbumList(List<AlbumModel> AlbumList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			//db.delete(TABLE_NAME, null, null);
			for (AlbumModel mAlbumModel : AlbumList) {
				ContentValues values = new ContentValues();
				if(mAlbumModel.getDevicePhotoId() != null)
					values.put(COLUMN_NAME_DEVICEPHOTOID, mAlbumModel.getDevicePhotoId());
				values.put(COLUMN_NAME_DEVICEID, mAlbumModel.getDeviceID());
				values.put(COLUMN_NAME_USERID, mAlbumModel.getUserID());
				if(mAlbumModel.getSource() != null)
					values.put(COLUMN_NAME_SOURCE, mAlbumModel.getSource());
				if(mAlbumModel.getDeviceTime() != null)
					values.put(COLUMN_NAME_DEVICETIME, mAlbumModel.getDeviceTime());
				values.put(COLUMN_NAME_LATITUDE, mAlbumModel.getLatitude());
				values.put(COLUMN_NAME_LONGITUDE, mAlbumModel.getLongitude());
				if(mAlbumModel.getAddress() != null)
				    values.put(COLUMN_NAME_ADDRESS, mAlbumModel.getAddress());
				if(mAlbumModel.getMark() != null)
				    values.put(COLUMN_NAME_MARK, mAlbumModel.getMark());
				if(mAlbumModel.getPath() != null)
				    values.put(COLUMN_NAME_PATH, mAlbumModel.getPath());
                if(mAlbumModel.getPath() != null)
                    values.put(COLUMN_NAME_THUMB, mAlbumModel.getThumb());
				if(mAlbumModel.getLocal() != null)
				    values.put(COLUMN_NAME_LOCAL, mAlbumModel.getLocal());
				if(mAlbumModel.getLength() != null)
				    values.put(COLUMN_NAME_LENGTH, mAlbumModel.getLength());
				if(mAlbumModel.getCreateTime() != null)
				    values.put(COLUMN_NAME_CREATETIME, mAlbumModel.getCreateTime());
				if(mAlbumModel.getUpdateTime() != null)
				    values.put(COLUMN_NAME_UPDATETIME, mAlbumModel.getUpdateTime());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	/**
	 * 获取相册Map
	 * 
	 * @return
	 */
	public Map<String, AlbumModel> getAlbumMap(int fId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, AlbumModel> Albums = new HashMap<String, AlbumModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_DEVICEID + " = ?", new String[]{String.valueOf(fId)});
			while (cursor.moveToNext()) {
				String DevicePhotoId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEPHOTOID));
				int DeviceID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				int UserID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Source = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOURCE));
				String DeviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				double Latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double Longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String Address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));
				String Mark = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARK));
				String Path = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PATH));
				String Thumb = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_THUMB));
				String Local = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCAL));
				String Length = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LENGTH));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				
				AlbumModel mAlbumModel = new AlbumModel();
				mAlbumModel.setDevicePhotoId(DevicePhotoId);
				mAlbumModel.setDeviceID(DeviceID);
				mAlbumModel.setUserID(UserID);
				mAlbumModel.setSource(Source);
				mAlbumModel.setDeviceTime(DeviceTime);
				mAlbumModel.setLatitude(Latitude);
				mAlbumModel.setLongitude(Longitude);
				mAlbumModel.setAddress(Address);
				mAlbumModel.setMark(Mark);
				mAlbumModel.setPath(Path);
				mAlbumModel.setThumb(Thumb);
				mAlbumModel.setLocal(Local);
				mAlbumModel.setLength(Length);
				mAlbumModel.setCreateTime(CreateTime);
				mAlbumModel.setUpdateTime(UpdateTime);
				Albums.put(DevicePhotoId, mAlbumModel);
			}
			cursor.close();
		}
		return Albums;
	}
	/**
	 * 获取相册List
	 * 
	 * @return
	 */
	public List<AlbumModel> getAlbumList(int fId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<AlbumModel> Albums = new ArrayList<AlbumModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_DEVICEID + " = ?", new String[]{String.valueOf(fId)});
			while (cursor.moveToNext()) {
				String DevicePhotoId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEPHOTOID));
				int DeviceID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				int UserID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Source = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOURCE));
				String DeviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				double Latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double Longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String Address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));
				String Mark = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARK));
				String Path = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PATH));
                String Thumb = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_THUMB));
				String Local = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCAL));
				String Length = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LENGTH));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				
				AlbumModel mAlbumModel = new AlbumModel();
				mAlbumModel.setDevicePhotoId(DevicePhotoId);
				mAlbumModel.setDeviceID(DeviceID);
				mAlbumModel.setUserID(UserID);
				mAlbumModel.setSource(Source);
				mAlbumModel.setDeviceTime(DeviceTime);
				mAlbumModel.setLatitude(Latitude);
				mAlbumModel.setLongitude(Longitude);
				mAlbumModel.setAddress(Address);
				mAlbumModel.setMark(Mark);
				mAlbumModel.setPath(Path);
				mAlbumModel.setThumb(Thumb);
				mAlbumModel.setLocal(Local);
				mAlbumModel.setLength(Length);
				mAlbumModel.setCreateTime(CreateTime);
				mAlbumModel.setUpdateTime(UpdateTime);
				Albums.add(0, mAlbumModel);
			}
			cursor.close();
		}
		return Albums;
	}
	/**
	 * 获取相册List
	 * 
	 * @return
	 */
	public List<AlbumModel> getAlbumList(int deviceId, int userId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<AlbumModel> Albums = new ArrayList<AlbumModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_DEVICEID + " = ?  and " + COLUMN_NAME_USERID +" = ?", new String[]{String.valueOf(deviceId), String.valueOf(userId)});
			while (cursor.moveToNext()) {
				String DevicePhotoId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEPHOTOID));
				int DeviceID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				int UserID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Source = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOURCE));
				String DeviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				double Latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double Longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String Address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));
				String Mark = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARK));
				String Path = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PATH));
                String Thumb = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_THUMB));
				String Local = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCAL));
				String Length = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LENGTH));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				
				AlbumModel mAlbumModel = new AlbumModel();
				mAlbumModel.setDevicePhotoId(DevicePhotoId);
				mAlbumModel.setDeviceID(DeviceID);
				mAlbumModel.setUserID(UserID);
				mAlbumModel.setSource(Source);
				mAlbumModel.setDeviceTime(DeviceTime);
				mAlbumModel.setLatitude(Latitude);
				mAlbumModel.setLongitude(Longitude);
				mAlbumModel.setAddress(Address);
				mAlbumModel.setMark(Mark);
				mAlbumModel.setPath(Path);
				mAlbumModel.setThumb(Thumb);
				mAlbumModel.setLocal(Local);
				mAlbumModel.setLength(Length);
				mAlbumModel.setCreateTime(CreateTime);
				mAlbumModel.setUpdateTime(UpdateTime);
				Albums.add(0, mAlbumModel);
			}
			cursor.close();
		}
		return Albums;
	}
	public AlbumModel getAlbum(int Id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		AlbumModel mAlbumModel = new AlbumModel();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_DEVICEPHOTOID + " = ?", new String[]{String.valueOf(Id)});
			//db.query(TABLE_NAME, new String[]{}, "wId = ?", new String[]{wId}, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				String DevicePhotoId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEPHOTOID));
				int DeviceID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				int UserID = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Source = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOURCE));
				String DeviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				double Latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double Longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String Address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));
				String Mark = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARK));
				String Path = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PATH));
                String Thumb = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_THUMB));
				String Local = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCAL));
				String Length = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LENGTH));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				
				mAlbumModel.setDevicePhotoId(DevicePhotoId);
				mAlbumModel.setDeviceID(DeviceID);
				mAlbumModel.setUserID(UserID);
				mAlbumModel.setSource(Source);
				mAlbumModel.setDeviceTime(DeviceTime);
				mAlbumModel.setLatitude(Latitude);
				mAlbumModel.setLongitude(Longitude);
				mAlbumModel.setAddress(Address);
				mAlbumModel.setMark(Mark);
				mAlbumModel.setPath(Path);
				mAlbumModel.setThumb(Thumb);
				mAlbumModel.setLocal(Local);
				mAlbumModel.setLength(Length);
				mAlbumModel.setCreateTime(CreateTime);
				mAlbumModel.setUpdateTime(UpdateTime);
			}
			//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
			cursor.close();
		}
		return mAlbumModel;
		
	}
	/**
	 * 更新相册
	 * @param wId
	 * @param values
	 */
	public void updateAlbum(String wId,ContentValues values){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_DEVICEPHOTOID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 更新相册
	 * @param wId
	 * @param mAlbumModel
	 */
	public void updateAlbum(String wId,AlbumModel mAlbumModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mAlbumModel.getDevicePhotoId() != null)
			values.put(COLUMN_NAME_DEVICEPHOTOID, mAlbumModel.getDevicePhotoId());
		values.put(COLUMN_NAME_DEVICEID, mAlbumModel.getDeviceID());
		values.put(COLUMN_NAME_USERID, mAlbumModel.getUserID());
		if(mAlbumModel.getSource() != null)
			values.put(COLUMN_NAME_SOURCE, mAlbumModel.getSource());
		if(mAlbumModel.getDeviceTime() != null)
			values.put(COLUMN_NAME_DEVICETIME, mAlbumModel.getDeviceTime());
		values.put(COLUMN_NAME_LATITUDE, mAlbumModel.getLatitude());
		values.put(COLUMN_NAME_LONGITUDE, mAlbumModel.getLongitude());
		if(mAlbumModel.getAddress() != null)
		    values.put(COLUMN_NAME_ADDRESS, mAlbumModel.getAddress());
		if(mAlbumModel.getMark() != null)
		    values.put(COLUMN_NAME_MARK, mAlbumModel.getMark());
		if(mAlbumModel.getPath() != null)
		    values.put(COLUMN_NAME_PATH, mAlbumModel.getPath());
		if(mAlbumModel.getThumb()!=null){
		    values.put(COLUMN_NAME_THUMB,mAlbumModel.getThumb());
        }
		if(mAlbumModel.getLocal() != null)
		    values.put(COLUMN_NAME_LOCAL, mAlbumModel.getLocal());
		if(mAlbumModel.getLength() != null)
		    values.put(COLUMN_NAME_LENGTH, mAlbumModel.getLength());
		if(mAlbumModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mAlbumModel.getCreateTime());
		if(mAlbumModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mAlbumModel.getUpdateTime());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_DEVICEPHOTOID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个相册
	 * @param wId
	 */
	public void deleteAlbum(String wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_DEVICEPHOTOID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 保存一个相册
	 * @param mAlbumModel
	 */
	public void saveAlbum(AlbumModel mAlbumModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mAlbumModel.getDevicePhotoId() != null)
			values.put(COLUMN_NAME_DEVICEPHOTOID, mAlbumModel.getDevicePhotoId());
		values.put(COLUMN_NAME_DEVICEID, mAlbumModel.getDeviceID());
		values.put(COLUMN_NAME_USERID, mAlbumModel.getUserID());
		if(mAlbumModel.getSource() != null)
			values.put(COLUMN_NAME_SOURCE, mAlbumModel.getSource());
		if(mAlbumModel.getDeviceTime() != null)
			values.put(COLUMN_NAME_DEVICETIME, mAlbumModel.getDeviceTime());
		values.put(COLUMN_NAME_LATITUDE, mAlbumModel.getLatitude());
		values.put(COLUMN_NAME_LONGITUDE, mAlbumModel.getLongitude());
		if(mAlbumModel.getAddress() != null)
		    values.put(COLUMN_NAME_ADDRESS, mAlbumModel.getAddress());
		if(mAlbumModel.getMark() != null)
		    values.put(COLUMN_NAME_MARK, mAlbumModel.getMark());
		if(mAlbumModel.getPath() != null)
		    values.put(COLUMN_NAME_PATH, mAlbumModel.getPath());
		if(mAlbumModel.getThumb()!=null){
		    values.put(COLUMN_NAME_THUMB,mAlbumModel.getThumb());
        }
		if(mAlbumModel.getLocal() != null)
		    values.put(COLUMN_NAME_LOCAL, mAlbumModel.getLocal());
		if(mAlbumModel.getLength() != null)
		    values.put(COLUMN_NAME_LENGTH, mAlbumModel.getLength());
		if(mAlbumModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mAlbumModel.getCreateTime());
		if(mAlbumModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mAlbumModel.getUpdateTime());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
