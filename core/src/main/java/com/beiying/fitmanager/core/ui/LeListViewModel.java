package com.beiying.fitmanager.core.ui;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeListViewModel<E> {

	protected LeListViewModelListener mListener;
	
	private ArrayList<E> mDataList;
	
	private Object mLock = new Object();
	
	public LeListViewModel() {
		mDataList = new ArrayList<E>();
	}

	public void setModelListener(LeListViewModelListener listener) {
		mListener = listener;
	}

	public int getSize() {
		return mDataList.size();
	}
	
	public int indexOf(Object itemModel) {
		return mDataList.indexOf(itemModel);
	}

	public E get(int index) {
		if (index < 0 || index >= getSize()) {
			Log.d("gyy", "NOTE:index is out of bounds!!!!!!");
			return null;
		}

		return mDataList.get(index);
	}

	public void add(E modelItem) {
		add(modelItem, getSize());
	}

	public void add(E modelItem, int index) {
		synchronized (mLock) {
			if (index < 0 || index > getSize()) {
				Log.d("gyy", "NOTE:index is out of bounds!!!!!!");
				return;
			}

			mDataList.add(index, modelItem);
			if (mListener != null) {
				mListener.onAdd(index);
			}
		}
	}

	public void remove(int index) {
		synchronized (mLock) {
			if (index < 0 || index >= getSize()) {
				Log.d("gyy", "NOTE:index is out of bounds!!!!!!");
				return;
			}

			mDataList.remove(index);
			if (mListener != null) {
				mListener.onRemove(index);
			}	
		}
	}
	
	public void clear() {
		synchronized (mLock) {
			mDataList.clear();
			if (mListener != null) {
				mListener.onClear();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void update(int index, Object modelItem) {
		synchronized (mLock) {
			if (index < 0 || index >= getSize()) {
				Log.d("gyy", "NOTE:index is out of bounds!!!!!!");
				return;
			}
	
			mDataList.set(index, (E) modelItem);
			if (mListener != null) {
				mListener.onUpdate(index);
			}
		}
	}
	
	public void refresh() {
		if (mListener != null) {
			mListener.onRefresh();
		}
	}
	
	public void sort(Comparator<E> comparator) {
		Collections.sort(mDataList, comparator);
	}

	public interface LeListViewModelListener {
		void onAdd(int index);

		void onRemove(int index);
		
		void onClear();

		void onUpdate(int index);

		void onRefresh();
	}
}