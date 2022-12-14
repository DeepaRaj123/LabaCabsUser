package com.laba.user.ui.activity.card;

import com.laba.user.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
