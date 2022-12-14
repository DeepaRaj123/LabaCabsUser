package com.laba.user.ui.activity.coupon;

import com.laba.user.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
