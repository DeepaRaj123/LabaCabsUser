package com.laba.user.ui.activity.register;

import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;
import com.laba.user.data.network.model.MyOTP;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegisterPresenter<V extends RegisterIView>
        extends BasePresenter<V>
        implements RegisterIPresenter<V> {

    @Override
    public void register(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .register(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void verifyEmail(String email) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onVerifyEmailError));
    }

    @Override
    public void sendOTP(HashMap<String, Object> obj) {

        Observable modelObservable = APIClient.getAPIClient().sendOtp(obj);
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((MyOTP) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}
