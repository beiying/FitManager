package com.beiying.fitmanager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.beiying.fitmanager.a.test.LPreviewUtilsBase;

import java.util.ArrayList;

public class BYBaseActivity extends ActionBarActivity {

	// allows access to L-Preview APIs through an abstract interface so we can
	// compile with
	// both the L Preview SDK and with the API 19 SDK
	private LPreviewUtilsBase mLPreviewUtils;

	// Navigation drawer:
	protected DrawerLayout mDrawerLayout;

	private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
	private ObjectAnimator mStatusBarColorAnimator;

	// list of navdrawer items that were actually added to the navdrawer, in
	// order
	private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

	// symbols for navdrawer items (indices must correspond to array below).
	// This is
	// not a list of items that are necessarily *present* in the Nav Drawer;
	// rather,
	// it's a list of all possible items.

	protected static final int NAVDRAWER_ITEM_NATIVE_CARDSLIB = 0;
	protected static final int NAVDRAWER_ITEM_CARDSLIB_V1 = 1;
	protected static final int NAVDRAWER_ITEM_GUIDELINES = 2;
	protected static final int NAVDRAWER_ITEM_GITHUB = 3;
	protected static final int NAVDRAWER_ITEM_DONATE = 4;
	protected static final int NAVDRAWER_ITEM_INFO = 5;

	protected static final int NAVDRAWER_ITEM_INVALID = -1;
	protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
	protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

	private ViewGroup mDrawerItemsListContainer;

	// views that correspond to each navdrawer item, null if not yet created
	private View[] mNavDrawerItemViews = null;

	// titles for navdrawer items (indices must correspond to the above)
	private static final int[] NAVDRAWER_TITLE_RES_ID = new int[] {
			R.string.navdrawer_item_native_cardslib,
			R.string.navdrawer_item_cardslib_v1,
			R.string.navdrawer_item_guidelines, R.string.navdrawer_item_github,
			R.string.navdrawer_item_donate, R.string.navdrawer_item_info };

	// icons for navdrawer items (indices must correspond to above array)
	private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_warning_black_36dp, R.drawable.ic_github,
			R.drawable.ic_money, R.drawable.ic_l_info };

	private int mThemedStatusBarColor;
	private int mNormalStatusBarColor;

	// Durations for certain animations we use:
	private static final int HEADER_HIDE_ANIM_DURATION = 300;

	// When set, these components will be shown/hidden in sync with the action
	// bar
	// to implement the "quick recall" effect (the Action Bar and the header
	// views disappear
	// when you scroll down a list, and reappear quickly when you scroll up).
	private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

	// delay to launch nav drawer item, to allow close animation to play
	private static final int NAVDRAWER_LAUNCH_DELAY = 250;

	// fade in and fade out durations for the main content when switching
	// between
	// different Activities of the app through the Nav Drawer
	private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
	private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

	// Primary toolbar and drawer toggle
	private Toolbar mActionBarToolbar;

	// variables that control the Action Bar auto hide behavior (aka
	// "quick recall")
	private boolean mActionBarAutoHideEnabled = false;
	private boolean mActionBarShown = true;

	// A Runnable that we should execute when the navigation drawer finishes its
	// closing animation
	private Runnable mDeferredOnDrawerClosedRunnable;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLPreviewUtils = LPreviewUtilsBase.getInstance(this);

		mThemedStatusBarColor = getResources().getColor(
				R.color.demo_colorPrimaryDark);
		mNormalStatusBarColor = mThemedStatusBarColor;

		mHandler = new Handler();
	}

	@Override
	public void setContentView(View view) {
		// TODO Auto-generated method stub
		super.setContentView(view);
		getActionBarToolbar();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupNavDrawer();// add navigation drawer
	}

	private void setupNavDrawer() {
		// what nav drawer item should be selected
		int selfItem = getSelfNavDrawerItem();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout == null) {
			return;
		}

		mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(
				R.color.demo_colorPrimaryDark));

		if (selfItem == NAVDRAWER_ITEM_INVALID) {
			// do not show a nav drawer
			View navDrawer = mDrawerLayout.findViewById(R.id.navdrawer);
			if (navDrawer != null) {
				((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
			}

			mDrawerLayout = null;
			return;
		}

		if (mActionBarToolbar != null) {
			mActionBarToolbar
					.setNavigationIcon(R.drawable.ic_navigation_drawer);
			mActionBarToolbar
					.setNavigationOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mDrawerLayout.openDrawer(Gravity.START);
						}
					});
		}

		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int newState) {
				onNavDrawerStateChanged(isNavDrawerOpen(),
						newState != DrawerLayout.STATE_IDLE);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				onNavDrawerSlide(slideOffset);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				onNavDrawerStateChanged(true, false);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				if (mDeferredOnDrawerClosedRunnable != null) {
					mDeferredOnDrawerClosedRunnable.run();
					mDeferredOnDrawerClosedRunnable = null;
				}

				onNavDrawerStateChanged(false, false);
			}
		});

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

		populateNavDrawer();

		// When the user runs the app for the first time, we want to land them
		// with the
		// navigation drawer open. But just the first time.
		// if (!PrefUtils.isWelcomeDone(this)) {
		// // first run of the app starts with the nav drawer open
		// PrefUtils.markWelcomeDone(this);
		// mDrawerLayout.openDrawer(Gravity.START);
		// }
	}

	private void populateNavDrawer() {
		mNavDrawerItems.clear();

		// Explore is always shown
		mNavDrawerItems.add(NAVDRAWER_ITEM_NATIVE_CARDSLIB);
		mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
		mNavDrawerItems.add(NAVDRAWER_ITEM_CARDSLIB_V1);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR_SPECIAL);

		mNavDrawerItems.add(NAVDRAWER_ITEM_GUIDELINES);

		mNavDrawerItems.add(NAVDRAWER_ITEM_GITHUB);
		mNavDrawerItems.add(NAVDRAWER_ITEM_DONATE);
		mNavDrawerItems.add(NAVDRAWER_ITEM_INFO);

		createNavDrawerItems();
	}

	private void createNavDrawerItems() {
		mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
		if (mDrawerItemsListContainer == null) {
			return;
		}

		mNavDrawerItemViews = new View[mNavDrawerItems.size()];
		mDrawerItemsListContainer.removeAllViews();
		int i = 0;
		for (int itemId : mNavDrawerItems) {
			mNavDrawerItemViews[i] = makeNavDrawerItem(itemId,
					mDrawerItemsListContainer);
			mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
			i++;
		}
	}

	private View makeNavDrawerItem(final int itemId, ViewGroup container) {
		boolean selected = getSelfNavDrawerItem() == itemId;
		int layoutToInflate = 0;
		if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
			layoutToInflate = R.layout.navdrawer_separator;
		} else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
			layoutToInflate = R.layout.navdrawer_separator;
		} else {
			layoutToInflate = R.layout.navdrawer_item;
		}
		View view = getLayoutInflater().inflate(layoutToInflate, container,
				false);

		if (isSeparator(itemId)) {
			// we are done
			// UIUtils.setAccessibilityIgnore(view);
			return view;
		}

		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		TextView titleView = (TextView) view.findViewById(R.id.title);
		int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ? NAVDRAWER_ICON_RES_ID[itemId]
				: 0;
		int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ? NAVDRAWER_TITLE_RES_ID[itemId]
				: 0;

		// set icon and text
		iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
		if (iconId > 0) {
			iconView.setImageResource(iconId);
		}
		titleView.setText(getString(titleId));

		formatNavDrawerItem(view, itemId, selected);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// onNavDrawerItemClicked(itemId);
			}
		});

		return view;
	}

	private void onNavDrawerItemClicked(final int itemId) {
		if (itemId == getSelfNavDrawerItem()) {
			mDrawerLayout.closeDrawer(Gravity.START);
			return;
		}

		if (isSpecialItem(itemId)) {
			goToNavDrawerItem(itemId);
		} else {
			// launch the target Activity after a short delay, to allow the
			// close animation to play
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					goToNavDrawerItem(itemId);
				}
			}, NAVDRAWER_LAUNCH_DELAY);

			// change the active item on the list so the user can see the item
			// changed
			setSelectedNavDrawerItem(itemId);

			// fade out the main content
			View mainContent = findViewById(R.id.container);
			if (mainContent != null) {
				mainContent.animate().alpha(0)
						.setDuration(MAIN_CONTENT_FADEOUT_DURATION);
			}
		}

		mDrawerLayout.closeDrawer(Gravity.START);
	}

	private void goToNavDrawerItem(int item) {
		Intent intent = null;
		switch (item) {
		case NAVDRAWER_ITEM_NATIVE_CARDSLIB:
			// intent = new Intent(this, NativeMenuActivity.class);
			startActivity(intent);
			finish();
			break;
		case NAVDRAWER_ITEM_CARDSLIB_V1:
			// intent = new Intent(this, V1MenuActivity.class);
			startActivity(intent);
			finish();
			break;
		case NAVDRAWER_ITEM_GUIDELINES:
			// intent = new Intent(this, GuidelinesActivity.class);
			startActivity(intent);
			finish();
			break;
		case NAVDRAWER_ITEM_DONATE:
			// IabUtil.showBeer(this, mHelper);
			break;
		case NAVDRAWER_ITEM_INFO:
			// Utils.showAbout(this);
			break;
		case NAVDRAWER_ITEM_GITHUB:
			String url = "https://github.com/gabrielemariotti/cardslib/";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			break;
		}
	}

	/**
	 * Sets up the given navdrawer item's appearance to the selected state.
	 * Note: this could also be accomplished (perhaps more cleanly) with
	 * state-based layouts.
	 */
	private void setSelectedNavDrawerItem(int itemId) {
		if (mNavDrawerItemViews != null) {
			for (int i = 0; i < mNavDrawerItemViews.length; i++) {
				if (i < mNavDrawerItems.size()) {
					int thisItemId = mNavDrawerItems.get(i);
					formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId,
							itemId == thisItemId);
				}
			}
		}
	}

	private void formatNavDrawerItem(View view, int itemId, boolean selected) {
		if (isSeparator(itemId)) {
			// not applicable
			return;
		}

		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		TextView titleView = (TextView) view.findViewById(R.id.title);

		// configure its appearance according to whether or not it's selected
		titleView.setTextColor(selected ? getResources().getColor(
				R.color.navdrawer_text_color_selected) : getResources()
				.getColor(R.color.navdrawer_text_color));
		iconView.setColorFilter(selected ? getResources().getColor(
				R.color.navdrawer_icon_tint_selected) : getResources()
				.getColor(R.color.navdrawer_icon_tint));
	}

	private void updateStatusBarForNavDrawerSlide(float slideOffset) {

	}

	protected void onNavDrawerSlide(float offset) {
	}

	// Subclasses can override this for custom behavior
	protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
		if (mActionBarAutoHideEnabled && isOpen) {
			// autoShowOrHideActionBar(true);
		}
	}

	protected void autoShowOrHideActionBar(boolean show) {
		if (show == mActionBarShown) {
			return;
		}
		mActionBarShown = show;
		onActionBarAutoShowOrHide(show);

	}

	protected void onActionBarAutoShowOrHide(boolean shown) {
		if (mStatusBarColorAnimator != null) {
			mStatusBarColorAnimator.cancel();
		}
		mStatusBarColorAnimator = ObjectAnimator.ofInt(
				(mDrawerLayout != null) ? mDrawerLayout : mLPreviewUtils,
				(mDrawerLayout != null) ? "statusBarBackgroundColor"
						: "statusBarColor",
				shown ? Color.BLACK : mNormalStatusBarColor,
				shown ? mNormalStatusBarColor : Color.BLACK).setDuration(250);
		if (mDrawerLayout != null) {
			mStatusBarColorAnimator
					.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(
								ValueAnimator valueAnimator) {
							ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
						}
					});
		}
		mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
		mStatusBarColorAnimator.start();

		for (View view : mHideableHeaderViews) {
			if (shown) {
				view.animate().translationY(0).alpha(1)
						.setDuration(HEADER_HIDE_ANIM_DURATION)
						.setInterpolator(new DecelerateInterpolator());
			} else {
				view.animate().translationY(-view.getBottom()).alpha(0)
						.setDuration(HEADER_HIDE_ANIM_DURATION)
						.setInterpolator(new DecelerateInterpolator());
			}
		}
	}

	private boolean isSpecialItem(int itemId) {
		return itemId == NAVDRAWER_ITEM_DONATE || itemId == NAVDRAWER_ITEM_INFO
				|| itemId == NAVDRAWER_ITEM_GITHUB;
	}

	protected boolean isNavDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(Gravity.START);
	}

	private boolean isSeparator(int itemId) {
		return itemId == NAVDRAWER_ITEM_SEPARATOR
				|| itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
	}

	/**
	 * Returns the navigation drawer item that corresponds to this Activity.
	 * Subclasses of BaseActivity override this to indicate what nav drawer item
	 * corresponds to them Return NAVDRAWER_ITEM_INVALID to mean that this
	 * Activity should not have a Nav Drawer.
	 */
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_INVALID;
	}

	public LPreviewUtilsBase getLPreviewUtils() {
		return mLPreviewUtils;
	}

	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
	}

}
