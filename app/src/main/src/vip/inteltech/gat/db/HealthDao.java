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

import vip.inteltech.gat.model.HealthModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HealthDao {
	public static final String TABLE_NAME = "health";
	public static final String COLUMN_NAME_ID = "wId";
	public static final String COLUMN_NAME_PEDOMETER = "Pedometer";
	public static final String COLUMN_NAME_LATITUDE = "Latitude";
	public static final String COLUMN_NAME_LONGITUDE = "Longitude";
	public static final String COLUMN_NAME_DEVICETIME = "DeviceTime";
	public static final String COLUMN_NAME_LOCATIONTYPE = "LocationType";
	public static final String COLUMN_NAME_ADDRESS = "Address";

	private DbOpenHelper dbHelper;

	public HealthDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	public HealthModel getHealth(int Id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		HealthModel mHealthModel = new HealthModel();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(Id)});
			//db.query(TABLE_NAME, new String[]{}, "wId = ?", new String[]{wId}, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				int wId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				String pedometer = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PEDOMETER));
				double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LATITUDE));
				double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_LONGITUDE));
				String deviceTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETIME));
				String locationType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCATIONTYPE));
				String address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));

				mHealthModel.setDeviceId(wId);
				mHealthModel.setPedometer(pedometer);
				mHealthModel.setLatitude(latitude);
				mHealthModel.setLongitude(longitude);
				mHealthModel.setDeviceTime(deviceTime);
				mHealthModel.setLocationType(locationType);
				mHealthModel.setAddress(address);
			}
			cursor.close();
		}
		return mHealthModel;
	}

	public void updateHealth(int wId, HealthModel mHealthModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, wId);
		if(mHealthModel.getPedometer() != null)
			values.put(COLUMN_NAME_PEDOMETER, mHealthModel.getPedometer());
		if(mHealthModel.getLatitude() != 0)
		    values.put(COLUMN_NAME_LATITUDE, mHealthModel.getLatitude());
		if(mHealthModel.getLongitude() != 0)
		    values.put(COLUMN_NAME_LONGITUDE, mHealthModel.getLongitude());
		if(mHealthModel.getDeviceTime() != null)
		    values.put(COLUMN_NAME_DEVICETIME, mHealthModel.getDeviceTime());
		if(mHealthModel.getLocationType() != null)
		    values.put(COLUMN_NAME_LOCATIONTYPE, mHealthModel.getLocationType());
		if(mHealthModel.getAddress() != null)
		    values.put(COLUMN_NAME_ADDRESS, mHealthModel.getAddress());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(wId)});
		}
	}

	public void saveHealth(HealthModel mHealthModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mHealthModel.getDeviceId());
		if(mHealthModel.getPedometer() != null)
			values.put(COLUMN_NAME_PEDOMETER, mHealthModel.getPedometer());
		if(mHealthModel.getLatitude() != 0)
		    values.put(COLUMN_NAME_LATITUDE, mHealthModel.getLatitude());
		if(mHealthModel.getLongitude() != 0)
		    values.put(COLUMN_NAME_LONGITUDE, mHealthModel.getLongitude());
		if(mHealthModel.getDeviceTime() != null)
		    values.put(COLUMN_NAME_DEVICETIME, mHealthModel.getDeviceTime());
		if(mHealthModel.getLocationType() != null)
		    values.put(COLUMN_NAME_LOCATIONTYPE, mHealthModel.getLocationType());
		if(mHealthModel.getAddress() != null)
		    values.put(COLUMN_NAME_ADDRESS, mHealthModel.getAddress());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}