package vip.inteltech.gat.db;

import java.util.ArrayList;
import java.util.List;

import vip.inteltech.gat.model.FriendModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FriendDao {
	public static final String TABLE_NAME = "friends";
	public static final String COLUMN_NAME_ID = "wId";
	public static final String COLUMN_NAME_DEVICEFRIENDID = "deviceFriendId";
	public static final String COLUMN_NAME_RELATIONSHIP = "relationShip";
	public static final String COLUMN_NAME_FRIENDDEVICEID = "friendDeviceId";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_PHONE = "phone";

	private DbOpenHelper dbHelper;

	public FriendDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	public void saveFriendList(List<FriendModel> FriendList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (FriendModel mFriendModel : FriendList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, mFriendModel.getId());
				values.put(COLUMN_NAME_DEVICEFRIENDID, mFriendModel.getDeviceFriendId());
				values.put(COLUMN_NAME_FRIENDDEVICEID, mFriendModel.getFriendDeviceId());
				if(mFriendModel.getRelationShip() != null)
					values.put(COLUMN_NAME_RELATIONSHIP, mFriendModel.getRelationShip());
				if(mFriendModel.getName() != null)
					values.put(COLUMN_NAME_NAME, mFriendModel.getName());
				if(mFriendModel.getPhone() != null)
					values.put(COLUMN_NAME_PHONE, mFriendModel.getPhone());

				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	public List<FriendModel> getWatchFriendList(int fId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<FriendModel> friends = new ArrayList<FriendModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(fId)});
			while (cursor.moveToNext()) {
				int wId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				int deviceFriendId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DEVICEFRIENDID));
				int friendDeviceId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_FRIENDDEVICEID));
				String relationShip = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_RELATIONSHIP));
				String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME));
				String phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));

				FriendModel mFriendModel = new FriendModel();
				mFriendModel.setId(wId);
				mFriendModel.setDeviceFriendId(deviceFriendId);
				mFriendModel.setFriendDeviceId(friendDeviceId);
				mFriendModel.setRelationShip(relationShip);
				mFriendModel.setName(name);
				mFriendModel.setPhone(phone);
				friends.add(mFriendModel);
			}
			cursor.close();
		}
		return friends;
	}

	public void deleteFriendList(int deviceId) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(deviceId)});
		}
	}
	
	public void saveFriend(FriendModel mFriendModel) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mFriendModel.getId());
		values.put(COLUMN_NAME_DEVICEFRIENDID, mFriendModel.getDeviceFriendId());
		values.put(COLUMN_NAME_FRIENDDEVICEID, mFriendModel.getFriendDeviceId());
		if (mFriendModel.getRelationShip() != null)
			values.put(COLUMN_NAME_RELATIONSHIP, mFriendModel.getRelationShip());
		if (mFriendModel.getName() != null)
			values.put(COLUMN_NAME_NAME, mFriendModel.getName());
		if (mFriendModel.getPhone() != null)
			values.put(COLUMN_NAME_PHONE, mFriendModel.getPhone());
		if (db.isOpen()) {
			db.replace(TABLE_NAME, null, values);
		}
	}
}