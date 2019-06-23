package cn.beiying.fit_mvp;

/**
 * Created by beiying on 19/3/8.
 */

public interface BaseView<P extends BasePresenter> {

    P createPresenter();

    void showLoading();

    void hideLoading();

    void showError(String msg);

    void onErrorCode(BaseModel model);
}
