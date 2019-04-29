package vip.inteltech.gat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.SMSDao;
import vip.inteltech.gat.model.SMSModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;
import com.nostra13.universalimageloader.core.ImageLoader;


public class WatchFare extends BaseActivity implements OnClickListener, WebServiceListener{
	private WatchFare mContext;
	private ListView lv;
	private Button btn_right;
	private TextView tv_title;
	private List<SMSModel> mSMSList;
	private WatchModel mWatchModel;
	private MyAdapter myAdapter;
	private boolean isEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.watch_fare);
        mContext = this;
        SMSDao mSMSDao = new SMSDao(mContext);
        
        mSMSList = mSMSDao.getSMSList(AppData.GetInstance(mContext).getSelectDeviceId(), AppData.GetInstance(mContext).getUserId());
        mWatchModel = AppContext.getInstance().getmWatchModel();
        
        findViewById(R.id.btn_left).setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        findViewById(R.id.ll_flow).setOnClickListener(this);
        findViewById(R.id.ll_fare).setOnClickListener(this);
        findViewById(R.id.ll_edit).setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.textView_Title);
        if(!TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
    		tv_title.setText(R.string.locator_fare);
    	}
        //Text();
        myAdapter = new MyAdapter(mContext);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(myAdapter);
        lv.setSelection(lv.getCount() - 1);
        initReceiver();
        GetDeviceSMS();
    }
    private void Text(){
    	mSMSList = new ArrayList<SMSModel>();
    	SMSModel mSMSModel = new SMSModel();
    	mSMSModel.setDeviceSMSID("2");
    	mSMSModel.setDeviceID("2");
    	mSMSModel.setType("1");
    	mSMSModel.setState("1");
    	mSMSModel.setSms("sssssssssssssssssssssssssssssssssssssssddddddddddddd");
    	mSMSModel.setCreateTime("2015/09/03 12:12:12");
    	mSMSModel.setUpdateTime("2015/09/03 12:12:12");
    	mSMSList.add(mSMSModel);
    	mSMSModel = new SMSModel();
    	mSMSModel.setDeviceSMSID("2");
    	mSMSModel.setDeviceID("2");
    	mSMSModel.setType("2");
    	mSMSModel.setState("1");
    	mSMSModel.setSms("ssssaaaaaaaaasssssssssssssssssssssssssssssssssfffffffffssddddddddddddd");
    	mSMSModel.setCreateTime("2015/09/03 12:12:12");
    	mSMSModel.setUpdateTime("2015/09/03 12:12:12");
    	mSMSList.add(mSMSModel);
    	mSMSModel = new SMSModel();
    	mSMSModel.setDeviceSMSID("2");
    	mSMSModel.setDeviceID("2");
    	mSMSModel.setType("2");
    	mSMSModel.setState("1");
    	mSMSModel.setSms("sssssssssssssssssssssssssssssssssssssssdddddddddsdaddddd");
    	mSMSModel.setCreateTime("2015/09/03 12:12:12");
    	mSMSModel.setUpdateTime("2015/09/03 12:12:12");
    	mSMSList.add(mSMSModel);
    	mSMSModel = new SMSModel();
    	mSMSModel.setDeviceSMSID("2");
    	mSMSModel.setDeviceID("2");
    	mSMSModel.setType("1");
    	mSMSModel.setState("1");
    	mSMSModel.setSms("ssssssssssssssssssaaaaaaaaaasssssssssssssssssssssddddddddddddd");
    	mSMSModel.setCreateTime("2015/09/03 12:12:12");
    	mSMSModel.setUpdateTime("2015/09/03 12:12:12");
    	mSMSList.add(mSMSModel);
    }
    private void initReceiver(){
		IntentFilter IntentFilter_a = new IntentFilter(
				Contents.getSMSBrodcast);
		//IntentFilter_a.setPriority(5);
		registerReceiver(getSMSReceiver, IntentFilter_a);
	}
    private void unReceiver(){
		try {
			unregisterReceiver(getSMSReceiver);
		} catch (Exception e) {
		}
	}
    private BroadcastReceiver getSMSReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			GetDeviceSMS();
		}
	};
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_right:
			if(isEdit){
				isEdit = false;
				btn_right.setText(R.string.edit);
			}else{
				isEdit = true;
				btn_right.setText(R.string.finish);
			}
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.ll_flow:
			if(TextUtils.isEmpty(mWatchModel.getSmsNumber())){
				MToast.makeText(R.string.edit_operator_number).show();
				startActivityForResult(new Intent(mContext,WatchFareEdit.class),WatchFareEdit);
				return;
			}else if(TextUtils.isEmpty(mWatchModel.getSmsFlowKey())){
				MToast.makeText(R.string.edit_FlowKey).show();
				startActivityForResult(new Intent(mContext,WatchFareEdit.class),WatchFareEdit);
				return;
			}
			SaveDeviceSMS(mWatchModel.getSmsFlowKey());
			EditContent(1);
			break;
		case R.id.ll_fare:
			if(TextUtils.isEmpty(mWatchModel.getSmsNumber())){
				MToast.makeText(R.string.edit_operator_number).show();
				startActivityForResult(new Intent(mContext,WatchFareEdit.class),WatchFareEdit);
				return;
			}else if(TextUtils.isEmpty(mWatchModel.getSmsBalanceKey())){
				MToast.makeText(R.string.edit_BalanceKey).show();
				startActivityForResult(new Intent(mContext,WatchFareEdit.class),WatchFareEdit);
				return;
			}
			SaveDeviceSMS(mWatchModel.getSmsBalanceKey());
			EditContent(0);
			break;
		case R.id.ll_edit:
			startActivityForResult(new Intent(mContext,WatchFareEdit.class),WatchFareEdit);
			break;
		}
	}
	private void EditContent(int index){
		String content;
		WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
		if(mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
			if(index == 0)
				content = getResources().getString(R.string.SMS_content_fare_1);
			else
				content = getResources().getString(R.string.SMS_content_flow_1);
		}else{
			if(index == 0)
				content = getResources().getString(R.string.SMS_content_fare);
			else
				content = getResources().getString(R.string.SMS_content_flow);
		}
		content = content.replace("$phone$", mWatchModel.getPhone());
		SMSModel mSMSModel = new SMSModel();
    	mSMSModel.setDeviceID(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
    	mSMSModel.setUserID(String.valueOf(AppData.GetInstance(mContext).getUserId()));
    	mSMSModel.setType("1");
    	mSMSModel.setState("2");
    	mSMSModel.setSms(content);
    	mSMSModel.setCreateTime(DateConversion.converToUTCTime(getDate()));
    	mSMSModel.setUpdateTime(DateConversion.converToUTCTime(getDate()));
    	mSMSList.add(mSMSModel);
    	SMSDao mSMSDao = new SMSDao(mContext);
    	mSMSDao.saveSMS(mSMSModel);
    	myAdapter.notifyDataSetChanged();
    	lv.setSelection(lv.getCount() - 1);
	}
	private String getDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String dateStr = sdf.format(date);
		return dateStr;
	}
	private void GetDeviceSMS(){
		WebService ws = new WebService(mContext, _GetDeviceSMS,true, "GetDeviceSMS");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private void SaveDeviceSMS(String content){
		WebService ws = new WebService(mContext, _SaveDeviceSMS,true, "SaveDeviceSMS");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		//property.add(new WebServiceProperty("type", "1"));
		property.add(new WebServiceProperty("phone", mWatchModel.getSmsNumber()));
		property.add(new WebServiceProperty("content", content));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _SaveDeviceSMS = 0;
	private final int _GetDeviceSMS = 1;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _SaveDeviceSMS) {
				int code = jsonObject.getInt("Code");
				if(code == 1){
					GetDeviceSMS();
				} else {
					//-1输入参数错误，0登录异常，6无权修改管理员信息
					//MToast.makeText(jsonObject.getString("Message")).show();
				} 
			}else if(id == _GetDeviceSMS){
				int code = jsonObject.getInt("Code");
				if(code == 1){
					JSONArray arrs = jsonObject.getJSONArray("SMSList");
					int j;
					for(j = 0; j < arrs.length(); j++){
						JSONObject items = arrs.getJSONObject(j);
						SMSModel mSMSModel = new SMSModel();
						mSMSModel.setDeviceSMSID(items.getString("DeviceSMSID"));
						mSMSModel.setDeviceID(items.getString("DeviceID"));
				    	mSMSModel.setUserID(String.valueOf(AppData.GetInstance(mContext).getUserId()));
				    	mSMSModel.setType(items.getString("Type"));
				    	mSMSModel.setPhone(items.getString("Phone"));
				    	mSMSModel.setSms(items.getString("SMS"));
				    	mSMSModel.setCreateTime(items.getString("CreateTime"));
				    	mSMSList.add(mSMSModel);
				    	SMSDao mSMSDao = new SMSDao(mContext);
				    	mSMSDao.saveSMS(mSMSModel);
				    	myAdapter.notifyDataSetChanged();
				    	lv.setSelection(lv.getCount() - 1);
					}
				} else if (code == 2){
					
				}else {
					//-1输入参数错误，0登录异常，6无权修改管理员信息
					//MToast.makeText(jsonObject.getString("Message")).show();
				} 
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mSMSList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v;
			ViewHolder mViewHolder;
			if(mSMSList.get(position).getType().equals("2")){
				v = LayoutInflater.from(mContext).inflate(
						R.layout.watch_fare_item_left, parent, false);
				mViewHolder = new ViewHolder();
				mViewHolder.iv_head = (ImageView) v.findViewById(R.id.iv_head);
				mViewHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
				mViewHolder.tv_time = (TextView) v.findViewById(R.id.tv_time);
				mViewHolder.tv_content = (TextView) v.findViewById(R.id.tv_content);
				mViewHolder.btn_del = (Button) v.findViewById(R.id.btn_del);
				
				if(isEdit){
					mViewHolder.btn_del.setVisibility(View.VISIBLE);
				}else{
					mViewHolder.btn_del.setVisibility(View.GONE);
				}
				mViewHolder.btn_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SMSDao mSMSDao = new SMSDao(mContext);
						mSMSDao.deleteSMS(String.valueOf(mSMSList.get(position).getSort()));
						mSMSList.remove(position);
						myAdapter.notifyDataSetChanged();
					}
				});
				ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL+mWatchModel.getAvatar(), mViewHolder.iv_head, new AnimateFirstDisplayListener()); 
				mViewHolder.tv_name.setText(mWatchModel.getName());
				if(position > 0){
					String str = DateConversion.TimeChange(mSMSList.get(position).getCreateTime(), mSMSList.get(position-1).getCreateTime());
					mViewHolder.tv_time.setText(str);
					if(mViewHolder.tv_time.getText().toString().trim().equals("0")){
						mViewHolder.tv_time.setVisibility(View.GONE);
					}else{
						mViewHolder.tv_time.setVisibility(View.VISIBLE);
					}
				}else{
					mViewHolder.tv_time.setText(DateConversion.TimeChange(mSMSList.get(position).getCreateTime(), ""));
				}
				mViewHolder.tv_content.setText(mSMSList.get(position).getSms());
			}else{
				v = LayoutInflater.from(mContext).inflate(
						R.layout.watch_fare_item_right, parent, false);
				mViewHolder = new ViewHolder();
				mViewHolder.tv_time = (TextView) v.findViewById(R.id.tv_time);
				mViewHolder.tv_content = (TextView) v.findViewById(R.id.tv_content);
				mViewHolder.btn_del = (Button) v.findViewById(R.id.btn_del);
				
				if(isEdit){
					mViewHolder.btn_del.setVisibility(View.VISIBLE);
					//System.out.println("isEdit");
				}else{
					mViewHolder.btn_del.setVisibility(View.GONE);
				}
				mViewHolder.btn_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SMSDao mSMSDao = new SMSDao(mContext);
						mSMSDao.deleteSMS(String.valueOf(mSMSList.get(position).getSort()));
						mSMSList.remove(position);
						myAdapter.notifyDataSetChanged();
					}
				});
				if(position > 0){
					String str = DateConversion.TimeChange(mSMSList.get(position).getCreateTime(), mSMSList.get(position-1).getCreateTime());
					mViewHolder.tv_time.setText(str);
					if(mViewHolder.tv_time.getText().toString().trim().equals("0")){
						mViewHolder.tv_time.setVisibility(View.INVISIBLE);
					}else{
						mViewHolder.tv_time.setVisibility(View.VISIBLE);
					}
				}else{
					mViewHolder.tv_time.setText(DateConversion.TimeChange(mSMSList.get(position).getCreateTime(), ""));
				}
				mViewHolder.tv_content.setText(mSMSList.get(position).getSms());
			}
			return v;
		}
	}

	class ViewHolder {
		ImageView iv_head;
		TextView tv_name;
		TextView tv_time;
		TextView tv_content;
		Button btn_del;
	}
	@Override
	protected void onResume() {
		super.onResume();
		AppContext.getInstance().setSMSShow(true);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unReceiver();
		AppContext.getInstance().setSMSShow(false);
	}
	private final int WatchFareEdit = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case WatchFareEdit:
			if (resultCode == RESULT_OK) {
				 mWatchModel = AppContext.getInstance().getmWatchModel();
			}
			break;
		default:
			break;

		}
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
		 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
			 finish();
			 overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		     return true;   
		 }
		    return super.onKeyDown(keyCode, event);
    }
}
