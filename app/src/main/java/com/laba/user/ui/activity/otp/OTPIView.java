package com.laba.user.ui.activity.otp;


import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.MyOTP;

public interface OTPIView extends MvpView {
    void onSuccess(MyOTP otp);
    void onError(Throwable e);
}
