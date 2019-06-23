package cn.beiying.fit_mvp.file;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.beiying.fit_mvp.ApiServer;
import cn.beiying.fit_mvp.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by beiying on 19/3/9.
 */

public class FilePresenter extends BasePresenter<FileView> {
    public FilePresenter(FileView baseView) {
        super(baseView);
    }

    public void downloadFile(String url, final String path) {

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://wawa-api.vchangyi.com/").build();

        mApiServer = retrofit.create(ApiServer.class);
        mApiServer.downloadFile(url)
                .map(new Function<ResponseBody, String>() {

                    @Override
                    public String apply(ResponseBody body) throws Exception {
                        File file = saveFile(path, body);
                        return file.getPath();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new FileObserver(mBaseView) {

                    @Override
                    public void onError(String msg) {
                        mBaseView.showError(msg);
                    }

                    @Override
                    public void onSuccess(File file) {

                    }

                });

        addDisposable(mApiServer.downloadFile(url), new FileObserver(mBaseView) {
            @Override
            public void onError(String msg) {
                mBaseView.showError(msg);
            }

            @Override
            public void onSuccess(File file) {
                if (file != null || file.exists()) {
                    mBaseView.onSuccess(file);
                } else {
                    mBaseView.showError("file is null");
                }
            }

        });
    }

    public File saveFile(String path, ResponseBody body) {
        InputStream in = null;
        OutputStream out = null;
        File file = null;
        try {
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            file = new File(path);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;
            byte[] fileReader = new byte[4096];

            in = body.byteStream();
            out = new FileOutputStream(file);

            while(true) {
                int size = in.read(fileReader);
                if (size == -1) {
                    break;
                }
                out.write(fileReader, 0, size);
                fileSizeDownloaded += size;
            }

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
}
