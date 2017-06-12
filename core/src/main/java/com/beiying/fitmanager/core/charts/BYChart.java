package com.beiying.fitmanager.core.charts;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.view.View;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.charts.data.BYChartData;
import com.beiying.fitmanager.core.charts.data.BYDataSet;
import com.beiying.fitmanager.core.charts.data.BYEntry;
import com.beiying.fitmanager.core.charts.interfaces.BYChartInterface;
import com.beiying.fitmanager.core.charts.interfaces.BYOnChartGestureListener;
import com.beiying.fitmanager.core.charts.interfaces.BYOnChartValueSelectedListener;
import com.beiying.fitmanager.core.charts.util.Highlight;
import com.beiying.fitmanager.core.charts.util.Legend;
import com.beiying.fitmanager.core.charts.util.SelInfo;
import com.beiying.fitmanager.core.charts.util.Utils;
import com.beiying.fitmanager.core.charts.util.ValueFormatter;
import com.beiying.fitmanager.core.ui.LeView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class BYChart<T extends BYChartData<? extends BYDataSet<? extends BYEntry>>>
		extends LeView implements AnimatorUpdateListener, BYChartInterface {

	public static final String LOG_TAG = "BYChart";
	public static final float OFFSET = 12f;

	/** flag that indicates if logging is enabled or not */
	protected boolean mLogEnabled = false;

	/**
	 * string that is drawn next to the values in the chart, indicating their
	 * unit
	 */
	protected String mUnit = "";

	/** custom formatter that is used instead of the auto-formatter if set */
	protected ValueFormatter mValueFormatter = null;

	/**
	 * flag that indicates if the default formatter should be used or if a
	 * custom one is set
	 */
	private boolean mUseDefaultFormatter = true;

	/** chart offset to the left */
	protected float mOffsetLeft = 0;

	/** chart toffset to the top */
	protected float mOffsetTop = 0;

	/** chart offset to the right */
	protected float mOffsetRight = 0;

	/** chart offset to the bottom */
	protected float mOffsetBottom = 0;

	/**
	 * object that holds all data that was originally set for the chart, before
	 * it was modified or any filtering algorithms had been applied
	 */
	protected T mData = null;

	/**
	 * Bitmap object used for drawing. This is necessary because hardware
	 * acceleration uses OpenGL which only allows a specific texture size to be
	 * drawn on the canvas directly.
	 **/
	protected Bitmap mDrawBitmap;

	/** the canvas that is used for drawing on the bitmap */
	protected Canvas mDrawCanvas;

	/** the lowest value the chart can display */
	protected float mYChartMin = 0.0f;

	/** the highest value the chart can display */
	protected float mYChartMax = 0.0f;

	/** paint object used for drawing the bitmap */
	protected Paint mDrawPaint;

	/** paint for the x-label values */
	protected Paint mXLabelPaint;

	/** paint for the y-label values */
	protected Paint mYLabelPaint;

	/** paint used for highlighting values */
	protected Paint mHighlightPaint;

	/**
	 * paint object used for drawing the description text in the bottom right
	 * corner of the chart
	 */
	protected Paint mDescPaint;

	/**
	 * paint object for drawing the information text when there are no values in
	 * the chart
	 */
	protected Paint mInfoPaint;

	/**
	 * paint object for drawing values (text representing values of chart
	 * entries)
	 */
	protected Paint mValuePaint;

	/** this is the paint object used for drawing the data onto the chart */
	protected Paint mRenderPaint;

	/** paint for the legend labels */
	protected Paint mLegendLabelPaint;

	/** paint used for the legend forms */
	protected Paint mLegendFormPaint;

	/** paint used for the limit lines */
	protected Paint mLimitLinePaint;

	/** description text that appears in the bottom right corner of the chart */
	protected String mDescription = "Description.";

	/** flag that indicates if the chart has been fed with data yet */
	protected boolean mDataNotSet = true;

	/** if true, units are drawn next to the values in the chart */
	protected boolean mDrawUnitInChart = false;

	/** the range of y-values the chart displays */
	protected float mDeltaY = 1f;

	/** the number of x-values the chart displays */
	protected float mDeltaX = 1f;

	/** if true, touch gestures are enabled on the chart */
	protected boolean mTouchEnabled = true;

	/** if true, y-values are drawn on the chart */
	protected boolean mDrawYValues = true;

	/** if true, value highlightning is enabled */
	protected boolean mHighlightEnabled = true;

	/** flag indicating if the legend is drawn of not */
	protected boolean mDrawLegend = true;

	/** flag that indicates if offsets calculation has already been done or not */
	protected boolean mOffsetsCalculated = false;

	/** this rectangle defines the area in which graph values can be drawn */
	protected RectF mContentRect = new RectF();

	/** the legend object containing all data associated with the legend */
	protected Legend mLegend;

	/**
	 * Transformer object used to transform values to pixels and the other way
	 * around
	 */
	protected BYTransformer mTrans;

	/** listener that is called when a value on the chart is selected */
	protected BYOnChartValueSelectedListener mSelectionListener;

	/** text that is displayed when the chart is empty */
	private String mNoDataText = "No chart data available.";

	/**
	 * Gesture listener for custom callbacks when making gestures on the chart.
	 */
	private BYOnChartGestureListener mGestureListener;

	/**
	 * text that is displayed when the chart is empty that describes why the
	 * chart is empty
	 */
	private String mNoDataTextDescription;

	/** default constructor for initialization in code */
	public BYChart(Context context) {
		super(context);
		init();
	}

	protected void init() {
		setWillNotDraw(false);

		mTrans = new BYTransformer();

		Utils.init(getContext().getResources());

		mOffsetLeft = Utils.convertDpToPixel(OFFSET);
		mOffsetTop = Utils.convertDpToPixel(OFFSET);
		mOffsetRight = Utils.convertDpToPixel(OFFSET);
		mOffsetBottom = Utils.convertDpToPixel(OFFSET);

		mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRenderPaint.setStyle(Style.FILL);

		mDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDescPaint.setColor(Color.BLACK);
		mDescPaint.setTextAlign(Align.RIGHT);
		mDescPaint.setTextSize(Utils.convertDpToPixel(9f));

		mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInfoPaint.setColor(Color.rgb(247, 189, 51)); // orange
		mInfoPaint.setTextAlign(Align.CENTER);
		mInfoPaint.setTextSize(Utils.convertDpToPixel(12f));

		mDrawPaint = new Paint(Paint.DITHER_FLAG);

		mXLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mXLabelPaint.setColor(Color.BLACK);
		mXLabelPaint.setTextAlign(Align.CENTER);
		mXLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

		mYLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mYLabelPaint.setColor(Color.BLACK);
		mYLabelPaint.setTextSize(Utils.convertDpToPixel(10f));

		mLegendFormPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLegendFormPaint.setStyle(Paint.Style.FILL);
		mLegendFormPaint.setStrokeWidth(3f);

		mLegendLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9f));

		mLimitLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLimitLinePaint.setStyle(Paint.Style.STROKE);
	}

	/** paint for the grid lines (only line and barchart) */
	public static final int PAINT_GRID = 3;

	/** paint for the grid background (only line and barchart) */
	public static final int PAINT_GRID_BACKGROUND = 4;

	/** paint for the y-legend values (only line and barchart) */
	public static final int PAINT_YLABEL = 5;

	/** paint for the x-legend values (only line and barchart) */
	public static final int PAINT_XLABEL = 6;

	/**
	 * paint for the info text that is displayed when there are no values in the
	 * chart
	 */
	public static final int PAINT_INFO = 7;

	/** paint for the value text */
	public static final int PAINT_VALUES = 8;

	/** paint for the inner circle (linechart) */
	public static final int PAINT_CIRCLES_INNER = 10;

	/** paint for the description text in the bottom right corner */
	public static final int PAINT_DESCRIPTION = 11;

	/** paint for the line surrounding the chart (only line and barchart) */
	public static final int PAINT_BORDER = 12;

	/** paint for the hole in the middle of the pie chart */
	public static final int PAINT_HOLE = 13;

	/** paint for the text in the middle of the pie chart */
	public static final int PAINT_CENTER_TEXT = 14;

	/** paint for highlightning the values of a linechart */
	public static final int PAINT_HIGHLIGHT = 15;

	/** paint object used for the limit lines */
	public static final int PAINT_RADAR_WEB = 16;

	/** paint used for all rendering processes */
	public static final int PAINT_RENDER = 17;

	/** paint used for the legend */
	public static final int PAINT_LEGEND_LABEL = 18;

	/** paint object used for the limit lines */
	public static final int PAINT_LIMIT_LINE = 19;

	/**
	 * set a new paint object for the specified parameter in the chart e.g.
	 * Chart.PAINT_VALUES
	 * 
	 * @param p
	 *            the new paint object
	 * @param which
	 *            Chart.PAINT_VALUES, Chart.PAINT_GRID, Chart.PAINT_VALUES, ...
	 */
	public void setPaint(Paint p, int which) {

		switch (which) {
		case PAINT_INFO:
			mInfoPaint = p;
			break;
		case PAINT_DESCRIPTION:
			mDescPaint = p;
			break;
		case PAINT_VALUES:
			mValuePaint = p;
			break;
		case PAINT_RENDER:
			mRenderPaint = p;
			break;
		case PAINT_LEGEND_LABEL:
			mLegendLabelPaint = p;
			break;
		case PAINT_XLABEL:
			mXLabelPaint = p;
			break;
		case PAINT_YLABEL:
			mYLabelPaint = p;
			break;
		case PAINT_HIGHLIGHT:
			mHighlightPaint = p;
			break;
		case PAINT_LIMIT_LINE:
			mLimitLinePaint = p;
			break;
		}
	}

	/**
	 * Returns the paint object associated with the provided constant.
	 * 
	 * @param which
	 *            e.g. Chart.PAINT_LEGEND_LABEL
	 * @return
	 */
	public Paint getPaint(int which) {
		switch (which) {
		case PAINT_INFO:
			return mInfoPaint;
		case PAINT_DESCRIPTION:
			return mDescPaint;
		case PAINT_VALUES:
			return mValuePaint;
		case PAINT_RENDER:
			return mRenderPaint;
		case PAINT_LEGEND_LABEL:
			return mLegendLabelPaint;
		case PAINT_XLABEL:
			return mXLabelPaint;
		case PAINT_YLABEL:
			return mYLabelPaint;
		case PAINT_HIGHLIGHT:
			return mHighlightPaint;
		case PAINT_LIMIT_LINE:
			return mLimitLinePaint;
		}

		return null;
	}

	/**
	 * Sets a new data object for the chart. The data object contains all values
	 * and information needed for displaying.
	 * 
	 * @param data
	 */
	public void setData(T lineData) {
		if (lineData == null) {
			return;
		}

		mDataNotSet = false;
		mOffsetsCalculated = false;
		mData = lineData;

		prepare();

		// calculate how many digits are needed
		calcFormats();
	}

	/**
	 * calcualtes the y-min and y-max value and the y-delta and x-delta value
	 */
	protected void calcMinMax(boolean fixedValues) {
		// only calculate values if not fixed values
		if (!fixedValues) {
			mYChartMin = mData.getYMin();
			mYChartMax = mData.getYMax();
		}

		// calc delta
		mDeltaY = Math.abs(mYChartMax - mYChartMin);
		mDeltaX = mData.getXVals().size() - 1;
	}
	
	/**
     * array of Highlight objects that reference the highlighted slices in the
     * chart
     */
    protected Highlight[] mIndicesToHightlight = new Highlight[0];

    /**
     * Returns true if there are values to highlight, false if there are no
     * values to highlight. Checks if the highlight array is null, has a length
     * of zero or if the first object is null.
     * 
     * @return
     */
    public boolean valuesToHighlight() {
        return mIndicesToHightlight == null || mIndicesToHightlight.length <= 0
                || mIndicesToHightlight[0] == null ? false
                : true;
    }

    /**
     * Highlights the values at the given indices in the given DataSets. Provide
     * null or an empty array to undo all highlighting. This should be used to
     * programmatically highlight values. This DOES NOT generate a callback to
     * the OnChartValueSelectedListener.
     * 
     * @param highs
     */
    public void highlightValues(Highlight[] highs) {

        // set the indices to highlight
        mIndicesToHightlight = highs;

        // redraw the chart
        invalidate();
    }

    /**
     * Highlights the value at the given x-index in the given DataSet. Provide
     * -1 as the x-index to undo all highlighting.
     * 
     * @param xIndex
     * @param dataSetIndex
     */
    public void highlightValue(int xIndex, int dataSetIndex) {

        if (xIndex < 0 || dataSetIndex < 0 || xIndex >= mData.getXValCount()
                || dataSetIndex >= mData.getDataSetCount()) {

            highlightValues(null);
        } else {
            highlightValues(new Highlight[] {
                    new Highlight(xIndex, dataSetIndex)
            });
        }
    }

    /**
     * Highlights the value selected by touch gesture. Unlike
     * highlightValues(...), this generates a callback to the
     * OnChartValueSelectedListener.
     * 
     * @param highs
     */
    public void highlightTouch(Highlight high) {

        if (high == null)
            mIndicesToHightlight = null;
        else {

            // set the indices to highlight
            mIndicesToHightlight = new Highlight[] {
                    high
            };
        }

        // redraw the chart
        invalidate();

        if (mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {

                BYEntry e = getEntryByDataSetIndex(high.getXIndex(),
                        high.getDataSetIndex());

                // notify the listener
                mSelectionListener.onValueSelected(e, high.getDataSetIndex());
            }
        }
    }

	/**
	 * calculates the required number of digits for the values that might be
	 * drawn in the chart (if enabled)
	 */
	protected void calcFormats() {

	}

	@Override
	public void onAnimationUpdate(ValueAnimator arg0) {

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		prepareContentRect();
		LeLog.e("LY BYLineChart onLayout ");
	}

	protected void prepareContentRect() {
		mContentRect.set(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight,
				getHeight() - mOffsetBottom);
	}

	/**
	 * 在调用onDraw之前
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mDrawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		mDrawCanvas = new Canvas(mDrawBitmap);

		prepareContentRect();
		prepare();

		LeLog.e("LY BYLineChart onLayout ");
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		LeLog.e("LY BYLineChart ondraw width=" + getMeasuredWidth()
				+ ";height=" + getMeasuredHeight());
		if (mDataNotSet) {
			canvas.drawText(mNoDataText, getWidth() / 2, getHeight() / 2,
					mInfoPaint);

			if (!TextUtils.isEmpty(mNoDataTextDescription)) {
				float textOffset = -mInfoPaint.ascent() + mInfoPaint.descent();
				canvas.drawText(mNoDataTextDescription, getWidth() / 2,
						getHeight() / 2 + textOffset, mInfoPaint);
			}

			return;
		}

		if (!mOffsetsCalculated) {
			calculateOffsets();
			mOffsetsCalculated = true;
		}

		if (mDrawBitmap == null || mDrawCanvas == null) {

			mDrawBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.ARGB_4444);
			mDrawCanvas = new Canvas(mDrawBitmap);
		}

		// clear everything
		mDrawBitmap.eraseColor(Color.TRANSPARENT);

	}

	/**
	 * Clears the chart from all data and refreshes it (by calling
	 * invalidate()).
	 */
	public void clear() {
		mData = null;
		mData = null;
		mDataNotSet = true;
		invalidate();
	}

	/**
	 * Returns true if the chart is empty (meaning it's data object is either
	 * null or contains no entries).
	 * 
	 * @return
	 */
	public boolean isEmpty() {

		if (mData == null)
			return true;
		else {

			if (mData.getYValCount() <= 0)
				return true;
			else
				return false;
		}
	}

	/**
	 * Returns the rectangle that defines the borders of the chart-value surface
	 * (into which the actual values are drawn).
	 * 
	 * @return
	 */
	@Override
	public RectF getContentRect() {
		return mContentRect;
	}

	@Override
	public View getChartView() {
		return this;
	}

	/**
	 * Returns the canvas object the chart uses for drawing.
	 * 
	 * @return
	 */
	public Canvas getCanvas() {
		return mDrawCanvas;
	}

	public float getOffsetLeft() {
		return mOffsetLeft;
	}

	public float getOffsetBottom() {
		return mOffsetBottom;
	}

	public float getOffsetRight() {
		return mOffsetRight;
	}

	public float getOffsetTop() {
		return mOffsetTop;
	}

	/**
	 * returns the total value (sum) of all y-values across all DataSets
	 * 
	 * @return
	 */
	public float getYValueSum() {
		return mData.getYValueSum();
	}

	/**
	 * returns the current y-max value across all DataSets
	 * 
	 * @return
	 */
	public float getYMax() {
		return mData.getYMax();
	}

	/**
	 * returns the lowest value the chart can display
	 * 
	 * @return
	 */
	public float getYChartMin() {
		return mYChartMin;
	}

	/**
	 * returns the highest value the chart can display
	 * 
	 * @return
	 */
	public float getYChartMax() {
		return mYChartMax;
	}

	/**
	 * returns the current y-min value across all DataSets
	 * 
	 * @return
	 */
	public float getYMin() {
		return mData.getYMin();
	}

	/**
	 * Get the total number of X-values.
	 * 
	 * @return
	 */
	public float getDeltaX() {
		return mDeltaX;
	}

	/**
	 * Returns the total range of values (on y-axis) the chart displays.
	 * 
	 * @return
	 */
	public float getDeltaY() {
		return mDeltaY;
	}

	/**
	 * returns the average value of all values the chart holds
	 * 
	 * @return
	 */
	public float getAverage() {
		return getYValueSum() / mData.getYValCount();
	}

	/**
	 * returns the average value for a specific DataSet (with a specific label)
	 * in the chart
	 * 
	 * @param dataSetLabel
	 * @return
	 */
	public float getAverage(String dataSetLabel) {

		BYDataSet<? extends BYEntry> ds = mData.getDataSetByLabel(dataSetLabel,
				true);

		return ds.getYValueSum() / ds.getEntryCount();
	}

	/**
	 * returns the total number of values the chart holds (across all DataSets)
	 * 
	 * @return
	 */
	public int getValueCount() {
		return mData.getYValCount();
	}
	
	/**
     * Returns the corresponding Entry object at the given xIndex from the given
     * DataSet. INFORMATION: This method does calculations at runtime. Do not
     * over-use in performance critical situations.
     * 
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    public BYEntry getEntryByDataSetIndex(int xIndex, int dataSetIndex) {
        return mData.getDataSetByIndex(dataSetIndex).getEntryForXIndex(xIndex);
    }

    /**
     * Returns an array of SelInfo objects for the given x-index. The SelInfo
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     * 
     * @param xIndex
     * @return
     */
    public ArrayList<SelInfo> getYValsAtIndex(int xIndex) {

        ArrayList<SelInfo> vals = new ArrayList<SelInfo>();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            // extract all y-values from all DataSets at the given x-index
            float yVal = mData.getDataSetByIndex(i).getYValForXIndex(xIndex);

            if (!Float.isNaN(yVal)) {
                vals.add(new SelInfo(yVal, i));
            }
        }

        return vals;
    }

    /**
     * Get all Entry objects at the given index across all DataSets.
     * INFORMATION: This method does calculations at runtime. Do not over-use in
     * performance critical situations.
     * 
     * @param xIndex
     * @return
     */
    public ArrayList<BYEntry> getEntriesAtIndex(int xIndex) {

        ArrayList<BYEntry> vals = new ArrayList<BYEntry>();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            BYDataSet<? extends BYEntry> set = mData.getDataSetByIndex(i);

            BYEntry e = set.getEntryForXIndex(xIndex);

            if (e != null) {
                vals.add(e);
            }
        }

        return vals;
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
     * returns the percentage the given value has of the total y-value sum
     * 
     * @param val
     * @return
     */
    public float getPercentOfTotal(float val) {
        return val / mData.getYValueSum() * 100f;
    }

    /**
     * sets a typeface for the value-paint
     * 
     * @param t
     */
    public void setValueTypeface(Typeface t) {
        mValuePaint.setTypeface(t);
    }

    /**
     * sets the typeface for the description paint
     * 
     * @param t
     */
    public void setDescriptionTypeface(Typeface t) {
        mDescPaint.setTypeface(t);
    }

    /**
     * Returns the bitmap that represents the chart.
     * 
     * @return
     */
    public Bitmap getChartBitmap() {
        // Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = getBackground();
        if (bgDrawable != null)
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            // does not have background drawable, then draw white background on
            // the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    /**
     * Saves the current chart state with the given name to the given path on
     * the sdcard leaving the path empty "" will put the saved file directly on
     * the SD card chart is saved as a PNG image, example:
     * saveToPath("myfilename", "foldername1/foldername2");
     * 
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     * @return returns true on success, false on error
     */
    public boolean saveToPath(String title, String pathOnSD) {

        Bitmap b = getChartBitmap();

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                    + pathOnSD + "/" + title
                    + ".png");

            /*
             * Write bitmap to file using JPEG or PNG and 40% quality hint for
             * JPEG.
             */
            b.compress(CompressFormat.PNG, 40, stream);

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Saves the current state of the chart to the gallery as a JPEG image. The
     * filename and compression can be set. 0 == maximum compression, 100 = low
     * compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     * 
     * @param fileName e.g. "my_image"
     * @param quality e.g. 50, min = 0, max = 100
     * @return returns true if saving was successfull, false if not
     */
    public boolean saveToGallery(String fileName, int quality) {

        // restrain quality
        if (quality < 0 || quality > 100)
            quality = 50;

        long currentTime = System.currentTimeMillis();

        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        String filePath = file.getAbsolutePath() + "/" + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);

            Bitmap b = getChartBitmap();

            b.compress(Bitmap.CompressFormat.JPEG, quality, out); // control
            // the jpeg
            // quality

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        long size = new File(filePath).length();

        ContentValues values = new ContentValues(8);

        // store the details
        values.put(Images.Media.TITLE, fileName);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.DATE_ADDED, currentTime);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DESCRIPTION, "MPAndroidChart-Library Save");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);

        return getContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values) == null
                ? false : true;
    }

	/**
	 * ################ ################ ################ ################
	 * Animation support below Honeycomb thanks to Jake Wharton's awesome
	 * nineoldandroids library: https://github.com/JakeWharton/NineOldAndroids
	 */
	/** CODE BELOW THIS RELATED TO ANIMATION */

	/** the phase that is animated and influences the drawn values on the y-axis */
	protected float mPhaseY = 1f;

	/** the phase that is animated and influences the drawn values on the x-axis */
	protected float mPhaseX = 1f;

	/** objectanimator used for animating values on y-axis */
	private ObjectAnimator mAnimatorY;

	/** objectanimator used for animating values on x-axis */
	private ObjectAnimator mAnimatorX;

	/**
	 * does needed preparations for drawing
	 */
	public abstract void prepare();

	/**
	 * Lets the chart know its underlying data has changed and performs all
	 * necessary recalculations.
	 */
	public abstract void notifyDataSetChanged();

	/**
	 * calculates the offsets of the chart to the border depending on the
	 * position of an eventual legend or depending on the length of the y-axis
	 * and x-axis labels and their position
	 */
	protected abstract void calculateOffsets();

	/**
	 * draws all the text-values to the chart
	 */
	protected abstract void drawValues();

	/**
	 * draws the actual data
	 */
	protected abstract void drawData();

	/**
	 * draws additional stuff, whatever that might be
	 */
	protected abstract void drawAdditional();

	/**
	 * draws the values of the chart that need highlightning
	 */
	protected abstract void drawHighlights();

}
