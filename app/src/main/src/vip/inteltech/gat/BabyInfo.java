package vip.inteltech.gat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yalantis.ucrop.UCrop;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.comm.Constants;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.ChangeBirthDialog;
import vip.inteltech.gat.viewutils.ChangeBirthDialog.OnBirthListener;
import vip.inteltech.gat.viewutils.ChooseDialog;
import vip.inteltech.gat.viewutils.ChooseDialog.OnListener;
import vip.inteltech.gat.viewutils.MToast;

public class BabyInfo extends BaseActivity implements OnClickListener, WebServiceListener {
    private BabyInfo mContext;
    private ImageView iv_head;
    private TextView tv_name, tv_watch_no, tv_cornet, tv_gender, tv_birthday, tv_grade, tv_schoolinfo, tv_homeinfo, tv_relationship, tv_number;
    private WatchModel mWatchModel;
    private String[] mGradeDatas;
    private boolean isHeadEdit = false, isNameEdit = false, isPhoneEdit = false, isCornetEdit = false, isGenderEdit = false, isBirthdayEdit = false, isGradeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.babyinfo);
        if (savedInstanceState != null) {
            photoName = savedInstanceState.getString("photoName");
        }
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.rl_watch_no).setOnClickListener(this);
        findViewById(R.id.rl_cornet).setOnClickListener(this);
        findViewById(R.id.rl_gender).setOnClickListener(this);
        findViewById(R.id.rl_birthday).setOnClickListener(this);
        findViewById(R.id.rl_grade).setOnClickListener(this);
        findViewById(R.id.rl_schoolinfo).setOnClickListener(this);
        findViewById(R.id.rl_homeinfo).setOnClickListener(this);
        findViewById(R.id.iv_edit_nick).setOnClickListener(this);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_head.setOnClickListener(this);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_watch_no = (TextView) findViewById(R.id.tv_watch_no);
        tv_cornet = (TextView) findViewById(R.id.tv_cornet);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_grade = (TextView) findViewById(R.id.tv_grade);
        tv_schoolinfo = (TextView) findViewById(R.id.tv_schoolinfo);
        tv_homeinfo = (TextView) findViewById(R.id.tv_homeinfo);
        tv_relationship = (TextView) findViewById(R.id.tv_relationship);
        tv_number = (TextView) findViewById(R.id.tv_number);

        tv_name.setOnClickListener(this);

        mGradeDatas = new String[]{
                mContext.getResources().getString(R.string.grade_a),
                mContext.getResources().getString(R.string.grade_b),
                mContext.getResources().getString(R.string.grade_c),
                mContext.getResources().getString(R.string.grade_d),
                mContext.getResources().getString(R.string.grade_e),
                mContext.getResources().getString(R.string.grade_f),
                mContext.getResources().getString(R.string.grade_g),
                mContext.getResources().getString(R.string.grade_h),
                mContext.getResources().getString(R.string.grade_i),
                mContext.getResources().getString(R.string.grade_j),
                mContext.getResources().getString(R.string.grade_k),
                mContext.getResources().getString(R.string.grade_l)};
        mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2") ||
                AppContext.getInstance().getmWatchModel().getCurrentFirmware().contains("D8_CH")) {
            findViewById(R.id.rl_gender).setVisibility(View.GONE);
            findViewById(R.id.rl_birthday).setVisibility(View.GONE);
            findViewById(R.id.rl_grade).setVisibility(View.GONE);
            findViewById(R.id.rl_schoolinfo).setVisibility(View.GONE);
            findViewById(R.id.rl_homeinfo).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_number.setText(R.string.locator_no);
        }

        WebServiceUtils.GetDeviceDetail(mContext, _GetDeviceDetail, String.valueOf(AppData.GetInstance(this).getSelectDeviceId()), mContext, false);
        initData();
    }

    private void initData() {
        try {
            mWatchModel = AppContext.getInstance().getmWatchModel();
            tv_name.setText(mWatchModel.getName());
            tv_watch_no.setText(mWatchModel.getPhone());
            tv_cornet.setText(mWatchModel.getCornet());
            tv_gender.setText(mWatchModel.getGender().equals("1") ? R.string.boy : R.string.girl);

            tv_birthday.setText(DateConversion.DateConversionUtilA(mWatchModel.getBirthday()));
            if (mWatchModel.getGrade() >= 0) {
                tv_grade.setText(mGradeDatas[mWatchModel.getGrade()]);
            }
            List<ContactModel> contactsList = AppContext.getInstance().getContactList();
            for (ContactModel mContatctModel : contactsList) {
                if (mContatctModel.getType().equals("2")) {
                    //System.out.println("tv_relationship："+ mContatctModel.getRelationShip() +"  " +mContatctModel.getObjectId());
                    if (mContatctModel.getObjectId().equals(String.valueOf(AppData.GetInstance(mContext).getUserId()))) {
                        tv_relationship.setText(mContatctModel.getRelationShip());
                        continue;
                    }
                }
            }
            ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL + mWatchModel.getAvatar(), iv_head, new AnimateFirstDisplayListener());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        /*tv_schoolinfo.setText(mWatchModel.getSchoolAddress());
        tv_homeinfo.setText(mWatchModel.getHomeAddress());*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                updateDevice(false);
                break;
            case R.id.rl_watch_no:
                editDialog(WATCHNO);
                break;
            case R.id.rl_cornet:
                editDialog(CORNET);
                break;
            case R.id.rl_gender:
                chooseGenderDialog();
                break;
            case R.id.rl_birthday:
                editBirthdayDialog();
                break;
            case R.id.rl_grade:
                chooseGradeDialog();
                break;
            case R.id.rl_schoolinfo:
                startActivity(new Intent(mContext, SchoolInfo.class));
                break;
            case R.id.rl_homeinfo:
                startActivity(new Intent(mContext, HomeInfo.class));
                break;
            case R.id.iv_head:
                selectPhotoDialog();
                break;
            case R.id.iv_edit_nick:
                editDialog(NAME);
                break;
            case R.id.tv_name:
                editDialog(NAME);
                break;
        }
    }

    private void updateDevice(boolean beLoad) {
        if (!TextUtils.isEmpty(photo)) {
            isHeadEdit = true;
        } else {
            isHeadEdit = false;
        }
        if (isHeadEdit || isNameEdit || isPhoneEdit || isCornetEdit || isGenderEdit || isGradeEdit || isBirthdayEdit) {
            Map<String, String> map = new HashMap<String, String>();
            WebService ws = new WebService(mContext, _UpdateDevice, beLoad, "UpdateDevice");
            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
            property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
            property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
            if (isNameEdit) {
                property.add(new WebServiceProperty("babyName", tv_name.getText().toString().trim()));
                map.put("babyName", tv_name.getText().toString().trim());
                mWatchModel.setName(tv_name.getText().toString().trim());
            }
            if (isHeadEdit) {
                property.add(new WebServiceProperty("photo", photo));
                map.put("photo", photo);
            }
            if (isPhoneEdit) {
                String mobile = tv_watch_no.getText().toString().trim();
                if (!Utils.isMobileNO(mobile)) {
                    CommUtil.showMsgShort(R.string.phone_num_error);
                    return;
                }
                property.add(new WebServiceProperty("phoneNumber", mobile));
                map.put("phoneNumber", mobile);
            }
            if (isCornetEdit) {
                String str = tv_cornet.getText().toString().trim();
                if (!TextUtils.isEmpty(str)) {
                    property.add(new WebServiceProperty("phoneCornet", str));
                    map.put("phoneCornet", str);
                } else {
                    property.add(new WebServiceProperty("phoneCornet", "-1"));
                    map.put("phoneCornet", "-1");
                }
            }
            if (isGenderEdit) {
                property.add(new WebServiceProperty("gender", tv_gender.getText().toString().trim().equals(getResources().getString(R.string.boy)) ? "1" : "2"));
                map.put("gender", tv_gender.getText().toString().trim().equals(getResources().getString(R.string.boy)) ? "1" : "2");
            }
            if (isBirthdayEdit) {
                property.add(new WebServiceProperty("birthday", birthday));
                map.put("birthday", birthday);
            }
            if (isGradeEdit) {
                property.add(new WebServiceProperty("grade", String.valueOf(Integer.valueOf(GradeIndex) + 1)));
                map.put("grade", String.valueOf(Integer.valueOf(GradeIndex) + 1));
            }
            //WebServiceUtils.updateDeviceForBabyInfo(mContext, _UpdateDevice, property, map, beLoad, isHeadEdit, isNameEdit, isPhoneEdit, isCornetEdit, isGenderEdit, isGradeEdit, isBirthdayEdit);
            ws.addWebServiceListener(mContext);
            ws.SyncGet(property);
        }
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private Dialog dialog;
    private String GradeIndex;
    private ChooseDialog mChangeGradeDialog;

    public void chooseGradeDialog() {
        if (mChangeGradeDialog != null)
            mChangeGradeDialog.cancel();
        mChangeGradeDialog = new ChooseDialog(this, mGradeDatas, R.string.choose_baby_grade);
        Window window = mChangeGradeDialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        if (!TextUtils.isEmpty(tv_grade.getText().toString().trim())) {
            mChangeGradeDialog.setChoose(tv_grade.getText().toString().trim());
        } else {
            mChangeGradeDialog.setChoose(mGradeDatas[0]);
        }
        mChangeGradeDialog.show();
        mChangeGradeDialog.setListener(new OnListener() {

            @Override
            public void onClick(String grade, int index) {

                if (!grade.equals(mGradeDatas[mWatchModel.getGrade()])) {
                    isGradeEdit = true;
                } else {
                    isGradeEdit = false;
                }
                GradeIndex = String.valueOf(index);
                tv_grade.setText(grade);
            }
        });
    }

    private String birthday;
    private ChangeBirthDialog mChangeBirthDialog;

    public void editBirthdayDialog() {
        if (mChangeBirthDialog != null)
            mChangeBirthDialog.cancel();
        mChangeBirthDialog = new ChangeBirthDialog(this);
        Window window = mChangeBirthDialog.getWindow();
        window.setWindowAnimations(R.style.slide_up_down);
        if (TextUtils.isEmpty(tv_birthday.getText().toString().trim())) {
            mChangeBirthDialog.setDate(2000, 01, 01);
        } else {
            int[] date = DateConversion.DateConversionUtilC(DateConversion.DateConversionUtilAA(tv_birthday.getText().toString().trim()));
            // 设置显示动画
            if (date.length != 3) {
                mChangeBirthDialog.setDate(2000, 01, 01);
            } else {
                mChangeBirthDialog.setDate(date[0], date[1], date[2]);
            }
        }
        //System.out.println(date[0]+" "+date[1]+" "+date[2]);
        mChangeBirthDialog.show();
        mChangeBirthDialog.setBirthdayListener(new OnBirthListener() {
            @Override
            public void onClick(int year, int month, int day) {
                //System.out.println(DateConversion.DateConversionUtilC(date));
                //System.out.println(year+" "+month+" "+day);
                birthday = DateConversion.DateConversionUtilD(year, month, day);
                /*System.out.println(birthday);
                System.out.println(mWatchModel.getBirthday());*/
                if (!birthday.equals(mWatchModel.getBirthday())) {
                    isBirthdayEdit = true;
                } else {
                    isBirthdayEdit = false;
                }
                String str = DateConversion.DateConversionUtilA(year, month, day);
                tv_birthday.setText(str);
            }
        });
    }

    private void chooseGenderDialog() {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_gender, null);
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
        final RadioButton rbtn_boy = (RadioButton) view.findViewById(R.id.rbtn_boy);
        final RadioButton rbtn_girl = (RadioButton) view.findViewById(R.id.rbtn_girl);
        if (tv_gender.getText().toString().equals(getResources().getString(R.string.boy))) {
            rbtn_boy.setChecked(true);
            rbtn_girl.setChecked(false);
        } else if (tv_gender.getText().toString().equals(getResources().getString(R.string.girl))) {
            rbtn_boy.setChecked(false);
            rbtn_girl.setChecked(true);
        } else {
            rbtn_boy.setChecked(false);
            rbtn_girl.setChecked(false);
        }
        rbtn_boy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtn_girl.setChecked(false);
                }
            }
        });
        rbtn_girl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbtn_boy.setChecked(false);
                }
            }
        });
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_gender.setText(rbtn_boy.isChecked() ? R.string.boy : R.string.girl);
                if (tv_gender.getText().toString().equals(getResources().getString(mWatchModel.getGender().equals("1") ? R.string.boy : R.string.girl))) {
                    isGenderEdit = false;
                } else {
                    isGenderEdit = true;
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

    private final int WATCHNO = 0, CORNET = 1, NAME = 2;

    private void editDialog(final int type) {
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
        final EditText et = (EditText) view.findViewById(R.id.et);
        switch (type) {
            case WATCHNO:
                if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    tv.setText(R.string.input_locator_no);
                } else {
                    tv.setText(R.string.input_watch_no);
                }
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                et.setText(tv_watch_no.getText().toString());
                break;
            case CORNET:
                if (!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
                    tv.setText(R.string.input_cornet_1);
                } else {
                    tv.setText(R.string.input_cornet);
                }
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                et.setText(tv_cornet.getText().toString());
                break;
            case NAME:
                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                tv.setText(R.string.input_baby_name);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setText(tv_name.getText().toString());
                break;
        }
        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String orgiVal = null;
                String editVal = null;
                switch (type) {
                    case WATCHNO:
                        orgiVal = tv_watch_no.getText().toString().trim();
                        editVal = et.getText().toString().trim();
                        if (!Utils.isMobileNO(editVal)) {
                            CommUtil.showMsgShort(R.string.phone_num_error);
                            return;
                        }
                        tv_watch_no.setText(editVal);
                        if (!orgiVal.equals(editVal)) {
                            isPhoneEdit = true;
                        } else {
                            isPhoneEdit = false;
                        }
                        break;
                    case CORNET:
                        orgiVal = tv_cornet.getText().toString().trim();
                        editVal = et.getText().toString().trim();
                        tv_cornet.setText(editVal);
                        if (!orgiVal.equals(editVal)) {
                            isCornetEdit = true;
                        } else {
                            isCornetEdit = false;
                        }
                        break;
                    case NAME:
                        orgiVal = tv_name.getText().toString().trim();
                        editVal = et.getText().toString().trim();
                        if (TextUtils.isEmpty(editVal)) {
                            return;
                        }
                        tv_name.setText(editVal);
                        if (!orgiVal.equals(editVal)) {
                            isNameEdit = true;
                        } else {
                            isNameEdit = false;
                        }
                        break;
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

    private String photoName;

    private void selectPhotoDialog() {
        if (dialog != null)
            dialog.cancel();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_photo, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
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

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_MEDIA);
                dialog.cancel();
            }
        });
        btn_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenCamera();
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

    private void tryToOpenCamera() {
        PermissionsUtil.requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                photoName = Utils.getAvailableStoragePath() + DateConversion.getTime1() + ".png";
                File file = new File(photoName);

                Uri photoURI = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoURI = FileProvider.getUriForFile(BabyInfo.this, Constants.FILE_PROVIDER_AUTHORITIES, file);
                } else {
                    photoURI = Uri.fromFile(file);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, IMAGE_CAPTURE);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                CommUtil.showMsgShort(R.string.permission_camera_denied);
            }
        });
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        try {
            String fileName = Utils.getAvailableStoragePath() + "files/" + System.currentTimeMillis() + ".jpg";
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            boolean res = file.createNewFile();
            if (res) {
                Uri target = Uri.fromFile(file);
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(getResources().getColor(R.color.theme_col));
                options.setStatusBarColor(getResources().getColor(R.color.blue_dark));
                options.setActiveWidgetColor(getResources().getColor(R.color.theme_col));
                UCrop.of(uri, target)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(500, 500)
                        .withOptions(options)
                        .start(BabyInfo.this);
            }
        } catch (Exception e) {
            Log.e(BabyInfo.class.getName(), "Crop pics failed!", e);
        }
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
         * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
         */
        /*Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        startActivityForResult(intent, CUT_AVATAR);*/
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private String photo;

    private void setPicToView(Uri uri) {
        if (uri != null) {
            Bitmap bmp_photo = BitmapFactory.decodeFile(uri.getPath());
            if (bmp_photo == null) {
                return;
            }
            iv_head.setImageBitmap(bmp_photo);
            isHeadEdit = true;
            photo = bytesToHexString(Bitmap2Bytes(bmp_photo));
            WebService ws = new WebService(mContext, _UpdateDevice, true, "UpdateDevice");
            List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
            property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
            property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
            property.add(new WebServiceProperty("photo", photo));
            ws.addWebServiceListener(mContext);
            ws.SyncGet(property);
            File file = new File(uri.getPath());
            if (file.exists()) {
                file.delete();
            }
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
			photo = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);*/
        }
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

    private final int IMAGE_CAPTURE = 0;
    private final int IMAGE_MEDIA = 1;
    private final int CUT_AVATAR = 2;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e(BabyInfo.class.getName(), "Crop pics failed!", cropError);
        } else if (resultCode != RESULT_OK) {
            return;
        }
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;// 图片高宽度都为本来的二分之一，即图片大小为本来的大小的四分之一
        //options.inTempStorage = new byte[5 * 1024];
        switch (requestCode) {
            case IMAGE_CAPTURE: {// 相机
                //ImageAdjust.Work(photoName);
                File file = new File(photoName);
                if (file.exists()) {
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        uri = Uri.fromFile(file);
                    } else {
                        uri = FileProvider.getUriForFile(this, Constants.FILE_PROVIDER_AUTHORITIES, file);
                    }
                    startPhotoZoom(uri);
                }
                break;
            }
            case IMAGE_MEDIA: {
                startPhotoZoom(data.getData());
                break;
            }
            case UCrop.REQUEST_CROP: {
                final Uri resultUri = UCrop.getOutput(data);
                setPicToView(resultUri);
                break;
            }
        }
        /*if (data != null) {
            if (requestCode == IMAGE_MEDIA) {
                startPhotoZoom(data.getData());
            } else if (requestCode == CUT_AVATAR) {
                setPicToView(data);
                if (StringUtils.isNotBlank(photoName)) {
                    File temp = new File(photoName);
                    if (temp.exists()) {
                        temp.delete();
                    }
                }
            }
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("photoName", photoName);
        super.onSaveInstanceState(savedInstanceState); // 实现父类方法 放在最后
        // 防止拍照后无法返回当前activity
    }

    private int _UpdateDevice = 0;
    private int _GetDeviceDetail = 1;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _UpdateDevice) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    MToast.makeText(jsonObject.getString("Message")).show();

                    if (isNameEdit) {
                        mWatchModel.setName(tv_name.getText().toString().trim());
                        isNameEdit = false;
                    }
                    if (isHeadEdit) {
//						MemoryCacheUtil.removeFromCache(Contents.IMAGEVIEW_URL+mWatchModel.getAvatar(), ImageLoader.getInstance().getMemoryCache());
//	                    DiscCacheUtil.removeFromCache(Contents.IMAGEVIEW_URL+mWatchModel.getAvatar(), ImageLoader.getInstance().getDiscCache());
                        mWatchModel.setAvatar(jsonObject.getString("Photo"));
                        photo = "";
                        isHeadEdit = false;
                        try {
                            ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL + mWatchModel.getAvatar(), iv_head, new AnimateFirstDisplayListener());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    if (isPhoneEdit) {
                        mWatchModel.setPhone(tv_watch_no.getText().toString().trim());
                        isPhoneEdit = false;
                    }
                    if (isCornetEdit) {
                        mWatchModel.setCornet(tv_cornet.getText().toString().trim());
                        isCornetEdit = false;
                    }
                    if (isGenderEdit) {
                        mWatchModel.setGender(tv_gender.getText().toString().trim().equals(getResources().getString(R.string.boy)) ? "1" : "2");
                        isGenderEdit = false;
                    }
                    if (isBirthdayEdit) {
                        mWatchModel.setBirthday(birthday);
                        isBirthdayEdit = false;
                    }
                    if (isGradeEdit) {
                        mWatchModel.setGrade(Integer.valueOf(GradeIndex));
                        isGradeEdit = false;
                    }
                    WatchDao mWatchDao = new WatchDao(this);
                    mWatchDao.updateWatch(AppData.GetInstance(mContext).getSelectDeviceId(), mWatchModel);
					/*finish();
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
                } else {
                    // -2系统异常
                    MToast.makeText(R.string.edit_fail).show();
                }/*else if (code == -2) {
					// -2系统异常
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -3) {
					// -3无权操作设备
					MToast.makeText(jsonObject.getString("Message")).show();
				} else if (code == -1){
					// -1设备参数错误
				} else if (code == 0){
					// 0登录异常
				}*/
				/*finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
            } else if (id == _GetDeviceDetail) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                    // 1成功
                    WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
                    mWatchModel.setUserId(jsonObject.getInt("UserId"));
                    mWatchModel.setModel(jsonObject.getString("DeviceModelID"));
                    mWatchModel.setName(jsonObject.getString("BabyName"));
                    mWatchModel.setAvatar(jsonObject.getString("Photo"));
                    mWatchModel.setPhone(jsonObject.getString("PhoneNumber"));
                    mWatchModel.setCornet(jsonObject.getString("PhoneCornet"));
                    mWatchModel.setGender(jsonObject.getString("Gender"));
                    mWatchModel.setBirthday(jsonObject.getString("Birthday"));
                    mWatchModel.setGrade(jsonObject.getInt("Grade"));
                    mWatchModel.setHomeAddress(jsonObject.getString("HomeAddress"));
                    mWatchModel.setHomeLat(jsonObject.getDouble("HomeLat"));
                    mWatchModel.setHomeLng(jsonObject.getDouble("HomeLng"));
                    mWatchModel.setSchoolAddress(jsonObject.getString("SchoolAddress"));
                    mWatchModel.setSchoolLat(jsonObject.getDouble("SchoolLat"));
                    mWatchModel.setSchoolLng(jsonObject.getDouble("SchoolLng"));
                    mWatchModel.setLastestTime(jsonObject.getString("LatestTime"));
                    mWatchModel.setSetVersionNO(jsonObject.getString("SetVersionNO"));
                    mWatchModel.setContactVersionNO(jsonObject.getString("ContactVersionNO"));
                    mWatchModel.setOperatorType(jsonObject.getString("OperatorType"));
                    mWatchModel.setSmsNumber(jsonObject.getString("SmsNumber"));
                    mWatchModel.setSmsBalanceKey(jsonObject.getString("SmsBalanceKey"));
                    mWatchModel.setSmsFlowKey(jsonObject.getString("SmsFlowKey"));
                    mWatchModel.setActiveDate(jsonObject.getString("ActiveDate"));
                    mWatchModel.setCreateTime(jsonObject.getString("CreateTime"));
                    mWatchModel.setBindNumber(jsonObject.getString("BindNumber"));
                    mWatchModel.setCurrentFirmware(jsonObject.getString("CurrentFirmware"));
                    mWatchModel.setFirmware(jsonObject.getString("Firmware"));
                    mWatchModel.setHireExpireDate(jsonObject.getString("HireExpireDate"));
                    mWatchModel.setUpdateTime(jsonObject.getString("UpdateTime"));
                    mWatchModel.setSerialNumber(jsonObject.getString("SerialNumber"));
                    mWatchModel.setPassword(jsonObject.getString("Password"));
                    mWatchModel.setIsGuard(jsonObject.getString("IsGuard").equals("1") ? true : false);
                    WatchDao mWatchDao = new WatchDao(mContext);
                    mWatchDao.saveWatch(mWatchModel);

                    initData();
                } else {
                    // -1输入参数错误，0登录异常，3设备不存在，-2系统异常，4已经关联
                    /*MToast.makeText(jsonObject.getString("Message")).show();*/
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_schoolinfo.setText(mWatchModel.getSchoolAddress());
        tv_homeinfo.setText(mWatchModel.getHomeAddress());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            updateDevice(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}