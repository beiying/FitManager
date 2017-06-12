package com.beiying.fitmanager.core.charts.data;

import java.util.ArrayList;

import com.beiying.fitmanager.core.charts.util.ColorTemplate;

import android.content.Context;
import android.graphics.Color;

public abstract class BYDataSet<T extends BYEntry> {
	
	//represent all colors that are used for this DataSet
	protected ArrayList<Integer> mColors = null;
	
	//represent the entries that this dataset holds together
	protected ArrayList<T> mYVals = null;
	
	// maximum y-value in the y-value array 
    protected float mYMax = 0.0f;

    // the minimum y-value in the y-value array */
    protected float mYMin = 0.0f;

    // the total sum of all y-values */
    private float mYValueSum = 0f;

    // label that describes the DataSet or the data the DataSet represents */
    private String mLabel = "DataSet";
	
	
    /**
     * Creates a new DataSet object with the given values it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     * 
     * @param yVals
     * @param label
     */
    public BYDataSet(ArrayList<T> yVals, String label) {

        this.mLabel = label;
        this.mYVals = yVals;

        if (mYVals == null)
            mYVals = new ArrayList<T>();

        mColors = new ArrayList<Integer>();
        mColors.add(Color.rgb(140, 234, 255));

        calcMinMax();
        calcYValueSum();
    }


    /**
     * 计算y坐标绝对值的和
     */
	private void calcYValueSum() {
		mYValueSum = 0;

        for (int i = 0; i < mYVals.size(); i++) {
            mYValueSum += Math.abs(mYVals.get(i).getVal());
        }
	}

	/**
	 * 计算y坐标的最大值和最小值
	 */
	private void calcMinMax() {
		if (mYVals.size() == 0) {
			return;
		}
		
		mYMax = mYVals.get(0).getVal();
		mYMin = mYVals.get(0).getVal();
		
		for (int i = 0;i < mYVals.size();i++) {
			float val = mYVals.get(i).getVal();
			if (val > mYMax) {
				mYMax = val;
			}
			
			if (val < mYMin) {
				mYMin = val;
			}
		}
	}
	
	
	/**
     * Returns the value of the Entry object at the given xIndex. Returns
     * Float.NaN if no value is at the given x-index. 
     * INFORMATION: This method does calculations at runtime. 
     * Do not over-use in performance critical situations.
     * 该方法比较耗时、比较占内存，谨慎使用
     * @param xIndex
     * @return
     */
    public float getYValForXIndex(int xIndex) {

        BYEntry e = getEntryForXIndex(xIndex);

        if (e != null)
            return e.getVal();
        else
            return Float.NaN;
    }

    /**
     * Returns the first Entry object found at the given xIndex with binary
     * search. Returns null if no Entry object at that index. 
     * INFORMATION: This method does calculations at runtime.
     * Do not over-use in performance critical situations.
     * 使用二分法查找
     * @param xIndex
     * @return
     */    
	public T getEntryForXIndex(int x) {
		int low = 0;
		int high = mYVals.size() - 1;
		while (low <= high) {
			int m = (low + high) / 2;
			
			if (x == mYVals.get(m).getXIndex()) {
				return mYVals.get(m);
			}
			
			if (x > mYVals.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;
		}
		
		return null;
	}
	
	/**
     * Returns all Entry objects at the given xIndex. INFORMATION: This method
     * does calculations at runtime. Do not over-use in performance critical
     * situations.
     * 
     * @param xIndex
     * @return
     */
    public ArrayList<T> getEntriesForXIndex(int x) {

        ArrayList<T> entries = new ArrayList<T>();

        int low = 0;
        int high = mYVals.size();

        while (low <= high) {
            int m = (high + low) / 2;

            if (x == mYVals.get(m).getXIndex()) {
                entries.add(mYVals.get(m));
            }

            if (x > mYVals.get(m).getXIndex())
                low = m + 1;
            else
                high = m - 1;
        }

        return entries;
    }
    
    /**
     * returns the number of y-values this DataSet represents
     * 
     * @return
     */
    public int getEntryCount() {
        return mYVals.size();
    }

    
    /**
     * returns the DataSets Entry array
     * 
     * @return
     */
    public ArrayList<T> getYVals() {
        return mYVals;
    }

    /**
     * gets the sum of all y-values
     * 
     * @return
     */
    public float getYValueSum() {
        return mYValueSum;
    }

    /**
     * returns the minimum y-value this DataSet holds
     * 
     * @return
     */
    public float getYMin() {
        return mYMin;
    }

    /**
     * returns the maximum y-value this DataSet holds
     * 
     * @return
     */
    public float getYMax() {
        return mYMax;
    }
    
    /**
     * Returns the label string that describes the DataSet.
     * 
     * @return
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * The xIndex of an Entry object is provided. This method returns the actual
     * index in the Entry array of the DataSet. IMPORTANT: This method does
     * calculations at runtime, do not over-use in performance critical
     * situations.
     * 
     * @param xIndex
     * @return
     */
    public int getIndexInEntries(int xIndex) {

        for (int i = 0; i < mYVals.size(); i++) {
            if (xIndex == mYVals.get(i).getXIndex())
                return i;
        }

        return -1;
    }
    
    /**
     * Returns the position of the provided entry in the DataSets Entry array.
     * Returns -1 if doesnt exist.
     * 
     * @param e
     * @return
     */
    public int getEntryPosition(BYEntry e) {

        for (int i = 0; i < mYVals.size(); i++) {
            if (e.equalTo(mYVals.get(i)))
                return i;
        }

        return -1;
    }
    
    /**
     * Adds an Entry to the DataSet dynamically. This will also recalculate the
     * current minimum and maximum values of the DataSet and the value-sum.
     *
     * @param d
     */
    public void addEntry(BYEntry e) {

        if (e == null)
            return;

        float val = e.getVal();
        
        //重新计算y坐标的最大值和最小值以及绝对值和
        if (mYVals == null || mYVals.size() <= 0) {

            mYVals = new ArrayList<T>();
            mYMax = val;
            mYMin = val;
        } else {

            if (mYMax < val)
                mYMax = val;
            if (mYMin > val)
                mYMin = val;
        }

        mYValueSum += val;

        // add the entry
        mYVals.add((T) e);
    }
    
    /**
     * Removes an Entry from the DataSets entries array. This will also
     * recalculate the current minimum and maximum values of the DataSet and the
     * value-sum. Returns true if an Entry was removed, false if no Entry could
     * be removed.
     * 
     * @param e
     */
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        // remove the entry
        boolean removed = mYVals.remove(e);

        if (removed) {

            float val = e.getVal();
            mYValueSum -= val;
            
            //删除的不是最大值或最小值就不需要重新计算
            if (e.getVal() == mYMax || e.getVal() == mYMin) {
            	calcMinMax();
            }
            
        }

        return removed;
    }
    
    /**
     * Removes the Entry object that has the given xIndex from the DataSet.
     * Returns true if an Entry was removed, false if no Entry could be removed.
     * 
     * @param xIndex
     */
    public boolean removeEntry(int xIndex) {

        T e = getEntryForXIndex(xIndex);
        return removeEntry(e);
    }
    
    
    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     * 
     * @param colors
     */
    public void setColors(ArrayList<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     * 
     * @param colors
     */
    public void setColors(int[] colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     * 
     * @param colors
     */
    public void setColors(int[] colors, Context c) {

        ArrayList<Integer> clrs = new ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mColors = clrs;
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     * 
     * @param color
     */
    public void addColor(int color) {
        if (mColors == null)
            mColors = new ArrayList<Integer>();
        mColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     * 
     * @param color
     */
    public void setColor(int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * returns all the colors that are set for this DataSet
     * 
     * @return
     */
    public ArrayList<Integer> getColors() {
        return mColors;
    }

    /**
     * Returns the color at the given index of the DataSet's color array.
     * Performs a IndexOutOfBounds check by modulus.
     * 
     * @param index
     * @return
     */
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }
    
    /**
     * Returns the first color (index 0) of the colors-array this DataSet
     * contains.
     * 
     * @return
     */
    public int getColor() {
        return mColors.get(0);
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        mColors = new ArrayList<Integer>();
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(toSimpleString());
        for (int i = 0; i < mYVals.size(); i++) {
            buffer.append(mYVals.get(i).toString() + " ");
        }
        return buffer.toString();
    }

    /**
     * Returns a simple string representation of the DataSet with the type and
     * the number of Entries.
     * 
     * @return
     */
    public String toSimpleString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataSet, label: " + mLabel + ", entries: " + mYVals.size() + "\n");
        return buffer.toString();
    }

    
    /**
     * Provides an exact copy of the DataSet this method is used on.
     * 
     * @return
     */
    public abstract BYDataSet<T> copy();
}
