package vip.inteltech.gat.viewutils;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.inter.CommCallback;

public class CallingView {
    static Dialog dialog;

    public static Dialog show(Context mContext, String note, CommCallback callback) {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calling, null);
        dialog = new Dialog(mContext, R.style.transparentFrameWindowFullStyle);
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
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.MATCH_PARENT;
        TextView noteTV = view.findViewById(R.id.note);
        noteTV.setText(note);
        ImageView hangUp = view.findViewById(R.id.hang_up);
        hangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (callback != null) {
                    callback.execute();
                }
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        //dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
}
