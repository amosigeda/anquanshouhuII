package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class AddContactsA extends BaseActivity implements OnClickListener, WebServiceListener {
    private AddContactsA mContext;
    private GridView gv;
    private Button btn_right;
    private GVAdapter mGVAdapter;
    private int selectRelation = -1;
    private int typeIndex = 0;
    private String bindNumber;

    //private String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_contacts_a);
        mContext = this;
        Intent intent = getIntent();
        typeIndex = intent.getIntExtra("typeIndex", 0);
        bindNumber = intent.getStringExtra("bindNumber");
        //serialNumber = intent.getStringExtra("serialNumber");
        findViewById(R.id.btn_left).setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        if (typeIndex != 0) {
            btn_right.setText(R.string.confirm);
        }
        btn_right.setOnClickListener(this);
        gv = (GridView) findViewById(R.id.gv);
        mGVAdapter = new GVAdapter(this);
        gv.setAdapter(mGVAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectRelation = position;
                mGVAdapter.notifyDataSetChanged();
                if (position == 6) {
                    editDialog();
                }
            }
        });
    }

    private Dialog dialog;

    private void editDialog() {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
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
        tv.setText(R.string.edit_name);
        final EditText et = (EditText) view.findViewById(R.id.et);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        //et.setInputType(InputType.TYPE_CLASS_PHONE);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectRelation == -1) {
                    return;
                }
                Intent intent_a;
                if (typeIndex == 0) {
                    intent_a = new Intent(mContext, AddContactsB.class);
                    intent_a.putExtra("relation", selectRelation);
                    intent_a.putExtra("relationStr", et.getText().toString().trim());
                    intent_a.putExtra("bindNumber", bindNumber);
                    startActivity(intent_a);
                    finish();
                } else if (typeIndex == 1) {
                    if (TextUtils.isEmpty(et.getText().toString().trim())) {
                        MToast.makeText(R.string.invalid_content).show();
                        return;
                    }
                    intent_a = new Intent();
                    intent_a.putExtra("relation", selectRelation);
                    intent_a.putExtra("relationStr", et.getText().toString().trim());
                    setResult(RESULT_OK, intent_a);
                    finish();
                } else if (typeIndex == 2) {
                    LinkDevice(et.getText().toString().trim());
                } else if (typeIndex == 3) {
                    WebService ws = new WebService(mContext, _LinkDeviceConfirm, true, "LinkDeviceConfirm");
                    List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
                    property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
                    property.add(new WebServiceProperty("deviceId", String.valueOf(getIntent().getIntExtra("deviceId", 0))));
                    property.add(new WebServiceProperty("userId", getIntent().getStringExtra("userId")));
                    property.add(new WebServiceProperty("name", et.getText().toString().trim()));
                    property.add(new WebServiceProperty("photo", String.valueOf(selectRelation + 1)));
                    property.add(new WebServiceProperty("confirm", "1"));
                    ws.addWebServiceListener(mContext);
                    ws.SyncGet(property);
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.btn_right:
                if (selectRelation == -1) {
                    return;
                }
                Intent intent_a;
                if (typeIndex == 0) {
                    intent_a = new Intent(mContext, AddContactsB.class);
                    intent_a.putExtra("relation", selectRelation);
                    intent_a.putExtra("relationStr", getResources().getString(mRelation[selectRelation]));
                    intent_a.putExtra("bindNumber", bindNumber);
                    startActivityForResult(intent_a, CONTACTSB);
                } else if (typeIndex == 1) {
                    intent_a = new Intent();
                    intent_a.putExtra("relation", selectRelation);
                    intent_a.putExtra("relationStr", getResources().getString(mRelation[selectRelation]));
                    setResult(RESULT_OK, intent_a);
                    finish();
                } else if (typeIndex == 2) {
                    LinkDevice(getResources().getString(mRelation[selectRelation]));
                } else if (typeIndex == 3) {
                    WebService ws = new WebService(mContext, _LinkDeviceConfirm, true, "LinkDeviceConfirm");
                    List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
                    property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
                    property.add(new WebServiceProperty("deviceId", String.valueOf(getIntent().getIntExtra("deviceId", 0))));
                    property.add(new WebServiceProperty("userId", getIntent().getStringExtra("userId")));
                    property.add(new WebServiceProperty("name", getResources().getString(mRelation[selectRelation])));
                    property.add(new WebServiceProperty("photo", String.valueOf(selectRelation + 1)));
                    property.add(new WebServiceProperty("confirm", "1"));
                    ws.addWebServiceListener(mContext);
                    ws.SyncGet(property);
                }

                break;
        }
    }

    private void LinkDevice(String str) {
        Intent intent = new Intent();
        intent.putExtra("photo", String.valueOf(selectRelation + 1));
        intent.putExtra("name", str);
        setResult(RESULT_OK, intent);
        //System.out.println("AddContact");
        finish();
        //System.out.println("LinkDevice" + str + " "+ serialNumber+ " " + String.valueOf(selectRelation+1));
        //WebServiceUtils.LinkDevice(_LinkDevice, String.valueOf(selectRelation+1), str, serialNumber, mContext);
		/*WebService ws = new WebService(mContext, _LinkDevice,true, "LinkDevice");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("name", str));
		property.add(new WebServiceProperty("photo", String.valueOf(selectRelation+1)));
		property.add(new WebServiceProperty("serialNumber", serialNumber));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);*/
    }

    private final int _LinkDevice = 1;
    private final int _LinkDeviceConfirm = 7;
    private final int _GetDeviceContact = 6;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        // TODO Auto-generated method stub
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _LinkDevice) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //1成功
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    //MToast.makeText(jsonObject.getString("Message")).show();
                    MToast.makeText(R.string.bind_fail).show();
                }
            } else if (id == _LinkDeviceConfirm) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.deleteUnconfirmed(getIntent().getStringExtra("userId"), AppData.GetInstance(mContext).getSelectDeviceId());
                    WebServiceUtils.GetDeviceContact(mContext, _GetDeviceContact, String.valueOf(getIntent().getIntExtra("deviceId", 0)), mContext, false, false);
                } else {
                    //MToast.makeText(R.string.add_contacts_fail).show();
                    MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _GetDeviceContact) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    // 1成功
                    JSONArray arrContact = jsonObject.getJSONArray("ContactArr");
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.deleteWatchContact(getIntent().getIntExtra("deviceId", 0));
                    for (int j = 0; j < arrContact.length(); j++) {
                        JSONObject item = arrContact.getJSONObject(j);
                        ContactModel mContactModel = new ContactModel();
                        mContactModel.setId(item.getString("DeviceContactId"));
                        mContactModel.setFromId(getIntent().getIntExtra("deviceId", 0));
                        mContactModel.setObjectId(item.getString("ObjectId"));
                        mContactModel.setRelationShip(item.getString("Relationship"));
                        mContactModel.setAvatar(item.getString("Photo"));
                        mContactModel.setPhone(item.getString("PhoneNumber"));
                        mContactModel.setCornet(item.getString("PhoneShort"));
                        mContactModel.setType(item.getString("Type"));

                        mContactDao.saveContact(mContactModel);
                    }
                    if (getIntent().getIntExtra("deviceId", 0) == AppData.GetInstance(this).getSelectDeviceId()) {
                        AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(this).getSelectDeviceId()));
                    }
                    finish();
                    // MToast.makeText(R.string.wait_admin_confirm).show();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Integer[] mThumbIds = {
            R.drawable.contacts_father_big, R.drawable.contacts_mom_big,
            R.drawable.contacts_grandfather_big, R.drawable.contacts_grandmother_big,
            R.drawable.contacts_grandpa_big, R.drawable.contacts_grandma_big,
            R.drawable.contacts_custom_big};
    private Integer[] mRelation = {
            R.string.father, R.string.mother,
            R.string.grandfather, R.string.grandmother,
            R.string.grandpa, R.string.grandma,
            R.string.custom};

    private class GVAdapter extends BaseAdapter {
        private Context mContext;

        public GVAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return mThumbIds[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub  
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //定义一个ImageView,显示在GridView里  
            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_contacts_a_item, null);
                mViewHolder.iv_relat = (ImageView) convertView.findViewById(R.id.iv_relat);
                mViewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
                mViewHolder.tv_relat = (TextView) convertView.findViewById(R.id.tv_relat);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.iv_relat.setImageResource(mThumbIds[position]);
            mViewHolder.tv_relat.setText(mRelation[position]);
            if (selectRelation == position) {
                mViewHolder.iv_select.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.iv_select.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv_relat, iv_select;
        TextView tv_relat;
    }

    private final int CONTACTSB = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CONTACTSB:
                if (resultCode == RESULT_OK)
                    finish();
                break;
        }
    }
}
