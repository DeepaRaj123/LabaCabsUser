package com.laba.user.ui.activity.register;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.MyOTP;
import com.laba.user.data.network.model.RegisterResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface RegisterIView extends MvpView{
    void onSuccess(RegisterResponse object);
    void onSuccess(Object object);
    void onError(Throwable e);
    void onVerifyEmailError(Throwable e);
    void onSuccess(MyOTP otp);
}
