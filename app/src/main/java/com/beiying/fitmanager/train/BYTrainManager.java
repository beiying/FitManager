package com.beiying.fitmanager.train;

import com.beiying.fitmanager.core.ContextContainer;

/**
 * Created by beiying on 17/6/9.
 */

public class BYTrainManager extends ContextContainer {

    private static BYTrainManager sInstance;
    private BYTrainManager() {

    }

    public static BYTrainManager getInstance() {
        if (sInstance == null) {
            synchronized (BYTrainManager.class) {
                sInstance = new BYTrainManager();
            }
        }
        return sInstance;
    }
}
