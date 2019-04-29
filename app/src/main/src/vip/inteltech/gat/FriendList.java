package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.FriendDao;
import vip.inteltech.gat.model.FriendModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MListView;
import vip.inteltech.gat.viewutils.MToast;
import vip.inteltech.gat.viewutils.MListView.OnRefreshListener;


public class FriendList extends BaseActivity implements OnClickListener, WebServiceListener{
	private FriendList mContext;
	private MListView lv;
	private List<FriendModel> friendsList;
	private static int SelectPosition;
	private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_list);
        if(savedInstanceState != null){
        	SelectPosition = savedInstanceState.getInt("SelectPosition");
        }
        mContext = this;

        friendsList = AppContext.getInstance().getFriendList();
        findViewById(R.id.btn_left).setOnClickListener(this);

        lv = (MListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(this);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SelectPosition = position-1;
				editAddressBookDialog(SelectPosition);
			}
		});
        lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void toRefresh() {
				getFriendList();
			}
		});

        initReceiver();
        getFriendList();
    }

	private void getFriendList() {
		WebService ws = new WebService(FriendList.this, _GetBabyFriendList, false, "GetBabyFriendList");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(FriendList.this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", Integer.toString(AppData.GetInstance(FriendList.this).getSelectDeviceId())));
		ws.addWebServiceListener(FriendList.this);
		ws.SyncGet(property);
	}
	
	private void updateFriendName(int id, String name) {
		WebService ws = new WebService(FriendList.this, _UpdateBabyFriendName, false, "UpdateBabyFriendName");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(FriendList.this).getLoginId()));
		property.add(new WebServiceProperty("DeviceFriendId", Integer.toString(id)));
		property.add(new WebServiceProperty("new_name", name));
		ws.addWebServiceListener(FriendList.this);
		ws.SyncGet(property);
	}
	
	private void deleteFriend(int id) {
		WebService ws = new WebService(FriendList.this, _DeleteBabyFriend, false, "DeleteBabyFriend");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(FriendList.this).getLoginId()));
		property.add(new WebServiceProperty("DeviceFriendId", Integer.toString(id)));
		ws.addWebServiceListener(FriendList.this);
		ws.SyncGet(property);
	}

	private void initReceiver() {
		IntentFilter IntentFilter_a = new IntentFilter(Contents.refreshFriendBrodcast);
		IntentFilter_a.setPriority(5);
		registerReceiver(refreshFriendReceiver, IntentFilter_a);
	}

	private void unReceiver() {
		try {
			unregisterReceiver(refreshFriendReceiver);
		} catch (Exception e) {
		}
	}

	private BroadcastReceiver refreshFriendReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getFriendList();
		}
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
	}

	private Dialog dialog;
	private void editAddressBookDialog(final int position){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_edit_friend_list, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv = (TextView) view.findViewById(R.id.tv);
		if (!TextUtils.isEmpty(friendsList.get(position).getName())){
			tv.setText(getResources().getString(R.string.friend)+"("+friendsList.get(position).getName()+")");
		} else if (!TextUtils.isEmpty(friendsList.get(position).getPhone())){
			tv.setText(getResources().getString(R.string.friend)+"("+friendsList.get(position).getPhone()+")");
		} else {
			tv.setText(getResources().getString(R.string.friend)+"("+getResources().getString(R.string.unknow_friend)+")");
		}

		LinearLayout ll_edit_name = (LinearLayout) view.findViewById(R.id.ll_edit_name);
		LinearLayout ll_del_friend = (LinearLayout) view.findViewById(R.id.ll_del_friend);
		ll_edit_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
				editIndex = 1;
				editDialog();
			}
		});
		ll_del_friend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
	private void editDialog(){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		TextView tv = (TextView) view.findViewById(R.id.tv);
		if (editIndex == 1) {
			tv.setText(R.string.edit_name);
		}
		final EditText et = (EditText) view.findViewById(R.id.et);
		Button btn_OK,btn_cancel;
		btn_OK = (Button) view.findViewById(R.id.btn_OK);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editIndex == 1) {
					String edit_name = et.getText().toString().trim();
					if (TextUtils.isEmpty(edit_name)) {
						MToast.makeText(R.string.invalid_content).show();
						return;
					} else {
						updateFriendName(friendsList.get(SelectPosition).getDeviceFriendId(), edit_name);
						friendsList.get(SelectPosition).setName(edit_name);
						FriendDao mFriendDao = new FriendDao(mContext);
						mFriendDao.saveFriend(friendsList.get(SelectPosition));
						myAdapter.notifyDataSetChanged();
						lv.onRefreshFinished();
					}
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

	private void delContactDialog(final int position){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_make_sure, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
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
				deleteFriend(friendsList.get(position).getDeviceFriendId());
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

	private final int _GetBabyFriendList = 0;
	private final int _UpdateBabyFriendName = 1;
	private final int _DeleteBabyFriend = 2;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		Log.v("kkk", result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (id == _GetBabyFriendList) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					JSONArray friendList = jsonObject.getJSONArray("friendList");
					FriendDao mFriendDao = new FriendDao(mContext);
					mFriendDao.deleteFriendList(AppData.GetInstance(mContext).getSelectDeviceId());
					for (int i = 0; i < friendList.length(); i++) {
						JSONObject item = friendList.getJSONObject(i);
						FriendModel mFriendModel = new FriendModel();
						mFriendModel.setId(AppData.GetInstance(this).getSelectDeviceId());
						mFriendModel.setDeviceFriendId(item.getInt("DeviceFriendId"));
						mFriendModel.setRelationShip(item.getString("Relationship"));
						mFriendModel.setFriendDeviceId(item.getInt("FriendDeviceId"));
						mFriendModel.setName(item.getString("Name"));
						mFriendModel.setPhone(item.getString("Phone"));
						mFriendDao.saveFriend(mFriendModel);
					}
					friendsList = mFriendDao.getWatchFriendList(AppData.GetInstance(this).getSelectDeviceId());
					AppContext.getInstance().setFriendList(friendsList);
					myAdapter.notifyDataSetChanged();
					lv.onRefreshFinished();
				} else {
					//-1输入参数错误，0登录异常，6无权修改管理员信息
					MToast.makeText(jsonObject.getString("Message")).show();
				}
			} else if (id == _UpdateBabyFriendName) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					MToast.makeText(R.string.edit_suc).show();
				} else {
					getFriendList();
					MToast.makeText(R.string.edit_fail).show();
				}
			} else if (id == _DeleteBabyFriend) {
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					getFriendList();
				}
				MToast.makeText(jsonObject.getString("Message")).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class MyAdapter extends BaseAdapter{
		private Context mContext;
		public MyAdapter(Context context) {
            mContext = context;
        }
		public int getCount() {
			return friendsList.size();
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
			if (convertView==null) {	
				mViewHolder = new ViewHolder();
				v =  LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false);
				mViewHolder.iv_head=(ImageView)v.findViewById(R.id.iv_head);
				mViewHolder.tv_name=(TextView)v.findViewById(R.id.tv_name);
				v.setTag(mViewHolder);
			} else {
				v=convertView;
				mViewHolder = (ViewHolder) v.getTag();
			}
			mViewHolder.iv_head.setImageResource(R.drawable.contacts_custom_small);
			if (!TextUtils.isEmpty(friendsList.get(position).getName())) {
				mViewHolder.tv_name.setText(friendsList.get(position).getName());
			} else if (!TextUtils.isEmpty(friendsList.get(position).getPhone())){
				mViewHolder.tv_name.setText(friendsList.get(position).getPhone());
			} else {
				mViewHolder.tv_name.setText(getResources().getString(R.string.unknow_friend));
			}

            return v;
		}
	}
	class ViewHolder{
		ImageView iv_head;
		TextView tv_name;
	}
	@Override
	protected void onResume() {
		super.onResume();
        friendsList = AppContext.getInstance().getFriendList();
		myAdapter.notifyDataSetChanged();
		AppContext.getInstance().setFriendListShow(true);
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppContext.getInstance().setFriendListShow(false);
		unReceiver();
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("SelectPosition", SelectPosition);
		super.onSaveInstanceState(savedInstanceState);
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