package com.laba.user.ui.fragment.book_ride;

import com.laba.user.base.MvpPresenter;

import java.util.HashMap;


public interface BookRideIPresenter<V extends BookRideIView> extends MvpPresenter<V> {
    void rideNow(HashMap<String, Object> obj);
    void getCouponList();
}
