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

import vip.inteltech.gat.chatutil.ChatMsgEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatMsgDao {
	public static final String TABLE_NAME = "chatMsgs";
	public static final String COLUMN_NAME_DEVICEVOICEID = "DeviceVoiceId";
	public static final String COLUMN_NAME_DEVICEID = "DeviceID";
	public static final String COLUMN_NAME_USERID = "UserID";
	public static final String COLUMN_NAME_STATE = "State";
	public static final String COLUMN_NAME_TOTALPACKAGE = "TotalPackage";
	public static final String COLUMN_NAME_CURRENTPACKAGE = "CurrentPackage";
	public static final String COLUMN_NAME_TYPE = "Type";
	public static final String COLUMN_NAME_OBJECTID = "ObjectId";
	public static final String COLUMN_NAME_MARK = "Mark";
	public static final String COLUMN_NAME_PATH = "Path";
	public static final String COLUMN_NAME_LENGTH = "Length";
	public static final String COLUMN_NAME_MSGTYPE = "MsgType";
	public static final String COLUMN_NAME_CREATETIME = "CreateTime";
	public static final String COLUMN_NAME_UPDATETIME = "UpdateTime";
	public static final String COLUMN_NAME_ISREAD = "isRead";
	private DbOpenHelper dbHelper;

	public ChatMsgDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 获取语音list
	 * 
	 * @return
	 */
	public List<ChatMsgEntity> getChatMsgLists(int deviceId, int userId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_DEVICEID + " = ? and " + COLUMN_NAME_USERID +" = ? ", new String[]{String.valueOf(deviceId),String.valueOf(userId)});
			while (cursor.moveToNext()) {
				String DeviceVoiceId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEVOICEID));
				String DeviceID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVICEID));
				String UserID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_USERID));
				String State = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STATE));
				String TotalPackage = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TOTALPACKAGE));
				String CurrentPackage = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CURRENTPACKAGE));
				String Type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
				String ObjectId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OBJECTID));
				String Mark = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARK));
				String Path = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PATH));
				String Length = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LENGTH));
				String MsgType = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MSGTYPE));
				String CreateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String UpdateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				boolean isRead = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ISREAD)) == 1?true:false;
				
				ChatMsgEntity mChatMsgEntity = new ChatMsgEntity();
				mChatMsgEntity.setDeviceVoiceId(DeviceVoiceId);
				mChatMsgEntity.setDeviceID(DeviceID);
				mChatMsgEntity.setUserID(UserID);
				mChatMsgEntity.setState(State);
				mChatMsgEntity.setTotalPackage(TotalPackage);
				mChatMsgEntity.setCurrentPackage(CurrentPackage);
				mChatMsgEntity.setType(Type);
				mChatMsgEntity.setObjectId(ObjectId);
				mChatMsgEntity.setMark(Mark);
				mChatMsgEntity.setPath(Path);
				mChatMsgEntity.setLength(Length);
				mChatMsgEntity.setMsgType(MsgType);
				mChatMsgEntity.setCreateTime(CreateTime);
				mChatMsgEntity.setUpdateTime(UpdateTime);
				mChatMsgEntity.setRead(isRead);
				list.add(mChatMsgEntity);
			}
			cursor.close();
		}
		return list;
	}

	public void updateChatMsg(String wId,ContentValues values){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_DEVICEVOICEID + " = ?", new String[]{wId});
		}
	}

	/**
	 * 清空语音
	 */
	public void clearChatMsg(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			String sql = "DELETE FROM " + TABLE_NAME +";";
			db.execSQL(sql);		
		}
	}
	/**
	 * 保存一个语音
	 */
	public void saveChatMsg(ChatMsgEntity mChatMsgEntity){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_DEVICEVOICEID, mChatMsgEntity.getDeviceVoiceId());
		if(mChatMsgEntity.getDeviceID() != null)
		    values.put(COLUMN_NAME_DEVICEID, mChatMsgEntity.getDeviceID());
		if(mChatMsgEntity.getUserID() != null)
		    values.put(COLUMN_NAME_USERID, mChatMsgEntity.getUserID());
		if(mChatMsgEntity.getState() != null)
			values.put(COLUMN_NAME_STATE, mChatMsgEntity.getState());
		if(mChatMsgEntity.getTotalPackage() != null)
		    values.put(COLUMN_NAME_TOTALPACKAGE, mChatMsgEntity.getTotalPackage());
		if(mChatMsgEntity.getCurrentPackage() != null)
		    values.put(COLUMN_NAME_CURRENTPACKAGE, mChatMsgEntity.getCurrentPackage());
		if(mChatMsgEntity.getType() != null)
		    values.put(COLUMN_NAME_TYPE, mChatMsgEntity.getType());
		if(mChatMsgEntity.getObjectId() != null)
		    values.put(COLUMN_NAME_OBJECTID, mChatMsgEntity.getObjectId());
		if(mChatMsgEntity.getMark() != null)
		    values.put(COLUMN_NAME_MARK, mChatMsgEntity.getMark());
		if(mChatMsgEntity.getPath() != null)
		    values.put(COLUMN_NAME_PATH, mChatMsgEntity.getPath());
		if(mChatMsgEntity.getLength() != null)
		    values.put(COLUMN_NAME_LENGTH, mChatMsgEntity.getLength());
		if(mChatMsgEntity.getMsgType() != null)
		    values.put(COLUMN_NAME_MSGTYPE, mChatMsgEntity.getMsgType());
		if(mChatMsgEntity.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mChatMsgEntity.getCreateTime());
		if(mChatMsgEntity.getUpdateTime() != null)
		    values.put(COLUMN_NAME_UPDATETIME, mChatMsgEntity.getUpdateTime());
		values.put(COLUMN_NAME_ISREAD, mChatMsgEntity.isRead()?1:0);
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
