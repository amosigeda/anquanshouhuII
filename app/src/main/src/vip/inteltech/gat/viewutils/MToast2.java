package vip.inteltech.gat.viewutils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;

public class MToast2 {
    //	static Context mContext;
    TextView tv;
    ImageView iv;
    static Dialog dialog;
    static MToast2 mInstance;
    static boolean isShow;
    static int delayTime = 1500;

    public static Dialog makeText(Context mContext, int resID) {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }

        //mContext = AppContext.getInstance().getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mtoast2, null);
        dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.fade_in_out);
        /*
         * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
         * getWindowManager().getDefaultDisplay().getHeight()/2;
         */
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv.setText(resID);
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(false);
        if (!isShow) {
            dialog.show();
            Message msg = new Message();
            msg.obtain();
            mHandler.sendMessageDelayed(msg, delayTime);
        }
        return dialog;
    }

    public static Dialog makeText(Context mContext, String showStr) {
        if (dialog != null)
            dialog.cancel();

        //mContext = AppContext.getInstance().getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mtoast2, null);
        dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.fade_in_out);
        /*
         * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
         * getWindowManager().getDefaultDisplay().getHeight()/2;
         */
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv.setText(showStr);
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(false);
        if (!isShow) {
            dialog.show();
            Message msg = new Message();
            msg.obtain();
            mHandler.sendMessageDelayed(msg, delayTime);
        }
        return dialog;
    }

    static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Log.v("kkk", "handleMessage");
            if (dialog != null && dialog.isShowing()) {
                dialog.cancel();
            }
        }

        ;
    };


}
