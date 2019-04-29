package vip.inteltech.gat.utils;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import vip.inteltech.gat.service.MService;
import vip.inteltech.gat.utils.WebService.WebServiceListener;

public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类 
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    /** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {
    }  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String msg = ex.getLocalizedMessage();
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                //MToast.makeText(msg).show();
                Looper.loop();
            }
        }.start();
        AppData.GetInstance(mContext).setLoginAuto(false);
        Intent intent_a = new Intent (mContext,MService.class);
		mContext.stopService(intent_a);
        //收集设备参数信息
        ExceptionError(ex);
        //saveCrashInfo2File(ex);
        collectDeviceInfo(mContext);
        return true;
    }

    /**
     * 收集设备参数信息
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        java.lang.reflect.Field[] fields = Build.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

 private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
//        for (Map.Entry<String, String> entry : infos.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            sb.append(key + "=" + value + "\n");
//        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
        	Log.i("liuyou", "cause:"+cause.toString()+"--");
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        Log.i("liuyou", "result:"+result);
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/"+mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).packageName+"/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
 	private final int _ExceptionError  = 0;
 	private void ExceptionError(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			Log.i("liuyou", "cause:" + cause.toString() + "--");
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);

		WebService ws = new WebService(mContext, _ExceptionError, true, "ExceptionError");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		//property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		property.add(new WebServiceProperty("error", Contents.APPName + "-" + getVersion() + " " + sb.toString()));
		ws.addWebServiceListener(new WebServiceListener() {
			@Override
			public void onWebServiceReceive(String method, int id, String result) {
				try {
					JSONObject jsonObject = JSONObject.parseObject(result);
					if (id == _ExceptionError) {
						int code = jsonObject.getIntValue("Code");
						if (code == 1) {
							android.os.Process.killProcess(android.os.Process.myPid());
							System.exit(1);
						} else {
							// -1表示输入参数错误
							// MToast.makeText(jsonObject.getString("Message")).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		ws.SyncGet(property);
	}
	public String getVersion() {
		try {
			PackageManager manager = AppContext.getInstance().getContext().getPackageManager();
			PackageInfo info = manager.getPackageInfo(AppContext.getInstance().getContext().getPackageName(), 0);
			String version = info.versionName;
		return  version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}  