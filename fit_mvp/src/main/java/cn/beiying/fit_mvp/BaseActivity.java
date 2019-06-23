package cn.beiying.fit_mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by beiying on 19/3/8.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView{
    public Context mContext;
    private ProgressDialog mDialog;
    public Toast mToast;
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mPresenter = createPresenter();
        initView();
        initData();
    }

    public abstract P createPresenter();

    public void initView() {}

    public void initData() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
    public void showtoast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    private void closeLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    private void showLoadingDialog() {

        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
        }
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }


    @Override
    public void hideLoading() {
        closeLoadingDialog();
    }


    @Override
    public void showError(String msg) {
        showtoast(msg);
    }

    @Override
    public void onErrorCode(BaseModel model) {
    }

//    @Override
//    public void showLoadingFileDialog() {
//        showFileDialog();
//    }
//
//    @Override
//    public void hideLoadingFileDialog() {
//        hideFileDialog();
//    }

//    @Override
//    public void onProgress(long totalSize, long downSize) {
//        if (mDialog != null) {
//            mDialog.setProgress((int) (downSize * 100 / totalSize));
//        }
//    }
}
