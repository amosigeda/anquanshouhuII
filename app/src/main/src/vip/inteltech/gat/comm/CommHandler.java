package vip.inteltech.gat.comm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import vip.inteltech.gat.inter.CommCallback;
import vip.inteltech.gat.utils.AppContext;

/**
 * Created by hh on 2015/7/26 0026.
 */
public class CommHandler extends Handler {
    private static Context context;
    private static CommHandler commHandler;

    public static final int TOAST_SHORT = 0;
    public static final int TOAST_LONG = 1;
    public static final int CLOSE_DIALOG = 2;
    public static final int SHOW_DIALOG = 3;
    public static final int SEND_COMMAND = 4;
    public static final int STOP_DIALOG = 5;
    public static final int DELAY_EXECUTE = 6;

    private CommHandler() {
        context = AppContext.getContext();
    }

    public synchronized static CommHandler getHandler() {
        if (commHandler == null) {
            commHandler = new CommHandler();
        }
        return commHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case TOAST_SHORT:
                Toast.makeText(context, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case TOAST_LONG:
                Toast.makeText(context, String.valueOf(msg.obj), Toast.LENGTH_LONG).show();
                break;
            case SHOW_DIALOG:
                break;
            case CLOSE_DIALOG:
                break;
            case STOP_DIALOG:
                break;
            case SEND_COMMAND:
                break;
            case DELAY_EXECUTE:
                if (msg.obj instanceof CommCallback) {
                    CommCallback callback = (CommCallback) msg.obj;
                    callback.execute();
                }
            default:
                break;
        }
    }
}
