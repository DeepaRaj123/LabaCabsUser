package com.laba.user.ui.activity.register;

import com.laba.user.base.MvpPresenter;

import java.util.HashMap;

public interface RegisterIPresenter<V extends RegisterIView> extends MvpPresenter<V>{
    void register(HashMap<String, Object> obj);
    void verifyEmail(String email);
    void sendOTP(HashMap<String, Object> map);
}
