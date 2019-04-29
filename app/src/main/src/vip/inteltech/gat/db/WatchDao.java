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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vip.inteltech.gat.model.WatchModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WatchDao {
	public static final String TABLE_NAME = "watchs";
	public static final String COLUMN_NAME_ID = "wId";
	public static final String COLUMN_NAME_USERID = "userId";
	public static final String COLUMN_NAME_MODEL = "model";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_NAME_PHONE = "phone";
	public static final String COLUMN_NAME_CORNET = "cornet";
	public static final String COLUMN_NAME_GENDER = "gender";
	public static final String COLUMN_NAME_BIRTHDAY = "birthday";
	public static final String COLUMN_NAME_GRADE = "grade";
	public static final String COLUMN_NAME_SCHOOLADRESS = "schoolAddress";
	public static final String COLUMN_NAME_SCHOOLLAT = "schoolLat";
	public static final String COLUMN_NAME_SCHOOLLNG = "schoolLng";
	public static final String COLUMN_NAME_HOMEADRESS = "homeAddress";
	public static final String COLUMN_NAME_HOMELAT = "homeLat";
	public static final String COLUMN_NAME_HOMELNG = "homeLng";
	public static final String COLUMN_NAME_LASTESTTIME = "lastestTime";

	public static final String COLUMN_NAME_SETVERSIONNO = "setVersionNO";
	public static final String COLUMN_NAME_CONTACTVERSIONNO = "contactVersionNO";
	public static final String COLUMN_NAME_OPERATORTYPE = "operatorType";
	public static final String COLUMN_NAME_SMSNUMBER = "smsNumber";
	public static final String COLUMN_NAME_SMSBALANCEKEY = "smsBalanceKey";
	public static final String COLUMN_NAME_SMSFLOWKEY = "smsFlowKey";

	public static final String COLUMN_NAME_ACTIVEDATE = "activeDate";
	public static final String COLUMN_NAME_CREATETIME = "createTime";
	public static final String COLUMN_NAME_BINDNUMBER = "bindNumber";
	public static final String COLUMN_NAME_CURENTFIRMWARE = "currentFirmware";
	public static final String COLUMN_NAME_FIRMWARE = "firmware";
	public static final String COLUMN_NAME_HIREEXPIREDATE = "hireExpireDate";
	public static final String COLUMN_NAME_HIRESTARTDATE = "hireStartDate";
	public static final String COLUMN_NAME_UPDATETIME = "updateTime";
	public static final String COLUMN_NAME_SERIALNUMBER = "serialNumber";
	public static final String COLUMN_NAME_PASSWORD = "password";
	public static final String COLUMN_NAME_ISGUARD = "isGuard";
	public static final String COLUMN_NAME_DEVICETYPE = "deviceType";
	public static final String COLUMN_NAME_CLOUDPLATFORM = "cloudPlatform";

	private DbOpenHelper dbHelper;

	public WatchDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存手表list
	 * 
	 * @param WatchList
	 */
	public void saveWatchList(List<WatchModel> WatchList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (WatchModel mWatchModel : WatchList) {
				//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, mWatchModel.getId());
				if(mWatchModel.getUserId() != 0)
				    values.put(COLUMN_NAME_USERID, mWatchModel.getUserId());
				if(mWatchModel.getModel() != null)
				    values.put(COLUMN_NAME_MODEL, mWatchModel.getModel());
				if(mWatchModel.getName() != null)
					values.put(COLUMN_NAME_NAME, mWatchModel.getName());
				if(mWatchModel.getAvatar() != null)
				    values.put(COLUMN_NAME_AVATAR, mWatchModel.getAvatar());
				if(mWatchModel.getPhone() != null)
				    values.put(COLUMN_NAME_PHONE, mWatchModel.getPhone());
				if(mWatchModel.getCornet() != null)
				    values.put(COLUMN_NAME_CORNET, mWatchModel.getCornet());
				if(mWatchModel.getGender() !=null)
					values.put(COLUMN_NAME_GENDER,mWatchModel.getGender());
				if(mWatchModel.getBirthday() != null)
				    values.put(COLUMN_NAME_BIRTHDAY, mWatchModel.getBirthday());
				values.put(COLUMN_NAME_GRADE, mWatchModel.getGrade());
				if(mWatchModel.getSchoolAddress() != null)
				    values.put(COLUMN_NAME_SCHOOLADRESS, mWatchModel.getSchoolAddress());
				if(mWatchModel.getSchoolLat() != 0)
				    values.put(COLUMN_NAME_SCHOOLLAT, mWatchModel.getSchoolLat());
				if(mWatchModel.getSchoolLng() != 0)
				    values.put(COLUMN_NAME_SCHOOLLNG, mWatchModel.getSchoolLng());
				if(mWatchModel.getHomeAddress() != null)
				    values.put(COLUMN_NAME_HOMEADRESS, mWatchModel.getHomeAddress());
				if(mWatchModel.getHomeLat() != 0)
				    values.put(COLUMN_NAME_HOMELAT, mWatchModel.getHomeLat());
				if(mWatchModel.getHomeLng() != 0)
				    values.put(COLUMN_NAME_HOMELNG, mWatchModel.getHomeLng());
				if(mWatchModel.getLastestTime() != null)
				    values.put(COLUMN_NAME_LASTESTTIME, mWatchModel.getLastestTime());
				if(mWatchModel.getSetVersionNO() != null)
				    values.put(COLUMN_NAME_SETVERSIONNO, mWatchModel.getSetVersionNO());
				if(mWatchModel.getContactVersionNO() != null)
				    values.put(COLUMN_NAME_CONTACTVERSIONNO, mWatchModel.getContactVersionNO());

				if(mWatchModel.getOperatorType() != null)
				    values.put(COLUMN_NAME_OPERATORTYPE, mWatchModel.getOperatorType());
				if(mWatchModel.getSmsNumber() != null)
				    values.put(COLUMN_NAME_SMSNUMBER, mWatchModel.getSmsNumber());
				if(mWatchModel.getSmsBalanceKey() != null)
				    values.put(COLUMN_NAME_SMSBALANCEKEY, mWatchModel.getSmsBalanceKey());
				if(mWatchModel.getSmsFlowKey() != null)
				    values.put(COLUMN_NAME_SMSFLOWKEY, mWatchModel.getSmsFlowKey());

				if(mWatchModel.getActiveDate() != null)
				    values.put(COLUMN_NAME_ACTIVEDATE, mWatchModel.getActiveDate());
				if(mWatchModel.getCreateTime() != null)
				    values.put(COLUMN_NAME_CREATETIME, mWatchModel.getCreateTime());
				if(mWatchModel.getBindNumber() != null)
				    values.put(COLUMN_NAME_BINDNUMBER, mWatchModel.getBindNumber());
				if(mWatchModel.getCurrentFirmware() != null)
				    values.put(COLUMN_NAME_CURENTFIRMWARE, mWatchModel.getCurrentFirmware());
				if(mWatchModel.getFirmware() != null)
				    values.put(COLUMN_NAME_FIRMWARE, mWatchModel.getFirmware());
				if(mWatchModel.getHireExpireDate() != null)
				    values.put(COLUMN_NAME_HIREEXPIREDATE, mWatchModel.getHireExpireDate());
				if(mWatchModel.getHireStartDate() != null)
				    values.put(COLUMN_NAME_HIRESTARTDATE, mWatchModel.getHireStartDate());
				if(mWatchModel.getUpdateTime() != null)
				    values.put(COLUMN_NAME_UPDATETIME, mWatchModel.getUpdateTime());
				if(mWatchModel.getSerialNumber() != null)
				    values.put(COLUMN_NAME_SERIALNUMBER, mWatchModel.getSerialNumber());
				if(mWatchModel.getPassword() != null)
				    values.put(COLUMN_NAME_PASSWORD, mWatchModel.getPassword());
				values.put(COLUMN_NAME_ISGUARD,mWatchModel.isIsGuard()?"1":"0");
				if(mWatchModel.getDeviceType() != null)
				    values.put(COLUMN_NAME_DEVICETYPE, mWatchModel.getDeviceType());
				if(mWatchModel.getCloudPlatform()>=0){
				    values.put(COLUMN_NAME_CLOUDPLATFORM,mWatchModel.getCloudPlatform());
                }
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	/**
	 * 获取手表map
	 * 
	 * @return
	 */
	public Map<String, WatchModel> getWatchMap() {
		//System.out.println("getWatchMap()");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, WatchModel> users = new HashMap<String, WatchModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				int wId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String model = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MODEL));
				String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));
				String cornet = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CORNET));
				String gender = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GENDER));
				String birthday = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BIRTHDAY));
				int grade = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_GRADE));
				String schoolAddress = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SCHOOLADRESS));
				double schoolLat =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_SCHOOLLAT));
				double schoolLng =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_SCHOOLLNG));
				String homeAddress = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOMEADRESS));
				double homeLat =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_HOMELAT));
				double homeLng =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_HOMELNG));
				String lastestTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LASTESTTIME));
				String operatorType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OPERATORTYPE));
				String setVersionNO = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SETVERSIONNO));
				String contactVersionNO = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTACTVERSIONNO));
				String smsNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMSNUMBER));
				String smsBalanceKey = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMSBALANCEKEY));
				String smsFlowKey = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMSFLOWKEY));
				String activeDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ACTIVEDATE));
				String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String bindNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BINDNUMBER));
				String currentFirmware = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CURENTFIRMWARE));
				String firmware = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FIRMWARE));
				String hireExpireDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HIREEXPIREDATE));
				String hireStartDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HIRESTARTDATE));
				String updateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String serialNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SERIALNUMBER));
				String password = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PASSWORD));
				String isGuard = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ISGUARD));
				String deviceType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETYPE));
				int platform = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_CLOUDPLATFORM));
				WatchModel mWatchModel = new WatchModel();
				mWatchModel.setId(wId);
				mWatchModel.setUserId(userId);
				mWatchModel.setModel(model);
				mWatchModel.setName(name);
				mWatchModel.setAvatar(avatar);
				mWatchModel.setPhone(phone);
				mWatchModel.setCornet(cornet);
				mWatchModel.setGender(gender);
				mWatchModel.setBirthday(birthday);
				mWatchModel.setGrade(grade);
				mWatchModel.setSchoolAddress(schoolAddress);
				mWatchModel.setSchoolLat(schoolLat);
				mWatchModel.setSchoolLng(schoolLng);
				mWatchModel.setHomeAddress(homeAddress);
				mWatchModel.setHomeLat(homeLat);
				mWatchModel.setHomeLng(homeLng);
				mWatchModel.setLastestTime(lastestTime);
				mWatchModel.setSetVersionNO(setVersionNO);
				mWatchModel.setContactVersionNO(contactVersionNO);
				mWatchModel.setOperatorType(operatorType);
				mWatchModel.setSmsNumber(smsNumber);
				mWatchModel.setSmsBalanceKey(smsBalanceKey);
				mWatchModel.setSmsFlowKey(smsFlowKey);
				mWatchModel.setActiveDate(activeDate);
				mWatchModel.setCreateTime(createTime);
				mWatchModel.setBindNumber(bindNumber);
				mWatchModel.setCurrentFirmware(currentFirmware);
				mWatchModel.setFirmware(firmware);
				mWatchModel.setHireExpireDate(hireExpireDate);
				mWatchModel.setHireStartDate(hireStartDate);
				mWatchModel.setUpdateTime(updateTime);
				mWatchModel.setSerialNumber(serialNumber);
				mWatchModel.setPassword(password);
				mWatchModel.setIsGuard(isGuard.equals("1")?true:false);
				mWatchModel.setDeviceType(deviceType);
				mWatchModel.setCloudPlatform(platform);
				users.put(String.valueOf(wId), mWatchModel);
			}
			cursor.close();
		}
		return users;
	}
	public WatchModel getWatch(int Id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		WatchModel mWatchModel = new WatchModel();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(Id)});
			//db.query(TABLE_NAME, new String[]{}, "wId = ?", new String[]{wId}, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				int wId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String model = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MODEL));
				String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));
				String cornet = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CORNET));
				String gender = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GENDER));
				String birthday = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BIRTHDAY));
				int grade = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_GRADE));
				String schoolAddress = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SCHOOLADRESS));
				double schoolLat =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_SCHOOLLAT));
				double schoolLng =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_SCHOOLLNG));
				String homeAddress = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOMEADRESS));
				double homeLat =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_HOMELAT));
				double homeLng =  cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_HOMELNG));
				String lastestTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LASTESTTIME));
				String setVersionNO = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SETVERSIONNO));
				String contactVersionNO = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTACTVERSIONNO));
				String operatorType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OPERATORTYPE));
				String smsNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMSNUMBER));
				String smsBalanceKey = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMSBALANCEKEY));
				String smsFlowKey = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SMSFLOWKEY));
				String activeDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ACTIVEDATE));
				String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String bindNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BINDNUMBER));
				String currentFirmware = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CURENTFIRMWARE));
				String firmware = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FIRMWARE));
				String hireExpireDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HIREEXPIREDATE));
				String hireStartDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HIRESTARTDATE));
				String updateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String serialNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SERIALNUMBER));
				String password = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PASSWORD));
				String isGuard = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ISGUARD));
				String deviceType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICETYPE));
                int platform = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_CLOUDPLATFORM));
				mWatchModel.setId(wId);
				mWatchModel.setUserId(userId);
				mWatchModel.setName(name);
				mWatchModel.setModel(model);
				mWatchModel.setAvatar(avatar);
				mWatchModel.setPhone(phone);
				mWatchModel.setCornet(cornet);
				mWatchModel.setGender(gender);
				mWatchModel.setBirthday(birthday);
				mWatchModel.setGrade(grade);
				mWatchModel.setSchoolAddress(schoolAddress);
				mWatchModel.setSchoolLat(schoolLat);
				mWatchModel.setSchoolLng(schoolLng);
				mWatchModel.setHomeAddress(homeAddress);
				mWatchModel.setHomeLat(homeLat);
				mWatchModel.setHomeLng(homeLng);
				mWatchModel.setLastestTime(lastestTime);
				mWatchModel.setSetVersionNO(setVersionNO);
				mWatchModel.setContactVersionNO(contactVersionNO);
				mWatchModel.setOperatorType(operatorType);
				mWatchModel.setSmsNumber(smsNumber);
				mWatchModel.setSmsBalanceKey(smsBalanceKey);
				mWatchModel.setSmsFlowKey(smsFlowKey);
				mWatchModel.setActiveDate(activeDate);
				mWatchModel.setCreateTime(createTime);
				mWatchModel.setBindNumber(bindNumber);
				mWatchModel.setCurrentFirmware(currentFirmware);
				mWatchModel.setFirmware(firmware);
				mWatchModel.setHireExpireDate(hireExpireDate);
				mWatchModel.setHireStartDate(hireStartDate);
				mWatchModel.setUpdateTime(updateTime);
				mWatchModel.setSerialNumber(serialNumber);
				mWatchModel.setPassword(password);
				mWatchModel.setIsGuard("1".equals(isGuard));
				mWatchModel.setDeviceType(deviceType);
                mWatchModel.setCloudPlatform(platform);
			}
			//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
			cursor.close();
		}
		return mWatchModel;
	}
	/**
	 * 更新手表
	 * @param msgId
	 * @param values
	 */
	public void updateWatch(int wId,WatchModel mWatchModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mWatchModel.getUserId() != 0)
			values.put(COLUMN_NAME_USERID, mWatchModel.getUserId());
		if(mWatchModel.getModel() != null)
		    values.put(COLUMN_NAME_MODEL, mWatchModel.getModel());
		if(mWatchModel.getName() != null)
			values.put(COLUMN_NAME_NAME, mWatchModel.getName());
		if(mWatchModel.getAvatar() != null)
		    values.put(COLUMN_NAME_AVATAR, mWatchModel.getAvatar());
		if(mWatchModel.getPhone() != null)
		    values.put(COLUMN_NAME_PHONE, mWatchModel.getPhone());
		if(mWatchModel.getCornet() != null)
		    values.put(COLUMN_NAME_CORNET, mWatchModel.getCornet());
		if(mWatchModel.getGender() !=null)
			values.put(COLUMN_NAME_GENDER,mWatchModel.getGender());
		if(mWatchModel.getBirthday() != null)
		    values.put(COLUMN_NAME_BIRTHDAY, mWatchModel.getBirthday());
		values.put(COLUMN_NAME_GRADE, mWatchModel.getGrade());
		if(mWatchModel.getSchoolAddress() != null)
		    values.put(COLUMN_NAME_SCHOOLADRESS, mWatchModel.getSchoolAddress());
		if(mWatchModel.getSchoolLat() != 0)
		    values.put(COLUMN_NAME_SCHOOLLAT, mWatchModel.getSchoolLat());
		if(mWatchModel.getSchoolLng() != 0)
		    values.put(COLUMN_NAME_SCHOOLLNG, mWatchModel.getSchoolLng());
		if(mWatchModel.getHomeAddress() != null)
		    values.put(COLUMN_NAME_HOMEADRESS, mWatchModel.getHomeAddress());
		if(mWatchModel.getHomeLat() != 0)
		    values.put(COLUMN_NAME_HOMELAT, mWatchModel.getHomeLat());
		if(mWatchModel.getHomeLng() != 0)
		    values.put(COLUMN_NAME_HOMELNG, mWatchModel.getHomeLng());
		if(mWatchModel.getLastestTime() != null)
		    values.put(COLUMN_NAME_LASTESTTIME, mWatchModel.getLastestTime());
		if(mWatchModel.getSetVersionNO() != null)
		    values.put(COLUMN_NAME_SETVERSIONNO, mWatchModel.getSetVersionNO());
		if(mWatchModel.getContactVersionNO() != null)
		    values.put(COLUMN_NAME_CONTACTVERSIONNO, mWatchModel.getContactVersionNO());
		if(mWatchModel.getOperatorType() != null)
		    values.put(COLUMN_NAME_OPERATORTYPE, mWatchModel.getOperatorType());
		if(mWatchModel.getSmsNumber() != null)
		    values.put(COLUMN_NAME_SMSNUMBER, mWatchModel.getSmsNumber());
		if(mWatchModel.getSmsBalanceKey() != null)
		    values.put(COLUMN_NAME_SMSBALANCEKEY, mWatchModel.getSmsBalanceKey());
		if(mWatchModel.getSmsFlowKey() != null)
		    values.put(COLUMN_NAME_SMSFLOWKEY, mWatchModel.getSmsFlowKey());
		if(mWatchModel.getActiveDate() != null)
		    values.put(COLUMN_NAME_ACTIVEDATE, mWatchModel.getActiveDate());
		if(mWatchModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mWatchModel.getCreateTime());
		if(mWatchModel.getBindNumber() != null)
		    values.put(COLUMN_NAME_BINDNUMBER, mWatchModel.getBindNumber());
		if(mWatchModel.getCurrentFirmware() != null)
		    values.put(COLUMN_NAME_CURENTFIRMWARE, mWatchModel.getCurrentFirmware());
		if(mWatchModel.getFirmware() != null)
		    values.put(COLUMN_NAME_FIRMWARE, mWatchModel.getFirmware());
		if(mWatchModel.getHireExpireDate() != null)
		    values.put(COLUMN_NAME_HIREEXPIREDATE, mWatchModel.getHireExpireDate());
		if(mWatchModel.getHireStartDate() != null)
		    values.put(COLUMN_NAME_HIRESTARTDATE, mWatchModel.getHireStartDate());
		if(mWatchModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mWatchModel.getUpdateTime());
		if(mWatchModel.getSerialNumber() != null)
		    values.put(COLUMN_NAME_SERIALNUMBER, mWatchModel.getSerialNumber());
		if(mWatchModel.getPassword() != null)
		    values.put(COLUMN_NAME_PASSWORD, mWatchModel.getPassword());
		values.put(COLUMN_NAME_ISGUARD,mWatchModel.isIsGuard()?"1":"0");
		if(mWatchModel.getDeviceType() != null)
		    values.put(COLUMN_NAME_DEVICETYPE, mWatchModel.getDeviceType());
        if(mWatchModel.getCloudPlatform()>=0){
            values.put(COLUMN_NAME_CLOUDPLATFORM,mWatchModel.getCloudPlatform());
        }
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(wId)});
		}
	}
	public void updateWatch(int wId,ContentValues values){
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(wId)});
		}
	}
	/**
	 * 删除一个手表
	 * @param username
	 */
	public void deleteWatch(int wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(wId)});
		}
	}

	/**
	 * 保存一个手表
	 * @param user
	 */
	public void saveWatch(WatchModel mWatchModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mWatchModel.getId());
		if(mWatchModel.getUserId() != 0)
			values.put(COLUMN_NAME_USERID, mWatchModel.getUserId());
		if(mWatchModel.getModel() != null)
		    values.put(COLUMN_NAME_MODEL, mWatchModel.getModel());
		if(mWatchModel.getName() != null)
			values.put(COLUMN_NAME_NAME, mWatchModel.getName());
		if(mWatchModel.getAvatar() != null)
		    values.put(COLUMN_NAME_AVATAR, mWatchModel.getAvatar());
		if(mWatchModel.getPhone() != null)
		    values.put(COLUMN_NAME_PHONE, mWatchModel.getPhone());
		if(mWatchModel.getCornet() != null)
		    values.put(COLUMN_NAME_CORNET, mWatchModel.getCornet());
		if(mWatchModel.getGender() !=null)
			values.put(COLUMN_NAME_GENDER,mWatchModel.getGender());
		if(mWatchModel.getBirthday() != null)
		    values.put(COLUMN_NAME_BIRTHDAY, mWatchModel.getBirthday());
		values.put(COLUMN_NAME_GRADE, mWatchModel.getGrade());
		if(mWatchModel.getSchoolAddress() != null)
		    values.put(COLUMN_NAME_SCHOOLADRESS, mWatchModel.getSchoolAddress());
		if(mWatchModel.getSchoolLat() != 0)
		    values.put(COLUMN_NAME_SCHOOLLAT, mWatchModel.getSchoolLat());
		if(mWatchModel.getSchoolLng() != 0)
		    values.put(COLUMN_NAME_SCHOOLLNG, mWatchModel.getSchoolLng());
		if(mWatchModel.getHomeAddress() != null)
		    values.put(COLUMN_NAME_HOMEADRESS, mWatchModel.getHomeAddress());
		if(mWatchModel.getHomeLat() != 0)
		    values.put(COLUMN_NAME_HOMELAT, mWatchModel.getHomeLat());
		if(mWatchModel.getHomeLng() != 0)
		    values.put(COLUMN_NAME_HOMELNG, mWatchModel.getHomeLng());
		if(mWatchModel.getLastestTime() != null)
		    values.put(COLUMN_NAME_LASTESTTIME, mWatchModel.getLastestTime());
		if(mWatchModel.getSetVersionNO() != null)
		    values.put(COLUMN_NAME_SETVERSIONNO, mWatchModel.getSetVersionNO());
		if(mWatchModel.getContactVersionNO() != null)
		    values.put(COLUMN_NAME_CONTACTVERSIONNO, mWatchModel.getContactVersionNO());
		if(mWatchModel.getOperatorType() != null)
		    values.put(COLUMN_NAME_OPERATORTYPE, mWatchModel.getOperatorType());
		if(mWatchModel.getSmsNumber() != null)
		    values.put(COLUMN_NAME_SMSNUMBER, mWatchModel.getSmsNumber());
		if(mWatchModel.getSmsBalanceKey() != null)
		    values.put(COLUMN_NAME_SMSBALANCEKEY, mWatchModel.getSmsBalanceKey());
		if(mWatchModel.getSmsFlowKey() != null)
		    values.put(COLUMN_NAME_SMSFLOWKEY, mWatchModel.getSmsFlowKey());
		if(mWatchModel.getActiveDate() != null)
		    values.put(COLUMN_NAME_ACTIVEDATE, mWatchModel.getActiveDate());
		if(mWatchModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mWatchModel.getCreateTime());
		if(mWatchModel.getBindNumber() != null)
		    values.put(COLUMN_NAME_BINDNUMBER, mWatchModel.getBindNumber());
		if(mWatchModel.getCurrentFirmware() != null)
		    values.put(COLUMN_NAME_CURENTFIRMWARE, mWatchModel.getCurrentFirmware());
		if(mWatchModel.getFirmware() != null)
		    values.put(COLUMN_NAME_FIRMWARE, mWatchModel.getFirmware());
		if(mWatchModel.getHireExpireDate() != null)
		    values.put(COLUMN_NAME_HIREEXPIREDATE, mWatchModel.getHireExpireDate());
		if(mWatchModel.getHireStartDate() != null)
		    values.put(COLUMN_NAME_HIRESTARTDATE, mWatchModel.getHireStartDate());
		if(mWatchModel.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mWatchModel.getUpdateTime());
		if(mWatchModel.getSerialNumber() != null)
		    values.put(COLUMN_NAME_SERIALNUMBER, mWatchModel.getSerialNumber());
		if(mWatchModel.getPassword() != null)
		    values.put(COLUMN_NAME_PASSWORD, mWatchModel.getPassword());
		values.put(COLUMN_NAME_ISGUARD,mWatchModel.isIsGuard()?"1":"0");
		if(mWatchModel.getDeviceType() != null)
		    values.put(COLUMN_NAME_DEVICETYPE, mWatchModel.getDeviceType());
        if(mWatchModel.getCloudPlatform()>=0){
            values.put(COLUMN_NAME_CLOUDPLATFORM,mWatchModel.getCloudPlatform());
        }
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}