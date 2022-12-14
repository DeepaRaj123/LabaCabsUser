package com.laba.user.ui.activity.payment;

import com.laba.user.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PaymentIPresenter<V extends PaymentIView> extends MvpPresenter<V> {
    void deleteCard(String cardId);
    void card();
}
