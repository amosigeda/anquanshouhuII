package vip.inteltech.gat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.chatutil.ChatMsgEntity;
import vip.inteltech.gat.db.*;
import vip.inteltech.gat.model.AlbumModel;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.GeoFenceModel;
import vip.inteltech.gat.model.HealthModel;
import vip.inteltech.gat.model.MsgRecordModel;
import vip.inteltech.gat.model.SMSModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.service.MService;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class AboutWatch extends BaseActivity implements OnClickListener, WebServiceListener {
    private AboutWatch mContext;
    private TextView tv_watch_bound_no, tv_watch_firmware_version, tv_watch_carr, tv_model, tv_GPS, tv_WIFI, tv_three_axis_sensor,
            tv_title, iv_QR_Code, iv_sweep_bound, iv_bound_no, tv_firmware_version, tv_carrieroperator, tv_configuration;
    private ImageView iv_code;

    private WatchModel mWatchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_watch);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.rl_watch_bound_no).setOnClickListener(this);
        findViewById(R.id.rl_watch_firmware_version).setOnClickListener(this);
        findViewById(R.id.rl_watch_carr).setOnClickListener(this);
        findViewById(R.id.rl_model).setOnClickListener(this);
        findViewById(R.id.rl_GPS).setOnClickListener(this);
        findViewById(R.id.rl_WIFI).setOnClickListener(this);
        findViewById(R.id.rl_three_axis_sensor).setOnClickListener(this);
        findViewById(R.id.btn_unbound).setOnClickListener(this);

        findViewById(R.id.rl_watch_carr).setVisibility(View.GONE);

        tv_watch_bound_no = (TextView) findViewById(R.id.tv_watch_bound_no);
        tv_watch_firmware_version = (TextView) findViewById(R.id.tv_watch_firmware_version);
        tv_watch_carr = (TextView) findViewById(R.id.tv_watch_carr);
        tv_model = (TextView) findViewById(R.id.tv_model);
        tv_GPS = (TextView) findViewById(R.id.tv_GPS);
        tv_WIFI = (TextView) findViewById(R.id.tv_WIFI);
        tv_three_axis_sensor = (TextView) findViewById(R.id.tv_three_axis_sensor);
        tv_title = (TextView) findViewById(R.id.textView_Title);
        iv_QR_Code = (TextView) findViewById(R.id.iv_QR_Code);
        iv_sweep_bound = (TextView) findViewById(R.id.iv_sweep_bound);
        iv_bound_no = (TextView) findViewById(R.id.iv_bound_no);
        tv_firmware_version = (TextView) findViewById(R.id.tv_firmware_version);
        tv_carrieroperator = (TextView) findViewById(R.id.tv_carrieroperator);
        tv_configuration = (TextView) findViewById(R.id.tv_configuration);

        iv_code = (ImageView) findViewById(R.id.iv_code);

        initData();
    }

    private void initData() {
        mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));

        tv_watch_bound_no.setText(mWatchModel.getBindNumber());
        tv_watch_firmware_version.setText(mWatchModel.getCurrentFirmware());
        //1移动 2联通 3电信
        if (mWatchModel.getOperatorType().equals("3")) {
            tv_watch_carr.setText(R.string.dx);
        } else if (mWatchModel.getOperatorType().equals("2")) {
            tv_watch_carr.setText(R.string.lt);
        } else {
            tv_watch_carr.setText(R.string.yd);
        }
        tv_model.setText(Contents.APPName + mWatchModel.getModel());
        tv_GPS.setText(R.string.GPS_PS);
        tv_WIFI.setText(R.string.WIFI_PS);
        tv_three_axis_sensor.setText(R.string.three_axis_sensor_PS);
        if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_title.setText(R.string.about_locator);
            iv_QR_Code.setText(R.string.locator_QR_Code);
            iv_sweep_bound.setText(R.string.sweep_bound_1);
            iv_bound_no.setText(R.string.locator_bound_no);
            tv_firmware_version.setText(R.string.locator_firmware_version);
            tv_carrieroperator.setText(R.string.locator_carrieroperator);
            tv_configuration.setText(R.string.configuration_1);
        }

        String content = mWatchModel.getBindNumber();
        try {
            Bitmap logo = BitmapFactory.decodeResource(super.getResources(), R.drawable.logo);
            Bitmap bm = CreateCodeUtil.createCode(content, logo, BarcodeFormat.QR_CODE);
            iv_code.setImageBitmap(bm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_watch_bound_no:
                break;
            case R.id.rl_watch_firmware_version:
                break;
            case R.id.rl_watch_carr:
                break;
            case R.id.rl_model:
                break;
            case R.id.rl_GPS:
                break;
            case R.id.rl_WIFI:
                break;
            case R.id.rl_three_axis_sensor:
                break;
            case R.id.btn_unbound:
                releaseBoundDialog();
                break;
        }
    }

    private Dialog dialog;

    private void releaseBoundDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        /*wl.x = getWindowManager().getDefaultDisplay().getWidth()/2;
        wl.y = getWindowManager().getDefaultDisplay().getHeight()/2;
		*/
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(R.string.unbound);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(R.string.sure_unbind);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteContact();
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void DeleteContact() {
        WebService ws = new WebService(mContext, _ReleaseBound, true, "ReleaseBound");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _ReleaseBound = 0;
    private final int _GetDeviceDetail = 1;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (id == _ReleaseBound) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    int deviceId = AppData.GetInstance(this).getSelectDeviceId();
                    Map<String, WatchModel> WatchMap = AppContext.getInstance().getWatchMap();
                    WatchMap.remove(String.valueOf(deviceId));
                    WatchDao mWatchDao = new WatchDao(mContext);
                    mWatchDao.deleteWatch(deviceId);

                    clearUserInfo(deviceId);

                    if (WatchMap.size() != 0) {
                        Iterator<Entry<String, WatchModel>> iterator = WatchMap.entrySet().iterator();
                        Entry<String, WatchModel> entry = iterator.next();
                        AppData.GetInstance(mContext).setSelectDeviceId(entry.getValue().getId());
                        AppContext.getInstance().setmWatchModel(AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
                        ContactDao mContactDao = new ContactDao(mContext);
                        AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(mContext).getSelectDeviceId()));

                        WatchSetDao mWatchSetDao = new WatchSetDao(mContext);
                        AppContext.getInstance().setSelectWatchSet(mWatchSetDao.getWatchSet(AppData.GetInstance(mContext).getSelectDeviceId()));

                        WatchStateDao mWatchStateDao = new WatchStateDao(mContext);
                        AppContext.getInstance().setmWatchStateModel(mWatchStateDao.getWatchState(AppData.GetInstance(mContext).getSelectDeviceId()));
                        ChatMsgDao mChatMsgDao = new ChatMsgDao(mContext);
                        List<ChatMsgEntity> allDataArrays = mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId());
                        if (allDataArrays.size() > Contents.CHATMSGINITIAL) {
                            AppContext.getInstance().setChatMsgList(allDataArrays.subList(allDataArrays.size() - Contents.CHATMSGINITIAL, allDataArrays.size()));
                        } else {
                            AppContext.getInstance().setChatMsgList(mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId()));
                        }
                        AppContext.getInstance().setmWatchModel(AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
                        finish();
                    } else {
                        AppContext.getInstance().finishAll();
                        Intent intent = new Intent(mContext, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        AppData.GetInstance(mContext).setLoginAuto(false);
                        startActivity(intent);
                        Intent intent_a = new Intent(mContext, MService.class);
                        stopService(intent_a);
                    }
                    MToast.makeText(R.string.have_unbind).show();
                } else if (code == -99) {
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else {
                    //-1输入参数错误，0登录异常，6无权修改管理员信息
                    MToast.makeText(R.string.unbind_fail).show();
                }
            } else if (id == _GetDeviceDetail) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    //1移动 2联通 3电信
                    if (jsonObject.getString("OperatorType").equals("3")) {
                        tv_watch_carr.setText(R.string.dx);
                    } else if (jsonObject.getString("OperatorType").equals("2")) {
                        tv_watch_carr.setText(R.string.lt);
                    } else {
                        tv_watch_carr.setText(R.string.yd);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearUserInfo(int deviceId) {
        try {
            int userId = AppData.GetInstance(mContext).getUserId();
            AppContext.db.delete(SMSModel.class, WhereBuilder.b().and("UserID", "=", userId));
            AppContext.db.deleteById(WatchSetModel.class, deviceId);
            AppContext.db.delete(MsgRecordModel.class, WhereBuilder.b().or("UserID", "=", userId).or("DeviceID", "=", deviceId));
            AppContext.db.delete(ChatMsgEntity.class, WhereBuilder.b().and("DeviceID", "=", deviceId));
            AppContext.db.delete(AlbumModel.class, WhereBuilder.b().and("DeviceID", "=", deviceId));
            AppContext.db.delete(GeoFenceModel.class, WhereBuilder.b().and("DeviceID", "=", deviceId));
            AppContext.db.delete(HealthModel.class, WhereBuilder.b().and("wId", "=", deviceId));
            AppContext.db.delete(WatchStateModel.class, WhereBuilder.b().and("wId", "=", deviceId));
            AppContext.db.delete(ContactModel.class, WhereBuilder.b().and("fromId", "=", deviceId).and("type", "<", 4));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}