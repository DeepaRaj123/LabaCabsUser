package com.laba.user.ui.activity.login;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class loginPresenter<V extends LoginIView> extends BasePresenter<V>
        implements LoginIPresenter<V> {
    @Override
    public void login(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().login(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));

        /*getCompositeDisposable().add(RetrofitFactory.getAPIClient(appContext()).login(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));*/

        /*getCompositeDisposable().add(new AppRetrofit().getAPIClient(appContext())
                .login(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));*/
    }

    @Override
    public void forgotPassword(String email) {

        getCompositeDisposable().add(APIClient.getAPIClient().forgotPassword(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
