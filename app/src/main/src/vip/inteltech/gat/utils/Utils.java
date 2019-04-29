package vip.inteltech.gat.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.ex.DbException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import vip.inteltech.coolbaby.BuildConfig;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.chatutil.FileUtils;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.inter.CommCallback;
import vip.inteltech.gat.view.DialogSureCancel;


public class Utils {
    private static SoundPool pool;
    private static int streamId;
    private static boolean isPlaying = false;
    private static Random rand = new Random();
    private static DialogSureCancel dialog;
    private static Random mRandom;

    public static int randInt(int max) {
        if (mRandom == null) {
            mRandom = new Random();
            mRandom.setSeed(System.currentTimeMillis());
        }
        return mRandom.nextInt(max);
    }

    public static int randBoundInt() {
        int max = 32;
        int s = 1;
        for (int i = 0; i < 18; i++) {
            s += 1 << randInt(max);
        }
        return s;
    }

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
            //
        }
        return apiKey;
    }

    /**
     * 获取当前时间点
     *
     * @param dateformat
     * @return
     */
    public static String getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
        String hehe = dateFormat.format(now);
        return hehe;
    }

    /**
     * 格式化日期 yyyy-MM-dd kk:mm
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date == null) return "None";
        return android.text.format.DateFormat.format("yyyy-MM-dd kk:mm", date).toString();
    }

    /**
     * 格式化日期 yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String formatDateShort(Date date) {
        if (date == null) return "None";
        return android.text.format.DateFormat.format("yyyy-MM-dd", date).toString();
    }

    /**
     * 格式化日期 MM-dd
     *
     * @param date
     * @return
     */
    public static String getMonthDate(Date date) {
        if (date == null) return "None";
        return android.text.format.DateFormat.format("MM-dd", date).toString();
    }

    /**
     * 判断系统是不是24小时制
     *
     * @return boolean  True是24小时
     */
    public static boolean is24(Context ctx) {
        ContentResolver cv = ctx.getContentResolver();
        String strTimeFormat = Settings.System.getString(cv, Settings.System.TIME_12_24);
        if (strTimeFormat != null && strTimeFormat.equals("24")) {// strTimeFormat某些rom12小时制时会返回null
            return true;
        } else {
            return false;
        }
    }

    /**
     * 比较两个时间的前后关系
     *
     * @param time1 老时间
     * @param time2 新时间
     * @return 0等于，1老时间小于新时间(所需)
     */
    public static int compareTime(String time1, String time2) {
//		String s1="2008-01-25 09:12:09";
//		String s2="2008-01-29 09:12:11";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(time1));
            c2.setTime(df.parse(time2));
        } catch (java.text.ParseException e) {
            Log.e(Constants.TAG, "格式不正确", e);
        }
        int result = c1.compareTo(c2);
        if (result == 0)
            return 0;
//		System.out.println("c1相等c2");
        else if (result < 0)
            return 1;
//		System.out.println("c1小于c2");
        else
            return 2;
//		System.out.println("c1大于c2");
    }

    /**
     * 将2014-10-12 12:12:12的格式转换为1012 121212格式
     *
     * @param time
     * @return
     */
    public static String getDayTime(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");// 可以方便地修改日期格式
            String hehe = dateFormat.format(date);
            return hehe;
        } catch (Exception e) {
            Log.e(Constants.TAG, "格式不正确", e);
            return "";
        }
    }

    /**
     * 对网络连接进行判断
     *
     * @return true, 网络已连接； false，未连接网络
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 对WIFI网络连接进行判断
     *
     * @return true, WIFI已连接； false，WIFI未连接
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 对MOBILE网络连接进行判断
     *
     * @return true, MOBILE已连接； false，MOBILE未连接
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取网络连接类型
     *
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 创建数据适配器时需要转换的工具
     *
     * @param @return MAP对象
     */
    public static Map<String, Object> createMap(String key, String label, String value) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        map.put("label", label);
        map.put("value", value);
        return map;
    }

    /**
     * 创建键值对工具
     */
    public static Map<String, Object> createMap(String key, String value) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", key);
        map.put("value", value);
        return map;
    }

    public static String dataPath = getSDCardPath() + "/gpstracker_data/";

    /**
     * 截取手机屏幕
     */
    private static String SavePath;

    @SuppressWarnings("deprecation")
    public static String GetandSaveCurrentImage(WindowManager windowManager,
                                                View decorview) {
        // 1.构建Bitmap
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        // 2.获取屏幕
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
        SavePath = getSDCardPath() + "/ShareWX/ScreenImage";
        // 3.保存Bitmap
        try {
            File path = new File(SavePath);
            // 文件
            String filepath = SavePath + "/Scinan_Screen.png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
        }
        return SavePath;
    }

    /**
     * 获取SD卡相关信息
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        try {
            boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdcardExist) {
                sdcardDir = Environment.getExternalStorageDirectory();
            }
        } catch (Exception e) {
            //
        }
        if (sdcardDir == null) {
            return "";
        } else {
            return sdcardDir.toString();
        }
    }

    /**
     * 获取可用存储路径，优先使用SD卡，结尾带斜杠
     *
     * @return
     */
    public static String getAvailableStoragePath() {
        String sdCardPath = getSDCardPath();
        StringBuilder sb = new StringBuilder();
        if (CommUtil.isNotBlank(sdCardPath)) {
            sb.append(sdCardPath).append("/data/").append(AppContext.getContext().getPackageName()).append('/');
        } else {
            sb.append(AppContext.getContext().getFilesDir()).append("/data/");
        }
        String path = sb.toString();
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = null;
        return path;
    }

    /**
     * 生成MD5字符串
     *
     * @param plainText 需要生成MD5的字符串
     * @return MD5字符串
     */
    public static String Md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
//			System.out.println("result: " + buf.toString());// 32位的加密
//			System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * String类型的集合转换为String 每个数据之间使用“,”间隔
     *
     * @param stringList
     * @return
     */
    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    /**
     * 获取WebView使用的HTML代码  没有其他任何修饰的样式
     *
     * @param content 内容
     * @return
     */
    public static String getHtml(String content) {
        StringBuffer html = new StringBuffer();
        html.append("<!DOCTYPE HTML><html><body >");
        html.append(content);
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 修复图片出线内存溢出的情况
     *
     * @param url  图片的URL
     * @param view 展示的控件
     */
    public static void fixImage(String url, ImageView view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap b = BitmapFactory.decodeFile(url, options);
        view.setImageBitmap(b);
    }

    /**
     * 动态添加控件
     *
     * @param context
     * @param time
     * @return
     */
    public static TextView getTimeView(Context context, String time) {
        TextView textView = new TextView(context);
        textView.setText(time);
        textView.setTextSize(12);
        textView.setTextColor(0x7f05001e);
        LinearLayout.LayoutParams layoutParams_txt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams_txt.setMargins(0, 5, 0, 0);
        textView.setLayoutParams(layoutParams_txt);
        return textView;
    }

    /**
     * 验证是否是一个正确的邮箱地址
     *
     * @param email
     * @return
     */
    public static boolean validateEmail(String email) {
        //Pattern pattern = Pattern.compile("[0-9a-zA-Z]*.[0-9a-zA-Z]*@[a-zA-Z]*.[a-zA-Z]*", Pattern.LITERAL);
        if (email == null) {
            return false;
        }
        //验证开始
        //不能有连续的.
        if (email.indexOf("..") != -1) {
            return false;
        }
        //必须带有@
        int atCharacter = email.indexOf("@");
        if (atCharacter == -1) {
            return false;
        }
        //最后一个.必须在@之后,且不能连续出现
        if (atCharacter > email.lastIndexOf('.') || atCharacter + 1 == email.lastIndexOf('.')) {
            return false;
        }
        //不能以.,@结束和开始
        if (email.endsWith(".") || email.endsWith("@") || email.startsWith(".") || email.startsWith("@")) {
            return false;
        }
        return true;
    }

    /**
     * 文本复制
     *
     * @param content 内容
     */
    public static void copy(String content) {
        Context context = AppContext.getContext();
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, content);
        cmb.setPrimaryClip(clipData);
        Toast.makeText(context, R.string.copy_succ, Toast.LENGTH_SHORT).show();
    }

    /**
     * 复制URI到剪贴板
     *
     * @param uri
     */
    public static void copy(Uri uri) {
        Context context = AppContext.getContext();
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newUri(context.getContentResolver(), null, uri);
        cmb.setPrimaryClip(clipData);
        Toast.makeText(context, R.string.copy_succ, Toast.LENGTH_SHORT).show();
    }

    /**
     * 粘贴
     *
     * @return
     */
    public static String pasteAsText() {
        Context context = AppContext.getContext();
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb.getPrimaryClip().getItemCount() == 0) {
            return Constants.DEFAULT_BLANK;
        }
        return cmb.getPrimaryClip().getItemAt(0).coerceToText(context).toString().trim();
    }


    /**
     * 将文件转成base64 字符串
     *
     * @param path 文件路径
     * @return *
     * @throws Exception
     */

    public static String encodeBase64File(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "";
            }
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return new String(Base64.encode(buffer, Base64.DEFAULT));
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("app_info", Context.MODE_PRIVATE);
    }

    public static boolean save(Context context, String key, Object value) {
        if (key == null) {
            return false;
        }
        SharedPreferences.Editor editor = getPreferences(context).edit();
        if (value == null) {
            return editor.remove(key).commit();
        }
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            editor.putString(key, value.toString());
        }
        return editor.commit();
    }

    public static <T> T get(Context context, String key) {
        try {
            SharedPreferences sp = getPreferences(context);
            Map<String, ?> mp = sp.getAll();
            return mp == null ? null : (T) mp.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取软键盘管理
     *
     * @param context
     * @return
     */
    public static InputMethodManager getManager(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return manager;
    }


    //存储进SD卡
    public static void saveImageFile(Bitmap bm, String fileName) throws Exception {
        File file = new File(fileName);
        //检测图片是否存在
        if (file.exists()) {
            file.delete();  //删除原图片
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, true));
        //100表示不进行压缩，70表示压缩率为30%
        bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);
        bos.flush();
        bos.close();
    }


    /**
     * 根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
     */
    public static double[] getAround(double lat, double lon, int raidus) {

        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        //System.out.println("["+minLat+","+minLng+","+maxLat+","+maxLng+"]");
        return new double[]{new BigDecimal(minLat).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue(),
                new BigDecimal(minLng).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue(),
                new BigDecimal(maxLat).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue(),
                new BigDecimal(maxLng).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue()};
    }


    /**
     * 把一个View的对象转换成bitmap
     */
    public static Bitmap getViewBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * 判断电话号码格式
     * 一键呼叫，拨打911，110，国内外手机号，座机
     *
     * @param mobile
     * @return
     */
    public static boolean isMobileNO(String mobile) {

        if (TextUtils.isEmpty(mobile)) {
            return false;
        } else {
            return (11 == mobile.length());
        }

//        return CommUtil.isNotBlank(mobile) && mobile.matches(Constants.MOBILE_FMT_I18N);
    }

    public static boolean isChinaMobileNO(String mobile) {
        return CommUtil.isNotBlank(mobile) && mobile.matches(Constants.MOBILE_FMT_CHINA);
    }

    // 判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static String join(String join, List<String> strAry) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.size(); i++) {
            if (i == (strAry.size() - 1)) {
                sb.append(strAry.get(i));
            } else {
                sb.append(strAry.get(i)).append(join);
            }
        }

        return new String(sb);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 手机震动
     *
     * @param activity
     * @param milliseconds
     */
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * final Context context  ：调用该方法的Activity实例
     * long milliseconds ：震动的时长，单位是毫秒
     * long[] pattern  ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     * boolean flag:为true时震动，为false时不震动
     */
    public static void vibrate(final Context context, long[] pattern, boolean isRepeat, boolean flag) {
        if (flag) {
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            if (vib != null) {
                vib.vibrate(pattern, isRepeat ? 1 : -1);
            }
        }
    }

    /**
     * 震动关闭
     *
     * @param context
     */
    public static void closeVibrate(Context context) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.cancel();
        }
    }

    /**
     * 图片转灰度
     *
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 将ImageView的图片转为灰色，ImageView必须是有图片的情况下才能用
     *
     * @param view
     */
    public static void setGrayImageView(ImageView view) {
        view.buildDrawingCache();
        BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
        if (null != drawable) {
            Bitmap image = drawable.getBitmap();
            if (null != image) {
                Bitmap bitmap = toGrayscale(image);
                view.setImageBitmap(bitmap);
            }
        }
    }

    public static Bitmap getBitmapFromView(ImageView view) {
        view.buildDrawingCache();
        BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
        if (null != drawable) {
            Bitmap image = drawable.getBitmap();
            return image;
        }
        return null;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void openGPSManually(Context context) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "GPS模块正常", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    public static void refreshBaseUrl(Context context) {
        SharedPreferences sp = getPreferences(context);
        Boolean customServerUrlEnabled = sp.getBoolean(Constants.SETTING_SERVER_URL_ENABLE, false);
        String customServerUrl = sp.getString(Constants.SETTING_SERVER_URL, null);
        String baseUrl;
        if (customServerUrlEnabled && CommUtil.isNotBlank(customServerUrl) && customServerUrl.length() >= 8) {
            baseUrl = customServerUrl;
        } else {
            baseUrl = BuildConfig.SERVER_URL;
        }
        Constants.baseUrl = baseUrl;
    }

    public static String getServerDomain() {
        String baseUrl = Constants.baseUrl;
        URI uri = URI.create(baseUrl);
        return uri.getHost();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealFilePath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
//        else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())){
        else {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 播放指定uri的声音
     *
     * @param context
     * @param uri
     */
    public synchronized static void playShortSound(Context context, Uri uri) {
        try {
            if (pool == null) {
                pool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            }
            if (!isPlaying) {
                isPlaying = true;
                if (Constants.SCHEME_ASSETS.startsWith(uri.getScheme())) {
                    pool.load(context.getAssets().openFd(uri.getHost()), 0);
                } else if (Constants.SCHEME_RES.startsWith(uri.getScheme())) {
                    pool.load(context, Integer.valueOf(uri.getHost()), 0);
                } else {
                    pool.load(getRealFilePath(context, uri), 0);
                }
                pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        streamId = soundPool.play(sampleId, 1, 1, 0, 0, 1);
                        isPlaying = false;
                    }
                });
            }
        } catch (Exception e) {
            Log.w(Constants.TAG, "播放声音失败", e);
        }
    }

    public static void stopSound() {
        if (pool != null) {
            pool.stop(streamId);
        }
    }

    /**
     * 获取电池电量百分比
     *
     * @param ele
     * @return
     */
    public static int getElectricityPercent(double ele) {
        if (ele >= 0 && ele <= 1) {
            //兼容最新电量百分比显示
            return (int) (ele * 100 + 0.5);
        }
        if (ele >= Constants.VOLTAGE_UPPER) {
            return 100;
        } else if (ele <= Constants.VOLTAGE_LOWER) {
            return 0;
        } else {
            return (int) (((ele - Constants.VOLTAGE_LOWER) / (Constants.VOLTAGE_UPPER - Constants.VOLTAGE_LOWER)) * 100 + 0.5);
        }
    }

    /**
     * 通用提示框
     *
     * @param mContext
     * @param titleResId
     * @param msgResId
     * @param countDownSeconds
     */
    public static void showNotifyDialog(Context mContext, int titleResId, int msgResId, int countDownSeconds, CommCallback callback) {
        showNotifyDialog(mContext, titleResId, msgResId, 0, 0, null, null, countDownSeconds, callback);
    }

    public static void showNotifyDialog(Context mContext, int titleResId, int msgResId,
                                        int positiveResId, int negativeResId,
                                        View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        showNotifyDialog(mContext, titleResId, msgResId, positiveResId, negativeResId, positiveListener, negativeListener, 0, null);
    }

    /**
     * @param mContext
     * @param titleResId
     * @param msgResId
     * @param positiveResId
     * @param negativeResId
     * @param positiveListener 可为null
     * @param negativeListener 可为null
     * @param countDownSeconds 不使用时设置为0
     * @param callback         countDownSeconds大于0时才会执行
     */
    public static void showNotifyDialog(Context mContext, int titleResId, int msgResId,
                                        int positiveResId, int negativeResId,
                                        View.OnClickListener positiveListener, View.OnClickListener negativeListener,
                                        int countDownSeconds, CommCallback callback) {
        showNotifyDialog(mContext,
                titleResId <= 0 ? Constants.DEFAULT_BLANK : mContext.getString(titleResId),
                mContext.getString(msgResId),
                positiveResId <= 0 ? Constants.DEFAULT_BLANK : mContext.getString(positiveResId),
                negativeResId <= 0 ? Constants.DEFAULT_BLANK : mContext.getString(negativeResId),
                positiveListener, negativeListener, countDownSeconds, callback);
    }

    public static void showNotifyDialog(Context mContext, String titleText, String msgText,
                                        String positiveBtText, String negativeBtText,
                                        View.OnClickListener positiveListener, View.OnClickListener negativeListener,
                                        int countDownSeconds, final CommCallback callback) {
        if (dialog != null) {
            closeNotifyDialog();
        }
        dialog = new DialogSureCancel(mContext);
        dialog.setTitle(titleText);
        dialog.setContent(msgText);
        dialog.setCancel(negativeBtText);
        dialog.setSure(positiveBtText);
        dialog.setCancelListener(negativeListener);
        dialog.setSureListener(positiveListener);

        if (countDownSeconds > 0) {
            dialog.setCancelable(true);
            CommUtil.delayExecute(countDownSeconds * 1000, new CommCallback() {
                @Override
                public void execute() {
                    if (dialog != null) {
                        closeNotifyDialog();
                    }
                    if (callback != null) {
                        callback.execute();
                    }
                }
            });
        }
        dialog.show();
    }

    public static void closeNotifyDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.cancel();
                dialog.hide();
                dialog.dismiss();
            } catch (Exception e) {
                //
            }
        }
    }

    public static ArrayList<String> genTimezones() {
        ArrayList<String> timeZoneIds = new ArrayList<>(25);
        for (int i = -12; i <= 12; i++) {
            StringBuilder sb = new StringBuilder(Constants.TIME_ZONE_PREFIX);
            if (i < 0) {
                sb.append('-');
            } else if (i > 0) {
                sb.append('+');
            }
            if (i < 10 && i > -10) {
                sb.append('0');
            }
            sb.append(i > 0 ? i : -i);
            sb.append(":00");
            if (i < 0 && i > -12) {
                String str = sb.toString();
                timeZoneIds.add(sb.replace(sb.length() - 2, sb.length() - 1, "3").toString());
                timeZoneIds.add(str);
            } else if (i > 0 && i < 12) {
                timeZoneIds.add(sb.toString());
                timeZoneIds.add(sb.replace(sb.length() - 2, sb.length() - 1, "3").toString());
            } else if (i == 12 || i == -12) {
                timeZoneIds.add(sb.toString());
            } else if (i == 0) {
                String str = sb.toString();
                timeZoneIds.add(sb.insert(4, '-').replace(sb.length() - 2, sb.length() - 1, "3").toString());
                timeZoneIds.add(str);
                timeZoneIds.add(sb.replace(4, 5, "+").toString());
            }
        }
        return timeZoneIds;
    }

    public static String formatTimezoneOffset(float offset) {
        StringBuilder sb = new StringBuilder(Constants.TIME_ZONE_PREFIX);
        int off = (int) offset;
        if (off < 0) {
            sb.append('-');
        } else if (off > 0) {
            sb.append('+');
        }
        if (off > -10 && off < 10) {
            sb.append('0');
        }
        sb.append(off > 0 ? off : -off).append(':');
        if (offset - off != 0.0) {
            sb.append('3');
        } else {
            sb.append('0');
        }
        sb.append('0');
        return sb.toString();
    }

    public static float convertTimezoneToOffset(String off) {
        return Float.parseFloat(off.replace(Constants.TIME_ZONE_PREFIX, "").replace(":", ".").replace("30", "5"));
    }

    /**
     * 隐藏软键盘
     *
     * @param activity 上下文Activity环境
     * @param view     焦点所在控件
     */
    public static void hideInputBoard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            IBinder binder = activity.getCurrentFocus() == null ? new Binder() : activity.getCurrentFocus().getApplicationWindowToken();
            imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param activity 上下文Activity环境
     * @param view     焦点所在控件
     */
    public static void showInputBoard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            IBinder binder = activity.getCurrentFocus() == null ? new Binder() : activity.getCurrentFocus().getApplicationWindowToken();
            imm.showSoftInputFromInputMethod(binder, InputMethodManager.SHOW_FORCED);
        } else if (imm != null) {
            view.requestFocus();
            view.requestFocusFromTouch();
            imm.showSoftInput(view, 0);
            imm.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
        }
    }


    /**
     * 获取当前屏幕旋转角度
     *
     * @param context
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static float getScreenRotationOnPhone(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return -90;
        }
        return 0;
    }

    public static void playSoundAndVibrate(Context context) {
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        playSoundAndVibrate(context, uri);
    }

    public static void playSoundAndVibrate(Context context, Uri uri) {
        if (!isPlaying) {
            try {
                SharedPreferences sp = Utils.getPreferences(context);
                if (sp.getBoolean(Constants.MSG_VOICE, true)) {
                    //获取系统默认铃声的Uri
                    Utils.playShortSound(context, uri);
                }

                if (sp.getBoolean(Constants.MSG_VIBRATE, true)) {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    //设置震动时长，停50毫秒，震动100毫秒，震动两次
                    vibrator.vibrate(new long[]{100, 300, 100, 300}, -1);
                }
            } catch (Exception e) {
                Log.e(Utils.class.getName(), "Play sound failed!", e);
            }
        }
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getFullScreenHeight(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param activity
     * @return
     */
    public static int getBottomStatusHeight(Activity activity) {
        int totalHeight = getFullScreenHeight(activity);
        int contentHeight = getScreenHeight(activity);
        return totalHeight - contentHeight;
    }

    /**
     * 标题栏高度
     *
     * @return
     */
    public static int getTitleHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    /**
     * 获得屏幕内容区域高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        Point pt = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(pt);
        return pt.y;
    }

    /**
     * 获得屏幕内容区域宽度
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        Point pt = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(pt);
        return pt.x;
    }

    /**
     * @param activity
     * @param resId
     * @param currFragment
     * @param to
     * @return
     */
    public static Fragment switchFragment(FragmentActivity activity, int resId, Fragment currFragment, Fragment to) {
        if (to == null || to.equals(currFragment) || resId <= 0) {
            return currFragment;
        }
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        if (currFragment == null) {
            transaction.replace(resId, to);
        } else if (!to.isAdded()) {
            transaction.hide(currFragment).add(resId, to);
        } else {
            transaction.hide(currFragment).show(to);
        }
        transaction.commitAllowingStateLoss();
        return to;
    }

    public static <T> T getMetaData(Context context, String name) {
        try {
            final ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                return (T) ai.metaData.get(name);
            }
        } catch (Exception e) {
            Log.w(Constants.TAG, "Couldn't find meta-data: " + name);
        }

        return null;
    }

    public static File copyAttachedDatabase(Context context, String dbName) {
        final File file = context.getDatabasePath(dbName);

        // If the database already exists, return
        if (file.exists()) {
            //file.delete();
            return file;
        }

        // Make sure we have a path to the file
        file.getParentFile().mkdirs();

        // Try to copy database file
        try {
            final InputStream inputStream = context.getAssets().open(dbName);
            final OutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Failed to open file", e);
        }
        return file;
    }

    public static String getFullUrl(String path) {
        if (CommUtil.isBlank(path)) {
            return null;
        }
        if (path.startsWith("/")) {
            return Constants.baseUrl + path;
        } else {
            return Constants.baseUrl + "/" + path;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static void refreshGridViewHeight(GridView gridview) {
        // 获取gridview的adapter
        ListAdapter listAdapter = gridview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int numColumns = gridview.getNumColumns(); //5
        int totalHeight = 0;
        // 计算每一列的高度之和
        for (int i = 0; i < listAdapter.getCount(); i += numColumns) {
            // 获取gridview的每一个item
            View listItem = listAdapter.getView(i, null, gridview);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }
        // 获取gridview的布局参数
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        params.height = totalHeight;
        gridview.setLayoutParams(params);
    }

    /**
     * This will create an hexadecimal md5 checksum.
     * For the readable version:
     *
     * @param file
     * @return
     * @see #getMD5Checksum(File file)
     */
    private static byte[] createChecksum(File file) {
        InputStream fis = null;
        MessageDigest complete = null;
        try {
            fis = new FileInputStream(file);
            complete = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
        } catch (Exception e) {
            Log.e(Utils.class.getName(), "Check MD5 error", e);
        } finally {
            FileUtils.closeStream(fis);
        }
        return complete == null ? null : complete.digest();
    }

    /**
     * Get file MD5Checksum
     *
     * @param file
     * @return
     */
    public static String getMD5Checksum(File file) {
        byte[] b = createChecksum(file);
        if (b == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (byte bt : b) {
            result.append(Integer.toString((bt & 0xFF) + 0x100, 16).substring(1));
        }
        return result.toString().toUpperCase();
    }

    /**
     * 根据RSSI计算距离
     * d - 计算所得距离
     * RSSI - 接收信号强度（负值）
     * A - 发射端和接收端相隔1米时的信号强度 59
     * n - 环境衰减因子 2.0
     *
     * @param rssi
     * @return
     */
    public static double calcDistanceByRSSI(int rssi) {
        return Math.pow(10, (Math.abs(rssi) - 59) / (10 * 2.0));
    }

    public static Map<String, Object> findOne(String sql, Object... args) {
        List<Map<String, Object>> list = findList(sql, args);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    public static List<Map<String, Object>> findList(String sql, Object... args) {
        DbManager db = AppContext.db;
        SqlInfo sqlInfo = new SqlInfo(sql);
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                KeyValue kv = new KeyValue(CommUtil.toStr(i), args[i]);
                sqlInfo.addBindArg(kv);
            }
        }
        Cursor cursor = null;
        List<Map<String, Object>> list = null;
        try {
            cursor = db.execQuery(sqlInfo);
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                for (int j = 0; j < cursor.getColumnCount(); j++) {
                    Object res = null;
                    switch (cursor.getType(j)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            res = cursor.getLong(j);
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            res = cursor.getDouble(j);
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            res = cursor.getString(j);
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            res = cursor.getBlob(j);
                            break;
                        default:
                            break;
                    }
                    map.put(cursor.getColumnName(j), res);
                }
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(map);
            }
        } catch (DbException e) {
            Log.e(Utils.class.getName(), "查询出错", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public static byte[] GZip(byte[] bContent) {
        ByteArrayOutputStream out = null;
        GZIPOutputStream pOut = null;
        try {
            out = new ByteArrayOutputStream();
            pOut = new GZIPOutputStream(out);
            pOut.write(bContent);
            pOut.finish();
            pOut.close();
            byte[] trueData = out.toByteArray();
            out.close();
            return trueData;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeStream(out, pOut);
        }
        return null;
    }

    public static byte[] unGZip(byte[] bContent) {
        GZIPInputStream gzipStream = null;
        ByteArrayOutputStream outStream = null;
        if (bContent == null) {
            return null;
        }
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bContent);

            gzipStream = new GZIPInputStream(in);
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = gzipStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            gzipStream.close();
            in.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeStream(gzipStream, outStream);
        }
        return null;
    }

    public static String captureScreen(Activity context, String target) {
        View dView = context.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        String picPath = null;
        if (bitmap != null) {
            try {
                String filePath = getAvailableStoragePath() + target;
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                picPath = filePath + File.separator + System.currentTimeMillis() + ".jpg";
                file = new File(picPath);
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                //
            }
        }
        return picPath;
    }
}
