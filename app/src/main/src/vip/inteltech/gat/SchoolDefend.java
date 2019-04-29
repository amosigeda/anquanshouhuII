package vip.inteltech.gat;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.WatchDao;
import vip.inteltech.gat.model.DefendInfoModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchSetModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;


public class SchoolDefend extends BaseActivity implements OnClickListener, WebServiceListener, OnGeocodeSearchListener{
	private SchoolDefend mContext;
	private TextView tv_address, tv_class_a, tv_class_b, tv_last;
	private TextView tv_time_a, tv_time_b, tv_time_c, tv_time_d, tv_a, tv_b, tv_c, tv_d, tv_msg_a, tv_msg_b, tv_msg_c, tv_msg_d,
					tv_msgs_a,tv_msgs_b,tv_msgs_c,tv_msgs_d;
	private ImageView iv_a, iv_b, iv_c, iv_d, iv_more_a, iv_more_b, iv_more_c, iv_more_d, iv_state_a, iv_state_b, iv_state_c, iv_state_d;
	private CheckBox cb_more_a, cb_more_b, cb_more_c, cb_more_d;
	private RelativeLayout rl_top_a, rl_top_b, rl_top_c, rl_top_d, rl_bottom_a, rl_bottom_b, rl_bottom_c, rl_bottom_d;
	private ListView lv;
	private DefendInfoModel mDefendInfoModel;
	private Button btn_switch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.school_defend);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_class_a = (TextView) findViewById(R.id.tv_class_a);
        tv_class_b = (TextView) findViewById(R.id.tv_class_b);
        tv_last = (TextView) findViewById(R.id.tv_last);
        
        tv_time_a = (TextView) findViewById(R.id.tv_time_a);
        tv_time_b = (TextView) findViewById(R.id.tv_time_b);
        tv_time_c = (TextView) findViewById(R.id.tv_time_c);
        tv_time_d = (TextView) findViewById(R.id.tv_time_d);
        tv_a = (TextView) findViewById(R.id.tv_a);
        tv_b = (TextView) findViewById(R.id.tv_b);
        tv_c = (TextView) findViewById(R.id.tv_c);
        tv_d = (TextView) findViewById(R.id.tv_d);
        tv_msg_a = (TextView) findViewById(R.id.tv_msg_a);
        tv_msg_b = (TextView) findViewById(R.id.tv_msg_b);
        tv_msg_c = (TextView) findViewById(R.id.tv_msg_c);
        tv_msg_d = (TextView) findViewById(R.id.tv_msg_d);
        tv_msgs_a = (TextView) findViewById(R.id.tv_msgs_a);
        tv_msgs_b = (TextView) findViewById(R.id.tv_msgs_b);
        tv_msgs_c = (TextView) findViewById(R.id.tv_msgs_c);
        tv_msgs_d = (TextView) findViewById(R.id.tv_msgs_d);
        
        iv_a = (ImageView) findViewById(R.id.iv_a);
        iv_b = (ImageView) findViewById(R.id.iv_b);
        iv_c = (ImageView) findViewById(R.id.iv_c);
        iv_d = (ImageView) findViewById(R.id.iv_d);
        iv_more_a = (ImageView) findViewById(R.id.iv_more_a);
        iv_more_b = (ImageView) findViewById(R.id.iv_more_b);
        iv_more_c = (ImageView) findViewById(R.id.iv_more_c);
        iv_more_d = (ImageView) findViewById(R.id.iv_more_d);
        iv_state_a = (ImageView) findViewById(R.id.iv_state_a);
        iv_state_b = (ImageView) findViewById(R.id.iv_state_b);
        iv_state_c = (ImageView) findViewById(R.id.iv_state_c);
        iv_state_d = (ImageView) findViewById(R.id.iv_state_d);
        
        cb_more_a = (CheckBox) findViewById(R.id.cb_more_a);
        cb_more_b = (CheckBox) findViewById(R.id.cb_more_b);
        cb_more_c = (CheckBox) findViewById(R.id.cb_more_c);
        cb_more_d = (CheckBox) findViewById(R.id.cb_more_d);
        
        rl_top_a = (RelativeLayout) findViewById(R.id.rl_top_a);
        rl_top_b = (RelativeLayout) findViewById(R.id.rl_top_b);
        rl_top_c = (RelativeLayout) findViewById(R.id.rl_top_c);
        rl_top_d = (RelativeLayout) findViewById(R.id.rl_top_d);
        rl_bottom_a = (RelativeLayout) findViewById(R.id.rl_bottom_a);
        rl_bottom_b = (RelativeLayout) findViewById(R.id.rl_bottom_b);
        rl_bottom_c = (RelativeLayout) findViewById(R.id.rl_bottom_c);
        rl_bottom_d = (RelativeLayout) findViewById(R.id.rl_bottom_d);
        
        btn_switch= (Button) findViewById(R.id.btn_switch);
        
        btn_switch.setOnClickListener(this);
        
        initFunction(cb_more_a, rl_top_a, rl_bottom_a, iv_more_a, true);
        initFunction(cb_more_b, rl_top_b, rl_bottom_b, iv_more_b, true);
        initFunction(cb_more_c, rl_top_c, rl_bottom_c, iv_more_c, false);
        initFunction(cb_more_d, rl_top_d, rl_bottom_d, iv_more_d, true);
        /*lv = (ListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(this);
        lv.setAdapter(myAdapter);*/
       
        SchoolGuardian();
    }
    private WatchSetModel mWatchSetModel;
	private WatchModel mWatchModel;
    private void initData(){
    	mWatchSetModel = AppContext.getInstance().getSelectWatchSet();
    	mWatchModel = AppContext.getInstance().getmWatchModel();
    }
    private void initView(){
    	initData();
    	tv_address.setText(mWatchModel.getSchoolAddress());
    	tv_class_a.setText(mWatchSetModel.getClassDisableda());
    	tv_class_b.setText(mWatchSetModel.getClassDisabledb());
    	tv_last.setText(mWatchModel.getLastestTime());
    	if(!TextUtils.isEmpty(mWatchSetModel.getClassDisableda()))
    		tv_time_a.setText(mWatchSetModel.getClassDisableda().split("-")[0]);
    	if(!TextUtils.isEmpty(mWatchSetModel.getClassDisabledb()))
    		tv_time_b.setText(mWatchSetModel.getClassDisabledb().split("-")[1]);
    	tv_time_d.setText(mWatchModel.getLastestTime());
    	btn_switch.setText(mWatchModel.isIsGuard()?R.string.defind_off:R.string.defind_on);
    }
    private void initSchoolDefend(){
    	if(TextUtils.isEmpty(mDefendInfoModel.getSchoolArriveType())){
    		iv_a.setImageResource(R.drawable.circle_yellow);
    		rl_top_a.setBackgroundResource(R.drawable.bg_defend);
    		cb_more_a.setVisibility(View.INVISIBLE);
    	}else{
    		//未到学校1，已到学校未迟到2，已到学校但迟到3
    		if(mDefendInfoModel.getSchoolArriveType().equals("1")){
    			iv_a.setImageResource(R.drawable.circle_yellow);
    			iv_state_a.setVisibility(View.INVISIBLE);
        		rl_top_a.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_a.setVisibility(View.VISIBLE);
        		initFunction(cb_more_a, rl_top_a, rl_bottom_a, iv_more_a, false);
    		}else if(mDefendInfoModel.getSchoolArriveType().equals("3")){
    			iv_a.setImageResource(R.drawable.gth);
    			iv_state_a.setVisibility(View.VISIBLE);
    			iv_state_a.setImageResource(R.drawable.gth);
        		rl_top_a.setBackgroundResource(R.drawable.bg_defend_noti);
        		cb_more_a.setVisibility(View.VISIBLE);
        		initFunction(cb_more_a, rl_top_a, rl_bottom_a, iv_more_a, true);
    		}else if(mDefendInfoModel.getSchoolArriveType().equals("2")){
    			iv_a.setImageResource(R.drawable.dg);
    			iv_state_a.setVisibility(View.VISIBLE);
    			iv_state_a.setImageResource(R.drawable.dg);
        		rl_top_a.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_a.setVisibility(View.VISIBLE);
        		initFunction(cb_more_a, rl_top_a, rl_bottom_a, iv_more_a, false);
    		}
    		
    		tv_msg_a.setText(mDefendInfoModel.getSchoolArriveTime()+" "+mDefendInfoModel.getSchoolArriveMsg());
    		GeocodeSearch geocoderSearch;
    		geocoderSearch = new GeocodeSearch(this);
    		geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
    			@Override
    			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
    				// TODO Auto-generated method stub
    				if (rCode == 0) {
    					if (result != null && result.getRegeocodeAddress() != null
    							&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
    						mDefendInfoModel.setSchoolArriveAdress(result.getRegeocodeAddress().getFormatAddress() + "附近");
    						tv_msgs_a.setText(getResources().getString(R.string.adress)
    		    					+ getResources().getString(R.string.mh) +
    		    					mDefendInfoModel.getSchoolArriveAdress());
    					} else {
    						tv_msgs_a.setText(getResources().getString(R.string.adress)
		    					+ getResources().getString(R.string.mh) +
		    					getResources().getString(R.string.no_result));
    						//System.out.println("暂无结果");
    						//暂无结果
    					}
    				} else if (rCode == 27) {
    					//网络错误
    				} else if (rCode == 32) {
    					//key错误
    				} else {
    					//其他错误
    				}
    			}
    			
    			@Override
    			public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDefendInfoModel.getSchoolArriveLat(), mDefendInfoModel.getSchoolArriveLng()), 200,
    				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
    		if(mDefendInfoModel.getSchoolArriveLat() != 0 && mDefendInfoModel.getSchoolArriveLng() != 0){
    			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    			tv_msgs_a.setVisibility(View.VISIBLE);
    		}else{
    			tv_msgs_a.setVisibility(View.GONE);
    		}
    			
    		/*if(TextUtils.isEmpty(mDefendInfoModel.getSchoolArriveAdress())){
    			tv_msgs_a.setText(getResources().getString(R.string.adress)
    					+ getResources().getString(R.string.mh)
    					+ mDefendInfoModel.getSchoolArriveAdress());
    		}else{
    			tv_msgs_a.setText("");
    		}*/
    	}
    	
    	if(TextUtils.isEmpty(mDefendInfoModel.getSchoolLeaveType())){
    		iv_b.setImageResource(R.drawable.circle_yellow);
    		rl_top_b.setBackgroundResource(R.drawable.bg_defend);
    		cb_more_b.setVisibility(View.INVISIBLE);
    	}else{
    		//还在学校1，离开学校没早退2，离开学校但早退3
    		System.out.println(mDefendInfoModel.getSchoolLeaveType());
    		if(mDefendInfoModel.getSchoolLeaveType().equals("1")){
    			iv_b.setImageResource(R.drawable.circle_yellow);
    			iv_state_b.setVisibility(View.INVISIBLE);
        		rl_top_b.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_b.setVisibility(View.VISIBLE);
        		initFunction(cb_more_b, rl_top_b, rl_bottom_b, iv_more_b, false);
    		}else if(mDefendInfoModel.getSchoolLeaveType().equals("3")){
    			iv_b.setImageResource(R.drawable.gth);
    			iv_state_b.setVisibility(View.VISIBLE);
    			iv_state_b.setImageResource(R.drawable.gth);
        		rl_top_b.setBackgroundResource(R.drawable.bg_defend_noti);
        		cb_more_b.setVisibility(View.VISIBLE);
        		initFunction(cb_more_b, rl_top_b, rl_bottom_b, iv_more_b, true);
    		}else if(mDefendInfoModel.getSchoolLeaveType().equals("2")){
    			iv_b.setImageResource(R.drawable.dg);
    			iv_state_b.setVisibility(View.VISIBLE);
    			iv_state_b.setImageResource(R.drawable.dg);
        		rl_top_b.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_b.setVisibility(View.VISIBLE);
        		initFunction(cb_more_b, rl_top_b, rl_bottom_b, iv_more_b, false);
    		}
    		
    		tv_msg_b.setText(mDefendInfoModel.getSchoolLeaveTime()+" "+mDefendInfoModel.getSchoolLeaveMsg());
    		GeocodeSearch geocoderSearch;
    		geocoderSearch = new GeocodeSearch(this);
    		geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
    			@Override
    			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
    				// TODO Auto-generated method stub
    				if (rCode == 0) {
    					if (result != null && result.getRegeocodeAddress() != null
    							&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
    						mDefendInfoModel.setSchoolLeaveAdress(result.getRegeocodeAddress().getFormatAddress() + "附近");
    						tv_msgs_b.setText(getResources().getString(R.string.adress)
    		    					+ getResources().getString(R.string.mh) +
    		    					mDefendInfoModel.getSchoolLeaveAdress());
    					} else {
    						tv_msgs_b.setText(getResources().getString(R.string.adress)
		    					+ getResources().getString(R.string.mh) +
		    					getResources().getString(R.string.no_result));
    						//System.out.println("暂无结果");
    						//暂无结果
    					}
    				} else if (rCode == 27) {
    					//网络错误
    				} else if (rCode == 32) {
    					//key错误
    				} else {
    					//其他错误
    				}
    			}
    			
    			@Override
    			public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDefendInfoModel.getSchoolLeaveLat(), mDefendInfoModel.getSchoolLeaveLng()), 200,
    				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
    		if(mDefendInfoModel.getSchoolLeaveLat() != 0 && mDefendInfoModel.getSchoolLeaveLng() != 0){
    			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    			tv_msgs_b.setVisibility(View.VISIBLE);
    		}else{
    			tv_msgs_b.setVisibility(View.GONE);
    		}
    		/*if(TextUtils.isEmpty(mDefendInfoModel.getSchoolLeaveAdress())){
    			tv_msgs_b.setText(getResources().getString(R.string.adress)
    					+ getResources().getString(R.string.mh)
    					+ mDefendInfoModel.getSchoolLeaveAdress());
    		}else{
    			tv_msgs_b.setText("");
    		}*/
    	}
    	
    	if(TextUtils.isEmpty(mDefendInfoModel.getRoadStayType())){
    		iv_c.setImageResource(R.drawable.circle_yellow);
    		rl_top_c.setBackgroundResource(R.drawable.bg_defend);
    		cb_more_c.setVisibility(View.INVISIBLE);
    	}else{
    		//路上逗留超过15分钟，只有一种类型0
    		if(mDefendInfoModel.getRoadStayType().equals("2")){
    			iv_c.setImageResource(R.drawable.circle_yellow);
    			iv_state_c.setVisibility(View.INVISIBLE);
        		rl_top_c.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_c.setVisibility(View.VISIBLE);
        		initFunction(cb_more_c, rl_top_c, rl_bottom_c, iv_more_c, false);
    		}else if(mDefendInfoModel.getRoadStayType().equals("1")){
    			iv_c.setImageResource(R.drawable.gth);
    			iv_state_c.setVisibility(View.VISIBLE);
    			iv_state_c.setImageResource(R.drawable.gth);
        		rl_top_c.setBackgroundResource(R.drawable.bg_defend_noti);
        		cb_more_c.setVisibility(View.VISIBLE);
        		initFunction(cb_more_c, rl_top_c, rl_bottom_c, iv_more_c, true);
    		}else if(mDefendInfoModel.getRoadStayType().equals("3")){
    			iv_c.setImageResource(R.drawable.dg);
    			iv_state_c.setVisibility(View.VISIBLE);
    			iv_state_c.setImageResource(R.drawable.dg);
        		rl_top_c.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_c.setVisibility(View.VISIBLE);
        		initFunction(cb_more_c, rl_top_c, rl_bottom_c, iv_more_c, false);
    		}
    		
    		tv_msg_c.setText(mDefendInfoModel.getRoadStayMsg());
    		GeocodeSearch geocoderSearch;
    		geocoderSearch = new GeocodeSearch(this);
    		geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
    			@Override
    			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
    				// TODO Auto-generated method stub
    				if (rCode == 0) {
    					if (result != null && result.getRegeocodeAddress() != null
    							&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
    						mDefendInfoModel.setRoadStayAdress(result.getRegeocodeAddress().getFormatAddress() + "附近");
    						tv_msgs_c.setText(getResources().getString(R.string.adress)
    		    					+ getResources().getString(R.string.mh) +
    		    					mDefendInfoModel.getRoadStayAdress());
    					} else {
    						tv_msgs_c.setText(getResources().getString(R.string.adress)
		    					+ getResources().getString(R.string.mh) +
		    					getResources().getString(R.string.no_result));
    						//System.out.println("暂无结果");
    						//暂无结果
    					}
    				} else if (rCode == 27) {
    					//网络错误
    				} else if (rCode == 32) {
    					//key错误
    				} else {
    					//其他错误
    				}
    			}
    			
    			@Override
    			public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDefendInfoModel.getRoadStayLat(), mDefendInfoModel.getRoadStayLng()), 200,
    				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
    		if(mDefendInfoModel.getRoadStayLat() != 0 && mDefendInfoModel.getRoadStayLng() != 0){
    			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    			tv_msgs_c.setVisibility(View.VISIBLE);
    		}else{
    			tv_msgs_c.setVisibility(View.GONE);
    		}
    		/*if(TextUtils.isEmpty(mDefendInfoModel.getRoadStayAdress())){
    			tv_msgs_c.setText(getResources().getString(R.string.adress)
    					+ getResources().getString(R.string.mh)
    					+ mDefendInfoModel.getRoadStayAdress());
    		}else{
    			tv_msgs_c.setText("");
    		}*/
    	}
    	
    	if(TextUtils.isEmpty(mDefendInfoModel.getHomeBackType())){
    		iv_d.setImageResource(R.drawable.circle_yellow);
    		rl_top_d.setBackgroundResource(R.drawable.bg_defend);
    		cb_more_d.setVisibility(View.INVISIBLE);
    	}else{
    		 //到家守护,未到家0，正常到家1，到家但迟到2
    		if(mDefendInfoModel.getHomeBackType().equals("1")){
    			iv_d.setImageResource(R.drawable.circle_yellow);
    			iv_state_d.setVisibility(View.INVISIBLE);
        		rl_top_d.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_d.setVisibility(View.VISIBLE);
        		initFunction(cb_more_d, rl_top_d, rl_bottom_d, iv_more_d, false);
    		}else if(mDefendInfoModel.getHomeBackType().equals("3")){
    			iv_d.setImageResource(R.drawable.gth);
    			iv_state_d.setVisibility(View.VISIBLE);
    			iv_state_d.setImageResource(R.drawable.gth);
        		rl_top_d.setBackgroundResource(R.drawable.bg_defend_noti);
        		cb_more_d.setVisibility(View.VISIBLE);
        		initFunction(cb_more_d, rl_top_d, rl_bottom_d, iv_more_d, true);
    		}else if(mDefendInfoModel.getHomeBackType().equals("2")){
    			iv_d.setImageResource(R.drawable.dg);
    			iv_state_d.setVisibility(View.VISIBLE);
    			iv_state_d.setImageResource(R.drawable.dg);
        		rl_top_d.setBackgroundResource(R.drawable.bg_defend);
        		cb_more_d.setVisibility(View.VISIBLE);
        		initFunction(cb_more_d, rl_top_d, rl_bottom_d, iv_more_d, false);
    		}
    		
    		tv_msg_d.setText(mDefendInfoModel.getHomeBackTime()+" "+mDefendInfoModel.getHomeBackMsg());
    		GeocodeSearch geocoderSearch;
    		geocoderSearch = new GeocodeSearch(this);
    		geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
    			@Override
    			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
    				// TODO Auto-generated method stub
    				if (rCode == 0) {
    					if (result != null && result.getRegeocodeAddress() != null
    							&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
    						mDefendInfoModel.setHomeBackAdress(result.getRegeocodeAddress().getFormatAddress() + "附近");
    						tv_msgs_d.setText(getResources().getString(R.string.adress)
    		    					+ getResources().getString(R.string.mh) +
    		    					mDefendInfoModel.getHomeBackAdress());
    					} else {
    						//暂无结果
    						tv_msgs_d.setText(getResources().getString(R.string.adress)
		    					+ getResources().getString(R.string.mh) +
		    					getResources().getString(R.string.no_result));
    						//System.out.println("暂无结果");
    					}
    				} else if (rCode == 27) {
    					//网络错误
    				} else if (rCode == 32) {
    					//key错误
    				} else {
    					//其他错误
    				}
    			}
    			
    			@Override
    			public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDefendInfoModel.getHomeBackLat(), mDefendInfoModel.getHomeBackLng()), 200,
    				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
    		if(mDefendInfoModel.getHomeBackLat() != 0 && mDefendInfoModel.getHomeBackLng() != 0){
    			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    			tv_msgs_d.setVisibility(View.VISIBLE);
    		}else{
    			tv_msgs_d.setVisibility(View.GONE);
    		}
    	}
    }
    private void initFunction(CheckBox cb,final RelativeLayout rl_a,final RelativeLayout rl_b, final ImageView iv, final boolean isNoti){
    	cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					if(isNoti)
						rl_a.setBackgroundResource(R.drawable.bg_defend_noti_top);
					else 
						rl_a.setBackgroundResource(R.drawable.bg_defend_noti_top_a);
					rl_b.setVisibility(View.VISIBLE);
					iv.setVisibility(View.VISIBLE);
				}else{
					if(isNoti)
						rl_a.setBackgroundResource(R.drawable.bg_defend_noti);
					else
						rl_a.setBackgroundResource(R.drawable.bg_defend);
					rl_b.setVisibility(View.GONE);
					iv.setVisibility(View.INVISIBLE);
				}
			}
		});
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.btn_right:
			settingDialog();
			break;
		case R.id.btn_switch:
			UpdateGuard(mWatchModel.isIsGuard()?"0":"1");
			break;
		}
	}
	private Dialog dialog;
	private void settingDialog(){
		if(dialog != null)
			dialog.cancel();
		View view = getLayoutInflater().inflate(R.layout.dialog_school_defend_setting, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.slide_up_down);
		WindowManager.LayoutParams wl = window.getAttributes();
		/*wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();*/
		
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		
		TextView tv_schoolinfo = (TextView) view.findViewById(R.id.tv_schoolinfo);
		TextView tv_homeinfo = (TextView) view.findViewById(R.id.tv_homeinfo);
		tv_schoolinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
				Intent intent_a = new Intent(mContext,SchoolInfo.class);
				startActivity(intent_a);
				
			}
		});
		tv_homeinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
				Intent intent_a = new Intent(mContext,HomeInfo.class);
				startActivity(intent_a);
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
	private String WedoffOn;
	private void UpdateGuard(String offOn) {
		WebService ws = new WebService(mContext, _UpdateGuard, true,
				"UpdateGuard");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		property.add(new WebServiceProperty("offOn", offOn));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
		WedoffOn = offOn;
	}
	private void SchoolGuardian() {
		WebService ws = new WebService(mContext, _SchoolGuardian, true,
				"SchoolGuardian");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(this).getLoginId()));
		property.add(new WebServiceProperty("deviceId", String.valueOf(AppData.GetInstance(mContext).getSelectDeviceId())));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _SchoolGuardian = 0;
	private final int _UpdateGuard = 1;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(id == _SchoolGuardian){
				int code = jsonObject.getInt("Code");
				if(code == 1){
					mDefendInfoModel = new DefendInfoModel();
					mDefendInfoModel.setSchoolID(jsonObject.getString("SchoolID"));
					mDefendInfoModel.setSchoolDay(jsonObject.getString("SchoolDay"));
					//System.out.println(jsonObject.getString("SchoolArriveContent"));
					if(!TextUtils.isEmpty(jsonObject.getString("SchoolArriveContent"))){
						String[] SchoolArriveStrs = jsonObject.getString("SchoolArriveContent").split(",");
						mDefendInfoModel.setSchoolArriveType(SchoolArriveStrs[0]);
						mDefendInfoModel.setSchoolArriveMsg(SchoolArriveStrs[1]);
						if(SchoolArriveStrs.length == 4){
							mDefendInfoModel.setSchoolArriveLat(Double.valueOf(SchoolArriveStrs[2]));
							mDefendInfoModel.setSchoolArriveLng(Double.valueOf(SchoolArriveStrs[3]));
						}
					}
					mDefendInfoModel.setSchoolArriveTime(jsonObject.getString("SchoolArriveTime"));

					if(!TextUtils.isEmpty(jsonObject.getString("SchoolLeaveContent"))){
						String[] SchoolLeaveStrs = jsonObject.getString("SchoolLeaveContent").split(",");
						mDefendInfoModel.setSchoolLeaveType(SchoolLeaveStrs[0]);
						mDefendInfoModel.setSchoolLeaveMsg(SchoolLeaveStrs[1]);
						if(SchoolLeaveStrs.length == 4){
							mDefendInfoModel.setSchoolLeaveLat(Double.valueOf(SchoolLeaveStrs[2]));
							mDefendInfoModel.setSchoolLeaveLng(Double.valueOf(SchoolLeaveStrs[3]));
						}
					}
					mDefendInfoModel.setSchoolLeaveTime(jsonObject.getString("SchoolLeaveTime"));
					
					if(!TextUtils.isEmpty(jsonObject.getString("RoadStayContent"))){
						if(jsonObject.getString("RoadStayContent").contains("-")){
							String[] RoadStayStrs = jsonObject.getString("RoadStayContent").split("-");
							String[] Strs = RoadStayStrs[RoadStayStrs.length - 1].split(",");
							mDefendInfoModel.setRoadStayType(Strs[0]);
							mDefendInfoModel.setRoadStayMsg(Strs[1]);
							if(Strs.length == 4){
								mDefendInfoModel.setRoadStayLat(Double.valueOf(Strs[2]));
								mDefendInfoModel.setRoadStayLng(Double.valueOf(Strs[3]));
							}
							mDefendInfoModel.setRoadStayTime(jsonObject.getString("RoadStayTime").split("-")[RoadStayStrs.length - 1]);
						}else{
							String[] Strs = jsonObject.getString("RoadStayContent").split(",");
							mDefendInfoModel.setRoadStayType(Strs[0]);
							mDefendInfoModel.setRoadStayMsg(Strs[1]);
							if(Strs.length == 4){
								mDefendInfoModel.setRoadStayLat(Double.valueOf(Strs[2]));
								mDefendInfoModel.setRoadStayLng(Double.valueOf(Strs[3]));
							}
						}
					}
					
					if(!TextUtils.isEmpty(jsonObject.getString("HomeBackContent"))){
						String[] HomeBackStrs = jsonObject.getString("HomeBackContent").split(",");
						mDefendInfoModel.setHomeBackType(HomeBackStrs[0]);
						mDefendInfoModel.setHomeBackMsg(HomeBackStrs[1]);
						if(HomeBackStrs.length == 4){
							mDefendInfoModel.setHomeBackLat(Double.valueOf(HomeBackStrs[2]));
							mDefendInfoModel.setHomeBackLng(Double.valueOf(HomeBackStrs[3]));
						}
					}
					mDefendInfoModel.setHomeBackTime(jsonObject.getString("HomeBackTime"));
					
					initSchoolDefend();
					
				}else{
					//MToast.makeText(jsonObject.getString("Message")).show();
				}
			}else if(id == _UpdateGuard){
				int code = jsonObject.getInt("Code");
				if(code == 1){
					mWatchModel.setIsGuard(WedoffOn.equals("1")?true:false);
					btn_switch.setText(mWatchModel.isIsGuard()?R.string.defind_off:R.string.defind_on);
					WatchDao mWatchDao = new WatchDao(mContext);
					ContentValues values = new ContentValues();
					values.put(WatchDao.COLUMN_NAME_ISGUARD,mWatchModel.isIsGuard()?"1":"0");
					mWatchDao.updateWatch(AppData.GetInstance(mContext).getSelectDeviceId(), values);
					//MToast.makeText(jsonObject.getString("Message")).show();
					
				}else{
					MToast.makeText(R.string.edit_fail).show();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		GeocodeSearch geocoderSearch;
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
			@Override
			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
				// TODO Auto-generated method stub
				if (rCode == 0) {
					if (result != null && result.getRegeocodeAddress() != null
							&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
					
					} else {
						System.out.println("暂无结果");
						//暂无结果
					}
				} else if (rCode == 27) {
					//网络错误
				} else if (rCode == 32) {
					//key错误
				} else {
					//其他错误
				}
			}
			
			@Override
			public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null && !TextUtils.isEmpty( result.getRegeocodeAddress().getFormatAddress())) {
			
			} else {
				System.out.println("暂无结果");
				//暂无结果
			}
		} else if (rCode == 27) {
			//网络错误
		} else if (rCode == 32) {
			//key错误
		} else {
			//其他错误
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//initData();
		initView();
	}
}
