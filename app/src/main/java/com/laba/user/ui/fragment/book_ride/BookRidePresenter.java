package com.laba.user.ui.fragment.book_ride;

import android.util.Log;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BookRidePresenter<V extends BookRideIView> extends BasePresenter<V> implements BookRideIPresenter<V> {

    @Override
    public void rideNow(HashMap<String, Object> obj) {
        Log.e("BOOK RIDE", "rideNow called!");
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .sendRequest(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void getCouponList() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .promocodesList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessCoupon, getMvpView()::onError));
    }
}
