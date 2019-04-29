package vip.inteltech.gat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.*;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MListView;
import vip.inteltech.gat.viewutils.MListView.OnRefreshListener;
import vip.inteltech.gat.viewutils.MToast;


public class AddressBook extends BaseActivity implements OnClickListener, WebServiceListener {
    private AddressBook mContext;
    private MListView lv;
    private TextView tv_name, tv_watch_no, tv_cornet, tv_number;
    private ImageView iv_head;
    private List<ContactModel> contactsList;
    private WatchModel mWatchModel;
    private int[] headID = new int[]{R.drawable.contacts_father_small, R.drawable.contacts_mom_small, R.drawable.contacts_grandfather_small,
            R.drawable.contacts_grandmother_small, R.drawable.contacts_grandpa_small, R.drawable.contacts_grandma_small, R.drawable.contacts_custom_small,
            R.drawable.contacts_unconfirmed_small};
    private static int SelectPosition;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.address_book);
        if (savedInstanceState != null) {
            SelectPosition = savedInstanceState.getInt("SelectPosition");
            photoName = savedInstanceState.getString("photoName");
        }
        mContext = this;

        contactsList = AppContext.getInstance().getContactList();
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);

        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_watch_no = (TextView) findViewById(R.id.tv_watch_no);
        tv_cornet = (TextView) findViewById(R.id.tv_cornet);
        tv_number = (TextView) findViewById(R.id.tv_number);

        initData();

        lv = (MListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(this);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SelectPosition = position - 1;
                //System.out.println(contactsList.get(SelectPosition).getId()+ "    " + contactsList.get(SelectPosition).getAvatar());
                Log.i(AddressBook.class.getName(), contactsList.get(SelectPosition).getType() + "    " + contactsList.get(SelectPosition).getPhone());
                if (contactsList.get(SelectPosition).getType().equals("4")) {
                    askBindingDialog(SelectPosition);
                } else {
                    boolean isAdminMe, isMe = false, isCommon;
                    isAdminMe = mWatchModel.getUserId() == AppData.GetInstance(mContext).getUserId() || 2 == AppData.GetInstance(mContext).getUserType();
                    ;
                    isCommon = contactsList.get(SelectPosition).getType().equals("1");
                    if (!isCommon) {
                        isMe = (AppData.GetInstance(mContext).getUserId() == Integer.valueOf(contactsList.get(SelectPosition).getObjectId()));
                    }
                    if (isAdminMe) {
                        editAddressBookDialog(SelectPosition, isAdminMe, isMe, isCommon);
                    } else {
                        if (isMe)
                            editAddressBookDialog(SelectPosition, isAdminMe, isMe, isCommon);
                        else {
                            if (isCommon) {
                                editAddressBookDialog(SelectPosition, isAdminMe, isMe, isCommon);
                            } else {
                                return;
                            }
                        }
                    }
                }
                //System.out.println(position+"");
            }
        });
        lv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void toRefresh() {
                WebServiceUtils.GetDeviceContact(mContext, _GetDeviceContact, String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()), mContext, false, false);
            }
        });

        //WebServiceUtils.GetDeviceContact(mContext, 22, String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()), mContext);

        initReceiver();
    }

    private void initData() {
        WatchDao mWatchDao = new WatchDao(this);
        AppContext.getInstance().setmWatchModel(mWatchDao.getWatch(AppData.GetInstance(mContext).getSelectDeviceId()));
        mWatchModel = AppContext.getInstance().getmWatchModel();
        try {
            ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL + mWatchModel.getAvatar(), iv_head, new AnimateFirstDisplayListener());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        tv_name.setText(mWatchModel.getName());
        tv_watch_no.setText(mWatchModel.getPhone());
        tv_cornet.setText(mWatchModel.getCornet());
        if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_number.setText(R.string.locator_no);
        }
    }

    private void initReceiver() {
        IntentFilter IntentFilter_a = new IntentFilter(Contents.refreshContactBrodcast);
        IntentFilter_a.setPriority(5);
        registerReceiver(refreshContactReceiver, IntentFilter_a);
    }

    private void unReceiver() {
        try {
            unregisterReceiver(refreshContactReceiver);
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver refreshContactReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("refreshContactReceiver");
            WebServiceUtils.GetDeviceContact(mContext,
                    _GetDeviceContact, String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()),
                    mContext, false, false);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                //overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.btn_right:
                if (contactsList.size() >= 50) {
                    MToast.makeText(R.string.addressbook_limit).show();
                    return;
                }
                startActivity(new Intent(mContext, AddContactsA.class));
                break;
        }
    }

    private Dialog dialog;

    private void editAddressBookDialog(final int position, boolean isAdminMe, boolean isMe, boolean isCommen) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_address_book, null);
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
        tv.setText(contactsList.get(position).getRelationShip() + "(" + contactsList.get(position).getPhone() + ")");

        LinearLayout ll_edit_relat = (LinearLayout) view.findViewById(R.id.ll_edit_relat);
        LinearLayout ll_edit_phone = (LinearLayout) view.findViewById(R.id.ll_edit_phone);
        LinearLayout ll_add_cornet = (LinearLayout) view.findViewById(R.id.ll_add_cornet);
        LinearLayout ll_del_contact = (LinearLayout) view.findViewById(R.id.ll_del_contact);
        LinearLayout ll_edit_photo = (LinearLayout) view.findViewById(R.id.ll_edit_photo);
        if (isAdminMe) {
            ll_edit_relat.setVisibility(View.VISIBLE);
            ll_add_cornet.setVisibility(View.VISIBLE);
            ll_del_contact.setVisibility(View.VISIBLE);
            if (isMe && new Contents().canEditHead) {
                ll_edit_photo.setVisibility(View.VISIBLE);
            }
        } else if (isMe) {
            ll_edit_relat.setVisibility(View.VISIBLE);
            ll_add_cornet.setVisibility(View.VISIBLE);
            if (new Contents().canEditHead) {
                ll_edit_photo.setVisibility(View.VISIBLE);
            }
        } else if (isCommen) {
            ll_edit_relat.setVisibility(View.VISIBLE);
            ll_add_cornet.setVisibility(View.VISIBLE);

            //ll_del_contact.setVisibility(View.VISIBLE);
            //ll_edit_photo.setVisibility(View.VISIBLE);
        }
        ll_edit_phone.setVisibility(View.VISIBLE);
        ll_edit_relat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent_a = new Intent(mContext, AddContactsA.class);
                intent_a.putExtra("typeIndex", 1);
                startActivityForResult(intent_a, _EditRelation);
            }
        });
        ll_edit_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                selectPhotoDialog();
            }
        });

        //短号
        ll_add_cornet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                editIndex = 1;
                editDialog();
            }
        });

        //长号or座机
        ll_edit_phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                editIndex = 3;
                editDialog();
            }
        });
        ll_del_contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                delContactDialog(position);
            }
        });

        Button btn_cancel;
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
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

    int editIndex = -1;
    String str;
	
/*	 
		 * 验证号码 手机号 固话均可
		 * 
		 
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		
		Pattern pattern = Pattern.compile(expression);
		
		Matcher matcher = pattern.matcher(inputStr);
		
		if (matcher.matches() ) {
			isValid = true;
		}
		
		return isValid;
	}*/

    private void editDialog() {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        final EditText et = (EditText) view.findViewById(R.id.et);
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
        if (editIndex == 1) {
            //短号限制6位
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                tv.setText(R.string.input_cornet_1);
            } else {
                tv.setText(R.string.input_cornet);
            }
        } else {
            tv.setText(R.string.edit_phone);
        }

        et.setInputType(InputType.TYPE_CLASS_PHONE);
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editIndex == 1) {
                    str = et.getText().toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        EditRelation(SelectPosition, -1, null, null, "-1");
                        dialog.cancel();
                        return;
                    }
                    EditRelation(SelectPosition, -1, null, null, str);
                } else {
                    str = et.getText().toString().trim();
                    if (!Utils.isMobileNO(str)) {
                        CommUtil.showMsgShort(R.string.phone_num_error);
                        return;
                    }
                    EditRelation(SelectPosition, -1, null, str, null);
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

    private int webServicePosition = -1;

    private void askBindingDialog(final int position) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
        tv_content.setText(contactsList.get(position).getRelationShip() + getResources().getString(R.string.ask_bind));
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
                intent.putExtra("userId", contactsList.get(position).getObjectId());
                // intent.putExtra("serialNumber", serialNumber);
                startActivity(intent);
                webServicePosition = position;
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinkDeviceConfirm(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()), contactsList.get(position).getObjectId(), null, null, "0");
                webServicePosition = position;
                dialog.cancel();
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void delContactDialog(final int position) {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
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

        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DeleteContact(position);
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

    private Dialog dialog_a;
    private String photoName;

    private void selectPhotoDialog() {
        if (dialog_a != null)
            dialog_a.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_photo, null);
        dialog_a = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog_a.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog_a.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        Button btn_album, btn_camera, btn_cancel;
        btn_album = (Button) view.findViewById(R.id.btn_album);
        btn_camera = (Button) view.findViewById(R.id.btn_camera);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_album.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(" SelectPosition "+SelectPosition);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_MEDIA);
                dialog_a.cancel();
            }
        });
        btn_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoName = Contents.APPName + DateConversion.getTime1() + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), photoName)));
                startActivityForResult(intent, IMAGE_CAPTURE);
                dialog_a.cancel();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_a.cancel();
            }
        });
        // 设置显示位置
        dialog_a.onWindowAttributesChanged(wl);
        //dialog_a设置点击外围解散
        dialog_a.setCanceledOnTouchOutside(true);
        dialog_a.show();
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
         * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CUT_AVATAR);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private String photo;

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bmp_photo = extras.getParcelable("data");
            //iv_head.setImageBitmap(bmp_photo);
            photo = bytesToHexString(Bitmap2Bytes(getRoundCornerImage(bmp_photo, 38)));
            EditHeadImg(SelectPosition);
			/*ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// 将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
			bmp_photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] buffer = baos.toByteArray();

			// 将图片的字节流数据加密成base64字符输出
			photo = Base64.encodeToString(buffer, 0, buffer.length,
					Base64.DEFAULT);*/
        }
    }

    /**
     * 将图片的四角圆化
     *
     * @param bitmap      原图
     * @param roundPixels 圆滑率
     * @return
     */
    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        //创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        //创建画笔
        Paint paint = new Paint();
        //创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);

        //画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        //设置相交模式
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
        return baos.toByteArray();
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private void LinkDeviceConfirm(String deviceId, String userId, String photo, String name, String confirm) {
        WebService ws = new WebService(mContext, _LinkDeviceConfirm, true, "LinkDeviceConfirm");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
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

    private void EditHeadImg(int position) {
        if (position >= contactsList.size()) {
            return;
        }
        webServicePosition = position;
        WebService ws = new WebService(mContext, _EditHeadImg, true, "EditHeadImg");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceContactId", contactsList.get(position).getId()));
        property.add(new WebServiceProperty("headImg", photo));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void EditRelation(int position, int relat, String relatStr, String phoneNumber, String phoneShort) {
        if (position >= contactsList.size()) {
            return;
        }
        webServicePosition = position;
        WebService ws = new WebService(mContext, _EditRelation, true, "EditRelation");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        if (!TextUtils.isEmpty(relatStr)) {
            property.add(new WebServiceProperty("name", relatStr));
        }
        if (relat != -1) {
            property.add(new WebServiceProperty("photo", String.valueOf(relat)));
        }
        property.add(new WebServiceProperty("deviceContactId", contactsList.get(position).getId()));
        if (!TextUtils.isEmpty(phoneNumber)) {
            property.add(new WebServiceProperty("phoneNumber", phoneNumber));
        }
        if (!TextUtils.isEmpty(phoneShort)) {
            property.add(new WebServiceProperty("phoneShort", phoneShort));
        }
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private void DeleteContact(int position) {
        if (position >= contactsList.size()) {
            return;
        }
        webServicePosition = position;
        WebService ws = new WebService(mContext, _DeleteContact, true, "DeleteContact");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("deviceContactId", contactsList.get(position).getId()));
        ws.addWebServiceListener(mContext);
        ws.SyncGet(property);
    }

    private final int _EditRelation = 0;
    private final int _DeleteContact = 1;
    private final int _GetDeviceContact = 2;
    private final int _EditHeadImg = 3;
    private final int _LinkDeviceConfirm = 4;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            if (webServicePosition >= contactsList.size()) {
                return;
            }
            JSONObject jsonObject = new JSONObject(result);
            if (id == _EditRelation) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {

                    ContactModel mContactModel = contactsList.get(webServicePosition);
                    if (editIndex == 2) {

                        mContactModel.setRelationShip(relatStr);
                        mContactModel.setAvatar(String.valueOf(relat));
                        //mContactModel.setAvatarUrl("");
                    } else if (editIndex == 1) {
                        mContactModel.setCornet(str);
                    } else {
                        mContactModel.setPhone(str);
                    }
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.updateContact(mContactModel.getId(), mContactModel);
                    myAdapter.notifyDataSetChanged();
                } else {
                    //-1输入参数错误，0登录异常，6无权修改管理员信息
                    MToast.makeText(R.string.edit_fail).show();
                }
            } else if (id == _DeleteContact) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    ContactModel mContactModel = contactsList.get(webServicePosition);
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.deleteContact(mContactModel.getId());
                    contactsList.remove(webServicePosition);
                    myAdapter.notifyDataSetChanged();
                } else {
                    //-1输入参数错误，0登录异常，6无权修改管理员信息
                    MToast.makeText(R.string.del_fail).show();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }

            } else if (id == _GetDeviceContact) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    JSONArray arrContact = jsonObject.getJSONArray("ContactArr");
                    ContactDao mContactDao = new ContactDao(mContext);
                    mContactDao.deleteWatchContact(AppData.GetInstance(mContext).getSelectDeviceId());
                    for (int j = 0; j < arrContact.length(); j++) {
                        JSONObject item = arrContact.getJSONObject(j);
                        ContactModel mContactModel = new ContactModel();
                        mContactModel.setId(item.getString("DeviceContactId"));
                        mContactModel.setFromId(AppData.GetInstance(mContext).getSelectDeviceId());
                        mContactModel.setObjectId(item.getString("ObjectId"));
                        mContactModel.setRelationShip(item.getString("Relationship"));
                        mContactModel.setAvatar(item.getString("Photo"));
                        mContactModel.setAvatarUrl(item.getString("HeadImg"));
                        mContactModel.setPhone(item.getString("PhoneNumber"));
                        mContactModel.setCornet(item.getString("PhoneShort"));
                        mContactModel.setType(item.getString("Type"));
                        mContactDao.saveContact(mContactModel);
                    }
                    contactsList = mContactDao.getContactList(AppData.GetInstance(this).getSelectDeviceId());
                    AppContext.getInstance().setContactList(contactsList);
                    myAdapter.notifyDataSetChanged();
                    lv.onRefreshFinished();
                } else {
                    //-1输入参数错误，0登录异常，6无权修改管理员信息
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (_EditHeadImg == id) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    contactsList.get(webServicePosition).setAvatarUrl(jsonObject.getString("HeadImg"));
                    ContactDao mContactDao = new ContactDao(mContext);
                    ContentValues values = new ContentValues();
                    values.put(ContactDao.COLUMN_NAME_AVATARURL, contactsList.get(webServicePosition).getAvatarUrl());
                    mContactDao.updateContact(contactsList.get(webServicePosition).getId(), values);
                    myAdapter.notifyDataSetChanged();
                } else {
                    //-1输入参数错误，0登录异常，6无权修改管理员信息
                    MToast.makeText(R.string.edit_fail).show();
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }
            } else if (id == _LinkDeviceConfirm) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    //拒绝请求成功
                    ContactDao mContactDao = new ContactDao(this);
                    mContactDao.deleteUnconfirmed(contactsList.get(webServicePosition).getObjectId(), AppData.GetInstance(mContext).getSelectDeviceId());
					/*WebServiceUtils.GetDeviceContact(mContext,
							_GetDeviceContact, String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()), null, true, false);*/
                    AppContext.getInstance().setContactList(
                            mContactDao.getContactList(AppData
                                    .GetInstance(mContext)
                                    .getSelectDeviceId()));
                    contactsList = AppContext.getInstance().getContactList();
                    myAdapter.notifyDataSetChanged();
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

    int relat;
    String relatStr;
    private final int IMAGE_CAPTURE = 3;
    private final int IMAGE_MEDIA = 4;
    private final int CUT_AVATAR = 5;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;// 图片高宽度都为本来的二分之一，即图片大小为本来的大小的四分之一
        options.inTempStorage = new byte[5 * 1024];
        if (requestCode == IMAGE_CAPTURE) {// 相机
            //System.out.println("onActivityResult SelectPosition "+SelectPosition);
            ImageAdjust.Work(photoName);
            File temp = new File(Environment.getExternalStorageDirectory()
                    + "/" + photoName);
            if (temp.exists())
                startPhotoZoom(Uri.fromFile(temp));
        }
        if (data != null) {
            if (requestCode == IMAGE_MEDIA) {
                startPhotoZoom(data.getData());

            } else if (requestCode == CUT_AVATAR) {
                setPicToView(data);
            }
        }
        switch (requestCode) {
            case _EditRelation:
                if (resultCode == RESULT_OK) {
                    relat = data.getIntExtra("relation", -1) + 1;
                    relatStr = data.getStringExtra("relationStr");
                    EditRelation(SelectPosition, relat, relatStr, null, null);
                    editIndex = 2;
                }
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return contactsList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                v = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.address_book_item, parent, false);
                mViewHolder.iv_head = (ImageView) v.findViewById(R.id.iv_head);
                mViewHolder.tv_admin = (TextView) v.findViewById(R.id.tv_admin);
                mViewHolder.tv_me = (TextView) v.findViewById(R.id.tv_me);
                mViewHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
                mViewHolder.tv_phone = (TextView) v.findViewById(R.id.tv_phone);
                mViewHolder.tv_cornet = (TextView) v.findViewById(R.id.tv_cornet);
                mViewHolder.tv_cornet_a = (TextView) v.findViewById(R.id.tv_cornet_a);
                mViewHolder.iv_from = (ImageView) v.findViewById(R.id.iv_from);
                v.setTag(mViewHolder);
            } else {
                v = convertView;
                mViewHolder = (ViewHolder) v.getTag();
            }
            mViewHolder.iv_head.setImageResource(headID[6]);
            if (TextUtils.isEmpty(contactsList.get(position).getAvatarUrl())) {
                if ((Integer.valueOf(contactsList.get(position).getAvatar()) - 1) < 8 && (Integer.valueOf(contactsList.get(position).getAvatar()) - 1) >= 0)
                    mViewHolder.iv_head.setImageResource(headID[Integer.valueOf(contactsList.get(position).getAvatar()) - 1]);
                else
                    mViewHolder.iv_head.setImageResource(R.drawable.contacts_unconfirmed_small);
            } else {
                ImageLoader.getInstance().displayImage(
                        Contents.IMAGEVIEW_URL
                                + contactsList.get(position).getAvatarUrl(), mViewHolder.iv_head,
                        new AnimateFirstDisplayListener());
            }
            if (contactsList.get(position).getType().equals("2")) {
                mViewHolder.iv_from.setImageResource(R.drawable.app_type);
                if (mWatchModel.getUserId() == Integer.valueOf(contactsList.get(position).getObjectId())) {
                    mViewHolder.tv_admin.setBackgroundResource(R.drawable.bg_tv_purple);
                    mViewHolder.tv_admin.setText(R.string.guan);
                } else {
                    mViewHolder.tv_admin.setBackgroundResource(R.drawable.nulls);
                    mViewHolder.tv_admin.setText("");
                }
                if (AppData.GetInstance(mContext).getUserId() == Integer.valueOf(contactsList.get(position).getObjectId())) {
                    mViewHolder.tv_me.setBackgroundResource(R.drawable.bg_tv_purple);
                    mViewHolder.tv_me.setText(R.string.wo);
                } else {
                    mViewHolder.tv_me.setBackgroundResource(R.drawable.nulls);
                    mViewHolder.tv_me.setText("");
                }
            } else if (contactsList.get(position).getType().equals("3")) {
                mViewHolder.iv_from.setImageResource(R.drawable.watch_type);
            } else {
                mViewHolder.iv_from.setImageResource(R.drawable.nulls);
                mViewHolder.tv_me.setBackgroundResource(R.drawable.nulls);
                mViewHolder.tv_me.setText("");
                mViewHolder.tv_admin.setBackgroundResource(R.drawable.nulls);
                mViewHolder.tv_admin.setText("");
            }
            mViewHolder.tv_name.setText(TextUtil.MaxTextLengthChange(4, contactsList.get(position).getRelationShip()));
            mViewHolder.tv_phone.setText(contactsList.get(position).getPhone());
            if (!TextUtils.isEmpty(contactsList.get(position).getCornet())) {
                mViewHolder.tv_cornet_a.setText(R.string.cornet_mh);
            } else {
                if (!contactsList.get(position).getType().equals("4"))
                    mViewHolder.tv_cornet_a.setText(R.string.add_cornet);
                else
                    mViewHolder.tv_cornet_a.setText("");
            }
            mViewHolder.tv_cornet.setText(contactsList.get(position).getCornet());

            return v;
        }

    }

    class ViewHolder {
        ImageView iv_head;
        TextView tv_admin;
        TextView tv_me;
        TextView tv_name;
        TextView tv_phone;
        TextView tv_cornet;
        TextView tv_cornet_a;
        ImageView iv_from;
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactsList = AppContext.getInstance().getContactList();
        myAdapter.notifyDataSetChanged();
        AppContext.getInstance().setAddressBookShow(true);

        WebServiceUtils.GetDeviceContact(mContext, _GetDeviceContact, String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()), mContext, false, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getInstance().setAddressBookShow(false);
        unReceiver();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("SelectPosition", SelectPosition);
        savedInstanceState.putString("photoName", photoName);
        super.onSaveInstanceState(savedInstanceState); // 实现父类方法 放在最后
        // 防止拍照后无法返回当前activity
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
