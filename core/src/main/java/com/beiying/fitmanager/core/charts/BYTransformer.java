package com.beiying.fitmanager.core.charts;

import java.util.ArrayList;

import android.graphics.Matrix;
import android.graphics.Path;

import com.beiying.fitmanager.core.charts.data.BYEntry;
import com.beiying.fitmanager.core.charts.interfaces.BYChartInterface;

public class BYTransformer {

	/** matrix to map the values to the screen pixels */
    protected Matrix mMatrixValueToPx = new Matrix();
    
    /** matrix for handling the different offsets of the chart */
    private Matrix mMatrixOffset = new Matrix();
    
	/** matrix used for touch events */
    private final Matrix mMatrixTouch = new Matrix();
    
    /** if set to true, the y-axis is inverted and low values start at the top */
    private boolean mInvertYAxis = false;
    
    /** minimum scale value on the y-axis */
    private float mMinScaleY = 1f;

    /** minimum scale value on the x-axis */
    private float mMinScaleX = 1f;

    /** contains the current scale factor of the x-axis */
    private float mScaleX = 1f;

    /** contains the current scale factor of the y-axis */
    private float mScaleY = 1f;

    /** offset that allows the chart to be dragged over its bounds on the x-axis */
    private float mTransOffsetX = 0f;

    /** offset that allows the chart to be dragged over its bounds on the x-axis */
    private float mTransOffsetY = 0f;
    
	/**
     * Prepares the matrix that transforms values to pixels.
     * 
     * @param chart
     */
    public void prepareMatrixValuePx(BYChartInterface chart) {

        float scaleX = (float) ((chart.getWidth() - chart.getOffsetRight() - chart
                .getOffsetLeft()) / chart.getDeltaX());
        float scaleY = (float) ((chart.getHeight() - chart.getOffsetTop() - chart
                .getOffsetBottom()) / chart.getDeltaY());

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(0, -chart.getYChartMin());
        mMatrixValueToPx.postScale(scaleX, -scaleY);
    }
    
    /**
     * Prepares the matrix that contains all offsets.
     * 
     * @param chart
     */
    public void prepareMatrixOffset(BYChartInterface chart) {

        mMatrixOffset.reset();

        if (!mInvertYAxis)
            mMatrixOffset.postTranslate(chart.getOffsetLeft(),
                    chart.getHeight() - chart.getOffsetBottom());
        else {
            mMatrixOffset.setTranslate(chart.getOffsetLeft(), -chart.getOffsetTop());
            mMatrixOffset.postScale(1.0f, -1.0f);
        }

    }
    
    /**
     * Transforms an arraylist of Entry into a float array containing the x and
     * y values transformed with all matrices for the LINECHART or SCATTERCHART.
     * 
     * @param entries
     * @return
     */
    public float[] generateTransformedValuesLineScatter(ArrayList<? extends BYEntry> entries,
            float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {

            BYEntry e = entries.get(j / 2);

            valuePoints[j] = e.getXIndex();
            valuePoints[j + 1] = e.getVal() * phaseY;
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }
    
    /**
     * Transforms the given array of touch positions (pixels) (x, y, x, y, ...)
     * into values on the chart.
     * 
     * @param pixels
     */
    public void pixelsToValue(float[] pixels) {

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pixels);
    }
    
    /**
     * Transform an array of points with all matrices. VERY IMPORTANT: Keep
     * matrix order "value-touch-offset" when transforming.
     * 
     * @param pts
     */
    public void pointValuesToPixel(float[] pts) {

        mMatrixValueToPx.mapPoints(pts);
        mMatrixTouch.mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }
    
    /**
     * If this is set to true, the y-axis is inverted which means that low
     * values are on top of the chart, high values on bottom.
     * 
     * @param enabled
     */
    public void setInvertYAxisEnabled(boolean enabled) {
        mInvertYAxis = enabled;
    }

    /**
     * If this returns true, the y-axis is inverted.
     * 
     * @return
     */
    public boolean isInvertYAxisEnabled() {
        return mInvertYAxis;
    }
    
    public Matrix getTouchMatrix() {
        return mMatrixTouch;
    }

    public Matrix getValueMatrix() {
        return mMatrixValueToPx;
    }

    public Matrix getOffsetMatrix() {
        return mMatrixOffset;
    }

    /**
     * returns the current x-scale factor
     */
    public float getScaleX() {
        return mScaleX;
    }

    /**
     * returns the current y-scale factor
     */
    public float getScaleY() {
        return mScaleY;
    }
    
    /**
     * Returns true if the chart is fully zoomed out on it's y-axis (vertical).
     * 
     * @return
     */
    public boolean isFullyZoomedOutY() {
        if (mScaleY > mMinScaleY || mMinScaleY > 1f)
            return false;
        else
            return true;
    }
    
    /**
     * transform a path with all the given matrices VERY IMPORTANT: keep order
     * to value-touch-offset
     * 
     * @param path
     */
    public void pathValueToPixel(Path path) {

        path.transform(mMatrixValueToPx);
        path.transform(mMatrixTouch);
        path.transform(mMatrixOffset);
    }

}
