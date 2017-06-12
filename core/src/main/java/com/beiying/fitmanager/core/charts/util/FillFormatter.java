package com.beiying.fitmanager.core.charts.util;

import com.beiying.fitmanager.core.charts.data.BYLineData;
import com.beiying.fitmanager.core.charts.data.BYLineDataSet;

/**
 * Interface for providing a custom logic to where the filling line of a DataSet
 * should end. If setFillEnabled(...) is set to true.
 * 
 */
public interface FillFormatter {

    /**
     * Returns the vertical (y-axis) position where the filled-line of the
     * DataSet should end.
     * 
     * @param dataSet
     * @param data
     * @param chartMaxY
     * @param chartMinY
     * @return
     */
    public float getFillLinePosition(BYLineDataSet dataSet, BYLineData data, float chartMaxY,
            float chartMinY);
}