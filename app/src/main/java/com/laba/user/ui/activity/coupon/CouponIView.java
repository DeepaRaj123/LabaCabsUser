package com.laba.user.ui.activity.coupon;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);
    void onError(Throwable e);
}
