package com.beiying.net;

/**
 * Created by beiying on 19/4/7.
 */

public class Utils {
    public static boolean isExist(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
