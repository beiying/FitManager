package com.beiying.fitmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.beiying.fitmanager.core.LeLog;

import java.io.File;


/**
 * Created by lktc420 on 14-3-12.
 * 安装 apk 包工具类，可以通过乐安全在乐 rom 上静默安装 apk
 */
public class LePackageUtils {
	public static final int INSTALL_RESULT_FAIL = 0;
	public static final int INSTALL_RESULT_SILENT = 1;
	public static final int INSTALL_RESULT_NORMAL = 2;

    /**
     * 安装一个 apk，如果能通过乐安全 root 权限，则静默安装，如果不能则常规 intent 安装
     * @param context context
     * @param filePath apk full path
     * @return true if ok
     */
    public static final int install(final Context context, final String filePath) {
    	int res = INSTALL_RESULT_FAIL;
        if(context == null || TextUtils.isEmpty(filePath)) {
            LeLog.e("invalid params !");
            return res;
        }

//        ShellCommand nac = getNacShellCommand(context);
//        if(nac != null) {
//            //如果获得 root 权限，静默安装
//            installSilentUsingNac(context, nac, filePath);
//            res = INSTALL_RESULT_SILENT;
//        } else {
//            //如果失败，普通安装
//            installNormal(context, filePath);
//            res = INSTALL_RESULT_NORMAL;
//        }
        installNormal(context, filePath);
        res = INSTALL_RESULT_NORMAL;
        return res;
    }

    /**
     * 常规安装 apk
     * @param context context
     * @param filePath apk full path
     * @return true if ok
     */
    public static final boolean installNormal(final Context context, final String filePath) {

        if(context == null || TextUtils.isEmpty(filePath)) {
            LeLog.e("invalid params !");
            return false;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        try {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 通过乐安全接口获得 nac shell command 实例，如果不能获得表示无法获得 root 权限（不是乐 rom）
     * @param context context
     * @return ShellCommand instance
     */
//    public static final ShellCommand getNacShellCommand(final Context context) {
//
//        if(context == null){
//            LeLog.e("invalid params !");
//            return null;
//        }
//
//        ShellCommand nac = ShellCommand.getNacShellCommandInstance(context);
//        if(nac != null) {
//            return nac;
//        } else {
//            LeLog.d("Cannot get system permision.\n");
//            File f = new File("/system/bin", "nac_server");
//            if(f.exists()) {
//                LeLog.d("=> System tools exist.\n");
//            } else {
//                LeLog.d("=> System tools doesn't exist.\n");
//            }
//            return null;
//        }
//    }

    /**
     * 通过Shell Command 静默安装 apk
     * @param context context
     * @param nac Shell Command
     * @param filePath apk full path
     * @return true if ok
     */
//    public static final boolean installSilentUsingNac(final Context context, final ShellCommand nac, final String filePath) {
//
//        if(context == null || nac == null || TextUtils.isEmpty(filePath)) {
//            LeLog.e("invalid params !");
//            return false;
//        }
//
//        //新开一个线程安装，否则安装大的 apk 会卡死 UI 线程
//        Thread thread = new Thread(new BYSafeRunnable() {
//            @Override
//            public void runSafely() {
//                nac.setResponseCallBack(new ShellCommand.ShellCommandResponseEx() {
//                    private boolean mWorkingWell = false;
//
//                    @Override
//                    public void onShellCommandResponse(int cmdId, String reply) {
//                        LeLog.d("reply: " + reply);
//                        if(reply != null && reply.equalsIgnoreCase("success")) {
//                            mWorkingWell = true;
//                        }
//                    }
//
//                    @Override
//                    public void onShellCommandResponseStarted(int exeId) {
//                        LeLog.d("start exec shell cmd!");
//                    }
//
//                    @Override
//                    public void onShellCommandResponseFinished(int exeId) {
//                        LeLog.d("finish exec shell cmd! mWorkingWell: " + mWorkingWell );
//                    }
//
//                });
//
//                try {
//                    nac.addCommand("pm install -r " + filePath);
//                    if(true){
//                        nac.syncExec();
//                    } else {
//                        nac.exec()
//                                .waitResponse(500L);
//                    }
//                } catch (IOException e) {
//                	LeLog.e(e);
//                }
//            }
//        });
//
//        thread.start();
//
//        return true;
//    }
}
