package cn.beiying.fit_mvp;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * Created by beiying on 19/3/8.
 */

public abstract class BaseObserver<T> extends DisposableObserver<T> {
    protected BaseView mView;

    /**
     * 解析数据失败
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络问题
     */
    public static final int BAD_NETWORK = 1002;
    /**
     * 连接错误
     */
    public static final int CONNECT_ERROR = 1003;
    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 1004;

    public BaseObserver(BaseView view) {
        this.mView = view;
    }

    @Override
    protected void onStart() {
        if (mView != null) {
            mView.showLoading();
        }
    }

    @Override
    public void onNext(T t) {
        try {
            BaseModel model = (BaseModel) t;
            if (model.getErrCode() == 0) {
                onSuccess(t);
            } else {
                if (mView != null) {
                    mView.onErrorCode(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.toString());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mView != null) {
            mView.hideLoading();
        }
        if (e instanceof HttpException) {
            onException(BAD_NETWORK);
        } else if (e instanceof ConnectException || e instanceof UnknownHostException) {
            onException(CONNECT_ERROR);
        } else if (e instanceof InterruptedException) {
            onException(CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {

        }
    }

    private void onException(int unknownError) {
        switch (unknownError) {
            case CONNECT_ERROR:
                onError("连接错误");
                break;

            case CONNECT_TIMEOUT:
                onError("连接超时");
                break;

            case BAD_NETWORK:
                onError("网络问题");
                break;

            case PARSE_ERROR:
                onError("解析数据失败");
                break;

            default:
                break;
        }
    }

    @Override
    public void onComplete() {
        if (mView != null) {
            mView.hideLoading();
        }

    }

    public abstract void onSuccess(T t);

    public abstract void onError(String msg);
}
