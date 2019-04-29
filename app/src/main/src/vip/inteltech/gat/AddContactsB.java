package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;

public class AddContactsB extends BaseActivity implements OnClickListener,
        WebServiceListener {
    private AddContactsB mContext;
    private TextView tv_relat;
    private EditText et_phone_num, et_cornet_num;
    private ImageView iv1, iv2;
    private int relation;
    private String relationStr;

    private String phoneNum;
    private String bindNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_contacts_b);
        Intent intent = getIntent();
        relation = intent.getIntExtra("relation", -1) + 1;
        relationStr = intent.getStringExtra("relationStr");
        bindNumber = intent.getStringExtra("bindNumber");
        tv_relat = (TextView) findViewById(R.id.tv_relat);
        tv_relat.setText(relationStr);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_OK).setOnClickListener(this);
        findViewById(R.id.btn_phone_book_a).setOnClickListener(this);
        findViewById(R.id.btn_phone_book_b).setOnClickListener(this);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        et_cornet_num = (EditText) findViewById(R.id.et_cornet_num);
        et_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                iv1.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(et_phone_num.getText().toString().trim())) {
                    iv1.setVisibility(View.INVISIBLE);
                }
            }
        });
        et_cornet_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                iv2.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils
                        .isEmpty(et_cornet_num.getText().toString().trim())) {
                    iv2.setVisibility(View.INVISIBLE);
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
            case R.id.btn_OK:
                AddContact();
                break;
            case R.id.iv1:
                et_phone_num.getText().clear();
                break;
            case R.id.iv2:
                et_cornet_num.getText().clear();
                break;
            case R.id.btn_phone_book_a:
                PermissionsUtil.requestPermission(this, Manifest.permission.READ_CONTACTS, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setData(ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, CONTACTS);
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        CommUtil.showMsgShort(R.string.contact_permission_denied);
                    }
                });

                break;
            case R.id.btn_phone_book_b:

                break;
        }
    }

    private void AddContact() {
        phoneNum = et_phone_num.getText().toString().trim();
        String cornet = et_cornet_num.getText().toString().trim();
//        if (!Utils.isMobileNO(phoneNum)) {
        if (TextUtils.isEmpty(phoneNum) || 11 != phoneNum.length()) {
            CommUtil.showMsgShort(R.string.phone_num_error);
            return;
        }
        /*
        else if(phoneNum.length() != 11){
			MToast.makeText(R.string.input_right_no).show();
			return;
		}*/
        WebService ws = new WebService(mContext, _AddContact, true, "AddContact");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        //property.add(new WebServiceProperty("type ", "1"));
        property.add(new WebServiceProperty("name", relationStr));
        property.add(new WebServiceProperty("photo", String.valueOf(relation)));
        property.add(new WebServiceProperty("phoneNum", phoneNum));
        property.add(new WebServiceProperty("phoneShort", cornet));
        property.add(new WebServiceProperty("bindNumber", bindNumber));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _AddContact = 0;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        JSONObject jsonObject;
        try {
            if (id == _AddContact) {
                jsonObject = new JSONObject(result);
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    ContactModel mContactModel = new ContactModel();
                    mContactModel.setId(jsonObject.getString("DeviceContactId"));
                    mContactModel.setFromId(AppData.GetInstance(mContext).getSelectDeviceId());
                    mContactModel.setObjectId("");
                    mContactModel.setRelationShip(relationStr);
                    mContactModel.setAvatar(String.valueOf(relation));
                    mContactModel.setPhone(et_phone_num.getText().toString().trim());
                    mContactModel.setCornet(et_cornet_num.getText().toString().trim());
                    mContactModel.setType("1");

                    ContactDao mContactDao = new ContactDao(mContext);
                    mContactDao.saveContact(mContactModel);
                    AppContext.getInstance().setContactList(
                            mContactDao.getContactList(AppData.GetInstance(this)
                                    .getSelectDeviceId()));
                    AppData.GetInstance(AddContactsB.this).setPhoneNumber(phoneNum);
                    setResult(RESULT_OK);
                    finish();
                } else if (code == -1 || code == 8) {
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else if (code == 9) {
                    MToast.makeText(R.string.add_contacts_fail_same_phone_number).show();
                } else {
                    MToast.makeText(R.string.add_contacts_fail).show();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final int CONTACTS = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CONTACTS:
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                if (uri == null) {
                    return;
                }
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor == null) {
                    return;
                }
                if (cursor.moveToFirst()) {
                    Cursor c2 = null;
                    try {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        c2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
                        if (c2 == null) {
                            return;
                        }
                        if (c2.moveToFirst()) {
                            String phone = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            et_phone_num.setText(phone);
                        }
                    } catch (Exception e) {
                        CommUtil.showMsgShort(R.string.read_contact_failed);
                    } finally {
                        if (c2 != null) {
                            c2.close();
                        }
                        cursor.close();
                    }
                }
                break;
        }
    }

}
