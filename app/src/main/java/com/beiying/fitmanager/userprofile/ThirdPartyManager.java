package com.beiying.fitmanager.userprofile;

import android.widget.Toast;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.SharedPrefManager;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beiying on 17/5/4.
 */

public class ThirdPartyManager extends BYBasicContainer{
    public static final String SOURCE_WEIBO = "weibo";
    public static final String SOURCE_WECHAT = "wechat";
    public static final String SOURCE_QQ = "qq";
    private static final String WX_APP_ID = "wx56a500c3960304a2";

    //    private static final String QQ_APP_ID = "1105672598";//222222
    private static final String QQ_APP_ID = "222222";

    //    private static final String WB_APP_ID = "2045436852";
    private static final String WB_APP_ID = "1712934041";
    private static final String WB_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    private static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    private static final String WX_ACCESS_TOKEN = "wx_access_token";
    private static final String WX_REFRESH_TOKEN = "wx_refresh_token";
    private static final String WX_EXPIRES_IN = "wx_expires_in";
    private static final String WX_OPEN_ID = "wx_open_id";
    private static final String QQ_ACCESS_TOKEN = "qq_access_token";
    private static final String QQ_EXPIRES_IN = "qq_expires_in";
    private static final String QQ_OPEN_ID = "qq_open_id";
    private static final String WB_ACCESS_TOKEN = "wb_access_token";
    private static final String WB_REFRESH_TOKEN = "wb_refresh_token";
    private static final String WB_EXPIRES_IN = "wb_expires_in";
    private static final String WB_UID = "wb_uid";

    private IWXAPI mWxapi;
    private Tencent mTencent;
    private IUiListener mQQUIListener;
    private UserInfo mInfo = null;
    private SsoHandler mSsoHandler;/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private Oauth2AccessToken mAccessToken;/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */

    private static ThirdPartyManager sInstance;
    private static String mSource;


    public static ThirdPartyManager getInstance() {
        if (sInstance == null) {
            synchronized (ThirdPartyManager.class) {
                sInstance = new ThirdPartyManager();
            }
        }
        return sInstance;
    }

    public IWXAPI getWxApi() {
        if (mWxapi == null) {
            regToWx();
        }
        return mWxapi;
    }

    public Tencent getTencent() {
        if (mTencent == null) {
            createTencent();
        }
        return mTencent;
    }

    public SsoHandler getWBSsoHandler() {
        if (mSsoHandler == null) {
            mSsoHandler = new SsoHandler(sActivity);
        }
        return mSsoHandler;
    }

    public String getSource() {
        return mSource;
    }


    public void regToWx() {
        mWxapi = WXAPIFactory.createWXAPI(sContext, WX_APP_ID, true);
        mWxapi.registerApp(WX_APP_ID);
    }

    public void createTencent() {
        mTencent = Tencent.createInstance(QQ_APP_ID, sActivity);
        mQQUIListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                //{"access_token":"C11D7CD5F6A5676E1AE59F1313ECC197","expires_in":"7776000",
                // "openid":"D3BD80FD12BF5B61DC215960D24CC2F2","pay_token":"DD85BD7B3B9D38878D7FEECED2B92CF9",
                // "ret":"0","pf":"desktop_m_qq-10000144-android-2002-",
                // "pfkey":"2607c5822a212f6b45b32e7c6e36442a","auth_time":"1493364907858","page_type":"1"}
                try {
                    JSONObject object = (JSONObject)o;
                    LeLog.e("liu", object.toString());
                    mSource = SOURCE_QQ;

                    SharedPrefManager.createMultiProSpHelper(sPackageName).putString(sContext, QQ_ACCESS_TOKEN, object.getString("access_token"));
                    SharedPrefManager.createMultiProSpHelper(sPackageName).putString(sContext, QQ_EXPIRES_IN, object.getString("expires_in"));
                    SharedPrefManager.createMultiProSpHelper(sPackageName).putString(sContext, QQ_OPEN_ID, object.getString("openid"));

                    mTencent.setAccessToken(object.getString("access_token"), object.getString("expires_in"));
                    mTencent.setOpenId(object.getString("openid"));

                    getQQUserInfo();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                LeLog.e("liu", uiError.errorMessage);
            }

            @Override
            public void onCancel() {

            }
        };
    }

    public IUiListener getQQUIListener() {
        return mQQUIListener;
    }

    public void initWB() {
        WbSdk.install(sActivity,new AuthInfo(sActivity, WB_APP_ID, WB_REDIRECT_URL, "all"));
    }

    public void loginByWx() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";
        getWxApi().sendReq(req);
    }

    public void loginByQQ() {
        if (!getTencent().isSessionValid()) {
            getTencent().login(sActivity, "all", mQQUIListener);
        }
    }

    public void logoutByQQ() {
        getTencent().logout(sActivity);
    }

    public void loginByWB() {
        getWBSsoHandler().authorize(new SelfWbAuthListener());
    }

    public void getWeiboUserInfo() {

    }

    public void getQQUserInfo(IUiListener listener) {
        if (isQQReady()) {
            mInfo = new UserInfo(sContext, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        }
    }

    private void getQQUserInfo() {
        getQQUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject userInfo = (JSONObject)o;
                LeLog.e("liuyu", userInfo.toString());
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }


    public boolean isQQReady() {
        if (mTencent == null) {
            return false;
        }
        boolean ready = mTencent.isSessionValid()
                && mTencent.getQQToken().getOpenId() != null;
        if (!ready) {
            Toast.makeText(sContext, "login and get openId first, please!",
                    Toast.LENGTH_SHORT).show();
        }
        return ready;
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener{
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            sActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
                    if (mAccessToken.isSessionValid()) {
                        LeLog.e("liuyu", "access_token=" + mAccessToken.getToken()+";uid=" + mAccessToken.getUid());
                        mSource = SOURCE_WEIBO;
                        // 保存 Token 到 SharedPreferences
                        SharedPrefManager.createMultiProSpHelper(sPackageName).putString(sContext, WB_ACCESS_TOKEN, mAccessToken.getToken());
                        SharedPrefManager.createMultiProSpHelper(sPackageName).putString(sContext, WB_REFRESH_TOKEN, mAccessToken.getRefreshToken());
                        SharedPrefManager.createMultiProSpHelper(sPackageName).putLong(sContext, WB_EXPIRES_IN, mAccessToken.getExpiresTime());
                        SharedPrefManager.createMultiProSpHelper(sPackageName).putString(sContext, WB_UID, mAccessToken.getUid());
                        Toast.makeText(sActivity,
                                R.string.weibo_toast_auth_success, Toast.LENGTH_SHORT).show();
                        new GetWeiboUserInfoTask(mAccessToken.getToken(), mAccessToken.getUid()).loadCache();
                    }
                }
            });
        }

        @Override
        public void cancel() {
            LeLog.e("yang", "quxiao");
            Toast.makeText(sActivity,
                    R.string.weibo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(sActivity, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
