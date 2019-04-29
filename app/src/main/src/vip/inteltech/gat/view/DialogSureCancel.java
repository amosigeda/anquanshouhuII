package vip.inteltech.gat.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vondear.rxtool.RxRegTool;
import com.vondear.rxtool.RxTextTool;

import vip.inteltech.coolbaby.R;

public class DialogSureCancel extends CommDialog {

    private ImageView mIvLogo;
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvSure;
    private TextView mTvCancel;
    private View divider;
    private boolean isMultipleLines = false;


    public DialogSureCancel(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public DialogSureCancel(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public DialogSureCancel(Context context) {
        super(context);
        initView();
    }

    public DialogSureCancel(Activity context) {
        super(context);
        initView();
    }

    public DialogSureCancel(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public ImageView getLogoView() {
        return mIvLogo;
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public TextView getTitleView() {
        return mTvTitle;
    }

    public void setContent(String content) {
        if (RxRegTool.isURL(content)) {
            // 响应点击事件的话必须设置以下属性
            mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
            mTvContent.setText(RxTextTool.getBuilder("").setBold().append(content).setUrl(content).create());//当内容为网址的时候，内容变为可点击
        } else {
            mTvContent.setText(content);
        }
        if (content.contains("\n")) {
            isMultipleLines = true;
        }
    }

    public TextView getContentView() {
        return mTvContent;
    }

    public void setSure(String strSure) {
        this.mTvSure.setText(strSure);
    }

    public TextView getSureView() {
        return mTvSure;
    }

    public void setCancel(String strCancel) {
        this.mTvCancel.setText(strCancel);
    }

    public TextView getCancelView() {
        return mTvCancel;
    }

    public void setSureListener(View.OnClickListener sureListener) {
        mTvSure.setOnClickListener(sureListener);
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        mTvCancel.setOnClickListener(cancelListener);
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sure_false, null);
        mIvLogo = (ImageView) dialogView.findViewById(R.id.iv_logo);
        mTvSure = (TextView) dialogView.findViewById(R.id.tv_sure);
        mTvCancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        mTvContent = (TextView) dialogView.findViewById(R.id.tv_content);
        mTvContent.setTextIsSelectable(false);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        divider = dialogView.findViewById(R.id.divider);
        setContentView(dialogView);
    }

    @Override
    public void show() {
        if (!TextUtils.isEmpty(mTvTitle.getText())) {
            mTvTitle.setVisibility(View.VISIBLE);
        }
        if (mIvLogo.getDrawable() != null || mIvLogo.getBackground() != null) {
            mIvLogo.setVisibility(View.VISIBLE);
        }
        if (!mTvCancel.hasOnClickListeners()) {
            mTvCancel.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        if (isMultipleLines) {
            mTvContent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            mTvContent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        super.show();
    }
}
