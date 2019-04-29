package vip.inteltech.gat.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;


/**
 * @author vondear
 * @date 2016/7/19
 * Mainly used for confirmation and cancel.
 */
public class DialogEditSureCancel extends CommDialog {

    private ImageView mIvLogo;
    private TextView mTvSure;
    private TextView mTvCancel;
    private EditText editText;
    private TextView mTvTitle;

    public DialogEditSureCancel(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public DialogEditSureCancel(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public DialogEditSureCancel(Context context) {
        super(context);
        initView();
    }

    public DialogEditSureCancel(Activity context) {
        super(context);
        initView();
    }

    public DialogEditSureCancel(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public ImageView getLogoView() {
        return mIvLogo;
    }

    @Override
    public void setTitle(int titleId) {
        mTvTitle.setText(getContext().getString(titleId));
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public TextView getTitleView() {
        return mTvTitle;
    }

    public EditText getEditText() {
        return editText;
    }

    public TextView getSureView() {
        return mTvSure;
    }

    public void setSure(String strSure) {
        this.mTvSure.setText(strSure);
    }

    public void setSure(int sureId) {
        this.mTvSure.setText(getContext().getString(sureId));
    }

    public TextView getCancelView() {
        return mTvCancel;
    }

    public void setCancel(String strCancel) {
        this.mTvCancel.setText(strCancel);
    }

    public void setCancel(int cancelId) {
        this.mTvCancel.setText(getContext().getString(cancelId));
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edittext_sure_false, null);
        mIvLogo = (ImageView) dialogView.findViewById(R.id.iv_logo);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        mTvSure = (TextView) dialogView.findViewById(R.id.tv_sure);
        mTvCancel = (TextView) dialogView.findViewById(R.id.tv_cancle);
        editText = (EditText) dialogView.findViewById(R.id.editText);
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
        super.show();
    }
}
