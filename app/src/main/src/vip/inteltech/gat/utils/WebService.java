package vip.inteltech.gat.utils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.viewutils.MProgressDialog;
import vip.inteltech.gat.viewutils.MToast;

public class WebService {
    public static final String TAG = WebService.class.getName();
    private static final String NAMESPACE = "http://tempuri.org/";
    //private String baseURL="http://192.168.1.38:6699/IClient";//测试
    //private String baseURL="http://120.24.180.38:6699/IClient";//兴韵星
    //private String baseURL="http://120.24.172.44:6699/IClient";//咪咕
    private String baseURL = Contents.Ip;//Contents.Ip + ":6699/Client";
    //private String baseURL="http://120.24.156.131:6699/IClient";//早教通
    //private String baseURL="http://112.74.130.160:6699/IClient";//关爱通
    private String methodName;
    private Context content;
    private String result = null;
    private Thread getThread = null;
    private Vector<WebServiceListener> WebServiceRepository = new Vector<WebServiceListener>();
    private Lock WebServiceRepositorylock = new ReentrantLock();
    private int id;
    private String dialog;
    private boolean returnByThread;
    private StringBuffer soapMessage = new StringBuffer();
    //private Dialog loadingProgressDialog;
    private MProgressDialog mProgressDialog = null;

    public WebService(Context content, int id, boolean dialog, String method) {
        this.methodName = method;
        this.content = content;
        this.id = id;
        if (dialog)
            this.dialog = (String) content.getResources().getText(R.string.wait);
    }

    public WebService(Context content, int id, String dialog, String method) {
        this.methodName = method;
        this.content = content;
        this.id = id;
        this.dialog = dialog;
    }

    private void SetProperty(List<WebServiceProperty> property) {
        soapMessage.append("<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\"><s:Header><a:Action>http://tempuri.org/IClient/");
        soapMessage.append(this.methodName);
        soapMessage.append("</a:Action><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo><a:To s:mustUnderstand=\"1\">");
        soapMessage.append(baseURL).append(getUrlCache());
        soapMessage.append("</a:To></s:Header><s:Body><");
        soapMessage.append(this.methodName);
        soapMessage.append(" xmlns=\"http://tempuri.org/\">");
        for (int i = 0; i < property.size(); i++) {
            String key = property.get(i).Key;
            if (StringUtils.isBlank(key)) {
                continue;
            }
            soapMessage.append('<').append(key).append('>').append(CommUtil.toStr(property.get(i).Value, Constants.DEFAULT_BLANK)).append("</").append(key).append('>');
        }
        soapMessage.append("</").append(this.methodName).append("></s:Body></s:Envelope>");
    }

    public void SyncGet(List<WebServiceProperty> property) {
        returnByThread = false;
        this.SetProperty(property);
        getThread = new Thread(getRunnable);
        getThread.start();
    }

    public void SyncGetReturnByThread(List<WebServiceProperty> property) {
        returnByThread = true;
        this.SetProperty(property);
        getThread = new Thread(getRunnable);
        getThread.start();
    }

    private Runnable getRunnable = new Runnable() {
        public void run() {
            if (dialog != null) {
                loadingDialogHandler.sendEmptyMessage(0);
//                timer.schedule(task, 2000);
            }
            result = Get();
            if (!returnByThread) {
                mhandler.sendEmptyMessage(0);
            } else {
                notifyGet(methodName, id, result);
            }
            if (dialog != null) {
                loadingDialogDismissHandler.sendEmptyMessage(0);
            }
        }
    };

    private int retry = 0;

    private String Get() {
        if (Contents.Debug) {
            Log.i("WebService=======", soapMessage.toString());
        }

        String res = null;
        byte[] reponseData = null;
        try {
            reponseData = HttpUtil.INSTANCE.postSyncWithGZip(baseURL, soapMessage.toString().getBytes());
            if (reponseData == null) {
//                CommUtil.showMsgShort(R.string.network_failed);
                if (retry++ >= 3) {
                    retry = 0;
                    return null;
                } else {
                    Thread.sleep(300);
                    return Get();
                }
            }
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new ByteArrayInputStream(reponseData), "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ((this.methodName + "Result").equals(parser.getName())) {
                        res = parser.nextText();
                        //Log.i("WebService.Get", res);
                        break;
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, "Received:" + (reponseData == null ? Constants.DEFAULT_BLANK : new String(reponseData)), e);
        }
        return res;
    }

    public interface WebServiceListener extends EventListener {
        public void onWebServiceReceive(String method, int id, String result);
    }

    public void addWebServiceListener(WebServiceListener dl) {
        WebServiceRepositorylock.lock();
        try {
            WebServiceRepository.addElement(dl);
        } finally {
            WebServiceRepositorylock.unlock();
        }
    }

    public void removeWebServiceListener(WebServiceListener dl) {
        WebServiceRepositorylock.lock();
        try {
            WebServiceRepository.remove(dl);
        } finally {
            WebServiceRepositorylock.unlock();
        }
    }

    public void notifyGet(String method, int id, String result) {
        try {
            Enumeration<WebServiceListener> _enumeration = WebServiceRepository.elements();
            while (_enumeration.hasMoreElements()) {
                WebServiceListener dl = _enumeration.nextElement();
                dl.onWebServiceReceive(method, id, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mhandler = new Handler() { // 更新UI的handler
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                notifyGet(methodName, id, result);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    };
    private Handler loadingDialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                startProgressDialog(dialog);
                /*loadingProgressDialog = createLoadingDialog(content,dialog);
                loadingProgressDialog.show();*/
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    };
    private Handler loadingDialogDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                /*if(loadingProgressDialog!=null)
                loadingProgressDialog.dismiss();*/
                stopProgressDialog();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    };
    private Handler loadingErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                if (dialog != null)
                    MToast.makeText(R.string.waring_internet_error).show();
                    /*Toast.makeText(content,
                        R.string.waring_internet_error, 3000)
						.show();*/

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    };

    private void startProgressDialog(String dialog) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = MProgressDialog.createDialog(content);
                mProgressDialog.setMessage(dialog);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.show();
        } catch (BadTokenException e) {

        }
    }

    private void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progressdialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.progressdialog);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        //Dialog loadingDialog = new Dialog(context, android.R.style.Theme_Translucent);// 创建自定义样式dialog
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            loadingDialogDismissHandler.sendEmptyMessage(0);
        }
    };

    private String getUrlCache() {
        String urlCache = "?web_id=";

        Calendar calendar = Calendar.getInstance();// 取系统时间
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        urlCache = urlCache + year + month + day + hour + minute + second;

        return urlCache;
    }
}