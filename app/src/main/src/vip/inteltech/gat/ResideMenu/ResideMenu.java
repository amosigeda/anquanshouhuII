package vip.inteltech.gat.ResideMenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.AboutWatch;
import vip.inteltech.gat.AddressBook;
import vip.inteltech.gat.Album;
import vip.inteltech.gat.BabyInfo;
import vip.inteltech.gat.FeedbackIdea;
import vip.inteltech.gat.Fence;
import vip.inteltech.gat.FriendList;
import vip.inteltech.gat.HistoryTrack;
import vip.inteltech.gat.Setting;
import vip.inteltech.gat.WatchFare;
import vip.inteltech.gat.WatchSetting;
import vip.inteltech.gat.Health;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class ResideMenu extends FrameLayout implements OnClickListener{

	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	private static final int PRESSED_MOVE_HORIZANTAL = 2;
	private static final int PRESSED_DOWN = 3;
	private static final int PRESSED_DONE = 4;
	private static final int PRESSED_MOVE_VERTICAL = 5;

	private ImageView imageViewShadow;
	private ImageView imageViewBackground;
	private LinearLayout leftMenu;
	private LinearLayout rightMenu;
	private LinearLayout scrollViewMenu;
	/** the activity that view attach to */
	private Activity activity;
	/** the decorview of the activity */
	private ViewGroup viewDecor;
	/** the viewgroup of the activity */
	private TouchDisableView viewActivity;
	/** the flag of menu open status */
	private boolean isOpened;

	private float shadowAdjustScaleX;
	private float shadowAdjustScaleY;
	/** the view which don't want to intercept touch event */
	private List<View> ignoredViews;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private OnMenuListener menuListener;
	private float lastRawX;
	private boolean isInIgnoredView = false;
	private int scaleDirection = DIRECTION_LEFT;
	private int pressedState = PRESSED_DOWN;
	private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
	// valid scale factor is between 0.0f and 1.0f.
	private float mScaleValue = 0.5f;
	private float translationX = 300f;

	public ImageView iv_head;
	private Button btn_add;
	private LinearLayout ll_babyinfo, ll_address_book, ll_watch_setting,
		ll_watch_fare, ll_fence, ll_about_watch, ll_problem_feedback, ll_setting;
	public LinearLayout ll_album, ll_history_track, ll_health, ll_friend_list;
	public ImageView iv_unRead_msg_record, iv_unRead_watch_fare, iv_unRead_album;
	private Context mContext;
	public TextView tv_about_watch, tv_watch_setting, tv_watch_fare, tv_change_watch, tv_watch_album;
	public ResideMenu(Context context) {
		super(context);
		mContext = context;
		initViews(context);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenus, this);
		leftMenu = (LinearLayout) findViewById(R.id.sv_left_menu);
		rightMenu = (LinearLayout) findViewById(R.id.sv_right_menu);
		imageViewShadow = (ImageView) findViewById(R.id.iv_shadow);
		imageViewBackground = (ImageView) findViewById(R.id.iv_background);
		
		iv_head = (ImageView) findViewById(R.id.iv_head);
		btn_add = (Button) findViewById(R.id.btn_add);
		
		//iv_unRead_msg_record = (ImageView) findViewById(R.id.iv_unRead_msg_record);
		iv_unRead_watch_fare = (ImageView) findViewById(R.id.iv_unRead_watch_fare);
		iv_unRead_album = (ImageView) findViewById(R.id.iv_unRead_album);
		
		ll_babyinfo = (LinearLayout) findViewById(R.id.ll_babyinfo);
		ll_address_book = (LinearLayout) findViewById(R.id.ll_address_book);
		ll_friend_list = (LinearLayout) findViewById(R.id.ll_friend_list);
		ll_watch_setting = (LinearLayout) findViewById(R.id.ll_watch_setting);
		ll_watch_fare = (LinearLayout) findViewById(R.id.ll_watch_fare);
		ll_fence = (LinearLayout) findViewById(R.id.ll_fence);
		ll_about_watch = (LinearLayout) findViewById(R.id.ll_about_watch);
		ll_problem_feedback = (LinearLayout) findViewById(R.id.ll_problem_feedback);
		ll_setting = (LinearLayout) findViewById(R.id.ll_setting);
		ll_history_track = (LinearLayout) findViewById(R.id.ll_history_track);
		ll_album = (LinearLayout) findViewById(R.id.ll_album);
		ll_health = (LinearLayout) findViewById(R.id.ll_health);
		
		ll_babyinfo.setOnClickListener(this);
		ll_address_book.setOnClickListener(this);
		ll_friend_list.setOnClickListener(this);
		ll_watch_setting.setOnClickListener(this);
		ll_watch_fare.setOnClickListener(this);
		ll_fence.setOnClickListener(this);
		ll_about_watch.setOnClickListener(this);
		ll_problem_feedback.setOnClickListener(this);
		ll_setting.setOnClickListener(this);
		ll_history_track.setOnClickListener(this);
		ll_album.setOnClickListener(this);
		ll_health.setOnClickListener(this);

		tv_about_watch = (TextView) findViewById(R.id.tv_about_watch);
		tv_watch_setting = (TextView) findViewById(R.id.tv_watch_setting);
		tv_watch_fare = (TextView) findViewById(R.id.tv_watch_fare);
		tv_change_watch = (TextView) findViewById(R.id.tv_change_watch);
		tv_watch_album = (TextView) findViewById(R.id.tv_watch_album);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ll_babyinfo:
			mContext.startActivity(new Intent(mContext, BabyInfo.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_address_book:
			mContext.startActivity(new Intent(mContext, AddressBook.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_friend_list:
			mContext.startActivity(new Intent(mContext, FriendList.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_watch_setting:
			mContext.startActivity(new Intent(mContext, WatchSetting.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_watch_fare:
			mContext.startActivity(new Intent(mContext, WatchFare.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_fence:
			mContext.startActivity(new Intent(mContext, Fence.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_about_watch:
			mContext.startActivity(new Intent(mContext, AboutWatch.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_problem_feedback:
			mContext.startActivity(new Intent(mContext, FeedbackIdea.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_setting:
			mContext.startActivity(new Intent(mContext, Setting.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_history_track:
			mContext.startActivity(new Intent(mContext, HistoryTrack.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_album:
			mContext.startActivity(new Intent(mContext, Album.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		case R.id.ll_health:
			mContext.startActivity(new Intent(mContext, Health.class));
			((Activity) mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		break;
		}
	}
	public void setIv_headClick(OnClickListener ocl){
		iv_head.setOnClickListener(ocl);
	}
	public void setBtn_addClick(OnClickListener ocl){
		btn_add.setOnClickListener(ocl);
	}
	/**
	 * use the method to set up the activity which residemenu need to show;
	 * 
	 * @param activity
	 */
	public void attachToActivity(Activity activity) {
		initValue(activity);
		setShadowAdjustScaleXByOrientation();
		viewDecor.addView(this, 0);
		setViewPadding();
	}

	private void initValue(Activity activity) {
		this.activity = activity;
		ignoredViews = new ArrayList<View>();
		viewDecor = (ViewGroup) activity.getWindow().getDecorView();
		viewActivity = new TouchDisableView(this.activity);

		View mContent = viewDecor.getChildAt(0);
		viewDecor.removeViewAt(0);
		viewActivity.setContent(mContent);
		addView(viewActivity);
	}

	private void setShadowAdjustScaleXByOrientation() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			shadowAdjustScaleX = 0.034f;
			shadowAdjustScaleY = 0.12f;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			shadowAdjustScaleX = 0.06f;
			shadowAdjustScaleY = 0.07f;
		}
	}

	/**
	 * set the menu background picture;
	 * 
	 * @param imageResrouce
	 */
	public void setBackground(int imageResrouce) {
		imageViewBackground.setImageResource(imageResrouce);
	}

	/**
	 * the visiblity of shadow under the activity view;
	 * 
	 * @param isVisible
	 */
	public void setShadowVisible(boolean isVisible) {
//		if (isVisible)
//			imageViewShadow.setImageResource(R.drawable.shadow);
//		else
//			imageViewShadow.setImageBitmap(null);
	}




	/**
	 * if you need to do something on the action of closing or opening menu, set
	 * the listener here.
	 * 
	 * @return
	 */
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public OnMenuListener getMenuListener() {
		return menuListener;
	}

	/**
	 * we need the call the method before the menu show, because the padding of
	 * activity can't get at the moment of onCreateView();
	 */
	private void setViewPadding() {
		this.setPadding(viewActivity.getPaddingLeft(),
				viewActivity.getPaddingTop(), viewActivity.getPaddingRight(),
				viewActivity.getPaddingBottom());
	}

	/**
	 * show the reside menu;
	 */
	public void openMenu(int direction) {
		setScaleDirection(direction);

		isOpened = true;
//		AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity, mScaleValue, 1.0f);
//		AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow, mScaleValue + shadowAdjustScaleX, mScaleValue + shadowAdjustScaleY);
        AnimatorSet scaleDown_activity = buildTranslationXDownAnimation(viewActivity, translationX);
//		AnimatorSet scaleDown_shadow = buildTranslationXDownAnimation(imageViewShadow, translationX);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 1.0f);
        scaleDown_activity.addListener(animationListener);
//		scaleDown_activity.playTogether(scaleDown_shadow);
		scaleDown_activity.playTogether(alpha_menu);
		scaleDown_activity.start();
	}

	/**
	 * close the reslide menu;
	 */
	public void closeMenu() {
		isOpened = false;
//		AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity, 1.0f, 1.0f);
//		AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow, 1.0f, 1.0f);
        AnimatorSet scaleUp_activity = buildTranslationXUpAnimation(viewActivity, 0);
//        AnimatorSet scaleUp_shadow = buildTranslationXUpAnimation(imageViewShadow, 0);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 0.0f);
		scaleUp_activity.addListener(animationListener);
//		scaleUp_activity.playTogether(scaleUp_shadow);
		scaleUp_activity.playTogether(alpha_menu);
		scaleUp_activity.start();
	}

	@Deprecated
	public void setDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	public void setSwipeDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	private boolean isInDisableDirection(int direction) {
		return disabledSwipeDirection.contains(direction);
	}

	private void setScaleDirection(int direction) {

		int screenWidth = getScreenWidth();
		float pivotX;
		float pivotY = getScreenHeight() * 0.5f;

		if (direction == DIRECTION_LEFT) {
			scrollViewMenu = leftMenu;
			pivotX = screenWidth * 1.5f;
		} else {
			scrollViewMenu = rightMenu;
			pivotX = screenWidth * -0.5f;
		}

		ViewHelper.setPivotX(viewActivity, pivotX);
		ViewHelper.setPivotY(viewActivity, pivotY);
		ViewHelper.setPivotX(imageViewShadow, pivotX);
		ViewHelper.setPivotY(imageViewShadow, pivotY);
		scaleDirection = direction;
	}

	/**
	 * return the flag of menu status;
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private OnClickListener viewActivityOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (isOpened())
				closeMenu();
		}
	};

	private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			if (isOpened()) {
				scrollViewMenu.setVisibility(VISIBLE);
				if (menuListener != null)
					menuListener.openMenu();
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// reset the view;
			if (isOpened()) {
				viewActivity.setTouchDisable(true);
				viewActivity.setOnClickListener(viewActivityOnClickListener);
			} else {
				viewActivity.setTouchDisable(false);
				viewActivity.setOnClickListener(null);
				scrollViewMenu.setVisibility(GONE);
				if (menuListener != null)
					menuListener.closeMenu();
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	};

    private AnimatorSet buildTranslationXDownAnimation(View target, float translationX) {

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(ObjectAnimator.ofFloat(target, "translationX", translationX));

        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator));
        scaleDown.setDuration(250);
        return scaleDown;
    }

    private AnimatorSet buildTranslationXUpAnimation(View target, float translationX) {

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(ObjectAnimator.ofFloat(target, "translationX", translationX));

        scaleUp.setDuration(250);
        return scaleUp;
    }

	/**
	 * a helper method to build scale down animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleDownAnimation(View target, float targetScaleX, float targetScaleY) {

		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator));
		scaleDown.setDuration(250);
		return scaleDown;
	}

	/**
	 * a helper method to build scale up animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX,
			float targetScaleY) {

		AnimatorSet scaleUp = new AnimatorSet();
		scaleUp.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleUp.setDuration(250);
		return scaleUp;
	}

	private AnimatorSet buildMenuAnimation(View target, float alpha) {

		AnimatorSet alphaAnimation = new AnimatorSet();
		alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha",
				alpha));

		alphaAnimation.setDuration(250);
		return alphaAnimation;
	}

	/**
	 * if there ware some view you don't want reside menu to intercept their
	 * touch event,you can use the method to set.
	 * 
	 * @param v
	 */
	public void addIgnoredView(View v) {
		ignoredViews.add(v);
	}

	/**
	 * remove the view from ignored view list;
	 * 
	 * @param v
	 */
	public void removeIgnoredView(View v) {
		ignoredViews.remove(v);
	}

	/**
	 * clear the ignored view list;
	 */
	public void clearIgnoredViewList() {
		ignoredViews.clear();
	}

	/**
	 * if the motion evnent was relative to the view which in ignored view
	 * list,return true;
	 * 
	 * @param ev
	 * @return
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : ignoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY()))
				return true;
		}
		return false;
	}

	private void setScaleDirectionByRawX(float currentRawX) {
		if (currentRawX < lastRawX)
			setScaleDirection(DIRECTION_RIGHT);
		else
			setScaleDirection(DIRECTION_LEFT);
	}

	private float getTargetScale(float currentRawX) {
		float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX
				: scaleFloatX;

		float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}

	private float lastActionDownX, lastActionDownY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);
		if (currentActivityScaleX == 1.0f)
			setScaleDirectionByRawX(ev.getRawX());

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastActionDownX = ev.getX();
			lastActionDownY = ev.getY();
			isInIgnoredView = isInIgnoredView(ev) && !isOpened();
			pressedState = PRESSED_DOWN;
			break;

		case MotionEvent.ACTION_MOVE:
			if (isInIgnoredView || isInDisableDirection(scaleDirection))
				break;

			if (pressedState != PRESSED_DOWN
					&& pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			int xOffset = (int) (ev.getX() - lastActionDownX);
			int yOffset = (int) (ev.getY() - lastActionDownY);

			if (pressedState == PRESSED_DOWN) {
				if (yOffset > 25 || yOffset < -25) {
					pressedState = PRESSED_MOVE_VERTICAL;
					break;
				}
				if (xOffset < -50 || xOffset > 50) {
					if(lastActionDownX > 50){
						break;
					}
					pressedState = PRESSED_MOVE_HORIZANTAL;
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
			} else if (pressedState == PRESSED_MOVE_HORIZANTAL) {
				/*if(lastActionDownX <50){
					if (currentActivityScaleX < 0.95)
						scrollViewMenu.setVisibility(VISIBLE);
					float targetScale = getTargetScale(ev.getRawX());
					ViewHelper.setScaleX(viewActivity, targetScale);
					ViewHelper.setScaleY(viewActivity, targetScale);
					ViewHelper.setScaleX(imageViewShadow, targetScale + shadowAdjustScaleX);
					ViewHelper.setScaleY(imageViewShadow, targetScale + shadowAdjustScaleY);
					ViewHelper.setAlpha(scrollViewMenu, (1 - targetScale) * 2.0f);
				}*/
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:

			if (isInIgnoredView)
				break;
			if (pressedState != PRESSED_MOVE_HORIZANTAL)
				break;

			pressedState = PRESSED_DONE;
			if (isOpened()) {
				if (currentActivityScaleX > 0.56f)
					closeMenu();
				else
					openMenu(scaleDirection);
			} else {
				if (currentActivityScaleX < 0.94f) {
					openMenu(scaleDirection);
				} else {
					closeMenu();
				}
			}

			break;

		}
		lastRawX = ev.getRawX();
		return super.dispatchTouchEvent(ev);
	}

	public int getScreenHeight() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public int getScreenWidth() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	public void setScaleValue(float scaleValue) {
		this.mScaleValue = scaleValue;
	}

    @Override
    public float getTranslationX() {
        return translationX;
    }

    @Override
    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public interface OnMenuListener {

		/**
		 * the method will call on the finished time of opening menu's
		 * animation.
		 */
		public void openMenu();

		/**
		 * the method will call on the finished time of closing menu's animation
		 * .
		 */
		public void closeMenu();
	}

}
