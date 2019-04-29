package vip.inteltech.gat.viewutils;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.viewutils.wheelview.adapters.AbstractWheelTextAdapter;
import vip.inteltech.gat.viewutils.wheelview.views.OnWheelChangedListener;
import vip.inteltech.gat.viewutils.wheelview.views.OnWheelScrollListener;
import vip.inteltech.gat.viewutils.wheelview.views.WheelView;

/**
 * 更改封面对话框
 * 
 * @author ywl
 *
 */
public class ChooseDialog extends Dialog implements android.view.View.OnClickListener {

	private WheelView wv;
	private View ly_myinfo_change;
	private View ly_myinfo_change_child;
	private Button btn_OK;
	private Button btn_cancel;

	private Context context;
	private String[] mDatas;

	private ArrayList<String> arr = new ArrayList<String>();
	private TextAdapter mAdapter;

	private String str;
	private OnListener onListener;

	private int maxsize = 24;
	private int minsize = 14;
	
	public TextView tv_title;
	private int i;

	public ChooseDialog(Context context, String[] strs,int i) {
		super(context, R.style.transparentFrameWindowStyle);
		this.context = context;
		mDatas = strs;
		this.i = i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_choose);
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(i);
		wv = (WheelView) findViewById(R.id.wv);
		ly_myinfo_change = findViewById(R.id.ly_myinfo_change);
		ly_myinfo_change_child = findViewById(R.id.ly_myinfo_change_child);
		btn_OK = (Button) findViewById(R.id.btn_OK);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);

		ly_myinfo_change.setOnClickListener(this);
		ly_myinfo_change_child.setOnClickListener(this);
		btn_OK.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		//str = context.getResources().getString(R.string.grade_l);
		/*mDatas = new String[]{context.getResources().getString(R.string.grade_a),
				context.getResources().getString(R.string.grade_b),
				context.getResources().getString(R.string.grade_c),
				context.getResources().getString(R.string.grade_d),
				context.getResources().getString(R.string.grade_e),
				context.getResources().getString(R.string.grade_f),
				context.getResources().getString(R.string.grade_g),
				context.getResources().getString(R.string.grade_h),
				context.getResources().getString(R.string.grade_i),
				context.getResources().getString(R.string.grade_j),
				context.getResources().getString(R.string.grade_k),
				context.getResources().getString(R.string.grade_l)};*/
		init();
		mAdapter = new TextAdapter(context, arr, getItem(str), maxsize, minsize);
		wv.setVisibleItems(5);
		wv.setViewAdapter(mAdapter);
		wv.setCurrentItem(getItem(str));


		wv.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mAdapter.getItemText(wheel.getCurrentItem());
				str = currentText;
				setTextviewSize(currentText, mAdapter);
			}
		});

		wv.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, mAdapter);
			}
		});

	}

	private class TextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected TextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
			super(context, R.layout.dialog_wheelview_item, NO_RESOURCE, currentItem, maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.tempValue);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index) + "";
		}
	}
	public void setTitle(int i){
		tv_title.setText(i);
	}
	/**
	 * 设置字体大小
	 * 
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText, TextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			} else {
				textvew.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			}
		}
	}

	public void setListener(OnListener onListener) {
		this.onListener = onListener;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_OK) {
			if (onListener != null) {
				onListener.onClick(str, getItem(str));
			}
		} else if (v == btn_cancel) {

		} else if (v == ly_myinfo_change_child) {
			return;
		} else {
			dismiss();
		}
		dismiss();
	}

	/**
	 * 回调接口
	 * 
	 * @author Administrator
	 *
	 */
	public interface OnListener {
		public void onClick(String cho, int index);
	}


	/**
	 * 年级
	 */
	public void init() {
		int length = mDatas.length;
		for (int i = 0; i < length; i++) {
			arr.add(mDatas[i]);
		}
	}

	public void setChoose(String choose) {
		str = choose;
	}

	/**
	 * 返回年级索引，没有就返回默认“其他”
	 * 
	 * @param province
	 * @return
	 */
	public int getItem(String grade) {
		int size = arr.size();
		int provinceIndex = 0;
		boolean noprovince = true;
		for (int i = 0; i < size; i++) {
			if (grade.equals(arr.get(i))) {
				noprovince = false;
				return provinceIndex;
			} else {
				provinceIndex++;
			}
		}
		if (noprovince) {
			str = arr.get(0);
			return 0;
		}
		return provinceIndex;
	}


}