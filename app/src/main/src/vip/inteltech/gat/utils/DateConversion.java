package vip.inteltech.gat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import vip.inteltech.gat.comm.Constants;

public class DateConversion {
    /**
     * yyyy/mm/dd 转 yyyy年MM月dd日或MMM d, yyyy
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilA(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy",
                Locale.ENGLISH);
        if (AppContext.getContext().getResources()
                .getConfiguration().locale.getCountry().equals("CN")) {
            try {
                dates = sdf1.format(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dates = sdf2.format(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    /**
     * yyyy年MM月dd日或MMM d, yyyy 转 yyyy/mm/dd
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilAA(String date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy",
                Locale.ENGLISH);
        if (AppContext.getInstance().getContext().getResources()
                .getConfiguration().locale.getCountry().equals("CN")) {
            try {
                dates = sdf.format(sdf1.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dates = sdf.format(sdf2.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    /**
     * int yyyy int mm int dd 转 yyyy年MM月dd日或MMM d, yyyy
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String DateConversionUtilA(int year, int month, int day) {
        String dates = "";
        dates = DateConversionUtilD(year, month, day);
        dates = DateConversionUtilA(dates);
        return dates;
    }

    /**
     * int hh int mm 转 hh:mm
     *
     * @param hour
     * @param minute
     * @return
     */
    public static String DateConversionUtilA(int hour, int minute) {
        String dates = "";
        if (hour < 10) {
            dates = dates + "0" + String.valueOf(hour) + ":";
        } else {
            dates = dates + String.valueOf(hour) + ":";
        }

        if (minute >= 10) {
            dates = dates + String.valueOf(minute);
        } else {
            dates = dates + "0" + String.valueOf(minute);
        }
        return dates;
    }

    /**
     * 将date转 yyyy年MM月dd日或MMM d, yyyy
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilA(Date date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        if (AppContext.getInstance().getContext().getResources()
                .getConfiguration().locale.getCountry().equals("CN")) {
            dates = sdf1.format(date);
        } else {
            dates = sdf2.format(date);
        }
        return dates;
    }

    /**
     * 将date转 MM月dd日 hh:mm或MMM d hh:mm
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilE(Date date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm MMM d", Locale.ENGLISH);
        if (AppContext.getInstance().getContext().getResources()
                .getConfiguration().locale.getCountry().equals("CN")) {
            dates = sdf1.format(date);
        } else {
            dates = sdf2.format(date);
        }
        return dates;
    }

    /**
     * 将date转yyyy年 MM月dd日 hh:mm或MMM d, yyyy hh:mm
     */
    public static String DateConversionUtilF(Date date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm MMM d, yyyy",
                Locale.ENGLISH);
        if (AppContext.getInstance().getContext().getResources()
                .getConfiguration().locale.getCountry().equals("CN")) {
            dates = sdf1.format(date);
        } else {
            dates = sdf2.format(date);
        }
        return dates;
    }

    /**
     * 将date转yyyy年 MM月dd日 hh:mm或MMM d, yyyy hh:mm
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilF(String date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            dates = DateConversionUtilF(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dates;
    }

    /**
     * yyyy/mm 转 yyyy年MM月或MMM, yyyy
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilB(String date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM, yyyy", Locale.ENGLISH);
        if (AppContext.getInstance().getContext().getResources().getConfiguration().locale.getCountry().equals("CN")) {
            try {
                dates = sdf1.format(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dates = sdf2.format(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    public static int[] DateConversionUtilC(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String[] strs = date.split("/");
        int[] mDate;
        mDate = new int[]{Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
                Integer.valueOf(strs[2])};
        return mDate;
    }

    /**
     * date转yyyy/mm/dd
     *
     * @param date
     * @return
     */
    public static String DateConversionUtilC(Date date) {
        String dates = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        dates = sdf.format(date);
        return dates;
    }

    /**
     * int yyyy int mm int dd 转 yyyy/MM/dd
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String DateConversionUtilD(int year, int month, int day) {
        String dates = "";
        dates = String.valueOf(year) + "/";
        if (month < 10) {
            dates = dates + "0" + String.valueOf(month) + "/";
        } else {
            dates = dates + String.valueOf(month) + "/";
        }

        if (day >= 10) {
            dates = dates + String.valueOf(day);
        } else {
            dates = dates + "0" + String.valueOf(day);
        }
        return dates;
    }

    public static String TimeChange(String time_a, String time_b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            if (CommUtil.isBlank(time_a)) {
                return Constants.DEFAULT_BLANK;
            }
            Date date_a = sdf.parse(time_a);
            if (TextUtils.isEmpty(time_b)) {
                return TimeDeferent(date_a);
            }
            Date date_b = sdf.parse(time_b);
            if (date_a.getYear() == date_b.getYear()) {
                if (date_a.getMonth() == date_b.getMonth()) {
                    if (date_a.getDay() == date_b.getDay()) {

                        if (date_a.getHours() == date_b.getHours()) {
                            if (date_a.getMinutes() == date_b.getMinutes()) {
                                return "";
                            } else if (date_a.getMinutes()
                                    - date_b.getMinutes() > 3) {
                                return "";
                            } else {
                                return TimeDeferent(date_a);
                            }
                        } else {
                            return TimeDeferent(date_a);
                        }
                    } else {
                        return TimeDeferent(date_a);
                    }
                } else {
                    return TimeDeferent(date_a);
                }
            } else {
                return TimeDeferent(date_a);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    private static String TimeDeferent(Date date_a) {
        Calendar calendar = Calendar.getInstance();// 取系统时间
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour = calendar.get(Calendar.MINUTE);
        Calendar calendar_a = Calendar.getInstance();
        calendar_a.setTime(date_a);
        int year_a = calendar_a.get(Calendar.YEAR);
        int monthOfYear_a = calendar_a.get(Calendar.MONTH) + 1;
        int dayOfMonth_a = calendar_a.get(Calendar.DAY_OF_MONTH);
        int hourOfDay_a = calendar_a.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour_a = calendar_a.get(Calendar.MINUTE);
        /*
         * System.out.println(dayOfMonth+"  "+monthOfYear+"  "+ year);
         * System.out.println(dayOfMonth_a+"  "+monthOfYear_a+"  "+ year_a);
         */
        if (year_a == year) {
            if (monthOfYear_a == monthOfYear) {
//				if (dayOfMonth_a == dayOfMonth) {
//					/*
//					 * System.out.println(DateConversionUtilA(date_a.getHours(),
//					 * date_a.getMinutes()));
//					 */
//					return DateConversionUtilA(date_a.getHours(),
//							date_a.getMinutes());
//				} else {
                return DateConversionUtilE(date_a);
//				}
            } else {
                return DateConversionUtilE(date_a);
            }
        } else {
            return DateConversionUtilF(date_a);
        }
    }

    /**
     * 获取今天日期
     *
     * @return
     */
    public static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String getTime1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    /**
     * 比较时间
     *
     * @param time_a
     * @param time_b
     * @return
     */
    public static boolean timeComparison(String time_a, String time_b) {
        if (TextUtils.isEmpty(time_a) || TextUtils.isEmpty(time_b)) {
            return true;
        }
        if (time_a.equals(time_b)) {
            return false;
        }
        int hour_a = Integer.valueOf(time_a.split(":")[0]),
                minute_a = Integer.valueOf(time_a.split(":")[1]),
                hour_b = Integer.valueOf(time_b.split(":")[0]),
                minute_b = Integer.valueOf(time_b.split(":")[1]);
        if (hour_a < hour_b) {
            return true;
        } else if (hour_a == hour_b) {
            if (minute_a < minute_b) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static Date converTimesDate(String srcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date result_date = new Date();

        if (srcTime != null) {
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
                result_date = sdf.parse(srcTime);
            } catch (Exception e) {
            }
        }

        return result_date;
    }

    /**
     * utc转本地时间
     *
     * @param srcTime
     * @return
     */
    public static String converTimes(String srcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String convertTime;

        Date result_date;
        long result_time = 0;

        // 如果传入参数异常，使用本地时间
        if (null == srcTime) {
            result_time = System.currentTimeMillis();
        } else {
            // 将输入时间字串转换为UTC时间
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
                result_date = sdf.parse(srcTime);

                result_time = result_date.getTime();
            } catch (Exception e) {
                // 出现异常时，使用本地时间
                result_time = System.currentTimeMillis();
                dspFmt.setTimeZone(TimeZone.getDefault());
                convertTime = dspFmt.format(result_time);
                return convertTime;
            }
        }
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        // 设定时区
        dspFmt.setTimeZone(localZone);
        convertTime = dspFmt.format(result_time);

        return convertTime;
    }

    /**
     * 本地时间转utc
     *
     * @param srcTime
     * @return
     */
    public static String converToUTCTime(String srcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String convertTime;

        Date result_date;
        long result_time = 0;

        // 如果传入参数异常，使用本地时间
        if (null == srcTime) {
            result_time = System.currentTimeMillis();
        } else {
            try {
                sdf.setTimeZone(TimeZone.getDefault());
                result_date = sdf.parse(srcTime);
                result_time = result_date.getTime();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                // 出现异常时，使用本地时间
                result_time = System.currentTimeMillis();
                sdf.setTimeZone(TimeZone.getDefault());
                convertTime = sdf.format(result_time);
                return convertTime;
            }

        }
        sdf.setTimeZone(TimeZone.getDefault());
        convertTime = sdf.format(result_time);

        return convertTime;
    }

    /**
     * 服务器时间转本地时间
     *
     * @param srcTime
     * @return
     */
    public static String converTimeToPhoneTime(String srcTime) {

        SimpleDateFormat sdf;
        if (srcTime.contains("/")) {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        } else {
            sdf = new SimpleDateFormat("HH:mm");
        }
        String convertTime;

        Date result_date;
        long result_time = 0;

        // 如果传入参数异常，使用本地时间
        if (null == srcTime) {
            result_time = System.currentTimeMillis();
        } else {
            try {
                sdf.setTimeZone(TimeZone.getTimeZone(Contents.SERVICETIMEZONE));
                result_date = sdf.parse(srcTime);
                result_time = result_date.getTime();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                // 出现异常时，使用本地时间
                result_time = System.currentTimeMillis();
                sdf.setTimeZone(TimeZone.getDefault());
                convertTime = sdf.format(result_time);
                return convertTime;
            }

        }
        sdf.setTimeZone(TimeZone.getDefault());
        convertTime = sdf.format(result_time);

        return convertTime;
    }

    /**
     * 本地时间转服务器时间
     *
     * @param srcTime
     * @return
     */
    public static String converTimeToServiceTime(String srcTime) {

        SimpleDateFormat sdf;
        if (srcTime.contains("/")) {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        } else {
            sdf = new SimpleDateFormat("HH:mm");
        }
        String convertTime;

        Date result_date;
        long result_time = 0;

        // 如果传入参数异常，使用本地时间
        if (null == srcTime) {
            result_time = System.currentTimeMillis();
        } else {
            try {
                sdf.setTimeZone(TimeZone.getDefault());
                result_date = sdf.parse(srcTime);
                result_time = result_date.getTime();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                // 出现异常时，使用本地时间
                result_time = System.currentTimeMillis();
                sdf.setTimeZone(TimeZone.getDefault());
                convertTime = sdf.format(result_time);
                return convertTime;
            }

        }
        sdf.setTimeZone(TimeZone.getTimeZone(Contents.SERVICETIMEZONE));
        convertTime = sdf.format(result_time);

        return convertTime;
    }

    /**
     * 获取当前时区对应的分钟数
     *
     * @return
     */
    public static String getTimeZoneMinute() {
        String[] timeZoneMintue = new String[]{"-720", "-660", "-600", "-540", "-480", "-420", "-360", "-300", "-270", "-240", "-210", "-180", "-120", "-60", "0",
                "60", "120", "180", "210", "240", "270", "300", "330", "345", "360", "390", "420", "480", "540", "570", "600", "660", "720", "780", "840"};
        String[] timezoneDatas = new String[]{"GMT-12:00", "GMT-11:00", "GMT-10:00", "GMT-09:00", "GMT-08:00", "GMT-07:00",
                "GMT-06:00", "GMT-05:00", "GMT-04:30", "GMT-04:00", "GMT-03:30", "GMT-03:00", "GMT-02:00", "GMT-01:00",
                "GMT", "GMT+01:00", "GMT+02:00", "GMT+03:00", "GMT+03:30", "GMT+04:00", "GMT+04:30", "GMT+05:00", "GMT+05:30",
                "GMT+05:45", "GMT+06:00", "GMT+06:30", "GMT+07:00", "GMT+08:00", "GMT+09:00", "GMT+09:30", "GMT+10:00", "GMT+11:00",
                "GMT+12:00", "GMT+13:00", "GMT+14:00"};
        for (int i = 0; i < timezoneDatas.length; i++) {
            if (TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT, Locale.ENGLISH).equals(timezoneDatas[i])) {
                return timeZoneMintue[i];
            }
        }
        return "480";
    }
}
