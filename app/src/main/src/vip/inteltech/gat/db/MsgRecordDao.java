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

import vip.inteltech.gat.model.MsgRecordModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MsgRecordDao {
	public static final String TABLE_NAME = "msgrecord";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TYPE = "Type";
	public static final String COLUMN_NAME_DEVICEID = "DeviceID";
	public static final String COLUMN_NAME_USERID = "UserID";
	public static final String COLUMN_NAME_CONTENT = "Content";
	public static final String COLUMN_NAME_MESSAGE = "Message";
	public static final String COLUMN_NAME_CREATETIME = "CreateTime";
	public static final String COLUMN_NAME_ISHANDLE = "isHandle";
	
	private DbOpenHelper dbHelper;

	public MsgRecordDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存消息记录list
	 * 
	 * @param MsgRecordList
	 */
	public void saveMsgRecordList(List<MsgRecordModel> MsgRecordList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (MsgRecordModel mMsgRecordModel : MsgRecordList) {
				//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
				ContentValues values = new ContentValues();
				if(mMsgRecordModel.getType() != null)
					values.put(COLUMN_NAME_TYPE, mMsgRecordModel.getType());
				if(mMsgRecordModel.getDeviceID() != null)
					values.put(COLUMN_NAME_DEVICEID, mMsgRecordModel.getDeviceID());
				if(mMsgRecordModel.getUserID() != null)
					values.put(COLUMN_NAME_USERID, mMsgRecordModel.getUserID());
				if(mMsgRecordModel.getContent() != null)
				    values.put(COLUMN_NAME_CONTENT, mMsgRecordModel.getContent());
				if(mMsgRecordModel.getMessage() != null)
				    values.put(COLUMN_NAME_MESSAGE, mMsgRecordModel.getMessage());
				if(mMsgRecordModel.getCreateTime() != null)
				    values.put(COLUMN_NAME_CREATETIME, mMsgRecordModel.getCreateTime());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	/**
	 * 获取消息记录List
	 * 
	 * @return
	 */
	public List<MsgRecordModel> getMsgRecordList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<MsgRecordModel> MsgRecords = new ArrayList<MsgRecordModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME ,null);
			while (cursor.moveToNext()) {
				String Id = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String Type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
				String DeviceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String UserID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Content = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTENT));
				String Message = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MESSAGE));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				MsgRecordModel mMsgRecordModel = new MsgRecordModel();
				mMsgRecordModel.setId(Id);
				mMsgRecordModel.setType(Type);
				mMsgRecordModel.setDeviceID(DeviceID);
				mMsgRecordModel.setUserID(UserID);
				mMsgRecordModel.setContent(Content);
				mMsgRecordModel.setMessage(Message);
				mMsgRecordModel.setCreateTime(CreateTime);
				MsgRecords.add(0,mMsgRecordModel);
			}
			cursor.close();
		}
		return MsgRecords;
	}
	public List<MsgRecordModel> getMsgRecordList(int dId, int uId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<MsgRecordModel> MsgRecords = new ArrayList<MsgRecordModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_DEVICEID + " = ? or "+ COLUMN_NAME_DEVICEID + " = 0 and " + COLUMN_NAME_USERID +" = ?", new String[]{String.valueOf(dId),String.valueOf(uId)});
			while (cursor.moveToNext()) {
				String Id = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String Type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
				String DeviceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String UserID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String Content = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTENT));
				String Message = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MESSAGE));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				MsgRecordModel mMsgRecordModel = new MsgRecordModel();
				mMsgRecordModel.setId(Id);
				mMsgRecordModel.setType(Type);
				mMsgRecordModel.setDeviceID(DeviceID);
				mMsgRecordModel.setUserID(UserID);
				mMsgRecordModel.setContent(Content);
				mMsgRecordModel.setMessage(Message);
				mMsgRecordModel.setCreateTime(CreateTime);
				MsgRecords.add(0,mMsgRecordModel);
			}
			cursor.close();
		}
		return MsgRecords;
	}
	/**
	 * 更新消息记录
	 * @param msgId
	 * @param values
	 */
	public void updateMsgRecord(String wId,MsgRecordModel mMsgRecordModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mMsgRecordModel.getType() != null)
			values.put(COLUMN_NAME_TYPE, mMsgRecordModel.getType());
		if(mMsgRecordModel.getDeviceID() != null)
			values.put(COLUMN_NAME_DEVICEID, mMsgRecordModel.getDeviceID());
		if(mMsgRecordModel.getUserID() != null)
			values.put(COLUMN_NAME_USERID, mMsgRecordModel.getUserID());
		if(mMsgRecordModel.getContent() != null)
		    values.put(COLUMN_NAME_CONTENT, mMsgRecordModel.getContent());
		if(mMsgRecordModel.getMessage() != null)
		    values.put(COLUMN_NAME_MESSAGE, mMsgRecordModel.getMessage());
		if(mMsgRecordModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mMsgRecordModel.getCreateTime());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个消息记录
	 * @param username
	 */
	public void deleteMsgRecord(String wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 清空消息记录
	 * @param username
	 */
	public void clearMsgRecord(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			String sql = "DELETE FROM " + TABLE_NAME +";";
			db.execSQL(sql);		
		}
	}
	/**
	 * 保存一个消息记录
	 * @param user
	 */
	public void saveMsgRecord(MsgRecordModel mMsgRecordModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mMsgRecordModel.getType() != null)
			values.put(COLUMN_NAME_TYPE, mMsgRecordModel.getType());
		if(mMsgRecordModel.getDeviceID() != null)
			values.put(COLUMN_NAME_DEVICEID, mMsgRecordModel.getDeviceID());
		if(mMsgRecordModel.getUserID() != null)
			values.put(COLUMN_NAME_USERID, mMsgRecordModel.getUserID());
		if(mMsgRecordModel.getContent() != null)
		    values.put(COLUMN_NAME_CONTENT, mMsgRecordModel.getContent());
		if(mMsgRecordModel.getMessage() != null)
		    values.put(COLUMN_NAME_MESSAGE, mMsgRecordModel.getMessage());
		if(mMsgRecordModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mMsgRecordModel.getCreateTime());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
