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
public class ChangeTimeDialog extends Dialog implements android.view.View.OnClickListener {

	private WheelView wv_hour;
	private WheelView wv_min;
	private View ly_myinfo_changegrade;
	private View ly_myinfo_changegrade_child;
	private Button btn_OK;
	private Button btn_cancel;
	public TextView tv_title;
	public int type;//1:morning   2:afternoon  3:all-day

	private Context context;

	private ArrayList<String> arrHour = new ArrayList<String>();
	private ArrayList<String> arrMin = new ArrayList<String>();
	private AddressTextAdapter hourAdapter, minAdapter;

	private String strHour, strMin;
	private OnTimeListener onTimeListener;

	private int maxsize = 24;
	private int minsize = 14;

	public ChangeTimeDialog(Context context,int i,int type) {
		super(context, R.style.transparentFrameWindowStyle);
		this.context = context;
		this.i = i;
		this.type = type;
	}
	public ChangeTimeDialog(Context context,int i) {
		super(context, R.style.transparentFrameWindowStyle);
		this.context = context;
		this.i = i;
		this.type = 3;
	}
	private int i;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_choose_time);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(i);
		wv_hour = (WheelView) findViewById(R.id.wv_hour);
		wv_min = (WheelView) findViewById(R.id.wv_min);
		ly_myinfo_changegrade = findViewById(R.id.ly_myinfo_changegrade);
		ly_myinfo_changegrade_child = findViewById(R.id.ly_myinfo_changegrade_child);
		btn_OK = (Button) findViewById(R.id.btn_OK);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);

		ly_myinfo_changegrade.setOnClickListener(this);
		ly_myinfo_changegrade_child.setOnClickListener(this);
		btn_OK.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		initTime();
		hourAdapter = new AddressTextAdapter(context, arrHour, getHourItem(strHour), maxsize, minsize);
		wv_hour.setVisibleItems(5);
		wv_hour.setViewAdapter(hourAdapter);
		wv_hour.setCurrentItem(getHourItem(strHour));


		wv_hour.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
				strHour = currentText;
				setTextviewSize(currentText, hourAdapter);
			}
		});

		wv_hour.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, hourAdapter);
			}
		});
		
		minAdapter = new AddressTextAdapter(context, arrMin, getMinItem(strMin), maxsize, minsize);
		wv_min.setVisibleItems(5);
		wv_min.setViewAdapter(minAdapter);
		wv_min.setCurrentItem(getMinItem(strMin));


		wv_min.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) minAdapter.getItemText(wheel.getCurrentItem());
				strMin = currentText;
				setTextviewSize(currentText, minAdapter);
			}
		});

		wv_min.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) minAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, minAdapter);
			}
		});
	}

	private class AddressTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
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

	/**
	 * 设置字体大小
	 * 
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
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

	public void setTimeListener(OnTimeListener onTimeListener) {
		this.onTimeListener = onTimeListener;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_OK) {
			if (onTimeListener != null) {
				onTimeListener.onClick(strHour,strMin);
			}
		} else if (v == btn_cancel) {

		} else if (v == ly_myinfo_changegrade_child) {
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
	public interface OnTimeListener {
		public void onClick(String hour, String min);
	}


	/**
	 * 年级
	 */
	public void initTime() {
		String str;
		if(type == 1)
		{
			for (int i = 0; i < 12; i++) {
				if(i<10){
					str = "0" + i;
				}else{
					str = String.valueOf(i);
				}
				arrHour.add(str);
			}
		}
		else if(type == 2)
		{
			for (int i = 12; i < 24; i++) {
				if(i<10){
					str = "0" + i;
				}else{
					str = String.valueOf(i);
				}
				arrHour.add(str);
			}			
		}
		else {
			for (int i = 0; i < 24; i++) {
				if(i<10){
					str = "0" + i;
				}else{
					str = String.valueOf(i);
				}
				arrHour.add(str);
			}	
		}
		for (int i = 0; i < 60; i++) {
			if(i<10){
				str = "0" + i;
			}else{
				str = String.valueOf(i);
			}
			arrMin.add(str);
		}
	}

	public void setHour(String hour) {
		strHour = hour;
	}
	public void setMin(String min) {
		strMin = min;
	}
	/**
	 * 返回分钟索引，没有就返回默认“00”
	 * 
	 * @param province
	 * @return
	 */
	public int getMinItem(String min) {
		int size = arrMin.size();
		int provinceIndex = 0;
		boolean noprovince = true;
		for (int i = 0; i < size; i++) {
			if (min.equals(arrMin.get(i))) {
				noprovince = false;
				return provinceIndex;
			} else {
				provinceIndex++;
			}
		}
		if (noprovince) {
			strMin = "00";
			return 1;
		}
		return provinceIndex;
	}
	/**
	 * 返回小时索引，没有就返回默认“00”
	 * 
	 * @param province
	 * @return
	 */
	public int getHourItem(String hour) {
		int size = arrHour.size();
		int provinceIndex = 0;
		boolean noprovince = true;
		for (int i = 0; i < size; i++) {
			if (hour.equals(arrHour.get(i))) {
				noprovince = false;
				return provinceIndex;
			} else {
				provinceIndex++;
			}
		}
		if (noprovince) {
			strHour = "00";
			return 1;
		}
		return provinceIndex;
	}

}