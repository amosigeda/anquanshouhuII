package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Message;

import vip.inteltech.coolbaby.R;
import com.zbar.lib.MCaptureActivity;
import com.zbar.lib.camera.CameraManager;

/**
 * 濞达絾绮忛敓鏂ゆ嫹: 闂傚嫬鐗婄粙锟�(1076559197@qq.com)
 * 
 * 闁哄啫鐖煎Λ锟�: 2014妤犵儑鎷�5闁哄牞鎷�9闁哄喛鎷� 濞戞挸顑呭畷锟�12:23:32
 *
 * 闁绘鐗婂﹢锟�: V_1.0.0
 *
 * 闁硅绻楅崼锟�: 闁规鍋呭鍨槈閸喍绱栭弶鐑嗗墮瑜帮拷
 */
public final class CaptureActivityHandler extends Handler {

	DecodeThread decodeThread = null;
	MCaptureActivity activity = null;
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(MCaptureActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.SUCCESS;
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case R.id.auto_focus:
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			}
			break;
		case R.id.restart_preview:
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			state = State.SUCCESS;
			activity.handleDecode((String) message.obj);// 閻熸瑱绲鹃悗浠嬪箣閹邦剙顫犻柨娑樿嫰濞叉牜鎷敓锟�
			break;

		case R.id.decode_failed:
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			break;
		}

	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
		removeMessages(R.id.decode);
		removeMessages(R.id.auto_focus);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
		}
	}

}
