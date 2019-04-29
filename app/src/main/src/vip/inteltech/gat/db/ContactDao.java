package vip.inteltech.gat.db;

import java.util.ArrayList;
import java.util.List;

import vip.inteltech.gat.model.ContactModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContactDao {
	public static final String TABLE_NAME = "contacts";
	public static final String COLUMN_NAME_ID = "wId";
	public static final String COLUMN_NAME_FROMID = "fromId";
	public static final String COLUMN_NAME_OBJECTID = "objectId";
	public static final String COLUMN_NAME_RELATIONSHIP = "relationShip";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_NAME_AVATARURL = "avatarUrl";
	public static final String COLUMN_NAME_PHONE = "phone";
	public static final String COLUMN_NAME_CORNET = "cornet";
	public static final String COLUMN_NAME_TYPE = "type";

	private DbOpenHelper dbHelper;

	public ContactDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存联系人list
	 * 
	 * @param ContactList
	 */
	public void saveContactList(List<ContactModel> ContactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			//db.delete(TABLE_NAME, null, null);
			for (ContactModel mContactModel : ContactList) {
				//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, mContactModel.getId());
				values.put(COLUMN_NAME_FROMID, mContactModel.getFromId());
				if(mContactModel.getObjectId() != null)
					values.put(COLUMN_NAME_OBJECTID, mContactModel.getObjectId());
				if(mContactModel.getRelationShip() != null)
					values.put(COLUMN_NAME_RELATIONSHIP, mContactModel.getRelationShip());
				if(mContactModel.getAvatar() != null)
				    values.put(COLUMN_NAME_AVATAR, mContactModel.getAvatar());
				if(mContactModel.getAvatarUrl() != null)
				    values.put(COLUMN_NAME_AVATARURL, mContactModel.getAvatarUrl());
				if(mContactModel.getPhone() != null)
				    values.put(COLUMN_NAME_PHONE, mContactModel.getPhone());
				if(mContactModel.getCornet() != null)
				    values.put(COLUMN_NAME_CORNET, mContactModel.getCornet());
				if(mContactModel.getType() != null)
				    values.put(COLUMN_NAME_TYPE, mContactModel.getType());
				
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取联系人List
	 * 
	 * @return
	 */
	public List<ContactModel> getContactList(int fId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<ContactModel> contacts = new ArrayList<ContactModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_FROMID + " = ?", new String[]{String.valueOf(fId)});
			while (cursor.moveToNext()) {
				String wId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				int fromId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_FROMID));
				String objectId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OBJECTID));
				String relationShip = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_RELATIONSHIP));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String avatarUrl = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATARURL));
				String phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));
				String cornet = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CORNET));
				String type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
				
				ContactModel mContactModel = new ContactModel();
				mContactModel.setId(wId);
				mContactModel.setFromId(fromId);
				mContactModel.setObjectId(objectId);
				mContactModel.setRelationShip(relationShip);
				mContactModel.setAvatar(avatar);
				mContactModel.setAvatarUrl(avatarUrl);
				mContactModel.setPhone(phone);
				mContactModel.setCornet(cornet);
				mContactModel.setType(type);
				contacts.add(mContactModel);
			}
			cursor.close();
		}
		return contacts;
	}

	/**
	 * 更新联系人
	 * @param wId
	 * @param values
	 */
	public void updateContact(String wId,ContentValues values){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 更新联系人
	 * @param wId
	 * @param mContactModel
	 */
	public void updateContact(String wId,ContactModel mContactModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mContactModel.getId());
		values.put(COLUMN_NAME_FROMID, mContactModel.getFromId());
		if(mContactModel.getObjectId() != null)
			values.put(COLUMN_NAME_OBJECTID, mContactModel.getObjectId());
		if(mContactModel.getRelationShip() != null)
			values.put(COLUMN_NAME_RELATIONSHIP, mContactModel.getRelationShip());
		if(mContactModel.getAvatar() != null)
		    values.put(COLUMN_NAME_AVATAR, mContactModel.getAvatar());
		if(mContactModel.getAvatarUrl() != null)
		    values.put(COLUMN_NAME_AVATARURL, mContactModel.getAvatarUrl());
		if(mContactModel.getPhone() != null)
		    values.put(COLUMN_NAME_PHONE, mContactModel.getPhone());
		if(mContactModel.getCornet() != null)
		    values.put(COLUMN_NAME_CORNET, mContactModel.getCornet());
		if(mContactModel.getType() != null)
		    values.put(COLUMN_NAME_TYPE, mContactModel.getType());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个联系人
	 * @param wId
	 */
	public void deleteContact(String wId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{wId});
		}
	}
	/**
	 * 删除一个未关注联系人
	 * @param objectId
	 * @param device
	 */
	public void deleteUnconfirmed(String objectId, int device){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_OBJECTID + " = ? and " + COLUMN_NAME_FROMID + " = ? and " + COLUMN_NAME_TYPE + " = ? ", new String[]{objectId,String.valueOf(device),"4"});
		}
	}
	/**
	 * 删除手表联系人
	 * @param deviceId
	 */
	public void deleteWatchContact(int deviceId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_FROMID + " = ? and " + COLUMN_NAME_TYPE + " < 4", new String[]{String.valueOf(deviceId)});
		}
	}
	/**
	 * 保存一个联系人
	 * @param mContactModel
	 */
	public void saveContact(ContactModel mContactModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mContactModel.getId());
		values.put(COLUMN_NAME_FROMID, mContactModel.getFromId());
		if(mContactModel.getObjectId() != null)
			values.put(COLUMN_NAME_OBJECTID, mContactModel.getObjectId());
		if(mContactModel.getRelationShip() != null)
			values.put(COLUMN_NAME_RELATIONSHIP, mContactModel.getRelationShip());
		if(mContactModel.getAvatar() != null)
		    values.put(COLUMN_NAME_AVATAR, mContactModel.getAvatar());
		if(mContactModel.getAvatarUrl() != null)
		    values.put(COLUMN_NAME_AVATARURL, mContactModel.getAvatarUrl());
		if(mContactModel.getPhone() != null)
		    values.put(COLUMN_NAME_PHONE, mContactModel.getPhone());
		if(mContactModel.getCornet() != null)
		    values.put(COLUMN_NAME_CORNET, mContactModel.getCornet());
		if(mContactModel.getType() != null)
		    values.put(COLUMN_NAME_TYPE, mContactModel.getType());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
