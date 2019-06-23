package com.beiying.net.download;

import java.io.File;

/**
 * Created by beiying on 19/4/7.
 */

public interface DownloadCallback {

    void onSuccess(File file);

    void onFailed(int errCode, String errMsg);

    void progress(int progress);
}
