package com.laba.user.ui.activity.splash;

import com.laba.user.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SplashIPresenter<V extends SplashIView> extends MvpPresenter<V>{
    void profile();
}
