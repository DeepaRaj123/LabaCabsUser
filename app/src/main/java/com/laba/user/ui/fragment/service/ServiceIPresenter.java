package com.laba.user.ui.fragment.service;

import com.laba.user.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceIPresenter<V extends ServiceIView> extends MvpPresenter<V> {
    void services(HashMap<String, Object> obj);

    void estimateFare(HashMap<String, Object> obj);

    void rideNow(HashMap<String, Object> obj);
}
