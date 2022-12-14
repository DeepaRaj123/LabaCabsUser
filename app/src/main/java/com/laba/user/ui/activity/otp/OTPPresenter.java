package com.laba.user.ui.activity.otp;


import com.laba.user.base.BasePresenter;
import com.laba.user.data.network.APIClient;
import com.laba.user.data.network.model.MyOTP;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OTPPresenter<V extends OTPIView> extends BasePresenter<V> implements OTPIPresenter<V> {

    @Override
    public void sendOTP(HashMap<String, Object> obj) {

        Observable modelObservable = APIClient.getAPIClient().sendOtp(obj);
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> getMvpView().onSuccess((MyOTP) trendsResponse),
                        throwable -> getMvpView().onError((Throwable) throwable));
    }
}
