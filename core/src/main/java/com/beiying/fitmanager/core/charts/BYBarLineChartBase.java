package com.beiying.fitmanager.core.charts;

import java.util.ArrayList;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.charts.BYXLabels.XLabelPosition;
import com.beiying.fitmanager.core.charts.BYYLabels.YLabelPosition;
import com.beiying.fitmanager.core.charts.data.BYBarDataSet;
import com.beiying.fitmanager.core.charts.data.BYBarLineScatterCandleData;
import com.beiying.fitmanager.core.charts.data.BYBarLineScatterCandleRadarDataSet;
import com.beiying.fitmanager.core.charts.data.BYDataSet;
import com.beiying.fitmanager.core.charts.data.BYEntry;
import com.beiying.fitmanager.core.charts.data.BYPieDataSet;
import com.beiying.fitmanager.core.charts.util.Legend;
import com.beiying.fitmanager.core.charts.util.LimitLine;
import com.beiying.fitmanager.core.charts.util.LimitLine.LimitLabelPosition;
import com.beiying.fitmanager.core.charts.util.PointD;
import com.beiying.fitmanager.core.charts.util.Utils;
import com.beiying.fitmanager.core.charts.util.Legend.LegendPosition;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Base-class of LineChart, BarChart, ScatterChart and CandleStickChart.
 * 
 * @author beiying
 *
 * @param <T>
 */
public abstract class BYBarLineChartBase<T extends BYBarLineScatterCandleData<? extends BYBarLineScatterCandleRadarDataSet<? extends BYEntry>>>
		extends BYChart<T> {
	/** the maximum number of entried to which values will be drawn */
    protected int mMaxVisibleCount = 100;
    
	/** the width of the grid lines */
    protected float mGridWidth = 1f;

	/** if true, data filtering is enabled */
	protected boolean mFilterData = false;

	/** flag indicating if the y-labels should be drawn or not */
	protected boolean mDrawYLabels = true;

	/** flag indicating if the x-labels should be drawn or not */
	protected boolean mDrawXLabels = true;

	/** flag indicating if the grid background should be drawn or not */
	protected boolean mDrawGridBackground = true;

	/** if true, the y-label entries will always start at zero */
	protected boolean mStartAtZero = true;

	/**
	 * if set to true, the highlight indicator (lines for linechart, dark bar
	 * for barchart) will be drawn upon selecting values.
	 */
	protected boolean mHighLightIndicatorEnabled = true;

	/** flag indicating if the vertical grid should be drawn or not */
	protected boolean mDrawVerticalGrid = true;

	/** flag indicating if the horizontal grid should be drawn or not */
	protected boolean mDrawHorizontalGrid = true;

	/** flag indicating if the chart border rectangle should be drawn or not */
	protected boolean mDrawBorder = true;
	
	/**
     * flag that indicates if pinch-zoom is enabled. if true, both x and y axis
     * can be scaled with 2 fingers, if false, x and y axis can be scaled
     * separately
     */
    protected boolean mPinchZoomEnabled = false;

    /** flat that indicates if double tap zoom is enabled or not */
    protected boolean mDoubleTapToZoomEnabled = true;

    /** if true, dragging is enabled for the chart */
    private boolean mDragEnabled = true;

    /** if true, scaling is enabled for the chart */
    private boolean mScaleEnabled = true;

    /** if true, the y range is predefined */
    protected boolean mFixedYValues = false;

	/**
	 * the object representing the labels on the y-axis, this object is prepared
	 * in the pepareYLabels() method
	 */
	protected BYYLabels mYLabels = new BYYLabels();

	/** the object representing the labels on the x-axis */
	protected BYXLabels mXLabels = new BYXLabels();

	/** paint object for the (by default) lightgrey background of the grid */
	protected Paint mGridBackgroundPaint;
	
	/** paint for the line surrounding the chart */
    protected Paint mBorderPaint;

	/** */
	protected Rect mGridBackground;

	/** paint object for the grid lines */
	protected Paint mGridPaint;

	public BYBarLineChartBase(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		super.init();

		mGridBackgroundPaint = new Paint();
		mGridBackgroundPaint.setStyle(Style.FILL);
		mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
		
		mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(mGridWidth);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);
	}

	@Override
	protected void calculateOffsets() {
		float legendRight = 0f, legendBottom = 0f;

		if (mDrawLegend && mLegend != null
				&& mLegend.getPosition() != LegendPosition.NONE) {
			LegendPosition legendPosition = mLegend.getPosition();

			if (legendPosition == LegendPosition.RIGHT_OF_CHART
					|| legendPosition == LegendPosition.RIGHT_OF_CHART_CENTER) {
				float spacing = Utils.convertDpToPixel(12f);

				legendRight = mLegend.getMaximumEntryLength(mLegendLabelPaint)
						+ mLegend.getFormSize() + mLegend.getFormToTextSpace()
						+ spacing;
				mLegendLabelPaint.setTextAlign(Align.LEFT);
			} else if (legendPosition == LegendPosition.BELOW_CHART_CENTER
					|| legendPosition == LegendPosition.BELOW_CHART_LEFT
					|| legendPosition == LegendPosition.BELOW_CHART_RIGHT) {
				if (mXLabels.getPosition() == XLabelPosition.TOP) {
					legendBottom = mLegendLabelPaint.getTextSize() * 3.5f;
				} else {
					legendBottom = mLegendLabelPaint.getTextSize() * 2.5f;
				}
			}
			mLegend.setOffsetBottom(legendBottom);
			mLegend.setOffsetRight(legendRight);
		}
		LeLog.e("LY BarLineChartBase calculateOffsets legendRight="+legendRight+";legendBottom="+legendBottom);

		float yLeft = 0f, yRight = 0f;
		String label = mYLabels.getLongestLabel();
		float yLabelWidth = Utils.calcTextWidth(mYLabelPaint, label + mUnit
				+ (mYChartMin < 0 ? "----" : "+++"));
		if (mDrawYLabels) {
			YLabelPosition yLabelPosition = mYLabels.getPosition();
			if (yLabelPosition == YLabelPosition.LEFT) {
				yLeft = yLabelWidth;
				mYLabelPaint.setTextAlign(Align.RIGHT);
			} else if (yLabelPosition == YLabelPosition.RIGHT) {
				yRight = yLabelWidth;
				mYLabelPaint.setTextAlign(Align.LEFT);
			} else if (yLabelPosition == YLabelPosition.BOTH_SIDED) {
				yRight = yLabelWidth;
				yLeft = yLabelWidth;
			}
		}
		LeLog.e("LY BarLineChartBase calculateOffsets yLeft="+yLeft+";yRight="+yRight);

		float xTop = 0f, xBottom = 0f;
		float xLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q") * 2f;
		if (mDrawXLabels) {
			XLabelPosition xLabelPosition = mXLabels.getPosition();
			if (xLabelPosition == XLabelPosition.BOTTOM) {
				xBottom = xLabelHeight;
			} else if (xLabelPosition == XLabelPosition.TOP) {
				xTop = xLabelHeight;
			} else if (xLabelPosition == XLabelPosition.BOTH_SIDED) {
				xBottom = xLabelHeight;
				xTop = xLabelHeight;
			}
		}
		LeLog.e("LY BarLineChartBase calculateOffsets xTop="+xTop+";xBottom="+xBottom);

		// all required offsets are calculated, now find largest and apply
		float min = Utils.convertDpToPixel(11f);
		mOffsetBottom = Math.max(min, xBottom + legendBottom);
		mOffsetTop = Math.max(min, xTop);
		mOffsetLeft = Math.max(min, yLeft);
		mOffsetRight = Math.max(min, yRight + legendRight);

		if (mLegend != null) {
			// those offsets are equal for legend and other chart, just apply
			// them
			mLegend.setOffsetTop(mOffsetTop + min / 3f);
			mLegend.setOffsetLeft(mOffsetLeft);
		}
		LeLog.e("LY BarLineChartBase calculateOffsets mOffsetBottom="+mOffsetBottom+";mOffsetTop="+mOffsetTop+";mOffsetLeft="+mOffsetLeft+";mOffsetRight="+mOffsetRight);
		prepareContentRect();

		prepareMatrix();
	}

	/**
	 * Sets up all the matrices that will be used for scaling the coordinates to
	 * the display. Offset and Value-px.
	 */
	private void prepareMatrix() {
		mTrans.prepareMatrixValuePx(this);
		mTrans.prepareMatrixOffset(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mDataNotSet) {
			return;
		}

		long starttime = System.currentTimeMillis();
		LeLog.e("LY BYLineChartBase ondraw width=" + getMeasuredWidth()
				+ ";height=" + getMeasuredHeight() + ";starttime=" + starttime);
		if (mFilterData) {
			mData = getFilterData();
			LeLog.e("FilterTime: " + (System.currentTimeMillis() - starttime)
					+ " ms");
			starttime = System.currentTimeMillis();
		} else {
			mData = getData();
		}

		if (mXLabels.isAdjustXLabelsEnabled()) {
			calcModulus();
		}
		drawGridBackground();

		prepareYLabels();

		// make sure the graph values and grid cannot be drawn outside the
		// content-rect
		int clipRestoreCount = mDrawCanvas.save();
		mDrawCanvas.clipRect(mContentRect);

		drawHorizontalGrid();
		drawVerticalGrid();
		
		drawData();
		
		drawLimitLines();
		
		mDrawCanvas.restoreToCount(clipRestoreCount);
		
//		drawAdditional();//绘制每个坐标点的所在位置的圆圈
		
		drawXLabels();
		
		drawYLabels();
		
		

		canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);//这一步很关键，使图表显示的内容能够在宿主窗口中显示出来

	}

	private T getFilterData() {
		return null;
	}

	/**
	 * Returns the ChartData object that ORIGINALLY has been set for the chart.
	 * It contains all data in an unaltered state, before any filtering
	 * algorithms have been applied.
	 * 
	 * @return
	 */
	public T getData() {
		return mData;
	}

	/**
	 * calculates the modulus for x-labels and grid 计算x轴标签和网格的系数
	 */
	protected void calcModulus() {
		float[] values = new float[9];
		mTrans.getTouchMatrix().getValues(values);

		mXLabels.mXAxisLabelModulus = (int) Math.ceil(mData.getXValCount()
				* mXLabels.mLabelWidth
				/ (mContentRect.width() * values[Matrix.MSCALE_X]));
	}

	/**
	 * draws the grid background
	 */
	protected void drawGridBackground() {
		if (!mDrawGridBackground) {
			return;
		}

		LeLog.e("LY BarLineChartBase drawGridBackground");
		mGridBackground = new Rect((int) mOffsetLeft + 1, (int) mOffsetTop + 1,
				getWidth() - (int) mOffsetRight, getHeight()
						- (int) mOffsetBottom);
		mDrawCanvas.drawRect(mGridBackground, mGridBackgroundPaint);
	}

	/**
	 * draws the horizontal grid
	 */
	protected void drawHorizontalGrid() {
		if (!mDrawHorizontalGrid) {
			return;
		}

		float[] position = new float[2];
		LeLog.e("LY BarLineChartBase drawHorizontalGrid mYLabels.mEntryCount="+mYLabels.mEntryCount);
		for (int i = 0; i < mYLabels.mEntryCount; i++) {
			position[1] = mYLabels.mEntries[i];
			mTrans.pointValuesToPixel(position);
			mDrawCanvas.drawLine(mOffsetLeft, position[1], getMeasuredWidth()
					- mOffsetRight, position[1],
					mGridPaint);
		}
	}
	
	/**
     * draws the vertical grid
     */
    protected void drawVerticalGrid() {
    	if (!mDrawVerticalGrid || mData == null)
            return;

        float[] position = new float[2];
        LeLog.e("LY BarLineChartBase drawVerticalGrid XValCount="+mData.getXValCount()+";XAxisLabelModulus="+mXLabels.mXAxisLabelModulus);
        for (int i = 0; i < mData.getXValCount(); i += mXLabels.mXAxisLabelModulus) {
            position[0] = i;
            mTrans.pointValuesToPixel(position);
            if (position[0] >= mOffsetLeft && position[0] <= getMeasuredWidth()) {
                mDrawCanvas.drawLine(position[0], mOffsetTop, position[0], getMeasuredHeight()
                        - mOffsetBottom, mGridPaint);
            }
        }
    }
    
    /**
     * Draws the limit lines if there are one.
     */
    private void drawLimitLines() {

        ArrayList<LimitLine> limitLines = mData.getLimitLines();

        if (limitLines == null)
            return;

        float[] pts = new float[4];

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            pts[1] = l.getLimit();
            pts[3] = l.getLimit();

            mTrans.pointValuesToPixel(pts);

            pts[0] = 0;
            pts[2] = getWidth();

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            mDrawCanvas.drawLines(pts, mLimitLinePaint);

            // if drawing the limit-value is enabled
            if (l.isDrawValueEnabled()) {

                PointF pos = getPosition(new BYEntry(l.getLimit(), 0));

                // save text align
                Align align = mValuePaint.getTextAlign();

                float xOffset = Utils.convertDpToPixel(4f);
                float yOffset = l.getLineWidth() + xOffset;
                String label = mValueFormatter.getFormattedValue(l.getLimit());

                if (mDrawUnitInChart)
                    label += mUnit;

                if (l.getLabelPosition() == LimitLabelPosition.RIGHT) {

                    mValuePaint.setTextAlign(Align.RIGHT);
                    mDrawCanvas.drawText(label, getWidth() - mOffsetRight
                            - xOffset,
                            pos.y - yOffset, mValuePaint);

                } else {
                    mValuePaint.setTextAlign(Align.LEFT);
                    mDrawCanvas.drawText(label, mOffsetLeft
                            + xOffset,
                            pos.y - yOffset, mValuePaint);
                }

                mValuePaint.setTextAlign(align);
            }
        }
    }
    
    private void drawXLabels() {
    	if (!mDrawXLabels) {
    		return;
    	}
    	
    	float yOffset = Utils.convertDpToPixel(4f);
    	mXLabelPaint.setTypeface(mXLabels.getTypeface());
    	mXLabelPaint.setTextSize(mXLabels.getTextSize());
    	mXLabelPaint.setColor(mXLabels.getTextColor());
    	
    	if (mXLabels.getPosition() == XLabelPosition.TOP) {
    		drawXLabels(getOffsetTop() - yOffset);
    	} else if (mXLabels.getPosition() == XLabelPosition.BOTTOM) {
    		drawXLabels(getHeight() - mOffsetBottom + mXLabels.mLabelHeight + yOffset * 1.5f);
    	} else if (mXLabels.getPosition() == XLabelPosition.BOTTOM_INSIDE) {
    		drawXLabels(getHeight() - mOffsetBottom - yOffset);
    	} else if (mXLabels.getPosition() == XLabelPosition.TOP_INSIDE) {
    		drawXLabels(getOffsetTop() + yOffset + mXLabels.mLabelHeight);
    	} else {
    		drawXLabels(getOffsetTop() - 7);
    		drawXLabels(getMeasuredHeight() - mOffsetBottom + mXLabels.mLabelHeight + yOffset * 1.6f);
    	}
    }
    
    protected void drawXLabels(float yPos) {
    	float[] position = new float[] {0,0};
    	
    	for (int i = 0;i < mData.getXValCount(); i+=mXLabels.mXAxisLabelModulus) {
    		position[0] = i;
    		
    		if (mXLabels.isCenterXLabelsEnabled()) {
    			position[0] += 0.5f;
    		}
    		
    		mTrans.pointValuesToPixel(position);
    		
   		if (position[0] >= mOffsetLeft && position[0] <= getWidth() - mOffsetRight) {
    			String label = mData.getXVals().get(i);
    			
    			//避免第一个和最后一个点的x坐标轴的label绘制时被剪切掉一部分
    			if (mXLabels.isAvoidFirstLastClippingEnabled()) {
    				if (i == mData.getXValCount() - 1) {
    					float labelWidth = Utils.calcTextWidth(mXLabelPaint, label);
    					if (labelWidth > getOffsetRight() * 2 && position[0] + labelWidth > getMeasuredWidth()) {
    						position[0] += labelWidth / 2;
    					}
    				} else if (i == 0) {
    					position[0] += Utils.calcTextWidth(mXLabelPaint, label) / 2;
    				}
    			}
    			mDrawCanvas.drawText(label, position[0], yPos, mXLabelPaint);
    		}
    		
    	}
    }
    
    private void drawYLabels() {
    	if (!mDrawYLabels) {
    		return;
    	}
    	
    	float[] positions = new float[mYLabels.mEntryCount * 2];
    	
    	for (int i = 0;i < positions.length; i+=2) {
    		positions[i + 1] = mYLabels.mEntries[i / 2];
    	}
    	
    	mTrans.pointValuesToPixel(positions);
    	
    	mYLabelPaint.setTypeface(mYLabels.getTypeface());
    	mYLabelPaint.setTextSize(mYLabels.getTextSize());
    	mYLabelPaint.setColor(mYLabels.getTextColor());
    	
    	float xOffset = Utils.convertDpToPixel(5f);
    	float yOffset = Utils.calcTextHeight(mYLabelPaint, "A") / 2.5f;
    	
    	if (mYLabels.getPosition() == YLabelPosition.LEFT) {
    		mYLabelPaint.setTextAlign(Align.RIGHT);
    		drawYLabels(mOffsetLeft - xOffset, positions, yOffset);
    	} else if (mYLabels.getPosition() == YLabelPosition.RIGHT) {

            mYLabelPaint.setTextAlign(Align.LEFT);
            drawYLabels(getWidth() - mOffsetRight + xOffset, positions, yOffset);

        } else if (mYLabels.getPosition() == YLabelPosition.RIGHT_INSIDE) {

            mYLabelPaint.setTextAlign(Align.RIGHT);
            drawYLabels(getWidth() - mOffsetRight - xOffset, positions, yOffset);

        } else if (mYLabels.getPosition() == YLabelPosition.LEFT_INSIDE) {

            mYLabelPaint.setTextAlign(Align.LEFT);
            drawYLabels(mOffsetLeft + xOffset, positions, yOffset);

        } else { // BOTH SIDED Y-AXIS LABELS

            // draw left legend
            mYLabelPaint.setTextAlign(Align.RIGHT);
            drawYLabels(mOffsetLeft - xOffset, positions, yOffset);

            // draw right legend
            mYLabelPaint.setTextAlign(Align.LEFT);
            drawYLabels(getWidth() - mOffsetRight + xOffset, positions, yOffset);
        }
    }
    
    protected void drawYLabels(float xPos, float[]  positions, float yOffset) {
    	for (int i = 0;i < mYLabels.mEntryCount; i++) {
    		String text = mYLabels.getFormattedLabel(i);
    		
    		if (!mYLabels.isDrawTopYLabelEntryEnabled() && i >= mYLabels.mEntryCount - 1) {
    			return;
    		}
    		
    		if (mYLabels.isDrawUnitsInYLabelEnabled()) {
    			mDrawCanvas.drawText(text + mUnit, xPos, positions[i * 2 + 1] + yOffset, mYLabelPaint);
    		} else {
    			mDrawCanvas.drawText(text, xPos, positions[i * 2 + 1] + yOffset, mYLabelPaint);
    		}
    	}
    }
    
    
    /**
     * returns true if the specified point (x-axis) exceeds the limits of what
     * is visible to the right side
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentRight(float p) {
        if (p > mContentRect.right)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (x-axis) exceeds the limits of what
     * is visible to the left side
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentLeft(float p) {
        if (p < mContentRect.left)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (y-axis) exceeds the limits of what
     * is visible on the top
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentTop(float p) {
        if (p < mContentRect.top)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (y-axis) exceeds the limits of what
     * is visible on the bottom
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentBottom(float p) {
        if (p > mContentRect.bottom)
            return true;
        else
            return false;
    }
    
    /**
     * Returns the position (in pixels) the provided Entry has inside the chart
     * view or null, if the provided Entry is null.
     * 
     * @param e
     * @return
     */
    public PointF getPosition(BYEntry e) {

        if (e == null)
            return null;

        float[] vals = new float[] {e.getXIndex(), e.getVal()};

        if (this instanceof BYBarChart) {

            BYBarDataSet set = (BYBarDataSet) mData.getDataSetForEntry(e);
            if (set != null)
                vals[0] += set.getBarSpace() / 2f;
        }

        mTrans.pointValuesToPixel(vals);

        return new PointF(vals[0], vals[1]);
    }

	/**
	 * Sets up the y-axis labels. Computes the desired number of labels between
	 * the two given extremes. Unlike the papareXLabels() method, this method
	 * needs to be called upon every refresh of the view.
	 * 
	 * @return
	 */
	protected void prepareYLabels() {
		float yMin = 0f;
		float yMax = 0f;

		if (mContentRect.width() > 10 && !mTrans.isFullyZoomedOutY()) {
			PointD p1 = getValuesByTouchPoint(mContentRect.left,
					mContentRect.top);
			PointD p2 = getValuesByTouchPoint(mContentRect.left,
					mContentRect.bottom);

			if (!mTrans.isInvertYAxisEnabled()) {
				yMin = (float) p2.y;
				yMax = (float) p1.y;
			} else {
				if (!mStartAtZero)
					yMin = (float) Math.min(p1.y, p2.y);
				else
					yMin = 0;
				yMax = (float) Math.max(p1.y, p2.y);
			}
		} else {

			if (!mTrans.isInvertYAxisEnabled()) {
				yMin = mYChartMin;
				yMax = mYChartMax;
			} else {

				if (!mStartAtZero)
					yMin = (float) Math.min(mYChartMax, mYChartMin);
				else
					yMin = 0;
				yMax = (float) Math.max(mYChartMax, mYChartMin);
			}
		}

		int labelCount = mYLabels.getLabelCount();
		double range = Math.abs(yMax - yMin);
		if (labelCount == 0 || range <= 0) {
			mYLabels.mEntries = new float[] {};
			mYLabels.mEntryCount = 0;
			return;
		}

		double rawInterval = range / labelCount;
		double interval = Utils.roundToNextSignificant(rawInterval);
		double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
		int intervalSigDigit = (int) (interval / intervalMagnitude);
		if (intervalSigDigit > 5) {
			// Use one order of magnitude higher, to avoid intervals like 0.9 or
			// 90
			interval = Math.floor(10 * intervalMagnitude);
		}

		// if the labels should only show min and max
		if (mYLabels.isShowOnlyMinMaxEnabled()) {
			mYLabels.mEntryCount = 2;
			mYLabels.mEntries = new float[2];
			mYLabels.mEntries[0] = mYChartMin;
			mYLabels.mEntries[1] = mYChartMax;

		} else {
			double first = Math.ceil(yMin / interval) * interval;
			double last = Utils.nextUp(Math.floor(yMax / interval) * interval);

			double f;
			int i;
			int n = 0;
			for (f = first; f <= last; f += interval) {
				++n;
			}

			mYLabels.mEntryCount = n;

			if (mYLabels.mEntries.length < n) {
				// Ensure stops contains at least numStops elements.
				mYLabels.mEntries = new float[n];
			}

			for (f = first, i = 0; i < n; f += interval, ++i) {
				mYLabels.mEntries[i] = (float) f;
			}
		}

		if (interval < 1) {
			mYLabels.mDecimals = (int) Math.ceil(-Math.log10(interval));
		} else {
			mYLabels.mDecimals = 0;
		}
	}
	
	/**
     * setup the x-axis labels
     */
    protected void prepareXLabels() {

        StringBuffer a = new StringBuffer();

        int max = (int) Math.round(mData.getXValAverageLength()
                + mXLabels.getSpaceBetweenLabels());

        for (int i = 0; i < max; i++) {
            a.append("h");
        }

        mXLabels.mLabelWidth = Utils.calcTextWidth(mXLabelPaint, a.toString());
        mXLabels.mLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q");
    }
    
    /**
     * Generates an automatically prepared legend depending on the DataSets in
     * the chart and their colors.
     */
    public void prepareLegend() {
    	ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // loop for building up the colors and labels used in the legend
        for (int i = 0; i < mData.getDataSetCount(); i++) {
            BYDataSet<? extends BYEntry> dataSet = mData.getDataSetByIndex(i);

            ArrayList<Integer> clrs = dataSet.getColors();
            int entryCount = dataSet.getEntryCount();

            // if we have a barchart with stacked bars
            if (dataSet instanceof BYBarDataSet && ((BYBarDataSet) dataSet).getStackSize() > 1) {
            	BYBarDataSet bds = (BYBarDataSet) dataSet;
                String[] sLabels = bds.getStackLabels();

                for (int j = 0; j < clrs.size() && j < entryCount && j < bds.getStackSize(); j++) {
                    labels.add(sLabels[j % sLabels.length]);
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-2);
                labels.add(bds.getLabel());
            } else if (dataSet instanceof BYPieDataSet) {
                ArrayList<String> xVals = mData.getXVals();
                BYPieDataSet pds = (BYPieDataSet) dataSet;

                for (int j = 0; j < clrs.size() && j < entryCount && j < xVals.size(); j++) {

                    labels.add(xVals.get(j));
                    colors.add(clrs.get(j));
                }

                // add the legend description label
                colors.add(-2);
                labels.add(pds.getLabel());

            } else { // all others
                for (int j = 0; j < clrs.size() && j < entryCount; j++) {
                    // if multiple colors are set for a DataSet, group them
                    if (j < clrs.size() - 1 && j < entryCount - 1) {
                        labels.add(null);
                    } else { // add label to the last entry
                        String label = mData.getDataSetByIndex(i).getLabel();
                        labels.add(label);
                    }
                    colors.add(clrs.get(j));
                }
            }
        }

        Legend l = new Legend(colors, labels);

        if (mLegend != null) {
            // apply the old legend settings to a potential new legend
            l.apply(mLegend);
        }

        mLegend = l;
    }

	/**
	 * Returns the x and y values in the chart at the given touch point
	 * (encapsulated in a PointD). This method transforms pixel coordinates to
	 * coordinates / values in the chart. This is the opposite method to
	 * getPixelsForValues(...).
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointD getValuesByTouchPoint(float x, float y) {

		// create an array of the touch-point
		float[] pts = new float[2];
		pts[0] = x;
		pts[1] = y;

		mTrans.pixelsToValue(pts);

		double xTouchVal = pts[0];
		double yTouchVal = pts[1];

		return new PointD(xTouchVal, yTouchVal);
	}

	@Override
	public void prepare() {
		if (mDataNotSet) {
			return;
		}
		
		calcMinMax(mFixedYValues);

        prepareYLabels();
        prepareXLabels();
        
        prepareLegend();
        
        calculateOffsets();
	}
	
	@Override
	protected void calcMinMax(boolean fixedValues) {
		super.calcMinMax(fixedValues);
		
		if (!fixedValues) {
			// additional handling for space (default 15% space)
            // float space = Math.abs(mDeltaY / 100f * 15f);
            float space = Math
                    .abs(Math.abs(Math.max(Math.abs(mYChartMax), Math.abs(mYChartMin))) / 100f * 20f);

            if (Math.abs(mYChartMax - mYChartMin) < 0.00001f) {
                if (Math.abs(mYChartMax) < 10f)
                    space = 1f;
                else
                    space = Math.abs(mYChartMax / 100f * 20f);
            }
            
            if (mStartAtZero) {

                if (mYChartMax < 0) {
                    mYChartMax = 0;
                    // calc delta
                    mYChartMin = mYChartMin - space;
                } else {
                    mYChartMin = 0;
                    // calc delta
                    mYChartMax = mYChartMax + space;
                }
            } else {

                mYChartMin = mYChartMin - space / 2f;
                mYChartMax = mYChartMax + space / 2f;
            }
		}
		
		mDeltaY = Math.abs(mYChartMax - mYChartMin);
	}

	@Override
	public void notifyDataSetChanged() {
		if (!mFixedYValues) {
            prepare();
            // prepareContentRect();
            mTrans.prepareMatrixValuePx(this);
        } else {
            calcMinMax(mFixedYValues);
        }
	}
	
	/**
     * if set to true, the vertical grid will be drawn, default: true
     * 
     * @param enabled
     */
    public void setDrawVerticalGrid(boolean enabled) {
        mDrawVerticalGrid = enabled;
    }

    /**
     * if set to true, the horizontal grid will be drawn, default: true
     * 
     * @param enabled
     */
    public void setDrawHorizontalGrid(boolean enabled) {
        mDrawHorizontalGrid = enabled;
    }

    /**
     * returns true if drawing the vertical grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawVerticalGridEnabled() {
        return mDrawVerticalGrid;
    }

    /**
     * returns true if drawing the horizontal grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawHorizontalGridEnabled() {
        return mDrawHorizontalGrid;
    }
    
    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_GRID:
                mGridPaint = p;
                break;
            case PAINT_GRID_BACKGROUND:
                mGridBackgroundPaint = p;
                break;
            case PAINT_BORDER:
                mBorderPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_GRID:
                return mGridPaint;
            case PAINT_GRID_BACKGROUND:
                return mGridBackgroundPaint;
            case PAINT_BORDER:
                return mBorderPaint;
        }

        return null;
    }

}
