package com.beiying.fitmanager.framework.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.core.BYSafeRunnable;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.utils.BYAndroidUtils;
import com.beiying.fitmanager.core.utils.LeColorUtil;
import com.beiying.fitmanager.framework.BYControlCenter;
import com.beiying.fitmanager.utils.BYBitmapUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by beiying on 18/3/20.
 */

public class BYTheme extends BYBasicContainer{
    public static final String COMMON_THEME_NAME = "common_theme";
    public static final String COMMON_TEXT_NAME = "common_text";
    public static final String COMMON_SUB_TEXT_NAME = "common_sub_text";
    public static final String COMMON_TEXT_DISABLE_NAME = "common_text_disable";
    public static final String COMMON_SELECT_NAME = "common_select";
    public static final String COMMON_TITLE_NAME = "common_title";
    public static final String COMMON_SUB_TITLE_NAME = "common_sub_title";
    public static final String COMMON_PRESS_BG_NAME = "common_press_bg";
    public static final String COMMON_FOCUS_NAME = "common_focused";
    public static final String COMMON_FOCUS_BG_NAME = "common_focused_bg";
    public static final String COMMON_CONTRAST_NAME = "common_contrast";
    public static final String COMMON_SPLITLINE_NAME = "common_splitline";
    public static final String COMMON_DISABLE_NAME = "common_disable";
    public static final String COMMON_BACKGROUND_NAME = "common_background";
    public static final String COMMON_TEXT_HINT_NAME = "common_text_hint";


    public static final String STATUSBAR_BG_NAME = "statusbar_bg";

    public static int sStatusbarColor;

    private static HashMap<String, Integer> sColorMap = new HashMap<String, Integer>();

    private static int sStatusBarHeight = -1;

    private BYTheme(){}

    public static int getColor(String resName) {
        if (sColorMap == null) {
            return 0xffff0000;
        }
//		if (LeColorDebugView.DEBUG) {
//			int color = LeColorDebugView.getColor(resName);
//			if (color != 0) {
//				return color;
//			}
//		}
        if (sColorMap.containsKey(resName)) {
            return sColorMap.get(resName);
        } else {
            int color = BYResources.getInstance().getColor(resName);
            sColorMap.put(resName, color);
            return color;
        }
    }

    public static void updateStatusBarColor() {
        sStatusbarColor = getInnerStatusBarColor();
        if (Looper.getMainLooper() == Looper.myLooper()) {
            changeStatusBarColor(sActivity, sStatusbarColor);
        } else {
            BYControlCenter.getInstance().postToUiThread(new BYSafeRunnable() {
                @Override
                public void runSafely() {
                    updateStatusBarColor();
                }
            });
        }

    }

    private static int getInnerStatusBarColor() {
        return BYResources.getInstance().getInnerColor(STATUSBAR_BG_NAME);
    }

    private static int getOutterStatusBarColor() {
        return BYResources.getInstance().getOutterColor(STATUSBAR_BG_NAME);
    }

    public static int getTitlebarAvgColor() {
        int avgcolor = 0;
        Drawable titlebarBg = BYResources.getInstance().getDrawable("");
        if (titlebarBg instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(titlebarBg.getIntrinsicWidth(),
                    titlebarBg.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            titlebarBg.setBounds(0, 0, titlebarBg.getIntrinsicWidth(), titlebarBg.getIntrinsicHeight());
            titlebarBg.draw(canvas);
            avgcolor = BYBitmapUtil.getAvgColor(bitmap);
            bitmap = null;
        } else if (titlebarBg instanceof BitmapDrawable) {
            avgcolor = BYBitmapUtil.getAvgColor(((BitmapDrawable) titlebarBg).getBitmap());
        }
//		}
        return avgcolor;
    }

    public static void changeStatusBarColorByFakeTitleBar() {
//		changeStatusBarColor(LeMainActivity.sInstance, sFakeTitleBarColor[0]);
    }

    @SuppressLint("NewApi")
    public static void changeStatusBarColor(Activity activity, int color) {
        if (!isDrawStatusbar()) {
            return;
        }

        if (isDrawStatusbarV21()) {

            if (isLenovoMachine()) {
//				setLenovoStatusbarColorV21(activity, color);
                setStatusBarTransparentV21(activity);
                setDarkStatusIconColorV21ForLenovo(activity, LeColorUtil.toGray(color) < 200);
            } else if (isMiui()) {
                changeMiuiStatusBarTextColor(activity, LeColorUtil.toGray(color) < 200);
            } else if (isMeizu()) {
                changeMeizuStatusBarTextColor(activity, LeColorUtil.toGray(color) < 200);
            } else if (isSamsung()){
                changeSamsungStatusBarColor(activity);
            }

            if (isDrawStatusbarV23()) {
                setStatusBarForV23(activity, color);
            }

            return;
        }

        if (isDrawLenovoStatusbarV19()) {
            setLenovoStatusbarColorV19(activity, color);
            return;
        }
        if (isMiui()) {
            changeMiuiStatusBarTextColor(activity, LeColorUtil.toGray(color) < 200);
            return;
        }
        if (isMeizu()) {
            changeMeizuStatusBarTextColor(activity, LeColorUtil.toGray(color) < 200);
            return;
        }


    }

    private static void setStatusBarForV23(Activity activity, int color) {
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            LeLog.e("changeStatusBarColor sStatusbarColor=" + color);
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (LeColorUtil.toGray(color) < 200) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void changeStatusBarIconForV23(Activity activity, boolean isImage) {
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            if (isImage) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    //设置状态栏全透
    @SuppressLint("NewApi")
    public static void setStatusBarTransparentV21(Activity activity){

        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }
    }

    //设置状态栏图标为黑色图标显示
    public static void setStatusBarDrakIconForZUK(Activity activity, boolean isDrakIcon){
        try{
            Method setM = Window.class.getDeclaredMethod("setDarkStatusIcon", boolean.class);
            setM.invoke(activity.getWindow(),!isDrakIcon);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void setDarkStatusIconColorV21ForLenovo(Activity activity, boolean isDarkIcon) {
        Window window = activity.getWindow();
        Method method;
        try {
            method = window.getClass().getMethod("romUI_setDarkStatusIcon",new Class[] {boolean.class});
            method.invoke(window, !isDarkIcon);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private static void setLenovoStatusbarColorV21(Activity activity, int color) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (LeColorUtil.toGray(color) < 200) {
            window.setStatusBarColor(color);
        } else {
            window.setStatusBarColor(0xfff3f3f3);
        }
    }

    private static void setLenovoStatusbarColorV19(Activity activity, int color) {
        if (Build.VERSION.SDK_INT == 19) {
            if (LeColorUtil.toGray(color) < 200) {
                setLenovoDarkStatusIconV19(activity, false);
            } else {
                setLenovoDarkStatusIconV19(activity, true);
            }
        }
    }

    public static void setLenovoDarkStatusIconV19(Activity activity, boolean isDark) {
        try {
            Class<?> clz = Class.forName("com.android.internal.policy.impl.PhoneWindow");
            Object o = activity.getWindow();
            Method m = clz.getMethod("romUI_setDarkStatusIcon", boolean.class);
            m.invoke(o, isDark);
        } catch (Exception e) {

        }
    }

    public static boolean isDrawStatusbar() {
        if (VERSION.SDK_INT >= 19) {
            return true;
        }
        return false;
    }

    public static boolean isDrawSystemStatusbarV21() {
        if (VERSION.SDK_INT >= 21 && !Build.MODEL.toLowerCase().contains("lenovo")) {
            return true;
        }
        return false;
    }

    public static boolean isDrawSystemStatusbarV19() {
        if (VERSION.SDK_INT == 19 && !Build.MODEL.toLowerCase().contains("lenovo")) {
            return true;
        }
        return false;
    }

    public static boolean isDrawStatusbarV23() {
        if (VERSION.SDK_INT >= 23) {
            return true;
        }
        return false;
    }

    public static boolean isDrawStatusbarV21() {
        if (VERSION.SDK_INT >= 21) {
            return true;
        }
        return false;
    }

    public static boolean isLenovoMachine() {
        return Build.MODEL.toLowerCase().contains("lenovo");
    }


    public static boolean isDrawLenovoStatusbarV19() {
        if (VERSION.SDK_INT == 19 && Build.MODEL.toLowerCase().contains("lenovo")) {
            return true;
        }
        return false;
    }

    public static void changeRootTheme(View view) {
//		LeColorDebugView.DEBUG = false;

        sColorMap.clear();
        BYDimen.clear();

        changeViewTheme(view);

    }

    public static void changeTheme(View view) {
        changeViewTheme(view);
    }

    private static void changeViewTheme(View view) {
        if (view == null) {
            return;
        }

        if (view instanceof ViewGroup) {
            ViewGroup childGroup = (ViewGroup) view;
            int childCnt = childGroup.getChildCount();
            for (int i = 0; i < childCnt; i++) {
                View childView = childGroup.getChildAt(i);
                changeViewTheme(childView);
            }
        }

        view.invalidate();
    }

    public static int getThemeColor() {
        return getColor(COMMON_THEME_NAME);
    }

    public static int getTagNumColor() {
        return getColor("tag_num");
    }

    public static int getTextHintColor() {
        return getColor(COMMON_TEXT_HINT_NAME);
    }

    public static int getTextColor() {
        return getColor(COMMON_TEXT_NAME);
    }

    public static int getSubTextColor() {
        return getColor(COMMON_SUB_TEXT_NAME);
    }

    public static int getTextDisableColor() {
        return getColor(COMMON_TEXT_DISABLE_NAME);
    }

    public static int getSelectColor() {
        return getColor(COMMON_SELECT_NAME);
    }

    public static int getPressBgColor() {
        return getColor(COMMON_PRESS_BG_NAME);
    }

    public static int getFocusColor() {
        return getColor(COMMON_FOCUS_NAME);
    }

    public static int getFocusBgColor() {
        return getColor(COMMON_FOCUS_BG_NAME);
    }

    public static int getContrastColor() {
        return getColor(COMMON_CONTRAST_NAME);
    }

    public static int getLineColor() {
        return getColor(COMMON_SPLITLINE_NAME);
    }

    public static int getTitleColor() {
        return getColor(COMMON_TITLE_NAME);
    }

    public static int getSubTitleColor() {
        return getColor(COMMON_SUB_TITLE_NAME);
    }

    public static int getDisableColor() {
        return getColor(COMMON_DISABLE_NAME);
    }

    public static int getBgColor() {
        return getColor(COMMON_BACKGROUND_NAME);
    }



    public static int getStatusBarHeight() {
        if (!isDrawStatusbar()) {
            return 0;
        }

        if (sStatusBarHeight == -1) {
            sStatusBarHeight = LeUI.getStatusbarHeight(sContext);
        }
        return sStatusBarHeight;
    }

    public static boolean isMiui() {
        if (VERSION.SDK_INT >= 19 && BYAndroidUtils.isMIUI()) {
            return true;
        }
        return false;
    }

    public static boolean isMeizu() {
        if (VERSION.SDK_INT >= 19 && BYAndroidUtils.isMeizu()) {
            return true;
        }
        return false;
    }

    public static boolean isSamsung() {
        if (VERSION.SDK_INT >= 19 && BYAndroidUtils.isSamsung()) {
            return true;
        }
        return false;
    }

    //小米手机上， 状态栏颜色变化.
    public static void changeMiuiStatusBarTextColor(Activity activity, boolean dark) {
        if (activity == null) {
            return;
        }

        Window window = activity.getWindow();
        Class clazz = window.getClass();
        try {
            int tranceFlag = 0;
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);

            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);

            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            //只需要状态栏透明
//				extraFlagField.invoke(getWindow(), tranceFlag, tranceFlag);

            if (!dark) {
                //状态栏透明且黑色字体
                extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);
            } else {
                //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置魅族的状态栏文字颜色。
     * @param activity
     * @param dark
     */
    public static void changeMeizuStatusBarTextColor(Activity activity, boolean dark) {
        try {
            Window loacalWindow = activity.getWindow();
            WindowManager.LayoutParams lp = loacalWindow.getAttributes();
            Class localClass = Class.forName("android.view.WindowManager$LayoutParams");
            Object localObject = localClass.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").get(localClass);

            Field localField = localClass.getDeclaredField("meizuFlags");
            localField.setAccessible(true);
            int i1 = Integer.valueOf(localField.getInt(lp)).intValue();

            int paramInt = ((Integer)localObject).intValue();
            if (!dark){
                localField.set(lp, Integer.valueOf(i1 | paramInt));
            } else {
                localField.set(lp, Integer.valueOf(i1 & (paramInt ^ 0xFFFFFFFF)));
            }

            loacalWindow.setAttributes(lp);

            return;

        } catch (Exception e) {
            LeLog.e(e);
        }
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public static void changeSamsungStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        int color = Color.argb(80, 0, 0, 0);
        window.setStatusBarColor(color);
    }
}
