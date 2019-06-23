package cn.beiying.fit_mvp;

import java.io.Serializable;

/**
 * Created by beiying on 19/3/8.
 */

public class BaseModel<T> implements Serializable {

    private int mErrcode;
    private String mErrMsg;
    private T mResult;

    public int getErrCode() {
        return mErrcode;
    }

    public void setErrCode(int errcode) {
        this.mErrcode = errcode;
    }

    public String getErrMsg() {
        return mErrMsg;
    }

    public void setErrMsg(String errMsg) {
        this.mErrMsg = errMsg;
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        this.mResult = result;
    }
}
