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
import java.util.List;

import vip.inteltech.gat.model.SMSModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SMSDao {
	public static final String TABLE_NAME = "sms";
	public static final String COLUMN_NAME_SORT = "sort";
	public static final String COLUMN_NAME_DEVICESMSID = "DeviceSMSID";
	public static final String COLUMN_NAME_DEVICEID = "DeviceID";
	public static final String COLUMN_NAME_USERID = "UserID";
	public static final String COLUMN_NAME_TYPE = "Type";
	public static final String COLUMN_NAME_STATE = "State";
	public static final String COLUMN_NAME_PHONE = "Phone";
	public static final String COLUMN_NAME_SMS = "Sms";
	public static final String COLUMN_NAME_CREATETIME = "CreateTime";
	public static final String COLUMN_NAME_UPDATETIME = "UpdateTime";
	
	
	private DbOpenHelper dbHelper;

	public SMSDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存SMSlist
	 * 
	 * @param SMSList
	 */
	public void saveSMSList(List<SMSModel> SMSList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (SMSModel mSMSModel : SMSList) {
				//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
				ContentValues values = new ContentValues();
				if(mSMSModel.getDeviceSMSID() != null)
				    values.put(COLUMN_NAME_DEVICESMSID, mSMSModel.getDeviceSMSID());
				if(mSMSModel.getDeviceID() != null)
					values.put(COLUMN_NAME_DEVICEID, mSMSModel.getDeviceID());
				if(mSMSModel.getUserID() != null)
					values.put(COLUMN_NAME_USERID, mSMSModel.getUserID());
				if(mSMSModel.getType() != null)
					values.put(COLUMN_NAME_TYPE, mSMSModel.getType());
				if(mSMSModel.getState() != null)
				    values.put(COLUMN_NAME_STATE, mSMSModel.getState());
				if(mSMSModel.getPhone() != null)
				    values.put(COLUMN_NAME_PHONE, mSMSModel.getPhone());
				if(mSMSModel.getSms() != null)
				    values.put(COLUMN_NAME_SMS, mSMSModel.getSms());
				if(mSMSModel.getCreateTime() != null)
				    values.put(COLUMN_NAME_CREATETIME, mSMSModel.getCreateTime());
				if(mSMSModel.getUpdateTime() != null)
					values.put(COLUMN_NAME_UPDATETIME, mSMSModel.getUpdateTime());
				db.replace(TABLE_NAME, null, values);
			}
			
		}
	}
	/**
	 * 获取SMSList
	 * 
	 * @return
	 */
	public List<SMSModel> getSMSList(int fId, int uId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<SMSModel> SMSs = new ArrayList<SMSModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_DEVICEID + " = ? and " + COLUMN_NAME_USERID + " = ?", new String[]{String.valueOf(fId),String.valueOf(uId)});
			while (cursor.moveToNext()) {
				int sort =  cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SORT));
				String DeviceSMSID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICESMSID));
				String DeviceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String UserID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
				String State = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STATE));
				String Phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));
				String Sms = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMS));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				SMSModel mSMSModel = new SMSModel();
				mSMSModel.setSort(sort);
				mSMSModel.setDeviceSMSID(DeviceSMSID);
				mSMSModel.setDeviceID(DeviceID);
				mSMSModel.setUserID(UserID);
				mSMSModel.setType(Type);
				mSMSModel.setState(State);
				mSMSModel.setPhone(Phone);
				mSMSModel.setSms(Sms);
				mSMSModel.setCreateTime(CreateTime);
				mSMSModel.setUpdateTime(UpdateTime);
				SMSs.add(mSMSModel);
			}
			cursor.close();
		}
		return SMSs;
	}
	/**
	 * 更新SMS
	 * @param msgId
	 * @param values
	 */
	public void updateSMS(String wId,SMSModel mSMSModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_SORT, mSMSModel.getSort());
		if(mSMSModel.getDeviceSMSID() != null)
		    values.put(COLUMN_NAME_DEVICESMSID, mSMSModel.getDeviceSMSID());
		if(mSMSModel.getDeviceID() != null)
			values.put(COLUMN_NAME_DEVICEID, mSMSModel.getDeviceID());
		if(mSMSModel.getUserID() != null)
			values.put(COLUMN_NAME_USERID, mSMSModel.getUserID());
		if(mSMSModel.getType() != null)
			values.put(COLUMN_NAME_TYPE, mSMSModel.getType());
		if(mSMSModel.getState() != null)
		    values.put(COLUMN_NAME_STATE, mSMSModel.getState());
		if(mSMSModel.getPhone() != null)
		    values.put(COLUMN_NAME_PHONE, mSMSModel.getPhone());
		if(mSMSModel.getSms() != null)
		    values.put(COLUMN_NAME_SMS, mSMSModel.getSms());
		if(mSMSModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mSMSModel.getCreateTime());
		if(mSMSModel.getUpdateTime() != null)
			values.put(COLUMN_NAME_UPDATETIME, mSMSModel.getUpdateTime());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_DEVICESMSID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个SMS
	 * @param username
	 */
	public void deleteSMS(String wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_SORT + " = ?", new String[]{wId});
		}
	}
	/**
	 * 清空SMS
	 * @param username
	 */
	public void clearChatMsg(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			String sql = "DELETE FROM " + TABLE_NAME +";";
			db.execSQL(sql);		
		}
	}
	/**
	 * 保存一个SMS
	 * @param user
	 */
	public void saveSMS(SMSModel mSMSModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mSMSModel.getDeviceSMSID() != null)
		    values.put(COLUMN_NAME_DEVICESMSID, mSMSModel.getDeviceSMSID());
		if(mSMSModel.getDeviceID() != null)
			values.put(COLUMN_NAME_DEVICEID, mSMSModel.getDeviceID());
		if(mSMSModel.getUserID() != null)
			values.put(COLUMN_NAME_USERID, mSMSModel.getUserID());
		if(mSMSModel.getType() != null)
			values.put(COLUMN_NAME_TYPE, mSMSModel.getType());
		if(mSMSModel.getState() != null)
		    values.put(COLUMN_NAME_STATE, mSMSModel.getState());
		if(mSMSModel.getPhone() != null)
		    values.put(COLUMN_NAME_PHONE, mSMSModel.getPhone());
		if(mSMSModel.getSms() != null)
		    values.put(COLUMN_NAME_SMS, mSMSModel.getSms());
		if(mSMSModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mSMSModel.getCreateTime());
		if(mSMSModel.getUpdateTime() != null)
			values.put(COLUMN_NAME_UPDATETIME, mSMSModel.getUpdateTime());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
