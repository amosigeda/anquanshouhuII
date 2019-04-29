package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.chatutil.ChatMsgEntity;
import vip.inteltech.gat.db.ChatMsgDao;
import vip.inteltech.gat.db.ContactDao;
import vip.inteltech.gat.db.FriendDao;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.db.WatchSetDao;
import vip.inteltech.gat.db.WatchStateDao;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.AnimateFirstDisplayListener;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.Contents;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WatchList extends BaseActivity implements OnClickListener {
	private WatchList mContext;
	private List<WatchModel> mWatchList;
	private ListView lv;
	private MyAdapter myAdapter;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.watch_list);
		mContext = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.textView_Title);

		initData();
		lv = (ListView) findViewById(R.id.lv);
		myAdapter = new MyAdapter(this);
		lv.setAdapter(myAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				AppData.GetInstance(mContext).setSelectDeviceId(mWatchList.get(position).getId());
				myAdapter.notifyDataSetChanged();
				AppContext.getInstance().setmWatchModel(AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
				ContactDao mContactDao = new ContactDao(mContext);
				AppContext.getInstance().setContactList(mContactDao.getContactList(AppData.GetInstance(mContext).getSelectDeviceId()));

				WatchSetDao mWatchSetDao = new WatchSetDao(mContext);
				AppContext.getInstance().setSelectWatchSet(mWatchSetDao.getWatchSet(AppData.GetInstance(mContext).getSelectDeviceId()));

				FriendDao mFriendDao = new FriendDao(mContext);
				AppContext.getInstance().setFriendList(mFriendDao.getWatchFriendList(AppData.GetInstance(mContext).getSelectDeviceId()));

				WatchStateDao mWatchStateDao = new WatchStateDao(mContext);
				AppContext.getInstance().setmWatchStateModel(mWatchStateDao.getWatchState(AppData.GetInstance(mContext).getSelectDeviceId()));
				ChatMsgDao mChatMsgDao = new ChatMsgDao(mContext);
				List<ChatMsgEntity> allDataArrays = mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(),AppData.GetInstance(mContext).getUserId());
				if(allDataArrays.size() > Contents.CHATMSGINITIAL){
					AppContext.getInstance().setChatMsgList(allDataArrays.subList(allDataArrays.size()-Contents.CHATMSGINITIAL, allDataArrays.size()));
				}else{
					AppContext.getInstance().setChatMsgList(mChatMsgDao.getChatMsgLists(AppData.GetInstance(mContext).getSelectDeviceId(),AppData.GetInstance(mContext).getUserId()));
				}
				AppContext.getInstance().setmWatchModel(AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	private void initData() {
		WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
		if(mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")){
    		tv_title.setText(R.string.select_locator);
    	}
		mWatchList = new ArrayList<WatchModel>();
		/*
		 * WatchModel mWatchModel; for(int i = 33;i < 80;i++){ mWatchModel = new
		 * WatchModel(); mWatchModel.setId(i); mWatchModel.setName("kkkk");
		 * mWatchList.add(0, mWatchModel); }
		 */
		WatchDao mWatchDao = new WatchDao(this);
		AppContext.getInstance().setWatchMap(mWatchDao.getWatchMap());
		Map<String, WatchModel> WatchMap = AppContext.getInstance().getWatchMap();
		Iterator<Entry<String, WatchModel>> iterator = WatchMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, WatchModel> entry = iterator.next();
			mWatchList.add(entry.getValue());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		}
	}

	private class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return mWatchList.size();
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
				v = LayoutInflater.from(mContext).inflate(R.layout.watch_list_item, parent, false);
				mViewHolder.iv_head = (ImageView) v.findViewById(R.id.iv_head);
				mViewHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
				mViewHolder.iv_select = (ImageView) v.findViewById(R.id.iv_select);
				v.setTag(mViewHolder);
			} else {
				v = convertView;
				mViewHolder = (ViewHolder) v.getTag();
			}
			ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL+mWatchList.get(position).getAvatar(), mViewHolder.iv_head, new AnimateFirstDisplayListener()); 
			mViewHolder.tv_name.setText(mWatchList.get(position).getName());
			if (mWatchList.get(position).getId() == AppData.GetInstance(mContext).getSelectDeviceId()) {
				mViewHolder.iv_select.setVisibility(View.VISIBLE);
			} else {
				mViewHolder.iv_select.setVisibility(View.INVISIBLE);
			}
			return v;
		}
	}

	class ViewHolder {
		ImageView iv_head;
		TextView tv_name;
		ImageView iv_select;
	}
}