package vip.inteltech.gat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.FeedBackMeModel;
import vip.inteltech.gat.utils.AppData;
import vip.inteltech.gat.utils.DateConversion;
import vip.inteltech.gat.utils.WebService;
import vip.inteltech.gat.utils.WebServiceProperty;
import vip.inteltech.gat.utils.WebService.WebServiceListener;
import vip.inteltech.gat.viewutils.MToast;


public class FeedbackIdeaMe extends BaseActivity implements OnClickListener,
	WebServiceListener{
	private FeedbackIdeaMe mContext;
	private ListView lv;
	private List<FeedBackMeModel> feedbackMeList; 
	private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback_idea_me);
        mContext = this;
        findViewById(R.id.btn_left).setOnClickListener(this);
        feedbackMeList = new ArrayList<FeedBackMeModel>();
        lv = (ListView) findViewById(R.id.lv);
        myAdapter = new MyAdapter(mContext);
        lv.setAdapter(myAdapter);
        GetFeedback();
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		}
		
	}
	private void GetFeedback() {
		WebService ws = new WebService(mContext, _GetFeedback, true, "GetFeedback");
		List<WebServiceProperty> property = new LinkedList<WebServiceProperty>();
		property.add(new WebServiceProperty("loginId", AppData.GetInstance(mContext).getLoginId()));
		ws.addWebServiceListener(mContext);
		ws.SyncGet(property);
	}
	private final int _GetFeedback = 0;
	@Override
	public void onWebServiceReceive(String method, int id, String result) {
		// TODO Auto-generated method stub
		JSONObject jsonObject;
		try {
			if(id == _GetFeedback){
				jsonObject = new JSONObject(result);
				int code = jsonObject.getInt("Code");
				if (code == 1) {
					JSONArray arrs = jsonObject.getJSONArray("Arr");
					int j;
					feedbackMeList = new ArrayList<FeedBackMeModel>();
					for(j = 0; j<arrs.length(); j++){
						JSONObject items = arrs.getJSONObject(j);
						FeedBackMeModel mFeedBackMeModel = new FeedBackMeModel();
						mFeedBackMeModel.setAnswerContent(items.getString("AnswerContent"));
						mFeedBackMeModel.setAnswerUserID(items.getString("AnswerUserID"));
						mFeedBackMeModel.setCreateTime(items.getString("CreateTime"));
						mFeedBackMeModel.setFeedbackID(items.getString("FeedbackID"));
						mFeedBackMeModel.setFeedbackState(items.getString("FeedbackState"));
						mFeedBackMeModel.setHandleTime(items.getString("HandleTime"));
						mFeedBackMeModel.setHandleUserID(items.getString("HandleUserID"));
						mFeedBackMeModel.setQuestionContent(items.getString("QuestionContent"));
						mFeedBackMeModel.setQuestionImg(items.getString("QuestionImg"));
						mFeedBackMeModel.setQuestionType(items.getString("QuestionType"));
						mFeedBackMeModel.setQuestionUserID(items.getString("QuestionUserID"));
						feedbackMeList.add(mFeedBackMeModel);
						/*Arr”：[{“AnswerContent”：“回答内容”，“AnswerUserID”：“回答用户ID”，“CreateTime”：“创建时间”，“FeedbackID”：“意见反馈ID”，
						“FeedbackState”：“反馈状态”，“HandleTime”：“处理时间”，“HandleUserID”：“处理用户的ID”，“QuestionContent”：
						“问题内容”，“QuestionImg”：“问题截图”，“QuestionType”：“问题类型”，“QuestionUserID”：“提问用户”}]}*/
				    	myAdapter.notifyDataSetChanged();
					}
				} else {
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
			return feedbackMeList.size();
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
						R.layout.feedback_idea_me_item, parent, false);
				mViewHolder.tv_question = (TextView) v.findViewById(R.id.tv_question);
				mViewHolder.tv_createtime = (TextView) v.findViewById(R.id.tv_createtime);
				mViewHolder.tv_answer = (TextView) v.findViewById(R.id.tv_answer);
				mViewHolder.tv_handletime = (TextView) v.findViewById(R.id.tv_handletime);
				mViewHolder.ll_b =  (LinearLayout) v.findViewById(R.id.ll_b);
				v.setTag(mViewHolder);
			} else {
				v = convertView;
				mViewHolder = (ViewHolder) v.getTag();
			}
			mViewHolder.tv_question.setText(feedbackMeList.get(position).getQuestionContent());
			mViewHolder.tv_createtime.setText(DateConversion.DateConversionUtilA(feedbackMeList.get(position).getCreateTime()));
			if(!TextUtils.isEmpty(feedbackMeList.get(position).getAnswerContent())){
				mViewHolder.tv_answer.setText(feedbackMeList.get(position).getAnswerContent());
				mViewHolder.tv_handletime.setText(DateConversion.DateConversionUtilA(feedbackMeList.get(position).getHandleTime()));
				mViewHolder.ll_b.setVisibility(View.VISIBLE);
			}else{
				mViewHolder.ll_b.setVisibility(View.INVISIBLE);
			}
			return v;
		}

	}

	class ViewHolder {
		TextView tv_question;
		TextView tv_createtime;
		TextView tv_answer;
		TextView tv_handletime;
		LinearLayout ll_b;
	}
}
