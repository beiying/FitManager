package com.beiying.fitmanager.core.charts.data;

import java.util.ArrayList;

import com.beiying.fitmanager.core.charts.util.Utils;

public class BYPieDataSet extends BYDataSet<BYEntry>{
	
	/** the space in degrees between the chart-slices, default 0f */
    private float mSliceSpace = 0f;

    /** indicates the selection distance of a pie slice */
    private float mShift = 18f;

    public BYPieDataSet(ArrayList<BYEntry> yVals, String label) {
        super(yVals, label);

//        mShift = Utils.convertDpToPixel(12f);
    }

    @Override
    public BYDataSet<BYEntry> copy() {

        ArrayList<BYEntry> yVals = new ArrayList<BYEntry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        BYPieDataSet copied = new BYPieDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mSliceSpace = mSliceSpace;
        copied.mShift = mShift;
        return copied;
    }

    /**
     * sets the space that is left out between the piechart-slices, default: 0°
     * --> no space, maximum 45, minimum 0 (no space)
     * 
     * @param degrees
     */
    public void setSliceSpace(float degrees) {

        if (degrees > 45)
            degrees = 45f;
        if (degrees < 0)
            degrees = 0f;

        mSliceSpace = degrees;
    }

    /**
     * returns the space that is set to be between the piechart-slices of this
     * DataSet, in degrees
     * 
     * @return
     */
    public float getSliceSpace() {
        return mSliceSpace;
    }

    /**
     * sets the distance the highlighted piechart-slice of this DataSet is
     * "shifted" away from the center of the chart, default 12f
     * 
     * @param shift
     */
    public void setSelectionShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }

    /**
     * returns the distance a highlighted piechart slice is "shifted" away from
     * the chart-center
     * 
     * @return
     */
    public float getSelectionShift() {
        return mShift;
    }
}
