package com.beiying.fitmanager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by beiying on 2019/6/11.
 */

public class BYSuperHandler extends Handler {
    private long mStartTime = System.currentTimeMillis();

    public BYSuperHandler() {
        super(Looper.myLooper(), null);
    }

    public BYSuperHandler(Callback callback) {
        super(Looper.myLooper(), callback);
    }

    public BYSuperHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }
    public BYSuperHandler(Looper looper) {
        super(looper);
    }

    @Override
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        boolean send = super.sendMessageAtTime(msg, uptimeMillis);
        if (send) {
//            GetDetailHandlerHelper.getMsgDetail().put(msg, Log.getStackTraceString(new Throwable()));
        }
        return send;
    }

    @Override
    public void dispatchMessage(Message msg) {
        mStartTime = System.currentTimeMillis();
        super.dispatchMessage(msg);
//        if (BYGetDetailHandlerHelper.getMsgDetail().containsKey(msg) && Looper.myLooper() == Looper.getMainLooper()) {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("Msg_Cost", System.currentTimeMillis() - mStartTime);
//                jsonObject.put("MsgTrace", msg.getTarget() + "" + GetDetailHandlerHelper.getMsgDetail().getMsg());
//
//                LeLog.e("MsgDetail:" + jsonObject.toString());
//                GetDetailHandlerHelper.getMsgDetail().remove(msg);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
    }
}
