package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.GeoFenceModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;



public class Fence extends BaseActivity implements OnClickListener,
	WebServiceListener {
	private Fence mContext;
	private ListView lv;
	private MyAdapter myAdapter;
	private RelativeLayout rl_bg;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fence);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        
        initData();
        lv = (ListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(mContext);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				editFenceDialog(position);
			}
		});
        GetGeoFenceList();
    }
    private List<GeoFenceModel> mGeoFenceList = new ArrayList<GeoFenceModel>();
    private void initData(){
    	/*mGeoFenceList = AppContext.getInstance().getmGeoFenceList();
    	if(mGeoFenceList.size() != 0){
    		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
    		rl_bg.setBackgroundColor(getResources().getColor(R.color.white));
    	}*/
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_right:
			startActivityForResult(new Intent(mContext, FenceEdit.class), SAVEFENCE);
			break;
		}
	}
	private void GetGeoFenceList(){
		WebService ws = new WebService(mContext, _GetGeoFenceList, true, "GetGeoFenceList");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private void DeleteGeoFence(int position){
		selectPosition = position;
		WebService ws = new WebService(mContext, _DeleteGeoFence, true, "DeleteGeoFence");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("geoFenceId",mGeoFenceList.get(position).getGeofenceID()));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private boolean isEditFenceMame = false, isEditEntry = false, isEditExit = false, isEditEnable = false;
	private String fenceName, entry, exit, enable;
	private void SaveGeoFence(int position, String fenceName, String entry, String exit, String enable) {
		selectPosition = position;
		isEditFenceMame = mGeoFenceList.get(position).getFenceName().equals(fenceName);
		isEditEntry = mGeoFenceList.get(position).getEntry().equals(entry);
		isEditExit = mGeoFenceList.get(position).getExit().equals(exit);
		isEditEnable = mGeoFenceList.get(position).getEnable().equals(enable);
		if(isEditFenceMame || isEditEntry || isEditExit || isEditEnable){
			WebService ws = new WebService(mContext, _SaveGeoFence, true, "SaveGeoFence");
			List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
			property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
			property.add(new WebServiceProperty("geoFenceId", mGeoFenceList.get(position).getGeofenceID()));
			if(fenceName == null){
				property.add(new WebServiceProperty("fenceName", mGeoFenceList.get(position).getFenceName()));
				this.fenceName = mGeoFenceList.get(position).getFenceName();
			}else{
				property.add(new WebServiceProperty("fenceName", fenceName));
				this.fenceName = fenceName;
			}
			if(entry == null){
				property.add(new WebServiceProperty("entry", mGeoFenceList.get(position).getEntry()));
				this.entry = mGeoFenceList.get(position).getEntry();
			}else{
				property.add(new WebServiceProperty("entry", entry));
				this.entry = entry;
			}
			if(exit == null){
				property.add(new WebServiceProperty("exit", mGeoFenceList.get(position).getExit()));
				this.exit = mGeoFenceList.get(position).getExit();
			}else{
				property.add(new WebServiceProperty("exit", exit));
				this.exit = exit;
			}
			property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
			String latAndLng = "";
			latAndLng = mGeoFenceList.get(position).getLat()
					+ "," + mGeoFenceList.get(position).getLng()
					+ "-" + mGeoFenceList.get(position).getRadius();
			property.add(new WebServiceProperty("latAndLng", latAndLng));
			if(enable == null){
				property.add(new WebServiceProperty("enable", mGeoFenceList.get(position).getEnable()));
				this.enable = mGeoFenceList.get(position).getEnable();
			}else{
				property.add(new WebServiceProperty("enable", enable));
				this.enable = enable;
			}
			ws.addWebServiceListener(mContext);
			ws.SyncGet(property);
		}
	}
	private final int _GetGeoFenceList = 0;
	private final int _DeleteGeoFence = 1;
	private final int _SaveGeoFence = 2;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(id == _GetGeoFenceList){
				int code = jsonObject.getInt("Code");
				if(code == 1){
					JSONArray array = jsonObject.getJSONArray("GeoFenceList");
					mGeoFenceList = new ArrayList<GeoFenceModel>();
					for(int i = 0;i < array.length(); i++){
						JSONObject item = array.getJSONObject(i);
						GeoFenceModel mGeoFenceModel = new GeoFenceModel();
						mGeoFenceModel.setDeviceId(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId()));
						mGeoFenceModel.setGeofenceID(item.getString("GeofenceID"));
						mGeoFenceModel.setFenceName(item.getString("FenceName"));
						mGeoFenceModel.setEntry(item.getString("Entry"));
						mGeoFenceModel.setExit(item.getString("Exit"));
						mGeoFenceModel.setCreateTime(item.getString("CreateTime"));
						mGeoFenceModel.setUpdateTime(item.getString("UpdateTime"));
						mGeoFenceModel.setEnable(item.getString("Enable"));
						mGeoFenceModel.setDescription(item.getString("Description"));
						mGeoFenceModel.setLat(item.getDouble("Lat"));
						mGeoFenceModel.setLng(item.getDouble("Lng"));
						mGeoFenceModel.setRadius(item.getInt("Radii"));
						mGeoFenceList.add(mGeoFenceModel);
					}
					AppContext.getInstance().setmGeoFenceList(mGeoFenceList);
					myAdapter.notifyDataSetChanged();
				}else if(code == 2){
				}else{
					//MToast.makeText(jsonObject.getString("Message")).show();
				}
			}else if(id == _DeleteGeoFence){
				int code = jsonObject.getInt("Code");
				if(code == 1){
					mGeoFenceList.remove(selectPosition);
					myAdapter.notifyDataSetChanged();
				}else{
					MToast.makeText(R.string.del_fail).show();
				}
			}else if(id == _SaveGeoFence){
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					mGeoFenceList.get(selectPosition).setFenceName(fenceName);
					mGeoFenceList.get(selectPosition).setEntry(entry);
					mGeoFenceList.get(selectPosition).setExit(exit);
					mGeoFenceList.get(selectPosition).setEnable(enable);
					
					MToast.makeText(R.string.save_suc).show();
					myAdapter.notifyDataSetChanged();
				} else {
					MToast.makeText(R.string.save_fail).show();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Dialog dialog_a;
	private int selectPosition;
	private void editFenceDialog(final int position){
		if(dialog_a != null)
			dialog_a.cancel();
		View view = mContext.getLayoutInflater().inflate(R.layout.dialog_fence_edit, null);
		dialog_a = new Dialog(mContext, R.style.transparentFrameWindowStyle);
		dialog_a.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog_a.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		//window.setWindowAnimations(R.style.slide_up_down);
		/*wl.x = getWindowManager().getDefaultDisplay().getWidth()/2;
		wl.y = getWindowManager().getDefaultDisplay().getHeight()/2;
		*/
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		RelativeLayout rl_top = (RelativeLayout) view.findViewById(R.id.rl_top);
		rl_top.setVisibility(View.VISIBLE);
		rl_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog_a.cancel();
				Intent intent = new Intent(mContext, FenceEdit.class);
				intent.putExtra("position", position);
				startActivityForResult(intent,SAVEFENCE);
			}
		});
		TextView tv = (TextView) view.findViewById(R.id.tv_title);
		final EditText et = (EditText) view.findViewById(R.id.et);
		et.setText(mGeoFenceList.get(position).getFenceName());
		final CheckBox cb_a, cb_b, cb_c;
		cb_a = (CheckBox) view.findViewById(R.id.cb_a);
		cb_b = (CheckBox) view.findViewById(R.id.cb_b);
		cb_c = (CheckBox) view.findViewById(R.id.cb_c);
		cb_a.setChecked(mGeoFenceList.get(position).getEntry().equals("1")?true:false);
		cb_b.setChecked(mGeoFenceList.get(position).getExit().equals("1")?true:false);
		cb_c.setChecked(mGeoFenceList.get(position).getEnable().equals("1")?true:false);
		Button btn_cancel, btn_OK, btn_middle;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_middle = (Button) view.findViewById(R.id.btn_middle);
		btn_middle.setVisibility(View.VISIBLE);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String fenceName = et.getText().toString().trim();
				if(TextUtils.isEmpty(fenceName)){
					return;
				}
				SaveGeoFence(position, fenceName, cb_a.isChecked()?"1":"0",  cb_b.isChecked()?"1":"0", cb_c.isChecked()?"1":"0");
				dialog_a.cancel();
			}
		});
		btn_middle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("middle");
				delDialog(position);
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
		// 设置点击外围解散
		dialog_a.setCanceledOnTouchOutside(true);
		dialog_a.show();
	}
	private Dialog dialog_b;
	private void delDialog(final int position){
		if(dialog_b != null)
			dialog_b.cancel();
		View view = mContext.getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
		dialog_b = new Dialog(mContext, R.style.transparentFrameWindowStyle);
		dialog_b.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog_b.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		//window.setWindowAnimations(R.style.slide_up_down);
		/*wl.x = getWindowManager().getDefaultDisplay().getWidth()/2;
		wl.y = getWindowManager().getDefaultDisplay().getHeight()/2;
		*/
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv, tv_content;
		tv = (TextView) view.findViewById(R.id.tv);
		tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv.setText(R.string.del_fence);
		tv_content.setText(R.string.sure_del);
		Button btn_cancel, btn_OK;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DeleteGeoFence(position);
				dialog_b.cancel();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog_b.cancel();
			}
		});
		// 设置显示位置
		dialog_b.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog_b.setCanceledOnTouchOutside(true);
		dialog_b.show();
	}
	private class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mGeoFenceList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v;
			ViewHolder mViewHolder;
			if (convertView == null) {
				mViewHolder = new ViewHolder();
				v = LayoutInflater.from(mContext).inflate(R.layout.fence_item, parent, false);
				mViewHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
				mViewHolder.tv_type = (TextView) v.findViewById(R.id.tv_type);
				v.setTag(mViewHolder);
			} else {
				v = convertView;
				mViewHolder = (ViewHolder) v.getTag();
			}
			mViewHolder.tv_name.setText(mGeoFenceList.get(position).getFenceName());
			if(mGeoFenceList.get(position).getEnable().equals("1")){
				if(mGeoFenceList.get(position).getEntry().equals("1") && mGeoFenceList.get(position).getExit().equals("1")){
					mViewHolder.tv_type.setText(R.string.in_out_fence);
				}else if(mGeoFenceList.get(position).getEntry().equals("1")){
					mViewHolder.tv_type.setText(R.string.in_fence);
				}else if(mGeoFenceList.get(position).getExit().equals("1")){
					mViewHolder.tv_type.setText(R.string.out_fence);
				}else if(!mGeoFenceList.get(position).getEntry().equals("1") && !mGeoFenceList.get(position).getExit().equals("1")){
					mViewHolder.tv_type.setText(R.string.close_fence);
				}
			}else{
				mViewHolder.tv_type.setText(R.string.close_fence);
			}
			return v;
		}

	}

	class ViewHolder {
		TextView tv_name, tv_type;
	}
	@Override
	public void onResume() {
		super.onResume();
		initData();
		myAdapter.notifyDataSetChanged();
	}
	private final int SAVEFENCE = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SAVEFENCE:
			if (resultCode == RESULT_OK) {
				GetGeoFenceList();
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
