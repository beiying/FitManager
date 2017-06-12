package com.beiying.fitmanager.history;

import com.beiying.fitmanager.BYBasicContainer;

public class BYHistoryManager extends BYBasicContainer{
    private static BYHistoryManager sInstance;

    private BYHistoryManager() {

    }

    public static BYHistoryManager getInstance() {
        if (sInstance == null) {
            synchronized (BYHistoryManager.class) {
                sInstance = new BYHistoryManager();
            }
        }
        return sInstance;
    }
}
