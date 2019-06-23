package com.beiying.fitmanager;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.beiying.base.modules.BYAbsModule;
import com.beiying.base.modules.BYModuleBus;
import com.beiying.base.modules.BYModuleContext;
import com.beiying.base.modules.BYModuleFactory;
import com.beiying.base.modules.BYModuleManager;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.module.BYModuleRootView;

public class BYModuleActivity extends Activity {
    private BYModuleRootView mRootView;
    private BYModuleManager mModuleManager;
    private BYModuleContext mModuleContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = new BYModuleRootView(this);
        setContentView(mRootView);

        BYModuleBus.getInstance().register(this);

        mModuleManager = new BYModuleManager();
        mModuleManager.moduleConfig(BYModuleBus.getInstance().getModuleList("top"));

        mModuleContext = new BYModuleContext();
        mModuleContext.setActivity(this);
        mModuleContext.setSaveInstance(savedInstanceState);
        SparseArrayCompat<ViewGroup> groups = new SparseArrayCompat<>();
        groups.put(BYModuleContext.TOP_VIEW_GROUP, mRootView.getTopView());
        mModuleContext.setViewGroups(groups);
        for (final String moduleName:mModuleManager.getModuleNames()){
            mModuleManager.getPool().execute(new Runnable() {
                @Override
                public void run() {
                    final BYAbsModule module = BYModuleFactory.newModuleInstance(moduleName);
                    if (module!=null){
                        mModuleManager.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                long before = System.currentTimeMillis();
                                module.init(mModuleContext, null);
                                LeLog.e("BYModuleActivity", "modulename: " +moduleName + " init time = " + (System.currentTimeMillis() - before) + "ms");
                                mModuleManager.putModule(moduleName, module);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mModuleManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mModuleManager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mModuleManager.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModuleManager.onDestroy();
        BYModuleBus.getInstance().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mModuleManager.onConfigurationChanged(newConfig);
    }
}
