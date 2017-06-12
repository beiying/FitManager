package com.beiying.fitmanager.userprofile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.BYSplitLineDrawable;
import com.beiying.fitmanager.framework.model.BYUserModel;

public class BYUserProfileView extends LeView {
	private final static int UI_AVATAR_HEIGHT = 85;
	private final static int UI_ITEM_HEIGHT = 60;
	private final static int UI_PADDING = 10;
	
//	private BYUserProfileItemView mEmalView;
//	private BYUserProfileItemView mCountryView;
//	private BYUserProfileItemView mProvinceView;
//	private BYUserProfileItemView mCityView;
//	private BYUserProfileItemView mAgeView;
//	private BYUserProfileItemView mHeightView;
//	private BYuser

	private int mAvatarHeight;
	private int mItemHeight;
	private int mPadding;
	private int mViewHeight;
	
	private Drawable mBgDrawable;
	private Drawable mAvatarDrawable;
	private BYSplitLineDrawable mLineDrawable;
	
	private Paint mTextPaint;
	private Path mCirclePath;
	
	private BYUserModel mUserModel;
	
	public BYUserProfileView(Context context) {
		super(context);
		
		mUserModel = new BYUserModel();
		mUserModel.mUserName = "beiying";
		mUserModel.mGender = 1;
		mUserModel.mAge = 28;
		mUserModel.mBMI = 90;
		mUserModel.mCity = "上海";
		mUserModel.mCountry = "中国";
		mUserModel.mProvince = "上海";
		mUserModel.mEmail = "277572011@qq.com";
		mUserModel.mHeight = 185;
		mUserModel.mWeight = 75;
		initResource(context);
		initViews(context);
		
//		mSampleView = new SampleView(context);
//		addView(mSampleView);
		setWillNotDraw(false);
	}
	
	
	private void initViews(Context context) {
		addView(new BYUserProfileItemView(context, "邮箱地址", mUserModel.mEmail));
		addView(new BYUserProfileItemView(context, "国家", mUserModel.mCountry));
		addView(new BYUserProfileItemView(context, "省份", mUserModel.mProvince));
		addView(new BYUserProfileItemView(context, "城市", mUserModel.mCity));
		addView(new BYUserProfileItemView(context, "年龄", mUserModel.mAge + " 岁"));
		addView(new BYUserProfileItemView(context, "身高", mUserModel.mHeight + " cm"));
		addView(new BYUserProfileItemView(context, "体重", mUserModel.mWeight + " kg"));
		addView(new BYUserProfileItemView(context, "BMI指数", mUserModel.mBMI+""));
	}

	private void initResource(Context context) {
		mAvatarHeight = LeUI.getDensityDimen(context, UI_AVATAR_HEIGHT);
		mItemHeight = LeUI.getDensityDimen(context, UI_ITEM_HEIGHT);
		mPadding = LeUI.getDensityDimen(context, UI_PADDING);
		mViewHeight = mItemHeight * 11 + mPadding * 3;
		
		mBgDrawable = getResources().getDrawable(R.drawable.card_panel_bg);
		mAvatarDrawable = getResources().getDrawable(R.drawable.avartar_user_male);
		mLineDrawable = new BYSplitLineDrawable(context);
		
		mTextPaint = new Paint();
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(getResources().getDimension(R.dimen.common_text_normal_size));
		
		mCirclePath = new Path();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		for (int i = 0; i<getChildCount();i++) {
			LeUI.measureExactly(getChildAt(i), width - mPadding * 2 - 10, mItemHeight - 2);
		}
		height = height > mViewHeight ? height : mViewHeight;
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX = mPadding;
		int offsetY = mItemHeight * 3 + mPadding * 2;
		for (int i = 0;i < getChildCount();i++) {
			LeUI.layoutViewAtPos(getChildAt(i), offsetX + 5, offsetY);
			offsetY += mItemHeight;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int offsetX = mPadding;
		int offsetY = mPadding; 

		mBgDrawable.setBounds(offsetX, offsetY, getMeasuredWidth() - mPadding, mItemHeight * 3 + mPadding);
		mBgDrawable.draw(canvas);
		
		offsetX += mPadding;
		offsetY += (mItemHeight * 2 - mAvatarHeight) / 2;
		canvas.save();
		canvas.translate(offsetX, offsetY);
		mCirclePath.reset();
		canvas.clipPath(mCirclePath);
		mCirclePath.addCircle(mAvatarHeight / 2, mAvatarHeight / 2, mAvatarHeight / 2, Path.Direction.CCW);
        canvas.clipPath(mCirclePath, Region.Op.REPLACE);
		mAvatarDrawable.setBounds(0, 0, mAvatarHeight, mAvatarHeight);
		mAvatarDrawable.draw(canvas);
		canvas.restore();
		
		mTextPaint.setColor(Color.BLACK);
		int textDy = LeTextUtil.calcYWhenAlignCenter(mItemHeight, mTextPaint);
		
		offsetX = mPadding * 3 + mAvatarHeight;
		offsetY = mPadding + textDy;
		canvas.drawText("用户名：", offsetX, offsetY, mTextPaint);
		mLineDrawable.setBounds(offsetX, mItemHeight + mPadding, getMeasuredWidth() - mPadding * 2, mItemHeight + mPadding + 1);
		mLineDrawable.draw(canvas);
		offsetY = mItemHeight + textDy;
		canvas.drawText(mUserModel.mUserName, offsetX, offsetY, mTextPaint);
		mLineDrawable.setBounds(mPadding * 2, mItemHeight * 2, getMeasuredWidth() - mPadding * 2, mItemHeight * 2 + 1);
		mLineDrawable.draw(canvas);
		
		
		offsetX = mPadding;
		offsetY = mItemHeight * 3 + mPadding * 2;
		mBgDrawable.setBounds(offsetX, offsetY, getMeasuredWidth() - mPadding, offsetY + mItemHeight * 8);
		mBgDrawable.draw(canvas);
		
		
//		offsetX += mPadding;
//		offsetY += textDy;
//		canvas.drawText("邮箱地址", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 4 + mPadding * 2 + textDy);
//		canvas.drawText("国家", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 5 + mPadding * 2 + textDy);
//		canvas.drawText("省份", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 6 + mPadding * 2 + textDy);
//		canvas.drawText("城市", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 7 + mPadding * 2 + textDy);
//		canvas.drawText("年龄", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 8 + mPadding * 2 + textDy);
//		canvas.drawText("身高", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 9 + mPadding * 2 + textDy);
//		canvas.drawText("体重", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 10 + mPadding * 2 + textDy);
//		canvas.drawText("BMI指数", offsetX, offsetY, mTextPaint);

//		offsetX = mPadding * 5 + (int) mTextPaint.measureText("邮箱地址");
//		offsetY =mItemHeight * 3 + mPadding * 2 + textDy;
//		canvas.drawText(mUserModel.mEmail, offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 4 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mCountry, offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 5 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mProvince, offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 6 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mCity, offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 7 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mAge + " 岁", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 8 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mHeight + " cm", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 9 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mWeight + " kg", offsetX, offsetY, mTextPaint);
//		offsetY = (mItemHeight * 10 + mPadding * 2 + textDy);
//		canvas.drawText(mUserModel.mBMI + "", offsetX, offsetY, mTextPaint);
		
		offsetX = mPadding * 2;
		offsetY = mItemHeight * 4 + mPadding * 2;
		for (int i = 0; i < 7; i++) {
			mLineDrawable.setBounds(offsetX, offsetY, getMeasuredWidth() - mPadding * 2, offsetY + 1);
			mLineDrawable.draw(canvas);
			offsetY += mItemHeight;
		}
		
		
		
		
//		canvas.drawText(mUserModel.mCountry, offsetX, offsetY, mTextPaint);
	}
	
	private class BYUserProfileItemView extends LeView {
        private Paint mProfilePaint;

        private String mProfileName;
        private String mProfileValue;
        public BYUserProfileItemView(Context context, String name, String value) {
            super(context);
            setClickable(true);
            
            mProfilePaint = new Paint();
            mProfilePaint.setAntiAlias(true);
            mProfilePaint.setTextSize(getResources().getDimension(R.dimen.common_text_normal_size));

            this.mProfileName = name;
            this.mProfileValue = value;
            
            setWillNotDraw(false);
        }
        
        @Override 
        protected void onDraw(Canvas canvas) {
        	if (isPressed()) {
    			canvas.drawColor(getResources().getColor(R.color.common_clicked_bg_color));
    		} else {
    			canvas.drawColor(Color.WHITE);
    		}
        	
        	int offsetX = 0;
        	int offsetY = 0;
        	offsetX = mPadding;
        	offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mProfilePaint);
        	canvas.drawText(mProfileName, offsetX, offsetY, mProfilePaint);
        	
        	offsetX += mProfilePaint.measureText("邮箱地址") + mPadding * 3;
        	canvas.drawText(mProfileValue, offsetX, offsetY, mProfilePaint);
        }
    }

}
