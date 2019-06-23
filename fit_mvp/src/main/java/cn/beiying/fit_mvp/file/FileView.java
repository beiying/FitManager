package cn.beiying.fit_mvp.file;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import java.io.File;

import cn.beiying.fit_mvp.BaseModel;
import cn.beiying.fit_mvp.BaseView;
import cn.beiying.fit_mvp.FileManager;

/**
 * Created by beiying on 19/3/9.
 */

public class FileView extends FrameLayout implements BaseView<FilePresenter> {
    private FilePresenter mPresenter;
    public FileView(@NonNull Context context) {
        super(context);
        mPresenter = createPresenter();
    }

    private void downloadFile() {
        String url = "http://download.sdk.mob.com/apkbus.apk";
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            mPresenter.downloadFile(url, FileManager.getDownloadPath() + "app-debug.apk");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public FilePresenter createPresenter() {
        return new FilePresenter(this);
    }

    public void onSuccess(File file) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void onErrorCode(BaseModel model) {

    }
}
