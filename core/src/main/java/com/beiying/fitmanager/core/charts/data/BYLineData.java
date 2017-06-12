package com.beiying.fitmanager.core.charts.data;

import java.util.ArrayList;

public class BYLineData extends BYBarLineScatterCandleData<BYLineDataSet> {
    
    public BYLineData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public BYLineData(String[] xVals) {
        super(xVals);
    }

    public BYLineData(ArrayList<String> xVals, ArrayList<BYLineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BYLineData(String[] xVals, ArrayList<BYLineDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public BYLineData(ArrayList<String> xVals, BYLineDataSet dataSet) {
        super(xVals, toArrayList(dataSet));        
    }
    
    public BYLineData(String[] xVals, BYLineDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<BYLineDataSet> toArrayList(BYLineDataSet dataSet) {
        ArrayList<BYLineDataSet> sets = new ArrayList<BYLineDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
