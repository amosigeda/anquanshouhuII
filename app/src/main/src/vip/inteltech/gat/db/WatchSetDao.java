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

import vip.inteltech.gat.model.WatchSetModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WatchSetDao {
	public static final String TABLE_NAME = "watchSet";
	public static final String COLUMN_NAME_ID = "wId";
	public static final String COLUMN_NAME_AUTOANSWER = "autoAnswer";
	public static final String COLUMN_NAME_REPORTLOCATION = "reportLocation";
	public static final String COLUMN_NAME_SOMATOANSWER = "somatoAnswer";
	public static final String COLUMN_NAME_RESERVEDPOWER = "reservedPower";
	public static final String COLUMN_NAME_CLASSDISABLED = "classDisabled";
	public static final String COLUMN_NAME_TIMESWITCH = "timeSwitch";
	public static final String COLUMN_NAME_REFUSEDSTRANGER = "refusedStranger";
	public static final String COLUMN_NAME_WATCHOFFALARM = "watchOffAlarm";
	public static final String COLUMN_NAME_CALLSOUND = "callSound";
	public static final String COLUMN_NAME_CALLVIBRATE = "callVibrate";
	public static final String COLUMN_NAME_MSGSOUND = "msgSound";
	public static final String COLUMN_NAME_MSGVIBRATE = "msgVibrate";
	public static final String COLUMN_NAME_CLASSDISABLEDA = "classDisableda";
	public static final String COLUMN_NAME_CLASSDISABLEDB = "classDisabledb";
	public static final String COLUMN_NAME_WEEKDISABLED = "weekDisabled";
	public static final String COLUMN_NAME_TIMEROPEN = "timerOpen";
	public static final String COLUMN_NAME_TIMERCLOSE = "timerClose";
	public static final String COLUMN_NAME_BRIGHTSCREEN = "brightScreen";
	public static final String COLUMN_NAME_WEEKALARM1 = "weekAlarm1";
	public static final String COLUMN_NAME_WEEKALARM2 = "weekAlarm2";
	public static final String COLUMN_NAME_WEEKALARM3 = "weekAlarm3";
	public static final String COLUMN_NAME_ALARM1 = "alarm1";
	public static final String COLUMN_NAME_ALARM2 = "alarm2";
	public static final String COLUMN_NAME_ALARM3 = "alarm3";
	public static final String COLUMN_NAME_LOCATIONMODE = "locationMode";
	public static final String COLUMN_NAME_LOCATIONTIME = "locationTime";
	public static final String COLUMN_NAME_FLOWERNUMBER = "flowerNumber";
	public static final String COLUMN_NAME_LANGUAGE = "language";
	public static final String COLUMN_NAME_TIMEZONE = "timeZone";
	public static final String COLUMN_NAME_CREATETIME = "createTime";
	public static final String COLUMN_NAME_UPDATETIME = "updateTime";
	public static final String COLUMN_NAME_VERSIONNUMBER = "versionNumber";
	public static final String COLUMN_NAME_SLEEPCALCULATE = "sleepCalculate";
	public static final String COLUMN_NAME_STEPCALCULATE = "stepCalculate";
	public static final String COLUMN_NAME_HRCALCULATE = "hrCalculate";
	public static final String COLUMN_NAME_SOSMSGSWITCH = "sosMsgswitch";

	private DbOpenHelper dbHelper;

	public WatchSetDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	public WatchSetModel getWatchSet(int Id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		WatchSetModel mWatchSetModel = new WatchSetModel();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(Id)});
			//db.query(TABLE_NAME, new String[]{}, "wId = ?", new String[]{wId}, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				int wId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				String autoAnswer = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AUTOANSWER));
				String reportLocation = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REPORTLOCATION));
				String somatoAnswer = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOMATOANSWER));
				String reservedPower = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_RESERVEDPOWER));
				String classDisabled = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CLASSDISABLED));
				String timeSwitch = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TIMESWITCH));
				String refusedStranger = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REFUSEDSTRANGER));
				String watchOffAlarm = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WATCHOFFALARM));
				String callSound = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CALLSOUND));
				String callVibrate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CALLVIBRATE));
				String msgSound = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MSGSOUND));
				String msgVibrate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MSGVIBRATE));
				String classDisableda = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CLASSDISABLEDA));
				String classDisabledb = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CLASSDISABLEDB));
				String weekDisabled = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WEEKDISABLED));
				String timerOpen = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TIMEROPEN));
				String timerClose = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TIMERCLOSE));
				String brightScreen = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BRIGHTSCREEN));
				String weekAlarm1 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WEEKALARM1));
				String weekAlarm2 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WEEKALARM2));
				String weekAlarm3 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WEEKALARM3));
				String alarm1 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ALARM1));
				String alarm2 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ALARM2));
				String alarm3 = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ALARM3));
				String locationMode = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCATIONMODE));
				String locationTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOCATIONTIME));
				String flowerNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FLOWERNUMBER));
				String language = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LANGUAGE));
				String timeZone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TIMEZONE));
				String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CREATETIME));
				String updateTime = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_UPDATETIME));
				String versionNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_VERSIONNUMBER));
				String sleepCalculate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SLEEPCALCULATE));
				String stepCalculate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_STEPCALCULATE));
				String hrCalculate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HRCALCULATE));
				String sosMsgswitch = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SOSMSGSWITCH));

				mWatchSetModel.setDeviceId(wId);
				mWatchSetModel.setAutoAnswer(autoAnswer);
				mWatchSetModel.setReportLocation(reportLocation);
				mWatchSetModel.setSomatoAnswer(somatoAnswer);
				mWatchSetModel.setReservedPower(reservedPower);
				mWatchSetModel.setClassDisabled(classDisabled);
				mWatchSetModel.setTimeSwitch(timeSwitch);
				mWatchSetModel.setRefusedStranger(refusedStranger);
				mWatchSetModel.setWatchOffAlarm(watchOffAlarm);
				mWatchSetModel.setCallSound(callSound);
				mWatchSetModel.setCallVibrate(callVibrate);
				mWatchSetModel.setMsgSound(msgSound);
				mWatchSetModel.setMsgVibrate(msgVibrate);
				mWatchSetModel.setClassDisableda(classDisableda);
				mWatchSetModel.setClassDisabledb(classDisabledb);
				mWatchSetModel.setWeekDisabled(weekDisabled);
				mWatchSetModel.setTimerOpen(timerOpen);
				mWatchSetModel.setTimerClose(timerClose);
				mWatchSetModel.setBrightScreen(brightScreen);
				mWatchSetModel.setWeekAlarm1(weekAlarm1);
				mWatchSetModel.setWeekAlarm2(weekAlarm2);
				mWatchSetModel.setWeekAlarm3(weekAlarm3);
				mWatchSetModel.setAlarm1(alarm1);
				mWatchSetModel.setAlarm2(alarm2);
				mWatchSetModel.setAlarm3(alarm3);
				mWatchSetModel.setLocationMode(locationMode);
				mWatchSetModel.setLocationTime(locationTime);
				mWatchSetModel.setFlowerNumber(flowerNumber);
				mWatchSetModel.setLanguage(language);
				mWatchSetModel.setTimeZone(timeZone);
				mWatchSetModel.setCreateTime(createTime);
				mWatchSetModel.setUpdateTime(updateTime);
				mWatchSetModel.setVersionNumber(versionNumber);
				mWatchSetModel.setSleepCalculate(sleepCalculate);
				mWatchSetModel.setStepCalculate(stepCalculate);
				mWatchSetModel.setHrCalculate(hrCalculate);
				mWatchSetModel.setSosMsgswitch(sosMsgswitch);
			}
			//System.out.println(mWatchModel.getName() +"  "+ mWatchModel.getBirthday());
			cursor.close();
		}
		return mWatchSetModel;
		
	}
	/**
	 * 更新联系人
	 * @param mWatchSetModel
	 */
	public void updateWatchSet(int wId,WatchSetModel mWatchSetModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mWatchSetModel.getDeviceId());
		if(mWatchSetModel.getAutoAnswer() != null)
			values.put(COLUMN_NAME_AUTOANSWER, mWatchSetModel.getAutoAnswer());
		if(mWatchSetModel.getReportLocation() != null)
		    values.put(COLUMN_NAME_REPORTLOCATION, mWatchSetModel.getReportLocation());
		if(mWatchSetModel.getSomatoAnswer() != null)
		    values.put(COLUMN_NAME_SOMATOANSWER, mWatchSetModel.getSomatoAnswer());
		if(mWatchSetModel.getReservedPower() != null)
		    values.put(COLUMN_NAME_RESERVEDPOWER, mWatchSetModel.getReservedPower());
		if(mWatchSetModel.getClassDisabled() != null)
		    values.put(COLUMN_NAME_CLASSDISABLED, mWatchSetModel.getClassDisabled());
		if(mWatchSetModel.getTimeSwitch() != null)
		    values.put(COLUMN_NAME_TIMESWITCH, mWatchSetModel.getTimeSwitch());
		if(mWatchSetModel.getRefusedStranger() != null)
		    values.put(COLUMN_NAME_REFUSEDSTRANGER, mWatchSetModel.getRefusedStranger());
		if(mWatchSetModel.getWatchOffAlarm() != null)
		    values.put(COLUMN_NAME_WATCHOFFALARM, mWatchSetModel.getWatchOffAlarm());
		if(mWatchSetModel.getCallSound() != null)
		    values.put(COLUMN_NAME_CALLSOUND, mWatchSetModel.getCallSound());
		if(mWatchSetModel.getCallVibrate() != null)
		    values.put(COLUMN_NAME_CALLVIBRATE, mWatchSetModel.getCallVibrate());
		if(mWatchSetModel.getMsgSound() != null)
		    values.put(COLUMN_NAME_MSGSOUND, mWatchSetModel.getMsgSound());
		if(mWatchSetModel.getMsgVibrate() != null)
		    values.put(COLUMN_NAME_MSGVIBRATE, mWatchSetModel.getMsgVibrate());
		if(mWatchSetModel.getClassDisableda() != null)
		    values.put(COLUMN_NAME_CLASSDISABLEDA, mWatchSetModel.getClassDisableda());
		if(mWatchSetModel.getClassDisabledb() != null)
		    values.put(COLUMN_NAME_CLASSDISABLEDB, mWatchSetModel.getClassDisabledb());
		if(mWatchSetModel.getWeekDisabled() != null)
		    values.put(COLUMN_NAME_WEEKDISABLED, mWatchSetModel.getWeekDisabled());
		if(mWatchSetModel.getTimerOpen() != null)
		    values.put(COLUMN_NAME_TIMEROPEN, mWatchSetModel.getTimerOpen());
		if(mWatchSetModel.getTimerClose() != null)
		    values.put(COLUMN_NAME_TIMERCLOSE, mWatchSetModel.getTimerClose());
		if(mWatchSetModel.getBrightScreen() != null)
		    values.put(COLUMN_NAME_BRIGHTSCREEN, mWatchSetModel.getBrightScreen());
		if(mWatchSetModel.getWeekAlarm1() != null)
		    values.put(COLUMN_NAME_WEEKALARM1, mWatchSetModel.getWeekAlarm1());
		if(mWatchSetModel.getWeekAlarm2() != null)
		    values.put(COLUMN_NAME_WEEKALARM2, mWatchSetModel.getWeekAlarm2());
		if(mWatchSetModel.getWeekAlarm3() != null)
		    values.put(COLUMN_NAME_WEEKALARM3, mWatchSetModel.getWeekAlarm3());
		if(mWatchSetModel.getAlarm1() != null)
		    values.put(COLUMN_NAME_ALARM1, mWatchSetModel.getAlarm1());
		if(mWatchSetModel.getAlarm2() != null)
		    values.put(COLUMN_NAME_ALARM2, mWatchSetModel.getAlarm2());
		if(mWatchSetModel.getAlarm3() != null)
		    values.put(COLUMN_NAME_ALARM3, mWatchSetModel.getAlarm3());
		if(mWatchSetModel.getLocationMode() != null)
		    values.put(COLUMN_NAME_LOCATIONMODE, mWatchSetModel.getLocationMode());
		if(mWatchSetModel.getLocationTime() != null)
		    values.put(COLUMN_NAME_LOCATIONTIME, mWatchSetModel.getLocationTime());
		if(mWatchSetModel.getFlowerNumber() != null)
		    values.put(COLUMN_NAME_FLOWERNUMBER, mWatchSetModel.getFlowerNumber());
		if(mWatchSetModel.getLanguage() != null)
		    values.put(COLUMN_NAME_LANGUAGE, mWatchSetModel.getLanguage());
		if(mWatchSetModel.getTimeZone() != null)
		    values.put(COLUMN_NAME_TIMEZONE, mWatchSetModel.getTimeZone());
		if(mWatchSetModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mWatchSetModel.getCreateTime());
		if(mWatchSetModel.getUpdateTime() != null)
			values.put(COLUMN_NAME_UPDATETIME, mWatchSetModel.getUpdateTime());
		if(mWatchSetModel.getVersionNumber() != null)
			values.put(COLUMN_NAME_VERSIONNUMBER, mWatchSetModel.getVersionNumber());
		if(mWatchSetModel.getSleepCalculate() != null)
			values.put(COLUMN_NAME_SLEEPCALCULATE, mWatchSetModel.getSleepCalculate());
		if(mWatchSetModel.getStepCalculate() != null)
			values.put(COLUMN_NAME_STEPCALCULATE, mWatchSetModel.getStepCalculate());
		if(mWatchSetModel.getHrCalculate() != null)
			values.put(COLUMN_NAME_HRCALCULATE, mWatchSetModel.getHrCalculate());
		if(mWatchSetModel.getSosMsgswitch() != null)
			values.put(COLUMN_NAME_SOSMSGSWITCH, mWatchSetModel.getSosMsgswitch());
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(wId)});
		}
	}

	public void saveWatchSet(WatchSetModel mWatchSetModel){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, mWatchSetModel.getDeviceId());
		if(mWatchSetModel.getAutoAnswer() != null)
			values.put(COLUMN_NAME_AUTOANSWER, mWatchSetModel.getAutoAnswer());
		if(mWatchSetModel.getReportLocation() != null)
		    values.put(COLUMN_NAME_REPORTLOCATION, mWatchSetModel.getReportLocation());
		if(mWatchSetModel.getSomatoAnswer() != null)
		    values.put(COLUMN_NAME_SOMATOANSWER, mWatchSetModel.getSomatoAnswer());
		if(mWatchSetModel.getReservedPower() != null)
		    values.put(COLUMN_NAME_RESERVEDPOWER, mWatchSetModel.getReservedPower());
		if(mWatchSetModel.getClassDisabled() != null)
		    values.put(COLUMN_NAME_CLASSDISABLED, mWatchSetModel.getClassDisabled());
		if(mWatchSetModel.getTimeSwitch() != null)
		    values.put(COLUMN_NAME_TIMESWITCH, mWatchSetModel.getTimeSwitch());
		if(mWatchSetModel.getRefusedStranger() != null)
		    values.put(COLUMN_NAME_REFUSEDSTRANGER, mWatchSetModel.getRefusedStranger());
		if(mWatchSetModel.getWatchOffAlarm() != null)
		    values.put(COLUMN_NAME_WATCHOFFALARM, mWatchSetModel.getWatchOffAlarm());
		if(mWatchSetModel.getCallSound() != null)
		    values.put(COLUMN_NAME_CALLSOUND, mWatchSetModel.getCallSound());
		if(mWatchSetModel.getCallVibrate() != null)
		    values.put(COLUMN_NAME_CALLVIBRATE, mWatchSetModel.getCallVibrate());
		if(mWatchSetModel.getMsgSound() != null)
		    values.put(COLUMN_NAME_MSGSOUND, mWatchSetModel.getMsgSound());
		if(mWatchSetModel.getMsgVibrate() != null)
		    values.put(COLUMN_NAME_MSGVIBRATE, mWatchSetModel.getMsgVibrate());
		if(mWatchSetModel.getClassDisableda() != null)
		    values.put(COLUMN_NAME_CLASSDISABLEDA, mWatchSetModel.getClassDisableda());
		if(mWatchSetModel.getClassDisabledb() != null)
		    values.put(COLUMN_NAME_CLASSDISABLEDB, mWatchSetModel.getClassDisabledb());
		if(mWatchSetModel.getWeekDisabled() != null)
		    values.put(COLUMN_NAME_WEEKDISABLED, mWatchSetModel.getWeekDisabled());
		if(mWatchSetModel.getTimerOpen() != null)
		    values.put(COLUMN_NAME_TIMEROPEN, mWatchSetModel.getTimerOpen());
		if(mWatchSetModel.getTimerClose() != null)
		    values.put(COLUMN_NAME_TIMERCLOSE, mWatchSetModel.getTimerClose());
		if(mWatchSetModel.getBrightScreen() != null)
		    values.put(COLUMN_NAME_BRIGHTSCREEN, mWatchSetModel.getBrightScreen());
		if(mWatchSetModel.getWeekAlarm1() != null)
		    values.put(COLUMN_NAME_WEEKALARM1, mWatchSetModel.getWeekAlarm1());
		if(mWatchSetModel.getWeekAlarm2() != null)
		    values.put(COLUMN_NAME_WEEKALARM2, mWatchSetModel.getWeekAlarm2());
		if(mWatchSetModel.getWeekAlarm3() != null)
		    values.put(COLUMN_NAME_WEEKALARM3, mWatchSetModel.getWeekAlarm3());
		if(mWatchSetModel.getAlarm1() != null)
		    values.put(COLUMN_NAME_ALARM1, mWatchSetModel.getAlarm1());
		if(mWatchSetModel.getAlarm2() != null)
		    values.put(COLUMN_NAME_ALARM2, mWatchSetModel.getAlarm2());
		if(mWatchSetModel.getAlarm3() != null)
		    values.put(COLUMN_NAME_ALARM3, mWatchSetModel.getAlarm3());
		if(mWatchSetModel.getLocationMode() != null)
		    values.put(COLUMN_NAME_LOCATIONMODE, mWatchSetModel.getLocationMode());
		if(mWatchSetModel.getLocationTime() != null)
		    values.put(COLUMN_NAME_LOCATIONTIME, mWatchSetModel.getLocationTime());
		if(mWatchSetModel.getFlowerNumber() != null)
		    values.put(COLUMN_NAME_FLOWERNUMBER, mWatchSetModel.getFlowerNumber());
		if(mWatchSetModel.getLanguage() != null)
		    values.put(COLUMN_NAME_LANGUAGE, mWatchSetModel.getLanguage());
		if(mWatchSetModel.getTimeZone() != null)
		    values.put(COLUMN_NAME_TIMEZONE, mWatchSetModel.getTimeZone());
		if(mWatchSetModel.getCreateTime() != null)
		    values.put(COLUMN_NAME_CREATETIME, mWatchSetModel.getCreateTime());
		if(mWatchSetModel.getUpdateTime() != null)
			values.put(COLUMN_NAME_UPDATETIME, mWatchSetModel.getUpdateTime());
		if(mWatchSetModel.getVersionNumber() != null)
			values.put(COLUMN_NAME_VERSIONNUMBER, mWatchSetModel.getVersionNumber());
		if(mWatchSetModel.getSleepCalculate() != null)
			values.put(COLUMN_NAME_SLEEPCALCULATE, mWatchSetModel.getSleepCalculate());
		if(mWatchSetModel.getStepCalculate() != null)
			values.put(COLUMN_NAME_STEPCALCULATE, mWatchSetModel.getStepCalculate());
		if(mWatchSetModel.getHrCalculate() != null)
			values.put(COLUMN_NAME_HRCALCULATE, mWatchSetModel.getHrCalculate());
		if(mWatchSetModel.getSosMsgswitch() != null)
			values.put(COLUMN_NAME_SOSMSGSWITCH, mWatchSetModel.getSosMsgswitch());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
