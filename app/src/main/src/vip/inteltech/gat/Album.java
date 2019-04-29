package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import org.apache.commons.lang3.StringUtils;

import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.AlbumDao;
import vip.inteltech.gat.model.AlbumModel;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.WebService.WebServiceListener;

public class Album extends BaseActivity implements OnItemLongClickListener, OnItemClickListener, OnGestureListener, WebServiceListener, OnClickListener {
    private Activity mContext;
    //Android sdk给我们提供了GestureDetector（Gesture：手势Detector：识别）类，
    // 通过这个类我们可以识别很多的手势，主要是通过他的onTouchEvent(event)方法完成了不同手势的识别。
    private GestureDetector detector;

    private ViewFlipper flipper;

    private GridView gv;
    private TextView tv_ceater, tv_address, tv_time, tv, tv_title;
    private LinearLayout ll;

    private ImageAdapter mImageAdapter;
    private List<AlbumModel> mAlbumList = new ArrayList<>();
    private List<ContactModel> contactsList;
    private WatchModel mWatchModel;
    private LayoutInflater inflater;
    private SparseArray<ManualPlayer> players = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.album);

        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.ll).setOnClickListener(this);
        detector = new GestureDetector(this);
        init();
        initData();
        mImageAdapter = new ImageAdapter(this, mAlbumList);


        gv.setAdapter(mImageAdapter);
        gv.setOnItemClickListener(this);
        gv.setOnItemLongClickListener(this);
        GetDevicePhoto();
        for (int i = 0; i < mAlbumList.size(); i++) {
            AlbumModel md = mAlbumList.get(i);
            String path = md.getPath();
            if (!StringUtils.startsWithIgnoreCase(path, "http://") && !StringUtils.startsWithIgnoreCase(path, "https://")) {
                path = Contents.IMAGEVIEW_URL + path;
            }

            if (path.endsWith(".jpg")) {
                ImageView iv = new ImageView(mContext);
                ImageLoader.getInstance().displayImage(path, iv, new AnimateFirstDisplayListener());
                flipper.addView(iv);
            } else if (path.endsWith(".mp4") || path.endsWith(".amr")) {
                if (inflater == null) {
                    inflater = LayoutInflater.from(this);
                }
                RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.layout_video, null);
                flipper.addView(view);
                VideoPlayerView playerView = view.findViewById(R.id.player);
                ManualPlayer videoPlayer = new ManualPlayer(this, playerView);
                players.put(i, videoPlayer);
                videoPlayer.setPlayUri(path);
                videoPlayer.setTitle(md.getSource());
                String thumb = CommUtil.isNotBlank(md.getThumb()) ? md.getThumb() : null;
                if (thumb != null && !StringUtils.startsWithIgnoreCase(thumb, "http://") && !StringUtils.startsWithIgnoreCase(thumb, "https://")) {
                    thumb = Contents.IMAGEVIEW_URL + thumb;
                }
                if (thumb != null) {
                    ImageLoader.getInstance().loadImage(thumb, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            videoPlayer.getVideoPlayerView().setPreviewImage(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                } else {
                    int drawableId = path.endsWith(".mp4") ? R.drawable.icon_video : R.drawable.icon_audio;
                    videoPlayer.getVideoPlayerView().setPreviewImage(((BitmapDrawable) getResources().getDrawable(drawableId)).getBitmap());
                }
            }
        }

        flipper.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        initReceiver();
        if (getIntent().getBooleanExtra("SHOW_DIALOG", false)) {
            makeSureDialog();
        }
    }

    private void init() {
        flipper = (ViewFlipper) findViewById(R.id.vf);
        ll = (LinearLayout) findViewById(R.id.ll);
        gv = (GridView) findViewById(R.id.gv);
        tv_ceater = (TextView) findViewById(R.id.tv_ceater);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv = (TextView) findViewById(R.id.tv);
        tv_title = (TextView) findViewById(R.id.textView_Title);
    }

    private AlbumDao mAlbumDao = new AlbumDao(mContext);

    private void initData() {
        mAlbumList = mAlbumDao.getAlbumList(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId());
        //================测试代码开始========================
        /*String[] ss = {
                "https://wx3.sinaimg.cn/mw690/6d0974fbly1fdkcm41smzj22kw3vc1kx.jpg",
                "https://wx3.sinaimg.cn/mw690/6d0974fbly1fdkcltl570j22kw3vc4qp.jpg",
                "https://wx3.sinaimg.cn/mw690/6d0974fbly1fdkclzc3mqj23vc2kwe7s.jpg",
                "https://wx1.sinaimg.cn/mw690/6d0974fbly1fdkclvrl7xj23ud2lk4qp.jpg",
                "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4",
                "http://96.ierge.cn/13/203/407369.mp3"
        };
        for (int i = 0; i < ss.length; i++) {
            AlbumModel album = new AlbumModel();
            album.setAddress("safsadfas_" + i);
            album.setCreateTime("sfdsafas");
            album.setDeviceID(111);
            album.setDevicePhotoId("fasfas_" + i);
            album.setPath(ss[i]);
            album.setSource("11111");
            if (ss[i].endsWith(".mp4")) {
                album.setThumb("https://wx1.sinaimg.cn/mw690/65df2e70ly1fptnp0lhncj21gb1ovx6p.jpg");
            }
            mAlbumList.add(album);
        }*/
        //================测试代码结束========================
        if (mAlbumList.size() != 0) {
            tv.setVisibility(View.GONE);
        }
        contactsList = AppContext.getInstance().getContactList();
        mWatchModel = AppContext.getInstance().getmWatchModel();
        setPhotoInfo(0);
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_title.setText(R.string.locator_album);
        }
    }

    private void initReceiver() {
        IntentFilter IntentFilter_a = new IntentFilter(
                Contents.getPhotoBrodcast);
        IntentFilter_a.setPriority(5);
        registerReceiver(getPhotoReceiver, IntentFilter_a);
    }

    private void unReceiver() {
        try {
            unregisterReceiver(getPhotoReceiver);
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver getPhotoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetDevicePhoto();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                makeSureDialog();
                break;
            case R.id.ll:
                if (mAlbumList.size() > 0) {
                    Intent intent_a = new Intent(mContext, AlbumLocation.class);
                    intent_a.putExtra("AlbumModel", mAlbumList.get(flipper.getDisplayedChild()));
                    startActivity(intent_a);
                }
                break;
        }
    }

    private Dialog dialog;

    private void makeSureDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_guard_choice, null);
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
        Button btn_photo, btn_video, btn_audio, btn_cancel;
        btn_photo = view.findViewById(R.id.btn_photo);
        btn_video = view.findViewById(R.id.btn_video);
        btn_audio = view.findViewById(R.id.btn_audio);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        if (!AppContext.getInstance().isSupportVideoSoundRecording()) {
            btn_video.setVisibility(View.GONE);
            btn_audio.setVisibility(View.GONE);
        }

        btn_photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isSupportVideoSoundRecording()) {
                    SendDeviceCommand("Guard", "3");
                } else {
                    SendDeviceCommand("TakePhoto", null);
                }
                dialog.cancel();
            }
        });
        btn_video.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SendDeviceCommand("Guard", "2");
                dialog.cancel();
            }
        });
        btn_audio.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SendDeviceCommand("Guard", "1");
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

    private void GetDevicePhoto() {
        WebService ws = new WebService(mContext, _GetDevicePhoto, true, "GetDevicePhoto");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        ws.addWebServiceListener(this);
        ws.SyncGet(property);
    }

    private void SendDeviceCommand(String command, String parameter) {
        WebService ws = new WebService(mContext, _SendDeviceCommand, true, "SendDeviceCommand");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
        property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
        property.add(new WebServiceProperty("commandType", command));
        if (CommUtil.isNotBlank(parameter)) {
            property.add(new WebServiceProperty("paramter", parameter));
        }
        ws.addWebServiceListener(this);
        ws.SyncGet(property);
    }

    private void GetAddress(double lat, double lng, final int position) {
        if (!TextUtils.isEmpty(mAlbumList.get(position).getAddress())) {
            tv_address.setText(TextUtil.MaxTextLengthChange(20, getResources().getString(R.string.location) + getResources().getString(R.string.mh) + mAlbumList.get(position).getAddress()));
            return;
        }
        WebService ws = new WebService(mContext, _GetAddress, true, "GetAddress");
        List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
        property.add(new WebServiceProperty("loginId", AppData
                .GetInstance(this).getLoginId()));
        property.add(new WebServiceProperty("mapType", "1"));
        property.add(new WebServiceProperty("lat", String.valueOf(lat)));
        property.add(new WebServiceProperty("lng", String.valueOf(lng)));
        ws.addWebServiceListener(new WebServiceListener() {

            @Override
            public void onWebServiceReceive(String method, int id, String result) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    int code = jsonObject.getIntValue("Code");
                    if (code == 1) {
                        String mAddress = jsonObject.getString("Province") +
                                jsonObject.getString("City") +
                                jsonObject.getString("District") +
                                jsonObject.getString("Road");
                        JSONArray array = jsonObject.getJSONArray("Nearby");
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject item = array.getJSONObject(i);
                            mAddress = mAddress + "," + item.getString("POI");
                        }
                        tv_address.setText(TextUtil.MaxTextLengthChange(20, getResources().getString(R.string.location) + mAddress));
                        mAlbumList.get(position).setAddress(mAddress);
                        ContentValues values = new ContentValues();
                        values.put(AlbumDao.COLUMN_NAME_ADDRESS, mAddress);
                        mAlbumDao.updateAlbum(mAlbumList.get(position).getDevicePhotoId(), values);
                    } else {
                        // 系统异常小于0，常规异常大于0
                        tv_address.setText(getResources().getString(R.string.location) + getResources().getString(R.string.mh) + getResources().getString(R.string.no_result));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ws.SyncGet(property);
    }

    private final int _GetDevicePhoto = 0;
    private final int _SendDeviceCommand = 1;
    private final int _GetAddress = 2;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (id == _GetDevicePhoto) {
                int code = jsonObject.getIntValue("Code");
                Log.v("kkk", "_GetDevicePhoto code = " + code);
                if (code == 1) {
                    JSONArray array = jsonObject.getJSONArray("List");
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        AlbumModel mAlbumModel = new AlbumModel();
                        mAlbumModel.setDevicePhotoId(item.getString("DevicePhotoId"));
                        mAlbumModel.setDeviceID(item.getIntValue("DeviceID"));
                        mAlbumModel.setUserID(AppData.GetInstance(mContext).getUserId());
                        mAlbumModel.setSource(item.getString("Source"));
                        mAlbumModel.setDeviceTime(item.getString("DeviceTime"));
                        mAlbumModel.setLatitude(item.getDouble("Latitude"));
                        mAlbumModel.setLongitude(item.getDouble("Longitude"));
                        mAlbumModel.setMark(item.getString("Mark"));
                        mAlbumModel.setPath(item.getString("Path"));
                        mAlbumModel.setThumb(item.getString("Thumb"));
                        mAlbumModel.setCreateTime(item.getString("CreateTime"));
                        mAlbumModel.setUpdateTime(item.getString("UpdateTime"));
                        mAlbumList.add(0, mAlbumModel);
                        mAlbumDao.saveAlbum(mAlbumModel);
                        ImageView iv = new ImageView(mContext);
                        ImageLoader.getInstance().displayImage(
                                Contents.IMAGEVIEW_URL + item.getString("Path"), iv,
                                new AnimateFirstDisplayListener());
                        flipper.addView(iv, 0);
                        mImageAdapter.notifyDataSetChanged();
                    }
                    flipper.setDisplayedChild(0);
                    setPhotoInfo(0);
                    if (mAlbumList.size() != 0) {
                        tv.setVisibility(View.GONE);
                    }
                } else {
                    //MToast.makeText(jsonObject.getString("Message")).show();
                }

            } else if (id == _GetAddress) {
                int code = jsonObject.getIntValue("Code");
                if (code == 1) {
                    String mAddress = jsonObject.getString("Province") +
                            jsonObject.getString("City") +
                            jsonObject.getString("District") +
                            jsonObject.getString("Road");
                    JSONArray array = jsonObject.getJSONArray("Nearby");
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        mAddress = mAddress + "," + item.getString("POI");
                    }
                    tv_address.setText(getResources().getString(R.string.location) + getResources().getString(R.string.mh) + mAddress);
                } else {
                    // 系统异常小于0，常规异常大于0
                    tv_address.setText(R.string.no_result);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPhotoInfo(int position) {
        if (mAlbumList.size() > 0) {
            if (TextUtils.isEmpty(mAlbumList.get(position).getSource())) {
                tv_ceater.setText(getResources().getString(R.string.photo_ceater) + getResources().getString(R.string.mh) + mWatchModel.getName());
            } else {
                for (int i = 0; i < contactsList.size(); i++) {
                    if (mAlbumList.get(position).getSource().equals(contactsList.get(i).getPhone())) {

                        tv_ceater.setText(getResources().getString(R.string.photo_ceater) + getResources().getString(R.string.mh) + contactsList.get(i).getRelationShip());
                        continue;
                    }

                }
            }
            if (mAlbumList.get(position).getLatitude() != 0 && mAlbumList.get(position).getLongitude() != 0) {
                GetAddress(mAlbumList.get(position).getLatitude(), mAlbumList.get(position).getLongitude(), position);
            }
            tv_time.setText(DateConversion.TimeChange(mAlbumList.get(position).getCreateTime(), ""));
        }
    }

    /**
     * Gallery监听事件
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (flipper.getDisplayedChild() != position) {
            startOrPausePlayer(false);
            flipper.setDisplayedChild(position); //设置ViewFlipper当前播放的子View
            setPhotoInfo(position);
            mImageAdapter.notifyDataSetChanged();
        }
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) {
            return true;
        }
        if (e1.getX() - e2.getX() > 180) {
            //向右滑动
            startOrPausePlayer(false);
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            flipper.showNext(); //显示下一个
            gv.setSelection(flipper.getDisplayedChild());
            setPhotoInfo(flipper.getDisplayedChild());
            mImageAdapter.notifyDataSetChanged();
            return true;
        } else if (e2.getX() - e1.getX() > 180) {
            //向左滑动
            startOrPausePlayer(false);
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            flipper.showPrevious(); //显示上一个
            gv.setSelection(flipper.getDisplayedChild());
            setPhotoInfo(flipper.getDisplayedChild());
            mImageAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private void startOrPausePlayer(boolean flag) {
        if (players == null || players.size() == 0) {
            return;
        }
        for (int i = 0; i < players.size(); i++) {
            ManualPlayer player = players.valueAt(i);
            if (player != null) {
                player.setStartOrPause(flag);
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        private List<AlbumModel> mAlbumList;

        public ImageAdapter(Context mContext, List<AlbumModel> mAlbumList) {
            super();
            this.mContext = mContext;
            this.mAlbumList = mAlbumList;
        }

        @Override
        public int getCount() {
            if (mAlbumList.size() == 0) {
                tv.setVisibility(View.VISIBLE);
                tv_address.setText("");
                tv_ceater.setText("");
                tv_time.setText("");
            }
            return mAlbumList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.album_item, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv);

            AlbumModel md = mAlbumList.get(position);
            String thumb = md.getThumb();
            if (md.getPath().endsWith(".jpg")) {
                thumb = md.getPath();
            }
            if (CommUtil.isNotBlank(thumb) && !StringUtils.startsWithIgnoreCase(thumb, "http://") && !StringUtils.startsWithIgnoreCase(thumb, "https://")) {
                thumb = Contents.IMAGEVIEW_URL + thumb;
            }
            if (CommUtil.isNotBlank(thumb)) {
                ImageLoader.getInstance().displayImage(thumb, iv, new AnimateFirstDisplayListener());
            } else if (md.getPath().endsWith(".mp4")) {
                iv.setImageResource(R.drawable.icon_video);
            } else if (md.getPath().endsWith(".amr")) {
                iv.setImageResource(R.drawable.icon_audio);
            }
            if (flipper.getDisplayedChild() == position) {
                convertView.setBackgroundColor(Color.rgb(186, 186, 186));
            } else {
                convertView.setBackgroundColor(Color.BLACK);
            }
            iv.setFocusable(false);
        	
           /* ImageView iv = new ImageView(mContext);
            ImageLoader.getInstance().displayImage(
            		Contents.IMAGEVIEW_URL + mAlbumList.get(position).getPath(), iv,
    				new AnimateFirstDisplayListener());
            iv.setAdjustViewBounds(true);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            Gallery.LayoutParams layoutParam = new Gallery.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParam.setMargins(5, 5, 5, 5);
            iv.setLayoutParams(layoutParam);*/

            return convertView;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.getInstance().setAlbumShow(true);
        for (int i = 0; i < players.size(); i++) {
            players.valueAt(i).onResume();
        }
    }

    @Override
    protected void onPause() {
        for (int i = 0; i < players.size(); i++) {
            players.valueAt(i).onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < players.size(); i++) {
            players.valueAt(i).onDestroy();
        }
        super.onDestroy();
        AppContext.getInstance().setAlbumShow(false);
        unReceiver();
    }

    @Override
    public void onBackPressed() {
        for (int i = 0; i < players.size(); i++) {
            players.valueAt(i).onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (int i = 0; i < players.size(); i++) {
            players.valueAt(i).onConfigurationChanged(newConfig);
        }
    }

    private void deleteDialog(final int position) {
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
        tv.setText(R.string.delete_photo_title);
        tv_content.setText(R.string.delete_photo);

        Button btn_OK, btn_cancel;
        btn_OK = (Button) view.findViewById(R.id.btn_OK);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_OK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                mImageAdapter.notifyDataSetChanged();
                flipper.removeViewAt(position);
                mAlbumDao.deleteAlbum(mAlbumList.get(position).getDevicePhotoId());
                mAlbumList.remove(position);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        deleteDialog(position);
        return false;
    }
}
