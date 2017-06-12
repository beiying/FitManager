package com.beiying.fitmanager.core.charts.interfaces;

import android.graphics.RectF;
import android.view.View;


/**
 * Interfaces that provides everything there is to know about the dimensions,bounds,and ranges 
 * of the chart
 * @author beiying
 *
 */
public interface BYChartInterface {
	public float getOffsetBottom();
	
	public float getOffsetTop();
	
	public float getOffsetLeft();
	
	public float getOffsetRight();
	
	public float getDeltaX();
	
	public float getDeltaY();
	
	public float getYChartMin();
	
	public float getYChartMax();
	
	public int getWidth();
	
	public int getHeight();
	
	public RectF getContentRect();
	
	public View getChartView();
}
