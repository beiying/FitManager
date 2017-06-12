package com.beiying.fitmanager.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYUserModel;

public class BYNavigationUserView extends LeView {
	private static final int UI_HEIGHT = 150;
	private static final int UI_AVARTAR_HEIGHT = 65;
	private static final int UI_AVARTAR_MARGIN = 25;

	private BYUserModel mUserModel;
	
	private Drawable mBackgroundDrawable;
	private Drawable mAvartarDrawable;
	
	private Paint mPaint;
	
	private int mHeight;
	private int mAvartarHeight;
	private int mAvartarMargin;
	public BYNavigationUserView(Context context,BYUserModel user) {
		super(context);
		
		this.mUserModel = user;
		initResource(context);
		
		initView();
		setWillNotDraw(false);
	}
	private void initView() {
		
	}
	private void initResource(Context context) {
		mHeight = LeUI.getDensityDimen(context, UI_HEIGHT);
		mAvartarHeight = LeUI.getDensityDimen(context, UI_AVARTAR_HEIGHT);
		mAvartarMargin = LeUI.getDensityDimen(context, UI_AVARTAR_MARGIN);
		mBackgroundDrawable = getResources().getDrawable(R.drawable.user_profile_bg);
		mAvartarDrawable = getResources().getDrawable(R.drawable.avartar_user_male);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = mHeight;
		LeLog.e("BY BYUserProfileView onMeasure width="+width+";height="+height);
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//绘制背景
		if (mBackgroundDrawable != null) {
			mBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
			mBackgroundDrawable.draw(canvas);
		}
		
		int offsetX = 0;
		int offsetY = 0;
		
		//绘制头像
		if (mAvartarDrawable != null) {
			int radius = mAvartarHeight / 2;
			offsetX = mAvartarMargin + radius;
			offsetY = mAvartarMargin + radius;
			canvas.drawCircle(offsetX, offsetY, radius, mPaint); 
			
			mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			mAvartarDrawable.setBounds(mAvartarMargin, mAvartarMargin, mAvartarHeight + mAvartarMargin, mAvartarHeight + mAvartarMargin);
			mAvartarDrawable.draw(canvas);
		}
		
		offsetX += mAvartarHeight / 4 * 3;
		offsetY = mAvartarMargin;
		mPaint.setXfermode(null);
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(getResources().getDimension(R.dimen.navigation_user_profile_text_size));
		offsetY -= mPaint.getFontMetrics().top;
		canvas.drawText(mUserModel.mUserName, offsetX, offsetY, mPaint);//用户名
		
		offsetY += LeTextUtil.getPaintHeight(mPaint);
		canvas.drawText(mUserModel.mHeight + " cm", offsetX, offsetY, mPaint);//身高
		
		offsetY += LeTextUtil.getPaintHeight(mPaint);
		canvas.drawText(mUserModel.mWeight + " kg", offsetX, offsetY, mPaint);//体重
	}

}
