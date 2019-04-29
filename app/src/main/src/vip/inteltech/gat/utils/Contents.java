package vip.inteltech.gat.utils;

import vip.inteltech.coolbaby.BuildConfig;

public class Contents {
    public static String[] SERVER_INFO = BuildConfig.SERVER_URL.split(",");
    //	public final static String Ip = "http://app.dami999.com";
    public final static String TEST_URL = SERVER_INFO[0]; //"https://apps.8kk.win";
    public final static String Ip = SERVER_INFO[1]; //"https://apps.8kk.win";
    //	public final static String Ip = "http://192.168.1.199";
    public final static String APPName = BuildConfig.APP_NAME; //"CoolBaby";
    public final static boolean Debug = BuildConfig.DEBUG;
    public final static String chatBrodcastForSelectWatch = APPName + ".chatBrodcastForSelectWatch";
    public final static String BrodcastForUnread = APPName + ".BrodcastForUnread";
    public final static String askBindingBrodcast = APPName + ".askBindingBrodcast";
    public final static String changeStateBrodcastForSelectWatch = APPName + ".changeStateBrodcastForSelectWatch";
    public final static String getSMSBrodcast = APPName + ".getSMSBrodcast";
    public final static String getMsgRecordBrodcast = APPName + ".getMsgRecordBrodcast";
    public final static String refreshContactBrodcast = APPName + ".refreshContactBrodcast";
    public final static String getPhotoBrodcast = APPName + ".getPhotoBrodcast";
    public final static String refreshFriendBrodcast = APPName + ".refreshFriendBrodcast";

    public final static String IMAGEVIEW_URL = SERVER_INFO[2];//Ip + ":6700/IFile/GetImage?path=";
    public final static String VOICE_URL = SERVER_INFO[3];//Ip + ":6700/IFile/GetAMR?path=";
    public final static int CHATMSGINITIAL = 30;
    public final static int CHATMSGREFRESH = 20;
    public final static String SERVICETIMEZONE = "GMT08:00";

    public boolean canDropAlarm = AppContext.getInstance().getmWatchModel().getModel().toCharArray()[7] == '1';
    public boolean canEditHead = AppContext.getInstance().getmWatchModel().getModel().toCharArray()[6] == '1';
    public boolean canCamera = AppContext.getInstance().getmWatchModel().getModel().toCharArray()[5] == '1';
    public boolean canLanguageTimeZone = AppContext.getInstance().getmWatchModel().getModel().toCharArray()[4] == '1';
    public boolean canHistoryTrack = AppContext.getInstance().getmWatchModel().getModel().toCharArray()[0] == '1';

    public final static String APP_ID = "hmQfXaEmiZd3c4U9ue";
    public final static String APP_SECRET = "74780ccbc705bfe0fca1cc96ba3e26e1";
    public final static String SPACE_ID = "854";
    public final static String SPACE_ID_BANNER = "855";
}