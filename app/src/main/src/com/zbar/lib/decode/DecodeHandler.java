package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import vip.inteltech.coolbaby.R;
import com.zbar.lib.MCaptureActivity;
import com.zbar.lib.ZbarManager;

/**
 * 娴ｆ粏锟斤拷: 闂勫牊绋�(1076559197@qq.com)
 * 
 * 閺冨爼妫�: 2014楠烇拷5閺堬拷9閺冿拷 娑撳宕�12:24:13
 *
 * 閻楀牊婀�: V_1.0.0
 *
 * 閹诲繗鍫�: 閹恒儱褰堝☉鍫熶紖閸氬氦袙閻拷
 */
final class DecodeHandler extends Handler {

	MCaptureActivity activity = null;

	DecodeHandler(MCaptureActivity activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.decode:
			decode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
			Looper.myLooper().quit();
			break;
		}
	}

	private void decode(byte[] data, int width, int height) {
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;// Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;

		ZbarManager manager = new ZbarManager();
		String result = manager.decode(rotatedData, width, height, true,
				activity.getX(), activity.getY(), activity.getCropWidth(),
				activity.getCropHeight());

		if (result != null) {
			if(null != activity.getHandler()){
				Message msg = new Message();
				msg.obj = result;
				msg.what = R.id.decode_succeeded;
				activity.getHandler().sendMessage(msg);
			}
		} else {
			if (null != activity.getHandler()) {
				activity.getHandler().sendEmptyMessage(R.id.decode_failed);
			}
		}
	}

}
