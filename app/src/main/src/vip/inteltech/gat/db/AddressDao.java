package vip.inteltech.gat.db;

import vip.inteltech.gat.model.AddressModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	public static final String TABLE_NAME = "address";
	public static final String COLUMN_NAME_LATITUDE = "Latitude";
	public static final String COLUMN_NAME_LONGITUDE = "Longitude";
	public static final String COLUMN_NAME_ADDRESS = "Address";

	private DbOpenHelper dbHelper;

	public AddressDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	public String getAddress(double lat, double lon){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String address = "";
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_LATITUDE + " = ? AND " + COLUMN_NAME_LONGITUDE + " = ?", 
					new String[]{String.valueOf(lat), String.valueOf(lon)});
			if(cursor != null && cursor.moveToFirst()) {
				address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));
			}
			cursor.close();
		}
		return address;
	}

	public void updateAddress(double lat, double lon, AddressModel mAddressModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mAddressModel.getLatitude() != 0)
		    values.put(COLUMN_NAME_LATITUDE, mAddressModel.getLatitude());
		if(mAddressModel.getLongitude() != 0)
		    values.put(COLUMN_NAME_LONGITUDE, mAddressModel.getLongitude());
		if(mAddressModel.getAddress() != null)
		    values.put(COLUMN_NAME_ADDRESS, mAddressModel.getAddress());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_LATITUDE + " = ? AND " + COLUMN_NAME_LONGITUDE + " = ?", new String[]{String.valueOf(lat), String.valueOf(lon)});
		}
	}

	public void saveAddress(AddressModel mAddressModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(mAddressModel.getLatitude() != 0)
		    values.put(COLUMN_NAME_LATITUDE, mAddressModel.getLatitude());
		if(mAddressModel.getLongitude() != 0)
		    values.put(COLUMN_NAME_LONGITUDE, mAddressModel.getLongitude());
		if(mAddressModel.getAddress() != null)
		    values.put(COLUMN_NAME_ADDRESS, mAddressModel.getAddress());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
		
		keepAddress();
	}

	private void keepAddress(){
		int count = 0;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME, null);
		if(cursor != null && cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		if(count > 200){
			db.delete(TABLE_NAME, "rowid = 1", null);
		}
	}
}