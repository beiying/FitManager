package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

public class LeTab extends LeFrameViewGroup implements View.OnClickListener, LeGallery.LeGalleryListener {
	public static final int UI_HEIGHT = 45;
	private static final int UI_TAB_BUTTON_PADDING_X = 0;
	private static final int UI_TAB_BUTTON_PADDING_Y = 0;
	private static final int UI_SHADOW_SIZE = 0;
	private static final int UI_DEFAULT_MINIMUM_ITEM_WIDTH = 100;
	private static final int UI_TEXT = 22;
	
	protected int mTextSize;
	protected int mSelectedTextSize;
	
	public LeGallery mGallery;
	private HorizontalScrollView mScrollView;
	private LeTabContent mTabContent;
	private List<LeTabButton> mTabButtons;
	private LeIndicator mIndicator;
	
	private int mHeight;
	private int mTabButtonLeftPaddingX;
	private int mTabButtonRightPaddingX;
	private int mTabButtonPaddingY;
	private int mMinimumItemWidth;
	
	private LeTabListener mListener;

	public LeTab(Context context) {
		super(context);

		setWillNotDraw(false);
		initResources();
		onThemeChanged();
		initViews();
	}
	
	private void initResources() {
		mTabButtonLeftPaddingX = LeUI.getDensityDimen(getContext(), UI_TAB_BUTTON_PADDING_X);
		mTabButtonPaddingY = LeUI.getDensityDimen(getContext(), UI_TAB_BUTTON_PADDING_Y);
		mMinimumItemWidth = LeUI.getDensityDimen(getContext(), UI_DEFAULT_MINIMUM_ITEM_WIDTH);
		mSelectedTextSize = LeUI.getDensityDimen(getContext(), UI_TEXT);
		mTextSize = LeUI.getDensityDimen(getContext(), UI_TEXT);
	}
	
	private void initViews() {
		mTabButtons = new ArrayList<LeTabButton>();
		mScrollView = new HorizontalScrollView(getContext());
		mScrollView.setHorizontalFadingEdgeEnabled(false);
		mScrollView.setHorizontalScrollBarEnabled(false);
		addView(mScrollView);
		
		mTabContent = new LeTabContent(getContext());
		mScrollView.addView(mTabContent);
	}
	
	public void setTextCommonColor(int color) {
		for (LeTabButton tabButton : mTabButtons) {
			tabButton.setTextCommonColor(color);
		}
	}
	
	public void setTextSelectColor(int color) {
		for (LeTabButton tabButton : mTabButtons) {
			tabButton.setTextSelectColor(color);
		}
	}
	
	public void setGallery(LeGallery gallery) {
		mGallery = gallery;
		gallery.addGalleryChangeListener(this);
	}
	
	public LeGallery getGallery() {
		return mGallery;
	}
	
	public int getTabItemWidth() {
		if (mTabButtons.size() == 0) {
			return 0;
		}
		return mTabButtons.get(0).getMeasuredWidth();
	}
	
	public List<LeTabButton> getTabButtons() {
		return mTabButtons;
	}
	
	public int getTabCount() {
		if (mTabButtons != null) {
			return mTabButtons.size();
		}
		return 0;
	}
	
	public void setTabOffsetX(int offsetX) {
		mScrollView.scrollTo(offsetX, 0);
	}
	
	public void setHeight(int height) {
		mHeight = height;
	}
	
	public void setMinimumItemWidth(int width) {
		mMinimumItemWidth = width;
	}
	
	public void setTextSize(int textSize) {
		mTextSize = textSize;
		
		for (int i = 0; i < mTabButtons.size(); i++) {
			mTabButtons.get(i).setTextSize(mTextSize);
		}
	}
	
	public void setSelectedTextSize(int selectedTextSize) {
		mSelectedTextSize = selectedTextSize;
	}
	
	public void release() {

	}

	public void addTabButton(LeTabButton tabButton) {
		tabButton.setOnClickListener(this);
		tabButton.setFocusable(true);
		mTabButtons.add(tabButton);
		mTabContent.addView(tabButton, mTabButtons.indexOf(tabButton));
	}
	
	public void addIndicator(LeIndicator indicator) {
		mIndicator = indicator;
		mTabContent.addView(mIndicator);
	}

	public void setSelected(int selected) {
		for (int i = 0; i < mTabButtons.size(); i++) {
			mTabButtons.get(i).setSelected(false);
			mTabButtons.get(i).setTextSize(mTextSize);
		}
		if (mTabButtons.size() > 0) {
			mTabButtons.get(selected).setSelected(true);
		}
	}
	
	public void setTabButtonLeftPaddingX(int paddingX) {
		mTabButtonLeftPaddingX = paddingX;
	}
	
	public void setTabButtonRightPaddingX(int paddingX) {
		mTabButtonRightPaddingX = paddingX;
	}
	
	public void setTabListener(LeTabListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onClick(View v) {
		int selectIndex = mTabButtons.indexOf(v);
		setSelected(selectIndex);
		if (mListener != null) {
			mListener.onTabClick(selectIndex);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = mHeight;
		if (height == 0) {
			height = View.MeasureSpec.getSize(heightMeasureSpec);
		}
		if (height == 0) {
			height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
		}
		setMeasuredDimension(width, height);

		LeUI.measureExactly(mScrollView, width, height);
		LeUI.measureExactly(mTabContent, width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		offsetX = 0;
		offsetY = 0;
		LeUI.layoutViewAtPos(mScrollView, offsetX, offsetY);
	}
	
	public class LeTabContent extends ViewGroup {

		public LeTabContent(Context context) {
			super(context);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			final int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
			final int height = MeasureSpec.getSize(heightMeasureSpec);
			
			if (mTabButtons.size() == 0) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			}
			
			int itemWidth = (screenWidth - mTabButtonLeftPaddingX - mTabButtonRightPaddingX) / mTabButtons.size();
			if (itemWidth < mMinimumItemWidth) {
				itemWidth = mMinimumItemWidth;
			}
			
			final int tabHeight = height - mTabButtonPaddingY * 2;
			final int shadowHeight = LeUI.getDensityDimen(getContext(), UI_SHADOW_SIZE);
			for (int i = 0; i < mTabButtons.size(); i++) {
				LeUI.measureExactly(mTabButtons.get(i), itemWidth, tabHeight - shadowHeight);
			}
			
			final int contentWidth = itemWidth * mTabButtons.size() + mTabButtonLeftPaddingX + mTabButtonRightPaddingX;
			setMeasuredDimension(contentWidth, height);
			
			if (mIndicator != null) {
				LeUI.measureExactly(mIndicator, contentWidth, height);
			}
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			int offsetX, offsetY;

			if (mTabButtons.size() == 0) {
				return;
			}

			offsetX = mTabButtonLeftPaddingX;
			offsetY = mTabButtonPaddingY;
			for (int i = 0; i < mTabButtons.size(); i++) {
				final View tabButton = mTabButtons.get(i);
				mTabButtons.get(i).layout(offsetX, offsetY, offsetX + tabButton.getMeasuredWidth(),
						offsetY + tabButton.getMeasuredHeight());
				offsetX += tabButton.getMeasuredWidth();
			}
			
			if (mIndicator != null) {
				offsetX = 0;
				offsetY = getMeasuredHeight() - mIndicator.getMeasuredHeight();
				LeUI.layoutViewAtPos(mIndicator, offsetX, offsetY);
			}
		}
		
	}
	
	public interface LeTabListener {
		void onTabSelect(int index);
		void onTabClick(int index);
	}

	@Override
	public void onGalleryScreenChanged(View view, int screen) {
	}

	@Override
	public void onGalleryScreenChangeComplete(View view, int screen) {
	}

	@Override
	public void onGalleryScrolled(int l, int t, int oldl, int oldt) {
		changeSelected(l);
		changeTextColor(l);
		changeTextSize(l);
	}
	
	protected void changeSelected(int leftPosition) {
		int galleryWidth = mGallery.getMeasuredWidth() / mGallery.getChildCount();
		if (leftPosition < 0) {
			leftPosition = 0;
		} else if (leftPosition > mGallery.getMeasuredWidth() - galleryWidth) {
			leftPosition = mGallery.getMeasuredWidth() - galleryWidth;
		}
		int currentIndex = (leftPosition + galleryWidth / 2) / galleryWidth;
		setSelected(currentIndex);
		if (mListener != null) {
			mListener.onTabSelect(currentIndex);
		}
	}
	
	protected void changeTextSize(int leftPosition) {
	}
	
	protected void changeTextColor(int leftPosition) {
	}

	@Override
	public void onXChange(int delta) {
	}
}
