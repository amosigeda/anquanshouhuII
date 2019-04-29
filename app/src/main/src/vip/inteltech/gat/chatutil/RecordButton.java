package vip.inteltech.gat.chatutil;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.CommUtil;

public class RecordButton extends AppCompatButton {

    private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    private static final int RECORD_OFF = 0; // 不在录音
    private static final int RECORD_ON = 1; // 正在录音

    private Dialog mRecordDialog;
    private AudioRecorder mAudioRecorder;
    private Thread mRecordThread;
    private RecordListener listener;

    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f, recodeTimeM; // 录音时长，如果录音时间太短则录音失败
    private final float MaxRecodeTime = 15.0f;
    private double voiceValue = 0.0; // 录音的音量值
    private boolean isCanceled = false; // 是否取消录音
    private float downY;

    private TextView dialogTextView, record_time;
    private ImageView dialogImg;
    private Context mContext;
    // 动画资源文件,用于录制语音时
    private Drawable[] micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_01),
            getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
            getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
            getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
            getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09),
            getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
            getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13),
            getResources().getDrawable(R.drawable.record_animate_14)};
    private OnFinish mOnFinish;
    private StartListener mStartListener;
    private Activity activity;

    public RecordButton(Context context) {
        super(context);
        init(context);
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        this.setText(R.string.hold_and_spreak);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setAudioRecord(AudioRecorder record) {
        this.mAudioRecorder = record;
    }

    public void setStartListener(StartListener mStartListener) {
        this.mStartListener = mStartListener;
    }

    public void setRecordListener(RecordListener listener) {
        this.listener = listener;
    }

    public AudioRecorder getAudioRecorder() {
        return mAudioRecorder;
    }

    public interface OnFinish {
        void Finish(String fileName, float recodeTime);
    }

    public void setOnFinish(OnFinish callBack) {
        mOnFinish = callBack;
    }

    // 录音时显示Dialog
    private void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(mContext, R.style.Dialogstyle);
            mRecordDialog.setContentView(R.layout.dialog_record);
            dialogImg = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            dialogTextView = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
            record_time = (TextView) mRecordDialog.findViewById(R.id.record_time);
        }
        switch (flag) {
            case 1:
                dialogImg.setImageResource(R.drawable.record_cancel);
                dialogTextView.setText(R.string.release_canceled);
                this.setText(R.string.release_canceled);
                break;

            default:
                dialogImg.setImageResource(R.drawable.record_animate_01);
                dialogTextView.setText(R.string.slide_cancel);
                this.setText(R.string.release_comletion);
                break;
        }
        dialogTextView.setTextSize(14);
        Window window = mRecordDialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            mRecordDialog.show();
        }
    }

    // 录音时间太短时Toast显示
    private void showWarnToast(String toastText) {
        Toast toast = new Toast(mContext);
        View warnView = LayoutInflater.from(mContext).inflate(R.layout.toast_warn, null);
        toast.setView(warnView);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
        toast.show();
    }

    // 开启录音计时线程
    private void callRecordTimeThread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    int vv;
    // 录音线程
    private Runnable recordThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        // 获取音量，更新dialog
                        if (!isCanceled) {
                            voiceValue = mAudioRecorder.getAmplitude();

                            if (voiceValue < 600.0) {
                                vv = 0;
                            } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
                                vv = 1;
                            } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
                                vv = 2;
                            } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
                                vv = 3;
                            } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
                                vv = 4;
                            } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
                                vv = 5;
                            } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
                                vv = 6;
                            } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
                                vv = 7;
                            } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
                                vv = 8;
                            } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
                                vv = 9;
                            } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
                                vv = 10;
                            } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
                                vv = 11;
                            } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
                                vv = 12;
                            } else if (voiceValue > 12000.0) {
                                vv = 13;
                            }
                            recordHandler.sendEmptyMessage(vv);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //setDialogImage();
            dialogImg.setImageDrawable(micImages[msg.what]);
            recodeTimeM = MaxRecodeTime - recodeTime;
            record_time.setText(
                    mContext.getResources().getString(R.string.remain)
                            + String.valueOf((int) recodeTimeM)
                            + mContext.getResources().getString(R.string.second));
            if (recodeTimeM <= 0) {
                //System.out.println("recodeTimeM   >>>>>>>>>>>>>>>>" + recodeTimeM);
                if (recordState == RECORD_ON) {
                    recordState = RECORD_OFF;
                    if (mRecordDialog.isShowing()) {
                        mRecordDialog.dismiss();
                    }
                    mAudioRecorder.stop();
                    mRecordThread.interrupt();
                    voiceValue = 0.0;
                    if (isCanceled) {
                        mAudioRecorder.deleteOldFile();
                    } else {
                        if (recodeTime < MIN_RECORD_TIME) {
                            showWarnToast(AppContext.getInstance().getContext().getResources().getString(R.string.record_too_short));
                            mAudioRecorder.deleteOldFile();
                        } else {
                            if (listener != null) {
                                listener.recordEnd();
                            }
                            mOnFinish.Finish(mAudioRecorder.getVoiceName(), recodeTime);
                        }
                    }
                    isCanceled = false;
                    RecordButton.this.setText(R.string.hold_and_spreak);
                    setBackgroundResource(R.drawable.btn_chat_normal);
                }
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下按钮
                downY = event.getY();
                PermissionsUtil.requestPermission(activity, Manifest.permission.RECORD_AUDIO, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                        if (recordState != RECORD_ON) {
                            mStartListener.recordStart();
                            recodeTimeM = 15.0f;
                            showVoiceDialog(0);
                            if (mAudioRecorder != null) {
                                mAudioRecorder.ready();
                                recordState = RECORD_ON;
                                mAudioRecorder.start();
                                callRecordTimeThread();
                            }
                            setBackgroundResource(R.drawable.btn_chat_pressed);
                        }
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        CommUtil.showMsgShort(R.string.permission_record_denied);
                    }
                });
                break;
            case MotionEvent.ACTION_MOVE: // 滑动手指
                //System.out.println("ACTION_MOVE   !!!!!!!!!!  " + recodeTimeM);
                float moveY = event.getY();
                if (Math.abs(downY - moveY) > 80) {
                    if (recodeTimeM > 0) {
                        isCanceled = true;
                        showVoiceDialog(1);
                    }

                }
                if (Math.abs(downY - moveY) < 20) {
                    if (recodeTimeM > 0) {
                        isCanceled = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                if (recordState == RECORD_ON) {
                    recordState = RECORD_OFF;
                    if (mRecordDialog.isShowing()) {
                        mRecordDialog.dismiss();
                    }
                    mAudioRecorder.stop();
                    mRecordThread.interrupt();
                    voiceValue = 0.0;
                    if (isCanceled) {
                        mAudioRecorder.deleteOldFile();
                    } else {
                        if (recodeTime < MIN_RECORD_TIME) {
                            showWarnToast(AppContext.getInstance().getContext().getResources().getString(R.string.record_too_short));
                            mAudioRecorder.deleteOldFile();
                        } else {
                            if (listener != null) {
                                listener.recordEnd();
                            }
                            mOnFinish.Finish(mAudioRecorder.getVoiceName(), recodeTime);
                        }
                    }
                    isCanceled = false;
                    this.setText(R.string.hold_and_spreak);
                    setBackgroundResource(R.drawable.btn_chat_normal);
                }
                //return true;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (recordState == RECORD_ON) {
                    recordState = RECORD_OFF;
                    if (mRecordDialog.isShowing()) {
                        mRecordDialog.dismiss();
                    }
                    mAudioRecorder.stop();
                    mRecordThread.interrupt();
                    voiceValue = 0.0;
                    if (isCanceled) {
                        mAudioRecorder.deleteOldFile();
                    } else {
                        if (recodeTime < MIN_RECORD_TIME) {
                            showWarnToast(AppContext.getInstance().getContext().getResources().getString(R.string.record_too_short));
                            mAudioRecorder.deleteOldFile();
                        } else {
                            if (listener != null) {
                                listener.recordEnd();
                            }
                            mOnFinish.Finish(mAudioRecorder.getVoiceName(), recodeTime);
                        }
                    }
                    isCanceled = false;
                    this.setText(R.string.hold_and_spreak);
                    setBackgroundResource(R.drawable.btn_chat_normal);
                }

                //return true;
                break;
            case MotionEvent.ACTION_MASK:
                System.out.println("MotionEvent.ACTION_MASK");
                break;
            case MotionEvent.ACTION_OUTSIDE:
                System.out.println("MotionEvent.ACTION_OUTSIDE");
                break;
        }
        //return super.onTouchEvent(event);
        return true;
    }

    public interface RecordListener {
        public void recordEnd();
    }

    public interface StartListener {
        public void recordStart();
    }
}
