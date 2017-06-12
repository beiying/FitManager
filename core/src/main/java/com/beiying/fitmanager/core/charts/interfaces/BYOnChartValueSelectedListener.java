package com.beiying.fitmanager.core.charts.interfaces;

import com.beiying.fitmanager.core.charts.data.BYEntry;

/**
 * Listener for callbacks when selecting values inside the chart by
 * touch-gesture.
 * @author beiying
 *
 */
public interface BYOnChartValueSelectedListener {

	/**
     * Called when a value has been selected inside the chart.
     * 
     * @param e The selected Entry.
     * @param dataSetIndex The index in the datasets array of the data object
     *            the Entrys DataSet is in.
     */
    public void onValueSelected(BYEntry e, int dataSetIndex);

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    public void onNothingSelected();
}
