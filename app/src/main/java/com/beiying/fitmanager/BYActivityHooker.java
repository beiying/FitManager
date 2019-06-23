package com.beiying.fitmanager;

/**
 * Created by beiying on 2019/6/11.
 */

public class BYActivityHooker {
    private static BYActivityRecord sActivityRecord;

    static {
        sActivityRecord = new BYActivityRecord();
    }


//    @TargetClass(value = "android.support.v7.app.AppCompatActivity", scope = Scope.ALL)
//    @Insert(value = "onCreate", mayCreateSuper = true)
//    public void onCreate(Bundle savedInstanceState) {
//        sActivityRecord.mOnCreateTime = System.currentTimeMillis();
//        Origin.callVoid();
//    }
//
//    @Insert(value = "onWindowFocusChanged", mayCreateSuper = true)
//    @TargetClass(value = "android.support.v7.app.AppCompatActivity", scope = Scope.ALL)
//    public void onWindowFocusChanged(Bundle savedInstanceState) {
//        sActivityRecord.mOnWidnowFocusChangedTime = System.currentTimeMillis();
//        Origin.callVoid();
//    }
}
