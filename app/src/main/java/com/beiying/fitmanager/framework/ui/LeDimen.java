package com.beiying.fitmanager.framework.ui;

public class LeDimen {
	
	private static int sCornerSize = -1;
	private static int sLineHeight = -1;
	private static int sUnitBgShadowTop = -1;
	private static int sUnitBgShadowBottom = -1;
	private static int sUnitBgShadowLeft = -1;
	private static int sUnitBgShadowRight = -1;
	private static int sHomeText = -1;
	private static int sTitleSize = -1;
	private static int sTextSize = -1;
	private static int sSubTextSize = -1;
	private static int sNumberTextSize = -1;
	private static int sTextViewHeight = -1;
	private static int sButtonHeight = -1;
	private static int sNaviHotItemHeight = -1;
	private static int sNaviItemHeight = -1;
	private static int sListItemSingleLineHeight = -1;
	private static int sListItemHeight = -1;
	private static int sTitlebarHeight = -1;
	private static int sToolbarHeight = -1;
	private static int sVerticalGap = -1;
	private static int sPadding = -1;
	private static int sDialogTitleSize = -1;
	
	private LeDimen(){}
	
	public static void clear() {
		sCornerSize = -1;
		sLineHeight = -1;
		sUnitBgShadowTop = -1;
		sUnitBgShadowBottom = -1;
		sUnitBgShadowLeft = -1;
		sUnitBgShadowRight = -1;
		sHomeText = -1;
		sTitleSize = -1;
		sTextSize = -1;
		sSubTextSize = -1;
		sTextViewHeight = -1;
		sButtonHeight = -1;
		sNaviHotItemHeight = -1;
		sNaviItemHeight = -1;
		sListItemSingleLineHeight = -1;
		sListItemHeight = -1;
		sTitlebarHeight = -1;
		sToolbarHeight = -1;
		sVerticalGap = -1;
		sPadding = -1;
		sDialogTitleSize = -1;
	}
	
	public static int getTextSize(int level) {
		level = Math.max(0, level);
		level = Math.min(9, level);
		String resName = "textsize_" + Integer.toString(level);
		return Math.round(LeResources.getInstance().getDimension(resName));
	}
	
	public static int getItemSize(int level) {
		level = Math.max(0, level);
		level = Math.min(12, level);
		String resName = "itemsize_" + Integer.toString(level);
		return Math.round(LeResources.getInstance().getDimension(resName));
	}
	
	public static int getPadding(int level) {
		level = Math.max(0, level);
		level = Math.min(6, level);
		String resName = "padding_" + Integer.toString(level);
		return Math.round(LeResources.getInstance().getDimension(resName));
	}
	
	public static int getCornerSize() {
		if (sCornerSize == -1) {
			sCornerSize = Math.round(LeResources.getInstance().getDimension("corner_size"));
		}
		return sCornerSize;
	}
	
	public static int getLineHeight() {
		if (sLineHeight == -1) {
			sLineHeight = Math.round(LeResources.getInstance().getDimension("line_height"));
		}
		return sLineHeight;
	}
	
	public static int getUnitBgShadowTop() {
		if (sUnitBgShadowTop == -1) {
			sUnitBgShadowTop = Math.round(LeResources.getInstance().getDimension("unit_bg_shadow_top"));
		}
		return sUnitBgShadowTop;
	}
	
	public static int getUnitBgShadowLeft() {
		if (sUnitBgShadowLeft == -1) {
			sUnitBgShadowLeft = Math.round(LeResources.getInstance().getDimension("unit_bg_shadow_left"));
		}
		return sUnitBgShadowLeft;
	}
	
	public static int getUnitBgShadowRight() {
		if (sUnitBgShadowRight == -1) {
			sUnitBgShadowRight = Math.round(LeResources.getInstance().getDimension("unit_bg_shadow_right"));
		}
		return sUnitBgShadowRight;
	}
	
	public static int getUnitBgShadowBottom() {
		if (sUnitBgShadowBottom == -1) {
			sUnitBgShadowBottom = (int) LeResources.getInstance().getDimension("unit_bg_shadow_bottom");
		}
		return sUnitBgShadowBottom;
	}
	
	public static int getDialogBtnCornerSize() {
		if (sCornerSize == -1) {
			sCornerSize = (int)LeResources.getInstance().getDimension("dialog_btn_corner_size");
		}
		return sCornerSize;
	}
	
	public static int getDialogTitleSize() {
		if (sDialogTitleSize == -1) {
			sDialogTitleSize = (int)LeResources.getInstance().getDimension("dialog_title_size");
		}
		return sDialogTitleSize;
	}
	
	public static int getHomeText() {
		if (sHomeText == -1) {
			sHomeText = getTextSize(1);
		}
		return sHomeText;
	}
	
	public static int getTitleSize() {
		if (sTitleSize == -1) {
			sTitleSize = getTextSize(4);
		}
		return sTitleSize;
	}
	
	public static int getTextSize() {
		if (sTextSize == -1) {
			sTextSize = getTextSize(3);
		}
		return sTextSize;
	}
	
	public static int getSubTextSize() {
		if (sSubTextSize == -1) {
			sSubTextSize = getTextSize(1);
		}
		return sSubTextSize;
	}

	public static int getNumberTextSize() {
		if (sNumberTextSize == -1) {
			sNumberTextSize = getTextSize(1);
		}
		return sNumberTextSize;
	}
	
	public static int getTextViewHeight() {
		if (sTextViewHeight == -1) {
			sTextViewHeight = getItemSize(2);
		}
		return sTextViewHeight;
	}
	
	public static int getButtonHeight() {
		if (sButtonHeight == -1) {
			sButtonHeight = getItemSize(3);
		}
		return sButtonHeight;
	}
	
	public static int getNaviHotItemHeight() {
		if (sNaviHotItemHeight == -1) {
			sNaviHotItemHeight = getItemSize(4);
		}
		return sNaviHotItemHeight;
	}
	
	public static int getNaviItemHeight() {
		if (sNaviItemHeight == -1) {
			sNaviItemHeight = getItemSize(5);
		}
		return sNaviItemHeight;
	}
	
	public static int getListItemSingleLineHeight() {
		if (sListItemSingleLineHeight == -1) {
			sListItemSingleLineHeight = getItemSize(7);
		}
		return sListItemSingleLineHeight;
	}
	
	public static int getListItemHeight() {
		if (sListItemHeight == -1) {
			sListItemHeight = getItemSize(8);
		}
		return sListItemHeight;
	}
	
	public static int getTitlebarHeight() {
		if (sTitlebarHeight == -1) {
			sTitlebarHeight = getItemSize(6);
		}
		return sTitlebarHeight;
	}
	
	public static int getToolbarHeight() {
		if (sToolbarHeight == -1) {
			sToolbarHeight = getItemSize(6);
		}
		return sToolbarHeight;
	}
	
	public static int getVerticalGap() {
		if (sVerticalGap == -1) {
			sVerticalGap = Math.round(LeResources.getInstance().getDimension("common_vertical_gap"));
		}
		return sVerticalGap;
	}
	
	public static int getPadding() {
		if (sPadding == -1) {
			sPadding = getPadding(4);
		}
		return sPadding;
	}
}
