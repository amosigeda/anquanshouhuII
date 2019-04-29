package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.MsgRecordDao;
import vip.inteltech.gat.model.MsgRecordModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.CircularImageView;
import vip.inteltech.gat.viewutils.LJListView;
import vip.inteltech.gat.viewutils.LJListView.IXListViewListener;
import vip.inteltech.gat.viewutils.MToast;

public class MsgRecord extends BaseActivity implements OnClickListener, WebServiceListener {
    private MsgRecord mContext;
    private List<MsgRecordModel> mMsgRecordList;
    private LJListView lv;
    private MyAdapter myAdapter, myAdapter_del;
    private int selectPosition = -1;
    private Button btn_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.msg_record);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        initData();
        lv = (LJListView) findViewById(R.id.lv);
        lv.setPullLoadEnable(false, "");
        lv.setPullRefreshEnable(true);
        lv.setIsAnimation(true);
        lv.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                GetMessage();
            }

            @Override
            public void onLoadMore() {
            }
        });
        myAdapter = new MyAdapter(this);
        myAdapter_del = new MyAdapter(this);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position - 1;
                //System.out.println(mMsgRecordList.get(position-1).getMessage()+"   "+mMsgRecordList.get(position-1).getType());
                //MToast.makeText(mMsgRecordList.get(position-1).getMessage()+"   "+mMsgRecordList.get(position-1).getType()).show();
                /*System.out.println(position-1+"");
                System.out.println(mMsgRecordList.size()+"");*/
                if (mMsgRecordList.get(selectPosition).getType().equals("2")) {
                    askBindingDialog(mMsgRecordList.get(selectPosition).getMessage());
                } else if (Integer.valueOf(mMsgRecordList.get(selectPosition).getType()) > 100 && Integer.valueOf(mMsgRecordList.get(selectPosition).getType()) < 200) {
                    Intent intent = new Intent(mContext, MsgRecordLocation.class);
                    if (TextUtils.isEmpty(mMsgRecordList.get(selectPosition).getContent()))
                        return;
                    if (Double.valueOf(mMsgRecordList.get(selectPosition).getContent().split("-")[0]) == 0 && Double.valueOf(mMsgRecordList.get(selectPosition).getContent().split("-")[1]) == 0) {
                        WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                            MToast.makeText(R.string.no_locationinfo_1).show();
                        } else {
                            MToast.makeText(R.string.no_locationinfo).show();
                        }
                        return;
                    }
                    intent.putExtra("content", mMsgRecordList.get(selectPosition).getContent());
                    intent.putExtra("CreateTime", mMsgRecordList.get(selectPosition).getCreateTime());
                    startActivity(intent);
                }/*else if(mMsgRecordList.get(position-1).getType().equals("103")){
					Intent intent = new Intent(mContext, MsgRecordLocation.class);
					if(TextUtils.isEmpty(mMsgRecordList.get(position-1).getContent()))
						return;
					intent.putExtra("content", mMsgRecordList.get(position-1).getContent());
					startActivity(intent);
				}*/
            }
        });
        GetMessage();
        initReceiver();
    }

    private WatchModel mWatchModel;

    private void initData() {
        mWatchModel = AppContext.getInstance().getmWatchModel();
        MsgRecordDao mMsgRecordDao = new MsgRecordDao(this);
        mMsgRecordList = new ArrayList<MsgRecordModel>();
        mMsgRecordList = mMsgRecordDao.getMsgRecordList(
                AppData.GetInstance(mContext).getSelectDeviceId(),
                AppData.GetInstance(mContext).getUserId());
        //System.out.println(mMsgRecordList.size() + "");
		/*MsgRecordModel mMsgRecordModel = new MsgRecordModel();
		mMsgRecordModel.setType("11");
		mMsgRecordModel.setDeviceID("4");
		mMsgRecordModel.setContent("dasda");
		mMsgRecordDao.saveMsgRecord(mMsgRecordModel);
		mMsgRecordModel = new MsgRecordModel();
		mMsgRecordModel.setType("33");
		mMsgRecordModel.setDeviceID("4");
		mMsgRecordModel.setContent("daddda");
		mMsgRecordDao.saveMsgRecord(mMsgRecordModel);
		MsgRecordModel mMsgRecordModels = mMsgRecordDao.getMsgRecordList(4).get(0);
		MsgRecordModel mMsgRecordModelss = mMsgRecordDao.getMsgRecordList(4).get(1);
		System.out.println(mMsgRecordModels.getId()+"  "+mMsgRecordModelss.getId());*/
    }

    private void initReceiver() {
        IntentFilter IntentFilter_a = new IntentFilter(
                Contents.getMsgRecordBrodcast);
        IntentFilter_a.setPriority(5);
        registerReceiver(getMsgRecordReceiver, IntentFilter_a);
    }

    private void unReceiver() {
        try {
            unregisterReceiver(getMsgRecordReceiver);
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver getMsgRecordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetMessage();
        }
    };

    private void onLoad(String count) {
        lv.setCount(count);
        lv.stopRefresh();
        lv.stopLoadMore();
        lv.setRefreshTime("刚刚");
    }

    boolean isEdit = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_right_out);
                break;
            case R.id.btn_right:
                if (!isEdit) {
                    isEdit = true;
                    lv.setAdapter(myAdapter_del);
                    btn_right.setText(R.string.finish);
                } else {
                    isEdit = false;
                    lv.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    btn_right.setText(R.string.edit);
                }
                break;
        }
    }

    private Dialog dialog;

    private void askBindingDialog(String msg) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater()
                .inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        /*
         * wl.x = getWindowManager().getDefaultDisplay().getWidth()/2; wl.y =
         * getWindowManager().getDefaultDisplay().getHeight()/2;
         */
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView tv = (TextView) view.findViewById(R.id.tv);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv.setText(R.string.ask_binding_locator);
        } else {
            tv.setText(R.string.ask_binding_watch);
        }
        tv_content.setText(msg);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setText(R.string.agree);
        btn_cancel.setText(R.string.refuse);
        btn_OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddContactsA.class);
                intent.putExtra("typeIndex", 3);
                intent.putExtra("deviceId", AppData.GetInstance(mContext).getSelectDeviceId());
                intent.putExtra("userId", mMsgRecordList.get(selectPosition).getContent().split(",")[0]);
                // intent.putExtra("serialNumber", serialNumber);
                startActivityForResult(intent, AGREEBIND);

                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinkDeviceConfirm(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()), mMsgRecordList.get(selectPosition).getContent().split(",")[0], null, null, "0");
                dialog.cancel();
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return mMsgRecordList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                if (!isEdit) {
                    v = LayoutInflater.from(mContext).inflate(R.layout.msg_record_item, parent, false);
                } else {
                    v = LayoutInflater.from(mContext).inflate(R.layout.msg_record_item_del, parent, false);
                }
                mViewHolder.iv_head = v.findViewById(R.id.iv_head);
                mViewHolder.tv_name = v.findViewById(R.id.tv_name);
                mViewHolder.tv_time = v.findViewById(R.id.tv_time);
                mViewHolder.tv_content_a = v.findViewById(R.id.tv_content_a);
                mViewHolder.tv_content_b = v.findViewById(R.id.tv_content_b);
                mViewHolder.btn_del = v.findViewById(R.id.btn_del);

                mViewHolder.iv_type = v.findViewById(R.id.iv_type);
                v.setTag(mViewHolder);
            } else {
                v = convertView;
                mViewHolder = (ViewHolder) v.getTag();
            }

            ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL + mWatchModel.getAvatar(), mViewHolder.iv_head, new AnimateFirstDisplayListener());
            mViewHolder.tv_content_b.setText(mMsgRecordList.get(position).getMessage());
            mViewHolder.tv_time.setText(DateConversion.TimeChange(mMsgRecordList.get(position).getCreateTime(), null));
            mViewHolder.tv_content_a.setText("");
            mViewHolder.iv_type.setImageResource(R.drawable.ask_bind);
            if (mMsgRecordList.get(position).getType().equals("2")) {
                WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    mViewHolder.tv_content_a.setText(R.string.ask_binding_locator);
                } else {
                    mViewHolder.tv_content_a.setText(R.string.ask_binding_watch);
                }
                mViewHolder.iv_type.setImageResource(R.drawable.ask_bind);
            } else if (mMsgRecordList.get(position).getType().equals("3")) {
                mViewHolder.tv_content_a.setText(R.string.admin_agree);
                mViewHolder.iv_type.setImageResource(R.drawable.agree_bind);
            } else if (mMsgRecordList.get(position).getType().equals("4")) {
                mViewHolder.tv_content_a.setText(R.string.admin_refuse);
                mViewHolder.iv_type.setImageResource(R.drawable.refuse_bind);
            } else if (mMsgRecordList.get(position).getType().equals("5")) {
                WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    mViewHolder.tv_content_a.setText(R.string.locator_update);
                } else {
                    mViewHolder.tv_content_a.setText(R.string.watch_update);
                }
                mViewHolder.iv_type.setImageResource(R.drawable.watch_update);
            } else if (mMsgRecordList.get(position).getType().equals("6")) {
                WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    mViewHolder.tv_content_a.setText(R.string.locatorInfo_Synchronous);
                } else {
                    mViewHolder.tv_content_a.setText(R.string.watchInfo_Synchronous);
                }
                mViewHolder.iv_type.setImageResource(R.drawable.watchinfo_synchro);
            } else if (mMsgRecordList.get(position).getType().equals("7")) {
                mViewHolder.tv_content_a.setText(R.string.concast_Synchronous);
                mViewHolder.iv_type.setImageResource(R.drawable.adressbook_synchro);
            } else if (mMsgRecordList.get(position).getType().equals("9")) {
                mViewHolder.tv_content_a.setText(R.string.admin_unbind);
                mViewHolder.iv_type.setImageResource(R.drawable.unbind);
            } else if (mMsgRecordList.get(position).getType().equals("10")) {
                mViewHolder.tv_content_a.setText(R.string.baby_Synchronous);
                mViewHolder.iv_type.setImageResource(R.drawable.babyinfo_synchro);
            } else if (mMsgRecordList.get(position).getType().equals("11")) {
                WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
                if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    mViewHolder.tv_content_a.setText(R.string.locator_camera);
                } else {
                    mViewHolder.tv_content_a.setText(R.string.watch_camera);
                }
                mViewHolder.iv_type.setImageResource(R.drawable.take_photo);
            } else if (Integer.valueOf(mMsgRecordList.get(position).getType()) > 100 && Integer.valueOf(mMsgRecordList.get(position).getType()) < 200) {
                mViewHolder.tv_content_a.setText(R.string.alert);
                mViewHolder.iv_type.setImageResource(R.drawable.alert);
            } else if (Integer.valueOf(mMsgRecordList.get(position).getType()) >= 200) {
                if (mMsgRecordList.get(position).getType().equals("210")) {
                    mViewHolder.tv_content_a.setText(mMsgRecordList.get(position).getMessage());
                    mViewHolder.iv_type.setImageResource(R.drawable.announcement);
                    mViewHolder.tv_content_b.setText(mMsgRecordList.get(position).getContent());
                } else {
                    mViewHolder.tv_content_a.setText(R.string.defend_remind);
                    mViewHolder.iv_type.setImageResource(R.drawable.school_defend_return);
                }
            }
            //System.out.println(mMsgRecordList.get(position).getType());
            mViewHolder.btn_del.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position >= mMsgRecordList.size()) {
                        return;
                    }
                    MsgRecordDao mMsgRecordDao = new MsgRecordDao(mContext);
                    mMsgRecordDao.deleteMsgRecord(mMsgRecordList.get(position).getId());
                    myAdapter_del.notifyDataSetChanged();
                    mMsgRecordList.remove(position);
                }
            });

            return v;
        }

    }

    class ViewHolder {
        CircularImageView iv_head;
        TextView tv_name, tv_time, tv_content_a, tv_content_b;
        ImageView iv_type;
        Button btn_del;
    }

    private void GetMessage() {
        WebService ws = new WebService(mContext, _GetMessage, true,
                "GetMessage");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(this).getSelectDeviceId())));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void LinkDeviceConfirm(String deviceId, String userId, String photo, String name, String confirm) {
        WebService ws = new WebService(mContext, _LinkDeviceConfirm, true,
                "LinkDeviceConfirm");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData
                .GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceId", deviceId));
        property.add(new WebServiceProperty("userId", userId));
        if (confirm.equals("1")) {
            property.add(new WebServiceProperty("name", name));
            property.add(new WebServiceProperty("photo", photo));
        }
        property.add(new WebServiceProperty("confirm", confirm));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _GetMessage = 0;
    private final int _LinkDeviceConfirm = 1;
    private final int _GetDeviceContact = 2;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _GetMessage) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    JSONArray arrs = jsonObject.getJSONArray("List");
                    MsgRecordDao mMsgRecordDao = new MsgRecordDao(this);
                    int j;
                    for (j = 0; j < arrs.length(); j++) {
                        JSONObject items = arrs.getJSONObject(j);
                        MsgRecordModel mMsgRecordModel = new MsgRecordModel();
                        mMsgRecordModel.setType(items.getString("Type"));
                        mMsgRecordModel.setDeviceID(items.getString("DeviceID"));
                        mMsgRecordModel.setUserID(String.valueOf(AppData.GetInstance(mContext).getUserId()));
                        mMsgRecordModel.setContent(items.getString("Content"));
                        mMsgRecordModel.setMessage(items.getString("Message"));
                        mMsgRecordModel.setCreateTime(items.getString("CreateTime"));
                        mMsgRecordDao.saveMsgRecord(mMsgRecordModel);
                    }
                    mMsgRecordList = mMsgRecordDao.getMsgRecordList(AppData.GetInstance(mContext).getSelectDeviceId(),
                            AppData.GetInstance(mContext).getUserId());
                    myAdapter.notifyDataSetChanged();
                    onLoad(String.valueOf(j));
                } else if (code == 2) {
                    onLoad("0");
                } else {
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _LinkDeviceConfirm) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    ContactDao mContactDao = new ContactDao(this);
                    if (selectPosition < mMsgRecordList.size()) {
                        mContactDao.deleteUnconfirmed(mMsgRecordList.get(selectPosition).getContent().split(",")[0], AppData.GetInstance(mContext).getSelectDeviceId());
                    }
					/*WebServiceUtils.GetDeviceContact(mContext,
							_GetDeviceContact, String.valueOf(mMsgRecordList.get(selectPosition).getDeviceID()), null, true, false);*/
                    AppContext.getInstance().setContactList(
                            mContactDao.getContactList(AppData
                                    .GetInstance(mContext)
                                    .getSelectDeviceId()));
                } else if (code == -1 || code == 8) {
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else {
                    MToast.makeText(R.string.add_contacts_fail).show();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final int AGREEBIND = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AGREEBIND:
                if (resultCode == RESULT_OK) {
                    String photo = data.getStringExtra("photo");
                    String name = data.getStringExtra("name");
                    LinkDeviceConfirm(mMsgRecordList.get(selectPosition).getDeviceID(), mMsgRecordList.get(selectPosition).getContent().split(",")[0], photo, name, "1");
                    // WebServiceUtils.LinkDevice(_LinkDevice, photo, name,
                    // serialNumber, mContext);
                }
                break;
            default:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.getInstance().setMsgRecordShow(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getInstance().setMsgRecordShow(false);
        unReceiver();
    }
}
