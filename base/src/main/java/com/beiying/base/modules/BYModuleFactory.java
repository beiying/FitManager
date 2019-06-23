package com.beiying.base.modules;

/**
 * Created by cangwang on 2017/6/16.
 */

public class BYModuleFactory {

    public static BYAbsModule newModuleInstance(String name){
        if (name ==null || name.equals("")){
            return null;
        }

        try{
            Class<? extends BYAbsModule> moduleClzz = (Class<? extends BYAbsModule>) Class.forName(name);
            if (moduleClzz !=null){
                BYAbsModule instance = (BYAbsModule)moduleClzz.newInstance();
                return instance;
            }
            return null;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }
}
