package vip.inteltech.gat.chatutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.db.ChatMsgDao;
import vip.inteltech.gat.model.ContactModel;
import vip.inteltech.gat.model.WatchModel;
import vip.inteltech.gat.utils.*;
import vip.inteltech.gat.utils.AppContext;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatMsgViewAdapter extends BaseAdapter {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	private List<ChatMsgEntity> coll;

	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private Context context;
	private List<ContactModel> contactsList = AppContext.getInstance().getContactList();
	private WatchModel mWatchModel = AppContext.getInstance().getmWatchModel();
	private ChatMsgDao mChatMsgDao;
	private int[] headID = new int[]{R.drawable.contacts_father_small, R.drawable.contacts_mom_small, 
			R.drawable.contacts_grandfather_small,R.drawable.contacts_grandmother_small, R.drawable.contacts_grandpa_small,
			R.drawable.contacts_grandma_small, R.drawable.contacts_custom_small};

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
		this.coll = coll;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mChatMsgDao = new ChatMsgDao(context);
	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public int getItemViewType(int position) {
		ChatMsgEntity entity = coll.get(position);

		if (!entity.getType().equals("3")&&entity.getObjectId().equals(String.valueOf(AppData.GetInstance(this.context).getUserId()))) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}
	}

	public int getViewTypeCount() {
		return 2;
	}

	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ChatMsgEntity entity = coll.get(position);
		
		//getType:3 equals isFromMe
		boolean isFromMe = coll.get(position).getType().equals("3") && coll.get(position).getObjectId().equals(String.valueOf(AppData.GetInstance(this.context).getUserId()));
		ViewHolder viewHolder = null;
		View v_a,v_b;

		if (TextUtils.isEmpty(entity.getMsgType())) {
			entity.setMsgType("0");
		}
		if (isFromMe) {
			convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
		} else {
			convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
		}

		viewHolder = new ViewHolder();
		viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
		viewHolder.iv_chatcontent = (ImageView) convertView.findViewById(R.id.iv_chatcontent);
		viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
		viewHolder.iv_isRead = (ImageView) convertView.findViewById(R.id.iv_isRead);
		viewHolder.tv_length = (TextView) convertView.findViewById(R.id.tv_length);
		viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		viewHolder.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
		viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.rl);
		viewHolder.isFromMe = isFromMe;
		viewHolder.iv_head.setImageResource(R.drawable.head_empty);
		if(entity.getType().equals("3")){
			for(ContactModel mContatctModel:contactsList){
				if(!mContatctModel.getType().equals("3")){
					if(mContatctModel.getObjectId().equals(entity.getObjectId())){
						//System.out.println(mContatctModel.getRelationShip() + " ");
						viewHolder.tv_name.setText(TextUtil.MaxTextLengthChange(4, mContatctModel.getRelationShip()));
						if(TextUtils.isEmpty(mContatctModel.getAvatarUrl())){
							if((Integer.valueOf(mContatctModel.getAvatar())-1)<7 && (Integer.valueOf(mContatctModel.getAvatar())-1)>=0)
								viewHolder.iv_head.setImageResource(headID[Integer.valueOf(mContatctModel.getAvatar()) - 1]);
							else
								viewHolder.iv_head.setImageResource(R.drawable.contacts_unconfirmed_small);
						}
						else
							ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL+mContatctModel.getAvatarUrl(),  
									viewHolder.iv_head, new AnimateFirstDisplayListener()); 
						continue;
					}
				}
			}
		}else{
			viewHolder.tv_name.setText(TextUtil.MaxTextLengthChange(4, mWatchModel.getName()));
			ImageLoader.getInstance().displayImage(Contents.IMAGEVIEW_URL+mWatchModel.getAvatar(),  
					viewHolder.iv_head, new AnimateFirstDisplayListener()); 
		}
		
		//if the msg is read or it's a text msg
		if(entity.isRead() || entity.getMsgType().equals("1")){
			viewHolder.iv_isRead.setVisibility(View.INVISIBLE);
		}else{
			viewHolder.iv_isRead.setVisibility(View.VISIBLE);
		}
		
		
		if(position > 0){
			String str = DateConversion.TimeChange(entity.getUpdateTime(), coll.get(position-1).getUpdateTime());
			viewHolder.tvSendTime.setText(str);
			if(TextUtils.isEmpty(viewHolder.tvSendTime.getText().toString().trim())){
				viewHolder.tvSendTime.setVisibility(View.INVISIBLE);
			}else{
				viewHolder.tvSendTime.setVisibility(View.VISIBLE);
			}
		}else{
			viewHolder.tvSendTime.setText(DateConversion.TimeChange(entity.getUpdateTime(), ""));
		}
		
		
		viewHolder.iv_chatcontent.setImageBitmap(null);
		if (viewHolder.isFromMe){
			viewHolder.iv_chatcontent
			.setImageResource(R.drawable.chatto_voice_playing_f3);
		}else{
			viewHolder.iv_chatcontent
			.setImageResource(R.drawable.chatto_voice_playing_f33);
		}

		//voice msg,set the frame length 
		if (!entity.getMsgType().equals("1")) {
			int time = Integer.valueOf(entity.getLength());
			viewHolder.rl.setTag(entity.getPath());
			if(viewHolder.rl != null && viewHolder.rl.getTag().equals(entity.getPath())){
				LayoutParams para;
				para = viewHolder.rl.getLayoutParams();
				if(time*50>450){
					para.width = 450;
				}else if(time*50<200){
					para.width = 200;
				}else{
					para.width = time*50;
				}
				viewHolder.rl.setLayoutParams(para);
			}
		}

		viewHolder.tv_length.setText(entity.getLength() + "\"");
		viewHolder.iv_chatcontent.setTag(entity.getPath());

		final ViewHolder viewHolders = viewHolder;
		
		//text msg,read the local msg content if exist,otherwise download the content
		//from server and write to the local device
		if (entity.getMsgType().equals("1")) {
			String[] str = coll.get(position).getPath().split("/");
			String txtName = str[str.length - 1];
		//	Log.v("kkk", "txtName = "+txtName);
			String contStr = ReadTxtFile(txtName);
		//	Log.v("kkk", "conStr = "+contStr+"  length = "+contStr.length());
			if (contStr.length() > 0) {
				viewHolder.tv_chatcontent.setText(contStr);
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
					//	Log.v("kkk", "Runnable");
						try {
							String[] str = coll.get(position).getPath().split("/");
							String txtName = str[str.length - 1];
							DownloadVoice.downloadmp(Contents.VOICE_URL+coll.get(position).getPath(), "TestRecord/", txtName);

							Message msg = new Message();
							msg.what = -2;
							msg.obj = (Object) viewHolders;
							Bundle data = new Bundle();
							data.putString("txtName", ReadTxtFile(txtName));
							msg.setData(data);
							handler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}

			Rect bounds = new Rect();
			TextPaint paint = viewHolder.tv_chatcontent.getPaint();
			paint.getTextBounds(contStr, 0, contStr.length(), bounds);
			float width = paint.measureText(contStr);// bounds.width();
			if (viewHolder.rl != null) {
				LayoutParams para;
				para = viewHolder.rl.getLayoutParams();
				if (width+120 > 450){
					para.width = 450;
				} else if (width+120 < 120){
					para.width = 120;
				} else {
					para.width = (int) (width+120);
				}
				viewHolder.rl.setLayoutParams(para);
			}
		} else {// voice msg
			viewHolder.rl.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//System.out.println(coll.get(position).getType()+"    "+coll.get(position).getObjectId());
					playAudioAnimation(viewHolders.iv_chatcontent,viewHolders.isFromMe);
					viewHolders.iv_isRead.setVisibility(View.INVISIBLE);
					ContentValues values = new ContentValues();
					values.put(ChatMsgDao.COLUMN_NAME_ISREAD, 1);
					mChatMsgDao.updateChatMsg(entity.getDeviceVoiceId(), values);
					coll.get(position).setRead(true);
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Message msg = new Message();

								String[] str = coll.get(position).getPath().split("/");
								String amrName = str[str.length - 1];
								int i = DownloadVoice.downloadmp(Contents.VOICE_URL+coll.get(position).getPath(), "TestRecord/", amrName);
								if (i == -1) {
									msg.what = i;
								} else {
									msg.what = Integer.valueOf(coll.get(position).getDeviceVoiceId());
									Bundle data = new Bundle();
									data.putString("name", amrName);
									msg.setData(data);
								}
								handler.sendMessage(msg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});
		}

		if (entity.getMsgType().equals("1")) {
			viewHolder.tv_chatcontent.setVisibility(View.VISIBLE);
			viewHolder.iv_chatcontent.setVisibility(View.GONE);
			viewHolder.tv_length.setVisibility(View.GONE);
		} else {
			viewHolder.tv_chatcontent.setVisibility(View.GONE);
			viewHolder.iv_chatcontent.setVisibility(View.VISIBLE);
			viewHolder.tv_length.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	public class ViewHolder {
		public TextView tvSendTime;
		public ImageView iv_chatcontent;
		public ImageView iv_head;
		public ImageView iv_isRead;
		public TextView tv_length;
		public RelativeLayout rl;
		public TextView tv_name;
		public TextView tv_chatcontent;
		public boolean isFromMe;
	}

	/**
	 * @Description
	 * @param name
	 */
	String name_a = "";
	private void playMusic(String name) {
		try {
			if(name_a.equals(name)){
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
					name_a = "";
				}
				return;
			}
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					name_a = "";
				}
			});
			name_a = name;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//语音动画控制器  
	Timer mTimer=null;  
	//语音动画控制任务  
	TimerTask mTimerTask=null;  
	//记录语音动画图片  
	int index=1;  
	AudioAnimationHandler audioAnimationHandler=null; 
	private void playAudioAnimation(final ImageView imageView, boolean isFromMe) {  
	        //定时器检查播放状态     
	        stopTimer();
	        mTimer=new Timer();
	        //将要关闭的语音图片归位  
	        if(audioAnimationHandler!=null)
	        {  
	            Message msg=new Message();  
	            msg.what=3;  
	            audioAnimationHandler.sendMessage(msg);  
	        }  
	          
	        audioAnimationHandler=new AudioAnimationHandler(imageView,isFromMe);  
	        mTimerTask = new TimerTask() {   
	            public boolean hasPlayed=false;  
	            @Override
	            public void run() {
	                if(mMediaPlayer.isPlaying()) {
	                    hasPlayed=true;
	                    index=(index+1)%3;
	                    Message msg=new Message();
	                    msg.what=index;
	                    audioAnimationHandler.sendMessage(msg);
	                }else
	                {
	                    //当播放完时  
	                    Message msg=new Message();
	                    msg.what=3;
	                    audioAnimationHandler.sendMessage(msg);
	                    //播放完毕时需要关闭Timer等  
	                    if(hasPlayed)
	                    {
	                        stopTimer();
	                    }
	                }
	            }
	        };
	        //调用频率为500毫秒一次  
	        mTimer.schedule(mTimerTask, 0, 500);
	    }
	 class AudioAnimationHandler extends Handler  
	 {  
         ImageView imageView;  
         //判断是左对话框还是右对话框  
         boolean isleft;  
         public AudioAnimationHandler(ImageView imageView,boolean isFromMe)  
         {  
            this.imageView=imageView;  
            //判断是左对话框还是右对话框 我这里是在前面设置ScaleType来表示的  
            //isleft=imageView.getScaleType()==ScaleType.FIT_START?true:false;  
            isleft = !isFromMe;
         }  
         @Override
         public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            //根据msg.what来替换图片，达到动画效果  
            switch (msg.what) {  
                case 0 :  
                    imageView.setImageResource(isleft?R.drawable.chatto_voice_playingg:R.drawable.chatto_voice_playing);  
                    break;  
                case 1 :  
                    imageView.setImageResource(isleft?R.drawable.chatto_voice_playing_f11:R.drawable.chatto_voice_playing_f1);  
                    break;  
                case 2 :  
                    imageView.setImageResource(isleft?R.drawable.chatto_voice_playing_f22:R.drawable.chatto_voice_playing_f2);  
                    break;  
                default :
                    imageView.setImageResource(isleft?R.drawable.chatto_voice_playing_f33:R.drawable.chatto_voice_playing_f3);  
                    break;  
             }
	     }
	 }  
	 private void stopTimer(){    
         if (mTimer != null) {    
             mTimer.cancel();    
             mTimer = null;    
         }    

         if (mTimerTask != null) {    
             mTimerTask.cancel();    
             mTimerTask = null;    
         }     

    }
	AnimationDrawable animationDrawable;
	private Handler handler = new Handler() { // 更新UI的handler
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == -2) {
				String contStr = msg.getData().getString("txtName");
				ViewHolder viewHolder = (ViewHolder) msg.obj;
				viewHolder.tv_chatcontent.setText(contStr);

				Rect bounds = new Rect();
				TextPaint paint = viewHolder.tv_chatcontent.getPaint();
				paint.getTextBounds(contStr, 0, contStr.length(), bounds);
				int width = bounds.width();
				if (viewHolder.rl != null) {
					LayoutParams para;
					para = viewHolder.rl.getLayoutParams();
					if (width+90 > 450){
						para.width = 450;
					} else if (width+90 < 90){
						para.width = 90;
					} else {
						para.width = width+90;
					}
					viewHolder.rl.setLayoutParams(para);
				}
			} else if (msg.what != -1) {
				 playMusic(AppContext.getInstance().getContext().getFilesDir().getAbsolutePath() + "/TestRecord/" + msg.getData().getString("name"));
			}
			notifyDataSetChanged(); //refresh the msg list after send a msg.--msg list scroll to the end
		}
	};
	public void stopPlay(){
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}
	private String ReadTxtFile(String strFilePath)
    {
        String content = "";
        String filePath = AppContext.getInstance().getContext().getFilesDir().getAbsolutePath() + "/TestRecord/" + strFilePath;
        File file = new File(filePath);
    //    Log.v("kkk", filePath);
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                while (( line = buffreader.readLine()) != null) {
                    content += line;
                }
                instream.close();
            }
        }
        catch (java.io.FileNotFoundException e)
        {
            Log.i("wangqh", "The File doesn't not exist.");
        }
        catch (IOException e)
        {
             Log.i("wangqh", e.getMessage());
        }

        return content;
    }
}
