package com.laba.user.ui.activity.wallet;

import com.laba.user.base.MvpPresenter;
import java.util.HashMap;

public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V>{
    void addMoney(HashMap<String, Object> obj);
    void addRazorPayMoney(String payId);
}
