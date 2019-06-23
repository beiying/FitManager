package com.beiying.fitmanager.train;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.beiying.fitmanager.core.ui.LeView;

/**
 * Created by beiying on 17/9/17.
 */

public class BYTrainListView extends LeView {
    private static final int ITEM_HEIGHT = 160;

    private RecyclerView mListView;
    public BYTrainListView(Context context, String type) {
        super(context);
        mListView = new RecyclerView(context);
        mListView.setLayoutManager(new LinearLayoutManager(context));
    }

    class TrainItemView extends LeView {

        public TrainItemView(Context context) {
            super(context);
        }
    }
}
