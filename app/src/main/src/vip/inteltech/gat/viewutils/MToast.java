package vip.inteltech.gat.viewutils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppContext;

public class MToast {
	Context mContext;
	static Toast toast;
	TextView tv;
	ImageView iv;
	public MToast(){
		mContext = AppContext.getInstance().getApplicationContext();
		toast = new Toast(mContext);
		//设置toast显示的位置                
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 80);
		//设置弹出显示的时间
		toast.setDuration(2000);
		//设置布局
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.mtoast, null); 
		tv = (TextView) view.findViewById(R.id.tv);
		toast.setView(view);
	}
	public static Toast makeText(int resId) {
		toast = new Toast(AppContext.getInstance().getApplicationContext());
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 120);
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) AppContext.getInstance().getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View view = inflater.inflate(R.layout.mtoast, null);
		TextView textView = (TextView) view.findViewById(R.id.tv);
		textView.setText(resId);

		toast.setView(view);
		//toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(1500);

		return toast;
	}
	public static Toast makeText(String text) {
		toast = new Toast(AppContext.getInstance().getApplicationContext());
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 120);
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) AppContext.getInstance().getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View view = inflater.inflate(R.layout.mtoast, null);
		TextView textView = (TextView) view.findViewById(R.id.tv);
		textView.setText(text);

		toast.setView(view);
		//toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(1500);

		return toast;
	}
	public static Toast makeText(int resId, int duration) {
		toast = new Toast(AppContext.getInstance().getApplicationContext());
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 120);
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) AppContext.getInstance().getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View view = inflater.inflate(R.layout.mtoast, null);
		TextView textView = (TextView) view.findViewById(R.id.tv);
		textView.setText(resId);

		toast.setView(view);
		//toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(duration);

		return toast;
	}
	public static Toast makeText(String text, int duration) {
		toast = new Toast(AppContext.getInstance().getApplicationContext());
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 120);
		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) AppContext.getInstance().getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View view = inflater.inflate(R.layout.mtoast, null);
		TextView textView = (TextView) view.findViewById(R.id.tv);
		textView.setText(text);

		toast.setView(view);
		//toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(duration);

		return toast;
	}
	public void setText(String text){
		tv.setText(text);
	}
	public void setText(int resId){
		tv.setText(resId);
	}
	public void setDuration(int duration){
		toast.setDuration(duration);
	}
	public void show(){
		toast.show();
	}
}
