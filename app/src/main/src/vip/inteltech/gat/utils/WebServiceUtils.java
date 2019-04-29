package vip.inteltech.gat.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.BabyInfo;
import vip.inteltech.gat.HomeInfo;
import vip.inteltech.gat.MsgNoti;
import vip.inteltech.gat.SchoolInfo;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class WebServiceUtils {
	private static Context mContext = AppContext.getInstance().getContext();

	// 获取宝贝资料
	public static void GetDeviceDetail(final Context context, final int ids, final String deviceId, WebServiceListener wsl, final boolean isRefreshLocal) {
		WebService ws = new WebService(context, ids, false, "GetDeviceDetail");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("deviceId", deviceId));
		if (wsl == null) {
			ws.addWebServiceListener(new WebServiceListener() {

				@Override
				public void onWebServiceReceive(String method, int id, String result) {
					try {
						if (ids == id) {
							JSONObject jsonObject = JSONObject.parseObject(result);
							int code = jsonObject.getIntValue("Code");
							if (code == 1) {
								// 1成功
								WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(deviceId);
								mWatchModel.setId(Integer.valueOf(deviceId));
								mWatchModel.setUserId(jsonObject.getIntValue("UserId"));
								mWatchModel.setModel(jsonObject.getString("DeviceModelID"));
								mWatchModel.setName(jsonObject.getString("BabyName"));
								mWatchModel.setAvatar(jsonObject.getString("Photo"));
								mWatchModel.setPhone(jsonObject.getString("PhoneNumber"));
								mWatchModel.setCornet(jsonObject.getString("PhoneCornet"));
								mWatchModel.setGender(jsonObject.getString("Gender"));
								mWatchModel.setBirthday(jsonObject.getString("Birthday"));
								mWatchModel.setGrade(jsonObject.getIntValue("Grade"));
								mWatchModel.setHomeAddress(jsonObject.getString("HomeAddress"));
								mWatchModel.setHomeLat(jsonObject.getDouble("HomeLat"));
								mWatchModel.setHomeLng(jsonObject.getDouble("HomeLng"));
								mWatchModel.setSchoolAddress(jsonObject.getString("SchoolAddress"));
								mWatchModel.setSchoolLat(jsonObject.getDouble("SchoolLat"));
								mWatchModel.setSchoolLng(jsonObject.getDouble("SchoolLng"));
								mWatchModel.setLastestTime(jsonObject.getString("LatestTime"));
								mWatchModel.setSetVersionNO(jsonObject.getString("SetVersionNO"));
								mWatchModel.setContactVersionNO(jsonObject.getString("ContactVersionNO"));
								mWatchModel.setOperatorType(jsonObject.getString("OperatorType"));
								mWatchModel.setSmsNumber(jsonObject.getString("SmsNumber"));
								mWatchModel.setSmsBalanceKey(jsonObject.getString("SmsBalanceKey"));
								mWatchModel.setSmsFlowKey(jsonObject.getString("SmsFlowKey"));
								mWatchModel.setActiveDate(jsonObject.getString("ActiveDate"));
								mWatchModel.setCreateTime(jsonObject.getString("CreateTime"));
								mWatchModel.setBindNumber(jsonObject.getString("BindNumber"));
								mWatchModel.setCurrentFirmware(jsonObject.getString("CurrentFirmware"));
								mWatchModel.setFirmware(jsonObject.getString("Firmware"));
								mWatchModel.setHireExpireDate(jsonObject.getString("HireExpireDate"));
								mWatchModel.setUpdateTime(jsonObject.getString("UpdateTime"));
								mWatchModel.setSerialNumber(jsonObject.getString("SerialNumber"));
								mWatchModel.setPassword(jsonObject.getString("Password"));
								mWatchModel.setIsGuard(jsonObject.getString("IsGuard").equals("1") ? true : false);
								WatchDao mWatchDao = new WatchDao(context);
								mWatchDao.saveWatch(mWatchModel);
								// MToast.makeText(R.string.wait_admin_confirm).show();
								if (isRefreshLocal) {
									AppContext.getInstance().setWatchMap(mWatchDao.getWatchMap());
									AppContext.getInstance().setmWatchModel(AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
								}
							} else {
								// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
								/*MToast.makeText(jsonObject.getString("Message")).show();*/
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			ws.addWebServiceListener(wsl);
		}
		ws.SyncGet(property);
	}

	// 更新宝贝资料
	public static void updateDeviceForBabyInfo(final BabyInfo mContexts,
			final int ids, final List<WebServiceProperty> property,
			final Map<String, String> map, final boolean beLoad,
			final boolean isHeadEdit, final boolean isNameEdit,
			final boolean isPhoneEdit, final boolean isCornetEdit,
			final boolean isGenderEdit, final boolean isGradeEdit,
			final boolean isBirthdayEdit) {
		mContexts.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WebService ws = new WebService(mContexts, ids, beLoad, "UpdateDevice");
				ws.addWebServiceListener(new WebServiceListener() {
					@Override
					public void onWebServiceReceive(String method, int id, String result) {
						try {
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (ids == id) {
								int code = jsonObject.getIntValue("Code");
								if (code == 1) {
									// MToast.makeText(jsonObject.getString("Message")).show();

									WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
									if (isNameEdit) {
										mWatchModel.setName(map.get("babyName"));
									}
									if (isHeadEdit) {
//										MemoryCacheUtil.removeFromCache(mWatchModel.getAvatar(),ImageLoader.getInstance().getMemoryCache());
//										DiscCacheUtil.removeFromCache(mWatchModel.getAvatar(),ImageLoader.getInstance().getDiscCache());
										mWatchModel.setAvatar(jsonObject.getString("Photo"));
									}
									if (isPhoneEdit) {
										mWatchModel.setPhone(map.get("phoneNumber"));
									}
									if (isCornetEdit) {
										if (map.get("phoneCornet").equals("-1")) {
											mWatchModel.setCornet("");
										} else {
											mWatchModel.setCornet(map.get("phoneCornet"));
										}
									}
									if (isGenderEdit) {
										mWatchModel.setGender(map.get("gender"));
									}
									if (isBirthdayEdit) {
										mWatchModel.setBirthday(map.get("birthday"));
									}
									if (isGradeEdit) {
										mWatchModel.setGrade(Integer.valueOf(map.get("grade")));
									}
									WatchDao mWatchDao = new WatchDao(mContext);
									mWatchDao.updateWatch(AppData.GetInstance(mContext).getSelectDeviceId(),mWatchModel);
									/*
									 * finish();
									 * overridePendingTransition(R.anim
									 * .push_left_in, R.anim.push_left_out);
									 */
								} /*else if (code == -2) {
									// -2系统异常
									MToast.makeText(jsonObject.getString("Message")).show();
								} else if (code == -3) {
									// -3无权操作设备
									MToast.makeText(jsonObject.getString("Message")).show();
								} else if (code == -1) {
									// -1设备参数错误
								} else if (code == 0) {
									// 0登录异常
								}*/else{
									MToast.makeText(R.string.edit_fail).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				ws.SyncGet(property);
			}
		});
	}

	public static void updateDeviceForSchoolInfo(final SchoolInfo mContexts,
			final int ids, final List<WebServiceProperty> property,
			final Map<String, String> map, final boolean isClassA,
			final boolean isClassB, final boolean isWeek) {
		mContexts.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WebService ws = new WebService(mContexts, ids, false, "UpdateDeviceSet");
				ws.addWebServiceListener(new WebServiceListener() {
					@Override
					public void onWebServiceReceive(String method, int id, String result) {
						try {
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (ids == id) {
								int code = jsonObject.getIntValue("Code");
								if (code == 1) {
									// MToast.makeText(jsonObject.getString("Message")).show();

									WatchSetModel mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
									if (isClassA) {
										mWatchSetModel.setClassDisableda(map.get("classDisable1"));
									}
									if (isClassB) {
										mWatchSetModel.setClassDisabledb(map.get("classDisable2"));
									}
									if (isWeek) {
										mWatchSetModel.setWeekDisabled(map.get("weekDisable"));
									}
									WatchSetDao mWatchSetDao = new WatchSetDao(mContext);
									mWatchSetDao.updateWatchSet(AppData.GetInstance(mContext).getSelectDeviceId(), mWatchSetModel);
									/*
									 * finish();
									 * overridePendingTransition(R.anim
									 * .push_left_in, R.anim.push_left_out);
									 */
								}/* else if (code == -2) {
									// -2系统异常
									MToast.makeText(jsonObject.getString("Message")).show();
								} else if (code == -3) {
									// -3无权操作设备
									MToast.makeText(jsonObject.getString("Message")).show();
								} else if (code == -1) {
									// -1设备参数错误
								} else if (code == 0) {
									// 0登录异常
								}*/else{
									MToast.makeText(R.string.edit_fail).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				ws.SyncGet(property);
			}
		});
	}

	public static void updateDeviceForHomeInfo(final HomeInfo mContexts, final int ids, final String latestTime) {
		mContexts.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WebService ws = new WebService(mContexts, ids, false, "UpdateDevice");
				List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
				property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
				property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
				property.add(new WebServiceProperty("latestTime", latestTime));
				ws.addWebServiceListener(new WebServiceListener() {
					@Override
					public void onWebServiceReceive(String method, int id, String result) {
						try {
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (ids == id) {
								int code = jsonObject.getIntValue("Code");
								if (code == 1) {
									// MToast.makeText(jsonObject.getString("Message")).show();

									WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
									mWatchModel.setLastestTime(latestTime);

									WatchDao mWatchDao = new WatchDao(mContext);
									mWatchDao.updateWatch(AppData.GetInstance(mContext).getSelectDeviceId(), mWatchModel);
									/*
									 * finish();
									 * overridePendingTransition(R.anim
									 * .push_left_in, R.anim.push_left_out);
									 */
								}/* else if (code == -2) {
									// -2系统异常
									MToast.makeText(jsonObject.getString("Message")).show();
								} else if (code == -3) {
									// -3无权操作设备
									MToast.makeText(jsonObject.getString("Message")).show();
								} else if (code == -1) {
									// -1设备参数错误
								} else if (code == 0) {
									// 0登录异常
								}*/else{
									MToast.makeText(R.string.edit_fail).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				ws.SyncGet(property);
			}
		});
	}

	// 获取手表设置
	public static void GetDeviceSet(final Context context, final int ids, final String deviceId, WebServiceListener wsl, final boolean isRefreshLocal) {
		WebService ws = new WebService(context, ids, false, "GetDeviceSet");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("deviceId", deviceId));
		if (wsl == null) {
			ws.addWebServiceListener(new WebServiceListener() {
				@Override
				public void onWebServiceReceive(String method, int id, String result) {
					try {
						JSONObject jsonObject = JSONObject.parseObject(result);
						if (ids == id) {

							int code = jsonObject.getIntValue("Code");
							if (code == 1) {
								// 1成功
								WatchSetModel mWatchSetModel = new WatchSetModel();
								mWatchSetModel.setDeviceId(Integer.valueOf(deviceId));
								String[] strs = jsonObject.getString("SetInfo").split("-");
								mWatchSetModel.setAutoAnswer(strs[11]);
								mWatchSetModel.setReportLocation(strs[10]);
								mWatchSetModel.setSomatoAnswer(strs[9]);
								mWatchSetModel.setReservedPower(strs[8]);
								mWatchSetModel.setClassDisabled(strs[7]);
								mWatchSetModel.setTimeSwitch(strs[6]);
								mWatchSetModel.setRefusedStranger(strs[5]);
								mWatchSetModel.setWatchOffAlarm(strs[4]);
								mWatchSetModel.setCallSound(strs[3]);
								mWatchSetModel.setCallVibrate(strs[2]);
								mWatchSetModel.setMsgSound(strs[1]);
								mWatchSetModel.setMsgVibrate(strs[0]);
								mWatchSetModel.setClassDisableda(jsonObject.getString("ClassDisabled1"));
								mWatchSetModel.setClassDisabledb(jsonObject.getString("ClassDisabled2"));
								mWatchSetModel.setWeekDisabled(jsonObject.getString("WeekDisabled"));
								mWatchSetModel.setTimerOpen(jsonObject.getString("TimerOpen"));
								mWatchSetModel.setTimerClose(jsonObject.getString("TimerClose"));
								mWatchSetModel.setBrightScreen(jsonObject.getString("BrightScreen"));
								mWatchSetModel.setWeekAlarm1(jsonObject.getString("WeekAlarm1"));
								mWatchSetModel.setWeekAlarm2(jsonObject.getString("WeekAlarm2"));
								mWatchSetModel.setWeekAlarm3(jsonObject.getString("WeekAlarm3"));
								mWatchSetModel.setAlarm1(jsonObject.getString("Alarm1"));
								mWatchSetModel.setAlarm2(jsonObject.getString("Alarm2"));
								mWatchSetModel.setAlarm3(jsonObject.getString("Alarm3"));
								mWatchSetModel.setLocationMode(jsonObject.getString("LocationMode"));
								mWatchSetModel.setLocationTime(jsonObject.getString("LocationTime"));
								mWatchSetModel.setFlowerNumber(jsonObject.getString("FlowerNumber"));
								//mWatchSetModel.setLanguage(jsonObject.getString("Language"));
								//mWatchSetModel.setTimeZone(jsonObject.getString("TimeZone"));
								mWatchSetModel.setCreateTime(jsonObject.getString("CreateTime"));
								mWatchSetModel.setUpdateTime(jsonObject.getString("UpdateTime"));
								// mWatchSetModel.setVersionNumber(jsonObject.getString("VersionNumber"));
								mWatchSetModel.setSleepCalculate(jsonObject.getString("SleepCalculate"));
								mWatchSetModel.setStepCalculate(jsonObject.getString("StepCalculate"));
								mWatchSetModel.setHrCalculate(jsonObject.getString("HrCalculate"));
								mWatchSetModel.setSosMsgswitch(jsonObject.getString("SosMsgswitch"));
								WatchSetDao mWatchSetDao = new WatchSetDao(context);
								mWatchSetDao.saveWatchSet(mWatchSetModel);
								if (isRefreshLocal) {
									AppContext.getInstance().setSelectWatchSet(mWatchSetDao.getWatchSet(AppData.GetInstance(context).getSelectDeviceId()));
								}
								// MToast.makeText(R.string.wait_admin_confirm).show();
							} else {
								// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
								/*MToast.makeText(jsonObject.getString("Message")).show();*/
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			ws.addWebServiceListener(wsl);
		}
		ws.SyncGet(property);
	}

	// 获取手表状态
	public static void GetDeviceState(final Context context, final int ids, final String deviceId, WebServiceListener wsl) {
		WebService ws = new WebService(context, ids, false, "GetDeviceState");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("deviceId", deviceId));
		if (wsl == null) {
			ws.addWebServiceListener(new WebServiceListener() {

				@Override
				public void onWebServiceReceive(String method, int id, String result) {
					try {
						JSONObject jsonObject = JSONObject.parseObject(result);
						if (ids == id) {
							int code = jsonObject.getIntValue("Code");
							if (code == 1) {
								// 1成功
								WatchStateModel mWatchStateModel = new WatchStateModel();
								mWatchStateModel.setDeviceId(Integer.valueOf(deviceId));
								mWatchStateModel.setAltitude(jsonObject.getDouble("Altitude"));
								mWatchStateModel.setLatitude(jsonObject.getDouble("Latitude"));
								mWatchStateModel.setLongitude(jsonObject.getDouble("Longitude"));
								mWatchStateModel.setCourse(jsonObject.getString("Course"));
								mWatchStateModel.setElectricity(jsonObject.getString("Electricity"));
								mWatchStateModel.setStep(jsonObject.getString("Step"));
								mWatchStateModel.setHealth(jsonObject.getString("Health"));
								mWatchStateModel.setOnline(jsonObject.getString("Online"));
								mWatchStateModel.setSpeed(jsonObject.getString("Speed"));
								mWatchStateModel.setSatelliteNumber(jsonObject.getString("SatelliteNumber"));
								// mWatchStateModel.setSocketId(jsonObject.getString("SocketId"));
								mWatchStateModel.setCreateTime(jsonObject.getString("CreateTime"));
								mWatchStateModel.setServerTime(jsonObject.getString("ServerTime"));
								mWatchStateModel.setUpdateTime(jsonObject.getString("UpdateTime"));
								mWatchStateModel.setDeviceTime(jsonObject.getString("DeviceTime"));
								mWatchStateModel.setLocationType(jsonObject.getString("LocationType"));

								WatchStateDao mWatchStateDao = new WatchStateDao(context);
								mWatchStateDao.saveWatchState(mWatchStateModel);

								// MToast.makeText(R.string.wait_admin_confirm).show();
							} else {
								// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
								MToast.makeText(jsonObject.getString("Message")).show();
							}
							// MToast.makeText(R.string.wait_admin_confirm).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			ws.addWebServiceListener(wsl);
		}
		ws.SyncGet(property);
	}

	// 获取联系人列表
	public static void GetDeviceContact(final Context context, final int ids, final String deviceId, WebServiceListener wsl, final boolean refreshLocal, final boolean isUpdata) {
		WebService ws = new WebService(context, ids, true, "GetDeviceContact");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("deviceId", deviceId));

		if (wsl == null) {
			ws.addWebServiceListener(new WebServiceListener() {

				@Override
				public void onWebServiceReceive(String method, int id, String result) {
					try {
						JSONObject jsonObject = JSONObject.parseObject(result);
						if (ids == id) {
							int code = jsonObject.getIntValue("Code");
							if (code == 1) {
								// 1成功

								JSONArray arrContact = jsonObject.getJSONArray("ContactArr");
								ContactDao mContactDao = new ContactDao(context);
								mContactDao.deleteWatchContact(Integer.valueOf(deviceId));
								for (int j = 0; j < arrContact.size(); j++) {
									// System.out.println("" + j);
									JSONObject item = arrContact.getJSONObject(j);
									ContactModel mContactModel = new ContactModel();
									mContactModel.setId(item.getString("DeviceContactId"));
									mContactModel.setFromId(Integer.valueOf(deviceId));
									mContactModel.setObjectId(item.getString("ObjectId"));
									mContactModel.setRelationShip(item.getString("Relationship"));
									mContactModel.setAvatar(item.getString("Photo"));
									mContactModel.setAvatarUrl(item.getString("HeadImg"));
									mContactModel.setPhone(item.getString("PhoneNumber"));
									mContactModel.setCornet(item.getString("PhoneShort"));
									mContactModel.setType(item.getString("Type"));
									if (isUpdata) {
										mContactDao.updateContact(mContactModel.getId(), mContactModel);
									} else {
										mContactDao.saveContact(mContactModel);
									}
								}
								if (refreshLocal) {
									AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(mContext).getSelectDeviceId()));
								}
								// MToast.makeText(R.string.wait_admin_confirm).show();
							} else {
								// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
								/*MToast.makeText(jsonObject.getString("Message")).show();*/
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			ws.addWebServiceListener(wsl);
		}
		ws.SyncGet(property);
	}

	// 检查手表是否关联
	public static void LinkDeviceCheck(Activity context, int id, String serialNumber, WebServiceListener wsl) {
		WebService ws = new WebService(context, id, true, "LinkDeviceCheck");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("bindNumber", serialNumber));
		ws.addWebServiceListener(wsl);
		ws.SyncGet(property);
	}

	// 关联手表
	public static void LinkDevice(Activity context, int id, String photo, String name, String serialNumber, WebServiceListener wsl) {
		// System.out.println("LinkDevice" + str + " "+ serialNumber+ " " +
		// String.valueOf(selectRelation+1));
		WebService ws = new WebService(context, id, true, "LinkDevice");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		if (!photo.equals("-1")) {
			property.add(new WebServiceProperty("photo", photo));
		}
		property.add(new WebServiceProperty("name", name));
		property.add(new WebServiceProperty("bindNumber", serialNumber));
		String language;
		if(context.getResources().getConfiguration().locale.getCountry().equals("CN")){
			language = "2";
		}else if(context.getResources().getConfiguration().locale.getCountry().equals("TW")){
			language = "3";
		}else{
			language = "1";
		}
		property.add(new WebServiceProperty("language", language));
		property.add(new WebServiceProperty("timeZone", DateConversion.getTimeZoneMinute()));
		
		ws.addWebServiceListener(wsl);
		ws.SyncGet(property);
	}

	// 消息通知
	public static void UpdateNotification(MsgNoti mContexts, final int ids, final String notification, final String notificationSound, final String notificationVibration) {
		mContexts.runOnUiThread(new Runnable() {
			public void run() {
				WebService ws = new WebService(mContext, ids, false,"UpdateNotification");
				List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
				property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
				property.add(new WebServiceProperty("notification",notification));
				property.add(new WebServiceProperty("notificationSound",notificationSound));
				property.add(new WebServiceProperty("notificationVibration",notificationVibration));
				ws.SyncGet(property);
				ws.addWebServiceListener(new WebServiceListener() {
					@Override
					public void onWebServiceReceive(String method, int id, String result) {
						
						try {
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (ids == id) {
								int code = jsonObject.getIntValue("Code");
								if (code == 1) {
									AppData.GetInstance(mContext).setNotification(notification.equals("1") ? true : false);
									AppData.GetInstance(mContext).setNotificationSound(notificationSound.equals("1") ? true : false);
									AppData.GetInstance(mContext).setNotificationVibration(notificationVibration.equals("1") ? true : false);
								} else{
									MToast.makeText(R.string.edit_fail).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	// 获取联系人列表
	public static void RefreshDeviceState(final Activity context, final int ids, final String deviceId) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WebService ws = new WebService(context, ids, true, "RefreshDeviceState");
				List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
				property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
				property.add(new WebServiceProperty("deviceId", deviceId));
				ws.addWebServiceListener(new WebServiceListener() {

					@Override
					public void onWebServiceReceive(String method, int id, String result) {
						try {
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (ids == id) {
								int code = jsonObject.getIntValue("Code");
								if (code == 1) {
									// 1成功
									// MToast.makeText(R.string.wait_admin_confirm).show();
								} else {
									// -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
									MToast.makeText(R.string.send_fail).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				ws.SyncGet(property);
			}
		});
	}

	public static void getDeviceList(final Activity context) {
		WebService ws = new WebService(context, 0, true, "GetDeviceList");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(context).getLoginId()));
		ws.addWebServiceListener(new WebServiceListener() {

			@Override
			public void onWebServiceReceive(String method, int id, String result) {
				JSONObject jsonObject;
				try {
					jsonObject = JSONObject.parseObject(result);

					List<WatchModel> mWatchList = new ArrayList<WatchModel>();
					List<ContactModel> mContactModelList = new ArrayList<ContactModel>();
					JSONArray arr = jsonObject.getJSONArray("deviceList");
					int oldDeviceId = AppData.GetInstance(mContext).getSelectDeviceId();
					AppData.GetInstance(mContext).setSelectDeviceId(arr.getJSONObject(0).getIntValue("DeviceID"));

					for (int i = 0; i < arr.size(); i++) {
						JSONObject item = arr.getJSONObject(i);
						WatchModel mWatchModel = new WatchModel();
						if (oldDeviceId == item.getIntValue("DeviceID")) {
							AppData.GetInstance(mContext).setSelectDeviceId(item.getIntValue("DeviceID"));
						}
						mWatchModel.setId(item.getIntValue("DeviceID"));
						mWatchModel.setUserId(item.getIntValue("UserId"));
						mWatchModel.setModel(item.getString("DeviceModelID"));
						mWatchModel.setName(item.getString("BabyName"));
						mWatchModel.setAvatar(item.getString("Photo"));
						mWatchModel.setPhone(item.getString("PhoneNumber"));
						mWatchModel.setCornet(item.getString("PhoneCornet"));
						mWatchModel.setGender(item.getString("Gender"));
						mWatchModel.setBirthday(item.getString("Birthday"));
						mWatchModel.setGrade(item.getIntValue("Grade"));
						mWatchModel.setHomeAddress(item.getString("HomeAddress"));
						mWatchModel.setHomeLat(item.getDouble("HomeLat"));
						mWatchModel.setHomeLng(item.getDouble("HomeLng"));
						mWatchModel.setSchoolAddress(item.getString("SchoolAddress"));
						mWatchModel.setSchoolLat(item.getDouble("SchoolLat"));
						mWatchModel.setSchoolLng(item.getDouble("SchoolLng"));
						mWatchModel.setLastestTime(item.getString("LatestTime"));
						mWatchModel.setSetVersionNO(item.getString("SetVersionNO"));
						mWatchModel.setContactVersionNO(item.getString("ContactVersionNO"));
						mWatchModel.setOperatorType(item.getString("OperatorType"));
						mWatchModel.setSmsNumber(item.getString("SmsNumber"));
						mWatchModel.setSmsBalanceKey(item.getString("SmsBalanceKey"));
						mWatchModel.setSmsFlowKey(item.getString("SmsFlowKey"));
						mWatchModel.setActiveDate(item.getString("ActiveDate"));
						mWatchModel.setCreateTime(item.getString("CreateTime"));
						mWatchModel.setBindNumber(item.getString("BindNumber"));
						mWatchModel.setCurrentFirmware(item.getString("CurrentFirmware"));
						mWatchModel.setFirmware(item.getString("Firmware"));
						mWatchModel.setHireExpireDate(item.getString("HireExpireDate"));
						mWatchModel.setUpdateTime(item.getString("UpdateTime"));
						mWatchModel.setSerialNumber(item.getString("SerialNumber"));
						mWatchModel.setPassword(item.getString("Password"));
						mWatchModel.setIsGuard(item.getString("IsGuard").equals("1") ? true : false);
						mWatchModel.setDeviceType(item.getString("DeviceType"));
						mWatchList.add(mWatchModel);

						JSONObject deviceSet = item.getJSONObject("DeviceSet");
						WatchSetModel mWatchSetModel = new WatchSetModel();
						mWatchSetModel.setDeviceId(item.getIntValue("DeviceID"));
						String[] strs = deviceSet.getString("SetInfo").split("-");
						mWatchSetModel.setAutoAnswer(strs[11]);
						mWatchSetModel.setReportLocation(strs[10]);
						mWatchSetModel.setSomatoAnswer(strs[9]);
						mWatchSetModel.setReservedPower(strs[8]);
						mWatchSetModel.setClassDisabled(strs[7]);
						mWatchSetModel.setTimeSwitch(strs[6]);
						mWatchSetModel.setRefusedStranger(strs[5]);
						mWatchSetModel.setWatchOffAlarm(strs[4]);
						mWatchSetModel.setCallSound(strs[3]);
						mWatchSetModel.setCallVibrate(strs[2]);
						mWatchSetModel.setMsgSound(strs[1]);
						mWatchSetModel.setMsgVibrate(strs[0]);
						mWatchSetModel.setClassDisableda(deviceSet.getString("ClassDisabled1"));
						mWatchSetModel.setClassDisabledb(deviceSet.getString("ClassDisabled2"));
						mWatchSetModel.setWeekDisabled(deviceSet.getString("WeekDisabled"));
						mWatchSetModel.setTimerOpen(deviceSet.getString("TimerOpen"));
						mWatchSetModel.setTimerClose(deviceSet.getString("TimerClose"));
						mWatchSetModel.setBrightScreen(deviceSet.getString("BrightScreen"));
						mWatchSetModel.setWeekAlarm1(deviceSet.getString("WeekAlarm1"));
						mWatchSetModel.setWeekAlarm2(deviceSet.getString("WeekAlarm2"));
						mWatchSetModel.setWeekAlarm3(deviceSet.getString("WeekAlarm3"));
						mWatchSetModel.setAlarm1(deviceSet.getString("Alarm1"));
						mWatchSetModel.setAlarm2(deviceSet.getString("Alarm2"));
						mWatchSetModel.setAlarm3(deviceSet.getString("Alarm3"));
						mWatchSetModel.setLocationMode(deviceSet.getString("LocationMode"));
						mWatchSetModel.setLocationTime(deviceSet.getString("LocationTime"));
						mWatchSetModel.setFlowerNumber(deviceSet.getString("FlowerNumber"));
						//mWatchSetModel.setLanguage(deviceSet.getString("Language"));
						//mWatchSetModel.setTimeZone(deviceSet.getString("TimeZone"));
						mWatchSetModel.setCreateTime(deviceSet.getString("CreateTime"));
						mWatchSetModel.setUpdateTime(deviceSet.getString("UpdateTime"));
						// mWatchSetModel.setVersionNumber(deviceSet.getString("VersionNumber"));
						mWatchSetModel.setSleepCalculate(deviceSet.getString("SleepCalculate"));
						mWatchSetModel.setStepCalculate(deviceSet.getString("StepCalculate"));
						mWatchSetModel.setHrCalculate(deviceSet.getString("HrCalculate"));
						mWatchSetModel.setSosMsgswitch(deviceSet.getString("SosMsgswitch"));

						WatchSetDao mWatchDao = new WatchSetDao(context);
						mWatchDao.saveWatchSet(mWatchSetModel);

						JSONObject deviceState = item.getJSONObject("DeviceState");
						WatchStateModel mWatchStateModel = new WatchStateModel();
						mWatchStateModel.setDeviceId(item.getIntValue("DeviceID"));
						if (!TextUtils.isEmpty(deviceState.getString("Altitude"))) {
							mWatchStateModel.setAltitude(deviceState.getDouble("Altitude"));
						}
						if (!TextUtils.isEmpty(deviceState.getString("Latitude"))) {
							mWatchStateModel.setLatitude(deviceState.getDouble("Latitude"));
						}
						if (!TextUtils.isEmpty(deviceState.getString("Longitude"))) {
							mWatchStateModel.setLongitude(deviceState.getDouble("Longitude"));
						}
						mWatchStateModel.setCourse(deviceState.getString("Course"));
						mWatchStateModel.setElectricity(deviceState.getString("Electricity"));
						mWatchStateModel.setStep(deviceState.getString("Step"));
						mWatchStateModel.setHealth(deviceState.getString("Health"));
						mWatchStateModel.setOnline(deviceState.getString("Online"));
						mWatchStateModel.setSpeed(deviceState.getString("Speed"));
						mWatchStateModel.setSatelliteNumber(deviceState.getString("SatelliteNumber"));
						// mWatchStateModel.setSocketId(deviceState.getString("SocketId"));
						mWatchStateModel.setCreateTime(deviceState.getString("CreateTime"));
						mWatchStateModel.setServerTime(deviceState.getString("ServerTime"));
						mWatchStateModel.setUpdateTime(deviceState.getString("UpdateTime"));
						mWatchStateModel.setDeviceTime(deviceState.getString("DeviceTime"));
						mWatchStateModel.setLocationType(deviceState.getString("LocationType"));

						WatchStateDao mWatchStateDao = new WatchStateDao(context);
						mWatchStateDao.saveWatchState(mWatchStateModel);

						JSONArray arrContact = item.getJSONArray("ContactArr");
						for (int j = 0; j < arrContact.size(); j++) {
							JSONObject items = arrContact.getJSONObject(j);
							ContactModel mContactModel = new ContactModel();
							mContactModel.setId(items.getString("DeviceContactId"));
							mContactModel.setFromId(item.getIntValue("DeviceID"));
							mContactModel.setObjectId(items.getString("ObjectId"));
							mContactModel.setRelationShip(items.getString("Relationship"));
							mContactModel.setAvatar(items.getString("Photo"));
							mContactModel.setAvatarUrl(items.getString("HeadImg"));
							mContactModel.setPhone(items.getString("PhoneNumber"));
							mContactModel.setCornet(items.getString("PhoneShort"));
							mContactModel.setType(items.getString("Type"));
							mContactModelList.add(mContactModel);
						}
						ContactDao mContactDao = new ContactDao(context);
						mContactDao.deleteWatchContact(item.getIntValue("DeviceID"));
					}
					WatchDao dao = new WatchDao(context);
					dao.saveWatchList(mWatchList);
					ContactDao mContactDao = new ContactDao(context);
					mContactDao.saveContactList(mContactModelList);

					AppContext.getInstance().setWatchMap(dao.getWatchMap());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		ws.SyncGet(property);
	}
}
