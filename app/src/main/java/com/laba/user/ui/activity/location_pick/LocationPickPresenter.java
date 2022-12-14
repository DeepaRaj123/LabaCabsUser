package com.laba.user.ui.activity.location_pick;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LocationPickPresenter<V extends LocationPickIView> extends BasePresenter<V> implements LocationPickIPresenter<V> {

    @Override
    public void address() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .address()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
