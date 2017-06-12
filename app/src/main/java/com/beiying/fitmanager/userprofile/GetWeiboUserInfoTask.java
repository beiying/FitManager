package com.beiying.fitmanager.userprofile;

import android.text.TextUtils;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LeThreadTask;
import com.beiying.fitmanager.core.net.LeHttpTask;
import com.beiying.fitmanager.core.net.LeNetTask;
import com.beiying.fitmanager.manager.LeFileManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beiying on 17/5/5.
 */

public class GetWeiboUserInfoTask extends LeHttpTask implements LeHttpTask.LeHttpTaskListener {
    private static final String CACHE = LeFileManager.getFileWeiboUserInfo();
    private static final String GET_WEIBO_USER_INFO_URL = "https://api.weibo.com/2/users/show.json";
    private String mAccessToken;
    private String mUid;

    public GetWeiboUserInfoTask(String access_token, String uid) {
        super(GET_WEIBO_USER_INFO_URL, CACHE, null);
        setListener(this);
        this.mAccessToken = access_token;
        this.mUid = uid;
    }

    public void request() {
        String params = "access_token=" + mAccessToken + "&uid=" + mUid;
        forceUpdate(params, false, null);
    }

    public void loadCache() {
        loadCache(LeThreadTask.PRIORITY_DEFAULT);
    }

    @Override
    protected boolean onParse(LeNetTask task, String data, boolean cacheParsing, boolean fromAsset) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject userInfo = new JSONObject(data);
                LeLog.e("liuyu", userInfo.toString());
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public void onCacheLoadSuccess() {
        request();
    }

    @Override
    public void onCacheLoadFail() {
        request();
    }

    @Override
    public void onReqeustSuccess(LeNetTask task) {

    }

    @Override
    public void onRequestFail(LeNetTask task) {

    }
}
