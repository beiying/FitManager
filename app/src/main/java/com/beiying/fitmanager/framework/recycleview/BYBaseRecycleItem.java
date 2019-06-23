package com.beiying.fitmanager.framework.recycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.beiying.fitmanager.core.ui.LeView;

/**
 * Created by beiying on 17/9/17.
 */

public abstract class BYBaseRecycleItem<T> extends LeView implements View.OnClickListener {
    public T mModel;
    private BYRecycleView.OnItemClickListener mItemClickListener;

    public BYBaseRecycleItem(@NonNull Context context) {
        super(context);
        setOnClickListener(this);
    }

    public void setModel(T model) {
        if (mModel == model) {
            return;
        }
        mModel = model;

        if (mModel == null) {
            return;
        }

        onModelChanged(mModel);
    }

    public void setOnItemClickListener(BYRecycleView.OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    protected abstract void onModelChanged(T model);

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(mModel, v);
        }
    }
}
