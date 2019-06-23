package com.beiying.fitmanager;

import android.content.Context;

import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by beiying on 2019/6/10.
 */

public class BYAppBlockCanaryContext extends BlockCanaryContext {
    @Override
    public String provideQualifier() {
        return "unknown";
    }

    @Override
    public String provideUid() {
        return "uid";
    }

    /**
     * like 2G,3G,4G,wifi
     * */
    @Override
    public String provideNetworkType() {
        return "unknown";
    }

    /**
     * 配置BlockCanary的
     * */
    @Override
    public int provideMonitorDuration() {
        return -1;
    }

    /**
     * 配置卡顿的阈值
     * */
    @Override
    public int provideBlockThreshold() {
        return 500;
    }

    @Override
    public int provideDumpInterval() {
        return provideBlockThreshold();
    }


    /**
     * Path to save log, like "/blockcanary/", will save to sdcard if can.
     *
     * @return path of log files
     */
    @Override
    public String providePath() {
        return "/blockcanary/";
    }

    /**
     * If need notification to notice block.
     *
     * @return true if need, else if not need.
     */
    @Override
    public boolean displayNotification() {
        return true;
    }

    /**
     * Implement in your project, bundle files into a zip file.
     *
     * @param src  files before compress
     * @param dest files compressed
     * @return true if compression is successful
     */
    @Override
    public boolean zip(File[] src, File dest) {
        return false;
    }

    /**
     * Implement in your project, bundled log files.
     *
     * @param zippedFile zipped file
     */
    @Override
    public void upload(File zippedFile) {
        throw new UnsupportedOperationException();
    }


    /**
     * Packages that developer concern, by default it uses process name,
     * put high priority one in pre-order.
     *
     * @return null if simply concern only package with process name.
     */
    @Override
    public List<String> concernPackages() {
        return null;
    }

    /**
     * Filter stack without any in concern package, used with @{code concernPackages}.
     *
     * @return true if filter, false it not.
     */
    @Override
    public boolean filterNonConcernStack() {
        return false;
    }

    /**
     * Provide white list, entry in white list will not be shown in ui list.
     *
     * @return return null if you don't need white-list filter.
     */
    @Override
    public List<String> provideWhiteList() {
        LinkedList<String> whiteList = new LinkedList<>();
        whiteList.add("org.chromium");
        return whiteList;
    }

    /**
     * Whether to delete files whose stack is in white list, used with white-list.
     *
     * @return true if delete, false it not.
     */
    @Override
    public boolean deleteFilesInWhiteList() {
        return true;
    }

    /**
     * Block interceptor, developer may provide their own actions.
     */
    @Override
    public void onBlock(Context context, BlockInfo blockInfo) {

    }
}
