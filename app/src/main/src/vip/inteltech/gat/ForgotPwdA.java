package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.zbar.lib.MCaptureActivity;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class ForgotPwdA extends Activity implements OnClickListener, WebServiceListener {
    private ForgotPwdA mContext;
    private EditText et_phone_num;
    private ImageView iv1;
    private String scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pwd_a);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv1.setOnClickListener(this);
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        et_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                iv1.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(et_phone_num.getText().toString().trim())) {
                    iv1.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_next:
                ForgotCheck();
                break;
            case R.id.iv1:
                et_phone_num.getText().clear();
                break;
        }
    }

    private void ForgotCheck() {
        String phoneNum = et_phone_num.getText().toString().trim();
        if (!Utils.isMobileNO(phoneNum)) {
            CommUtil.showMsgShort(R.string.phone_num_error);
            return;
        }
        PermissionsUtil.requestPermission(this, Manifest.permission.CAMERA, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Intent intent_a = new Intent(mContext, MCaptureActivity.class);
                intent_a.putExtra("hideInput", "1");
                startActivityForResult(intent_a, SCANBIND);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                CommUtil.showMsgShort(R.string.permission_camera_denied);
            }
        });
    }

    private int _ForgotCheck = 0;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _ForgotCheck) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //MToast.makeText(R.string.have_send_verification_code).show();
                    Intent intent_a = new Intent(mContext, ForgotPwdB.class);
                    intent_a.putExtra("phoneNum", et_phone_num.getText().toString().trim());
                    intent_a.putExtra("scanResult", scanResult);
                    startActivity(intent_a);
                    finish();
                } else if (code == 3) {
                    // 3表示手机已经存在
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else if (code == 4) {
                    // 4验证码请求过于平凡，需要等待1分钟
                    MToast.makeText(R.string.have_send_verification_code_more).show();
                } else {
                    MToast.makeText(jsonObject.getString("Message")).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 点击空白处隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private final int SCANBIND = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANBIND:
                if (resultCode == RESULT_OK) {
                    String phoneNum = et_phone_num.getText().toString().trim();
                    Bundle bundle = data.getExtras();
                    scanResult = bundle.getString("result");
                    //MToast.makeText(scanResult).show();

                    WebService ws = new WebService(mContext, _ForgotCheck, true, "ForgotCheck");
                    List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
                    property.add(new WebServiceProperty("phoneNumber", phoneNum));
                    property.add(new WebServiceProperty("project", Contents.APPName));
                    property.add(new WebServiceProperty("SerialNumber", scanResult));
                    ws.addWebServiceListener(mContext);
                    ws.SyncGet(property);
                }
                break;
            default:
                break;
        }
    }
}