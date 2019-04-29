package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.model.WatchStateModel;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MProgressDialog;
import vip.inteltech.gat.viewutils.MToast;


public class SearchLocation extends BaseActivity implements OnClickListener,
        WebServiceListener, OnPoiSearchListener, AMapLocationListener {
    private SearchLocation mContext;
    private TextView tv_Title, tv, tv_watch_location;
    private AutoCompleteTextView et_search;
    private ListView lv;
    private MyAdapter myAdapter;

    private PoiResult poiResult; // poi返回的结果
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems = new ArrayList<PoiItem>();// poi数据
    private Marker detailMarker;// 显示Marker的详情
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLonPoint lp;

    private boolean isHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_location);

        isHome = getIntent().getBooleanExtra("isHome", false);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        tv_Title.setText(isHome ? R.string.search_neighborhood : R.string.search_school);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText(isHome ? R.string.nearby_neighborhood : R.string.nearby_school);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.ll_watch).setOnClickListener(this);
        findViewById(R.id.ll_phone).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

        et_search = (AutoCompleteTextView) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            // 添加文本输入框监听事件
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String newText = s.toString().trim();
                Inputtips inputTips = new Inputtips(SearchLocation.this,
                        new InputtipsListener() {

                            @Override
                            public void onGetInputtips(List<Tip> tipList, int rCode) {
                                if (rCode == 0) {// 正确返回
                                    List<String> listString = new ArrayList<String>();
                                    for (int i = 0; i < tipList.size(); i++) {
                                        listString.add(tipList.get(i).getName());
                                    }
                                    ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                                            getApplicationContext(),
                                            R.layout.route_inputs, listString);
                                    et_search.setAdapter(aAdapter);
                                    aAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                try {
                    inputTips.requestInputtips(newText, "");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        });
        lv = (ListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(mContext);
        lv.setAdapter(myAdapter);
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                LatLonPoint l = poiItems.get(position).getLatLonPoint();

                Intent intent = new Intent();
                intent.putExtra("Lat", l.getLatitude());
                intent.putExtra("Lng", l.getLongitude());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        tv_watch_location = (TextView) findViewById(R.id.tv_watch_location);
        WatchModel mWatchModel = AppContext.getInstance().getWatchMap().get(String.valueOf(AppData.GetInstance(this).getSelectDeviceId()));
        if (mWatchModel != null && !TextUtils.isEmpty(mWatchModel.getDeviceType()) && mWatchModel.getDeviceType().equals("2")) {
            tv_watch_location.setText(R.string.locator_location);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_search:
                String keyWord = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(keyWord))
                    return;
                Intent intent_c = new Intent(mContext, SearchResult.class);
                intent_c.putExtra("keyWord", keyWord);
                intent_c.putExtra("City", City);
                startActivityForResult(intent_c, REARCH);
                break;
            case R.id.ll_watch:
                Intent intent_a = new Intent();
                WatchStateModel mWatchStateModel = AppContext.getInstance().getmWatchStateModel();
                if (mWatchStateModel != null && (mWatchStateModel.getLatitude() != 0 || mWatchStateModel.getLongitude() != 0)) {
                    intent_a.putExtra("Lat", mWatchStateModel.getLatitude());
                    intent_a.putExtra("Lng", mWatchStateModel.getLongitude());
                }
                setResult(RESULT_OK, intent_a);
                finish();
                break;
            case R.id.ll_phone:
                Intent intent_b = new Intent();
                if (lp != null) {
                    intent_b.putExtra("Lat", lp.getLatitude());
                    intent_b.putExtra("Lng", lp.getLongitude());
                }
                setResult(RESULT_OK, intent_b);
                finish();
                break;
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        startProgressDialog();
        query = new PoiSearch.Query("", isHome ? "小区" : "学校", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

        if (lp != null) {
            System.out.println("doSearchQuery");
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new SearchBound(lp, 2000, true));//
            // 设置搜索区域为以lp点为圆心，其周围2000米范围
            /*
             * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
             * list.add(lp);
             * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
             * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
             */
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    private final int _ChangePassword = 0;

    @Override
    public void onWebServiceReceive(String method, int id, String result) {
        // TODO Auto-generated method stub
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (id == _ChangePassword) {
                int code = jsonObject.getInt("Code");
                if (code == 1) {
                } else if (code == -1) {
                    // -1表示输入参数错误
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else if (code == 3) {
                    // 3原密码错误
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else if (code < 0) {
                    // 系统异常小于0
                    MToast.makeText(jsonObject.getString("Message")).show();
                } else if (code <= 0) {
                    // 常规异常大于0
                    MToast.makeText(jsonObject.getString("Message")).show();
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
            return poiItems.size();
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
                v = LayoutInflater.from(mContext).inflate(
                        R.layout.search_location_item, parent, false);
                mViewHolder.iv = (ImageView) v.findViewById(R.id.iv);
                mViewHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
                v.setTag(mViewHolder);
            } else {
                v = convertView;
                mViewHolder = (ViewHolder) v.getTag();
            }
            mViewHolder.tv_name.setText(poiItems.get(position).toString());
            return v;
        }

    }

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
    private String City = "深圳";

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        stopProgressDialog();
        if (rCode == 0) {

            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    //MToast.makeText(poiItems.toString()).show();
                    if (poiItems != null && poiItems.size() > 0) {
						/*poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();*/
                        City = poiItems.get(0).getCityName();
                        myAdapter.notifyDataSetChanged();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        //poi没有搜索到数据，返回一些推荐城市的信息
                        showSuggestCity(suggestionCities);
                    } else {
                        MToast.makeText(R.string.no_result).show();
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

    private boolean isFirst = false;

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        // TODO Auto-generated method stub
        if (!isFirst) {
            if (aLocation != null) {
                lp = new LatLonPoint(aLocation.getLatitude(),
                        aLocation.getLongitude());
                doSearchQuery();
            }
            isFirst = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        super.onDestroy();
    }

    private final int REARCH = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REARCH:
                if (resultCode == RESULT_OK) {
                    Intent intent_b = new Intent();
                    intent_b.putExtra("Lat", data.getDoubleExtra("Lat", 0d));
                    intent_b.putExtra("Lng", data.getDoubleExtra("Lng", 0d));
                    setResult(RESULT_OK, intent_b);
                    finish();
                }
                break;
            default:
                break;

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
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}