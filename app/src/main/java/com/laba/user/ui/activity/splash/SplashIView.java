package com.laba.user.ui.activity.splash;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.User;


public interface SplashIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}
