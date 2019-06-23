package cn.beiying.fit_mvp;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by beiying on 19/3/8.
 */

public class BasePresenter<V extends BaseView> {
    private CompositeDisposable mCompositeDisposable;

    public V mBaseView;

    protected  ApiServer mApiServer = ApiRetrofit.getInstance().getApiService();

    public BasePresenter(V baseView) {
        this.mBaseView = baseView;
    }

    public void detachView() {
        mBaseView = null;
        removeDisposable();
    }

    public V getBaseView() {
        return mBaseView;
    }

    public void addDisposable(Observable<?> observable, BaseObserver observer) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer));
    }

    public void removeDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }
}
