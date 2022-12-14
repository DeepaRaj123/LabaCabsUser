package com.laba.user.ui.activity.main;

import com.laba.user.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIPresenter<V extends MainIView> extends MvpPresenter<V> {
    void profile();
    void logout(String id);
    void checkStatus(int pos);
    void address();
    void settings();
    void providers(HashMap<String, Object> params);
    void updateRazorPayment(Integer mReqId,String mPayId);
}
