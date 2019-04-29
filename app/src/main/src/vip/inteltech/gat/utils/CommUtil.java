package vip.inteltech.gat.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import android.os.Message;
import android.util.Log;

import vip.inteltech.gat.comm.CommHandler;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.inter.CommCallback;

/**
 * Created by HH
 * Date: 2015/6/11 0011
 * Time: 下午 13:56
 */
public class CommUtil {
    /**
     * 对象到字符串
     *
     * @param obj
     * @return 字符串，默认为""
     */
    public static String toStr(Object obj) {
        return toStr(obj, Constants.DEFAULT_BLANK);
    }

    /**
     * 对象到字符串
     *
     * @param obj
     * @param defaultVal
     * @return 字符串，默认为 defaultVal 的值
     */
    public static String toStr(Object obj, String defaultVal) {
        if (obj == null || "NULL".equalsIgnoreCase(obj.toString())) {
            return defaultVal;
        } else {
            return obj.toString();
        }
    }

    /**
     * 获取时间 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDateTime() {
        return Constants.FMT_DATE_TIME.format(new Date());
    }

    /**
     * 获取时间 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDateTime(Date date) {
        return Constants.FMT_DATE_TIME.format(date);
    }

    /**
     * 获取日期 格式：yyyy-MM-dd
     *
     * @return
     */
    public static String getDate() {
        return Constants.FMT_DATE.format(new Date());
    }

    /**
     * 获取日期  格式： 中文 yyyy-MM-dd 其它： MM/dd/yyyy
     *
     * @return
     */
    public static String getDate(Date date) {
        if (date == null) {
            return null;
        }
        return Constants.FMT_DATE.format(date);
    }

    /**
     * 将字符串时间转换成Date类型（yyyy-MM-dd HH:mm:ss） String dateTimeStr --日期对象的字符串
     *
     * @param dateTimeStr
     * @return
     * @throws ParseException
     */
    public static Date getDateTimeFromStr(String dateTimeStr) {
        try {
            return Constants.FMT_DATE_TIME.parse(dateTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串时间转换成Date类型（yyyy-MM-dd） String dateStr --日期对象的字符串
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date getDateFromStr(String dateStr) {
        try {
            return Constants.FMT_DATE.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Integer toInteger(Object obj) {
        return toInteger(obj, null);
    }

    public static Integer toInteger(Object obj, Integer defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(toStr(obj));
        } catch (Exception e) {
            Log.e(Constants.TAG, "Parse obj to Integer error!", e);
            return defaultVal;
        }
    }

    public static Long toLong(Object obj) {
        return toLong(obj, null);
    }

    public static Long toLong(Object obj, Long defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(toStr(obj));
        } catch (Exception e) {
            Log.e(Constants.TAG, "Parse obj to Long error!", e);
            return defaultVal;
        }
    }

    public static Double toDouble(Object obj) {
        return toDouble(obj, null);
    }

    public static Double toDouble(Object obj, Double defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.parseDouble(toStr(obj));
        } catch (Exception e) {
            Log.e(Constants.TAG, "Parse obj to Long error!", e);
            return defaultVal;
        }
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    public static boolean isBlank(Object object) {
        if (object == null) {
            return true;
        }
        int strLen;
        String str = object.toString();
        if ((strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(Object object) {
        return !isBlank(object);
    }

    public static void sendMsg(int what) {
        Message msg = CommHandler.getHandler().obtainMessage(what);
        CommHandler.getHandler().sendMessage(msg);
    }

    public static void sendMsg(int what, Object obj) {
        Message msg = CommHandler.getHandler().obtainMessage(what, obj);
        CommHandler.getHandler().sendMessage(msg);
    }

    public static void showMsgShort(int resId) {
        showMsgShort(AppContext.getContext().getString(resId));
    }

    public static void showMsgLong(int resId) {
        showMsgLong(AppContext.getContext().getString(resId));
    }

    public static void showMsgShort(String msg) {
        if (CommUtil.isBlank(msg)) {
            return;
        }
        Message message = CommHandler.getHandler().obtainMessage(CommHandler.TOAST_SHORT, msg);
        CommHandler.getHandler().sendMessage(message);
    }

    public static void showMsgLong(String msg) {
        if (CommUtil.isBlank(msg)) {
            return;
        }
        Message message = CommHandler.getHandler().obtainMessage(CommHandler.TOAST_LONG, msg);
        CommHandler.getHandler().sendMessage(message);
    }


    public static void delayExecute(long delayMillis, CommCallback callback) {
        Message message = CommHandler.getHandler().obtainMessage(CommHandler.DELAY_EXECUTE, callback);
        CommHandler.getHandler().sendMessageDelayed(message, delayMillis);
    }

    /**
     * 将以米数为单位的距离转换成纬度度数
     * (避免用Double初始化BigDecimal类型)
     *
     * @param metre
     * @return
     */
    public static double metreToLatDegree(Double metre) {
        return new BigDecimal(toStr(metre)).multiply(new BigDecimal(180)).divide(new BigDecimal(toStr(Math.PI))
                .multiply(new BigDecimal(toStr(Constants.EARTH_RADIUS))), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double lonLanToDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, R = Constants.EARTH_RADIUS;
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    public static int min(int... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        int res = args[0];
        for (int i = 1; i < args.length; i++) {
            if (res > args[i]) {
                res = args[i];
            }
        }
        return res;
    }

    public static String join(Object[] objs, String seprator) {
        if (objs == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objs.length; i++) {
            if (i > 0) {
                sb.append(seprator);
            }
            sb.append(objs[i]);
        }
        return sb.toString();
    }

    public static String generateUniqueId(Object seed, int len) {
        if (seed == null) {
            return null;
        }
        if (len < 0 || len > 50) {
            len = 15;
        }
        String code;
        String sd = seed.toString();
        if (!sd.matches("^\\d+$")) {
            code = String.valueOf(Math.abs(sd.hashCode()));
        } else {
            code = String.valueOf(Math.abs(Long.parseLong(sd)));
        }
        StringBuffer sb = new StringBuffer().append(code).append(code.length());
        int h = 0;
        while (sb.length() < len) {
            for (int i = 0; i < sb.length(); i++) {
                h = 31 * h + sb.charAt(i);
            }
            sb.append(Math.abs(h));
        }
        return sb.substring(0, len);
    }

    public static String genSpecificName(String seed, int range) {
        if (range == 0) {
            range = 100;
        }
        if (seed == null) {
            seed = Integer.valueOf(new Random().nextInt()).toString();
        }
        return Integer.valueOf(Math.abs(seed.hashCode() % range)).toString();
    }

//    public static String toPinYin(String src,HanyuPinyinOutputFormat format,boolean outputMultiSyllable,String separator) {
//        if (src == null) {
//            return null;
//        }
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < src.length(); i++) {
//            try {
//                char c = src.charAt(i);
//                String[] res = PinyinHelper.toHanyuPinyinStringArray(c, format);
//                if (res == null) {
//                    sb.append(c);
//                } else {
//                    Set<String> set=new HashSet<>();
//                    set.addAll(Arrays.asList(res));
//                    int j=0;
//                    for (String s:set) {
//                        sb.append(s);
//                        if (!outputMultiSyllable) {
//                            break;
//                        }
//                        if (separator != null && j < set.size() - 1) {
//                            sb.append(separator);
//                        }
//                        j++;
//                    }
//                    if (separator != null && i < src.length() - 1) {
//                        sb.append(separator);
//                    }
//                }
//            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//                badHanyuPinyinOutputFormatCombination.printStackTrace();
//            }
//        }
//        return sb.toString();
//    }

    /**
     * 根据当前选择的秒数还原时间点
     *
     * @param
     */
    public static String getCheckTimeBySeconds(int progress) {

        String startTime = "00:00:00";
        String return_h = "", return_m = "", return_s = "";

        String[] st = startTime.split(":");

        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);

        int h = progress / 3600;

        int m = (progress % 3600) / 60;

        int s = progress % 60;

        if ((s + st_s) >= 60) {

            int tmpSecond = (s + st_s) % 60;

            m = m + 1;

            if (tmpSecond >= 10) {
                return_s = tmpSecond + "";
            } else {
                return_s = "0" + (tmpSecond);
            }

        } else {
            if ((s + st_s) >= 10) {
                return_s = s + st_s + "";
            } else {
                return_s = "0" + (s + st_s);
            }

        }

        if ((m + st_m) >= 60) {

            int tmpMin = (m + st_m) % 60;

            h = h + 1;

            if (tmpMin >= 10) {
                return_m = tmpMin + "";
            } else {
                return_m = "0" + (tmpMin);
            }

        } else {
            if ((m + st_m) >= 10) {
                return_m = (m + st_m) + "";
            } else {
                return_m = "0" + (m + st_m);
            }

        }

        if ((st_h + h) < 10) {
            return_h = "0" + (st_h + h);
        } else {
            return_h = st_h + h + "";
        }

        return return_h + ":" + return_m + ":" + return_s;
    }

    /**
     * 计算连个时间之间的秒数
     */
    public static int totalSeconds(String startTime, String endTime) {

        String[] st = startTime.split(":");
        String[] et = endTime.split(":");

        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);

        int et_h = Integer.valueOf(et[0]);
        int et_m = Integer.valueOf(et[1]);
        int et_s = Integer.valueOf(et[2]);

        int totalSeconds = (et_h - st_h) * 3600 + (et_m - st_m) * 60 + (et_s - st_s);

        return totalSeconds;

    }

    /**
     * 计算字符串对应的ASCII字符长度
     *
     * @param str
     * @return
     */
    public static int calcASCIILen(String str) {
        if (str == null) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 127 || str.charAt(i) < 0) {
                //非ASCII字符统一当作两个ASCII字符
                sum += 2;
            } else {
                sum++;
            }
        }
        return sum;
    }

    /**
     * <pre>
     * 格式化较长的字符串，中间用分隔字符串分开，使之更易阅读
     * 输入：prettyString("135235677884564564789456132123", " ", true, 3, 4, 5);
     * 输出：135 2356 77884 56456 47894 56132 123
     * </pre>
     *
     * @param cont       原始字符串
     * @param separator  分隔字符串
     * @param repeatLast 是否重复使用最后一个分隔字符串来分隔剩下的内容
     * @param pos        可变长度分隔数量，3，4，5表示第一节3字符，第二节4字符，第三节5字符
     * @return 分隔后的字符串
     */
    public static String prettyString(String cont, String separator, boolean repeatLast, int... pos) {
        if (pos == null || pos.length == 0) {
            return cont;
        }
        if (separator == null) {
            separator = Constants.DEFAULT_SPACE;
        }
        cont = cont.trim();
        StringBuilder sb = new StringBuilder();
        int currPos = 0;
        int ct = pos[currPos];
        int curr = 1;
        for (int i = 0; i < cont.length(); i++) {
            sb.append(cont.charAt(i));
            if (curr++ == ct) {
                curr = 1;
                currPos++;
                ct = currPos >= pos.length ? pos[pos.length - 1] : pos[currPos];
                if (cont.indexOf(separator, i) == i + 1) {
                    curr = curr - separator.length();
                    continue;
                }
                if ((repeatLast || currPos <= pos.length) && ct > 0) {
                    sb.append(separator);
                }
            }
        }
        return sb.toString().replaceAll("(^(" + separator + ")+)|((" + separator + ")+$)", "");
    }

    /**
     * 还原经过分隔符分隔后的字符串
     *
     * @param cont     原始字符串
     * @param seprator 分隔字符串，可以是正则表达式
     * @return
     */
    public static String trimSeprator(String cont, String seprator) {
        if (seprator == null) {
            seprator = Constants.DEFAULT_SPACE;
        }
        return cont.replaceAll(seprator, "");
    }

    public static String prettyFileSize(double fileSize) {
        double size;
        DecimalFormat fmt = new DecimalFormat("#.##");
        StringBuilder sb = new StringBuilder();
        if (fileSize < 1024) {
            //B
            size = fileSize;
            sb.append(size);
        } else if (fileSize < 1 << 20) {
            //KB
            size = fileSize / (1 << 10);
            sb.append(fmt.format(size)).append('K');
        } else if (fileSize < 1 << 30) {
            //MB
            size = fileSize / (1 << 20);
            sb.append(fmt.format(size)).append('M');
        } else if (fileSize < (1L << 40)) {
            //GB
            size = fileSize / (1L << 30);
            sb.append(fmt.format(size)).append('G');
        } else if (fileSize < (1L << 50)) {
            //TB
            size = fileSize / (1L << 40);
            sb.append(fmt.format(size)).append('T');
        } else if (fileSize < (1L << 60)) {
            //PB
            size = fileSize / (1L << 50);
            sb.append(fmt.format(size)).append('P');
        }
        return sb.toString();
    }

    public static String prettyTimeCounting(int seconds) {
        int minute, sec;
        minute = seconds / 60;
        sec = seconds % 60;
        StringBuilder sb = new StringBuilder();
        if (minute < 10) {
            sb.append(0);
        }
        sb.append(minute).append(':');
        if (sec < 10) {
            sb.append(0);
        }
        sb.append(sec);
        return sb.toString();
    }
}

