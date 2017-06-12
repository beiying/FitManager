package com.beiying.fitmanager.datacollect;

import android.text.TextUtils;

public class BYFitnessBikeMetaDataMode {
	private String mIndicatorName;//BMI指标名称
	private String mValue;
	private String mUnit;//计量单位
	
	public BYFitnessBikeMetaDataMode(String indicatorName, String value, String unit){
		this.mIndicatorName = indicatorName;
		this.mValue = value;
		this.mUnit = unit;
	}
	
	public String getIndicatorName() {
		return mIndicatorName;
	}
	public void setIndicatorName(String indicatorName) {
		this.mIndicatorName = indicatorName;
	}
	public String getValue() {
		return mValue;
	}
	public void setValue(String value) {
		this.mValue = value;
	}
	public String getUnit() {
		return mUnit;
	}
	public void setUnit(String unit) {
		this.mUnit = unit;
	}
	
	public String getDescription() {
		if (TextUtils.isEmpty(mUnit)) {
			return mIndicatorName;
		}
		return mIndicatorName + "(" + mUnit +")";
	}
	
	
}
