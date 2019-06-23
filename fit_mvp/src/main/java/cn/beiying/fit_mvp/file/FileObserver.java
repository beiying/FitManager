package cn.beiying.fit_mvp.file;

import java.io.File;

import cn.beiying.fit_mvp.BaseObserver;
import cn.beiying.fit_mvp.BaseView;

/**
 * Created by beiying on 19/3/9.
 */

public abstract class FileObserver extends BaseObserver<String> {
    public FileObserver(BaseView view) {
        super(view);
    }

    @Override
    public void onSuccess(String path) {

    }

    @Override
    public abstract void onError(String msg);

    @Override
    public void onNext(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
            onSuccess(file);
        } else {
            onError("file is null or a file does not exist");
        }
    }


    public abstract void onSuccess(File file);
}
