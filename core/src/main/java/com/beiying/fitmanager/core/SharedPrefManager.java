package com.beiying.fitmanager.core;

import android.content.Context;

import com.beiying.fitmanager.core.data.LeMPSharedPrefUnit;
import com.beiying.fitmanager.core.data.LeSharedPrefFactory;
import com.beiying.fitmanager.core.data.LeSpHelper;


public class SharedPrefManager extends ContextContainer {

    public static LeSharedPrefFactory getFactory() {
        return new LeSharedPrefFactory() {

            @Override
            public LeSpHelper createCommonHelper() {
                return createCommonSpHelper(sPackageName);
            }
        };
    }

    private static LeSpHelper createCommonSpHelper(String pkgName) {
        String spName = formSpName(pkgName);
        return new LeSpHelper(spName);
    }

    private static String formSpName(String pkgName) {
        return pkgName.replace(".", "_") + "_sp";
    }


    public static LeSpHelper createMultiProSpHelper(String pkgName) {
        String spName = LeMPSharedPrefUnit.adjustSpName(pkgName);
        return new LeSpHelper(spName, Context.MODE_MULTI_PROCESS);
    }
}