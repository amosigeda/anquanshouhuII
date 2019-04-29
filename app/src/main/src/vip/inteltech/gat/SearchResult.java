package vip.inteltech.gat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.maputil.GooglePoiItem;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.viewutils.MProgressDialog;

public class SearchResult extends BaseActivity implements OnClickListener, OnPoiSearchListener ,OnItemClickListener{
	private SearchResult mContext;
	private ListView lv;
	private MyAdapter myAdapter;

	private PoiResult poiResult; // poi返回的结果
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;
	private List<PoiItem> poiItems = new ArrayList<PoiItem>();// poi数据
	private List<GooglePoiItem> googlePoiItems = new ArrayList<GooglePoiItem>();
	private Marker detailMarker;// 显示Marker的详情
	private double lat_dev,lon_dev;

	private String keyWord;
	private String City;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_result);
        
        keyWord = getIntent().getStringExtra("keyWord");
        City = getIntent().getStringExtra("City");
        lat_dev = getIntent().getDoubleExtra("latitude", 0.0);
        lon_dev = getIntent().getDoubleExtra("longitude", 0.0);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        lv = (ListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(mContext);
        lv.setAdapter(myAdapter);
        lv.setOnItemClickListener(this);
        doSearchQuery();
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			break;
		}
	}
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		if(AppData.GetInstance(this).getMapSelect() == 1)
		{
			startProgressDialog();
			query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
			query.setPageSize(30);// 设置每页最多返回多少条poiitem
			query.setPageNum(0);// 设置查第一页
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();
		}
		else if(AppData.GetInstance(this).getMapSelect() == 2){
			startProgressDialog();
			new searchAddressThread(keyWord,lat_dev,lon_dev).start();
		}
	}

	private class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			// TODO Auto-generated method stub

			if(AppData.GetInstance(mContext).getMapSelect() == 1)
			{
				return poiItems.size();
			}
			else {
				return googlePoiItems.size();
			}
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
				v = LayoutInflater.from(mContext).inflate(R.layout.search_location_item, parent, false);
				mViewHolder.iv = (ImageView) v.findViewById(R.id.iv);
				mViewHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
				v.setTag(mViewHolder);
			} else {
				v = convertView;
				mViewHolder = (ViewHolder) v.getTag();
			}
			if(AppData.GetInstance(mContext).getMapSelect() == 1)
			{
				Log.v("kkk", "333");
				mViewHolder.tv_name.setText(poiItems.get(position).toString());
			}
			else {
				Log.v("kkk", "444");
				mViewHolder.tv_name.setText(googlePoiItems.get(position).address);
			}
			return v;
		}
	}

	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.v("kkk", "handler");
			myAdapter.notifyDataSetChanged();
		};
	};
	
	private class searchAddressThread extends Thread{
		private String address;
		private double lat,lon;
		public searchAddressThread(String text,double latitude,double longitude) {
			// TODO Auto-generated constructor stub
			address = text;
			lat = latitude;
			lon = longitude;
		}
		
        public void run() {
            HttpURLConnection connection = null;
            try {
            	//URLEncoder解决参数乱码
            	String urlString;
            	urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
            	urlString += "query="+URLEncoder.encode(keyWord);
            	urlString += "&key="+URLEncoder.encode("AIzaSyDMG1nXJI_hMtYCZnwinlJp-yErbhBpPkM");
            	urlString += "&location="+URLEncoder.encode(String.valueOf(lat))+","+URLEncoder.encode(String.valueOf(lon));
            	urlString += "&radius="+URLEncoder.encode("3000");
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                // 设置请求方法，默认是GET
                connection.setRequestMethod("GET");
                // 设置字符集
              //  connection.setRequestProperty("Charset", "UTF-8");
                // 设置文件类型
           //     connection.setRequestProperty("Content-Type", "json/application");
                // 设置请求参数，可通过Servlet的getHeader()获取
            //    connection.setRequestProperty("Cookie", "AppName=" + URLEncoder.encode("你好", "UTF-8"));
                // 设置自定义参数
           //     connection.setRequestProperty("query", URLEncoder.encode("雪象", "UTF-8"));
            //    connection.setRequestProperty("key", URLEncoder.encode("AIzaSyDMG1nXJI_hMtYCZnwinlJp-yErbhBpPkM", "UTF-8"));
             //   Log.v("kkk", "111");
                if(connection.getResponseCode() == 200){
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
                    byte[] data = new byte[1024];  
                    int count = -1;  
                    while((count = is.read(data,0,1024)) != -1)  
                        outStream.write(data, 0, count);  
                      
                    data = null;
                    String json = new String(outStream.toByteArray());
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray array = jsonObject.getJSONArray("results");
					for (int i = 0; i < array.length(); i++) {
						GooglePoiItem item = new GooglePoiItem();
						item.address = array.getJSONObject(i).getString("name");
						JSONObject locationObject = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
						item.latitude = Double.parseDouble(locationObject.getString("lat"));
						item.longitude = Double.parseDouble(locationObject.getString("lng"));
						item.type = array.getJSONObject(i).getJSONArray("types").getString(0);
						googlePoiItems.add(item);
					}
					Log.v("kkk", "111");
					mHandler.sendEmptyMessage(1);
					Log.v("kkk", "222");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                stopProgressDialog();
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stopProgressDialog();
			} finally {
                if(connection != null){
                    connection.disconnect();
					stopProgressDialog();
                }
            }
        };
    };

	class ViewHolder {
		ImageView iv;
		TextView tv_name;
	}
	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
	}

	/**
	 * POI搜索回调方法
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		stopProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					System.out.println("poiItems:"+poiItems.toString());
					List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						/*poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();*/
						myAdapter.notifyDataSetChanged();
					} else if (suggestionCities != null && suggestionCities.size() > 0) {
						//poi没有搜索到数据，返回一些推荐城市的信息
						showSuggestCity(suggestionCities);
					} else {
						//no_result
					}
				}
			} else {
				//no_result
			}
		} else if (rCode == 27) {
			//error_network
		} else if (rCode == 32) {
			//error_key
		} else {
			//error_other
		}
	}

	private MProgressDialog mProgressDialog = null;
	private void startProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = MProgressDialog.createDialog(this);
			mProgressDialog.setMessage(getResources().getString(R.string.wait));
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(AppData.GetInstance(this).getMapSelect() == 1)
		{
			LatLonPoint l = poiItems.get(position).getLatLonPoint();
			Intent intent = new Intent();
			intent.putExtra("Lat", l.getLatitude());
			intent.putExtra("Lng", l.getLongitude());
			setResult(RESULT_OK, intent);
		}
		else 
		{
			GooglePoiItem item = googlePoiItems.get(position);
			Intent intent = new Intent();
			intent.putExtra("Lat", item.latitude);
			intent.putExtra("Lng", item.longitude);
			setResult(RESULT_OK, intent);
		}
		finish();
			
	}

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}