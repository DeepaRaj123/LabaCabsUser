package com.laba.user.ui.activity.help;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HelpPresenter<V extends HelpIView> extends BasePresenter<V> implements HelpIPresenter<V> {

    @Override
    public void help() {
        getCompositeDisposable().add(APIClient.getAPIClient().help()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(help -> getMvpView().onSuccess(help),
                        throwable -> getMvpView().onError(throwable)));
    }
}
