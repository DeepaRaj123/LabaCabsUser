package com.laba.user.ui.fragment.service;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ServicePresenter<V extends ServiceIView> extends BasePresenter<V> implements ServiceIPresenter<V> {

    @Override
    public void services(HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient.getAPIClient().services(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(serviceResponse -> getMvpView().onSuccess(serviceResponse),
                        throwable -> getMvpView().onError(throwable)));
    }


    @Override
    public void estimateFare(HashMap<String, Object> obj) {
 /* getCompositeDisposable().add(APIClient.getAPIClient().estimateFare(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(estimateResponse -> getMvpView().onSuccess(estimateResponse),
                        throwable -> getMvpView().onError(throwable)));*/
    }

  /*  @Override
    public void estimateFareall(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient.getAPIClient().estimateFareall(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(estimateResponse -> getMvpView().onSuccess(estimateResponse),
                        throwable -> getMvpView().onError(throwable)));
    } */

    @Override
    public void rideNow(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient.getAPIClient().sendRequest(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(sendRequestResponse -> getMvpView().onSuccess(sendRequestResponse),
                        throwable -> getMvpView().onError(throwable)));
    }
}
