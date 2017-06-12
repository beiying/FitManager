package com.beiying.fitmanager.core.charts.data;

import java.util.ArrayList;

public class BYBarLineScatterCandleData<T extends BYBarLineScatterCandleRadarDataSet<? extends BYEntry>>
		extends BYBarLineScatterCandleRadarData<T> {
	
	public BYBarLineScatterCandleData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public BYBarLineScatterCandleData(String[] xVals) {
        super(xVals);
    }

    public BYBarLineScatterCandleData(ArrayList<String> xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }

    public BYBarLineScatterCandleData(String[] xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }
}
