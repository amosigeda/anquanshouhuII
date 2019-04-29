package vip.inteltech.gat.comm;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import vip.inteltech.coolbaby.BuildConfig;

public final class Constants {

    private Constants() {
        // hide me
    }

    public static final String TAG = "GKT";

    public static Locale LOCALE = Locale.getDefault();
    public static String LOCALE_STR = LOCALE.toString();

    public static final String DEFAULT_BLANK = "";
    public static final String DEFAULT_SPACE = " ";
    public static final Object DEFAULT_OBJECT = new Object();

    public static final SimpleDateFormat FMT_DATE_TIME = new SimpleDateFormat(LOCALE_STR.equals("zh_CN") ? "yyyy-MM-dd HH:mm:ss" : "M/d/yyyy HH:mm:ss", LOCALE);
    public static final SimpleDateFormat FMT_DATE = new SimpleDateFormat(LOCALE_STR.equals("zh_CN") ? "yyyy-MM-dd" : "M/d/yyyy", LOCALE);
    public static final SimpleDateFormat FMT_DATE_MONTH = new SimpleDateFormat(LOCALE_STR.equals("zh_CN") ? "MM-dd" : "MM/d", LOCALE);
    public static final SimpleDateFormat FMT_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat FMT_TIME_MINUTE = new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * 保留两位小数，如果有的话
     */
    public static final DecimalFormat FMT_DECIMAL = new DecimalFormat("#.##");
    /**
     * 强制保留两位小数
     */
    public static final DecimalFormat FMT_DECIMAL_FORCE = new DecimalFormat("0.00");

//    public static final String[] STS=new String[]{"MQNAW","HXNDX","WSZDANXD","WXNGZQM","MQIMYSM"};

    /**
     * 经纬度坐标组分隔符
     */
    public static final String POSI_GROUP_SEP = ";";
    /**
     * 经纬度坐标分隔符
     */
    public static final String POSI_ITEM_SEP = ",";
    public static final char WAVE_SEP = ',';

    /**
     * 地球半径（单位：米），来自NASA网站：
     * http://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html
     */
    public static final Double EARTH_RADIUS = 6371000D;

    public static final String REFRESH_FLAG = "REFRESH_FLAG";


    //EVENT_TAG
    public static final String EVENT_BLE_CONNECT_CHANGED = "EVENT_BLE_CONNECT_CHANGED";
    public static final String EVENT_LOGOUT_DIRECTLY = "EVENT_LOGOUT_DIRECTLY";
    public static final String EVENT_NETWORK_ERROR = "EVENT_NETWORK_ERROR";

    public static final String MSG_VOICE = "MSG_VOICE";
    public static final String MSG_VIBRATE = "MSG_VIBRATE";
    public static final String MSG_VOICE_KEEP = "MSG_VOICE_KEEP";

    public static final String TIME_ZONE_PREFIX = "UTC ";

    public static final String SCHEME_FILE = "file://";
    public static final String SCHEME_ASSETS = "assets://";
    public static final String SCHEME_RES = "res://";


    /**
     * 中国大陆常规手机号码与紧急号码，如110等
     */
    public static final String MOBILE_FMT_CHINA = "^((\\+?86)?1\\d{10}|(\\d{3}))$";
    /**
     * 国际手机号格式
     */
    public static final String MOBILE_FMT_I18N = "^\\+?\\d{1,16}$";

    public static final String FILE_PROVIDER_AUTHORITIES = BuildConfig.APPLICATION_ID + ".fileprovider";
    /**
     * 结尾不带 "/"
     */
    public static String baseUrl;
    public static final String SETTING_SERVER_URL = "SERVER_URL";
    public static final String SETTING_SERVER_URL_ENABLE = "SERVER_URL_ENABLE";
    public static final double VOLTAGE_UPPER = 4.17;
    public static final double VOLTAGE_LOWER = 3.4;

    /**
     * NET_TIMEOUT,Seconds.
     */
    public static final long NET_TIMEOUT = 10;

    public static final String USER_AGENT_KEY="User-Agent";
    public static final String USER_AGENT="Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1";
}