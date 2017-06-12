package com.beiying.fitmanager.core.charts.data;

import java.util.ArrayList;

public class BYBarData extends BYBarLineScatterCandleData<BYBarDataSet>{

	/** the space that is left between groups of bars */
    private float mGroupSpace = 0.8f;
    
    public BYBarData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public BYBarData(String[] xVals) {
        super(xVals);
    }

    public BYBarData(ArrayList<String> xVals, ArrayList<BYBarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BYBarData(String[] xVals, ArrayList<BYBarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BYBarData(ArrayList<String> xVals, BYBarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public BYBarData(String[] xVals, BYBarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<BYBarDataSet> toArrayList(BYBarDataSet dataSet) {
        ArrayList<BYBarDataSet> sets = new ArrayList<BYBarDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the space that is left out between groups of bars. Always returns
     * 0 if the BarData object only contains one DataSet (because for one
     * DataSet, there is no group-space needed).
     * 
     * @return
     */
    public float getGroupSpace() {

        if (mDataSets.size() <= 1)
            return 0f;
        else
            return mGroupSpace;
    }

    /**
     * Sets the space between groups of bars of different datasets in percent of
     * the total width of one bar. 100 = space is exactly one bar width,
     * default: 80
     * 
     * @param percent
     */
    public void setGroupSpace(float percent) {
        mGroupSpace = percent / 100f;
    }
}
