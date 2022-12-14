package com.laba.user.ui.fragment.book_ride;

import com.laba.user.base.MvpView;
import com.laba.user.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView{
    void onSuccess(Object object);
    void onError(Throwable e);
    void onSuccessCoupon(PromoResponse promoResponse);
}
