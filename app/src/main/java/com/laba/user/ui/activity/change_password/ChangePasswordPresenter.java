package com.laba.user.ui.activity.change_password;


import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ChangePasswordPresenter<V extends ChangePasswordIView> extends BasePresenter<V> implements ChangePasswordIPresenter<V> {


    @Override
    public void changePassword(HashMap<String, Object> parms) {

        getCompositeDisposable().add(APIClient.getAPIClient().changePassword(parms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
