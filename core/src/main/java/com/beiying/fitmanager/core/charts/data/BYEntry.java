package com.beiying.fitmanager.core.charts.data;

/**
 * Represent one entry in the chart.Might contain multiple values.
 * Might only contain a single value depending on the constructor.
 * 图表中的一个实体
 * @author beiying
 *
 */
public class BYEntry {
	
	//the value on the Y-axis
	private float mVal = 0f;
	
	//the index on the X-axis
	private int mXIndex = 0;
	
	private Object mData = null;
	
	public BYEntry(float val, int index) {
		mVal = val;
		mXIndex = index;
	}
	
	public BYEntry(float val, int index, Object data) {
		mVal = val;
		mXIndex = index;
		mData = data;
	}
	
	/**
     * returns the x-index the value of this object is mapped to
     * 
     * @return
     */
    public int getXIndex() {
        return mXIndex;
    }

    /**
     * sets the x-index for the entry
     * 
     * @param x
     */
    public void setXIndex(int x) {
        this.mXIndex = x;
    }

    /**
     * Returns the total value the entry represents.
     * 
     * @return
     */
    public float getVal() {
        return mVal;
    }

    /**
     * Sets the value for the entry.
     * 
     * @param val
     */
    public void setVal(float val) {
        this.mVal = val;
    }

    /**
     * Returns the data, additional information that this Entry represents, or
     * null, if no data has been specified.
     * 
     * @return
     */
    public Object getData() {
        return mData;
    }

    /**
     * Sets additional data this Entry should represents.
     * 
     * @param data
     */
    public void setData(Object data) {
        this.mData = data;
    }
    
    /**
     * return  an exact copy of the entry
     * @return
     */
    public BYEntry copy() {
    	BYEntry e = new BYEntry(mVal, mXIndex, mData);
    	return e;
    }
	
    /**
     * Compares value, xIndex and data of the entries. Returns true if entries
     * are equal, false if not.
     * 
     * @param e
     * @return
     */
    public boolean equalTo(BYEntry e) {

        if (e == null)
            return false;

        if (e.mData != this.mData)
            return false;
        if (e.mXIndex != this.mXIndex)
            return false;

        if (Math.abs(e.mVal - this.mVal) > 0.00001f)
            return false;

        return true;
    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    public String toString() {
        return "Entry, xIndex: " + mXIndex + " val (sum): " + getVal();
    }
	
}
