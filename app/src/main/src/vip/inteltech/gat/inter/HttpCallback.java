package vip.inteltech.gat.inter;

import org.xutils.common.Callback;

import vip.inteltech.gat.utils.CommUtil;


/**
 *
 */
public abstract class HttpCallback<T> implements Callback.ProgressCallback<T> {

    @Override
    public void onWaiting() {

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        CommUtil.showMsgShort("Network Error!");
    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
